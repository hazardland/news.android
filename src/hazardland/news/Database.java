package hazardland.news;

import hazardland.lib.db.Method;
import hazardland.lib.db.Order;
import hazardland.lib.db.Table;
import hazardland.lib.project.Command;
import hazardland.news.model.Article;
import hazardland.news.model.Category;
import hazardland.news.model.Image;
import hazardland.news.model.Setting;
import hazardland.news.model.Source;
import hazardland.news.model.Topic;
import android.content.Context;
import android.graphics.Bitmap.CompressFormat;

public class Database extends hazardland.lib.db.Database
{
    public Table<Source> sources;
    public Table<Topic> topics;
    public Table<Article> articles;
    public Table<Image> images;
    public Table<Category> categorys;
    public Table<Setting> settings;
    public Table<Command> commands;
    @SuppressWarnings("unchecked")
    public Database(Context context, String name, int version)
    {
        super(context, name, version);
        
        add (Source.class, new Table<Source>(this, Source.class));
        add (Topic.class, new Table<Topic>(this, Topic.class));
        add (Article.class, new Table<Article>(this, Article.class));
        add (Image.class, new Table<Image>(this, Image.class));
        add (Category.class, new Table<Category>(this, Category.class));
        add (Setting.class, new Table<Setting>(this, Setting.class));
        add (Command.class, new Table<Command>(this, Command.class));
        sources = table(Source.class);
        topics = table(Topic.class);
        images = table(Image.class);
        articles = table(Article.class);
        categorys = table(Category.class);
        settings = table(Setting.class);
        commands = table(Command.class);
        
        sources.query.order.add (new Order(sources, sources.field("order"), new Method(Method.ASC)));
        sources.query.order.add (new Order(sources, sources.field("id"), new Method(Method.ASC)));
        sources.query.where.query = "sources.enabled=1";
        
        topics.query.order.method.name = Method.DESC;
        
        images.field("data").config.image.read.inScaled = true;
        //images.field("data").config.image.read.inSampleSize = 2;
        images.field("data").config.image.read.outHeight = 120;
        images.field("data").config.image.read.outWidth = 120;
        
        images.field("data").config.image.write.format = CompressFormat.JPEG;
        images.field("data").config.image.write.quality = 80;
        
        if (sources.count()==0)
        {
            sources.save (new Source("ipn_ge_1", "ინტერპრესნიუსი", "http://interpressnews.ge", "სიახლეების სააგენტო"));
            sources.save (new Source("radiotavisufbleba_ge", "რადიო თავისუფლება", "http://radiotavisupleba.ge", "სიახლეების სააგენტო"));
            sources.save (new Source("liberali_ge", "ლიბერალი", "http://www.liberali.ge", "ჟურნალი"));
            sources.save (new Source("tabula_ge", "ტაბულა", "http://tabula.ge", "ჟურნალი"));
            sources.save (new Source("marao_ambebi_ge", "მარაო", "http://marao.ambebi.ge", "marao.ambebi.ge"));
            sources.save (new Source("navigator_ge", "ნავიგატორი", "http://navigator.ge", "ტექნოლოგიური სიახლეები"));
            sources.save (new Source("forbesgeorgia_ge", "ფორბსი", "http://forbesgeorgia.ge", "ჟურნალი"));
//            sources.save (new Source("ipn_ge_6", "ინტერპრესნიუსი 8", "http://interpressnews.ge", "სიახლეების სააგენტო"));
//            sources.save (new Source("ipn_ge_7", "ინტერპრესნიუსი 9", "http://interpressnews.ge", "სიახლეების სააგენტო"));
//            sources.save (new Source("ipn_ge_8", "ინტერპრესნიუსი 10", "http://interpressnews.ge", "სიახლეების სააგენტო"));
            debug ("populating sources");
            //sources.save (new Source("jobs_ge", "Jobs.ge"));
            
        }
        else
        {
            debug ("source count is "+sources.count());
        }
        if (settings.count()==0)
        {
            Main.setting = new Setting();
            settings.save (Main.setting);
        }
        else
        {
            Main.setting = settings.load(1);
        }
    }

}
