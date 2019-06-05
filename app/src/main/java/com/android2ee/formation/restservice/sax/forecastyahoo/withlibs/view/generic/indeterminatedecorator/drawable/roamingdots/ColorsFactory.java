package com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.view.generic.indeterminatedecorator.drawable.roamingdots;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.android2ee.formation.restservice.sax.forecastyahoo.R;


/**
 * Created by Harini Sekar on 18/05/2017.
 * Return an array of colors for playing with shaders
 */

public class ColorsFactory {

    /**
     * @return
     * @chiuki http://chiuki.github.io/android-shaders-filters/#/5
     */
    public static int[] getRainbowColors(Context ctx) {
        return new int[]{
                ContextCompat.getColor(ctx, R.color.blue_100),
                ContextCompat.getColor(ctx, R.color.blue_200),
                ContextCompat.getColor(ctx, R.color.blue_300),
                ContextCompat.getColor(ctx, R.color.blue_400),
                ContextCompat.getColor(ctx, R.color.blue_500),
                ContextCompat.getColor(ctx, R.color.blue_600)
        };
    }

    /**
     * @return a list of colors
     */
    public static int[] getRadialColors(Context ctx) {
        return new int[]{
                ContextCompat.getColor(ctx, R.color.colorAccent),
                ContextCompat.getColor(ctx, R.color.colorPrimaryDarker),
                ContextCompat.getColor(ctx, R.color.colorPrimary)
        };
    }

//    public static int[] getRadialGreyColors(Context ctx) {
//        return new int[]{
//                ContextCompat.getColor(ctx, R.color.grey1),
//                ContextCompat.getColor(ctx, R.color.grey2),
//                ContextCompat.getColor(ctx, R.color.grey3),
//                ContextCompat.getColor(ctx, R.color.grey4),
//                ContextCompat.getColor(ctx, R.color.grey5),
//                ContextCompat.getColor(ctx, R.color.grey6)
//        };
//    }
//
//    public static int[] getRadialOrangeColors(Context ctx) {
//        return new int[]{
//                ContextCompat.getColor(ctx, R.color.orange_indeterminate1),
//                ContextCompat.getColor(ctx, R.color.orange_indeterminate2),
//                ContextCompat.getColor(ctx, R.color.orange_indeterminate3),
//                ContextCompat.getColor(ctx, R.color.orange_indeterminate4),
//                ContextCompat.getColor(ctx, R.color.orange_indeterminate5),
//                ContextCompat.getColor(ctx, R.color.orange_indeterminate6)
//        };
//    }

}
