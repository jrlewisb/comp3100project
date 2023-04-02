abstract class SchedulingManager
{
    Server[] servers;
    abstract void initialise(String[] serversRaw);
    abstract Server getNextServer(Server[] capableServers);
}