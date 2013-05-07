package hazardland.news.model;

import hazardland.lib.db.Entity;

public class Article extends Entity
{
	//public Integer id;
	public Topic topic;
	public String content;
	public String link;
	public Article ()
	{
		
	}
	public Article (String link, String content)
	{
	    this.link = link;
	    this.content = content;
	}
}
