algo=$1
client=$2
output=$3

if [ "$client" = "ds-client" ]; then
    gnome-terminal -- bash -c "./ds-server -c sample-config01.xml -v all > $output; exec bash"&
    sleep 3
    ./ds-client -a $algo
    sleep 3
else
    ./build.sh
    gnome-terminal -- bash -c "./ds-server -n -c sample-config01.xml -v all > $output; exec bash"&
    sleep 3
    java Main -a $algo
    sleep 3
fi