abstract class SchedulingManager
{
    Server[] servers;
    abstract void initialise(String[] serversRaw);

    abstract Server getNextServer(Job job, Server[] capableServers);
}