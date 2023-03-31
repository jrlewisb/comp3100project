cd ./NewClient && javac ./EventData.java && javac ./Job.java && javac ./Server.java && javac ./Main.java
cd ../

gnome-terminal -- bash -c "./ds-server -n -c sample-config01.xml -v all; exec bash" &
sleep 2
cd ./NewClient && java Main

# mv ./NewClient/EventData.class EventData.class
# mv ./NewClient/Job.class Job.class
# mv ./NewClient/Main.class Main.class
# mv ./NewClient/Server.class Server.class
# mv ./NewClient/Session.class Session.class