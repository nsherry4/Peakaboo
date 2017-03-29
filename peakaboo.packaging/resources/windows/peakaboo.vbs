'#!/Windows/System32/wscript.exe
'Windows Launcher


Function IsJavaInstalled()
	On Error Resume Next
	Err.Clear
	Dim WshShell
	Set WshShell = CreateObject("WScript.Shell")

	Dim result
	Set result = WshShell.Exec("javaw.exe")
	If Err.Number = 0 Then
		IsJavaInstalled=True
	Else
		IsJavaInstalled=False
	End If

End Function

Dim WshShell
Set WshShell = CreateObject("WScript.Shell")

If IsJavaInstalled() Then
	WshShell.exec("javaw.exe -Xmx512m -jar Peakaboo.jar")	
Else
	MsgBox "Peakaboo could not locate Java. Please visit Java.com to make sure it is installed on your system.", 0, "Could Not Locate Java"
End If
