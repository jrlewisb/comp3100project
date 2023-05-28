
#for x in ff bf ffq bfq wfq ass
find S2TestConfigs/ -regextype posix-extended -regex '.*/config[0-9]+(-[a-z]+)+.xml' -print0 | while IFS= read -r -d '' configXML
do
    configXMLFilename=$(basename "$configXML")

    ./runallwithlog.sh ass "my-client" "benchmarks/ass/my-client-$configXMLFilename.txt" $configXML
    sleep 8
    ./runallwithlog.sh bf "my-client" "benchmarks/bf/my-client-$configXMLFilename.txt" $configXML
    sleep 8


done