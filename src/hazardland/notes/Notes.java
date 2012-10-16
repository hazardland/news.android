package hazardland.notes;

import java.util.ArrayList;

import hazardland.lib.Files;
import hazardland.notes.lib.Adapter;
import hazardland.notes.lib.Note;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
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
		setContentView (R.layout.notes);
		notesList = (ListView) findViewById (R.id.listNotes);
		notes = new ArrayList <Note> ();
		notesAdapter = new Adapter (Notes.this, R.layout.item, notes);
		notesList.setAdapter (notesAdapter);
		registerForContextMenu(notesList);
		
		String[] files = Files.list ("/sdcard/biohazard/oneonote/");
		
		for (String file : files)
		{
			add (new Note(-1, file));
		}
		
//		add (new Note (1, "dadada"));
//		add (new Note (2, "dadada"));
//		add (new Note (3, "dadada"));
		
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
		if (note.id==-1)
		{
			note.id = notes.size ();
		}
		notes.add (note);
	}

	@Override
	public void onCreateContextMenu (ContextMenu menu, View view, ContextMenuInfo menuInfo) 
	{
	    super.onCreateContextMenu (menu, view, menuInfo);
	    //menu.setHeaderTitle (getString(R.string.menu_context_title));
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.view, menu);
	}

	@Override
	public boolean onContextItemSelected (MenuItem item) 
	{
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	    switch (item.getItemId()) 
	    {
		    	case R.id.view_delete:
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
					 
					// set title
					//alertDialogBuilder.setTitle("Your Title");
		 
					// set dialog message
					alertDialogBuilder
						.setMessage("Delete note "+notes.get((int) info.id).name+"?")
						.setCancelable(false)
						.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
							}
						  })
						.setNegativeButton("No",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								dialog.cancel();
							}
						});
		 
						// create alert dialog
						AlertDialog alertDialog = alertDialogBuilder.create();
		 
						// show it
						alertDialog.show();
					
		
		
	    		return true;
		    	default:
		        return super.onContextItemSelected(item);
	    }
	}
	
	
}
