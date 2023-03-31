import java.io.*;
import java.net.*;
import java.util.Vector;
import java.util.Arrays;

public class Session
{
    BufferedReader in;
    DataOutputStream out;
    Socket sock;
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
                writeln("AUTH xxx");
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
        if(responseEqual("."))
        {
            Server[] servers = new Server[serversRaw.length];
            print("RAW SERVERS");
            for(int i = 0; i < serversRaw.length; i++)
            {   
                print(serversRaw[i]);
                servers[i] = new Server(serversRaw[i]);
            }
            // int minRunning = Integer.Max();
            // Server mostSuitable;
            // for(int i = 0; i < serversRaw.length - 1; i++)
            // {
            //     minRunning = min(servers[0].runningJobs, minRunning)
            //     if(servers[i].runningJobs
            // }
            Arrays.sort(servers);
            print("SORTED SERVERS:");
            for(int i = 0; i < servers.length; i++)
            {
                System.out.println(servers[i].type);
            }

            print("SCHEDULING CHOICE MADE FOR: " + job.id + " TO SERVER: " + servers[0]);
            sendSCHD(job, servers[0]);
            

        }
        if(!in.readLine().equals("OK")){throw new Exception();}

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