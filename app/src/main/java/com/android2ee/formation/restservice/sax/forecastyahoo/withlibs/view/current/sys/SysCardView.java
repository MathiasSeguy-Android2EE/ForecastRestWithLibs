package com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.view.current.sys;

import android.annotation.SuppressLint;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.widget.TextView;

import com.android2ee.formation.restservice.sax.forecastyahoo.R;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.transverse.model.current.Sys;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.view.MotherCardView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Marion Aubard on 14/06/2018.
 */
public class SysCardView extends MotherCardView {

    private static final String TIME_FORMAT = "HH:mm";

    /***********************************************************
     *  Attributes
     **********************************************************/

    private TextView tvCountry;
    private TextView tvSunrise;
    private TextView tvSunset;
    private AppCompatImageView ivSunset;
    private AppCompatImageView ivSunrise;

    SysViewModel model;

    /***********************************************************
     *  Constructors
     **********************************************************/
    public SysCardView(@NonNull Context context) {
        super(context);
    }

    public SysCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SysCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initViews();
    }

    /***********************************************************
     *  ViewModel management
     *********************************************************/

    @Override
    public Class getCardViewModelClass() {
        return null;
    }

    @Override
    public String getCardViewModelKey() {
        return null;
    }

    @NonNull
    @Override
    protected ViewModelProvider.Factory getCardViewFactory() {
        return null;
    }

    /***********************************************************
     *  Private methods
     **********************************************************/

    @SuppressLint("NewApi")
    private void onChangedLiveData(@Nullable Sys sys) {
        if (sys != null) {
            updateUI(sys);
        }
    }

    private void updateUI(@NonNull Sys sys) {
        tvCountry.setText(sys.getCountry());
        Date sunrise = new Date(sys.getSunrise()*1000L);
        Date sunset = new Date(sys.getSunset()*1000L);
        tvSunrise.setText(new SimpleDateFormat(TIME_FORMAT, Locale.getDefault()).format(sunrise));
        tvSunset.setText(new SimpleDateFormat(TIME_FORMAT, Locale.getDefault()).format(sunset));
        animateAVD(ivSunset.getDrawable());
        animateAVD(ivSunrise.getDrawable());
    }

    @SuppressLint("NewApi")
    private void animateAVD(Drawable drawable) {
        if (drawable instanceof AnimatedVectorDrawableCompat) {
            ((AnimatedVectorDrawableCompat) drawable).start();
        } else {
            ((AnimatedVectorDrawable) drawable).start();
        }
    }

    private void initViews() {
        tvCountry = findViewById(R.id.tv_country);
        tvSunrise = findViewById(R.id.tv_sunrise);
        tvSunset = findViewById(R.id.tv_sunset);
        ivSunset = findViewById(R.id.iv_sunset);
        ivSunrise = findViewById(R.id.iv_sunrise);

    }

    @Override
    protected void initObservers() {
        model = ViewModelProviders.of(activity, new SysModelFactory(contextId)).get(SysViewModel.class);
        model.getLiveData().observe(activity, new Observer<Sys>() {
            @Override
            public void onChanged(@Nullable Sys sys) {
                onChangedLiveData(sys);
            }
        });

    }

}
