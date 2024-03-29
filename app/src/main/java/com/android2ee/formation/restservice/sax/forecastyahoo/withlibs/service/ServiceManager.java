/**
 * <ul>
 * <li>ServiceManager</li>
 * <li>com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.service</li>
 * <li>25/02/2016</li>
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

package com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.service;

/**
 * Created by Mathias Seguy - Android2EE on 25/02/2016.
 */

import com.android2ee.formation.restservice.sax.forecastyahoo.BuildConfig;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.MyApplication;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.service.city.CityService;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.service.city.CityServiceIntf;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.service.weather.ForecastRepository;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.service.weather.ForecastRepositoryIntf;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.service.weather.WeatherDataUpdater;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.service.weather.WeatherDataUpdaterIntf;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.service.weather.WeatherOfTheDayRepository;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.service.weather.WeatherOfTheDayRepositoryIntf;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.transverse.utils.MyLog;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author Mathias Seguy (Android2EE)
 * @goals This class aims to manage services
 * It can be access only through MyApplication object
 * by calling MyApplication.getServiceManager()
 */
public class ServiceManager implements ServiceManagerIntf {
    private static final String TAG = "ServiceManager";
    /***********************************************************
     *  Services List
     **********************************************************/
    ArrayList<MotherBusinessServiceIntf> motherBusinessServices;
    /**
     * The  Forecast Updater service
     */
    WeatherDataUpdaterIntf weatherUpdaterService = null;
    /**
     * The  Forecast Repository
     */
    ForecastRepositoryIntf forecastRepository = null;
    /**
     * The  Forecast Repository
     */
    WeatherOfTheDayRepositoryIntf weatherOfTheDayRepository = null;


    /**
     * The  City  service
     */
    CityServiceIntf cityServiceIntf = null;
     /***********************************************************
      *  Constructor and destructor
      **********************************************************/
    /**
     * Insure only the Application object can instantiate once this object
     * If not the case throw an Exception
     */
    public ServiceManager(MyApplication application) {
        if(BuildConfig.FLAVOR.equals("mock")){
            //do what you want
            MyLog.e(TAG,"Mock Mode on ServiceManager initialization");
        }else if (application.serviceManagerAlreadyExist()) {
            throw new ExceptionInInitializerError();
        }
        motherBusinessServices=new ArrayList<MotherBusinessServiceIntf>();
    }
    /**
     * To be called when you need to release all the services
     * Is managed by the MyApplication object in fact
     */
    @Override
    public void unbindAndDie() {
        MyLog.e("ServiceManager", "UnbindAndDie is called");
        //kill your thread
        if (cancelableThreadsExecutor != null) {
            killCancelableThreadExecutor();
        }
        if (keepAliveThreadsExceutor != null) {
            killKeepAliveThreadExecutor();
        }
        //kill your business services
        for(MotherBusinessServiceIntf service:motherBusinessServices){
            service.onDestroy(this);
        }
        //release your pointer
        weatherUpdaterService=null;
        forecastRepository=null;
        cityServiceIntf =null;
    }

    /***********************************************************
     *  Services Getters
     **********************************************************/
    /**
     * @return the forecastServiceData
     */
    @Override
    public final WeatherDataUpdaterIntf getWeatherUpdaterService() {
        if (null == weatherUpdaterService) {
            weatherUpdaterService = new WeatherDataUpdater(this);
            motherBusinessServices.add(weatherUpdaterService);
        }
        return weatherUpdaterService;
    }

    @Override
    public ForecastRepositoryIntf getForecastRepository(){
        if (null == forecastRepository) {
            forecastRepository = new ForecastRepository(this);
            motherBusinessServices.add(forecastRepository);
        }
        return forecastRepository;
    }
    /**
     * @return the cityService
     */
    @Override
    public final CityServiceIntf getCityService() {
        if (null == cityServiceIntf) {
            cityServiceIntf = new CityService(this);
            motherBusinessServices.add(cityServiceIntf);
        }
        return cityServiceIntf;
    }

