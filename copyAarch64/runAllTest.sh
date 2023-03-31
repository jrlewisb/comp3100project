cd ./NewClient && javac ./EventData.java && javac ./Job.java && javac ./Server.java && javac ./Main.java


mv EventData.class ../EventData.class
mv Job.class ../Job.class
mv Main.class ../Main.class
mv Server.class ../Server.class
mv Session.class ../Session.class

cd ../ && ./S1Tests-wk6.sh -n Main.class