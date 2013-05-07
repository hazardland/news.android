package hazardland.news.parsers;

import hazardland.lib.tool.Text;
import hazardland.news.Config;
import hazardland.news.Main;
import hazardland.news.model.Article;
import hazardland.news.model.Image;
import hazardland.news.model.Topic;

public class radiotavisufbleba_ge extends Parser
{
    public static boolean active = false; 

    public radiotavisufbleba_ge ()
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
        String result = text ("http://www.radiotavisupleba.ge/rss/?count=35");
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
                    value.hash = Text.between("/content/", ".", value.link);
                    value.hash = Text.afterLast("/", value.hash);
                    value.image = Text.between("<enclosure url=\"", "\"", text.result);
                    value.image = Text.before("cw0_",value.image)+"cw0_w400_r1.jpg";
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
                        topic.date.set (Text.between("<p class=\"article_date\">", "</p>", result));
                       
                        topic.author = Text.after(">",(Text.between("href=\"/author", "</a>", result).trim())).trim();
                        
                        article = new Article(value.link, Text.between("<div class=\"zoomMe\">", "<div class=\"authorBioBox\">", result).trim());
                        article.content = article.content.replaceAll ("<div class='embedded_content_object'\\>(.*?)</div\\></div\\></div\\>", "<img src=\""+value.image+"\">");
                        article.content = article.content.replaceAll(Text.replaceTagContentWith("div"," class=\"box_with_innerQuote\""), "<br>");
                        article.content = article.content.replaceAll(Text.replaceTagContentWith("p"," id=\"ctl00_ctl00_cpAB_cp1_pTags\" class=\"topintend\""), "<br>");
                        article.content = article.content.replaceAll(Text.replaceTagContent("script"), "");
                        article.content = article.content.replaceAll(Text.replaceTagContent("object"), "");
                        article.content = article.content.replaceAll(Text.replaceTag("div"), "");
                        article.content = article.content.replaceAll("width=\"(.*?)\"", "");
                        article.content = article.content.replaceAll("height=\"(.*?)\"", "");


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
        //System.out.println ("parser.radiotavisufbleba: "+message);
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
