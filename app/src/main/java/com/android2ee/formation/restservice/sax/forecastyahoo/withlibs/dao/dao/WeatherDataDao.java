package com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.dao.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.transverse.model.current.WeatherData;

import java.util.List;

@Dao
public interface WeatherDataDao {
    /***********************************************************
     *  LiveData request
     **********************************************************/
    @Query("SELECT * FROM weather_data_current ")
    LiveData<List<WeatherData>> loadAllLiveData();

    @Query("SELECT * FROM weather_data_current WHERE _id IN (:id)")
    LiveData<WeatherData> loadLiveDataById(long id);

    @Query("SELECT * FROM weather_data_current WHERE cityId IN (:cityId)")
    LiveData<List<WeatherData>> loadLiveDataByCityId(long cityId);
    @Query("SELECT * FROM weather_data_current WHERE cityId IN (:cityServerId) ORDER BY dateTime DESC LIMIT 1 ")
    LiveData<WeatherData> loadLiveDataCurrentByCityId(long cityServerId);
    /***********************************************************
     *  Insert
     **********************************************************/
    @Insert
    long[] insertAll(List<WeatherData> weatherData);

    @Insert
    long insert(WeatherData weatherData);

    /***********************************************************
     *  Update
     **********************************************************/
    @Update
    int update(WeatherData weatherData);

    /***********************************************************
     *  Delete
     **********************************************************/
    @Delete
    int delete(WeatherData weatherData);

    @Query("DELETE  FROM weather_data_current WHERE _id IN (:id)")
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
