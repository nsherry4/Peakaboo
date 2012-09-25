SetCompressor /FINAL lzma

!include "MUI.nsh"

!define APPLICATION_NAME "Peakaboo"
!define VERSION_NUMBER "4"
!define SOURCE_RELPATH "..\.."

Name "${APPLICATION_NAME}"
OutFile "${APPLICATION_NAME} ${VERSION_NUMBER} Setup.exe"
InstallDir "$PROGRAMFILES\${APPLICATION_NAME}\"

;--------------------------------
; Interface Settings

!define MUI_ABORTWARNING
!define MUI_WELCOMEPAGE_TEXT "The wizard will guide you through the installation of ${APPLICATION_NAME} ${VERSION_NUMBER}.\r\n\
                              \r\n\
                              Click Next co continue."
!define MUI_WELCOMEFINISHPAGE_BITMAP "InstallerSide.bmp"
!define MUI_HEADERIMAGE
!define MUI_HEADERIMAGE_BITMAP "InstallerHeader.bmp"

;--------------------------------
; Uninstaller Interface Settings

!define MUI_UNABORTWARNING
!define MUI_UNABORTWARNING_TEXT "Are you sure you want to quit ${APPLICATION_NAME} uninstallation?"
!define MUI_UNWELCOMEFINISHPAGE_BITMAP "InstallerSide.bmp"
!define MUI_HEADERIMAGE_UNBITMAP "InstallerHeader.bmp"

;--------------------------------
; Install Pages

!insertmacro MUI_PAGE_WELCOME
Page custom LocatePreviousInstallations
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH

;--------------------------------
; Uninstaller Pages

!insertmacro MUI_UNPAGE_WELCOME
!insertmacro MUI_UNPAGE_INSTFILES
!insertmacro MUI_UNPAGE_FINISH

;--------------------------------
; Languages

!insertmacro MUI_LANGUAGE "English"

;--------------------------------
; Helper Functions
Function LocatePreviousInstallations
         ReadRegStr $0 HKLM 'Software\Microsoft\Windows\CurrentVersion\Uninstall\${APPLICATION_NAME}' 'UninstallString'
         StrCmp $0 '' done foundInstall
         
         foundInstall:
         StrCpy $R0 $0
         ReadRegStr $R1 HKLM 'Software\Microsoft\Windows\CurrentVersion\Uninstall\${APPLICATION_NAME}' 'InstallLocation'
                      
         IfFileExists '$R0' continue done
         continue:
         MessageBox MB_YESNO 'An installation of ${APPLICATION_NAME} has already been found. This must be uninstalled before re-installing ${APPLICATION_NAME}. Would you like to do this now?' /SD IDNO IDYES doUninstall IDNO exit
         
         doUninstall:
         GetTempFileName $0
         CopyFiles $R0 $0

         ExecWait '"$0" _?=$R1'
         
         Delete $R0
         
         ReadRegStr $0 HKLM 'Software\Microsoft\Windows\CurrentVersion\Uninstall\${APPLICATION_NAME}' "UninstallString"
         StrCmp $0 "" done exit
         
         exit:
         MessageBox MB_OK "${APPLICATION_NAME} was not successfully installed."  /SD IDOK
         Quit
         
         done:
                      
FunctionEnd

!define LVM_GETITEMCOUNT 0x1004
!define LVM_GETITEMTEXT 0x102D

Function DumpLog
  Exch $5
  Push $0
  Push $1
  Push $2
  Push $3
  Push $4
  Push $6

  FindWindow $0 "#32770" "" $HWNDPARENT
  GetDlgItem $0 $0 1016
  StrCmp $0 0 exit
  FileOpen $5 $5 "w"
  StrCmp $5 "" exit
    SendMessage $0 ${LVM_GETITEMCOUNT} 0 0 $6
    System::Alloc ${NSIS_MAX_STRLEN}
    Pop $3
    StrCpy $2 0
    System::Call "*(i, i, i, i, i, i, i, i, i) i \
      (0, 0, 0, 0, 0, r3, ${NSIS_MAX_STRLEN}) .r1"
    loop: StrCmp $2 $6 done
      System::Call "User32::SendMessageA(i, i, i, i) i \
        ($0, ${LVM_GETITEMTEXT}, $2, r1)"
      System::Call "*$3(&t${NSIS_MAX_STRLEN} .r4)"
      FileWrite $5 "$4$\r$\n"
      IntOp $2 $2 + 1
      Goto loop
    done:
      FileClose $5
      System::Free $1
      System::Free $3
  exit:
    Pop $6
    Pop $4
    Pop $3
    Pop $2
    Pop $1
    Pop $0
    Exch $5
FunctionEnd

;--------------------------------
; ScanPlotter files

