public class Session
{
    BufferedReader in;
    DataOutputStream out;
    public Session(Socket sock)
    {
        try
        {
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            out = new DataOutputStream(sock.getOutputStream());
        }catch(Exception e){System.out.println(e);}
    }

    public void print(String s)
    {
        System.out.println(s);
    }

    public void writeln(String s)
    {
        print("CLIENT SEND: " + s);
        out.write(s.concat("\n").getBytes());
        out.flush();
    }

    boolean responseEqual(String s)
    {
        return s.equals(in.readLine());
    }

    compareAndReturn(String s)
    {
        String res = in.readLine();
        if(s.equals(res))
        {
            return res;
        }else{
            throw new Exception;
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
        }catch(Exception e){print(e);}
    }

    public boolean 

    public void sendSCHD(Job job, Server server)
    {
        writeln("SCHD " + job.id + " " + server.type + " " + server.id);
    }

    public String[] handleData(String header)
    {
        String[] headerData = header.split(" ");
        int lines = Integer.valueOf(headerData[0]);
        int bytes = Integer.valueOf(headerData[1]);
        //prepare to read
        writeln("OK");
        String[] lines = new String[lines];
        for(int i = 0; i < lines; i++)
        {
            lines[i] = in.readLine();
        }
        //after all data recieved
        writeln("OK");
    }

    public void handleJOBN(Event e)
    {
        Job job = new Job(e.tokens);
        writeln("GETS Capable " + job.requirements());
        String[] serversRaw = handleData(in.readLine());
        if(responseEqual("."))
        {
            Server[] servers = new Server[serversRaw.length];
            for(int i = 0; i < serversRaw.length; i++)
            {
                servers[i] = new Server(serversRaw[i]);
            }
            Arrays.sort(servers);
            sendSCHD(job, server);

        }

    }

    handleJCPL(Event e)
    {
        //job completion
        //should just continue?
    }

    public void handleEvent(String s)
    {
        Event e = new Event(s);
        switch(e.type)
        {
            case "JOBN": handleJOBN(e); break;
            case "JOBP": //handleJOBP(e); break;
            case "JCPL": handleJCPL(e); break;
            case "RESF": //handleRESF(e); break;
            case "RESR": //handleRESR(e); break;
            case "CHKQ": //handleCHKQ(e); break;
            case "NONE": //not needed
        }
    }

    public void start()
    {
        if(makeConnection() == true) //if successful connection
        {
            writeln("REDY"); //let the server know we are ready to begin the event loop
            String event;
            while(!(event = in.readLine()).equals("NONE"))
            {
                handleEvent(event);
            }
            writeln("QUIT");
            out.close();
            if(responseEqual("QUIT")){ in.close(); }
            socket.close();
        }
    }
}