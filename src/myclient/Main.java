import java.net.*;
import java.io.*;
import java.util.Vector;

public class Main
{
    
    public static void main(String[] args) throws Exception
    {
        //Variables to store potential CLI arguments
        String algorithm = "lrr"; //this is the default and will be overwritten by CLI arguments passed after -a
        
        //Process the command line arguments to get the algorithm
        for(int i = 0; i < args.length; i++)
        {
            if(args[i].equals("-a") && i < args.length - 1)
            {
                algorithm = args[i+1];
                break;
            }
        }

        Socket sock = new Socket("localhost",50000);
        Session session = new Session(sock, algorithm);
        session.start();
        sock.close();

    }
}