package io.cloudboost.rss;

import io.cloudboost.CloudException;
import io.cloudboost.CloudObject;
import io.cloudboost.CloudObjectArrayCallback;
import io.cloudboost.CloudQuery;
import io.cloudboost.CloudTable;
import io.cloudboost.CloudTableCallback;
import io.cloudboost.Column;
import io.cloudboost.Column.DataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


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
	Button tweetSearch;
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
		// tweetSearch = (Button) findViewById(R.id.tweet_search);
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

	class initCloudApp extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			runProgressGuy("Initialising cloud app...");
		}

		/**
		 * getting all recent articles and showing them in listview
		 * */
		@Override
		protected String doInBackground(String... args) {

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

	public void populateMaps() {
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
							public void done(CloudObject[] x,
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
											populateMaps();
											ListAdapter adapter = new SimpleAdapter(
													CardListActivity.this,
													rssItemMaps,
													R.layout.rss_item_list_row,
													new String[] { TAG_LINK,
															TAG_TITLE,
															TAG_PUB_DATE,
															TAG_DESRIPTION },
													new int[] { R.id.page_url,
															R.id.title,
															R.id.pub_date,
															R.id.link });

											// updating listview
											listView.setAdapter(adapter);
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

	@Override
	public void onClick(View arg0) {
		String searchword = String.valueOf(searchBox.getText());
		if (searchword == null) {
			Toast.makeText(this, "no search params", Toast.LENGTH_SHORT);
			return;
		}
		Cursor cursor;
		int id = arg0.getId();
		switch (id) {
		case R.id.name_search:
			Toast.makeText(this, "searching users", Toast.LENGTH_SHORT).show();
			;
			// cursor = db.search(searchword, DbHelper.KEY_NAME);
			// startManagingCursor(cursor);
			// adapter = new TimelineAdapter(this, cursor);
			// listView.setAdapter(adapter);

			break;
		// case R.id.tweet_search:
		// Toast.makeText(this, "searching tweets", Toast.LENGTH_SHORT).show();
		// cursor = db.search(searchword, DbHelper.KEY_TWEET);
		// startManagingCursor(cursor);
		// adapter = new TimelineAdapter(this, cursor);
		// listView.setAdapter(adapter);

		// break;
		default:
			break;
		}

	}
}
