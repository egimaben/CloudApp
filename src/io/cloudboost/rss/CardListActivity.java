package io.cloudboost.rss;

import io.cloudboost.CloudException;
import io.cloudboost.CloudObject;
import io.cloudboost.CloudObjectArrayCallback;
import io.cloudboost.CloudObjectCallback;
import io.cloudboost.CloudQuery;
import io.cloudboost.CloudSearch;
import io.cloudboost.SearchQuery;
import io.cloudboost.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;







import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class CardListActivity extends Activity implements OnClickListener {

	private static final String TAG = "CardListActivity";
	private ListView listView;
	private ProgressDialog pDialog;
	private static String TAG_TITLE = "title";
	private static String TAG_LINK = "link";
	private static String TAG_DESRIPTION = "description";
	private static String TAG_PUB_DATE = "pubDate";
	private static String TABLE_NAME = "rssfeeds";
	ArrayList<CloudObject> rssItemList = new ArrayList<>();
	ArrayList<HashMap<String, String>> rssItemMaps = new ArrayList<>();

	// DbHelper db;
	AppDataz data;
	// ImageLoader loader;
	// Cursor cursor;
	Button nameSearch;
	EditText searchBox;
	List<RSSItem> rssItems = new ArrayList<>();
	RSSParser rssParser = new RSSParser();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		data = (AppDataz) getApplication();
		// loader=data.getImgLoader();
		// data.setCardList(this);
		// db = data.getDb();

		setContentView(R.layout.listview);
		listView = (ListView) findViewById(R.id.card_listView);
		nameSearch = (Button) findViewById(R.id.name_search);
		nameSearch.setOnClickListener(this);
		searchBox = (EditText) findViewById(R.id.search_word);

		listView.addHeaderView(new View(this));
		listView.addFooterView(new View(this));
		new loadRSSFeedItems().execute("static_url");
	}

	@Override
	protected void onResume() {
		super.onResume();
		// new loadRSSFeedItems().execute("static_url");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	/**
	 * Background Async Task to get RSS Feed Items data from URL
	 * */
	public void runProgressGuy(String msg) {
		pDialog = new ProgressDialog(CardListActivity.this);
		pDialog.setMessage(msg);
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);
		pDialog.show();
	}

	public void stopProgressGuy() {
		pDialog.dismiss();
	}

	public void populateMaps(CloudObject[] rssItemList) {
		if(rssItemList.length==0){
			say("0 records found");
			return;
			}
		rssItemMaps.clear();
		for (CloudObject obj : rssItemList) {
			HashMap<String, String> map = new HashMap<>();
			map.put(TAG_TITLE, obj.getString(TAG_TITLE));
			map.put(TAG_LINK, obj.getString(TAG_LINK));

			map.put(TAG_PUB_DATE, obj.getString(TAG_PUB_DATE));

			map.put(TAG_DESRIPTION, obj.getString(TAG_DESRIPTION));
			rssItemMaps.add(map);
		}
	}

	public void say(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}
	class SearchCloudBoost extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			runProgressGuy("Searching CloudBoost...");
		}

		/**
		 * getting all recent articles and showing them in listview
		 * */
		@Override
		protected String doInBackground(String... args) {
			String searchWord=args[0];
			SearchQuery searchObject=new SearchQuery().wildcard("title", "*"+searchWord+"*", null);
			
			CloudSearch search=new CloudSearch(TABLE_NAME, searchObject, null);
			search.orderByDesc("title");
			search.setLimit(10);
			

			try {
				
				search.search(new CloudObjectArrayCallback() {
					
					@Override
					public void done(final CloudObject[] x, final CloudException t) throws CloudException {
						runOnUiThread(new Runnable() {
							public void run() {
								stopProgressGuy();
								if(x==null){
									say("Error: "+t.getMessage());
								}
								else {
									
									updateList(x);
								}
							}
						});
						
					
						
					}
				});
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CloudException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String args) {
			// dismiss the dialog after getting all products
			// stopProgressGuy();
			pDialog.dismiss();
		}
	}
	class loadRSSFeedItems extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			runProgressGuy("Loading feeds...");
		}

		/**
		 * getting all recent articles and showing them in listview
		 * */
		@Override
		protected String doInBackground(String... args) {
			// rss link url
			// list of rss items
			rssItems = rssParser
					.getRSSFeedItems("http://news.ycombinator.com/rss");
			runOnUiThread(new Runnable() {
				public void run() {
					pDialog.setMessage("Populating CloudObjects...");
				}
			});
			// looping through each item
			for (RSSItem item : rssItems) {
				// creating new HashMap
				CloudObject obj = new CloudObject(TABLE_NAME);
				// adding each child node to HashMap key => value
				try {
					obj.set(TAG_TITLE, item.getTitle());

					obj.set(TAG_LINK, item.getLink());
					obj.set(TAG_PUB_DATE, item.getPubdate()); // If you want
																// parse the
																// date
					String description = item.getDescription();
					// taking only 200 chars from description
					if (description.length() > 100) {
						description = description.substring(0, 97) + "..";
					}
					obj.set(TAG_DESRIPTION, description);

				} catch (CloudException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// adding HashList to ArrayList
				rssItemList.add(obj);
			}
			CloudObject cbobj = new CloudObject(TABLE_NAME);
			runOnUiThread(new Runnable() {
				public void run() {
					pDialog.setMessage("Backing up CloudObjects...");
				}
			});
			
			try {

				cbobj.saveAll(rssItemList.toArray(new CloudObject[0]),
						new CloudObjectArrayCallback() {

							@Override
							public void done(final CloudObject[] x,
									final CloudException t)
									throws CloudException {
								if (t != null) {
									runOnUiThread(new Runnable() {
										public void run() {
											say("Error:" + t.getMessage());
										}
									});
								} else {
									runOnUiThread(new Runnable() {
										public void run() {
											pDialog.setMessage("Loading CloudData...");
											/**
											 * Updating parsed items into
											 * listview
											 * */
											updateList(x);
										
										}
									});
								}

							}
						});
			} catch (CloudException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String args) {
			// dismiss the dialog after getting all products
			// stopProgressGuy();
			pDialog.dismiss();
		}
	}
	public void updateList(CloudObject[] x){
		populateMaps(x);
		ListAdapter adapter = new SimpleAdapter(
				CardListActivity.this,
				rssItemMaps,
				R.layout.rss_item_list_row,getRowFieldNames(),getRowFields());

		// updating listview
		listView.setAdapter(adapter);
	}

	public String[] getRowFieldNames(){
		return new String[] { TAG_LINK,
				TAG_TITLE,
				TAG_PUB_DATE,
				TAG_DESRIPTION };
	}
	public int[] getRowFields(){
		return new int[] { R.id.page_url,
				R.id.title,
				R.id.pub_date,
				R.id.link };
	}
	@Override
	public void onClick(View arg0) {
		String searchword = String.valueOf(searchBox.getText());
		new SearchCloudBoost().execute(searchword);
		}

	
}
