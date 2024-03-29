
package com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.transverse.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.Nullable;

import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.transverse.model.current.WeatherData;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.transverse.model.forecast.WeatherForecastItem;
import com.squareup.moshi.Json;

/**
 * This is the main weather data
 */
@Entity(tableName="main_temp",
        indices = {
                @Index(value = {"weatherDataId"},unique = false),
                @Index(value = {"weatherForecastItemId"},unique = false)},
        foreignKeys = {@ForeignKey(entity = WeatherData.class,
                    parentColumns = "_id",
                    childColumns = "weatherDataId",
                    onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = WeatherForecastItem.class,
                    parentColumns = "_id",
                    childColumns = "weatherForecastItemId",
                            onDelete = ForeignKey.CASCADE)}
                )
public class Main{
    @PrimaryKey(autoGenerate = true)
    private long _id_m;
    @ColumnInfo(name="temp")
    private float temp;
    @ColumnInfo(name="pressure")
    private float pressure;
    @ColumnInfo(name="humidity")
    private int humidity;
    /**
     * accuracy of temp (min value)
     */
    @Json(name = "temp_min")
    @ColumnInfo(name="temp_min")
    private float tempMin;
    /**
     * accuracy of temp (min value)
     */
    @Json(name = "temp_max")
    @ColumnInfo(name="temp_max")
    private float tempMax;
    @ColumnInfo(name="sea_level")
    private float sea_level;
    @ColumnInfo(name="grnd_level")
    private float grnd_level;
    /***********************************************************
     *  Foreign Key
     *important trick as you have 2 foreign key, but only one will be defined
     *for any instance, so put a Long instead of long and Nullable
     **********************************************************/
    @ColumnInfo(name="weatherDataId")
    @Nullable
    private Long weatherDataId;
    @ColumnInfo(name="weatherForecastItemId")
    @Nullable
    private Long weatherForecastItemId;

    /***********************************************************
    *  Constructors
    **********************************************************/

    public float getGrnd_level() {
        return grnd_level;
    }

    public void setGrnd_level(float grnd_level) {
        this.grnd_level = grnd_level;
    }

    public float getSea_level() {
        return sea_level;
    }

    public void setSea_level(float sea_level) {
        this.sea_level = sea_level;
    }

    /**
     * No args constructor for use in serialization
     * 
     */
    public Main() {
    }

    /**
     * 
     * @param humidity
     * @param pressure
     * @param tempMax
     * @param temp
     * @param tempMin
     */
    @Ignore
    public Main(float temp, float pressure, int humidity, float tempMin, float tempMax) {
        this.temp = temp;
        this.pressure = pressure;
        this.humidity = humidity;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
    }

    /**
     * 
     * @return
     *     The temp
     */
    public float getTemp() {
        return temp;
    }

    /**
     * 
     * @param temp
     *     The temp
     */
    public void setTemp(float temp) {
        this.temp = temp;
    }

    /**
     * 
     * @return
     *     The pressure
     */
    public float getPressure() {
        return pressure;
    }

    /**
     * 
     * @param pressure
     *     The pressure
     */
    public void setPressure(float pressure) {
        this.pressure = pressure;
    }

    /**
     * 
     * @return
     *     The humidity
     */
    public int getHumidity() {
        return humidity;
    }

    /**
     * 
     * @param humidity
     *     The humidity
     */
    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    /**
     * 
     * @return
     *     The tempMin
     */
    public float getTempMin() {
        return tempMin;
    }

    /**
     * 
     * @param tempMin
     *     The temp_min
     */
    public void setTempMin(float tempMin) {
        this.tempMin = tempMin;
    }

    /**
     * 
     * @return
     *     The tempMax
     */
    public float getTempMax() {
        return tempMax;
    }

    /**
     * 
     * @param tempMax
     *     The temp_max
     */
    public void setTempMax(float tempMax) {
        this.tempMax = tempMax;
    }

    public long get_id_m() {
        return _id_m;
    }

    public void set_id_m(long _id_m) {
        this._id_m = _id_m;
    }

    public Long getWeatherDataId() {
        return weatherDataId;
    }

    public void setWeatherDataId(Long weatherDataId) {
        this.weatherDataId = weatherDataId;
    }

    public Long getWeatherForecastItemId() {
        return weatherForecastItemId;
    }

    public void setWeatherForecastItemId(Long weatherForecastItemId) {
        this.weatherForecastItemId = weatherForecastItemId;
    }
}
