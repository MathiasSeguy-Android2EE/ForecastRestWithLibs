package com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.view.current.sys;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

/**
 * Created by Marion Aubard on 15/06/2018.
 */
public class SysModelFactory extends ViewModelProvider.NewInstanceFactory {
    /**
     * The model associated with the Sys Id
     */
    private long sysId;

    public SysModelFactory(long sysId) {
        this.sysId = sysId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new SysViewModel(sysId);
    }
}
