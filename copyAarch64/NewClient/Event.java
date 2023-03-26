public class EventData
{
    String[] tokens;
    String type;
    public Event(String s)
    {
        tokens = s.split(" ");
        type = tokens[0];

    }
}