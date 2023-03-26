public class Main
{
    public static void main(String[] args)
    {
        Socket sock = new Socket("localhost",50000)
        Session session = new Session(sock);
        session.start();
        sock.close();

    }
}