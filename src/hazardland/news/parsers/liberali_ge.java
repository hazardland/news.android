package hazardland.news.parsers;

import hazardland.lib.tool.Text;
import hazardland.news.Config;
import hazardland.news.Main;
import hazardland.news.model.Article;
import hazardland.news.model.Image;
import hazardland.news.model.Topic;

public class liberali_ge extends Parser
{
    public static boolean active = false; 

    public liberali_ge ()
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
        String result = text ("http://www.liberali.ge/ge/liberali/articles/");
        if (result==null)
        {
            debug ("seems no connection");
            disable ();
            return;
        }
        Text text = new Text(result, "<h6 class=\"section_title\">სტატიები</h6>", "<ul class=\"pagination\">");
        if (Text.empty(text.content))
        {
            debug ("url fetch failed with text");
            disable ();
            return;
        }
        while (text.next("<li class=\"news_item", "<h5 class=\"author\">"))
        {
            value = new Value();
            value.title = Text.between ("<h2 class=\"title\">", "</h2>", text.result);
            debug ("[title "+value.title+"]");
            if (!Text.empty(value.title))
            {
                value.link = Text.between("<a href=\"", "\"", value.title);
                if (!Text.empty(value.link))
                {
                    value.title = Text.between(">", "</a>", value.title).replace("&X8221;", "");
                    value.hash = Text.betweenLast("/", "/", value.link);
                    value.image = Text.between("src=\"/images/cache/", "\"", text.result);
                    value.image = "http://liberali.ge/images/cache/150x150/"+Text.after("/",value.image);
                    //debug ("value.title: " + value.title + " value.image: " + value.icon + " " + " value.link: " + value.link + " " + " value.hash: " + value.hash);
                    if (!Text.empty(value.hash) && !exists(value.hash))
                    {
                    	values.add (value);
                        if (values.size()==Config.topics)
                        {
                            break;
                        }                    	
                    }
                }
                else
                {
                    debug ("empty link");
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
    		    refresh++;
    			Topic topic = new Topic();
    			topic.database (Main.database);
    			topic.title = value.title;
    			topic.hash = value.hash;
    			
    			if (Text.begins("http://", value.link))
                {
                    result = text (value.link);
                    if (result!=null)
                    {
                        topic.date.set (Text.between("<p class=\"date date_icon light_grey_border_bottom\">", "</p>", result));
                        
                        topic.author = Text.between("<div class=\"author float_left\">", "</div>", result);
                        if (!Text.empty(Text.between(">", "</", topic.author).trim()))
                        {
                            topic.author = Text.between(">", "</", topic.author).trim();
                        }
                        
                        result = Text.between("<div class=\"article_side\">", "<a name=\"comments\"", result);
                        result = ("<p>"+Text.after("<p>", Text.between ("</script>", "<script", result)).trim()).replace("<p></p>", "");
                        result = result.replace("<p>", "[[p]]");
                        result = result.replace("</p>", "[[/p]]");
                        result = result.replace("</br>", "[[br]]");
                        result = result.replace("<br>", "[[br]]");
                        result = result.replaceAll("<(.*?)\\>"," ");
                        result = result.replace("[[p]]", "<p>");
                        result = result.replace("[[/p]]", "</p>");
                        result = result.replace("[[br]]", "<b>");
                        article = new Article(value.link, result);

                        if (!Text.empty(value.image) && Text.begins("http://", value.image))
                        {
                            image = new Image();
                            image.link = value.image;
                            article.content = "<img src='"+value.image+"'>" + article.content;                            
                            //image.data.crop (http.data(value.image), Topic.WIDTH, Topic.HEIGHT);
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
                    else
                    {
                        topic.delete();
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
        //System.out.println ("parser.liberali: "+message);
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
