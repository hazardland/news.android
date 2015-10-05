package hazardland.news.parsers;

import hazardland.lib.tool.Text;
import hazardland.news.Config;
import hazardland.news.Main;
import hazardland.news.model.Article;
import hazardland.news.model.Image;
import hazardland.news.model.Topic;

public class tabula_ge extends Parser
{
    public static boolean active = false; 

    public tabula_ge ()
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
        String result = text ("http://www.tabula.ge/?feed=rss2");
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
                    value.hash = Text.afterLast("p=", value.link);
                    value.link = "http://www.tabula.ge/article-"+value.hash+".html";
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
                debug ("checking "+value.title);
                value = values.get(position);
                if (Text.begins("http://", value.link))
                {
                    refresh++;
                    Topic topic = new Topic();
                    topic.database (Main.database);
                    topic.title = value.title;
                    topic.hash = value.hash;

                    result = text (value.link);
                    debug ("fetch result for link "+value.link+" result size "+result.length());
                    
                    //debug (result);

                    if (result!=null)
                    {
                        debug ("tabula.date"+Text.between("style=\"color: #999; line-height:22px; float: right; top:3px\">", "</div>", result));
                        topic.date.set (Text.between("style=\"color: #999; line-height:22px; float: right; top:3px\">", "</div>", result));
                        debug ("date is"+topic.date.get());

                        topic.author = (Text.between("<b>ავტორი:</b>", "</a>", result));
                        topic.author = (Text.afterLast(">", topic.author)).trim();
                        debug ("author is --> "+topic.author);                        
                        
                        //article.content = article.content.replaceAll ("<div class='embedded_content_object'\\>(.*?)</div\\></div\\></div\\>", "<img src=\""+value.image+"\">");
                        
                        article = new Article(value.link, Text.after("<h3 class='mt'>დაკავშირებული:</h3>", result));
                        article.content = Text.between("</div>","</div>", article.content);
                        article.content = article.content.replace("&shy;", "");
                        article.content = article.content.replaceAll("[<](/)?div[^>]*[>]", "");
                        article.content = article.content.replaceAll("<img(.*?)src", "<img src");                        
                        //debug ("author is"+article.content);
                        
                        value.image = Text.between("multimedia/image/crop/"+value.hash,"\'",result);
                        if (!Text.empty(value.image))
                        {
                            value.image = "http://www.tabula.ge/multimedia/image/crop/"+value.hash+value.image;
                        }
                        debug ("tabula.image "+value.image);
                        if (!Text.empty(value.image))
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
        //System.out.println ("parser.tabula: "+message);
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
