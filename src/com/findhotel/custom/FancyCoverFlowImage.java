package com.findhotel.custom;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.findhotel.activity.ActivityShowHotel;
import com.findhotel.activity.R;
import com.findhotel.fancycoverflow.FancyCoverFlow;
import com.findhotel.fancycoverflow.FancyCoverFlowAdapter;
import com.findhotel.image.LoadImageUrl;
import com.findhotel.model.HotelInfo;

public class FancyCoverFlowImage extends FancyCoverFlowAdapter {
	private HotelInfo hotelInfo =ActivityShowHotel.hotelInfo;
	ImageView imageView;
	int[] image ={R.drawable.no_image};
	@Override
	public int getCount() {
		return 1000;
	}

	@Override
	public Integer getItem(int arg0) {
		return image[0];
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getCoverFlowItem(int position, View reusableView,
			ViewGroup parent) {
        if (reusableView != null) {
            imageView = (ImageView) reusableView;
        } else {
            imageView = new ImageView(parent.getContext());
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            imageView.setLayoutParams(new FancyCoverFlow.LayoutParams(300, 400));
        }
        imageView.setImageResource(this.getItem(0));
        String url =hotelInfo.getAvataLink();
        LoadImageUrl  urlLoad = new LoadImageUrl(url,imageView);
        urlLoad.execute();
        return imageView;
	}
}
