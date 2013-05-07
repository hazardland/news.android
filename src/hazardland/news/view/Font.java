package hazardland.news.view;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;

public class Font
{
    public Typeface face;
    public boolean set;
    public Font (Context context)
    {
        //face = Typeface.createFromAsset(context.getAssets(), "fonts/bpg_arial.ttf");
        if (Build.VERSION.SDK_INT<14)
        {
            set = true;
            face = Typeface.createFromAsset(context.getAssets(), "fonts/bpg_arial.ttf");
        }
        else
        {
            set = false;
        }
    }
}
