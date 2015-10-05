package hazardland.news;


import hazardland.news.model.Source;
import hazardland.news.model.Topic;
import hazardland.news.view.Font;
import hazardland.news.view.Message;
import hazardland.news.view.Reader;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
//import java.util.Random;

@SuppressLint("UseSparseArrays")
public class Board extends Activity implements OnClickListener 
{
	public static Map <Integer, Source> sources;
    public static Handler handler;

    public Main main;
    public Load load;

    public LinearLayout grid;
    public RelativeLayout board_bar;
    public LinearLayout article_view;
    public Reader reader;
    public Font font;
    
    public Message message;



	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.board);
		
		font = new Font(this);
		
		TextView.class.cast(findViewById(R.id.grid_title)).setTypeface(font.face);
		TextView.class.cast(findViewById(R.id.article_title)).setTypeface(font.face);
		TextView.class.cast(findViewById(R.id.article_share)).setTypeface(font.face);
		TextView.class.cast(findViewById(R.id.grid_options)).setTypeface(font.face);
		//TextView.class.cast(findViewById(R.id.board_message_content)).setTypeface(font.face);
		
		TextView.class.cast(findViewById(R.id.grid_title)).setOnClickListener(this);
		TextView.class.cast(findViewById(R.id.grid_options)).setOnClickListener(this);
		TextView.class.cast(findViewById(R.id.article_share)).setOnClickListener(this);
	
		//database = main.database;

		grid = (LinearLayout) findViewById(R.id.grid);
        board_bar = (RelativeLayout) findViewById (R.id.board_bar);
        article_view = (LinearLayout) findViewById (R.id.article_view);

        grid.setOnClickListener (Board.this);
        article_view.setOnClickListener (this);

        limit ();

        main = (Main)getApplicationContext();

        reader = new Reader (this);
        message = new Message ((LinearLayout)findViewById(R.id.board_message), font, R.id.board_message_content, R.id.board_message_close, R.id.board_message_action);
        if (Main.setting.board)
        {
            message.show ("აპლიკაცია მოამბე საშუალებას გაძლევთ ერთდროულად დაათვალიეროთ ქართული სიახლეების ვებ-გვერდები.\n\nსიახლეების დაფაზე თითოეული ვებ-გვერდის სტატიები განლაგებულია ჰორიზონტალურ ზოლზე.\n\nყველა ვებ-გვერდის სტატიების ერთდროულად განსაახლებლად დააჭირეთ წარწერას „მოამბე“ ან თითით ჩამოსწიეთ მთლიანი დაფა ქვემოთ.\n\nინდივიდუალური ვებ-გვერდის სიახლეების განსაახლებლად დააჭირეთ ვებ-გვერდის სათაურს ან გასწიეთ შესაბამისი ვებ-გვერდის ზოლი მარჯვნივ.\n\nდაფიდან ვებ-გვერდის ზოლის გასაქრობად ან ზოლის რიგის შესაცვლელად ისარგებლეთ ღილაკით „არხები“.");
            Main.setting.board = false;
            Main.database.settings.save (Main.setting);
        }
        else
        {
            message.command (main.server.command());
        }
		
        handler = new Handler();

        load ();

        if (!Main.update)
        {
        	update ();
        	Main.update = true;
        }
	}

	@Override
    protected void onStop()
    {
        super.onStop();
        load.interrupt();
        debug("main is being stoped");
        debug ("cleaned so far "+(Main.database.clean()/1024)+" kb");
        handler = null;
        sources.clear();
        sources = null;
        
        main = null;
        load = null;

        grid = null;
        board_bar = null;
        article_view = null;
        reader = null;
        font = null;
        
        finish();
    }

    public void load ()
	{
        sources = new ConcurrentHashMap<Integer, Source>();
        ArrayList<Source> result = Main.database.sources.load();
        for (int position = 0; position < result.size(); position++) 
        {
            sources.put(result.get(position).id, result.get(position)); //This line may cause concurrent modification error ?
            sources.get(result.get(position).id).append (grid);
            Main.memory (result.get(position).caption+" view create");
        }
        load = new Load ();
        load.start();
        main.load ();
	}
    
    public void next (Source source)
    {
        source.next ();
        source.append ();
    }
    
	public void update ()
	{
	    main.update();
	}
	
    public void update (Source source)
    {
        main.update (source);
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.board, menu);
		return true;
	}

	@Override
	public void onClick (View view) 
	{
		debug ("clicked");
		if (view.getId()==R.id.grid_title)
		{
		    update ();
		}
		else if (view.getId()==R.id.article_share)
        {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, reader.topic.article().link);
            if (font.set)
            {
                startActivity(Intent.createChooser(share, "Share"));
            }
            else
            {
                startActivity(Intent.createChooser(share, "გაზიარება"));
            }
        }
        else if (view.getId()==R.id.article_view)
        {
            reader.hide();
        }
        else if (view.getId()==R.id.grid_options)
        {
            startActivity (new Intent(this, Sources.class));
        }
        else if (view.getTag()!=null)
		{
			if (view.getTag().getClass().isAssignableFrom(Source.class))
			{
			    update ((Source)view.getTag());
			}
			else if (view.getTag().getClass().isAssignableFrom(Topic.class))
			{
				debug ("clicked topic");
				Topic topic = (Topic) view.getTag();
				if (topic.article()!=null && topic.article().content!=null)
				{
				    reader.show (topic);
				}
			}
		}
	}
	
	public void debug (String message)
	{
	    if (Config.debug) System.out.println("board: "+message);
	}
	
	static public class Load extends Thread
	{
	    public Load ()
	    {
	    }
	    public void run ()
	    {
	        try
	        {
                for (Source source: sources.values()) 
                {
                    source.load();
                    Main.memory (source.caption+" load");
                    if (source.topics!=null && source.topics.size()>0)
                    {
                        handler.post(new Refresh(source, Source.APPEND));
                        Main.memory (source.caption+" render");                    
                    }
                }
                boolean refresh = false;
                for (Source source: sources.values()) 
                {
                    Main.memory (source.caption+" load");
                    if (source.topics!=null && source.topics.size()>0)
                    {
                        for (int topic=0;topic<source.topics.size();topic++)
                        {
                            if (Main.memory() && source.topics.get(topic).image.data.set())
                            {
                                handler.post (new Icon(source.topics.get(topic).icon));
                                Main.memory ("image render");
                                refresh = true;
                            }
                        }
                        if (refresh)
                        {
                            //handler.post(new Refresh(source, Source.APPEND));
                        }
                        refresh = false;
                    }
                }
            }
	        catch (NullPointerException error)
	        {
	            
	        }
	    }
	}

    static public class Icon implements Runnable
    {
        Topic.Icon icon;
        public Icon (Topic.Icon icon)
        {
            this.icon = icon;
        }
        public void run ()
        {
            if (icon!=null)
            {
                icon.set ();
            }
        }
    }

    static public class Refresh implements Runnable
    {
        int source;
        int mode = Source.PREPEND;
        public Refresh (Source source)
        {
            this.source = source.id;
        }
        public Refresh (Source source, int mode)
        {
            this.source = source.id;
            this.mode = mode;
        }
        public Refresh (int source)
        {
            this.source = source;
        }
        public Refresh (int source, int mode)
        {
            this.source = source;
            this.mode = mode;
        }
        public void run()
        {
            if (sources.get(source)!=null)
            {
                if (mode==Source.APPEND)
                {
                    sources.get(source).append ();
                }
                else
                {
                    sources.get(source).prepend ();
                }
            }
        }
    }

    static public class Progress implements Runnable
    {
        Source source;
        boolean visible;
        public Progress (Source source, boolean visible)
        {
            this.source = source;
            this.visible = visible;
        }
        public void run()
        {
            source.progress (visible);
        }
    }

    
    public boolean onKeyDown(int keyCode, KeyEvent event) 
    {
        if (keyCode == KeyEvent.KEYCODE_BACK) 
        {
            if (message.visible())
            {
                message.hide();
                return true;
            }
            else if (article_view.getVisibility()==View.VISIBLE)
            {
                reader.hide();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    
    private int pixel (float point)
    {
        return (int)(point*getResources().getDisplayMetrics().density + 0.5f);
    }
    
    public static Source source (int id)
    {
        return sources.get(id);
    }
    
    public void limit ()
    {
        Display display = getWindowManager().getDefaultDisplay();
        if (display.getHeight()>display.getWidth())
        {
            Config.topics = (display.getHeight()/pixel(150))+1; 
        }
        else
        {
            Config.topics = (display.getWidth()/pixel(150))+1;
        }
        debug ("willing limit "+Config.topics);
        if (Config.topics<8)
        {
            Config.topics = 8;
        }
        debug ("150 density is "+pixel(150)+" pixel");
        if (display.getHeight()>display.getWidth())
        {
            Config.fit = display.getWidth();
        }
        else
        {
            Config.fit = display.getHeight();
        }
        
        debug ("fit is "+Config.fit);
    }
}
