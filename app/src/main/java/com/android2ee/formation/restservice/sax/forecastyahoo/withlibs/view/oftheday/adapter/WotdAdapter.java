package com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.view.oftheday.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android2ee.formation.restservice.sax.forecastyahoo.R;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.transverse.model.calculated.WeatherOfTheDay;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.transverse.utils.MyLog;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.view.oftheday.WotdActivityModel;

import java.util.List;

/**
 * Created by Created by Mathias Seguy alias Android2ee on 22/06/2018.
 */
public class WotdAdapter extends RecyclerView.Adapter<WotdHolder> {
    private static final String TAG = "ForecastItemAdapter";
    /***********************************************************
     *  Attributes
     **********************************************************/
    private WotdActivityModel parentModel;
    private LayoutInflater inflater;
    private WotdHolder viewHolder;
    private View view;
    private AppCompatActivity activity;
    List<WeatherOfTheDay> items=null;
    /**
     * To know the position of today
     */
    int itemOfTodayPosition;
    /***********************************************************
    *  Constructors
    **********************************************************/

    public WotdAdapter(AppCompatActivity ctx, WotdActivityModel parentModel) {
        activity = ctx;
        this.parentModel=parentModel;
        inflater=LayoutInflater.from(ctx);

    }

    /***********************************************************
    *  Managing LifeCycle
    **********************************************************/
    @NonNull
    @Override
    public WotdHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate
        view=inflater.inflate(R.layout.activity_wotd_cardview_item, parent, false);
        //create
        viewHolder=new WotdHolder(view,activity,parentModel);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull WotdHolder holder, int position) {
        MyLog.e(TAG,"onBindViewHolder called"+position+", item:"+items.get(position));
        holder.updateView(items.get(position),itemOfTodayPosition==position);
    }

    @Override
    public int getItemCount() {
        if(items==null){
            return 0;
        }
        return items.size();
    }


    /**
     * Called by the activity to update the list of items
     * @param weatherForecastItems
     */
    public void updateList(@Nullable List<WeatherOfTheDay> weatherForecastItems){
        MyLog.e(TAG, "updateList called new size"+(weatherForecastItems==null?"0":weatherForecastItems.size())+" onThread:"+Thread.currentThread());
        DiffUtil.calculateDiff(diffUtilForecastItemChangesAnlayser(this.items, weatherForecastItems)).dispatchUpdatesTo(this);
        items=weatherForecastItems;
    }


/***********************************************************
 *  DiffUtilsCallBack and ListUpdateCallback implementation
 **********************************************************/
    /**
     * The goal is to detect if the PagerAdapter has to be be notifyDataSetChanged
     * So the size are the same and the isOver is the same
     * @param oldList The old battle
     * @param newList The new battle
     * @return Nothing, dispatch the updateEntity to this (which implement UpdateCallback)
     */
    @NonNull
    private DiffUtil.Callback diffUtilForecastItemChangesAnlayser(final List<WeatherOfTheDay> oldList, final List<WeatherOfTheDay> newList) {
        return new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return oldList==null?0:oldList.size();
            }
            @Override
            public int getNewListSize() {
                return newList==null?0:newList.size();
            }
            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return oldList.get(oldItemPosition).get_id_wotd() == newList.get(oldItemPosition).get_id_wotd();
            }
            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                //only detect changes in isOver, you don't care here about tracking title, deb and fin
                return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
            }
        };
    }
    /***********************************************************
     *  Public methods called by activity
     **********************************************************/
    /**
     * Has o be called by the activty
     * Define the position of today in the list
     * @param itemOfTodayPosition
     */
    public void setItemOfTodayPosition(int itemOfTodayPosition){
        this.itemOfTodayPosition=itemOfTodayPosition;
    }

}
