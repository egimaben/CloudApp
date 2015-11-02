package io.cloudboost.rss;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
 
public class DbHelper extends SQLiteOpenHelper {
 
    /**
	 * 
	 */

	// All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 20;
 
    // Database Name
    private static final String DATABASE_NAME = "tweetsManager";
 
    // Contacts table name
    private static final String TABLE_FEEDS = "rssfeeds";
 
    // Contacts Table Columns names
    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_IMG = "image_url";
    public static final String KEY_TWEET="tweet";
    
    public static final String KEY_TITLE = "title";
    public static final String KEY_LINK = "link";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_PUBDATE="pubdate";
    Context context;
    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
    }
 
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
    	Log.d("DB", "creating database=======");
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_FEEDS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_IMG + " TEXT," + KEY_TWEET+" TEXT)";
        db.execSQL(CREATE_CONTACTS_TABLE);
        
        
    	Log.d("DB", "finished creating database=======");

    }
    public Cursor search(String key,String column){
    	Log.d("DB", "searcing for key and col======="+key+" and "+column);
    	String query="SELECT * from "+TABLE_FEEDS+" where "+column+" like '%"+key+"%'";
    	Log.d("DB", "search query="+query);
    	SQLiteDatabase db = this.getReadableDatabase();
    	
    	Cursor cursor = db.query(TABLE_FEEDS, new String[] {}, column + " like ?",
                new String[] { "%"+key+"%" }, null, null, null, null);
    	Log.d("DB", "cursor size="+cursor.getCount());
    	return cursor;


    	
    }
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FEEDS);
 
        // Create tables again
        onCreate(db);
    }
 
    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */
 
    // Adding new tweet
    void addTweet(Tweet tweet) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, tweet.getName()); // Tweet Name
        values.put(KEY_ID, tweet.getId()); // Tweet Phone
        values.put(KEY_TWEET, tweet.getTweet());
        values.put(KEY_IMG, tweet.getImgUrl());
 
        // Inserting Row
//        db.insert(TABLE_FEEDS, null, values);
		try {
			db.insertWithOnConflict(TABLE_FEEDS, null, values,
					SQLiteDatabase.CONFLICT_IGNORE); //
		} finally {
			db.close(); //
		}
//        db.close(); // Closing database connection
    }
 
    // Getting single tweet
    Tweet getTweet(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
 
        Cursor cursor = db.query(TABLE_FEEDS, new String[] { KEY_ID,
                KEY_NAME, KEY_IMG,KEY_TWEET }, KEY_ID + "=?",
                new String[] { id }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
 
        Tweet tweet = new Tweet(cursor.getString(1),
                cursor.getString(0), cursor.getString(2), cursor.getString(3));
        // return tweet
        return tweet;
    }
     public Cursor getAllCursor(){
         // Select All Query
         String selectQuery = "SELECT  * FROM " + TABLE_FEEDS;
  
         SQLiteDatabase db = this.getReadableDatabase();
         Cursor cursor = db.rawQuery(selectQuery, null);
//         cursor.close();
         return cursor;
     }
    // Getting All Contacts
    public List<Tweet> getAllContacts() {
        List<Tweet> contactList = new ArrayList<Tweet>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_FEEDS;
 
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Tweet tweet = new Tweet();
                tweet.setId(cursor.getString(0));
                tweet.setName(cursor.getString(1));
                tweet.setTweet(cursor.getString(3));
                tweet.setImgUrl(cursor.getString(2));

                // Adding tweet to list
                contactList.add(tweet);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // return tweet list
        return contactList;
    }
    public void addMultiple(List<Tweet> tweets){
    	for(Tweet t:tweets){
    		addTweet(t);
    	}
    }
    public void deleteAll(){
    	String query="DELETE FROM "+TABLE_FEEDS;
        SQLiteDatabase db = this.getWritableDatabase();

    	db.execSQL(query);
    }
    public void replaceAll(List<Tweet> tweets){
    	deleteAll();

    	addMultiple(tweets);
    }
    // Updating single tweet
    public int updateContact(Tweet tweet) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, tweet.getName()); // Tweet Name
        values.put(KEY_ID, tweet.getId()); // Tweet Phone
        values.put(KEY_TWEET, tweet.getTweet());
        values.put(KEY_IMG, tweet.getImgUrl());
 
        // updating row
        return db.update(TABLE_FEEDS, values, KEY_ID + " = ?",
                new String[] { tweet.getId() });
    }
 
    // Deleting single tweet
    public void deleteContact(Tweet tweet) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FEEDS, KEY_ID + " = ?",
                new String[] {tweet.getId() });
        db.close();
    }
 
 
    // Getting contacts Count
    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_FEEDS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
 
        // return count
        return cursor.getCount();
    }
 
}
