/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id: AspellProcess.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

package at.dms.vkopi.lib.spellchecker;

import java.io.*;
import java.util.*;

/**
 * Models a spelling checker
 *<p>
 *
 */
public class AspellProcess
{

  public AspellProcess(String aSpellCommandLine) throws SpellException
  {
    try {
      String            aspellVersionMsg;
      String            fehler = "";
      Runtime           runtime = Runtime.getRuntime();
      BufferedReader    aspellError;

      aspellProcess = runtime.exec(aSpellCommandLine);
      aspellInput =  new BufferedReader(new InputStreamReader(aspellProcess.getInputStream()));
      aspellError =  new BufferedReader(new InputStreamReader(aspellProcess.getErrorStream()));
      aspellOutput = new BufferedWriter(new OutputStreamWriter(aspellProcess.getOutputStream()));
      aspellVersionMsg = aspellInput.readLine();
      if (aspellError.ready()) {
        fehler = aspellError.readLine();
      }

      // verify that he found the dict
      verifyStartup(aspellVersionMsg, fehler);
    } catch(IOException e) {
      throw new SpellException("Cannot create aspell process.", e );
    }
  }

  
  private void verifyStartup(String startupMsg, String fehler) throws SpellException {
    if (startupMsg == null || ! startupMsg.startsWith("@(#)")) {
      throw new SpellException("Wrong configuration of Aspell:"
                               + ((startupMsg !=null) ? startupMsg : "" ) 
                               + " " + fehler);
    }
  }

  public List checkText(String text) throws SpellException {
    try {
      String            response;
      List              results = new ArrayList();
      final String      spellCheckLinePrefix = "^";

      aspellOutput.write( spellCheckLinePrefix + text );
      aspellOutput.newLine();
      aspellOutput.flush();
      response = aspellInput.readLine();

      while(response != null && !response.equals( "" )) {
        Suggestions          result = new Suggestions(response);

        results.add( result );
        response = aspellInput.readLine();
      }

      return results;
    } catch( IOException e ) {
      throw new SpellException("Failure during spell checking:", e);
    }
  }

  public void cancel() {
    aspellProcess.destroy();
  }

  BufferedReader aspellInput;
  BufferedWriter aspellOutput;
  Process        aspellProcess;
}
