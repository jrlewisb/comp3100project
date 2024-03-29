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

    private boolean debug = false;

    private void debugln(String s)
    {
        if(this.debug){ System.out.println(s); }
    }
    
    public Server(String s)
    {
        debugln(s);
        String[] arr = s.split(" ");
        type = arr[0];
        id = Integer.valueOf(arr[1]);
        state = arr[2];
        curStartTime = Integer.valueOf(arr[3]);
        cores = Integer.valueOf(arr[4]);
        debugln("CORES OF " + type + " : " + cores);
        memory = Integer.valueOf(arr[5]);
        disk = Integer.valueOf(arr[6]);
        waitingJobs = Integer.valueOf(arr[7]);
        runningJobs = Integer.valueOf(arr[8]);
    }

    @Override
    public int compareTo(Server other)
    {
        //sort descending for easy access
        //compare by type, then wether there are queued jobs, then wether there are running jobs?
        //System.out.print(this.cores + " vs " + other.cores + "\n");

        int comparison = this.cores - other.cores;
        //debugln((comparison > 0) ? "This wins (" + this.cores + ")" : " Other wins (" + other.cores + ")");

        if(comparison > 0){ return -1; }
        if(comparison < 0){ return 1; }

        // comparison = this.memory - other.memory;

        // if(comparison > 0){ return -1; }
        // if(comparison < 0){ return 1; }

        // comparison = this.disk - other.disk;

        // if(comparison > 0){ return -1; }
        // if(comparison < 0){ return 1; }


        // if(this.id > other.id){return 1;}else{return -1;}
        /* THIS IS TOO SMART FOR NOW
        comparison = this.waitingJobs - other.waitingJobs;
        if(comparison < 0){ return -1; }
        if(comparison > 0){ return 1; }
        comparison = this.runningJobs - other.runningJobs;
        if(comparison < 0){ return -1; }
        if(comparison > 0){ return 1; }
        */
        //if not yet returned, these servers are equal 
        return 0;
    }
}