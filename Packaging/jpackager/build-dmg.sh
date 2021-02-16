JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-11.0.1.jdk/Contents/Home
rm -rf "./output/osx/"

./bin/osx/jpackager create-image \
	-i ./input/ \
	-o ./output/osx/pkg/ \
	-n "Peakaboo" \
	-j Peakaboo.jar  \
	-c "peakaboo.ui.swing.Peakaboo" \
	--verbose  \
	--add-modules java.base,java.compiler,java.datatransfer,java.desktop,java.logging,java.naming,java.prefs,java.rmi,java.scripting,java.sql,jdk.jdi,jdk.unsupported \
	--version "$1" \
	--jvm-args "--illegal-access=permit" \
	--java-options "Xms1g" \
	--icon "peakaboo.icns" \
	--mac-bundle-name "Peakaboo" \
	--mac-bundle-identifier "peakaboo.ui.swing.Peakaboo"

./bin/osx/create-dmg/create-dmg \
	--volname "Peakaboo $1" \
	--volicon "peakaboo.icns" \
	--icon-size 128 \
	--icon Peakaboo.app 64 64 \
	--app-drop-link 256 64 \
	"./output/osx/Peakaboo $1.dmg" \
	./output/osx/pkg/
