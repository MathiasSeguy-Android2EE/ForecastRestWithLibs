package com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.view;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;

public abstract class MotherCardView<VM extends MotherViewModel> extends CardView {

    protected AppCompatActivity activity;
    protected long contextId;

    /***********************************************************
     *  Constructors
     **********************************************************/
    public MotherCardView(@NonNull Context context) {
        super(context);
    }

    public MotherCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MotherCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setLifecycleOwner(AppCompatActivity activity, long contextId) {
        this.activity = activity;
        this.contextId = contextId;
        initObservers();
    }


    protected abstract void initObservers();

    /**
     * Give us the model associated with the mContext
     * @return The view model class
     */
    public abstract Class<VM> getCardViewModelClass();

    /**
     * Give us the model associated with the mContext
     * @return The view model class
     */
    public abstract String getCardViewModelKey();

    @NonNull
    protected abstract ViewModelProvider.Factory getCardViewFactory();

    @Nullable
    protected VM getViewModel() {
        if (activity == null) return null;

        if (getCardViewModelClass() != null) {
                return ViewModelProviders.of(activity, getCardViewFactory())
                        .get(getCardViewModelKey(), getCardViewModelClass());
        }
        return null;
    }
}
