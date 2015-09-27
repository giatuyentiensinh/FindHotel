package com.findhotel.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.findhotel.custom.ShowBankinhAdapter;
import com.findhotel.model.GpsLocation;
import com.findhotel.model.Utility;


public class MainActivity extends Activity {
	private ImageView btnFindHotel;
	private TextView bankinh;
	private GpsLocation gps;
	private int bk;
	private Bundle bundle;
	private boolean isShowListView = false;
	private View view;
	private  ListView listBanKinh;
	private ShowBankinhAdapter adapterBankinh;
	private int[] arrBankinh = new int[]{1,2,3,4,5,6,7,8,9,10,
			   11,12,13,14,15,16,17,18,19,20,
			   21,22,23,24,25,26,27,28,29,30};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bankinh = (TextView)findViewById(R.id.bankinh);
        btnFindHotel =(ImageView)findViewById(R.id.btn_find);
        /**
         * Check show list ban kinh
         */
        view =(View)findViewById(R.id.combobox);
        view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			if(!isShowListView){
				listBanKinh =(ListView)findViewById(R.id.list_bankinh);
				listBanKinh.setVisibility(View.VISIBLE);
				isShowListView = true;
				adapterBankinh = new ShowBankinhAdapter(getBaseContext(),arrBankinh);
				listBanKinh.setAdapter(adapterBankinh);
		        listBanKinh.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View view, int positon,
							long id) {
						listBanKinh.setVisibility(View.GONE);
						bankinh.setText(arrBankinh[positon]+"");
					}
				});
			}
			else{
				isShowListView = false;
				listBanKinh.setVisibility(View.GONE);
			}
			}
		}); 
        /**
         * find hotel
         */
        btnFindHotel.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				/**
				 * Check gps
				 */
				if(!Utility.isGpsConnected(getApplicationContext())){
					gps = new GpsLocation(MainActivity.this);
					gps.showDialogSettingGPS();
				}
				/**
				 * default bk = 2 km
				 */
				else{
					String strbk =bankinh.getText().toString();
					if(strbk.length()==0){
						bk = 2;
					}
					else{
					bk =Integer.parseInt(strbk);
					}
					bundle = new Bundle();
					bundle.putInt("BANKINH",bk);
					Intent intent = new Intent(MainActivity.this,ActivityShowHotel.class);
					intent.putExtra("DATA",bundle);
					startActivity(intent);
				}
				
			}
		}); 
  	}
    /**
     * handle on back
     */
    @Override
    public void onBackPressed() {
    	if(isShowListView){
    		isShowListView = false;
    		listBanKinh.setVisibility(View.GONE);
    	}
    	else{
    		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
    		builder.setTitle("Thoát");
    		builder.setMessage("Bạn muốn thoát ứng dụng");
    		builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int wich) {
					dialog.cancel();
				}
			});
    		builder.setPositiveButton("Có",new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});
    		builder.create().show();
    	}
    }
   
}
