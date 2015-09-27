package com.findhotel.model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;

public class GpsLocation {
	private final Context mContext;
	private Location location;
	private double latitude,longitude;
	private LocationManager locationManager;
	private boolean isGps =false;
	public GpsLocation(Context context) {
		this.mContext = context;
		getLocation();
	}
	/**
	 * get location
	 * @return location
	 */
	private Location getLocation() {
		locationManager =(LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
		isGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			if(isGps && Utility.isNetworkConnected(mContext)){
					location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
					if(location !=null){
						latitude = location.getLatitude();
						longitude = location.getLongitude();
					}
				}
		return location;
	}
	// get latitude
	public double getLocationX(){
		return latitude;
	}
	// get longitude
	public double getLocationY(){
		return longitude;
	}
	 /**
     * show dialog setting gps
     */
  	public void showDialogSettingGPS(){
  		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
	    builder.setMessage("Bạn hãy bật GPS để định vị tốt hơn")
	    		.setCancelable(false)
	           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog,int id) {
	            	   Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	                   mContext.startActivity(intent);
	               }
	           });
		builder.create().show();
  	}
}
