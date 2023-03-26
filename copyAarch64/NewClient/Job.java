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
        submitTime = tokens[1];
        id = tokens[2];
        estRunTime = tokens[3];
        coresReq = tokens[4];
        memoryReq = tokens[5];
        diskReq = tokens[6];
    }

    String requirements()
    {
        return coresReq + " " + memoryReq + " " + diskReq;
    }    
}