; Script generated by the HM NIS Edit Script Wizard.

!cd ..

; HM NIS Edit Wizard helper defines
!define PRODUCT_NAME "Crossfire Client"
!define PRODUCT_VERSION "1.12.0"
!define PRODUCT_PUBLISHER "Crossfire Project"
!define PRODUCT_WEB_SITE "http://crossfire.real-time.com/"
!define PRODUCT_DIR_REGKEY "Software\Microsoft\Windows\CurrentVersion\App Paths\Crossfire Client Gtkv2"
!define PRODUCT_UNINST_KEY "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT_NAME}"
!define PRODUCT_UNINST_ROOT_KEY "HKLM"
!define PRODUCT_STARTMENU_REGVAL "NSIS:StartMenuDir"

SetCompressor lzma

; MUI 1.67 compatible ------
!include "MUI.nsh"

; MUI Settings
!define MUI_ABORTWARNING
!define MUI_ICON "pixmaps\client.ico"
!define MUI_UNICON "${NSISDIR}\Contrib\Graphics\Icons\modern-uninstall.ico"

; Welcome page
!insertmacro MUI_PAGE_WELCOME
; License page
!insertmacro MUI_PAGE_LICENSE "nsis-tmp\COPYING.txt"
; Components page
!insertmacro MUI_PAGE_COMPONENTS
; Directory page
!insertmacro MUI_PAGE_DIRECTORY
; Start menu page
var ICONS_GROUP
!define MUI_STARTMENUPAGE_NODISABLE
!define MUI_STARTMENUPAGE_DEFAULTFOLDER "Crossfire"
!define MUI_STARTMENUPAGE_REGISTRY_ROOT "${PRODUCT_UNINST_ROOT_KEY}"
!define MUI_STARTMENUPAGE_REGISTRY_KEY "${PRODUCT_UNINST_KEY}"
!define MUI_STARTMENUPAGE_REGISTRY_VALUENAME "${PRODUCT_STARTMENU_REGVAL}"
!insertmacro MUI_PAGE_STARTMENU Application $ICONS_GROUP
; Instfiles page
!insertmacro MUI_PAGE_INSTFILES
; Finish page
!define MUI_FINISHPAGE_RUN
!define MUI_FINISHPAGE_RUN_TEXT "Start the client"
!define MUI_FINISHPAGE_RUN_FUNCTION "LaunchClient"
!define MUI_FINISHPAGE_SHOWREADME "$INSTDIR\Win32-Readme.txt"
!insertmacro MUI_PAGE_FINISH

Function LaunchClient
#Exec "cd $INSTDIR"
SetOutPath $INSTDIR
Exec "crossfire-client-gtk2.exe"
FunctionEnd

; Uninstaller pages
!insertmacro MUI_UNPAGE_INSTFILES

; Language files
!insertmacro MUI_LANGUAGE "English"

; Reserve files
!insertmacro MUI_RESERVEFILE_INSTALLOPTIONS

; MUI end ------

Name "${PRODUCT_NAME} ${PRODUCT_VERSION}"
OutFile "Crossfire client Gtk v2 1.12.0.exe"
InstallDir "$PROGRAMFILES\Crossfire\Gtk Client v2"
InstallDirRegKey HKLM "${PRODUCT_DIR_REGKEY}" ""
ShowInstDetails hide
ShowUnInstDetails hide

