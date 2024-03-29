/**
 * <ul>
 * <li>ConnectedDataCommunication</li>
 * <li>com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.com.retrofit</li>
 * <li>24/02/2016</li>
 * <p/>
 * <li>======================================================</li>
 * <p/>
 * <li>Projet : Mathias Seguy Project</li>
 * <li>Produit par MSE.</li>
 * <p/>
 * /**
 * <ul>
 * Android Tutorial, An <strong>Android2EE</strong>'s project.</br>
 * Produced by <strong>Dr. Mathias SEGUY</strong>.</br>
 * Delivered by <strong>http://android2ee.com/</strong></br>
 * Belongs to <strong>Mathias Seguy</strong></br>
 * ***************************************************************************************************************</br>
 * This code is free for any usage but can't be distribute.</br>
 * The distribution is reserved to the site <strong>http://android2ee.com</strong>.</br>
 * The intelectual property belongs to <strong>Mathias Seguy</strong>.</br>
 * <em>http://mathias-seguy.developpez.com/</em></br> </br>
 * <p/>
 * *****************************************************************************************************************</br>
 * Ce code est libre de toute utilisation mais n'est pas distribuable.</br>
 * Sa distribution est reservée au site <strong>http://android2ee.com</strong>.</br>
 * Sa propriété intellectuelle appartient à <strong>Mathias Seguy</strong>.</br>
 * <em>http://mathias-seguy.developpez.com/</em></br> </br>
 * *****************************************************************************************************************</br>
 */

package com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.com.retrofit;

import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.MyApplication;
import com.android2ee.formation.restservice.sax.forecastyahoo.R;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.com.DataCommunicationIntf;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.transverse.exception.ExceptionManaged;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.transverse.exception.ExceptionManager;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.transverse.model.current.FindCitiesResponse;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.transverse.model.current.WeatherData;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.transverse.model.forecast.Forecast;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.transverse.utils.MyLog;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Mathias Seguy - Android2EE on 24/02/2016.
 * This class used retrofit to make Http calls
 */
public class ConnectedDataCommunication implements DataCommunicationIntf {
    private static final String TAG = "ConDataCom";

    /**
     * The call to find city using its name
     */
    Call<FindCitiesResponse>  findCityByNameCall = null;
    /**
     * The call to find the weather of a city according the city Id
     */
    Call<WeatherData> findWeatherByCityIdCall = null;
    /**
     * The call to find the forecast of a city according the city Id
     */
    Call<Forecast> findForecastByCityIdCall = null;
    /**
     * The Retrofit service to make call
     */
    RetrofitServiceIntf webServiceComplex;

    /**
     * Constructor
     */
    public ConnectedDataCommunication() {
        MyLog.e(TAG, "ConnectedDataCommunication() called with: " + "");
        webServiceComplex = RetrofitBuilder.getComplexClient(MyApplication.instance);
    }

    @Override
    public FindCitiesResponse getCitiesByName(String cityName) {
        MyLog.e(TAG, "findCityByName() called with: " + "cityName = [" + cityName + "]");
        try {
            findCityByNameCall=webServiceComplex.findCityByName(cityName);
            Response<com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.transverse.model.current.FindCitiesResponse> resp=findCityByNameCall.execute();
            if(resp.code()==200){
                MyLog.e(TAG, "findCityByName() called with: " + "cityName = [" + cityName + "] response 200 Object ="+resp.body());
                return resp.body();
            }else{
                MyLog.e(TAG, "findCityByName() called with: " + "cityName = [" + cityName + "] failure response "+resp.code());
                ExceptionManager.manage(new ExceptionManaged(ConnectedDataCommunication.class, R.string.datacom_findcity_ioexc));
            }
        } catch (IOException e) {
            ExceptionManager.manage(new ExceptionManaged(ConnectedDataCommunication.class, R.string.datacom_findcity_ioexc,e));
        }
        return null;
    }

    @Override
    public WeatherData getWeatherByCityServerId(long cityId) {
        MyLog.e(TAG, "findWeatherByCityServerId() called with: " + "cityId = [" + cityId + "]");
        try {
            findWeatherByCityIdCall=webServiceComplex.findWeatherByCityServerId(cityId);
            return findWeatherByCityIdCall.execute().body();
        } catch (IOException e) {
            ExceptionManager.manage(new ExceptionManaged(ConnectedDataCommunication.class, R.string.datacom_findcity_ioexc,e));
        }
        return null;
    }

    @Override
    public Forecast getForecastByCityId(long cityId) {
        MyLog.e(TAG, "findForecastByCityId() called with: " + "cityId = [" + cityId + "]");
        try {
            findForecastByCityIdCall=webServiceComplex.findForecastByCityId(cityId);
            Forecast forecast=findForecastByCityIdCall.execute().body();
//            MyLog.e(TAG,forecast.toString());
//            int maxLogSize = 100;
//            String longString=forecast.toString();
//            for(int i = 0; i <= longString.length() / maxLogSize; i++) {
//                int start = i * maxLogSize;
//                int end = (i+1) * maxLogSize;
//                end = end > longString.length() ? longString.length() : end;
//                MyLog.e(TAG, longString.substring(start, end));
//            }
            MyLog.e(TAG,"Too long?");
            return forecast;
        } catch (IOException e) {
            ExceptionManager.manage(new ExceptionManaged(ConnectedDataCommunication.class, R.string.datacom_findcity_ioexc,e));
        }
        return null;
    }
}
