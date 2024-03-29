package com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.dao.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.transverse.model.Main;

import java.util.List;

@Dao
public interface MainDao {
    /***********************************************************
     *  LiveData request
     **********************************************************/
    @Query("SELECT * FROM main_temp ")
    LiveData<List<Main>> loadAllLiveData();

    @Query("SELECT * FROM main_temp WHERE _id_m IN (:id)")
    LiveData<Main> loadLiveDataById(long id);

    @Query("SELECT * FROM main_temp WHERE weatherForecastItemId IN (:weatherForecastItemId)")
    LiveData<List<Main>> loadLiveDataMainForWeatherForecastItems(Long[] weatherForecastItemId);


    @Query("SELECT * FROM main_temp WHERE weatherForecastItemId IN (:weatherForecastItemId) LIMIT 1")
    LiveData<Main> loadLiveDataMainForWeatherForecastItem(long weatherForecastItemId);

    @Query("SELECT * FROM main_temp WHERE weatherDataId IN (:weatherDataId) LIMIT 1")
    LiveData<Main> loadLiveDataMainForWeatherData(long weatherDataId);

    @Query("SELECT * FROM main_temp WHERE weatherForecastItemId IN (:weatherForecastItemId) LIMIT 1")
    Main loadMainForWeatherForecastItems(Long weatherForecastItemId);

    /***********************************************************
     *  Insert
     **********************************************************/
    @Insert
    long[] insertAll(List<Main> mains);

    @Insert
    long insert(Main main);

    /***********************************************************
     *  Update
     **********************************************************/
    @Update
    int update(Main main);

    /***********************************************************
     *  Delete
     **********************************************************/
    @Delete
    int delete(Main main);

    @Delete
    int deleteAll(List<Main> mainList);

    @Query("DELETE  FROM main_temp WHERE _id_m IN (:id)")
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
