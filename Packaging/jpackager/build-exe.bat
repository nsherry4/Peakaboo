call "%JAVA_HOME%\bin\java.exe" ^
    -Xmx512M ^
    --module-path "%JAVA_HOME%\jmods" ^
    -m jdk.packager/jdk.packager.Main ^
    create-image ^
    --module-path "%MODULE_PATH%" ^
    --verbose ^
    --add-modules "java.base,java.compiler,java.datatransfer,java.desktop,java.logging,java.naming,java.prefs,java.rmi,java.scripting,java.sql,jdk.jdi,jdk.unsupported" ^
    --input ".\input\" ^
    --output "\output\" ^
    --name "Peakaboo" ^
    --main-jar "Peakaboo.jar" ^
    --version "%1" ^
    --jvm-args "--illegal-access=permit ^
    --icon "icon-1024.png" ^
    --class "peakaboo.ui.swing.Peakaboo"
