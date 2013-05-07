package hazardland.news.model;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import hazardland.lib.db.Date;
import hazardland.lib.db.Entity;
import hazardland.news.Board;
import hazardland.news.Config;
import hazardland.news.R;

@SuppressWarnings("unchecked")
public class Topic extends Entity
{
    public static final int APPEND = 1;
    public static final int PREPEND = 2;
    //public static final int WIDTH = 150;
    //public static final int HEIGHT = 150;

	public String title;
	public Source source;
	public String hash;
	public Date date = new Date();
	public Image image;
	public String author;
	public Category category;
	private Article article;
	
	public LinearLayout view;
	
	//private boolean flush = true;
	//private boolean display = false;
	public Icon icon;

	public final static int[] colors = new int[] {0xFF3399CC, 0xFFFF9900, 0xFFCC33CC};
    //public final static int[] images = new int[] {R.drawable.i1,R.drawable.i2,R.drawable.i3,R.drawable.i4,R.drawable.i5,R.drawable.i6,R.drawable.i7,R.drawable.i8,R.drawable.i9,R.drawable.i10,R.drawable.i11,R.drawable.i12,R.drawable.i13,R.drawable.i14,R.drawable.i15,R.drawable.i16,R.drawable.i17};
    public final static Random random = new Random();	
	
	public Topic ()
	{

	}
	
	public Topic (Source source, String title, Bitmap bitmap)
	{
	    this.source = source;
		this.title = title;
		this.image = new Image (bitmap);
	}
	
    public void prepend (LinearLayout row)
    {
        render (row, PREPEND);
    }   
	
	
	public void prepend ()
	{
		if (view!=null)
		{
			render (null, PREPEND);
		}
	}

    public void append (LinearLayout row)
    {
        render (row, APPEND);
    }   
    
    
    public void append ()
    {
        if (view!=null)
        {
            render (null, APPEND);
        }
    }
	
	private void render (LinearLayout row, int mode)
	{
	    if (view==null && row==null)
	    {
	        return;
	    }
		boolean add = false;
		if (view == null) 
		{
			add = true;
			view = new LinearLayout(row.getContext());
			LayoutInflater inflater;
			inflater = (LayoutInflater) row.getContext().getSystemService (Context.LAYOUT_INFLATER_SERVICE);
			inflater.inflate (R.layout.cell, (ViewGroup) view, true);
			view.setTag (this);
			view.setOnClickListener ((OnClickListener) row.getContext());
	        TextView.class.cast(view.findViewById(R.id.grid_cell_title)).setText(title);
	        icon = new Icon (ImageView.class.cast(view.findViewById(R.id.grid_cell_image)));
		}
		
		icon.set ();
		
//		if (image!=null && image.data!=null && image.data.value!=null)
//		{
//		    if (!icon.display)
//		    {
//		        icon.view.setImageBitmap(image.data.value);
//		        icon.display = true;
//		    }
//		    
//		    if (icon.flush && Build.VERSION.SDK_INT>=14)
//		    {
//		        icon.view.startAnimation(AnimationUtils.loadAnimation(row.getContext(),android.R.anim.fade_in));
//		        icon.flush = false;
//		    }
//		}
//		else
//		{
//		    icon.view.setBackgroundColor(colors[random.nextInt(colors.length)]);
//		}
		
		if (add)
		{
			debug ("adding "+title);
			TextView.class.cast(view.findViewById(R.id.grid_cell_title)).setTypeface(Board.class.cast(row.getContext()).font.face);			
			if (mode==APPEND)
			{
			    debug ("appending "+title);
			    LinearLayout.class.cast(row.findViewById(R.id.grid_row)).addView(view,LinearLayout.class.cast(row.findViewById(R.id.grid_row)).getChildCount());
			    view.startAnimation(AnimationUtils.loadAnimation(row.getContext(), R.anim.left));
			}
			else
			{
			    debug ("prepending "+title);
			    LinearLayout.class.cast(row.findViewById(R.id.grid_row)).addView(view, 0);
			    view.startAnimation(AnimationUtils.loadAnimation(row.getContext(), android.R.anim.slide_in_left));
			}

		}
	}

	public void debug (String message)
	{
	    if (Config.debug)
	    {
	        System.out.println("topic: "+message);
	    }
	}
	
	public void article (boolean clear)
	{
	    article = null;
	}
	
    public Article article ()
    {
        if (article==null)
        {
            ArrayList<Article> articles = database.table(Article.class).of (this);
            if (articles!=null && articles.size()>0)
            {
                article = articles.get(0);
            }
        }
        return article;
        
    }
    
    public void article (Article article)
    {
        if (article!=null)
        {
            article.topic = this;
            database.table(Article.class).save(article);
            //this.article = article;
        }
    }
    
    public void delete ()
    {
        if (image!=null)
        {
            database.table(Image.class).delete(image);
        }
        if (article()!=null && article().id!=null)
        {
            database.table(Article.class).delete(article);
        }        
        if (id!=null)
        {
            database.table(Topic.class).delete (this);
        }
        if (source!=null)
        {
            source.remove (this);
        }
    }
    
    public class Icon
    {
        public ImageView view;
        public boolean display = false;
        public boolean flush = false;
        public boolean color = false;
        public Icon (ImageView view)
        {
            this.view = view;
        }
        public void set ()
        {
            if (image!=null && image.data!=null && image.data.value!=null)
            {
                if (!icon.display)
                {
                    view.setImageBitmap(image.data.value);
                    icon.display = true;
                }
                
                if (icon.flush && Build.VERSION.SDK_INT>=14)
                {
                    view.startAnimation(AnimationUtils.loadAnimation(view.getContext(),android.R.anim.fade_in));
                    icon.flush = false;
                }
            }
            else if (!color)
            {
                view.setBackgroundColor(colors[random.nextInt(colors.length)]);
                color = true;
            }
        }
    }
}
