##
## Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
##
## This library is free software; you can redistribute it and/or
## modify it under the terms of the GNU Lesser General Public
## License version 2.1 as published by the Free Software Foundation.
##
## This library is distributed in the hope that it will be useful,
## but WITHOUT ANY WARRANTY; without even the implied warranty of
## MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
## Lesser General Public License for more details.
##
## You should have received a copy of the GNU Lesser General Public
## License along with this library; if not, write to the Free Software
## Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
##
## $Id$
##

TOPDIR =	../../../../..

SUBDIRS =	client

PACKAGE = 	org/kopi/vkopi/lib/ui/vaadin/addons

NONSTANDARDCOMPILER = true

## ----------------------------------------------------------------------
## TOOLS

## ----------------------------------------------------------------------
## FILES

JAVAFILES +=	AbstractBlockLayout AbstractNotification ActionEvent	\
	        ActionListener Actor Block BlockLayout BlockListener	\
		ChartBlockLayout CloseListener ConfirmNotification	\
		ContextMenu DateChooser DateChooserListener		\
		ErrorNotification Field FieldListener Form		\
		FormListener ImageField ImageFieldListener		\
		InformationNotification Label LabelEvent LabelListener	\
		ListDialog ListDialogListener MainWindow		\
		MainWindowListener ModuleItem ModuleList		\
		ModuleListEvent ModuleListListener MultiBlockLayout	\
		NoInputStreamEvent NoOutputStreamEvent			\
		NotificationListener ObjectField ObjectFieldListener	\
		PopupWindow ProgressDialog SimpleBlockLayout		\
		SortableLabel SortableLabelListener TextField		\
		TextFieldListener TextValueChangeListener		\
		TristateChangeListener TristateCheckBox Upload 		\
		UploadChangeEvent UploadChangeListener 			\
		UploadFailedEvent UploadFailedListener 			\
		UploadFinishedEvent UploadFinishedListener		\
		UploadProgressListener UploadReceiver 			\
		UploadStartedEvent UploadStartedListener		\
		UploadSucceededEvent UploadSucceededListener WaitDialog	\
		WaitWindow WarningNotification WelcomeView		\
		WelcomeViewEvent WelcomeViewListener Window		\
		GridEditorDateField GridEditorEnumField GridEditorField	\
		GridEditorFixnumField GridEditorHandlingExtension	\
		GridEditorIntegerField GridEditorLabel			\
		GridEditorMonthField GridEditorTextAreaField		\
		GridEditorTextField GridEditorTimeField			\
		GridEditorTimestampField GridEditorWeekField		\
		SingleComponentBlockLayout ActorField 			\
		GridEditorActorField GridListDialog			\
		GridEditorImageField BooleanField BooleanRenderer	\
		GridEditorBooleanField UploadProgress RichTextField	\
		ActorRenderer

WIDGETSETS +=	KopiAddonsWidgetset.gwt

## ----------------------------------------------------------------------
## GOALS

default:	build

classes.stamp:	$(JAVAFILES:%=%.java)
		$(JAVAC) -sourcepath $(TOPDIR)/../.. -d $(CLASSROOT) $?
		cp $(WIDGETSETS:%=%.xml) $(CLASSROOT)/$(PACKAGE)
		touch classes.stamp

## ----------------------------------------------------------------------
## GLOBAL DEFINITIONS

include $(TOPDIR)/Make.Defs
