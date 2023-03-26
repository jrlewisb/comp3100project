import java.net.*;
import java.io.*;
import java.util.Vector;

public class MyClient{
    public static void writef(DataOutputStream out, String str) throws Exception{
        out.write((str).getBytes());
        out.flush();
    }

    public static void writeln(DataOutputStream out, String str) throws Exception{
        String temp = str.concat("\n");
        out.write(temp.getBytes());
        out.flush();
    }

    public static String readLine(BufferedReader in) throws Exception{
        String temp = in.readLine();
        System.out.println(temp);
        return temp;
    }

    public static String[] sendAndReadResponse(String msg, DataOutputStream o, BufferedReader in)
    throws Exception
    {
        writeln(o, msg);
        String dataInfo = readLine(in);
        //Loop for the amount of expected lines,
        String[] lines = new String[Integer.valueOf(dataInfo.split(" ",3)[1])];
        System.out.println(lines.length);
        writeln(o, "OK"); //let it know its cool to send data
        for(int i = 0; i < lines.length; i++){
            System.out.println(i);
            lines[i] = readLine(in);
        }
        for(String line : lines){
            print(line);
        }
        writeln(o, "OK"); //say thanks brah i got the data
        return lines;
    }

    public static void print(String toPrint){
        System.out.println(toPrint);
    }

    public static String jobReqs(String[] jobArr){
        int l = jobArr.length;
        return jobArr[l - 3] + " " + jobArr[l - 2] + " " + jobArr[l-1];

    }

    public static String[] serverSizes = {"xlarge", "large", "medium", "small", "micro"};

    public static String[] getSortedServers(String[] arr)
    {
        String[] sorted = new String[arr.length];
        System.out.println(arr.length);
        int i = 0;
        for(String size : serverSizes)
        {
            print(size);
            for (String s : arr){
                print(s);
                print(s.split(" ")[0]);
                if(s.split(" ")[0].equals(size)){
                    print(s.split(" ")[0] + " is allegedly equal to " + size);
                    sorted[i++] = s;
                }
            }
        }
        return sorted;
    }

    public static void runScheduler(DataOutputStream o, BufferedReader in) throws Exception{
        print("bababa");
        writeln(o,"REDY");
        String job;
        while((job = readLine(in)) != "NONE")
        {
            String[] jobSplit = job.split(" ");
            String[] capableServers = sendAndReadResponse("GETS Capable " + jobReqs(jobSplit), o, in);
            System.out.println(readLine(in).equals("."));
            String[] sortedServers = getSortedServers(capableServers);
            for(String server : sortedServers){
                print(server);
                String[] serverParsed = server.split(" ");
                if(serverParsed[serverParsed.length - 2].equals("0")){
                    writeln(o, "SCHD " + jobSplit[2] + " " + serverParsed[0] + " " + serverParsed[1]);
                    break;
                }
            }
            print("im here");
            if(readLine(in).equals("OK"));
            //do some cleanup if necessary
            writeln(o, "REDY");
            
            
        }
        
    }

    public static void makeConnection(DataOutputStream o, BufferedReader in) throws Exception{
        writeln(o, "HELO");
        if(readLine(in).equals("OK")){
            writeln(o, "AUTH xxx");
            if(readLine(in).equals("OK")){
                System.out.println("Yeah brother");
                return;
            }
        }
        throw new Exception("Exception ting");
    }

   

    public static void main(String args[]){
        try
        {
            //init
            Socket socket = new Socket("127.0.0.1",50000);
            BufferedReader din = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Yeet");
            //connection
            makeConnection(dout, din);
            System.out.println("We moving?");

            runScheduler(dout, din);
                
            System.out.println("Yeet");
    
            dout.close();
            din.close();
            socket.close();
            
        }catch(Exception e){
            System.out.println(e);
        }
    }
}
