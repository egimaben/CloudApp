package io.cloudboost.rss;


//import com.egima.androidtest.CardListActivity.TimelineAdapter;

import io.cloudboost.CloudApp;
import android.app.Application;
import android.database.Cursor;
import android.util.Log;

public class AppDataz extends Application {
	private static final String TAG = AppDataz.class.getSimpleName();
	DbHelper db;
	CardListActivity cardList;
	
	ImageLoader imgLoader;
	public CardListActivity getCardList() {
		return cardList;
	}

	public void setCardList(CardListActivity cardList) {
		this.cardList = cardList;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.i(TAG, "onCreated");
		initClient();
		db=new DbHelper(this);
		
		imgLoader=new ImageLoader(this);

	}
	public void initClient(){
		CloudApp.init("egimabengitest", "yiBh75txY35CB+LSb/1XLQ==");
	}
	public void initMaster(){
		CloudApp.init("egimabengitest", "63Ei0ghiCeQI0Ly685CmKcsgFec6tAKQ5QnJWVrpkus=");
	}
	
	public ImageLoader getImgLoader() {
		return imgLoader;
	}

	public void setImgLoader(ImageLoader imgLoader) {
		this.imgLoader = imgLoader;
	}

//	public TimelineAdapter getAdapter() {
//		return adapter;
//	}

//	public void setAdapter(TimelineAdapter adapter) {
//		this.adapter = adapter;
//	}


	public DbHelper getDb() {
		
		return db;
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
		Log.i(TAG, "onTerminated");

	}

}
