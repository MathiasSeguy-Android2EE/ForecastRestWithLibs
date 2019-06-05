package com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.view.current.weather_data;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

/**
 * Created by Created by Mathias Seguy alias Android2ee on 26/05/2018.
 */

public class WeatherDataBeaconModelFactory extends ViewModelProvider.NewInstanceFactory {
    /**
     * The model associated with the Battle Id
     */
    private long cityId;

    public WeatherDataBeaconModelFactory(long cityId) {
        this.cityId = cityId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new WeatherDataBeaconCardViewModel(cityId);
    }
}
