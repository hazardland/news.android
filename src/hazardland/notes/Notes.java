package hazardland.notes;

import java.util.ArrayList;

import hazardland.notes.lib.Adapter;
import hazardland.notes.lib.Note;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ListView;

public class Notes extends Activity
{
	public ListView notesList;
	ArrayList <Note> notes;
	Adapter notesAdapter;	

	@Override
	public void onCreate (Bundle savedInstanceState)
	{
		super.onCreate (savedInstanceState);
		setContentView (R.layout.main);
		notesList = (ListView) findViewById (R.id.listNotes);
		notes = new ArrayList <Note> ();
		notesAdapter = new Adapter (Notes.this, R.layout.item, notes);
		notesList.setAdapter (notesAdapter);
		
		add (new Note (1, "dadada"));
		add (new Note (2, "dadada"));
		add (new Note (3, "dadada"));
		
		refresh ();
	}

	@Override
	public boolean onCreateOptionsMenu (Menu menu)
	{
		getMenuInflater().inflate (R.menu.main, menu);
		return true;
	}
	
	public void refresh ()
	{
		notesAdapter.notifyDataSetChanged ();
	}
	
	public void add (Note note)
	{
		notes.add (note);
	}

}
