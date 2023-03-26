public class Server implements Comparable<Server>
{
    public String type;
    public int id;
    public String state;
    public int curStartTime;
    public int cores;
    public int memory;
    public int disk;
    public int waitingJobs;
    public int runningJobs;
    public Server(String s)
    {

    }

    private int typeAsInt(String type)
    {
        switch(type)
        {
            case "xlarge": return 5;
            case "large": return 4;
            case "medium": return 3;
            case "small": return 2;
            case "micro": return 1;
        }
    }

    @Override
    public int compareTo(Server other)
    {
        //compare by type, then wether there are queued jobs, then wether there are running jobs?
        int comparison = typeAsInt(this.type) - typeAsInt(other.type);
        if(comparison > 0){ return 1; }
        if(comparison < 0){ return -1; }
        comparison = this.waitingJobs - other.waitingJobs;
        if(comparison < 0){ return 1; }
        if(comparison > 0){ return -1; }
        comparison = this.runningJobs - other.runningJobs;
        if(comparison < 0){ return 1; }
        if(comparison > 0){ return -1; }
        //if not yet returned, these servers are equal
        return 0;
    }
}