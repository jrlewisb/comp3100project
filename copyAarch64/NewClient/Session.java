import java.io.*;
import java.net.*;
import java.lang.Math.*;
import java.util.Vector;
import java.util.Arrays;

public class Session
{
    BufferedReader in;
    DataOutputStream out;
    Socket sock;
    Server[] sortedServers;
    int robinNumber = 0;
    public Session(Socket sock)
    {
        try
        {
            this.sock = sock;
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            out = new DataOutputStream(sock.getOutputStream());
        }catch(Exception e){System.out.println(e);}
    }

    public void print(String s)
    {
        System.out.println(s);
    }

    public void writeln(String s) throws Exception
    {
        print("CLIENT SEND: " + s);
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
        print("HEADER RCVD: " + header);
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
            print(l);
        }
        return lines;
    }

    public void handleJOBN(EventData e) throws Exception
    {
        Job job = new Job(e.tokens);
        print(job.type);
        for(String s : e.tokens){
            System.out.print(s + " ,");
        }
        print("\n");
        writeln("GETS Capable " + job.requirements());
        String[] serversRaw = handleData(in.readLine());
        if(this.sortedServers == null)
        {
            sortServers(serversRaw);
        }
        if(responseEqual("."))
        {
            Server[] servers = new Server[serversRaw.length];
            print("RAW SERVERS");
            for(int i = 0; i < serversRaw.length; i++)
            {   
                servers[i] = new Server(serversRaw[i]);
            }

            String largestType = this.sortedServers[0].type;
            print(largestType);

            Vector<Server> serversOfLargestType = new Vector<Server>();
            int minTotalJobs = Integer.MAX_VALUE;

            for(Server s : servers){
                if(s.type.equals(largestType)){
                    // if(s.cores == 0){
                    //     continue;
                    // }
                    serversOfLargestType.add(s);
                    minTotalJobs = Math.min(s.waitingJobs + s.runningJobs, minTotalJobs);
                } 
            }

            robinNumber %= serversOfLargestType.size();
            // while(serversOfLargestType.get(robinNumber).cores > -10)
            // {
            //     robinNumber += 1;
            //     robinNumber %= serversOfLargestType.size();
            // }
            sendSCHD(job, serversOfLargestType.get(robinNumber));
            robinNumber+=1;


            // int minId = Integer.MAX_VALUE;
            // Vector<Server> largestWithMinTotalJobs = new Vector<Server>();

            // for(Server s : serversOfLargestType){
            //     if(s.runningJobs + s.waitingJobs == (minTotalJobs)){
            //         // if(s.cores == 0){
            //         //     continue;
            //         // }
            //         largestWithMinTotalJobs.add(s);
            //         minId = Math.min(s.id, minId);
            //     } 
            // }
            
 
            // for(Server s : largestWithMinTotalJobs)
            // {
            //     if(s.id == minId){
            //         print("SCHEDULING CHOICE MADE FOR: " + job.id + " TO SERVER: " + s);
            //         sendSCHD(job, s);
            //         break;
            //     }
                
            // }


            

        }
        if(!in.readLine().equals("OK")){throw new Exception();}

    }

    void sortServers(String[] rawServers)
    {
        Server[] servers = new Server[rawServers.length];

            for(int i = 0; i < rawServers.length; i++)
            {   
                servers[i] = new Server(rawServers[i]);
            }

            Arrays.sort(servers);
            this.sortedServers = servers;
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
        print(e.type);
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
                print(event);
                handleEvent(event);
            }
            writeln("QUIT");
            out.close();
            if(responseEqual("QUIT")){ in.close(); }
            sock.close();
        }
    }
}