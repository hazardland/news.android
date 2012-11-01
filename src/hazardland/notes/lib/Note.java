package hazardland.notes.lib;

public class Note
{
	public int id;
	public String name;
	public String date;
	public int color;
	public int image;
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
