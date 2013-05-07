package hazardland.news;

import hazardland.lib.db.Query;
import hazardland.news.model.Source;
import hazardland.news.view.Adapter;
import hazardland.news.view.Draglist;
import hazardland.news.view.Font;
import hazardland.news.view.Message;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
//import android.widget.Toast;

public class Sources extends ListActivity
{
    public static ArrayList <Source> sources;
    public Database database;
    public Main main;
    public Adapter adapter;
    public Draglist list;
    public Font font;
    public Message message;
   
    private Draglist.DropListener listener =
    new Draglist.DropListener() 
    {
        public void drop(int from, int to) 
        {
            debug ("dragging from:"+from+" to:"+to);

            //Assuming that item is moved up the list
            int direction = -1;
            int start = from;
            int end = to;

            //For instance where the item is dragged down the list
            if (from<to) 
            {
                direction = 1;
            }

            Source target = sources.get (from);
            //Source buffer = null;

            for(int position=start;position!=end;position=position+direction)
            {
                //sArray[position] = sArray[position+direction];
                sources.set (position, sources.get(position+direction));
                
                //buffer = sources.get (position);
                //sources.set (position, sources.get(position+direction));
                //sources.set (position+direction, buffer);                
            }

            //sArray[to] = target;
            sources.set (to, target);

            //System.out.println("Changed array is:"+Arrays.toString(sArray));
            adapter.notifyDataSetChanged();
        }
    };    
    public void onCreate (Bundle state)
    {
        super.onCreate(state);
        setContentView(R.layout.sources);
        font = new Font(this);
        main = (Main) getApplicationContext();
        database = Main.database;
        list = (Draglist) getListView();
        Query query = new Query(database.sources);
        query.order = database.sources.query.order;
        sources = database.sources.load(query);
        adapter = new Adapter(this, sources);
        if (list==null)
        {
            debug ("list is null");
        }
        list.setAdapter (adapter);
        list.setDropListener(listener);
        list.setLongClickable(true);
        list.setOnItemLongClickListener(new OnItemLongClickListener() 
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) 
            {
                Source source = sources.get(position);
                if (source.enabled)
                {
                    source.enabled = false;
                    //Toast.makeText(Sources.this, source.caption+" გაპასიურდა", Toast.LENGTH_SHORT).show();
                    //ImageView.class.cast(view.findViewById(R.id.source_icon)).setImageResource(R.drawable.source_disabled);
                }
                else
                {
                    source.enabled = true;
                    //Toast.makeText(Sources.this, source.caption+" გააქტიურდა", Toast.LENGTH_SHORT).show();
                    //ImageView.class.cast(view.findViewById(R.id.source_icon)).setImageResource(R.drawable.source_enabled);
                }
                adapter.notifyDataSetChanged();
                return true;
            }
        });
        registerForContextMenu(list);
        TextView.class.cast(findViewById(R.id.list_title)).setTypeface(font.face);
        //TextView.class.cast(findViewById(R.id.sources_message_content)).setTypeface(font.face);
        message = new Message ((LinearLayout)findViewById(R.id.sources_message), font, R.id.sources_message_content, R.id.sources_message_close, R.id.sources_message_action);
        if (Main.setting.channel)
        {
            message.show ("არხის ჩასართველად ან ამოსართველად გააჩერეთ თითი შესაბამისი არხის სათაურზე და დაელოდეთ არხის ზოლის ფერის შეცვლას.\n\nარხის პოზიციის შესაცვლელად გადაადგილეთ არხი ზემოთ ან ქვემოთ, არხის ფერად ზოლზე თითის დაჭერით.");
            Main.setting.channel = false;
            Main.database.settings.save (Main.setting);
        }
                
    }
    public void refresh ()
    {
        adapter.notifyDataSetChanged ();
    }
    public void debug (String message)
    {
        if (Config.debug) System.out.println("sources: "+message);
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) 
    {
        if (keyCode == KeyEvent.KEYCODE_BACK) 
        {
            if (message.visible())
            {
                message.hide();
                return true;
            }
            else
            {
                for (int position=0; position<sources.size(); position++)
                {
                    sources.get(position).order = position+1;
                    database.sources.save(sources.get(position));
                }
                sources.clear();
                sources = null;            
                finish();
                startActivity(new Intent(this, Board.class));
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) 
    {
//        String selection = sources.get(position).caption;
//        Toast.makeText(this, selection, Toast.LENGTH_SHORT).show();
        //Toast.makeText(context, text, duration);
    }
}
