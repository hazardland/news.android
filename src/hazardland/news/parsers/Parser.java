package hazardland.news.parsers;

import hazardland.lib.db.Query;
import hazardland.lib.tool.Http;
import hazardland.news.Board;
import hazardland.news.Main;
import hazardland.news.model.Source;
import hazardland.news.model.Topic;

import java.util.ArrayList;

public abstract class Parser
{
    //public Board board;
    //public Database database;
    public int source;
    ArrayList<Value> values = new ArrayList<Value> ();
    //Http http = new Http();
    public void create (Source source)
    {
        //this.board = (Board) context;
        //this.database = board.database;
        this.source = source.id;
    }    
    public void fetch ()
    {
        
    }
    public void refresh ()
    {
        debug ("calling refresh");
        if (Board.handler!=null && Board.source(source)!=null)
        {
            debug ("surely refresh");
            Board.source(source).refresh();
            //Board.handler.post (new Board.Refresh(Board.source(source)));
        }
    }
    
    public boolean active ()
    {
        return false;
    }
    
    public void add (Topic topic)
    {
        Main.sources.get(source).add(topic);
//        if (Board.source(source)!=null)
//        {
//            Board.source(source).add (topic);
//        }
    }
    
    public void enable ()
    {
        if (Board.handler!=null && Board.source(source)!=null)
        {
            Board.handler.post(new Board.Progress(Board.source(source),true));
        }
    }
    
    public void disable ()
    {
        if (Board.handler!=null && Board.source(source)!=null)
        {
            Board.handler.post(new Board.Progress(Board.source(source),false));
        }
    }

    public void debug (String message)
    {
        System.out.println (message);
    }
    
    public boolean exists (String hash)
    {
        Query query = new Query (Main.database.topics);        
        query.where.string = "\"topic\".\"source\"='"+source+"' and \"topic\".\"hash\"='"+hash+"'";
        if (Main.database.topics.exists(query))
        {
        	//if (true) return false;
        	debug ("hash exists "+hash);
            return true;            
        }        
        return false;
    }
    
    public String text (String link)
    {
        Http http = new Http ();
        Main.memory ("after http request");
        return http.text (link);
    }

}
