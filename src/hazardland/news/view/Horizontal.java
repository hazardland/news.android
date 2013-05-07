package hazardland.news.view;

import hazardland.news.Board;
import hazardland.news.model.Source;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;

public class Horizontal extends HorizontalScrollView
{
    float density;
    Source source;
    Board board;
    boolean touch = false;
    
    
    Integer previousX = null;
    boolean over = false;
    int direction;
    int edge = 135;
    
    public static final int NONE = 0;
    public static final int LEFT = 1;
    public static final int RIGHT = 2;
    public static final int STOP = 3;
    
    public int direction (Integer x, int deltaX)
    {
        if (x==0)
        {
            debug ("scrollX: start ***********");
            previousX = 0;
            return NONE;
        }
        if (previousX==null)
        {
            return NONE;
        }
        if (x+deltaX>=0 && x!=0)
        {
            debug ("scrollX: end ***********");
            previousX = null;
            if (over)
            {
                debug ("scrollX: returning over ***********");
                over = false;
                return STOP;
            }
            return NONE;
        }
        if (x>previousX)
        {
            if (x<-(edge-10)*density)
            {
                debug ("scrollX: setting over ***********");
                over = true;
            }
            previousX = x;
            return RIGHT;
        }
        if (x<previousX)
        {
            previousX = x;
            return LEFT;
        }
        return NONE;
    }

    public Horizontal(Context context, AttributeSet attrs) 
    {
        super(context, attrs);
        setHorizontalFadingEdgeEnabled(false);
        density = context.getResources().getDisplayMetrics().density;
        board = (Board) context;
        setOverScrollMode(View.OVER_SCROLL_ALWAYS);        
    }

    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) 
    { 
        direction = direction (scrollX,deltaX);
        if (direction==LEFT)
        {
            debug ("scrollX: left");
        }
        else if (direction==RIGHT)
        {
            debug ("scrollX: right");
        }
        else if (direction==NONE)
        {
            debug ("scrollX: none");
        }
        else if (direction==STOP)
        {
            debug ("scrollX: stop");
        }
        //This is where the magic happens, we have replaced the incoming maxOverScrollY with our own custom variable mMaxYOverscrollDistance;
        if (scrollX>0 || (!isTouchEvent && direction==LEFT))
        {
            if (getScrollX()>=getChildAt(0).getWidth()-getWidth()+85*density && isTouchEvent)
            {
                debug ("x: "+getScrollX()+" vs width: "+(getChildAt(0).getWidth()-getWidth()));
                if (source==null)
                {
                    source = (Source) getTag();                
                }
                board.next (source);
                return false;
                
                //fullScroll(FOCUS_RIGHT);
                //smoothScrollBy (150, 0);
            }
            return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, (int)(100*density), maxOverScrollY, isTouchEvent);
        }
        if (direction==STOP)
        {
            if (source==null)
            {
                source = (Source) getTag();                
            }
            board.update (source);
            debug ("scroll end");
        }        
        if (previousX==null)
        {
            return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);            
        }
//        if (getScrollX()+deltaX>=0 && scrollX!=0)
//        {
//            if (source==null)
//            {
//                source = (Source) getTag();                
//            }
//            board.update (source);
//            debug ("scroll end");
//        }
        debug ("scrollX: "+scrollX+" deltaX: "+deltaX+" scrollRangeX: "+scrollRangeX);
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, (int)(edge*density), maxOverScrollY, isTouchEvent);        
    }
    public void debug (String message)
    {
        //System.out.println("board:[horizontal] "+message);
    }    
    
    
}
