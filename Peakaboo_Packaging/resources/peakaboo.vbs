' This Script Was Modified From:
' Sample VBScript to discover how much RAM in computer
' Author Guy Thomas http://computerperformance.co.uk/
' Version 1.3 - August 2005
' -------------------------------------------------------' 

Function systemMemory()

	Dim objWMIService, objComputer, colComputer 
	Dim strLogonUser, strComputer

	strComputer = "."

	Set objWMIService = GetObject("winmgmts:" _
	& "{impersonationLevel=impersonate}!\\" _ 
	& strComputer & "\root\cimv2") 
	Set colComputer = objWMIService.ExecQuery _
	("Select * from Win32_ComputerSystem")

	For Each objComputer in colComputer 
		systemMemory = objComputer.TotalPhysicalMemory / 1024 / 1024
	Next

end function

Function min( v1, v2 )
	If v1 >= v2 Then min = v2 Else min = v1
End Function

Function getRecommendedMemory()

	percent = 90
	maxMem = 1536
	goodIdea = systemMemory() * percent / 100

	javaMem = min(maxMem, goodIdea)
	
	getRecommendedMemory = CStr(CInt(javaMem)) + "m"

End Function

Dim WshShell
Set WshShell = CreateObject("WScript.Shell")
WshShell.exec("javaw.exe -Xmx" + getRecommendedMemory() + " -XX:MaxNewSize=20m -XX:+UseFastAccessorMethods -XX:+AggressiveOpts peakaboo.Peakaboo")
