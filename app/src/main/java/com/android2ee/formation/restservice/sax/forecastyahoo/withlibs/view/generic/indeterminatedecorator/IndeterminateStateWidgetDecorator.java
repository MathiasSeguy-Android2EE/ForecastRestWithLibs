package com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.view.generic.indeterminatedecorator;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;

import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.view.generic.indeterminatedecorator.drawable.IndeterminateDrawable;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.view.generic.indeterminatedecorator.drawable.roamingdots.MovingBottomMaterialBarDrawable;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.view.generic.indeterminatedecorator.drawable.roamingdots.RoamingDotsDrawable;


/**
 * Created by Harini Sekar on 16/05/2017.
 * If you want a component (any view) to have an indeterminate state
 * by changing its background or by having a "layer" upon it
 * or by having a translucent animation,
 * <p>
 * Use this class this way:
 * IndeterminateStateWidgetDecorator decorator;
 *
 * @Override protected void onCreate(Bundle savedInstanceState) {
 * super.onCreate(savedInstanceState);
 * setContentView(R.layout.activity_main);
 * btnButton= (Button) findViewById(R.id.button);
 * decorator=new IndeterminateStateWidgetDecorator(btnButton);
 * btnButton.setOnClickListener(new View.OnClickListener() {
 * @Override public void onClick(View v) {
 * switchState();
 * }
 * });
 * }
 * <p>
 * //
 * // Change the staate of the button
 * //
 * public void switchState(){
 * indterminate=!indterminate;
 * if(indterminate){
 * decorator.setIndeterminate();
 * }else {
 * decorator.setDeterminate();
 * }
 * <p>
 * }
 * You need in onStop for Activity or in onDetachFromWindow to call decorator.stop
 */
public class IndeterminateStateWidgetDecorator {
    private static final String TAG = "DecoratorThreeStatesWid";
    /***********************************************************
     *  Attributes
     **********************************************************/
    /**
     * The decoratedView we manage
     */
    private View decoratedView;
    /**
     * The size of the DecoratedView
     */
    private int height, width;
    /**
     * The background drawable of the decoratedView we manage
     */
    private Drawable decoratedDrawable;
    /**
     * The drawable that decorate the initial background
     * It should implements IndetermlinateDrawableIntf
     */
    private IndeterminateDrawable decoratingDrawable = null;
    /**
     * The new background of the decorated view when the state is indeterminate
     */
    private LayerDrawable layerDrawable = null;
    /**
     * Sometimes the setIndeterminate has been called before the view has been built
     * So track taht for when the view is built to automaticly kaunche the animation
     */
    private boolean needToBeLaunchAsap = false;
    /***********************************************************
     *  State constants
     **********************************************************/
    private int state = DETERMINATE;
    public static final int DETERMINATE = 0;
    public static final int INDETERMINATE = 1;

    /***********************************************************
     *  Position constants
     **********************************************************/
    public static final int TOP = 0;
    public static final int BOTTOM = 1;
    public static final int DEFAULT_POSITION = BOTTOM;
    private int position = DEFAULT_POSITION;


    /***********************************************************
     *  Decorated view padding.
     **********************************************************/
    private int leftPadding = 0;
    private int topPadding = 0;
    private int rightPadding = 0;
    private int bottomPadding = 0;

    /***********************************************************
     *  Constructors
     **********************************************************/
    public IndeterminateStateWidgetDecorator(View decoratedView) {
        this(decoratedView, DEFAULT_POSITION);
    }

    public IndeterminateStateWidgetDecorator(View decoratedView, int position) {
        this(decoratedView, null, position);
    }

    public IndeterminateStateWidgetDecorator(View decoratedView, IndeterminateDrawable decoratingDrawable) {
        this(decoratedView, decoratingDrawable, DEFAULT_POSITION);
    }

    public IndeterminateStateWidgetDecorator(View decoratedView, IndeterminateDrawable decoratingDrawable, int position) {
        this.decoratedView = decoratedView;
        this.position = position;
        if (decoratingDrawable != null) {
            this.decoratingDrawable = decoratingDrawable;
        }
        init();
    }
    /***********************************************************
     *  Initialisation
     **********************************************************/
    /**
     * initialize the components and size
     */
    private void init() {
        //get the background
        decoratedDrawable = decoratedView.getBackground();
        //find the size of the decorated view
        findDecoratedViewSize();
    }

