package com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.dao.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.transverse.model.forecast.WeatherForecastItem;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.transverse.model.forecast.WeatherForecatsItemWithMainAndWeathers;

import java.util.List;

@Dao
public interface WeatherForecastItemDao {
    /***********************************************************
     *  LiveData request
     **********************************************************/
    @Query("SELECT * FROM weather_forecast_item ")
    LiveData<List<WeatherForecastItem>> loadAllLiveData();

    @Query("SELECT * FROM weather_forecast_item WHERE _id IN (:id)")
    LiveData<WeatherForecastItem> loadLiveDataById(long id);

    @Query("SELECT * FROM weather_forecast_item WHERE cityId IN (:cityId)")
    LiveData<List<WeatherForecastItem>> loadLiveDataByCityId(long cityId);

    @Query("SELECT * FROM weather_forecast_item WHERE cityId IN (:cityId) AND day_hash IN (:hashDay)")
    List<WeatherForecastItem> loadByCityIdAndHashDay(long cityId,int hashDay);

    @Query("SELECT _id FROM weather_forecast_item WHERE dataTime IN (:dt) AND cityId IN (:cityId)")
    Long loadIdByDateTimeAndCity(long dt,long cityId);


    //Avoid to have _id from weather_forecast_item and main_temp by giving the list of column you want
    @Transaction
    @Query("SELECT * "+
            "FROM weather_forecast_item " +
            "INNER JOIN main_temp ON weather_forecast_item._id = main_temp.weatherForecastItemId " +
            "WHERE cityId IN (:cityId)")
    List<WeatherForecatsItemWithMainAndWeathers> getWeatherForecastItemWithMainAndWeathers(long cityId);

    //Avoid to have _id from weather_forecast_item and main_temp by giving the list of column you want
    @Transaction
    @Query("SELECT * "+
            "FROM weather_forecast_item " +
            "INNER JOIN main_temp ON weather_forecast_item._id = main_temp.weatherForecastItemId " +
            "WHERE cityId IN (:cityId)")
    LiveData<List<WeatherForecatsItemWithMainAndWeathers>> getWeatherForecastItemWithMainAndWeathersLD(long cityId);
    /***********************************************************
     *  Insert
     **********************************************************/
    @Insert
    long[] insertAll(List<WeatherForecastItem> weatherForecastItems);

    @Insert
    long insert(WeatherForecastItem weatherForecastItem);

    /***********************************************************
     *  Update
     **********************************************************/
    @Update
    int update(WeatherForecastItem weatherForecastItem);

    /***********************************************************
     *  Delete
     **********************************************************/
    @Delete
    int delete(WeatherForecastItem weatherForecastItem);

    @Query("DELETE  FROM weather_forecast_item WHERE _id IN (:id)")
    int delete(long id);
    /***********************************************************
     *  For the examples
     **********************************************************/
//    @Query("SELECT * FROM battle WHERE title LIKE :title LIMIT 1")
//    Battle findByName(String title);
//    @Query("SELECT * FROM battle WHERE _id IN (:ids)")
//    List<Battle> loadAllByIds(int[] ids);
//    @Insert
//    long[] insertAll(Battle... battles);
}
