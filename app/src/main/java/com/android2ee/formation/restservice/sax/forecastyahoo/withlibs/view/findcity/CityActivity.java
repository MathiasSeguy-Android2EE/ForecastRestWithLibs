package com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.view.findcity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android2ee.formation.restservice.sax.forecastyahoo.R;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.transverse.event.CityAddedEvent;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.transverse.model.current.City;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.transverse.utils.MyLog;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.view.MotherActivity;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.view.current.CurrentWeatherActivity;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.view.findcity.arrayadapter.CitiesArrayAdapter;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.view.generic.indeterminatedecorator.IndeterminateStateWidgetDecorator;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * In a way this is the View of the MVP model
 * It focused on displaying the Data
 * (ok as it is also an Activity it manages the lifecycle and all the activities stuffs)
 */
public class CityActivity extends MotherActivity {
	private static final String TAG = "CityActivity";
	public static final String CITY_ID = "CityId";
	/***********************************************************
	 *  Presenter
	 **********************************************************/
	/**
	 * The Presenter associated with that view
	 */
	CityActivityViewModel viewModel;

	/******************************************************************************************/
	/** Constant **************************************************************************/
	/******************************************************************************************/
	/**
	 * For the save and restore, the key for the editText
	 */
	private static final String EDT_SEARCHED_CITY = "EDT_SEARCHED_CITY";

	/******************************************************************************************/
	/** Attribute **************************************************************************/
	/******************************************************************************************/
	/**
	 * The edit text where the user give the name of the serached city
	 */
	private EditText edtSearchedCity = null;
	/**
	 * The button to launch the search of the city
	 */
	private Button btnSearch = null;
	/**
	 * The listView that displays the list of the Cities found
	 */
	private ListView lsvCityList = null;
	/**
	 * Message when no city found
	 */
	private AppCompatTextView txvEmptyCase;
	/**
	 * The arrayAdapter to display the city
	 */
	private CitiesArrayAdapter arrayAdapterCities = null;
	/**
	 * The boolean to know if the edittext is empty
	 */
	private boolean isEdtSizeEnough = true;
	private IndeterminateStateWidgetDecorator decorator;
	/******************************************************************************************/
	/** Managing LifeCycle **************************************************************************/
	/******************************************************************************************/

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//instanciate your presenter here not before not after
		viewModel= ViewModelProviders.of(this).get(CityActivityViewModel.class);
		setContentView(R.layout.activity_city);
		edtSearchedCity = (EditText) findViewById(R.id.edt_citySearchedName);
		decorator=new IndeterminateStateWidgetDecorator(findViewById(R.id.lil_main));
		txvEmptyCase=findViewById(R.id.txvEmptyCase);
        //add the ime action
        edtSearchedCity.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            /**
             * Called when an action is being performed.
             *
             * @param v        The view that was clicked.
             * @param actionId Identifier of the action.  This will be either the
             *                 identifier you supplied, or {@link EditorInfo#IME_NULL
             *                 EditorInfo.IME_NULL} if being called due to the enter key
             *                 being pressed.
             * @param event    If triggered by an enter key, this is the event;
             *                 otherwise, this is null.
             * @return Return true if you have consumed the action, else false.
             */
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				MyLog.d(TAG, "onEditorAction() called with: " + "v = [" + v + "], actionId = [" + actionId + "], event = [" + event + "]");
				if (actionId == R.id.edt_citySearchedName_ime
                        ||actionId == EditorInfo.IME_ACTION_DONE
                        ||actionId == EditorInfo.IME_NULL) {
                    // do here your stuff
                    searchCityAction();
                    return true;
                }
                return false;
            }
        });
		btnSearch = (Button) findViewById(R.id.btn_search_city);
		lsvCityList = (ListView) findViewById(R.id.lsvCityList);
		arrayAdapterCities = new CitiesArrayAdapter(this, new ArrayList<City>());
		lsvCityList.setAdapter(arrayAdapterCities);
		// adding listeners
		btnSearch.setEnabled(false);
		btnSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				searchCityAction();

			}
		});
		lsvCityList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectCity(position);
			}
		});
		edtSearchedCity.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				setEdtEmpty(s.length());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		//clear because of initialization of the request
		viewModel.searchCity(null);
		//observe the data you want to display (the list of cities)
		viewModel.getCitiesFoundLiveData().observe(this, new Observer<List<City>>() {
			@Override
			public void onChanged(@Nullable List<City> cities) {
				updateCities(cities);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		manageSearchButtonStatus();
	}


	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		edtSearchedCity.setText(savedInstanceState.getString(EDT_SEARCHED_CITY));
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(EDT_SEARCHED_CITY, edtSearchedCity.getText().toString());
	}

	@Override
	protected void onStop() {
		super.onStop();
		decorator.onStop();
	}

	/******************************************************************************************/
	/** Managing connectivity **************************************************************************/
	/******************************************************************************************/

	/**
	 * Change the status enable/disable of the btnSearch depending on the connectivity status
	 * and the value contained by the editText
	 */
	private void manageSearchButtonStatus() {
		if (isConnected && !isEdtSizeEnough) {
			// do the animation only if the button state changes from disable to enable
			if (!btnSearch.isEnabled()) {
				((TransitionDrawable) btnSearch.getBackground()).startTransition(500);
			}
		} else {
			// do the animation only if the button state changes from enable to disable
			if (btnSearch.isEnabled()) {
				((TransitionDrawable) btnSearch.getBackground()).reverseTransition(500);
			}
		}
		// and then update the button state
		btnSearch.setEnabled(isConnected && !isEdtSizeEnough);
	}

	/**
	 * Update the boolean isEdtSizeEnough and then call the manage button function
	 * 
	 * @param edtSize
	 */
	private void setEdtEmpty(int edtSize) {
		if (edtSize > 1) {
			isEdtSizeEnough = false;
		} else {
			isEdtSizeEnough = true;
		}
		// then manage the button status
		manageSearchButtonStatus();
	}
	/***********************************************************
	 *  Actions
	 **********************************************************/
	/**
	 * Launch the search city action
	 */
	private void searchCityAction(){
		//TODO change the button while you wait for the answer
		viewModel.searchCity(edtSearchedCity.getText().toString());
		//indeterminate decorator
		decorator.setIndeterminate();
	}

	/**
	 * Launch the select city action
	 * @param position
	 */
	private void selectCity(int position){
		City city=arrayAdapterCities.getItem(position);
		viewModel.selectCity(city);
	}

	/******************************************************************************************/
	/** Updating data **************************************************************************/
	/******************************************************************************************/

	public void updateCities(List<City> cities){
		//indeterminate decorator
		decorator.setDeterminate();
		arrayAdapterCities.clear();
		if(cities!=null){
			arrayAdapterCities.addAll(cities);
			arrayAdapterCities.notifyDataSetChanged();
		}
		//manage empty answer
		if((cities==null||cities.size()==0)&&edtSearchedCity.getText().length()!=0){
			//empty case
			txvEmptyCase.setVisibility(View.VISIBLE);
			lsvCityList.setVisibility(View.GONE);
		}else{
			//not empty case
			txvEmptyCase.setVisibility(View.GONE);
			lsvCityList.setVisibility(View.VISIBLE);
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEventMainThread(CityAddedEvent event){
		finishView();
	}
	/**
	 * Call this method when the view has to be finished (Activity's notion)
	 */
	public void finishView() {
		//launch the main activity
		Intent launchMainActivity=new Intent(this,CurrentWeatherActivity.class);
		startActivity(launchMainActivity);
		//and die
		finish();
	}

}
