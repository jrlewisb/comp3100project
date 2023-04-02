import java.io.*;
import java.net.*;
import java.lang.Math.*;
import java.util.Vector;
import java.util.Arrays;

public class Session
{
    private boolean debug = false; //set to true to see debug output
    BufferedReader in;
    DataOutputStream out;
    Socket sock;
    SchedulingManager schedulingManager;

    public class LRRManager extends SchedulingManager
    {
        //Stateful member variables that we require for our algorithm - these should only be accessed from within the algorithm and we provide a generic interface to the session
        int robinNumber = 0;
        String largestType;
        void initialise(String[] serversRaw)
        {
            //Here, we should define any pre-conditions for our algorithm. For example in LRR, we get the largest sorted servers once at the start.
            this.servers = sortServers(serversRaw); //sort the servers
            this.largestType = this.servers[0].type; //assign the largest type
        }

        Server getNextServer(Server[] capableServers)
        {
            Vector<Server> serversOfLargestType = new Vector<Server>();

            for(Server s : capableServers)
            {
                if(s.type.equals(largestType))
                {
                    serversOfLargestType.add(s);
                } 
            }

            this.robinNumber %= serversOfLargestType.size();
            return serversOfLargestType.get(this.robinNumber++);
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
        if(algorithm.equals("lrr")){ this.schedulingManager = new LRRManager(); }
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
                writeln("AUTH josh");
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
        writeln("SCHD " + job.id + " " + server.type + " " + server.id);
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
        writeln("GETS Capable " + job.requirements());
        String[] serversRaw = handleData(in.readLine());
        if(this.schedulingManager.servers == null)
        {
            schedulingManager.initialise(serversRaw);
        }
        if(responseEqual("."))
        {
            Server[] capableServers = new Server[serversRaw.length];
            debugln("RAW SERVERS");
            for(int i = 0; i < serversRaw.length; i++)
            {   
                capableServers[i] = new Server(serversRaw[i]);
            }

            sendSCHD(job, schedulingManager.getNextServer(capableServers));
        }
        if(!in.readLine().equals("OK")){throw new Exception();}

    }

    Server[] sortServers(String[] rawServers)
    {
        Server[] servers = new Server[rawServers.length];

            for(int i = 0; i < rawServers.length; i++)
            {   
                servers[i] = new Server(rawServers[i]);
            }

            Arrays.sort(servers);
            return servers;
            //end sort servers
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
            out.close();
            if(responseEqual("QUIT")){ in.close(); }
            return;
        }
    }
}