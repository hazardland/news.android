package hazardland.news;

public class Server extends hazardland.lib.project.Server
{
    public Server ()
    {
        super (Main.database, Config.server, Config.name, Config.release, Config.version);
    }
}
