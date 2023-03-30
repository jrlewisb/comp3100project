public class EventData
{
    String[] tokens;
    String type;
    public EventData(String s)
    {
        tokens = s.split(" ");
        type = tokens[0];

    }
}