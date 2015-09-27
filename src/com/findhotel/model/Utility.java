package com.findhotel.model;


import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.android.gms.maps.model.LatLng;

public class Utility {

    /**
     * Check if connect to network or not.
     * @param mContext
     *          The context of current activity.
     * @return boolean value.
     */
    public static boolean isNetworkConnected(Context mContext) {

        ConnectivityManager connManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = connManager.getActiveNetworkInfo();

        return (mNetworkInfo != null && mNetworkInfo.isConnectedOrConnecting());
    }
    /**
     * Check GPS
     */
    public static boolean isGpsConnected(Context mContext){
    	LocationManager locationManager =(LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
    	return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
    /**
     * Count distance between two point
     */
	public static float CalulationByDistance (LatLng start,LatLng end){
		    //Bán kính trái đất 
	        float Radius=6371;        
	        double lat1 = start.latitude;
	        double lat2 = end.latitude;
	        double long1 = start.longitude;
	        double long2 = end.longitude;
	        double dLat = Math.toRadians(lat2-lat1);
	        double dLong = Math.toRadians(long2-long1);
	        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	        Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
	        Math.sin(dLong/2) * Math.sin(dLong/2);
	        double c = 2 * Math.asin(Math.sqrt(a));
	        double valueResult = Radius*c;
	        DecimalFormat decimalFormat = (DecimalFormat)NumberFormat.getNumberInstance(Locale.US);
	        decimalFormat.applyPattern("#.##");  
	        float kmInDec = Float.valueOf(decimalFormat.format(valueResult));
	        return kmInDec;
	}
}
