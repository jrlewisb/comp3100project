abstract class SchedulingManager
{
    Server[] servers;


    abstract Server getNextServer(Job job);
    abstract void schedule(Job job) throws Exception;

}