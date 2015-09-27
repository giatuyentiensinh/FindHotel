package com.findhotel.custom;

import com.findhotel.activity.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
public class ShowBankinhAdapter extends BaseAdapter {
		private Context context;
		private int[] bankinh;
		public ShowBankinhAdapter(Context context, int[] bk){
			this.context = context;
			this.bankinh = bk;
		}
		@Override
		public int getCount() {		
			return bankinh.length;
		}
		@Override
		public Object getItem(int position) {		
			return bankinh[position];
		}
		@Override
		public long getItemId(int position) {		
			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
				LayoutInflater inflater = LayoutInflater.from(context);
				convertView = inflater.inflate(R.layout.activity_custom_bankinh_hotel, null);
				TextView tv =(TextView)convertView.findViewById(R.id.customBankinh);
				tv.setText(bankinh[position]+"");
			return convertView;
		}
}
