package hazardland.notes.lib;

import hazardland.lib.db.Entity;

public class Note extends Entity
{
	public Integer id;
	public String name;
	public String date;
	public Integer color;
	public Integer image;
	public Note ()
	{
		
	}
	public Note (String name)
	{
		this.name = name;
	}
	public Note (int id, String name, String date, int color, int image)
	{
		this.id = id;
		this.name = name;
		this.date = date;
		this.color = color;
		this.image = image;
		
	}
	public String text ()
	{
		return "";
	}
}
