

for x in ff bf ffq bfq wfq
do
    ./runallwithlog.sh $x ds-client "benchmarks/$x/ds-client.txt"
    ./runallwithlog.sh $x my-client "benchmarks/$x/ds-client.txt"
done