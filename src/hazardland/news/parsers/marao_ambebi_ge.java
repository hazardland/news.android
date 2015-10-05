package hazardland.news.parsers;

import hazardland.lib.tool.Text;
import hazardland.news.Config;
import hazardland.news.Main;
import hazardland.news.model.Article;
import hazardland.news.model.Image;
import hazardland.news.model.Topic;

public class marao_ambebi_ge extends Parser
{
    public static boolean active = false; 
    
    public marao_ambebi_ge ()
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
        String result = text ("http://marao.ambebi.ge/index.php");
        if (result==null)
        {
            debug ("seems no connection");
            disable ();
            return;
        }
        Text text = new Text(result, "<div class=\"page_title\">", "<div class=\"pagination\">");
        if (Text.empty(text.content))
        {
            debug ("url fetch failed with text");
            disable ();
            return;
        }
        while (text.next("<div class=\"latestnews_item\">", "<div class=\"latestnews_more\">"))
        {
            text.result = Text.between("<div class=\"blog_item_img\">", "</div>", text.result);
            value = new Value();
            value.title = Text.between ("alt=\"", "\"", text.result).replace("&quot;", "\"");
            debug ("[title "+value.title+"]");
            if (!Text.empty(value.title))
            {
                value.link = "http://marao.ambebi.ge"+Text.between("<a href=\"", "\"", text.result);
                if (!Text.empty(value.link))
                {
                    value.hash = Text.before("-", Text.afterLast("/",value.link));
                    value.image = Text.between("<img src=\"", "\"", text.result);

                    if (!Text.empty(value.hash) && !exists(value.hash))
                    {
                        debug ("marao value.title: " + value.title + " value.image: " + value.icon + " " + " value.link: " + value.link + " " + " value.hash: " + value.hash);                        
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
                        result = Text.between("<div class=\"article_body\">", "<div class=\"bot_content\">", result);
                        debug ("moambe.article"+result);
                    }
                    if (!Text.empty(result))
                    {
                        topic.date.set (Text.between("<span class=\"article_meta_in\">", "</span>", result));
                        //result = result.replace(Text.between("<span class=\"article_meta_in\">", "</span>", result), "");
                        result = result.replaceAll(Text.replaceTagContentWith("span", " class=\"article_meta_in\""), "");
                        
                        result = result.replaceAll(Text.replaceTag("div"), "");
                        result = result.replaceAll(Text.replaceTag("span"), "");
                        result = result.replaceAll(Text.replaceTag("a"), "");
                        article = new Article(value.link, result);
                        
                        value.icon = Text.between("src=\"http://marao.ambebi.ge/pictures/", "\"", result);
                        if (!Text.empty(value.icon))
                        {
                            value.image = "http://marao.ambebi.ge/pictures/"+value.icon;
                        }
                        if (!Text.empty(value.image) && Text.begins("http://", value.image))
                        {
                            image = new Image();
                            image.link = value.image;
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
                        //topic.delete();
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
        //System.out.println ("parser.marao: "+message);
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