Section "!Client" SEC01

  SetOutPath "$INSTDIR"
  SetOverwrite ifnewer
  File "nsis-tmp\SDL_image.dll"
  File "nsis-tmp\SDL.dll"
  File "nsis-tmp\pthreadGC2.dll"
  File "nsis-tmp\libxml2.dll"
  File "nsis-tmp\libtiff3.dll"
  File "nsis-tmp\libpng12-0.dll"
  File "nsis-tmp\libpangowin32-1.0-0.dll"
  File "nsis-tmp\libpangocairo-1.0-0.dll"
  File "nsis-tmp\libpango-1.0-0.dll"
  File "nsis-tmp\libgtk-win32-2.0-0.dll"
  File "nsis-tmp\libgobject-2.0-0.dll"
  File "nsis-tmp\libgmodule-2.0-0.dll"
  File "nsis-tmp\libglib-2.0-0.dll"
  File "nsis-tmp\libglade-2.0-0.dll"
  File "nsis-tmp\libgio-2.0-0.dll"
  File "nsis-tmp\libgdk-win32-2.0-0.dll"
  File "nsis-tmp\libgdk_pixbuf-2.0-0.dll"
  File "nsis-tmp\libcurl-4.dll"
  File "nsis-tmp\libcairo-2.dll"
  File "nsis-tmp\libatk-1.0-0.dll"
  File "nsis-tmp\intl.dll"
  File "nsis-tmp\iconv.dll"
  File "nsis-tmp\gtk-query-immodules-2.0.exe"
  File "nsis-tmp\gspawn-win32-helper-console.exe"
  File "nsis-tmp\gspawn-win32-helper.exe"
  File "nsis-tmp\gdk-pixbuf-query-loaders.exe"
  File "gtk-v2\src\crossfire-client-gtk2.exe"
  File "nsis-tmp\zlib1.dll"
  SetOutPath "$INSTDIR\glade-gtk2"
  File "gtk-v2\glade\README"
  File "gtk-v2\glade\dialogs.glade"
  File "gtk-v2\glade\gtk-v2.glade"
  SetOutPath "$INSTDIR\pixmaps"
  File "pixmaps\16x16.png"
  File "pixmaps\32x32.png"
  File "pixmaps\48x48.png"
  File "pixmaps\all.xpm"
  File "pixmaps\applied.xbm"
  File "pixmaps\applied.xpm"
  File "pixmaps\bg.xpm"
  File "pixmaps\clear.xbm"
  File "pixmaps\client.ico"
  File "pixmaps\close.xbm"
  File "pixmaps\close.xpm"
  File "pixmaps\coin.xpm"
  File "pixmaps\crossfiretitle.xpm"
  File "pixmaps\cursed.xbm"
  File "pixmaps\cursed.xpm"
  File "pixmaps\damned.xbm"
  File "pixmaps\damned.xpm"
  File "pixmaps\dot.xpm"
  File "pixmaps\hand.xpm"
  File "pixmaps\hand2.xpm"
  File "pixmaps\lock.xpm"
  File "pixmaps\locked.xbm"
  File "pixmaps\locked.xpm"
  File "pixmaps\mag.xpm"
  File "pixmaps\magic.xbm"
  File "pixmaps\magic.xpm"
  File "pixmaps\nonmag.xpm"
  File "pixmaps\question.sdl"
  File "pixmaps\question.xpm"
  File "pixmaps\sign_east.xpm"
  File "pixmaps\sign_flat.xpm"
  File "pixmaps\sign_west.xpm"
  File "pixmaps\skull.xpm"
  File "pixmaps\stipple.111"
  File "pixmaps\stipple.112"
  File "pixmaps\test.xpm"
  File "pixmaps\unlock.xpm"
  File "pixmaps\unpaid.xbm"
  File "pixmaps\unpaid.xpm"
  File "pixmaps\question.111"
  SetOutPath "$INSTDIR\themes"
  File "themes\Black"
  File "themes\Standard"
  SetOutPath "$INSTDIR"
  File "nsis-tmp\COPYING.txt"
  File "Win32-Readme.txt"

; Shortcuts
  !insertmacro MUI_STARTMENU_WRITE_BEGIN Application
  CreateDirectory "$SMPROGRAMS\$ICONS_GROUP"
  CreateShortCut "$SMPROGRAMS\$ICONS_GROUP\Crossfire Client.lnk" "$INSTDIR\crossfire-client-gtk2.exe" "" "$INSTDIR\pixmaps\client.ico"
  CreateShortCut "$DESKTOP\Crossfire Client.lnk" "$INSTDIR\crossfire-client-gtk2.exe" "" "$INSTDIR\pixmaps\client.ico"
  !insertmacro MUI_STARTMENU_WRITE_END
SectionEnd

Section "Additional layouts" SEC02
  SetOutPath "$INSTDIR\glade-gtk2"
  SetOverwrite ifnewer
  File "gtk-v2\glade\caelestis.glade"
  File "gtk-v2\glade\chthonic.glade"
  File "gtk-v2\glade\eureka.glade"
  File "gtk-v2\glade\gtk-v1.glade"
  File "gtk-v2\glade\lobotomy.glade"
  File "gtk-v2\glade\meflin.glade"
  File "gtk-v2\glade\oroboros.glade"
  File "gtk-v2\glade\un-deux.glade"
  File "gtk-v2\glade\v1-redux.glade"

; Shortcuts
  !insertmacro MUI_STARTMENU_WRITE_BEGIN Application
  !insertmacro MUI_STARTMENU_WRITE_END
SectionEnd

