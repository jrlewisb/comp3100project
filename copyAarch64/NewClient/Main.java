import java.net.*;
import java.io.*;
import java.util.Vector;

public class Main
{
    
    public static void main(String[] args) throws Exception
    {
        Socket sock = new Socket("localhost",50000);
        Session session = new Session(sock);
        session.start();
        sock.close();

    }
}