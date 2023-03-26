javac ./MyClient.java

gnome-terminal -- bash -c "./ds-server -n -c sample-config01.xml -v all; exec bash" &
sleep 2
java MyClient