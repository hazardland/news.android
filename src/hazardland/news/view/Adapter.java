package hazardland.news.view;

import hazardland.news.R;
import hazardland.news.Sources;
import hazardland.news.model.Source;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Adapter extends ArrayAdapter <Source>
{
	public Adapter(Context context, ArrayList <Source> notes)
	{
		super (context, R.layout.source, notes);
	}

	@Override
	public View getView (int position, View input, ViewGroup parent)
	{
		LinearLayout item;
		//Source source = getItem (position);
		
		@SuppressWarnings("static-access")
        Source source = Sources.class.cast (getContext()).sources.get(position);

		if (input == null)
		{
			item = new LinearLayout (getContext ());
			LayoutInflater inflater;
			inflater = (LayoutInflater) getContext ().getSystemService (Context.LAYOUT_INFLATER_SERVICE);
			inflater.inflate (R.layout.source, item, true);
			TextView.class.cast(item.findViewById(R.id.source_caption)).setTypeface(Sources.class.cast (getContext()).font.face);
			TextView.class.cast(item.findViewById(R.id.source_description)).setTypeface(Sources.class.cast (getContext()).font.face);
		}
		else
		{
			item = (LinearLayout) input;
		}

        TextView.class.cast(item.findViewById(R.id.source_caption)).setText(source.caption);
        TextView.class.cast(item.findViewById(R.id.source_description)).setText(source.description);
        //Imag.class.cast(item.findViewById(R.id.source_description)).setText(source.description);
        if (source.enabled)
        {
            //ImageView.class.cast(item.findViewById(R.id.source_icon)).setImageResource(R.drawable.source_enabled);
            ImageView.class.cast(item.findViewById(R.id.source_icon)).setBackgroundColor(0xFF3399CC);
        }
        else
        {
            //ImageView.class.cast(item.findViewById(R.id.source_icon)).setImageResource(R.drawable.source_disabled);
            //ImageView.class.cast(item.findViewById(R.id.source_icon)).setBackgroundColor(0xFFFF9900);            
            ImageView.class.cast(item.findViewById(R.id.source_icon)).setBackgroundColor(0xFF555555);
        }
		
		return item;
	}

}
