package hazardland.notes.db;

import java.util.ArrayList;

import hazardland.notes.lib.Note;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Notes extends SQLiteOpenHelper 
{
	
	private static final int VERSION = 1;
	private static final String DATABASE = "notes";
	private static final String TABLE = "notes";
	private static final String FIELD_ID = "_id";
	private static final String FIELD_NAME = "name";
	private static final String FIELD_DATE = "date";
	private static final String FIELD_COLOR = "color";
	private static final String FIELD_IMAGE = "image";
	
	private static final String CREATE = "create table " + TABLE + "(" 
			  + FIELD_ID + " integer primary key autoincrement, " 
			  + FIELD_NAME + " text not null,"
			  + FIELD_DATE + " text not null,"
			  + FIELD_COLOR + " int,"
			  + FIELD_IMAGE + " int"
			  + ");";

	public Notes (Context context) 
	{
		super (context, DATABASE, null, VERSION);
	}

	@Override
	public void onCreate (SQLiteDatabase database) 
	{
		database.execSQL (CREATE);
	}

	@Override
	public void onUpgrade (SQLiteDatabase database, int from, int to) 
	{
		 database.execSQL ("DROP TABLE IF EXISTS " + TABLE);
	}
	
	public Note load (int id)
	{
		SQLiteDatabase database = this.getReadableDatabase();
	    Cursor cursor = database.query (TABLE, 
	    								new String[] { FIELD_ID, FIELD_NAME, FIELD_DATE, FIELD_COLOR, FIELD_IMAGE },
	    								FIELD_ID + "=?",
	    								new String[] { String.valueOf(id) },
	    								null,
	    								null,
	    								null);
	    
	    if (cursor==null) 
	    {
	    	return null;
	    }
	    
	    cursor.moveToFirst ();
	    
	    return new Note(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), Integer.parseInt(cursor.getString(3)), Integer.parseInt(cursor.getString(4)));
	}
	
	public ArrayList<Note> load ()
	{
		  SQLiteDatabase database = this.getReadableDatabase();
		  Cursor cursor = database.rawQuery ("select * from " + TABLE + " order by " + FIELD_ID + " desc", null);
		  ArrayList<Note> result = new ArrayList<Note>();
		  if (cursor.moveToFirst())
		  {
			  do
			  {
				  result.add (Integer.parseInt(cursor.getString(0)), new Note(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), Integer.parseInt(cursor.getString(3)), Integer.parseInt(cursor.getString(4))));
			  }
			  while (cursor.moveToNext());
		  }
		  database.close();
		  return result;
	}

	
	public boolean save (Note note)
	{
		int result = 0;
		SQLiteDatabase database = this.getWritableDatabase ();
		ContentValues values = new ContentValues ();

		values.put(FIELD_NAME, note.name);
		values.put(FIELD_DATE, note.date);
		values.put(FIELD_COLOR, note.color);
		values.put(FIELD_IMAGE, note.image);
		
		if (note.id==null)
		{
			result = (int) database.insert (TABLE, null, values);
			if (result>0)
			{
				note = load (result);
				database.close();
				return true;
			}
		}
		else
		{
			result = database.update (TABLE, values, FIELD_ID + " = ?", new String[] {String.valueOf(note.id)});
			if (result>0)
			{
				note = load (note.id);
				database.close();
				return true;				
			}
		}
		database.close();
		return false;
	}
	
    public boolean delete (Note note) 
    {
        SQLiteDatabase database = this.getWritableDatabase();
        if (database.delete (TABLE, FIELD_ID + " = ?", new String[] {String.valueOf(note.id)})>0)
        {
        	database.close();
        	return true;
        }
        database.close();
        return false;
    }	

}
