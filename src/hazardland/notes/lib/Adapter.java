package hazardland.notes.lib;

import hazardland.notes.R;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Adapter extends ArrayAdapter <Note>
{
	int listNotesItem;

	public Adapter(Context context, int notesListItem, ArrayList <Note> notes)
	{
		super (context, notesListItem, notes);
		this.listNotesItem = notesListItem;
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView (int position, View convertView, ViewGroup parent)
	{
		LinearLayout notesLayout;
		Note note = getItem (position);

		if (convertView == null)
		{
			notesLayout = new LinearLayout (getContext ());
			LayoutInflater notesInflater;
			notesInflater = (LayoutInflater) getContext ().getSystemService (Context.LAYOUT_INFLATER_SERVICE);
			notesInflater.inflate (listNotesItem, notesLayout, true);
		}
		else
		{
			notesLayout = (LinearLayout) convertView;
		}

		TextView notesListItemCaption = (TextView) notesLayout.findViewById (R.id.notesListItemCaption);
		TextView notesListItemComment = (TextView) notesLayout.findViewById (R.id.notesListItemComment);

		notesListItemCaption.setText (note.name);
		notesListItemComment.setText ("adada");

		return notesLayout;
	}

}