Section "ScanPlotter Program Files" SecScanPlotter

        SetDetailsPrint textonly
        DetailPrint "Installing ${APPLICATION_NAME} program files..."
        SetDetailsPrint listonly
        
        SetOutPath "$INSTDIR"
        File /r /x .svn /x _win32 ${SOURCE_RELPATH}\*
        File ${SOURCE_RELPATH}\Logo.ico
        
        SetDetailsPrint textonly
        DetailPrint "Installing dependencies..."
        SetDetailsPrint listonly
        
		        
        WriteUninstaller "$INSTDIR\uninstall.exe"
        
        StrCpy $0 "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APPLICATION_NAME}"
        WriteRegStr HKLM $0 "DisplayName" "${APPLICATION_NAME}"
        WriteRegStr HKLM $0 "UninstallString" "$INSTDIR\uninstall.exe"
        WriteRegStr HKLM $0 "InstallLocation" "$INSTDIR"
 

        SetOutPath "$SMPROGRAMS\${APPLICATION_NAME}"
        ;setting outpath back to install dir makes the shortcuts 'start in' that folder
        SetOutPath "$INSTDIR"
        CreateShortCut "$SMPROGRAMS\${APPLICATION_NAME}\${APPLICATION_NAME}.lnk" 'javaw.exe' '-Xmx1536m peakaboo.Peakaboo' '$INSTDIR\logo.ico' ''
        CreateShortCut "$SMPROGRAMS\${APPLICATION_NAME}\Remove ${APPLICATION_NAME}.lnk" '"$INSTDIR\uninstall.exe"'
        
        StrCpy $0 "$INSTDIR\install.log"
        Push $0
        Call DumpLog
        
SectionEnd

;--------------------------------
; ScanPlotter Uninstaller

Section "un.UninstallScanPlotter" SecUnScanPlotter

        SetDetailsPrint textonly
        DetailPrint "Uninstalling..."
        SetDetailsPrint listonly
        
        StrCpy $0 "$INSTDIR\install.log"
        Push $0
        Call un.DeleteFromLog
        
        Delete "$INSTDIR\gtk-config.bat"
        Delete "$INSTDIR\error.log"
        Delete "$INSTDIR\debug.log"
        Delete "$INSTDIR\install.log"
        Delete "$INSTDIR\uninstall.exe"
        
        RMDir "$INSTDIR"

        RMDir /r "$SMPROGRAMS\${APPLICATION_NAME}"

        DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APPLICATION_NAME}"
SectionEnd

Function un.GetParent

  Exch $R0
  Push $R1
  Push $R2
  Push $R3

  StrCpy $R1 0
  StrLen $R2 $R0

  loop:
    IntOp $R1 $R1 + 1
    IntCmp $R1 $R2 get 0 get
    StrCpy $R3 $R0 1 -$R1
    StrCmp $R3 "\" get
  Goto loop

  get:
    StrCpy $R0 $R0 -$R1

    Pop $R3
    Pop $R2
    Pop $R1
    Exch $R0

FunctionEnd

function un.DeleteFromLog

         ; R0 will be used as the log
         ; R1 will be used as the current directory
         ; R2 will be used as the current file
         ; R3 will be the file handle
         ; R4 will be the line
         ; R5 will be temp space
         exch $R0 ; gets the log
         push $R1 ; Backup $R1
         push $R2 ; Backup $R2
         push $R3 ; Backup $R3.  Stack is left in order $R0, $R1, $R2, etc
         push $R4
         push $R5
         push $R6
         FileOpen $R3 "$R0" "r"
NewLine:
         FileRead $R3 $R4
         ;MessageBox MB_OK "New Line: $R4"
         StrCpy $R5 $R4 15
         ;MessageBox MB_OK "Foldertest: $R5"
         StrCmp $R5 "Output folder: " ChangeDir ; Line specifies a directory
         StrCpy $R5 $R4 9
         ;MessageBox MB_OK "Filetest: $R5"
         StrCmp $R5 "Extract: " ChangeFile ; Line specifies a file
         StrCmp $R4 "" DoneAndDone ; Out of lines
         Goto NewLine ; If the line is none of the above, grab a new one.

ChangeDir:
         RMDir $R1 ; Try to remove the last directory
         StrCpy $R5 $R1
DeleteParents:
         ; Will attempt to delete parent directorys until it runs out
         ; not aggressive, just removes empty directories recurisvely
         ; before moving onto a new directory to delete files from
         Push $R5
         Call un.GetParent
         Pop $R5
         StrCmp $R5 "" +2
         RMDir $R5
         StrCmp $R5 "" 0 DeleteParents

         StrCpy $R1 $R4 1024 15 ; Get the new directory
         StrLen $R5 $R1
         IntOp $R5 $R5 - "2"
         StrCpy $R1 $R1 $R5 ; trims off the newline at the end
         Goto NewLine

ChangeFile:
         StrCpy $R2 $R4 1024 9 ; Get the new file
         StrLen $R5 $R2
         IntOp $R5 $R5 - "10"
         StrCpy $R6 $R2 8 $R5
         StrCmp $R6 "... 100%" RemovePercent DontRemovePercent
         DontRemovePercent:
         StrLen $R5 $R2
         IntOp $R5 $R5 - "2"
         RemovePercent:
         StrCpy $R2 $R2 $R5 ; trims off the ...100% and the newline
         Delete "$R1\$R2" ; Try to delete the new file
         Goto NewLine

DoneAndDone:
         FileClose $R3
         Pop $R6
         Pop $R5 ; Restore the registers
         Pop $R4
         Pop $R3
         Pop $R2
         Pop $R1
         Exch $R0
functionend
