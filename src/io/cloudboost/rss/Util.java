package io.cloudboost.rss;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Util {
    public static boolean haveNetworkConnection(Activity activity) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
    public static byte[] getImageBytes(String imgUrl){
    	   URL url=null;
		try {
			url = new URL(imgUrl);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
    	     URLConnection ucon;
    	     InputStream is=null;
			try {
				ucon = url.openConnection();
				
				is= ucon.getInputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	      
    	     BufferedInputStream bis = new BufferedInputStream(is,128);
    	     ByteArrayBuffer barb= new ByteArrayBuffer(128);
    	      //read the bytes one by one and append it into the ByteArrayBuffer barb
    	      int current = 0;
    	      try {
				while ((current = bis.read()) != -1) {
				          barb.append((byte) current);
				  }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	      
    	     byte[] imageBytes= barb.toByteArray();
    	     p("image bytes are "+imageBytes);
    	     return imageBytes;
    	      //db is a SQLiteDatabase object
//    	      ContentValues filedata= new ContentValues();
//    	     //TABLE_FIELD = a field in the database table of type text                          
//    	      filedata.put(TABLE_FIELD,barb.toByteArray()); 
//    	     //TABLE_NAME = a table in the database with a field TABLE_FIELD in the database table of type text
//    	      db.insert(TABLE_NAME, null, filedata);
    }
    public static void p(String str){
    	System.out.println(str);
    }
//    public static Bitmap getImage(){
//    	//get it as a ByteArray
//        //reading as Binary large object(BLOB)
//	byte[] imageByteArray=cursor.getBlob(1);
//	//the cursor is not needed anymore so release it
//	cursor.close();
//	//convert it back to an image
//	ByteArrayInputStream imageStream = new ByteArrayInputStream(mybyte);
//	Bitmap theImage = BitmapFactory.decodeStream(imageStream));
//    }
   public static void main(String[] args) {
	getImageBytes("http://pbs.twimg.com/profile_images/449566090419396609/MQBliWdf_normal.jpeg");
}
}
