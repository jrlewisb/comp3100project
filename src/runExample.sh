#USE THIS SCRIPT TO RUN THE PROVIDED DS-SERVER and DS-CLIENT EXAMPLE
gnome-terminal -- bash -c "./ds-server -c sample-config09.xml -v all > simlogs/ffq-log.txt; exec bash" &
sleep 2
./ds-client -a ffq


#ff, bf, wf
#ffq, bfq, wfq