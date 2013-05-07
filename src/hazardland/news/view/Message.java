package hazardland.news.view;

import java.util.ArrayList;

import hazardland.lib.db.Query;
import hazardland.lib.project.Command;
import hazardland.news.Main;
import hazardland.news.R;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Message implements OnClickListener
{
    LinearLayout view;
    TextView message;
    Button close;
    Button action;
    public Command command;

    public Message (LinearLayout view, Font font, int message, int close, int action)
    {
        this.view = view;
        this.message = (TextView) this.view.findViewById (message);
        this.message.setTypeface(font.face);
        this.close = (Button) this.view.findViewById (close);
        this.close.setTypeface(font.face);
        this.action = (Button) this.view.findViewById (action);
        this.action.setTypeface(font.face);
        this.close.setOnClickListener(this);
        this.action.setOnClickListener(this);
        this.message.setOnClickListener(this);
    }
    
    
    public void set (String text)
    {
        message.setText (text);
    }
    
    public boolean visible ()
    {
        if (view.getVisibility()==View.VISIBLE) 
        {
            return true;
        }
        return false;
    }

    public void show (String text)
    {
        action.setVisibility(View.INVISIBLE);
        message.setText (text);
        view.startAnimation(AnimationUtils.loadAnimation(view.getContext(),R.anim.left));    
        view.setVisibility(View.VISIBLE);
    }
    
    public void command (Command input)
    {
        if (input!=null)
        {
            debug("message entering");
            this.command = input;
            if (command.type.equalsIgnoreCase("update"))
            {
                action.setText ("განახლება");
                action.setVisibility (View.VISIBLE);
            }
            else
            {
                action.setVisibility(View.INVISIBLE);
            }
            message.setText (command.text);
            view.startAnimation(AnimationUtils.loadAnimation(view.getContext(),R.anim.left));    
            view.setVisibility(View.VISIBLE);
            if (command!=null)
            {
                command.view = true;
                Main.database.commands.save (command);
            }
        }
    }
    
    public void hide ()
    {
        view.startAnimation(AnimationUtils.loadAnimation(view.getContext(),R.anim.right));    
        view.setVisibility(View.INVISIBLE);
        
        Query query = new Query (Main.database.commands);
        query.where.query = "commands.view=0";
        query.limit.count = 1;
        ArrayList<Command> commands = Main.database.commands.load(query);
        if (commands!=null && commands.size()>0)
        {
            command (commands.get(0));
        }
        //message.setText ("");
    }
    
    public void debug (String message)
    {
        System.out.println("message: "+message);
    }


    @Override
    public void onClick(View view)
    {
        if (view.getId()==close.getId() || view.getId()==message.getId() )
        {
            hide();
        }
        else if (view.getId()==action.getId())
        {
            if (command!=null)
            {
                if (command.type.equalsIgnoreCase("update"))
                {
                    Intent market = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("market://details?id=hazardland.news"));
                    try
                    {
                        view.getContext().startActivity(market);
                    }
                    catch (Exception e)
                    {
                        
                    }
                }
                hide();
            }
        }
    }

}
