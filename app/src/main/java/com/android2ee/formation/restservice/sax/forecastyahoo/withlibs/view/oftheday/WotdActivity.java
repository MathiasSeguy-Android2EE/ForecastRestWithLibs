package com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.view.oftheday;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.MyApplication;
import com.android2ee.formation.restservice.sax.forecastyahoo.R;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.transverse.model.calculated.WeatherOfTheDay;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.transverse.model.current.City;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.transverse.utils.DayHashCreator;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.transverse.utils.MyLog;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.view.NavigationActivity;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.view.forecast.arrayadapter.LinearLayoutManagerFixed;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.view.generic.indeterminatedecorator.IndeterminateStateWidgetDecorator;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.view.oftheday.adapter.WotdAdapter;

import java.util.List;

/**
 * Created by Created by Mathias Seguy alias Android2ee on 22/06/2018.
 */
public class WotdActivity extends NavigationActivity {
    private static final String TAG = "WotdActivity";
    /***********************************************************
    *  Attributes
    **********************************************************/
    private RecyclerView rcvWeatherOfTheDay;
    private WotdAdapter adapter;
    private WotdActivityModel model;
    /**     * The name of the city on Stage     */
    private String cityName;
    /**
     * The hash of the day today
     */
    private int todayHash;
    /**
     * The position of today in the list
     */
    private int itemOfTodayPosition;
    /**
     * Indterminate decorator
     */
    private IndeterminateStateWidgetDecorator indeterminateDecorator;
    /**
     * Message when no forecast found
     */
    private AppCompatTextView txvEmptyCase;
    /***********************************************************
    *  Managing LifeCycle
    **********************************************************/

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model= ViewModelProviders.of(this).get(WotdActivityModel.class);
        setContentView(R.layout.activity_wotd_entrypoint);
        indeterminateDecorator=new IndeterminateStateWidgetDecorator(findViewById(R.id.content));
        txvEmptyCase=findViewById(R.id.txvEmptyCase);
        rcvWeatherOfTheDay =findViewById(R.id.rcv_weather_of_the_day);
        // use a linear layout manage
        LinearLayoutManager limn=new LinearLayoutManagerFixed(this);
        limn.setOrientation(LinearLayoutManager.VERTICAL);
        rcvWeatherOfTheDay.setLayoutManager(limn);
        // specify an adapter (see also next example)
        adapter = new WotdAdapter(this,model);
        rcvWeatherOfTheDay.setAdapter(adapter);
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //find today Hash
        todayHash= DayHashCreator.getTempKeyFromToday();
        itemOfTodayPosition=0;
        //obsreve
        model.getOnStageCities().observe(this, new Observer<List<City>>() {
                    @Override
                    public void onChanged(@Nullable List<City> cities) {
                        updateCity(cities);
                    }
                }
        );
        if(model.getData()!=null) {
            model.getData().observe(this, new Observer<List<WeatherOfTheDay>>() {
                        @Override
                        public void onChanged(@Nullable List<WeatherOfTheDay> weatherOfTheDays) {
                            updateArrayAdapter(weatherOfTheDays);
                        }
                    }
            );
        }
        indeterminateDecorator.setIndeterminate();
    }

    @Override
    protected void onStop() {
        super.onStop();
        indeterminateDecorator.onStop();
    }

    /***********************************************************
    *  Business Methods
    **********************************************************/
    private void updateCity(List<City> cities){
        MyLog.e(TAG,"update city with"+(cities!=null?cities.size():"0")+" elements");
        if(cities.size()!=0){
            cityName=cities.get(0).getName();
            super.getSupportActionBar().setSubtitle(cityName);
        }else{
            super.getSupportActionBar().setSubtitle("No onStage city found");
        }
    }

    /**
     * @param cityId
     */
    public void selectCity(long cityId) {
        MyLog.e(TAG,"New cityId on stage:"+cityId);
        MyApplication.instance.getServiceManager().getCityService().onStage(cityId);
        //launch the main activity
        Intent launchMainActivity = new Intent(this, WotdActivity.class);
        startActivity(launchMainActivity);
        //and die
        finish();
    }


    /**
     * Is called by this when data are updated from the liveData
     * @param weatherForecatsItemWithMainAndWeathers
     */
    private void updateArrayAdapter(List<WeatherOfTheDay> weatherForecatsItemWithMainAndWeathers){
        indeterminateDecorator.setDeterminate();

        //manage empty answer
        if(weatherForecatsItemWithMainAndWeathers==null||weatherForecatsItemWithMainAndWeathers.size()==0){
            //empty case
            txvEmptyCase.setVisibility(View.VISIBLE);
            rcvWeatherOfTheDay.setVisibility(View.GONE);
        }else{
            //not empty case
            txvEmptyCase.setVisibility(View.GONE);
            rcvWeatherOfTheDay.setVisibility(View.VISIBLE);
            //you just need to updateEntity
            adapter.updateList(weatherForecatsItemWithMainAndWeathers);
            //select the item of the day
            //Display the right item in the middle of the screen
            scrollToToday(weatherForecatsItemWithMainAndWeathers);
        }
    }

    /**
     * Scrool to Today
     * @param weatherForecatsItemWithMainAndWeathers
     */
    private void scrollToToday(List<WeatherOfTheDay> weatherForecatsItemWithMainAndWeathers) {
        //find the day
        for (int i = 0; i < weatherForecatsItemWithMainAndWeathers.size(); i++) {
            if(todayHash==weatherForecatsItemWithMainAndWeathers.get(i).getDayHash()){
                itemOfTodayPosition=i;
                adapter.setItemOfTodayPosition(itemOfTodayPosition);
            }
        }
        //broadcast the today's position to elements
        if(itemOfTodayPosition!=0){
            rcvWeatherOfTheDay.scrollToPosition(itemOfTodayPosition);
        }
    }

    @Override
    protected int getBootomNavAssociatedItemId() {
        return R.id.menu_wotf;
    }
}
