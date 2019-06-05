package com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.view.current.main;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

public class MainModelFactory extends ViewModelProvider.NewInstanceFactory {

    private long contextId;
    private boolean isForecast;

    public MainModelFactory(long contextId,
                            boolean isForecast) {
        this.contextId = contextId;
        this.isForecast = isForecast;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (isForecast) {
            return (T) new MainViewModel(contextId,MainViewModel.FORECAST_CONTEXT);
        } else {
            return (T) new MainViewModel(contextId,MainViewModel.CURRENT_CONTEXT);
        }
    }
}
