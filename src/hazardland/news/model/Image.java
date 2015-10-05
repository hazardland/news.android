package hazardland.news.model;

import android.graphics.Bitmap;
import hazardland.lib.db.Entity;

public class Image extends Entity
{
	//public Integer id;
	public hazardland.lib.db.Image data = new hazardland.lib.db.Image();
	public String name;
	public String link;
	public Image ()
	{
	    
	}
	public Image (Bitmap bitmap)
	{
	    data.value = bitmap;
	}
}
