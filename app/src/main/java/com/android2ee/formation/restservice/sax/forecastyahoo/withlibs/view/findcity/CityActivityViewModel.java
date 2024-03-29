package com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.view.findcity;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.MyApplication;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.transverse.model.current.City;

import java.util.List;

/**
 * Created by Created by Mathias Seguy alias Android2ee on 14/06/2018.
 */
public class CityActivityViewModel extends ViewModel {
    /**
     * The LiveData used by the Views to displayed the cities found
     */
    MutableLiveData<List<City>> citiesFoundLiveData;
    /***********************************************************
    *  Constructors
    **********************************************************/
    public CityActivityViewModel() {
        citiesFoundLiveData =  MyApplication.instance.getServiceManager().getCityService().getLiveFindCitiesResponse();
    }
    /***********************************************************
     *  Answering and Getting the answer of the Query find Cities
     **********************************************************/
    /**
     * The LiveData to listen for find cities http request answer
     * @return
     */
    public MutableLiveData<List<City>> getCitiesFoundLiveData() {
        return citiesFoundLiveData;
    }
    /**
     * Launch the Http Query
     * @param cityName
     */
    public void searchCity(String cityName){
        MyApplication.instance.getServiceManager().getCityService().findCityByNameAsync(cityName);
    }
    /**
     * Selecting a City
     * @param city
     */
    public  void selectCity(City city){
        MyApplication.instance.getServiceManager().getCityService().addCityAsync(city);
        MyApplication.instance.getServiceManager().getWeatherUpdaterService().downloadCurrentWeatherAsync(city.getServerId());
        MyApplication.instance.getServiceManager().getWeatherUpdaterService().downloadForecastWeatherAsync(city.getServerId());
    }
}
