import java.io.*;
import java.net.*;
import java.lang.Math.*;
import java.util.Vector;
import java.util.Arrays;

public class Session
{
    private boolean debug = true; //set to true to see debug output
    BufferedReader in;
    DataOutputStream out;
    Socket sock;
    SchedulingManager schedulingManager;

    public class LRRManager extends SchedulingManager
    {
        //Stateful member variables that we require for our algorithm - these should only be accessed from within the algorithm and we provide a generic interface to the session
        int robinNumber = 0;
        String largestType;

        Server getNextServer(Job job)
        {
            Vector<Server> serversOfLargestType = new Vector<Server>();

            for (Server s : this.servers) {
                if (s.type.equals(largestType)) {
                    serversOfLargestType.add(s);
                }
            }

            this.robinNumber %= serversOfLargestType.size();
            return serversOfLargestType.get(this.robinNumber++);
        }
        
        void schedule(Job job) throws Exception{
            writeln("GETS Capable " + job.requirements());
            String[] serversRaw = handleData(in.readLine());
            if(responseEqual("."))
            {
                if (this.servers == null)
                {
                    this.servers = sortServers(serversRaw); //sort the servers
                    this.largestType = this.servers[0].type; //assign the largest type
                }
                sendSCHD(job, getNextServer(job));
            }
        }
    }

    public class FFManager extends SchedulingManager {
        Server getNextServer(Job job) {
            return this.servers[0];
        }

        void schedule(Job job) throws Exception
        {
            writeln("GETS Capable " + job.requirements());
            String[] serversRaw = handleData(in.readLine());
            if(responseEqual("."))
            {
                this.servers = makeServerArray(serversRaw);
                sendSCHD(job, getNextServer(job));
            }
        }
    }

    public class BFManager extends SchedulingManager {

        Server getNextServer(Job job) {
            return this.servers[this.servers.length - 1];
        }

        void schedule(Job job) throws Exception {
            writeln("GETS Capable " + job.requirements());
            String[] serversRaw = handleData(in.readLine());
            if (responseEqual(".")) {
                this.servers = sortServers(serversRaw);
                sendSCHD(job, getNextServer(job));
            }
        }

    }
    
    public class FFQManager extends SchedulingManager {
        Server getNextServer(Job job) {
            return this.servers[0];
        }

        void schedule(Job job) throws Exception {
            writeln("GETS Capable " + job.requirements());
            String[] serversRaw = handleData(in.readLine());
            if (responseEqual(".")) {
                this.servers = makeServerArray(serversRaw);
                sendSCHD(job, getNextServer(job));
            }
        }
    }
    
    public class BFQManager extends SchedulingManager {
        Server getNextServer(Job job) {
            return this.servers[0];
        }

        void schedule(Job job) throws Exception {
            writeln("GETS Capable " + job.requirements());
            String[] serversRaw = handleData(in.readLine());
            if (responseEqual(".")) {
                this.servers = makeServerArray(serversRaw);
                sendSCHD(job, getNextServer(job));
            }
        }
    }
    
    public class WFQManager extends SchedulingManager
    {
        Server getNextServer(Job job) {
            return this.servers[0];
        }

        void schedule(Job job) throws Exception
        {
            writeln("GETS Capable " + job.requirements());
            String[] serversRaw = handleData(in.readLine());
            if(responseEqual("."))
            {
                this.servers = makeServerArray(serversRaw);
                sendSCHD(job, getNextServer(job));
            }
        }
    }
    
    public class AssignmentManager extends SchedulingManager {
        int capacity;
        //This will be my assignment algorithm
        //Lets call it "first capable low capacity"
        //Because we have bootup time costs,
        //We want to use servers that are running, if possible
        //However, we don't want to use the same servers for all jobs 
        //If there are other servers available
        //So we will set some threshold for capacity
        //And if the server is below that capacity, we will use it
        //Otherwise, we will use the next best server under capacity
        //In the case that the next server is not capable

        //Other option:
        //Jobs have a estimated time,
        //Servers have power
        //if the power of a
        AssignmentManager() {
            this.capacity = 3;
        }

        Server getNextServer(Job job) {
            return new Server("temp");
        }
        
        void schedule(Job job) throws Exception
        {
            writeln("GETS Capable " + job.requirements());
            String[] serversRaw = handleData(in.readLine());
            if(responseEqual("."))
            {
                this.servers = sortServers(serversRaw);
                sendSCHD(job, getNextServer(job));
            }
        }
    }

