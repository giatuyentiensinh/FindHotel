/**
 * Load Image
 */
package com.findhotel.image;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.findhotel.activity.R;
import com.findhotel.model.HotelInfo;

public class NetworkConnection {
	/*Số lần thử kết nối nếu không có mạng*/
	public static final int NETWORK_MAX_RETRY = 3;	
	public static LruCache<String, Bitmap> mMemoryCache;
	private static String TAG = "NetworkConnection";
	private static ArrayList<HotelInfo> sPendingImages;
	
	public static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemCache(key) == null) {
			mMemoryCache.put(key, bitmap);
		}
	}
	public static void initLruCache() {
		if (mMemoryCache==null) {
			// Lấy tối đa bộ nhớ máy ảo có sẵn
			final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

			//Sử dụng 1/4 dành cho bộ nhớ cache;
			final int cacheSize = maxMemory / 4;

			mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
				@Override
				protected int sizeOf(String key, Bitmap bitmap) {
					// Kích thước bộ nhớ cache được đo bằng kb 
					return bitmap.getRowBytes() * bitmap.getHeight()/ 1024;
				}
			};
		}
	}

	public static Bitmap getBitmapFromMemCache(String key) {
		return mMemoryCache.get(key);
	}
	/**
	 * openHttpConnectionGetImage.<br>
	 * .<br>
	 * @param urlStr
	 * @return
	 * @throws JSONException
	 */
	public static byte[] openHttpConnectionGetImage(String urlStr) throws JSONException {
		int resCode = -1;
		byte[] result = null;
		HttpURLConnection httpConn = null;

		try {
			URL url = new URL(urlStr);
			URLConnection urlConn = url.openConnection();

			httpConn = (HttpURLConnection) urlConn;
			httpConn.setRequestMethod("GET");

			int retryExhausted = NETWORK_MAX_RETRY;
			do{
				retryExhausted--;
				httpConn.connect();
				resCode = httpConn.getResponseCode();
				if (resCode == HttpURLConnection.HTTP_OK){
					break;
				}
			}while (retryExhausted > 0);

			if (resCode == HttpURLConnection.HTTP_OK) {
				InputStream responseData = httpConn.getInputStream();
				BufferedInputStream in = new BufferedInputStream(responseData);
				ByteArrayOutputStream byteArr = new ByteArrayOutputStream();
				int c;
				while ((c = in.read()) != -1) {
					byteArr.write(c);
				}
				result = byteArr.toByteArray();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if (httpConn != null){
				httpConn.disconnect();
			}
		}

		return result;
	}
	/**
	 * load data from url
	 * 
	 */
	public static InputStream loadDataFromUrl(String urlStr) {
		Log.i(TAG, "url request = "+urlStr);
		InputStream is = null;
		try {
			is = (InputStream) new URL(urlStr).getContent();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return is;
	}
	/**
	 * @return string
	 */
	public static String convertStreamToString(final InputStream input) {
		if (input == null)
		{
			return "";
		}
		final BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		final StringBuilder sBuf = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sBuf.append(line);
			}
		} catch (IOException e) {
		} finally {
			try {
				input.close();
			} catch (IOException e) {
			}
		}
		return sBuf.toString();
	}


	/**
	 * load image
	 * @return bitmap
	 */
	public static Bitmap loadRecieImage(Context ctx,String url) {
		Bitmap result = null;
		InputStream imageInputStream = NetworkConnection.loadDataFromUrl(url);
		result = BitmapFactory.decodeStream(imageInputStream);

		if (result==null) {
			result = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.no_image);
		}
		return result;
	}
	/**
	 * load Image
	 * @param entry
	 * @param imageView
	 * @param context
	 * @param adapter
	 */
	public static void loadImage(HotelInfo entry, ImageView imageView, Context context, BaseAdapter adapter) {
		Bitmap bm = NetworkConnection.getBitmapFromMemCache(entry.getAvataLink());
		if (bm==null) {
			NetworkConnection.addPendingRecipeImageLoading(entry, context, adapter);
		}
		imageView.setImageBitmap(bm);
	}
	/**
	 * @param entry
	 * @param context 
	 * @param adapter 
	 */
	private static void addPendingRecipeImageLoading(HotelInfo entry, Context context, BaseAdapter adapter) {
		sPendingImages = new ArrayList<HotelInfo>();
		addRecipeToPending(entry);
		List<HotelInfo> pendings = new ArrayList<HotelInfo>(sPendingImages);
		LoadImagesAsyncTask mTask = new LoadImagesAsyncTask(context, pendings , adapter);
		mTask.execute();
	}
	/**
	 * @param entry
	 */
	public static void addRecipeToPending(HotelInfo entry) {
		sPendingImages.add(entry);
	}
	/**
	 * @param mDetailRecipes
	 * @param context 
	 * @param ba 
	 */
	public static void loadRecipeImages(List<HotelInfo> mDetailRecipes, Context context, BaseAdapter ba) {
		LoadImagesAsyncTask lri = new LoadImagesAsyncTask(context,mDetailRecipes,ba);
		lri.execute();
	}
}