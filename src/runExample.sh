#USE THIS SCRIPT TO RUN THE PROVIDED DS-SERVER and DS-CLIENT EXAMPLE
gnome-terminal -- bash -c "./ds-server -c sample-config01.xml -v all; exec bash" &
sleep 2
./ds-client -a lrr



