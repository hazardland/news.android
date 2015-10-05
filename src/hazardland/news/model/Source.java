package hazardland.news.model;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import hazardland.lib.db.Date;
import hazardland.lib.db.Entity;
import hazardland.lib.db.Method;
import hazardland.lib.db.Query;
import hazardland.news.Board;
import hazardland.news.Config;
import hazardland.news.Main;
import hazardland.news.R;
import hazardland.news.Board.Refresh;
import hazardland.news.parsers.Parser;
import hazardland.news.view.Horizontal;

public class Source extends Entity 
{
    //public static int LIMIT = 5;
    public static final int APPEND = 1;
    public static final int PREPEND = 2;
	public String name;
	public String caption;
	public String link;
	public Date update;
	public String last;
	public Image image;
	public Boolean enabled = true;
	public Integer order;
	public ArrayList <Topic> topics; // = new ArrayList<Topic>();
	public Query next;
	public Parser parser;
	public String description;
	public LinearLayout view;
	
	public Source ()
	{
	    
	}
	
	public Source (String name, String caption, String link, String description)
	{
		this();
		this.name = name;
		this.caption = caption;
		this.link = link;
		this.description = description;
	}
	
    public void prepend (LinearLayout grid)
    {
        render (grid, PREPEND);
    }	

	public void prepend ()
	{
		if (view!=null)
		{
			render (null, PREPEND);
		}
	}

    public void append (LinearLayout grid)
    {
        render (grid, APPEND);
    }   
	
	public void append ()
	{
	    if (view!=null)
	    {
	        render (null, APPEND);
	    }
	}
	
	private void render (LinearLayout grid, int mode)
	{
	    if (view==null && grid==null)
	    {
	        return;
	    }
		boolean add = false;
		if (view == null) 
		{
			add = true;
			view = new LinearLayout(grid.getContext());
			LayoutInflater inflater;
			inflater = (LayoutInflater) grid.getContext().getSystemService (Context.LAYOUT_INFLATER_SERVICE);
			inflater.inflate (R.layout.row, (ViewGroup) view, true);
			view.setTag (this);
            Horizontal.class.cast(view.findViewById(R.id.grid_row_box)).setTag(this);
            Horizontal.class.cast(view.findViewById(R.id.grid_row_box)).setId(this.id);
			view.setOnClickListener ((OnClickListener) grid.getContext());
		} 
		
		TextView.class.cast(view.findViewById(R.id.grid_row_title)).setText(caption);
		
		
		if (add)
		{
		    TextView.class.cast(view.findViewById(R.id.grid_row_title)).setTypeface(Board.class.cast(grid.getContext()).font.face);
			grid.addView (view);
		}
		
		if (topics!=null)
		{
		    Topic topic;
		    for (int position=0;position<topics.size();position++)
		    //for (Topic topic: topics)
			{
		        topic = topics.get (position);
		        debug("populating topic with id " + topic.id);
			    if (mode==APPEND)
			    {
			        topic.append (view);
			    }
			    else
			    {
			        topic.prepend (view);
			    }
			}
		}

	}
	
	public void load ()
	{
	    if (this.topics==null)
	    {
	        Main.database.images.field("data").config.image.decode = false;
	        Query query = new Query(database.table(Topic.class));
	        query.limit.count = Config.topics;
	        query.order.method = new Method(Method.DESC);
	        topics = database.table(Topic.class).of (this, query);
	        if (topics!=null)
	        {
	            for (Topic topic: topics)
	            {
                    if (topic.image.data.empty())
                    {
                        Main.download.add (topic);
                    }
	            }
	        }
	        Main.database.images.field("data").config.image.decode = true;
	    }
	}
	
