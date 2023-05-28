abstract class SchedulingManager
{
    Server[] servers;


    abstract Server getNextServer(Job job) throws Exception;

    abstract void schedule(Job job) throws Exception;

}