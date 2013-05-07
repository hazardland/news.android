package hazardland.news.parsers;

import hazardland.lib.tool.Text;
import hazardland.news.Config;
import hazardland.news.Main;
import hazardland.news.model.Article;
import hazardland.news.model.Image;
import hazardland.news.model.Topic;

public class navigator_ge extends Parser
{
    public static boolean active = false; 

    public navigator_ge ()
    {
        
    }

    @Override
    public void fetch ()
    {
        if (active)
        {
            return;
        }
        enable ();
        Value value = null;
        values.clear();
        String result = text ("http://navigator.ge/RSS.aspx");
        if (result==null)
        {
            debug ("seems no connection");
            disable ();
            return;
        }
        //debug (result);
        Text text = new Text(result);
        if (Text.empty(text.content))
        {
            debug ("url fetch failed with text");
            disable ();
            return;
        }
        debug ("url content is: "+text.content.length()+" bytes");
        debug ("url content is: "+text.content);
        while (text.next("<item>", "</item>"))
        {
            value = new Value();
            value.title = Text.between ("<title>", "</title>", text.result).replace("&quot;", "\"");
            debug ("[title "+value.title+"]");
            if (!Text.empty(value.title))
            {
                value.link = Text.between("<link>", "</link>", text.result);
                if (!Text.empty(value.link))
                {
                    value.hash = Text.afterLast("=", value.link);
                    //debug ("value.title: " + value.title + " value.image: " + value.icon + " " + " value.link: " + value.link + " " + " value.hash: " + value.hash);
                    if (!Text.empty(value.hash) && !exists(value.hash))
                    {
                        value.date = Text.between("<pubDate>", "</pubDate>", text.result);
                        value.article = Text.between("<description><![CDATA[", "]]></description>", text.result);
                        values.add (value);
                        if (values.size()==Config.topics)
                        {
                            break;
                        }
                    }
                }
                debug ("fetched topic: "+value.title);
            }
            
        }
        
        if (values.size()>0)
        {
            int refresh = 0;
            Image image = null;
            Article article = null;
            for (int position = values.size()-1; position >= 0; position--) 
            {
                debug ("parser.radiotavisufleba: checking "+value.title);
                value = values.get(position);
                if (Text.begins("http://", value.link))
                {
                    refresh++;
                    Topic topic = new Topic();
                    topic.database (Main.database);
                    topic.title = value.title;
                    topic.hash = value.hash;

                    result = text (value.link);

                    if (result!=null)
                    {
                        
                        value.image = Text.between("<img id=\"ctl00_MainContent_Article1_ImageNewsSrc\" class=\"news-img br\" src=\"", "\"", result);
                        if (!Text.empty(value.image))
                        {
                            value.image = "http://navigator.ge/"+value.image;
                            article = new Article(value.link, "<img src='"+value.image+"'>"+value.article);
                        }
                        else
                        {
                            article = new Article(value.link, value.article);    
                        }
                        
                        article.content = article.content.replace("\r", "<p>");
                        
                        if (!Text.empty(value.image) && Text.begins("http://", value.image))
                        {
                            image = new Image();
                            image.link = value.image;
                        }
                        
                        topic.image = image;
                        add (topic);
                        topic.article(article);
                        
                        if (refresh>8)
                        {
                            refresh ();
                            refresh = 0;
                        }
                        article = null;
                        image = null;
                        Main.memory ("article");  
                    }
                }
            }
            if (refresh>0)
            {
                refresh();
            }
        }
        values.clear();
        disable ();
    }
    
    public void debug (String message)
    {
        //System.out.println ("parser.navigator: "+message);
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