Section -AdditionalIcons
  !insertmacro MUI_STARTMENU_WRITE_BEGIN Application
  WriteIniStr "$INSTDIR\${PRODUCT_NAME}.url" "InternetShortcut" "URL" "${PRODUCT_WEB_SITE}"
  CreateShortCut "$SMPROGRAMS\$ICONS_GROUP\Website.lnk" "$INSTDIR\${PRODUCT_NAME}.url"
  CreateShortCut "$SMPROGRAMS\$ICONS_GROUP\Uninstall.lnk" "$INSTDIR\uninst.exe"
  !insertmacro MUI_STARTMENU_WRITE_END
SectionEnd

Section -Post
  WriteUninstaller "$INSTDIR\uninst.exe"
  WriteRegStr HKLM "${PRODUCT_DIR_REGKEY}" "" "$INSTDIR\crossfire-client-gtk2.exe"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "DisplayName" "$(^Name)"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "UninstallString" "$INSTDIR\uninst.exe"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "DisplayIcon" "$INSTDIR\pixmaps\client.ico"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "DisplayVersion" "${PRODUCT_VERSION}"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "URLInfoAbout" "${PRODUCT_WEB_SITE}"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "Publisher" "${PRODUCT_PUBLISHER}"
SectionEnd

; Section descriptions
!insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
  !insertmacro MUI_DESCRIPTION_TEXT ${SEC01} "All necessary files to play the game"
  !insertmacro MUI_DESCRIPTION_TEXT ${SEC02} "A few different window layouts"
!insertmacro MUI_FUNCTION_DESCRIPTION_END


Function un.onUninstSuccess
  HideWindow
  MessageBox MB_ICONINFORMATION|MB_OK "The Crossfire client was successfully removed from your computer."
FunctionEnd

Function un.onInit
  MessageBox MB_ICONQUESTION|MB_YESNO|MB_DEFBUTTON2 "Are you sure you want to completely remove the Crossfire client and all of its components?" IDYES +2
  Abort
FunctionEnd

