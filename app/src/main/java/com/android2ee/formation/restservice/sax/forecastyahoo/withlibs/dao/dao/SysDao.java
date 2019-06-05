package com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.dao.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.transverse.model.current.Sys;

import java.util.List;

@Dao
public interface SysDao {
    /***********************************************************
     *  LiveData request
     **********************************************************/
    @Query("SELECT * FROM sys_current ")
    LiveData<List<Sys>> loadAllLiveData();

    @Query("SELECT * FROM sys_current WHERE _id IN (:id)")
    LiveData<Sys> loadLiveDataById(long id);

//    @Query("SELECT * FROM sys_current WHERE weatherForecastItemId IN (:weatherForecastItemId)")
//    LiveData<List<Sys>> loadLiveDataMainForWeatherForecastItem(long weatherForecastItemId);

    @Query("SELECT * FROM sys_current WHERE weatherDataId IN (:weatherDataId)")
    LiveData<Sys> loadLiveDataSysByWeatherDataId(long weatherDataId);

    /***********************************************************
     *  Insert
     **********************************************************/
    @Insert
    long[] insertAll(List<Sys> sys);

    @Insert
    long insert(Sys sys);

    /***********************************************************
     *  Update
     **********************************************************/
    @Update
    int update(Sys sys);

    /***********************************************************
     *  Delete
     **********************************************************/
    @Delete
    int delete(Sys city);

    @Query("DELETE  FROM sys_current WHERE _id IN (:id)")
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
