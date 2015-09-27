package com.findhotel.image;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class LoadImageUrl extends AsyncTask<Void,Void,Bitmap> {
	private String url;
	private ImageView imageview;
	public LoadImageUrl(String url,ImageView img) {
		this.url = url;
		this.imageview = img;
	}
	@Override
	protected Bitmap doInBackground(Void... param) {
		if(NetworkConnection.getBitmapFromMemCache(url)==null){
		try {
			HttpURLConnection conn =(HttpURLConnection)new URL(url).openConnection();
			conn.setDoInput(true);
			conn.connect();
		    InputStream in =conn.getInputStream();
		    Bitmap bm = BitmapFactory.decodeStream(in);
		    NetworkConnection.addBitmapToMemoryCache(url,bm);
		    return NetworkConnection.getBitmapFromMemCache(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
		}
		return NetworkConnection.getBitmapFromMemCache(url);
	}
	@Override
	protected void onPostExecute(Bitmap result) {
		super.onPostExecute(result);
		imageview.setImageBitmap(result);
	}
}
