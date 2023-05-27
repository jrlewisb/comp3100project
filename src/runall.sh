#USE THIS SCRIPT TO BUILD THE CLIENT, RUN THE SERVER WITH THE SAME CONFIG AND THEN EXECUTE THE CLIENT
./build.sh

gnome-terminal -- bash -c "./ds-server -n -c sample-config01.xml -v all; exec bash" &
sleep 2

java Main -a fc

