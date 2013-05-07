package hazardland.news.view;

import hazardland.news.Board;
import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View;
import android.widget.ScrollView;


public class Vertical extends ScrollView 
{
    float density;
    private GestureDetector detector;
    Board board;
    

    Integer previousY = null;
    int direction;
    boolean over = false;
    int edge = 160;
    
    public static final int NONE = 0;
    public static final int UP = 1;
    public static final int DOWN = 2;
    public static final int STOP = 3;
    
    public int direction (Integer y, int deltaY)
    {
        if (y==0)
        {
            debug ("scrollX: start ***********");
            previousY = 0;
            return NONE;
        }
        if (previousY==null)
        {
            return NONE;
        }
        if (y+deltaY>=0 && y!=0)
        {
            debug ("scrollX: end ***********");
            previousY = null;
            if (over)
            {
                debug ("scrollX: returning over ***********");
                over = false;
                return STOP;
            }
            return NONE;
        }
        if (y>previousY)
        {
            if (y<-(edge-10)*density)
            {
                debug ("scrollX: setting over ***********");
                over = true;
            }
            previousY = y;
            return DOWN;
        }
        if (y<previousY)
        {
            previousY = y;
            return UP;
        }
        return NONE;
    }
    
    
    public Vertical(Context context, AttributeSet attrs) 
    {
        super(context, attrs);
        detector = new GestureDetector(new Move());
        setVerticalFadingEdgeEnabled(false);
        density = context.getResources().getDisplayMetrics().density;
        debug ("context is"+context.getClass().getName());
        board = (Board) context;
        setOverScrollMode(View.OVER_SCROLL_ALWAYS);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) 
    {
        return super.onInterceptTouchEvent(event) && detector.onTouchEvent(event);
//        super.onInterceptTouchEvent(event);        
//        return detector.onTouchEvent(event);
    }
    
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) 
    {
        //debug(getScrollY() + " - " + deltaX+" "+deltaY+" "+scrollX+" "+scrollY+" "+scrollRangeX+" "+scrollRangeY+" "+maxOverScrollX+" "+maxOverScrollY);

        direction = direction (scrollY, deltaY);
        
        if (scrollY>0 || (!isTouchEvent && direction==UP))
        {
            return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
        }
        if (direction==STOP)
        {
            board.update ();
        }
        if (previousY==null)
        {
            return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
        }
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, (int)(density*edge), isTouchEvent);
    }

    class Move extends SimpleOnGestureListener 
    {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) 
        {
            //System.out.println("entering onscroll listener with distenceX:"+distanceX+" distanceY:"+distanceY);
            if (Math.abs(distanceX)>Math.abs(distanceY)) 
            {
                //System.out.println("halting vertical scroll distenceX:"+distanceX+" distanceY:"+distanceY);
                return false;
            } 
            return true;
        }
    }
    public void debug (String message)
    {
        //System.out.println("board:[vertical] "+message);
    }    

}
