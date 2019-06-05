package com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.dao.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.transverse.model.Weather;

import java.util.List;

@Dao
public interface WeatherDao {
    /***********************************************************
     *  LiveData request
     **********************************************************/
    @Query("SELECT * FROM weather ")
    LiveData<List<Weather>> loadAllLiveData();

    @Query("SELECT * FROM weather WHERE _id IN (:id)")
    LiveData<Weather> loadLiveDataById(long id);

    @Query("SELECT * FROM weather WHERE weatherForecastItemId IN (:weatherForecastItemId)")
    LiveData<List<Weather>> loadLiveDataWeatherForWeatherForecastItem(long weatherForecastItemId);

    @Query("SELECT * FROM weather WHERE weatherForecastItemId IN (:weatherForecastItemIds)")
    LiveData<List<Weather>> loadLiveDataWeatherForWeatherForecastItem(Long[] weatherForecastItemIds);

    @Query("SELECT * FROM weather WHERE weatherForecastItemId IN (:weatherForecastItemId) LIMIT 1")
    LiveData<Weather> loadLiveDataWeatherForWeatherForecastItemUnique(long weatherForecastItemId);

    @Query("SELECT * FROM weather WHERE weatherDataId IN (:weatherDataId)")
    LiveData<List<Weather>> loadLiveDataWeatherForWeatherData(long weatherDataId);

    @Query("SELECT * FROM weather WHERE weatherForecastItemId IN (:weatherForecastItemId)")
    List<Weather> loadWeatherForWeatherForecastItem(long weatherForecastItemId);

    /***********************************************************
     *  Insert
     **********************************************************/
    @Insert
    long[] insertAll(List<Weather> weathers);

    @Insert
    long insert(Weather weather);

    /***********************************************************
     *  Update
     **********************************************************/
    @Update
    int update(Weather weather);

    /***********************************************************
     *  Delete
     **********************************************************/
    @Delete
    int delete(Weather weather);

    @Query("DELETE  FROM weather WHERE _id IN (:id)")
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
