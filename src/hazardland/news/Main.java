package hazardland.news;

import hazardland.lib.db.Method;
import hazardland.lib.db.Query;
import hazardland.news.Server;
import hazardland.lib.tool.Http;
import hazardland.lib.tool.Text;
import hazardland.news.Board.Refresh;
import hazardland.news.model.Image;
import hazardland.news.model.Setting;
import hazardland.news.model.Source;
import hazardland.news.model.Topic;
import hazardland.news.parsers.Parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Debug;

@SuppressLint("UseSparseArrays")
public class Main extends Application
{
    //public static int FETCHS = 3;
    //public static boolean debug = false;
    public static Download download = new Download();
    public static Database database;
    public static Map <Integer,Fetch> fetchs = new HashMap <Integer, Fetch>();
    public static Map <Integer,Parser> parsers = new HashMap <Integer, Parser>();
    public static Map <Integer,Source> sources = new HashMap <Integer, Source>();
    private static Debug.MemoryInfo memory = new Debug.MemoryInfo();
    private static float previous = 0;
    public Server server;
    public static Setting setting;
    public static boolean update = true;
    @Override
    public void onCreate()
    {
        super.onCreate();
        memory ("----------------------------------");
        database = new Database(getBaseContext(), "hazardland.news", Config.database); 
        server = new Server();
        clean();
        memory ("database");
        ArrayList<Source> result = database.sources.load (new Query(database.sources));
        for (Source source: result)
        {
            sources.put (source.id, source);
        }
        result.clear();
        result = null;
        download.start();
        memory ("all sources load");
    }
    public void load ()
    {
        memory ("init");
        for (Source source: Board.sources.values()) 
        {
            if (fetchs.get(source.id)!=null)
            {
                if (parsers.get(source.id)!=null)
                {
                    Board.handler.post(new Board.Progress(source, parsers.get(source.id).active()));
                }
            }
        }
    }
    
    public void update ()
    {
        Fetch.fetch ();
    }
    
    public void update (Source source)
    {
        Fetch.fetch (source.id);
    }
    
    public static void memory (String message)
    {
        if (Config.debug)
        {
            //float free = Config.memory;
            Debug.getMemoryInfo (memory);
            debug ("memory: "+String.format("%.02f",memory.getTotalPrivateDirty()/1024.0)+"mb cost "+String.format("%.02f",(memory.getTotalPrivateDirty()-previous)/1024.0)+"mb of "+String.format("%.02f",(float)(Config.memory/1024/1024))+"mb - "+message);
            previous = memory.getTotalPrivateDirty();
        }
    }
    
    public static boolean memory ()
    {
        Debug.getMemoryInfo (memory);
        if ((Config.memory/1024-memory.getTotalPrivateDirty())<10*1024)
        {
            debug ("************ memory limit *****************");
            return false;
        }
        return true;
        
    }
    
    public static void debug (String message)
    {
        if (Config.debug) System.out.println ("main: "+message);
    }
    
    
    
    static public class Download extends Thread
    {
        class Target
        {
            public int image;
            public int topic;
            public int source;
            public String link;
            public Target (Topic topic)
            {
                this.image = topic.image.id;
                this.link = topic.image.link;
                this.source = topic.source.id;
                this.topic = topic.id;
            }
        }
        Map<Integer,Target> targets = new ConcurrentHashMap <Integer,Target>();
        Boolean sleep = true;
        public Download ()
        {
    
        }
        @Override
        public void run()
        {
            while (true)
            {
                sleep ();
                if (targets.size()>0)
                {
                    for (int id: targets.keySet ())
                    {
                        if (!memory())
                        {
                            continue;
                        }
                        Target target = targets.get(id);
                        Image image = new Image();
                        image.link = target.link;
                        image.id = target.image;
                        debug ("trying to download image "+id);
                        Http http = new Http();
                        image.data.crop (http.data(image.link), Config.width, Config.height);
                        http.finish();
                        http = null;
                        if (image.data.value!=null)
                        {
                            debug ("downloaded image "+id);
                            database.images.save (image);
                            if (Board.handler!=null && Board.sources!=null && Board.sources.get(target.source)!=null && Board.sources.get(target.source).topics!=null)
                            {
                                for (int topic=0; topic<Board.sources.get(target.source).topics.size(); topic++)
                                {
                                    if (Board.sources.get(target.source).topics.get(topic).id==target.topic)
                                    {
                                        debug ("refreshing board for image "+id);
                                        Board.sources.get(target.source).topics.get(topic).image.data.value = image.data.value;
                                        Board.handler.post (new Board.Icon(Board.sources.get(target.source).topics.get(topic).icon));
                                        break;
                                    }
                                }
                            }
                            memory ("before image download pause");
                            try 
                            {
                                java.lang.Thread.sleep (10);
                            } 
                            catch (InterruptedException error) 
                            {
                                
                            }                            
                            memory ("after image download pause");
                        }
                        else
                        {
                            debug ("downloaded image "+id+" failed");
                        }
                        targets.remove(id);
                    }
                }                   
            }
        }
        public void sleep ()
        {
            while (true)
            {
                if (!sleep)
                {
                    sleep = true;
                    return;
                }            
                try 
                {
                    java.lang.Thread.sleep (1000);
                } 
                catch (InterruptedException error) 
                {
                    
                }
            }
        }
        public void add (Topic topic)
        {
            if (topic.image.data.empty() && !Text.empty(topic.image.link) && Text.begins("http", topic.image.link))
            {
                debug ("waking to download image for "+topic.title);
                targets.put (topic.image.id, new Target(topic));
                sleep = false;
            }
        }
    }        
    
    static public class Fetch extends Thread
    {
        public static Map <Integer,Integer> schedules = new ConcurrentHashMap <Integer, Integer>();        
        public static int last = -1;
        public int source;
        public Fetch (int source)
        {
            this.source = source;            
        }
        @Override
        public void run()
        {
            sources.get(source).fetch();
            if (Board.handler!=null && Board.sources!=null && Board.sources.get(source)!=null)
            {
                Board.handler.post (new Refresh(source));
            }
            fetchs.remove (source);
            next();
        }
        public static void next ()
        {
            for (Integer source : schedules.values())
            {
                fetch (source);
                return;
            }
        }
        
        public static void fetch (int source)
        {
            if (fetchs.get(source)==null)
            {
                if (fetchs.size()>=Config.fetchs)
                {
                    if (fetchs.get(last)!=null)
                    {
                        fetchs.get(last).interrupt();
                        fetchs.remove(last);
                    }
                }
                if (fetchs.size()<Config.fetchs)
                {
                    schedules.remove (source);
                    fetchs.put (source,new Fetch(source));
                    fetchs.get (source).start();
                    last = source;
                }
            }
        }
        
        public static void fetch ()
        {
            if (Board.sources!=null && Board.sources.size()>0)
            {
                for (Source source: Board.sources.values())
                {
                    schedules.put (source.id, source.id);
                }
            }
            int limit = 0;
            for (Integer source : schedules.values())
            {
                limit++;
                fetch(source);
                if (limit==Config.fetchs)
                {
                    return;
                }
            }
        }        
    }
    
    public void clean ()
    {
        int count = database.images.count(); 
        if (count>Config.images)
        {
            Query query = new Query(database.images);
            query.limit.count = count - Config.images;
            query.order.method = new Method(Method.ASC);
            query.set.query = "data=null";
            database.images.update (query);
        }
    }

}
