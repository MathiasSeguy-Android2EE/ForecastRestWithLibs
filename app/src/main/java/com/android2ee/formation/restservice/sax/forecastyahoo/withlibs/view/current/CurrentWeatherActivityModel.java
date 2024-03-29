package com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.view.current;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import android.graphics.Bitmap;
import androidx.annotation.Nullable;
import android.util.SparseArray;

import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.MyApplication;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.dao.ForecastDatabase;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.service.PictureCacheDownloader;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.transverse.event.PictureLoadedEvent;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.transverse.model.Weather;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.transverse.model.current.City;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.transverse.model.current.WeatherData;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.transverse.utils.MyLog;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.view.MotherViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Created by Mathias Seguy alias Android2ee on 15/06/2018.
 */
public class CurrentWeatherActivityModel extends MotherViewModel {
    private static final String TAG = "CurrentWeatherActivityM";
    /***********************************************************
     *  Managing the WeatherData
     **********************************************************/

    /**
     * The cities displayed/on stage/on screen
     */
    LiveData<List<City>> onStageCities;
    /**
     * The main Live Data of the Use case: This is the main entity represents by the activity
     */
    LiveData<WeatherData> data;
    /**
     * Sub LiveData depending on the data:
     * When the data changes, this live data should request new live stream
     * Because it's a sub list of the object WeatherData
     * But as we manage weatherData as an entity, weatherData.getWeathers==null always
     * So this is the big new pattern to learn: it uses Transformations
     */
    LiveData<List<Weather>> weatherForWeatherData;
    /**
     * The sparse array that maps the holder with the stream of bitmap
     * The goal is to have for each Holder a specific LiveData<Bitmap> for its icon
     * So it updates it accordng to it's icon value
     */
    SparseArray<MutableLiveData<Bitmap>> holderToBitmapLiveData = new SparseArray<>();
    /**
     * For the downloading case: When dowloading, this method is called before the picture is saved on disk
     * When a picture is saved an event is send
     * You lilsten to it, find the MutableLiveData<Bitmap> requesting for it and then update it
     */
    HashMap<String, MutableLiveData<Bitmap>> iconNotFoundNameToBitmapLiveData = new HashMap<>();

    /***********************************************************
     *  Constructors
     **********************************************************/
    @Deprecated
    public CurrentWeatherActivityModel(long cityId) {
        this();
    }

    /***********************************************************
     *  Business Methods
     **********************************************************/
    /**
     * You manage your entity by their id
     * So you need to know which id is the entity you want to manage
     * To do that we listen for the onStageCities stream
     */
    public CurrentWeatherActivityModel() {
        EventBus.getDefault().register(this);
        onStageCities = MyApplication.instance.getServiceManager().getCityService().loadOnStageCities();
        //instanciate your LiveData for your UI
        data = Transformations.switchMap(onStageCities, new Function<List<City>, LiveData<WeatherData>>() {
            @Override
            public LiveData<WeatherData> apply(List<City> input) {
                if (input.size() != 0) {
                    return ForecastDatabase.getInstance().getWeatherDataDao().loadLiveDataCurrentByCityId(input.get(0).get_id());
                } else {
                    return new LiveData<WeatherData>() {
                        @Nullable
                        @Override
                        public WeatherData getValue() {
                            return null;
                        }
                    };
                }
            }
        });
        //transform the main data to the weather List
        //So you said, if the liveData (first paremeter) changes, I rebuild a new LiveData
        //you use switchmap because you change your query (the cityId has changed)
        weatherForWeatherData = Transformations.switchMap(data, new Function<WeatherData, LiveData<List<Weather>>>() {
            @Override
            public LiveData<List<Weather>> apply(WeatherData input) {
                //So you have the new WeatherData live streamed by the livedata
                //and you want to load its list of weathers
                //So you instanciate your new LiveData according to that query:
                //I want the Weathers with the foreign key weatherdata_id equels to weatherData.getId
                //And it's done
                //And the previous liveData unregister from its previous query and plug to the new one
                //because you switchMap (if you had map only, you would have both streams)
                if (input != null) {
                    return ForecastDatabase.getInstance().getWeatherDao().loadLiveDataWeatherForWeatherData(input.get_id());
                } else {
                    return new LiveData<List<Weather>>() {
                        @Nullable
                        @Override
                        public List<Weather> getValue() {
                            return new ArrayList<>();
                        }
                    };
                }
            }
        });
    }
    @Override
    protected void onCleared() {
        super.onCleared();
        holderToBitmapLiveData = null;
        iconNotFoundNameToBitmapLiveData = null;
        EventBus.getDefault().unregister(this);
    }


