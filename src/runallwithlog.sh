algo=$1
client=$2
output=$3
config=$4

if [ "$client" = "ds-client" ]; then
    gnome-terminal -- bash -c "./ds-server -c $config -v brief > $output; exec bash"&
    sleep 2
    ./ds-client -a $algo& 
    sleep 2
else
    gnome-terminal -- bash -c "./ds-server -n -c $config -v brief > $output; exec bash"&
    sleep 2
    java Main -a $algo& 
    sleep 2
fi
