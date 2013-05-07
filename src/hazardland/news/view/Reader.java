package hazardland.news.view;

import hazardland.lib.tool.Text;
import hazardland.news.Board;
import hazardland.news.Config;
import hazardland.news.R;
import hazardland.news.model.Topic;
import android.content.Context;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.LinearLayout;

public class Reader
{
    Board board;
    LinearLayout holder;
    WebView browser;
    public Topic topic;
//    LinearLayout header;
//    TextView title;
//    TextView source;
    public Reader (Context context)
    {
        board = (Board) context;
        holder = (LinearLayout) board.findViewById(R.id.article_view);
        browser = (WebView) board.findViewById(R.id.article_browser);
        //browser.ad
        
//        header = new LinearLayout (board);
//        LayoutInflater inflater;
//        inflater = (LayoutInflater) board.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
//        inflater.inflate (R.layout.article, header, true);
//        source = (TextView) header.findViewById (R.id.article_source);
//        title = (TextView) header.findViewById (R.id.article_title);
//        browser.addView (header, 0);
    }
    
    public void show (Topic topic)
    {
        this.topic = topic;
//        source.setText (topic.source.caption);
//        title.setText (topic.title);
//        int height = header.getHeight();
        //article_browser.loadData("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"+topic.article().content, "text/html", "UTF-8");
        String html =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML Basic 1.1//EN\"\n\"http://www.w3.org/TR/xhtml-basic/xhtml-basic11.dtd\">"
                +"<html><head>"
                +"<style type=\"text/css\">"
                    +"body{margin:0px;padding:0px;background-color:black}";
        
        if (board.font.set)
        {
            html+= "@font-face {font-family:georgian; src:url(\"file:///android_asset/fonts/bpg_arial.ttf\")}" 
                  +"body {font-family:georgian}";

        }
        
             html += "img{float:left !important;margin-right:"+dp(3)+";margin-bottom:"+dp(2)+";margin-top:"+dp(2)+";max-height:"+(Config.fit-200)+"px !important; max-width:"+(Config.fit-200)+"px !important;border:0px;}"
                    +"a{text-decoration:none;color:#555;}"
                    +".header,.footer{height:auto;background-color:black;width:100%;padding-bottom:"+dp(6)+";overflow:hidden}"
                    +".source{display:block;color:white;font-weight:bold;padding:0px "+dp(6)+" "+dp(6)+" "+dp(6)+";padding-bottom:0px;}"
                    +".topic,.author,.date,.link{display:block;color:#555;font-weight:bold;padding:"+dp(6)+" "+dp(6)+" 0px "+dp(6)+";}"
                    +".link a {text-decoration:none;color:#555}"
                    +".article{background-color:white;padding:"+dp(7)+";width:auto !important;overflow:hidden !important;}"
                +"</style>"
                +"</head><body>"
                +"<div class='header'>"
                    +"<span class='source'>"+topic.source.caption+"</span>"
                    +"<span class='topic'>"+topic.title+"</span>";
             
             //debug (html);
        
        if (!Text.empty(topic.author))
        {
            html += "<span class='author'>"+topic.author+"</span>";
        }
        
        if (!topic.date.empty())
        {
            html += "<span class='date'>"+Text.before(" ",topic.date.get())+"</span>";
        }
        
        html +=                     
                "</div>"
                +"<div class='article'>"
                +topic.article().content //.replaceAll ("<div class='embedded_content_object'\\>(.*?)</div\\></div\\></div\\>"," ")
            +"</div>"
            +"<div class='footer'>"
                +"<span class='link'><a href='"+topic.article().link+"'>სტატიის წაკითხვა საიტზე</a></span>"
            +"</div>"
        +"</body></html>";

        
        browser.loadDataWithBaseURL (null ,html, "text/html", "UTF-8", null);
        board.article_view.startAnimation(AnimationUtils.loadAnimation(board,R.anim.left));
        board.article_view.setVisibility(View.VISIBLE);
        //board.board_bar.setVisibility(View.INVISIBLE);
        
        //debug (topic.article().content);
    }
    
    public void hide ()
    {
        board.article_view.startAnimation(AnimationUtils.loadAnimation(board,R.anim.right));    
        //board.board_bar.setVisibility(View.VISIBLE);
        board.article_view.setVisibility(View.INVISIBLE);
        browser.loadData (null, null, null);
        topic.article (false);
    }
    
    private String dp (float dp)
    {
        return (int)(dp * board.getResources().getDisplayMetrics().density + 0.5f)+"px";
    }

//    private int px (float dp)
//    {
//        return (int) (dp * board.getResources().getDisplayMetrics().density + 0.5f);
//    }

    public void debug (String message)
    {
        System.out.println("reader: "+message);
    }

}
