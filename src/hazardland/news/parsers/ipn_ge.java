package hazardland.news.parsers;

import hazardland.lib.tool.Text;
import hazardland.news.Config;
import hazardland.news.Main;
import hazardland.news.model.Article;
import hazardland.news.model.Image;
import hazardland.news.model.Topic;

public class ipn_ge extends Parser
{
    //public static boolean active = false;
    public ipn_ge ()
    {
        
    }

    @Override
    public void fetch ()
    {
    	debug ("FETCHING IPN");
    	if (active())
    	{
    		return;
    	}
    	enable ();
        Value value = null;
        values.clear();
        String result = text ("http://www.interpressnews.ge/ge/dghis-yvela-siakhle.html?view=allnews");
        if (result==null)
        {
            debug ("seems no connection");
            disable ();
            return;
        }
        //debug (result);
        Text text = new Text(result, "<div class=\"othernews\">", "<div class=\"media_block_tvpal\">");
        if (Text.empty(text.content))
        {
            debug ("url fetch failed with text");
            disable ();
            return;
        }
        //debug ("url content is: "+text.content.length()+" bytes");
        //debug ("url content is: "+text.content);
        while (text.next("<div class=\"othernews_item\">", "<div class=\"othernews_more\">"))
        {
            value = new Value();
            value.title = Text.between ("title=\"", "\"", text.result).replace("&quot;", "\"");
            debug ("[title "+value.title+"]");
            if (!Text.empty(value.title))
            {
                value.icon = "http:"+Text.between("<img src=\"", "\"", text.result);
                value.link = Text.between("<div class=\"othernews_title\">", "</div>", text.result); //<div class=\"othernews_title\">
                value.link = Text.between ("<a href=\"", "\"", text.result);
                if (!Text.empty(value.link))
                {
                    value.hash = Text.between("/ge/", "-", value.link);
                    value.hash = Text.afterLast("/", value.hash);
                    if (!Text.begins("http://",value.link) && !Text.begins("http://",value.link))
                    {
                        value.link = "http://www.interpressnews.ge"+value.link;    
                    }
                    debug ("value.title: " + value.title + " value.image: " + value.icon + " " + " value.link: " + value.link + " " + " value.hash: " + value.hash);
                    if (!Text.empty(value.hash) && !exists(value.hash))
                    {
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
        	    value = values.get(position);
    			if (Text.begins("http://", value.link))
                {

                    refresh++;
                    Topic topic = new Topic();
                    topic.database (Main.database);
                    topic.title = value.title;
                    topic.hash = value.hash;
                    
                    debug("hash "+topic.hash);
                    
    			    result = text (value.link);
                    if (result!=null)
                    {
                        topic.date.set (Text.between("<span class=\"createdate\">", "</span>", result));

                        article = new Article(value.link, Text.between("<div class=\"article-content\">", "<div class=\"cls\">", result).trim());
                        //debug ("article content is "+article.content);
                        if (!Text.empty(article.content))
                        {
                            value.image = Text.between("<img src=\"", "\"", article.content);
                        }
                        
                        if (!Text.empty(value.image) && Text.begins("http://", value.image))
                        {
                            image = new Image();
                            image.link = value.image;
                            //image.data.crop (http.data(value.image), Topic.WIDTH, Topic.HEIGHT);
                        }
                        else if (Text.begins("http://", value.icon)) 
                        {
                            image = new Image();
                            image.link = value.icon;
                            //image.data.crop (http.data(value.icon), Topic.WIDTH, Topic.HEIGHT);
                        }
                        
                        topic.image = image;
                        add (topic);
                        Main.memory ("article before clean");
                        topic.article(article);
                        
                        if (refresh>8)
                        {
                            refresh ();
                            refresh = 0;
                        }
                        article = null;
                        image = null;
                        Main.memory ("article after clean");
                    }
                    else
                    {
                    	debug ("empty result");
                        topic.delete();
                    }
                }             
    		}
            if (refresh>0)
            {
                refresh();
            }
        }
        else
        {
        	debug ("EMPTY VALUES");
        }
        values.clear();
        disable ();
    }
    
    
}
