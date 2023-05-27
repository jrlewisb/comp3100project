

#for x in ff bf ffq bfq wfq
for x in ffq 
do
    ./runallwithlog.sh $x "ds-client" "benchmarks/$x/ds-client.txt"&
    sleep 4
    ./runallwithlog.sh $x "my-client" "benchmarks/$x/my-client.txt"
done