    /**
     * Find the size of the View we decorate
     */
    private void findDecoratedViewSize() {
        //this is an usual trick when we want to know the dimension of the elements of our view
        // find the dimension of the decoratedView
        final ViewTreeObserver vto = decoratedView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                initializeSize(this);
            }
        });
    }

    /**
     * Initialize the size of the components as soon as we know its size
     */
    private void initializeSize(ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            decoratedView.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
        } else {
            decoratedView.getViewTreeObserver().removeGlobalOnLayoutListener(onGlobalLayoutListener);
        }
        //initialize size
        width = decoratedView.getMeasuredWidth();
        height = decoratedView.getMeasuredHeight();

        leftPadding = decoratedView.getPaddingLeft();
        rightPadding = decoratedView.getPaddingRight();
        topPadding = decoratedView.getPaddingTop();
        bottomPadding = decoratedView.getPaddingBottom();
        //just in case
        if (width == 0) {
            findDecoratedViewSize();
            return;
        }

        createLayerlDrawable();
        if (needToBeLaunchAsap) {
            //it means your state has changed before you were created
            //so just update yourself now according to your state
            if (state == INDETERMINATE) {
                setIndeterminate();
            } else {
                setDeterminate();
            }
            needToBeLaunchAsap = false;
        }
        //MyLog.e(TAG,"Found dimension : w="+width+", h="+height);
        //MyLog.e(TAG,"FoundNOTMEASUE  dimension : w="+ decoratedView.getWidth()+", h="+ decoratedView.getHeight());
    }

    /**
     * Create the Drawable to use as background when the state is indeterminate
     */
    private void createLayerlDrawable() {
        //MyLog.e(TAG,"Found visible rectangle for "+decoratedView.getTag()+": w="+width+", h="+height);
        //create the new drawable if not provided
        if (decoratingDrawable == null) {
            //create default
//            decoratingDrawable = new RoamingDotsDrawable(RoamingDotsDrawable.RADIAL_GREY_TYPE, decoratedView, this, width, height);
            decoratingDrawable = new MovingBottomMaterialBarDrawable(RoamingDotsDrawable.RADIAL_RAINBOW_TYPE, decoratedView, width, height, position);
        }
        //MyLog.e(TAG,"decoratedDrawable == null ? "+(decoratedDrawable==null));
        //MyLog.e(TAG,"decoratingDrawable == null ? "+(decoratingDrawable==null));
        //define the layerDrawable
        Drawable[] backgroundsDrawable = new Drawable[2];
        backgroundsDrawable[0] = decoratedDrawable == null ? decoratingDrawable : decoratedDrawable;
        backgroundsDrawable[1] = decoratingDrawable;
        layerDrawable = new LayerDrawable(backgroundsDrawable);
    }

    /***********************************************************
     *  Implement Public method
     **********************************************************/
    /**
     * Switch to the indeterminate state
     */
    public void setIndeterminate() {
        state = INDETERMINATE;
        //if the layerDrawable is not defined yet, create it
        if (layerDrawable == null) {
            needToBeLaunchAsap = true;
            return;
        }

        //make the switch
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            decoratedView.setBackground(layerDrawable);
            decoratedView.setPadding(leftPadding, topPadding, rightPadding, bottomPadding);
        } else {
            decoratedView.setBackgroundDrawable(layerDrawable);
        }

        //then set the indeterminate state on the drawable
        decoratingDrawable.setIndeterminate();        //
    }

    public int getState() {
        return state;
    }

    /**
     * Like in Android: stop animation, release memory
     * Should be called in onStop
     */
    public void onStop() {
        if (layerDrawable == null) {
            return;
        }
        decoratingDrawable.onStop();
    }

    /**
     * Switch to the determinate state
     */
    public void setDeterminate() {
        state = DETERMINATE;
        if (layerDrawable == null) {
            needToBeLaunchAsap = true;
            return;
        }
        //switch to determinate
        decoratingDrawable.setDeterminate();
    }

    /*****************onDeterminated NRVER CALLED = NOT USED************************/
   /* @Override
    public void onDeterminated() {
        //switch background, the state is transition to state determinate is finished
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            decoratedView.setBackground(decoratedDrawable);
            decoratedView.setPadding(leftPadding, topPadding, rightPadding, bottomPadding);
        }else{
            decoratedView.setBackgroundDrawable(decoratedDrawable);
        }
    }*/
}