    /**
     * @return the WeatherOfTheDayRepository
     */
    @Override
    public WeatherOfTheDayRepositoryIntf getWeatherOfTheDayRepository() {
        if (null == weatherOfTheDayRepository) {
            weatherOfTheDayRepository = new WeatherOfTheDayRepository(this);
            motherBusinessServices.add(weatherOfTheDayRepository);
        }
        return weatherOfTheDayRepository;
    }
    /******************************************************************************************/
    /** Pool Executor for Threads that has to cancelled when the application shutdown**/
    /******************************************************************************************/
    /**
     * The pool executor to use for all cancellable thread and Threads that has to cancelled when the application shutdown
     */
    private ExecutorService cancelableThreadsExecutor = null;

    /**
     * @return the cancelableThreadsExceutor
     */
    @Override
    public final ExecutorService getCancelableThreadsExecutor() {
        if (cancelableThreadsExecutor == null) {
            cancelableThreadsExecutor = Executors.newFixedThreadPool(12, new CancelableThreadFactory());
        }
        return cancelableThreadsExecutor;
    }

    /** * And its associated factory */
    private class CancelableThreadFactory implements ThreadFactory {
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setName("CancelableThread"+((int)(Math.random()*1000)));
            return t;
        }
    }

    /**
     * Kill all running Thread and destroy then all
     * Kill the cancelableThreadsExceutor
     */
    private void killCancelableThreadExecutor() {
        if (cancelableThreadsExecutor != null) {
            cancelableThreadsExecutor.shutdownNow(); // Disable new tasks from being submitted and kill every running task using Thread.interrupt
            try {// as long as your threads hasn't finished
                while (!cancelableThreadsExecutor.isTerminated()) {
                    // Wait a while for existing tasks to terminate
                    if (!cancelableThreadsExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                        // Cancel currently executing tasks
                        cancelableThreadsExecutor.shutdownNow();
                        MyLog.e("MyApp", "Probably a memory leak here");
                    }
                }
            } catch (InterruptedException ie) {
                // (Re-)Cancel if current thread also interrupted
                cancelableThreadsExecutor.shutdownNow();
                cancelableThreadsExecutor=null;
                MyLog.e("MyApp", "Probably a memory leak here too");
            }
        }
        cancelableThreadsExecutor=null;
    }
    /******************************************************************************************/
    /** Pool Executor for Threads that has to finish they threatment when the application shutdown**/
    /******************************************************************************************/
    /**
     * The pool executor to use for all cancellable thread and Threads that has to cancelled when the application shutdown
     */
    private ExecutorService keepAliveThreadsExceutor = null;

    /**
     * @return the cancelableThreadsExceutor
     */
    @Override
    public final ExecutorService getKeepAliveThreadsExecutor() {
        if (keepAliveThreadsExceutor == null) {
            keepAliveThreadsExceutor = Executors.newFixedThreadPool(12, new BackgroundThreadFactory());
        }
        return keepAliveThreadsExceutor;
    }
    /** * And its associated factory */
    private class BackgroundThreadFactory implements ThreadFactory {
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setName("KeepAlive"+((int)(Math.random()*1000)));
            return t;
        }
    }

    /**
     * Kill all running Thread and destroy then all
     * Kill the cancelableThreadsExceutor
     */
    private void killKeepAliveThreadExecutor() {
        if (keepAliveThreadsExceutor != null) {
            keepAliveThreadsExceutor.shutdown(); // Disable new tasks from being submitted
            try {// as long as your threads hasn't finished
                while (!keepAliveThreadsExceutor.isTerminated()) {
                    // Wait a while for existing tasks to terminate
                    if (!keepAliveThreadsExceutor.awaitTermination(5, TimeUnit.SECONDS)) {
                        // Cancel currently executing tasks
                        keepAliveThreadsExceutor.shutdown();
                        MyLog.e("MyApp", "Probably a memory leak here");
                    }
                }
            } catch (InterruptedException ie) {
                // (Re-)Cancel if current thread also interrupted
                keepAliveThreadsExceutor.shutdownNow();
                keepAliveThreadsExceutor=null;
                MyLog.e("MyApp", "Probably a memory leak here too");
            }
        }
        keepAliveThreadsExceutor=null;
    }
}