    public Session(Socket sock, String algorithm)
    {
        try
        {
            this.sock = sock;
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            out = new DataOutputStream(sock.getOutputStream());

        }catch(Exception e){System.out.println(e);}

        //if our session was successfully started, lets instanciate our schedulingManager
        switch(algorithm)
        {
            case "lrr" : this.schedulingManager = new LRRManager(); break;
            case "ff":
                this.schedulingManager = new FFManager();
                break;
            case "bf":
                this.schedulingManager = new BFManager();
                break;
            case "ffq":
                this.schedulingManager = new FFQManager();
                break;
            case "wfq":
                this.schedulingManager = new BFQManager();
                break;
        }
        
    }

    public void print(String s)
    {
        System.out.println(s);
    }

    public void debugln(String s)
    {
        if(this.debug)
        {
            System.out.println(s);
        }
    }

    public void debug(String s)
    {
        if(this.debug)
        {
            System.out.print(s);
        }
    }

    public void writeln(String s) throws Exception
    {
        debugln("CLIENT SEND: " + s);
        out.write(s.concat("\n").getBytes());
        out.flush();
    }

    boolean responseEqual(String s) throws Exception
    {
        return s.equals(in.readLine());
    }

    String compareAndReturn(String s) throws Exception
    {
        String res = in.readLine();
        if(s.equals(res))
        {
            return res;
        }else{
            throw new Exception();
        }
    }

    

    public boolean makeConnection() 
    {
        try
        {
            writeln("HELO");
            if(responseEqual("OK"))
            {
                writeln("AUTH " + System.getProperty("user.name"));
                if(responseEqual("OK"))
                {
                    return true;
                }
            }
            return false;
        }catch(Exception e){
            print(e.toString());
            return false;
            }
    }

    public void sendSCHD(Job job, Server server) throws Exception
    {
        String infoString = "SCHD " + job.id + " " + server.type + " " + server.id;
        debugln(infoString);
        writeln(infoString);
    }


    public String[] handleData(String header) throws Exception
    {
        debugln("HEADER RCVD: " + header);
        String[] headerData = header.split(" ");
        if(!headerData[0].equals("DATA")){
            throw new Exception();
        }
        int amtLines = Integer.valueOf(headerData[1]);
        int amtBytes = Integer.valueOf(headerData[2]);
        //prepare to read
        writeln("OK");
        String[] lines = new String[amtLines];
        for(int i = 0; i < amtLines; i++)
        {
            lines[i] = in.readLine();
        }
        //after all data recieved
        writeln("OK");
        for(String l : lines){
            debugln(l);
        }
        return lines;
    }

    public void handleJOBN(EventData e) throws Exception
    {
        Job job = new Job(e.tokens);
        debugln(job.type);
        for(String s : e.tokens){ debug(s + " ,"); } //debug
        debugln("\n");
        schedulingManager.schedule(job);
        if(!in.readLine().equals("OK")){throw new Exception();}

    }

    Server[] makeServerArray(String[] rawServers)
    {
        Server[] servers = new Server[rawServers.length];

            for(int i = 0; i < rawServers.length; i++)
            {   
                servers[i] = new Server(rawServers[i]);
            }

            return servers;
            //end sort servers
    }

    Server[] sortServers(String[] rawServers)
    {
        Server[] serverArray = makeServerArray(rawServers);
        Arrays.sort(serverArray);
        return serverArray;
    }

    void handleJCPL(EventData e) throws Exception
    {
        //job completion
        //should just continue?
        //
        return;
    }

    public void handleEvent(String s) throws Exception
    {
        EventData e = new EventData(s);
        debugln(e.type);
        switch(e.type)
        {
            case "JOBN": handleJOBN(e); break;
            case "JOBP": break; //handleJOBP(e); break;
            case "JCPL": handleJCPL(e); break;
            case "RESF": //handleRESF(e); break;
            case "RESR": //handleRESR(e); break;
            case "CHKQ": //handleCHKQ(e); break;
            case "NONE": //not needed
        }
        writeln("REDY");
    }

    public void start() throws Exception
    {
        if(makeConnection() == true) //if successful connection
        {
            writeln("REDY"); //let the server know we are ready to begin the event loop
            String event;
            while(!(event = in.readLine()).equals("NONE"))
            {
                debugln(event);
                handleEvent(event);
            }
            writeln("QUIT");
            //out.close();
            if(responseEqual("QUIT")){ in.close(); }
            return;
        }
    }
}