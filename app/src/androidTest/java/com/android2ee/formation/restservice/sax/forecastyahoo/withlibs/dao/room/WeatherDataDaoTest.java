package com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.dao.room;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.room.Room;
import android.content.Context;
import androidx.annotation.Nullable;
import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.dao.ForecastDatabase;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.dao.dao.WeatherDao;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.transverse.model.Clouds;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.transverse.model.DataGeneratorSimple;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.transverse.model.Snow;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.transverse.model.Weather;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.transverse.model.current.City;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.transverse.model.current.WeatherData;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.transverse.model.forecast.WeatherForecastItem;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.transverse.utils.MyLog;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;


/**
 * Created by Created by Mathias Seguy alias Android2ee on 13/06/2018.
 * https://stackoverflow.com/questions/44270688/unit-testing-room-and-livedata
 */
@RunWith(AndroidJUnit4.class)
public class WeatherDataDaoTest {

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    private static final String TAG = "WeatherDataDaoTest";
    private ForecastDatabase db;
    Observer<List<WeatherData>> weatherDataObserver;
    Observer<List<WeatherForecastItem>> weatherForecastItemObserver;

    private int cptWeatherDataCall = 0;
    private int cptWeatherForecastItemCall = 0;

    @Before
    public void createDb() {
        cptWeatherDataCall = 0;
        cptWeatherForecastItemCall = 0;

        Context context = InstrumentationRegistry.getTargetContext();
        db = Room.inMemoryDatabaseBuilder(context, ForecastDatabase.class)
                .allowMainThreadQueries().build();
        //populate with the data set
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void testPopulate(){
        //declare the observer
        weatherDataObserver=new Observer<List<WeatherData>>() {
            @Override
            public void onChanged(@Nullable List<WeatherData> weatherData) {
                if(weatherData.size()==0){
                    //first call
                }else{
                    MyLog.e(TAG,"Toto est un roi");
                    Assert.assertEquals(1,weatherData.size());
                }
            }
        };
        weatherForecastItemObserver=new Observer<List<WeatherForecastItem>>() {
            @Override
            public void onChanged(@Nullable List<WeatherForecastItem> weatherForecastItems) {
                if(weatherForecastItems.size()==0){
                    //first call
                }else{
                    MyLog.e(TAG,"Toto est un roi deux");
                    Assert.assertEquals(1,weatherForecastItems.size());
                }
            }
        };

        //declare the LiveData you listen,
        LiveData<List<WeatherData>> lvWeatherData=db.getWeatherDataDao().loadAllLiveData();
        LiveData<List<WeatherForecastItem>> lvWFI=db.getWeatherForecastItemDao().loadAllLiveData();

        //bind the observer to the live data
        lvWFI.observeForever(weatherForecastItemObserver);
        lvWeatherData.observeForever(weatherDataObserver);

        /**Make you insertion or DB stuff*/
        //create the City (no city no foreign key, no weatherData)
        City city= DataGeneratorSimple.getCity("Palaminy");
        long cityId=db.getCityDao().insert(city);
        //create your WeatherData object
        WeatherData wData= populateWithAWeatherData(cityId);
        //create your WeatherForecastItem object
        WeatherForecastItem wForecast= populateWithAWeatherForecastItem(cityId);
        //then check your insertion

        //Remove observer
        //it works because you allow the main thread query:allowMainThreadQueries
        lvWeatherData.removeObserver(weatherDataObserver);
        lvWFI.removeObserver(weatherForecastItemObserver);
    }

    @Test
    public void testUpdate() {
        //declare the observer
        weatherDataObserver=new Observer<List<WeatherData>>() {
            @Override
            public void onChanged(@Nullable List<WeatherData> weatherData) {
                cptWeatherDataCall++;

                // First time = null, Second time = inserted, Third time = updated
                if (weatherData != null && cptWeatherDataCall == 3) {
                    MyLog.e(TAG,"Hello motherfucker");
                    Assert.assertEquals("TheTotoBase", weatherData.get(0).getBase());
                    Assert.assertEquals(42, weatherData.get(0).getClouds().getAll());
                }
            }
        };
        weatherForecastItemObserver=new Observer<List<WeatherForecastItem>>() {
            @Override
            public void onChanged(@Nullable List<WeatherForecastItem> weatherForecastItems) {
                cptWeatherForecastItemCall++;

                // First time = null, Second time = inserted, Third time = updated
                if (weatherForecastItems != null && cptWeatherForecastItemCall == 3) {
                    MyLog.e(TAG,"Hello motherfucker");
                    Assert.assertEquals(0.03f, weatherForecastItems.get(0).getSnow().get3h());
                }
            }
        };

        //declare the LiveData you listen,
        LiveData<List<WeatherData>> lvWeatherData=db.getWeatherDataDao().loadAllLiveData();
        LiveData<List<WeatherForecastItem>> lvWFI=db.getWeatherForecastItemDao().loadAllLiveData();

        //bind the observer to the live data
        lvWFI.observeForever(weatherForecastItemObserver);
        lvWeatherData.observeForever(weatherDataObserver);

        /**Make you insertion or DB stuff*/
        //create the City (no city no foreign key, no weatherData)
        City city= DataGeneratorSimple.getCity("Palaminy");
        long cityId=db.getCityDao().insert(city);
        //create your WeatherData object
        WeatherData wData= populateWithAWeatherData(cityId);
        //create your WeatherForecastItem object
        WeatherForecastItem wForecast= populateWithAWeatherForecastItem(cityId);

        //update your WeatherData object
        wData.setBase("TheTotoBase");
        wData.setClouds(new Clouds(42));

        // Update your WeatherForecastItem object
        wForecast.setSnow(new Snow(0.03f));

        // Let's go!
        try {
            db.getWeatherDataDao().update(wData);
            db.getWeatherForecastItemDao().update(wForecast);
        } catch (Exception e) {
            Assert.fail("Exception while updating: " + e.getMessage());
        }

        //Remove observer
        //it works because you allow the main thread query:allowMainThreadQueries
        lvWeatherData.removeObserver(weatherDataObserver);
        lvWFI.removeObserver(weatherForecastItemObserver);
    }


    @Test
    public void testDelete(){
        //declare the observer
        weatherDataObserver=new Observer<List<WeatherData>>() {
            @Override
            public void onChanged(@Nullable List<WeatherData> weatherData) {
                cptWeatherDataCall++;

                // First time = null, Second time = inserted, Third time = deleted
                if (weatherData != null && cptWeatherDataCall == 3) {
                    MyLog.e(TAG,"Hello motherfucker");
                    Assert.assertEquals(0, weatherData.size());
                }
            }
        };
        weatherForecastItemObserver=new Observer<List<WeatherForecastItem>>() {
            @Override
            public void onChanged(@Nullable List<WeatherForecastItem> weatherForecastItems) {
                cptWeatherForecastItemCall++;
                // First time = null, Second time = inserted, Third time = deleted
                if (weatherForecastItems != null && cptWeatherForecastItemCall == 3) {
                    MyLog.e(TAG,"Hello motherfucker");
                    Assert.assertEquals(0, weatherForecastItems.size());
                }
            }
        };

        //declare the LiveData you listen,
        LiveData<List<WeatherData>> lvWeatherData=db.getWeatherDataDao().loadAllLiveData();
        LiveData<List<WeatherForecastItem>> lvWFI=db.getWeatherForecastItemDao().loadAllLiveData();

        //bind the observer to the live data
        lvWFI.observeForever(weatherForecastItemObserver);
        lvWeatherData.observeForever(weatherDataObserver);

        /**Make you insertion or DB stuff*/
        //create the City (no city no foreign key, no weatherData)
        City city= DataGeneratorSimple.getCity("Palaminy");
        long cityId=db.getCityDao().insert(city);
        city.set_id(cityId);
        //create your WeatherData object
        WeatherData wData= populateWithAWeatherData(cityId);
        //create your WeatherForecastItem object
        WeatherForecastItem wForecast= populateWithAWeatherForecastItem(cityId);
        //then check your insertion

        // Let's go!
        try {
            db.getWeatherDataDao().delete(wData);
            db.getWeatherForecastItemDao().delete(wForecast);
        }catch (Exception e) {
            Assert.fail("Exception while deleting: " + e.getMessage());
        }

        //Remove observer
        //it works because you allow the main thread query:allowMainThreadQueries
        lvWeatherData.removeObserver(weatherDataObserver);
        lvWFI.removeObserver(weatherForecastItemObserver);
    }

    /**
     * Populate the DB with a WeatherData object
     * @param cityId
     */
    private WeatherData populateWithAWeatherData(long cityId) {
        WeatherData weatherData=DataGeneratorSimple.getWeatherData(cityId);
        //in fact no need the fake object is already created with this id
        //but in real life you have to make it, so I keep it here
        weatherData.setCity_Id(cityId);
        //save it
        long weatherDataId=db.getWeatherDataDao().insert(weatherData);
        weatherData.set_id(weatherDataId);
        //then persist the sub object
        //Persist Main
        weatherData.getMain().setWeatherDataId(weatherDataId);
        //important trick as you have 2 foreign key,
        //the on not used should be set to null
        weatherData.getMain().setWeatherForecastItemId(null);
        db.getMainDao().insert(weatherData.getMain());

        //Persist the sys object
        weatherData.getSys().setWeatherDataId(weatherDataId);
        db.getSysDao().insert(weatherData.getSys());

        //persist weather list
        WeatherDao weatherDao=db.getWeatherDao();
        for (Weather weather : weatherData.getWeather()) {
            weather.setWeatherDataId(weatherDataId);
            weather.setWeatherForecastItemId(null);
            weatherDao.insert(weather);
        }
        return weatherData;
    }
    /**
     * Populate the DB with a WeatherData object
     * @param cityId
     */
    private WeatherForecastItem populateWithAWeatherForecastItem(long cityId) {
        WeatherForecastItem weatherForecastItem=DataGeneratorSimple.getWeatherForecastItem(cityId);
        //in fact no need the fake object is already created with this id
        //but in real life you have to make it, so I keep it here
        weatherForecastItem.setCity_Id(cityId);
        //save it
        long weatherDataId=db.getWeatherForecastItemDao().insert(weatherForecastItem);
        weatherForecastItem.set_id(weatherDataId);
        //then persist the sub object
        //Persist Main
        weatherForecastItem.getMain().setWeatherForecastItemId(weatherDataId);
        //important trick as you have 2 foreign key,
        //the on not used should be set to null
        weatherForecastItem.getMain().setWeatherDataId(null);
        db.getMainDao().insert(weatherForecastItem.getMain());

        //persist weather list
        WeatherDao weatherDao=db.getWeatherDao();
        for (Weather weather : weatherForecastItem.getWeather()) {
            weather.setWeatherDataId(weatherDataId);
            weather.setWeatherForecastItemId(null);
            weatherDao.insert(weather);
        }
        return weatherForecastItem;
    }

}
