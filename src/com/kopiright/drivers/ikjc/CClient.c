/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id$
 */

#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <arpa/inet.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>

#ifdef __linux__
#define GETOPT_PREFIX	"+"	// no more options after first argument without leading -
#else
#define GETOPT_PREFIX
#endif
#ifndef INADDR_NONE
#define INADDR_NONE 0xffffffff
#endif

#define TERM_STRING	"!@#$%^&*()"

#define MAXBUFFER	15000
#define MAXDIRSIZE	1024

/*
 * Print usage information
 */
static void printUsageInformation(const char *progname)
{
  fprintf(stderr, "usage: %s [-h servername] [-p serverport] <ikjc-args>\n", progname);
  exit(2);
}

/*
 * Create a connection to the compilation server
 * Returns the file descriptor of the connection
 */
static int connectToServer(const char *name, int port) {
  struct sockaddr_in	address;
  struct hostent *	hp;
  unsigned long		servaddr;
  int			fd;

  /*
   * Create the server address (protocol, IP address, port)
   */
  bzero((char *) &address, sizeof address);

  /*
   * Convert the server address in a form suitable for the system calls
   * argument. Because the server name can be an IP address
   * (like 131.112.17.2) or hostname (like mike.cs.titech.ac.jp), we
   * must handle both cases.
   */
  /* First try to convert the host address as an IP address. If it fails,
   * then we assume hostname.
   */
  if ((servaddr = inet_addr(name)) != INADDR_NONE) {
    /* IP address */
    bcopy((char *)&servaddr, (char *)&address.sin_addr, sizeof servaddr);
  } else {
    /* hostname */
    if ((hp = gethostbyname(name)) == NULL){
      fprintf(stderr, "Server name error: unknown host\n");
      exit(-1);
    }
    bcopy(hp->h_addr, (char *)&address.sin_addr, hp->h_length);
  }
  address.sin_family = AF_INET;
  address.sin_port = htons(port);

  /*
   * Create a TCP socket.
   */
  /*
   * You can use AF_INET instead of AF_INET. They are equivalent.
   */
  fd = socket(AF_INET, SOCK_STREAM, 0);
  if (fd < 0){
    perror("Open socket error");
    exit(-1);
  }

  /* Request connection */
  if (connect(fd, (struct sockaddr *)&address, sizeof address) < 0) {
    perror("Connection error");
    exit(-1);
  }

  return fd;
}

/*
 * Send a new-line terminated string to the server
 */
static void sendLine(int fd, const char *str) {
  if (str != NULL) {
    int		len = strlen(str);

    if (write(fd, str, len) != len) {
      perror("could not write to server");
      exit(1);
    }
  }

  /* add new-line */
  str = "\n";
  if (write(fd, str, 1) != 1) {
    perror("could not write to server");
    exit(1);
  }
}

/*
 * Read a new-line terminated string from the server
 * Returns 0 on EOF
 */
static int readLine(int fd, char *buffer) {
  char		c;

  if (read(fd, &c, 1) == 0) {
    return 0;
  } else {
    if (c != '\n') {
      *buffer++ = c;
      while ((read(fd, &c, 1)) == 1 && c != '\n') {
	*buffer++ = c;
      }
    }
    *buffer = '\0';

    return 1;
  }
}

/*
 * The function below uses the received result from the sender. Currently,
 * it simply prints the string.
 */
static void echoAnswer(char buffer[], int length) {
  if (length == 0) {
    return;
  }
  if (length != -1) {
    buffer[length] = '\0';
  }
  fputs(buffer, stderr);
  fputs("\n", stderr);
}

int main(int argc, char *argv[])
{
  char *	server_name = "localhost";
  int		server_port = 4444;
  int		opt;
  int		socket_conn;
  char		dirbuf[MAXDIRSIZE];

  /*
   * read arguments
   */
  while ((opt = getopt(argc, argv, GETOPT_PREFIX "h:p:")) != EOF) {
    switch (opt) {
    case 'h':
      server_name = optarg;
      break;

    case 'p':
      server_port = atoi(optarg);
      break;

    case '?':
      printUsageInformation(argv[0]);
    }
  }

  if (optind == argc) {
    /* nothing to do: error */
    printUsageInformation(argv[0]);
  }

  /* create connection */
  socket_conn = connectToServer(server_name, server_port);

  /* send current working directory */
  sendLine(socket_conn, getcwd(dirbuf, MAXDIRSIZE));

  /* send remaining command line arguments */
  for (/* already initialised */; optind < argc; optind++) {
    sendLine(socket_conn, argv[optind]);
  }
  sendLine(socket_conn, NULL);

  /* handle answers from server */
  for (;;) {
    char	buffer[MAXBUFFER];

    if (! readLine(socket_conn, buffer)) {
      perror("Stream reading error");
      exit(1);
    }

    if (! strcmp(buffer + strlen(buffer) - strlen(TERM_STRING), TERM_STRING)) {
      echoAnswer(buffer, strlen(buffer) - strlen(TERM_STRING));

      if (! readLine(socket_conn, buffer)) {
	perror("Stream reading error");
	exit(1);
      }
      exit(strcmp(buffer, "true") ? 1 : 0);
    } else {
      echoAnswer(buffer, -1);
    }
  }

  /* end of requests */
  close(socket_conn);
}

