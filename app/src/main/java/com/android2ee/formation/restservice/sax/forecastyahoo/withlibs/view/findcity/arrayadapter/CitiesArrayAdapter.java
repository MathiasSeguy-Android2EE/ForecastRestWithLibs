/**<ul>
 * <li>ForecastYahooRest</li>
 * <li>com.android2ee.formation.restservice.sax.forecastyahoo.view.city.arrayadapter</li>
 * <li>17 nov. 2014</li>
 * 
 * <li>======================================================</li>
 *
 * <li>Projet : Mathias Seguy Project</li>
 * <li>Produit par MSE.</li>
 *
 /**
 * <ul>
 * Android Tutorial, An <strong>Android2EE</strong>'s project.</br> 
 * Produced by <strong>Dr. Mathias SEGUY</strong>.</br>
 * Delivered by <strong>http://android2ee.com/</strong></br>
 *  Belongs to <strong>Mathias Seguy</strong></br>
 ****************************************************************************************************************</br>
 * This code is free for any usage except training and can't be distribute.</br>
 * The distribution is reserved to the site <strong>http://android2ee.com</strong>.</br>
 * The intelectual property belongs to <strong>Mathias Seguy</strong>.</br>
 * <em>http://mathias-seguy.developpez.com/</em></br> </br>
 * 
 * *****************************************************************************************************************</br>
 *  Ce code est libre de toute utilisation mais n'est pas distribuable.</br>
 *  Sa distribution est reservée au site <strong>http://android2ee.com</strong>.</br> 
 *  Sa propriété intellectuelle appartient à <strong>Mathias Seguy</strong>.</br>
 *  <em>http://mathias-seguy.developpez.com/</em></br> </br>
 * *****************************************************************************************************************</br>
 */
package com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.view.findcity.arrayadapter;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android2ee.formation.restservice.sax.forecastyahoo.R;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.transverse.exception.ExceptionManaged;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.transverse.exception.ExceptionManager;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.transverse.model.current.City;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.transverse.utils.MyLog;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.view.current.main.MainCardView;

import java.util.ArrayList;

/**
 * @author Mathias Seguy (Android2EE)
 * @goals
 *        This class aims to:
 *        <ul>
 *        <li></li>
 *        </ul>
 */
public class CitiesArrayAdapter extends ArrayAdapter<City> {

	private static final String TAG = "CitiesArrayAdapter";
	/**
	 *
	 */
	private static final float KELVIN_OFFSET_TO_CELSIUS = -273.15f;
	/**
	 * *The layout inflater to build the view
	 */
	LayoutInflater inflater;
	/**
	 * Context 
	 */
	Context ctx;
	/**
	 * The Earth icon to display googleMap
	 */
	Drawable earth;
	/**
	 *
	 */
	Boolean postJellyBean;
	/**
	 *
	 */
	ArrayList<City> list;
	/**
	 * @param context
	 * @param list
	 */
	public CitiesArrayAdapter(Context context, ArrayList<City> list) {
		super(context, R.layout.activity_city_item_city, list);
		inflater = LayoutInflater.from(context);
		earth = ContextCompat.getDrawable(context, R.drawable.ic_googlemap);
		ctx=context;
        if(ctx.getResources().getBoolean(R.bool.postJB)){
            postJellyBean=true;
        }else{
            postJellyBean=false;
        }
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		MyLog.e(TAG, "notifyDataSetChanged() called with: ");
	}
/******************************************************************************************/
	/** Managing View **************************************************************************/
	/******************************************************************************************/
	private static City city;
	private static View rowView;
	private static ViewHolder viewHolder;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		rowView = convertView;
		city = getItem(position);
		if (rowView == null) {
			rowView = inflater.inflate(R.layout.activity_city_item_city, parent, false);
			viewHolder = new ViewHolder(rowView);
			rowView.setTag(viewHolder);			
		}
		viewHolder = (ViewHolder) rowView.getTag();
		viewHolder.getTxvCityName().setText(city.getName());
		if(city.getSys()!=null){
			viewHolder.getTxvCountry().setVisibility(View.VISIBLE);
			viewHolder.getTxvCountry().setText(city.getSys().getCountry());
		}else{
			viewHolder.getTxvCountry().setVisibility(View.GONE);
		}
		if(city.getMain()!=null){
			viewHolder.getTxvTemp().setVisibility(View.VISIBLE);
			viewHolder.getTxvTemp().setText(getContext().getString(R.string.main_temperature, city.getMain().getTemp() + KELVIN_OFFSET_TO_CELSIUS));
		}else{
			viewHolder.getTxvTemp().setVisibility(View.GONE);
		}
		if(city.getCoord()!=null) {
			viewHolder.getImvGoogleMap().setVisibility(View.VISIBLE);
			if (postJellyBean) {
				viewHolder.getImvGoogleMap().setBackground(earth);
			} else {
				viewHolder.getImvGoogleMap().setBackgroundDrawable(earth);
			}
			viewHolder.setLatLon(city.getCoord().getLat(), city.getCoord().getLon());
		}else{
			viewHolder.getImvGoogleMap().setVisibility(View.GONE);
		}
		return rowView;
	}


	/******************************************************************************************/
	/** View Holder **************************************************************************/
	/******************************************************************************************/

	private class ViewHolder {
		TextView txvCityName = null;
        TextView txvCountry = null;
        TextView txvTemp = null;
		ImageView imvGoogleMap = null;
		Uri uri;
		View rowView;
		/**
		 * @param rowView
		 */
		public ViewHolder(View rowView) {
			super();
			this.rowView = rowView;
			imvGoogleMap = (ImageView) rowView.findViewById(R.id.imvGoogleMap);
			imvGoogleMap.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					launchGoogleEarth(v) ;
				}
			});
            txvCityName = rowView.findViewById(R.id.txvCityName);
            txvCountry = rowView.findViewById(R.id.txvCountry);
            txvTemp = rowView.findViewById(R.id.txvTemp);
		}

		/**
		 * @return the txvCityName
		 */
		public final TextView getTxvCityName() {
			return txvCityName;
		}

		/**
		 * @return the txvCountry
		 */
		public final TextView getTxvCountry() {
			return txvCountry;
		}

		/**
		 * @return the imvGoogleMap
		 */
		public final ImageView getImvGoogleMap() {
			return imvGoogleMap;
		}

        /**
         *
         * @return the txvTemp (for temperature)
         */
        public TextView getTxvTemp() {
            return txvTemp;
        }

        /**
		 * Launch GoogleMap using an Intent
		 */
		private void launchGoogleEarth(View v) {
			try {
				ctx.startActivity(new Intent(Intent.ACTION_VIEW,uri));
			}catch (ActivityNotFoundException exc) {
				ExceptionManager.manage(new ExceptionManaged(getClass(), R.string.exc_no_map_found, exc));
			}
		}
		/**
		 * Define the LatLong to use when launching GoogleMap
		 * @param lat latitude
		 * @param longi longitude
		 */
		public void setLatLon(float lat, float longi) {
			uri= Uri.parse("geo:" + lat + "," + longi+"?z=8"+"&&q=<"+lat+">,<"+longi+">("+txvCityName.getText()+")");
		}
	}
}
