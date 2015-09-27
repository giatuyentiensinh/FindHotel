package com.findhotel.custom;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.findhotel.activity.R;
import com.findhotel.image.NetworkConnection;
import com.findhotel.model.HotelInfo;

public class ShowHotelAdapter extends BaseAdapter {

	private Context mContext;
	private List<HotelInfo> listHotel;
	
	public ShowHotelAdapter(Context rContext, List<HotelInfo> listHotel){
		this.mContext = rContext;
		this.listHotel = listHotel;
	}
	@Override
	public int getCount() {		
		return listHotel.size();
	}

	@Override
	public HotelInfo getItem(int position) {		
		return listHotel.get(position);
	}

	@Override
	public long getItemId(int position) {		
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//get selected Entry
		HotelInfo hotelInfo = listHotel.get(position);
		
		// inflating list view layout if null
		if(convertView == null){
			LayoutInflater inflater = LayoutInflater.from(mContext);
			convertView = inflater.inflate(R.layout.list_custom_hotel, null);
		}
		//set name
		TextView name = (TextView)convertView.findViewById(R.id.tenkhachsan);
		name.setText("Khách Sạn:"+hotelInfo.getName()+"("+hotelInfo.getBankinh()+"km)");
		// set address
		TextView address = (TextView)convertView.findViewById(R.id.diachikhachsan);
		address.setText(hotelInfo.getAddress());
		// set rank
		RatingBar rankBar =(RatingBar)convertView.findViewById(R.id.rankkhachsan);
		rankBar.setRating(hotelInfo.getRank());
		//set image
		ImageView rImage = (ImageView)convertView.findViewById(R.id.anhkhachsan);
		NetworkConnection.loadImage(hotelInfo,rImage,mContext,this);
		// set description
		TextView description = (TextView)convertView.findViewById(R.id.thongtin);
		description.setText(hotelInfo.getDescription());
		return convertView;
	}
	
}
