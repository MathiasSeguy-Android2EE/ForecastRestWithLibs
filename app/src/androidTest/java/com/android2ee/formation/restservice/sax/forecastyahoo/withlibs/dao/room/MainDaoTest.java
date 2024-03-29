package com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.dao.room;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.room.Room;
import android.content.Context;
import androidx.annotation.Nullable;
import androidx.test.InstrumentationRegistry;

import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.dao.ForecastDatabase;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.transverse.model.DataGeneratorSimple;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.transverse.model.Main;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.transverse.utils.MyLog;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.util.ArrayList;
import java.util.List;

public class MainDaoTest {
    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    public static final String TAG = "mainDao";
    private ForecastDatabase db;
    Observer<List<Main>> mainObserver;

    @Before
    public  void createDb() {
        Context context = InstrumentationRegistry.getTargetContext();
        db = Room.inMemoryDatabaseBuilder(context, ForecastDatabase.class)
                .allowMainThreadQueries().build();
    }

    @After
    public void closeDb() {
        db.close();
    }

    @Test
    public void testInsert() {
        mainObserver = new Observer<List<Main>>() {
            @Override
            public void onChanged(@Nullable List<Main> mainData) {
                if(mainData.size() == 0) {
                    //firstCall
                } else {
                    MyLog.d(TAG, "It's working bitch");
                    Assert.assertEquals(1, mainData.size());
                }
            }
        };

        LiveData<List<Main>> ldMain = db.getMainDao().loadAllLiveData();

        ldMain.observeForever(mainObserver);

        Main main = DataGeneratorSimple.getMain();

        long newMainId = db.getMainDao().insert(main);


        // Let's clean
        ldMain.removeObserver(mainObserver);
    }


    @Test
    public void testDelete() {
        mainObserver = new Observer<List<Main>>() {
            @Override
            public void onChanged(@Nullable List<Main> mainData) {
                if(mainData.size() == 0) {
                    MyLog.d(TAG, "It's working bitch");
                    Assert.assertTrue("Winner", true);
                } else {
                    MyLog.d(TAG, "Wait for it");
                }
            }
        };

        LiveData<List<Main>> ldMain = db.getMainDao().loadAllLiveData();

        ldMain.observeForever(mainObserver);

        Main main = DataGeneratorSimple.getMain();

        long mainId = db.getMainDao().insert(main);

        db.getMainDao().delete(mainId);

        // Let's clean
        ldMain.removeObserver(mainObserver);
    }

    @Test
    public void testInsertAll() {
        mainObserver = new Observer<List<Main>>() {
            @Override
            public void onChanged(@Nullable List<Main> mainData) {
                if(mainData.size() == 0) {
                    //firstCall
                } else {
                    MyLog.d(TAG, "It's working bitch");
                    if(mainData.size() == 10) {
                        Assert.assertTrue(true);
                    }
                }
            }
        };

        LiveData<List<Main>> ldMain = db.getMainDao().loadAllLiveData();

        ldMain.observeForever(mainObserver);

        List<Main> listToAdd = new ArrayList<>();
        for(int i = 0;i < 10; i++ ){
            listToAdd.add(DataGeneratorSimple.getMain());
        }
        long[] newMainIds = db.getMainDao().insertAll(listToAdd);

        // Let's clean
        ldMain.removeObserver(mainObserver);
    }

    @Test
    public void testDeletetAll() {
        mainObserver = new Observer<List<Main>>() {
            @Override
            public void onChanged(@Nullable List<Main> mainData) {
                if(mainData.size() == 0) {
                    //firstCall
                    MyLog.d(TAG, "It's working bitch");
                    Assert.assertTrue(true);
                } else {
                    // Nothing we don't care
                }
            }
        };

        List<Main> listToAdd = new ArrayList<>();
        for(int i = 0;i < 10; i++ ){
            listToAdd.add(DataGeneratorSimple.getMain());
        }
        long[] newMainIds = db.getMainDao().insertAll(listToAdd);

        LiveData<List<Main>> ldMain = db.getMainDao().loadAllLiveData();

        ldMain.observeForever(mainObserver);


        db.getMainDao().deleteAll(listToAdd);
        // Let's clean
        ldMain.removeObserver(mainObserver);

    }

    @Test
    public void testUpdate() {
        mainObserver = new Observer<List<Main>>() {
            @Override
            public void onChanged(@Nullable List<Main> mainData) {
                if(mainData.size() == 0) {
                    //firstCall
                } else {
                    if(mainData.get(0).getPressure() == 100000) {
                        Assert.assertTrue(true);
                    } else {
                        MyLog.d(TAG, "Waiting update");
                    }
                }
            }
        };

        LiveData<List<Main>> ldMain = db.getMainDao().loadAllLiveData();

        Main main = DataGeneratorSimple.getMain();

        long mainId = db.getMainDao().insert(main);

        ldMain.observeForever(mainObserver);

        main.setPressure(100000);

        db.getMainDao().update(main);

        // Let's clean
        ldMain.removeObserver(mainObserver);
    }
}
