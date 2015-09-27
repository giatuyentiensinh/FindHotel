package com.findhotel.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.findhotel.activity.R.color;
import com.findhotel.custom.FancyCoverFlowImage;
import com.findhotel.fancycoverflow.FancyCoverFlow;
import com.findhotel.model.GpsLocation;
import com.findhotel.model.HotelInfo;
import com.findhotel.model.HttpConnection;
import com.findhotel.model.PathMapJson;
import com.findhotel.model.Utility;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class ActivityDetailHotel extends FragmentActivity implements OnClickListener {
	private Button btnLienhe;
	private FancyCoverFlow fancyCoverFlow;
	private LatLng locationMe;
	private LatLng locationHotel;
	GoogleMap googleMap;
	final String TAG = "ActivityDetailHotel";
	private GpsLocation gps;
	private double x,y;
	private HotelInfo hotelInfo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_activity_detail_hotel);
		btnLienhe = (Button)findViewById(R.id.lienhe);
		btnLienhe.setOnClickListener(this);
		/**
		 * get location me
		 */
		gps = new GpsLocation(getApplicationContext());
		if(Utility.isGpsConnected(getApplicationContext())){
			x =gps.getLocationX();
			y =gps.getLocationY();
		}
		else{
			gps.showDialogSettingGPS();
		}
		locationMe = new LatLng(x,y);
		/**
		 * get location hotel
		 */
		Intent intent = getIntent();
		Bundle bundle =intent.getBundleExtra("DATA");
		hotelInfo = (HotelInfo)bundle.getSerializable("HOTEL");
		TextView thongtinchitiet = (TextView)findViewById(R.id.thongtinchitiet);
		thongtinchitiet.setText(hotelInfo.getDescriptiondetail());
		btnLienhe.setText(hotelInfo.getContactInfo());
		locationHotel = new LatLng(hotelInfo.getLat(),hotelInfo.getLng());
		/**
		 * show map
		 */
		SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		googleMap = fm.getMap();

		MarkerOptions options = new MarkerOptions();
		options.position(locationMe);
		options.position(locationHotel);
		googleMap.addMarker(options);
		String url = getMapsApiDirectionsUrl();
		ReadTask downloadTask = new ReadTask();
		downloadTask.execute(url);

		googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationHotel,13));
		addMarkers();
		doFancyCoverFlow();
	}
	// effect image 
	@SuppressWarnings("deprecation")
	private void doFancyCoverFlow() {
		this.fancyCoverFlow = (FancyCoverFlow) this.findViewById(R.id.fancyCoverFlow);
		this.fancyCoverFlow.setBackgroundResource(R.drawable.fancyconver_bg);
        this.fancyCoverFlow.setAdapter(new FancyCoverFlowImage());
        this.fancyCoverFlow.setUnselectedAlpha(2.0f);
        this.fancyCoverFlow.setUnselectedSaturation(0);
        this.fancyCoverFlow.setUnselectedScale(1.0f);
        this.fancyCoverFlow.setSpacing(0);
        this.fancyCoverFlow.setMaxRotation(0);
        this.fancyCoverFlow.setScaleDownGravity(0);
        this.fancyCoverFlow.setActionDistance(FancyCoverFlow.ACTION_DISTANCE_AUTO);
		
	}
	/**
	 * get map api direction url
	 * @return url
	 */
	private String getMapsApiDirectionsUrl() {
		String waypoints = "waypoints=optimize:true|"
				+ locationMe.latitude + "," + locationMe.longitude
				+ "|" + "|" + locationHotel.latitude + ","
				+ locationHotel.longitude;

		String sensor = "sensor=false";
		String params = waypoints + "&" + sensor;
		String output = "json";
		String url = "https://maps.googleapis.com/maps/api/directions/"
				+ output + "?" + params;
		return url;
	}

	private void addMarkers() {
		if (googleMap != null) {
			// hotel
			String locationhotel ="Khách sạn:"+hotelInfo.getName()+'\n'+"Địa chỉ:"+hotelInfo.getAddress()+'\n'+"Liên hệ:"+hotelInfo.getContactInfo();
			googleMap.addMarker(new MarkerOptions().position(locationHotel)
					.title(locationhotel));
			// location me
			googleMap.addMarker(new MarkerOptions().position(locationMe)
					.title("Vị trí của bạn"));
		}
	}

	private class ReadTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... url) {
			String data = "";
			try {
				HttpConnection http = new HttpConnection();
				data = http.readUrl(url[0]);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
			return data;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			new ParserTask().execute(result);
		}
	}

	private class ParserTask extends
			AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

		@SuppressWarnings("unchecked")
		@Override
		protected List<List<HashMap<String, String>>> doInBackground(
				String... jsonData) {

			JSONObject jObject;
			List<List<HashMap<String, String>>> routes = null;

			try {
				jObject = new JSONObject(jsonData[0]);
				PathMapJson parser = new PathMapJson();
				routes = parser.parse(jObject);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return routes;
		}
		/**
		 *  draw line
		 */
		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
			ArrayList<LatLng> points = null;
			PolylineOptions polyLineOptions = null;

			// traversing through routes
			for (int i = 0; i < routes.size(); i++) {
				points = new ArrayList<LatLng>();
				polyLineOptions = new PolylineOptions();
				List<HashMap<String, String>> path = routes.get(i);

				for (int j = 0; j < path.size(); j++) {
					HashMap<String, String> point = path.get(j);

					double lat = Double.parseDouble(point.get("lat"));
					double lng = Double.parseDouble(point.get("lng"));
					LatLng position = new LatLng(lat, lng);

					points.add(position);
				}

				polyLineOptions.addAll(points);
				polyLineOptions.width(6);
				polyLineOptions.color(Color.BLUE);
			}
			googleMap.addPolyline(polyLineOptions);
		}
	}
	/**
	 * contact
	 */
	@Override
	public void onClick(View v) {
		String phone =btnLienhe.getText().toString();
		Uri uri =Uri.parse("tel:"+phone);
		Intent intent = new Intent(Intent.ACTION_CALL, uri);
		startActivity(intent);
	}
}
