

for x in ff bf ffq bfq wfq ass
do
    if [ $x = "ass" ]; then
        ./runallwithlog.sh $x "my-client" "benchmarks/$x/my-client.txt"
    else
        ./runallwithlog.sh $x "ds-client" "benchmarks/$x/ds-client.txt"&
        sleep 4
        ./runallwithlog.sh $x "my-client" "benchmarks/$x/my-client.txt"
    fi
done