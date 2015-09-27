package com.findhotel.activity;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.findhotel.image.OptimizeRecipeAdapter;
import com.findhotel.model.HotelInfo;
public class ActivityShowHotel extends Activity {

	private Bitmap headerBackground;
	private ListView listHotel;
	private OptimizeRecipeAdapter mOptimizeRecipeAdapter;
	private ArrayList<HotelInfo> listHotelInfo;
	private EditText searchHotel;
	private ProgressDialog progressDialog;
	public static HotelInfo hotelInfo;
	public static int bankinh;
	private ArrayList<HotelInfo> listHotelInfoSearch;
	private ArrayList<HotelInfo> listHotelInfoNullSearch;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_activity_show_hotel);
		Intent intent =getIntent();
		Bundle bundle =intent.getBundleExtra("DATA");
		bankinh = bundle.getInt("BANKINH");
		initViewComponent();
		// show progressdialog
        progressDialog =ProgressDialog.show(ActivityShowHotel.this,"Xin vui lòng đợi trong giây lát...","Bạn đang tìm với bán kính: "+bankinh+ "Km");
        progressDialog.setCancelable(true);
        new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				progressDialog.dismiss();
			}
		}).start();
	}
	/**
	 * init view component
	 */
	private void initViewComponent() {
		listHotel = (ListView)findViewById(R.id.listkhachsan);
		searchHotel =(EditText)findViewById(R.id.searchhotel);
		registerForContextMenu(listHotel);
		if (listHotel!=null) {
			listHotelInfo = new ArrayList<HotelInfo>();
			listHotel.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View view,
						int position, long id) {
					hotelInfo =listHotelInfo.get(position);
					return false;
				}
			});
		}
	}
	public void backToFoodArchive(View v){
		onBackPressed();
	}
	@Override
    public void onDestroy() {
        super.onDestroy();
        if (headerBackground != null){
        	headerBackground.recycle();
        	headerBackground = null;
        }
    }
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	@Override
	protected void onResume() {
		super.onResume();
		loadHotelInfo();	
		searchHotel();
	}
	/**
	 * load hotel
	 */
	public void loadHotelInfo() {
		if (listHotel!=null) {
			if (mOptimizeRecipeAdapter==null) {
				mOptimizeRecipeAdapter = new OptimizeRecipeAdapter(getApplicationContext(),listHotelInfo);
				listHotel.setAdapter(mOptimizeRecipeAdapter);
				mOptimizeRecipeAdapter.notifyDataSetChanged();
			}
		}
	}
	/**
	 * Search hotel
	 */
	private void searchHotel() {
		searchHotel = (EditText)findViewById(R.id.searchhotel);
		searchHotel.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String search =searchHotel.getText().toString();
				ActivityShowHotel.this.filter(search);
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {		
			}
		});
		
	}
	/**
	 * search hotel follow name
	 */
	public void filter(String search) {
		listHotelInfoSearch = new ArrayList<HotelInfo>();
		listHotelInfoNullSearch = new ArrayList<HotelInfo>();
		search = search.toLowerCase(Locale.ENGLISH);
		int lenghtList = listHotelInfo.size();
		if(search.length()!=0){
		for(int i=0; i<lenghtList; i++){
			hotelInfo = listHotelInfo.get(i);
			if(hotelInfo.getName().toLowerCase(Locale.ENGLISH).contains(search))
			{
				listHotelInfoSearch.add(hotelInfo);
			}
		}
		listHotelInfoNullSearch.addAll(listHotelInfo);
		listHotelInfo.clear();
		listHotelInfo.addAll(listHotelInfoSearch);
		}
		else{
			listHotelInfo.clear();
			listHotelInfo.addAll(listHotelInfoNullSearch);
		}
		mOptimizeRecipeAdapter.notifyDataSetChanged();
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.main, menu);
	}
	/**
	 * handle onclick menu
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.searchline:
			doSearchLine();
			break;
		case R.id.cancel:
			doCancel();
			break;
		}
		return super.onContextItemSelected(item);
	}
	/**
	 * choose bk other
	 */
	private void doCancel() {
		Intent intent = new Intent(ActivityShowHotel.this,MainActivity.class);
		startActivity(intent);
	}
	/**
	 * Call ActivityDetailHotel
	 */
	private void doSearchLine() {
		Bundle bundle = new Bundle();
		bundle.putSerializable("HOTEL",hotelInfo);
		Intent intent = new Intent(ActivityShowHotel.this,ActivityDetailHotel.class);
		intent.putExtra("DATA",bundle);
		startActivity(intent);
	}
}
