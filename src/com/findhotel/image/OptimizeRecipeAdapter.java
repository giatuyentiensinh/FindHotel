package com.findhotel.image;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import com.findhotel.activity.ActivityShowHotel;
import com.findhotel.activity.R;
import com.findhotel.custom.ShowHotelAdapter;
import com.findhotel.model.GpsLocation;
import com.findhotel.model.HotelInfo;
import com.findhotel.model.Utility;
import com.google.android.gms.maps.model.LatLng;

public class OptimizeRecipeAdapter extends EndlessAdapter {
	private RotateAnimation rotate=null;
	private View pendingView=null;
	private List<HotelInfo> hotelInfo = null;
	private Context mContext;
	private List<HotelInfo> listHotelInfo = null;
	private GpsLocation gps;
	private LatLng locationMe,locationHotel;
	private float bk;
	public ArrayList<HotelInfo> arrListHotelInfo ;
	public OptimizeRecipeAdapter(Context ctxt, List<HotelInfo> listHotelInfo) {
		super(new ShowHotelAdapter(ctxt,listHotelInfo));
		this.hotelInfo = listHotelInfo;
		this.mContext = ctxt;
		rotate=new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF,0.5f);
		rotate.setDuration(600);
		rotate.setRepeatMode(Animation.RESTART);
		rotate.setRepeatCount(Animation.INFINITE);
		NetworkConnection.initLruCache();
	}
	public LatLng locationMe(){
		gps = new GpsLocation(mContext);
		locationMe = new LatLng(gps.getLocationX(),gps.getLocationY());
		return locationMe;
	}
	/**
	 * pending view
	 */
	@Override
	protected View getPendingView(ViewGroup parent) {
		View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.row, null);
		pendingView=view.findViewById(android.R.id.text1);
		pendingView.setVisibility(View.GONE);
		pendingView=view.findViewById(R.id.img_pending);
		pendingView.setVisibility(View.VISIBLE);
		startProgressAnimation();

		return(view);
	}

	@Override
	protected boolean cacheInBackground() {
		listHotelInfo  = getListRecipes();
		NetworkConnection.loadRecipeImages(listHotelInfo, mContext, this);
		return false;
	}

	@Override
	protected void appendCachedData() {
		hotelInfo.addAll(listHotelInfo);
	}

	void startProgressAnimation() {
		if (pendingView!=null) {
			pendingView.startAnimation(rotate);
		}
	}
	public List<HotelInfo> getListRecipes() {
		arrListHotelInfo = new ArrayList<HotelInfo>();
		String urlStr ="http://findhotel.bugs3.com/hotelService.php";
		InputStream is = NetworkConnection.loadDataFromUrl(urlStr);
		String result = NetworkConnection.convertStreamToString(is);

		try {
			String name = null;
			int rank;
			String address = null;
			String description = null;
			String descriptionDetail = null;
			String contactInfo = null;
			double lat,lng;
			String avataLink = null;
			JSONObject Jhotel  = null;
			HotelInfo hotel = null;

			// path json
			JSONArray Jarrayhotel = new JSONArray(result);
			int hotelNumber = Jarrayhotel.length();
			if (Jarrayhotel!=null && hotelNumber > 0) {
				for (int i = 0; i < hotelNumber; i++) {
					
					Jhotel = Jarrayhotel.getJSONObject(i);
					name = Jhotel.getString("name");
					rank = Jhotel.getInt("rank");
					address = Jhotel.getString("address");
					description = Jhotel.getString("description");
					descriptionDetail =Jhotel.getString("descriptiondetail");
					contactInfo = Jhotel.getString("tel");
					lat = Jhotel.getDouble("lat");
					lng = Jhotel.getDouble("lng");
					avataLink = Jhotel.getString("avataLink");
					// check bk
					locationMe();
					locationHotel = new LatLng(lat, lng);
					bk = Utility.CalulationByDistance(locationMe, locationHotel);
					if(bk <= ActivityShowHotel.bankinh)
					{
					hotel = new HotelInfo(name, rank, address, description,descriptionDetail,contactInfo,lat,lng,avataLink,bk);
					arrListHotelInfo.add(hotel );
					}
				}

			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		return arrListHotelInfo;
	}
	
}