Section Uninstall
  !insertmacro MUI_STARTMENU_GETFOLDER "Application" $ICONS_GROUP
  Delete "$INSTDIR\${PRODUCT_NAME}.url"
  Delete "$INSTDIR\uninst.exe"
  Delete "$INSTDIR\COPYING.txt"
  Delete "$INSTDIR\Win32-Readme.txt"
  Delete "$INSTDIR\question.111"
  Delete "$INSTDIR\themes\Standard"
  Delete "$INSTDIR\themes\Black"
  Delete "$INSTDIR\pixmaps\unpaid.xpm"
  Delete "$INSTDIR\pixmaps\unpaid.xbm"
  Delete "$INSTDIR\pixmaps\unlock.xpm"
  Delete "$INSTDIR\pixmaps\test.xpm"
  Delete "$INSTDIR\pixmaps\stipple.112"
  Delete "$INSTDIR\pixmaps\stipple.111"
  Delete "$INSTDIR\pixmaps\skull.xpm"
  Delete "$INSTDIR\pixmaps\sign_west.xpm"
  Delete "$INSTDIR\pixmaps\sign_flat.xpm"
  Delete "$INSTDIR\pixmaps\sign_east.xpm"
  Delete "$INSTDIR\pixmaps\question.xpm"
  Delete "$INSTDIR\pixmaps\question.sdl"
  Delete "$INSTDIR\pixmaps\nonmag.xpm"
  Delete "$INSTDIR\pixmaps\magic.xpm"
  Delete "$INSTDIR\pixmaps\magic.xbm"
  Delete "$INSTDIR\pixmaps\mag.xpm"
  Delete "$INSTDIR\pixmaps\locked.xpm"
  Delete "$INSTDIR\pixmaps\locked.xbm"
  Delete "$INSTDIR\pixmaps\lock.xpm"
  Delete "$INSTDIR\pixmaps\hand2.xpm"
  Delete "$INSTDIR\pixmaps\hand.xpm"
  Delete "$INSTDIR\pixmaps\dot.xpm"
  Delete "$INSTDIR\pixmaps\damned.xpm"
  Delete "$INSTDIR\pixmaps\damned.xbm"
  Delete "$INSTDIR\pixmaps\cursed.xpm"
  Delete "$INSTDIR\pixmaps\cursed.xbm"
  Delete "$INSTDIR\pixmaps\crossfiretitle.xpm"
  Delete "$INSTDIR\pixmaps\coin.xpm"
  Delete "$INSTDIR\pixmaps\close.xpm"
  Delete "$INSTDIR\pixmaps\close.xbm"
  Delete "$INSTDIR\pixmaps\client.ico"
  Delete "$INSTDIR\pixmaps\clear.xbm"
  Delete "$INSTDIR\pixmaps\bg.xpm"
  Delete "$INSTDIR\pixmaps\applied.xpm"
  Delete "$INSTDIR\pixmaps\applied.xbm"
  Delete "$INSTDIR\pixmaps\all.xpm"
  Delete "$INSTDIR\pixmaps\48x48.png"
  Delete "$INSTDIR\pixmaps\32x32.png"
  Delete "$INSTDIR\pixmaps\16x16.png"
  Delete "$INSTDIR\pixmaps\question.111"
  Delete "$INSTDIR\glade-gtk2\gtk-v2.glade"
  Delete "$INSTDIR\glade-gtk2\dialogs.glade"
  Delete "$INSTDIR\glade-gtk2\caelestis.glade"
  Delete "$INSTDIR\glade-gtk2\chthonic.glade"
  Delete "$INSTDIR\glade-gtk2\dialogs.glade"
  Delete "$INSTDIR\glade-gtk2\eureka.glade"
  Delete "$INSTDIR\glade-gtk2\gtk-v1.glade"
  Delete "$INSTDIR\glade-gtk2\gtk-v2.glade"
  Delete "$INSTDIR\glade-gtk2\lobotomy.glade"
  Delete "$INSTDIR\glade-gtk2\meflin.glade"
  Delete "$INSTDIR\glade-gtk2\oroboros.glade"
  Delete "$INSTDIR\glade-gtk2\un-deux.glade"
  Delete "$INSTDIR\glade-gtk2\v1-redux.glade"
  Delete "$INSTDIR\glade-gtk2\README"
  Delete "$INSTDIR\zlib1.dll"
  Delete "$INSTDIR\crossfire-client-gtk2.exe"
  Delete "$INSTDIR\gdk-pixbuf-query-loaders.exe"
  Delete "$INSTDIR\gspawn-win32-helper.exe"
  Delete "$INSTDIR\gspawn-win32-helper-console.exe"
  Delete "$INSTDIR\gtk-query-immodules-2.0.exe"
  Delete "$INSTDIR\iconv.dll"
  Delete "$INSTDIR\intl.dll"
  Delete "$INSTDIR\libatk-1.0-0.dll"
  Delete "$INSTDIR\libcairo-2.dll"
  Delete "$INSTDIR\libcurl-4.dll"
  Delete "$INSTDIR\libgdk_pixbuf-2.0-0.dll"
  Delete "$INSTDIR\libgdk-win32-2.0-0.dll"
  Delete "$INSTDIR\libgio-2.0-0.dll"
  Delete "$INSTDIR\libglade-2.0-0.dll"
  Delete "$INSTDIR\libglib-2.0-0.dll"
  Delete "$INSTDIR\libgmodule-2.0-0.dll"
  Delete "$INSTDIR\libgobject-2.0-0.dll"
  Delete "$INSTDIR\libgtk-win32-2.0-0.dll"
  Delete "$INSTDIR\libpango-1.0-0.dll"
  Delete "$INSTDIR\libpangocairo-1.0-0.dll"
  Delete "$INSTDIR\libpangowin32-1.0-0.dll"
  Delete "$INSTDIR\libpng12-0.dll"
  Delete "$INSTDIR\libtiff3.dll"
  Delete "$INSTDIR\libxml2.dll"
  Delete "$INSTDIR\pthreadGC2.dll"
  Delete "$INSTDIR\SDL.dll"
  Delete "$INSTDIR\SDL_image.dll"
  Delete "$INSTDIR\stderr.txt"
  RMDir /REBOOTOK "$INSTDIR\themes"
  RMDir /REBOOTOK "$INSTDIR\pixmaps"
  RMDir /REBOOTOK "$INSTDIR\glade-gtk2"
  RMDir /REBOOTOK "$INSTDIR"

  Delete "$SMPROGRAMS\$ICONS_GROUP\Uninstall.lnk"
  Delete "$SMPROGRAMS\$ICONS_GROUP\Website.lnk"
  Delete "$DESKTOP\Crossfire Client.lnk"
  Delete "$SMPROGRAMS\$ICONS_GROUP\Crossfire Client.lnk"

  RMDir "$SMPROGRAMS\$ICONS_GROUP"
  RMDir "$INSTDIR"

  DeleteRegKey ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}"
  DeleteRegKey HKLM "${PRODUCT_DIR_REGKEY}"
  SetAutoClose true
SectionEnd