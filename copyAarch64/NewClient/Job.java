public class Job
{
    public int id;
    public String type;
    public int submitTime;
    public int estRunTime;
    public int coresReq;
    public int memoryReq;
    public int diskReq;
    public Job(String[] tokens)
    {
        type = tokens[0];
        submitTime = Integer.valueOf(tokens[1]);
        id = Integer.valueOf(tokens[2]);
        estRunTime = Integer.valueOf(tokens[3]);
        coresReq = Integer.valueOf(tokens[4]);
        memoryReq = Integer.valueOf(tokens[5]);
        diskReq = Integer.valueOf(tokens[6]);
    }

    String requirements()
    {
        return coresReq + " " + memoryReq + " " + diskReq;
    }    
}