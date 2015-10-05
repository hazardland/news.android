package hazardland.news.parsers;

public class ipn_ge_2 extends ipn_ge
{
    public static boolean active = false;
    public ipn_ge_2 ()
    {
        
    }
    
    public void debug (String message)
    {
        //System.out.println ("parser.ipn: "+message);
    }

    @Override
    public boolean active ()
    {
        return active;
    }
    
    @Override
    public void enable ()
    {
        active = true;
        super.enable();
    }
    
    @Override
    public void disable ()
    {
        active = false;
        super.disable();
    }
    
}
