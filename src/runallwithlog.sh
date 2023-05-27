algo=$1
client=$2
output=$3

if [ "$client" = "ds-client" ]; then
    ./ds-server -n -c sample-config01.xml -v all > $output&
    sleep 2
    ./ds-client -a $algo
else
    ./build.sh
    ./ds-server -n -c sample-config01.xml -v all > $output&
    sleep 2
    java Main -a $algo
fi