    /**
     * Donwload the cities data
     */
    public void updateDataOfCitiesOnStage(){
        MyLog.e(TAG,"updateDataOfCitiesOnStage");
        for (City city : onStageCities.getValue()) {
            MyLog.e(TAG,"updateDataOfCitiesOnStage updating the city data of "+city.getName());
            MyApplication.instance.getServiceManager().getWeatherUpdaterService().downloadCurrentWeatherAsync(city.getServerId());
            MyApplication.instance.getServiceManager().getWeatherUpdaterService().downloadForecastWeatherAsync(city.getServerId());
        }
    }


    /***********************************************************
     *  Getters for the Views
     **********************************************************/



    /**
     * Delete the current City
     *
     * @param cityId
     */
    public void deleteCity(long cityId) {
        //Call the service
        MyApplication.instance.getServiceManager().getCityService().deleteCityByIdAsynch(cityId);
    }

    /**
     * The liveData to observe when displaying a WethaerData with the id passed in parameter of the constructor
     *
     * @return the liveData to observe when displaying a WeatherData
     */
    public LiveData<WeatherData> getLiveData() {
        return data;
    }

    /***********************************************************
     *  Managing Loading Image
     **********************************************************/

    /**
     * The liveData to observe when displaying the weathers list of the WethaerData with the id passed in parameter of the constructor
     *
     * @return The liveData to observe when displaying the weathers list
     */
    public LiveData<List<Weather>> getWeather() {
        return weatherForWeatherData;
    }

    /**
     * The liveData to observe when displaying the city of the WethaerData with the id passed in parameter of the constructor
     *
     * @return The liveData to observe when displaying the city
     */
    public LiveData<List<City>> getOnStageCities() {
        return onStageCities;
    }

    /**
     * First find you LiveData and register to it
     * This method provide you the liveData to observe
     *
     * @param holderHash
     * @return
     */
    public MutableLiveData<Bitmap> getIconByHolder(int holderHash) {
        if (holderToBitmapLiveData.get(holderHash) != null) {
            return holderToBitmapLiveData.get(holderHash);
        }
        //else create it and add it to the HashMap
        MutableLiveData<Bitmap> current = new MutableLiveData<Bitmap>();
        holderToBitmapLiveData.put(holderHash, current);
        return current;
    }

    /**
     * Then update the LiveData the Holder is observing, it will changes its icon
     *
     * @param holderHash
     * @param iconName
     */
    public void updateIcon(int holderHash, String iconName) {
        MutableLiveData<Bitmap> current = holderToBitmapLiveData.get(holderHash);
        if (current != null) {
            current.postValue(PictureCacheDownloader.loadPictureFromDisk(iconName));
            iconNotFoundNameToBitmapLiveData.put(iconName, current);
        }
    }

    /**
     * For the downloading case: When dowloading, this method is called before the picture is saved on disk
     * When a picture is saved an event is send
     * You lilsten to it, find the MutableLiveData<Bitmap> requesting for it and then update it
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(PictureLoadedEvent event) {
        String iconName=event.getPictureName();
        //you have found
        MutableLiveData<Bitmap> current = iconNotFoundNameToBitmapLiveData.get(iconName);
        if (current != null) {
            current.postValue(PictureCacheDownloader.loadPictureFromDisk(iconName));
        }
    }


}