    public void refresh ()
    {
        debug ("inside refresh");
        if (this.topics!=null)
        {
            Query query = new Query(database.table(Topic.class));
            //query.limit.count = LIMIT;
            query.debug = true;
            query.order.method = new Method(Method.DESC);
            if (topics.size()>0)
            {
                int high = 0;
                for (int position=0;position<topics.size();position++)
                {
                    if (topics.get(position).id>high)
                    {
                        high = topics.get(position).id;
                    }
                }
                System.out.println ("high "+high+" vs first "+topics.get(0).id()+" vs last "+topics.get(topics.size()-1).id);
                query.where.string = "topic.source="+this.id+" and topic.id>"+high;
            }
            else
            {
                query.where.string = "topic.source="+this.id;
            }
            //database.table(Topic.class).of (this, query);
            ArrayList<Topic> result = Main.database.topics.load(query);
            if (result!=null && result.size()>0)
            {
                for (Topic topic: result)
                {
                    topics.add(0,topic);
                    if (topic.image.data.empty())
                    {
                        Main.download.add (topic);
                    }
                    debug ("refreshing topic "+topic.title);
                }
            }
            else
            {
                debug ("no topics fetched");
            }
            Board.handler.post (new Refresh(this, Source.PREPEND));
        }
        else
        {
        	debug ("vbanaobt");
        }
    }

	@SuppressWarnings("unchecked")
    public void next ()
	{
	    if (next==null)
	    {
	        next = new Query(database.table(Topic.class));
	        next.limit.count = 5;
	        next.order.method = new Method(Method.DESC);    
	        next.where.string = "\"topic\".\"source\"="+id;
	    }
	    
	    if (topics.size()>0)
	    {
            int low = topics.get(topics.size()-1).id;
            for (int position=0;position<topics.size();position++)
            {
                if (topics.get(position).id<low)
                {
                    low = topics.get(position).id;
                }
            }
            System.out.println ("low "+low+" vs first "+topics.get(0).id()+" vs last "+topics.get(topics.size()-1).id);	        
	        next.where.string = "\"topic\".\"source\"="+id+" and \"topic\".\"id\"<"+low;
	    }
	    
	    //next.limit.from = topics.size();
	    database.table(Topic.class).load(next);
	    topics.addAll(database.table(Topic.class).load(next));
	}
	
	@SuppressWarnings("unchecked")
    public void add (Topic topic)
	{
	    if (topic.source==null)
	    {
	        topic.source = this;
	    }
	    if (topics==null)
	    {
	    	topics = new ArrayList<Topic>(); 
	    }
	    if (topic!=null)
	    {
	    	if (database.table(Topic.class)!=null)
	    	{
	    		debug ("topic saved "+topic.title);
	    		database.table(Topic.class).save (topic);
	    	}
		    //topics.add (topic);
	    }
	}
	
	public boolean fetch ()
	{
	    if (parser==null)
	    {
	        if (Main.parsers.get(this.id)==null)
	        {
	            try
	            {
	                Main.parsers.put (this.id, (Parser) Class.forName("hazardland.news.parsers."+name).newInstance());
	            } 
	            catch (InstantiationException e)
	            {

	            } 
	            catch (IllegalAccessException e)
	            {
	                
	            } 
	            catch (ClassNotFoundException e)
	            {
	                
	            }
	        }
	       
	        if (Main.parsers.get(this.id)!=null)
	        {
	            parser = Main.parsers.get(this.id);
	        }
	        
	        if (parser!=null)
	        {
	            parser.create(this);
	        }
	    }
	    if (parser!=null)
	    {
	        parser.fetch ();
	        return true;
	    }
	    return false;
	}
	
	public void remove (Topic topic)
	{
	    if (topics!=null)
	    {
	        for (int position = 0; position < topics.size(); position++)
            {
                if (topics.get(position).equals(topic))
                {
                    topics.remove (position);
                    return;
                }
            }
	    }
	}
	
	public void progress (boolean visible)
	{
        if (view!=null)
        {
            if (visible)
            {
                view.findViewById(R.id.grid_row_progress).setVisibility(View.VISIBLE);
            }
            else
            {
                view.findViewById(R.id.grid_row_progress).setVisibility(View.INVISIBLE);    
            }
        }           
	}
	
	public void debug (String message)
	{
	    if (Config.debug) System.out.println ("source: "+ message);
	}

}
