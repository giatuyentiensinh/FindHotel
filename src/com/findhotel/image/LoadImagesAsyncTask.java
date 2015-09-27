package com.findhotel.image;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.BaseAdapter;
import com.findhotel.model.HotelInfo;
public class LoadImagesAsyncTask extends
AsyncTask<Void, Void, Void> {
	private List<HotelInfo> listHotelInfo;
	private Context mContext;
	private BaseAdapter hotelAdapter;

	/**
	 * Constructor
	 * @param mContext 
	 * @param listHotelInfo
	 * @param hotelAdapter 
	 */
	public LoadImagesAsyncTask(Context mContext, List<HotelInfo> listHotelInfo, BaseAdapter hotelAdapter) {
		this.listHotelInfo = listHotelInfo;
		this.mContext = mContext;
		this.hotelAdapter = hotelAdapter;
	}
	@Override
	protected Void doInBackground(Void... params) {
		//load bitmap
		Bitmap image = null;
		String url = "";
		HotelInfo hotelInfo = null;
		for (int i = 0; i < listHotelInfo.size(); i++) {
			if (!isCancelled()) {
				 hotelInfo= listHotelInfo.get(i);
				if (hotelInfo!=null) {
					url = listHotelInfo.get(i).getAvataLink();
					Bitmap bm = NetworkConnection.getBitmapFromMemCache(url);
					if (bm == null){
						image = NetworkConnection.loadRecieImage(mContext,url);
						NetworkConnection.addBitmapToMemoryCache(url, image);			    	  
					}
				}
				publishProgress();
			}
		}
		return null;
	}
	@Override
	protected void onProgressUpdate(Void... values) {
		super.onProgressUpdate(values);
		hotelAdapter.notifyDataSetChanged();							
	}
}
