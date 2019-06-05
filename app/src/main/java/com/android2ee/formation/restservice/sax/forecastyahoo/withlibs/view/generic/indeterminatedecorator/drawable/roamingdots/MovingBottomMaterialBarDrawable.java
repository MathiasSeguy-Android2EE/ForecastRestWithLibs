/**
 * <ul>
 * <li>RondBleu</li>
 * <li>com.android2ee.formation.paris.atos.customview</li>
 * <li>07/07/2015</li>
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

package com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.view.generic.indeterminatedecorator.drawable.roamingdots;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Shader;
import androidx.annotation.IntRange;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.android2ee.formation.restservice.sax.forecastyahoo.R;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.transverse.utils.MyLog;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.view.generic.indeterminatedecorator.IndeterminateStateWidgetDecorator;
import com.android2ee.formation.restservice.sax.forecastyahoo.withlibs.view.generic.indeterminatedecorator.drawable.IndeterminateDrawable;


/**
 * Created by Harini Sekar on 07/07/2015.
 */
public class MovingBottomMaterialBarDrawable extends IndeterminateDrawable {
    private static final String TAG = "MovingBottomMaterialBarDrawable";
    /**
     * To initialized variable when the animation is launched for the first time
     */
    private static final int NOT_INITIALIZED = -1;
    /**
     * Alpha of the background paint
     */
    private int backgroundAlpha;
    /**
     * Width and height of the view
     */
    private int w, h;
    /**
     * Can be top or Bottom.
     * Are defined here:
     * IndeterminateStateWidgetDecorator.TOP
     * IndeterminateStateWidgetDecorator.BOTTOM
     */
    private int position;
    /**
     * The vertical position of the view
     */
    private int barVerticalBottomPosition, barVerticalTopPosition;
    /**
     * x0:position of the left corner of the bar
     * x1:position of the right corner of the bar
     * <p>
     * x0 belong to [0,w]
     * x1 belong to [0,min(x0+l,w)]
     * radius is the radius of the circle to make a round border
     */
    private float x0 = 0, x1 = 0, radius;
    /**
     * Initial distance between x0 and x1 when ending animation starts
     */
    private float delta;
    /**
     * barLenght: lenght of the bar
     */
    private int barLenght = 0, barHeight;
    /**
     * Distance to bottom
     */
    private int verticalPadding, minWeight, maxWeight;
    /**
     * Pain used to draw the dots
     * When the state is indeterminate
     * (animation is running)
     */
    private Paint indeterminatePaint, indeterminatePaintBackgroundBar;
    /**
     * Pain used to draw the dot
     * When the state is determinate
     */
    private Paint determinatePaint;
    /**
     * Shader used by the paint to draw the dots
     * When the state is indeterminate
     * (animation is running)
     */
    Shader indetereminateShader;
    /**
     * Shader used by the paint to draw the dot
     * When the state is determinate
     */
    Shader detereminateShader;
    /**
     * The view to decorate
     * Mainly because we need to call invalidate on it
     */
    private View decoratedView;
    /**
     * The graphical context
     */
    Context ctx;
    /**
     * Callback to say, ok the transition to the determinate state is over
     */
    /***********************************************************
     *  Type for the effect
     **********************************************************/
    public static final int DEFAULT_TYPE = 0;
    public static final int RAINBOW_TYPE = 1;
    public static final int RADIAL_RAINBOW_TYPE = 2;
    public static final int RADIAL_PRIMARYCOLORS_TYPE = 3;
    public static final int RADIAL_GREY_TYPE = 4;

    /***********************************************************
     *  Constructors
     **********************************************************/

    public MovingBottomMaterialBarDrawable(View ownerView, int w, int h, int position) {
        this(DEFAULT_TYPE, ownerView, w, h, position);
    }

    public MovingBottomMaterialBarDrawable(int type, View ownerView, int w, int h, int position) {
        this.position = position;
        decoratedView = ownerView;
        ctx = decoratedView.getContext();
        initializeSize(w, h);
        initPaints();
    }

    /***********************************************************
     * Managing initialization
     **********************************************************/
    /**
     * Call this method to initialize the size of bluedot
     *
     * @param w
     * @param h
     */
    private void initializeSize(int w, int h) {
        this.w = w;
        this.h = h;
        barLenght = w / 3;
        minWeight = 0;
        maxWeight = w;
        barHeight = (int) ctx.getResources().getDimension(R.dimen.movbarBarHeight);
        verticalPadding = (int) ctx.getResources().getDimension(R.dimen.movbarBottomPadding);
        radius = barHeight / 2f;//2f = avoid zero radius
        if (position == IndeterminateStateWidgetDecorator.BOTTOM) {
            barVerticalBottomPosition = h - verticalPadding;
        } else {
            barVerticalBottomPosition = verticalPadding + barHeight;
        }
        barVerticalTopPosition = barVerticalBottomPosition - barHeight;
        MyLog.e(TAG, "*************************");
        MyLog.e(TAG, "Initialization values : ");
        MyLog.e(TAG, "barLenght " + barLenght + ", maxWeight=" + maxWeight + ", barHeight=" + barHeight);
        MyLog.e(TAG, "verticalBottomPadding " + verticalPadding + ", radius=" + radius);
        //initialize shaders
//        initializeShaders();

    }


    /**
     * Initialize
     */
    private void initPaints() {

        //fields initiazalization
        backgroundAlpha = ctx.getResources().getInteger(R.integer.movbarBackgroundAlpha);
        // instantiate paints
        determinatePaint = new Paint();
        indeterminatePaint = new Paint();

        indeterminatePaint.setColor(ContextCompat.getColor(ctx, R.color.movbarMainColor));
        indeterminatePaint.setStrokeWidth(barHeight);
        indeterminatePaint.setStyle(Paint.Style.FILL);
        indeterminatePaint.setAntiAlias(true);
        indeterminatePaintBackgroundBar = new Paint();
        indeterminatePaintBackgroundBar.setColor(ContextCompat.getColor(ctx, R.color.movbarBackgroundColor));
        indeterminatePaintBackgroundBar.setStrokeWidth(barHeight);
        indeterminatePaintBackgroundBar.setStyle(Paint.Style.FILL);
        indeterminatePaintBackgroundBar.setAntiAlias(true);
        indeterminatePaintBackgroundBar.setAlpha(backgroundAlpha);
    }


    /**
     * Initialize the Shaders of the Paints
     */
//    private void initializeShaders() {
//        Matrix matrix;
//        //initialize the shader (stuff that make the color of the paint depending on the location in the screen and a set of colors)
//        //@chiuki at droidcon london
//        switch (shaderEffectType) {
//            case RAINBOW_TYPE:
//                indetereminateShader = new LinearGradient(0, 0, 0, w, ColorsFactory.getRainbowColors(ctx),
//                        null, Shader.TileMode.MIRROR);
//                matrix = new Matrix();
//                matrix.setRotate(90);
//                indetereminateShader.setLocalMatrix(matrix);
//                break;
//
//            case DEFAULT_TYPE:
////                indetereminateShader = new LinearGradient(0, 0, h, w, ColorsFactory.getRadialColors(ctx),
////                        null, Shader.TileMode.MIRROR);
//                indetereminateShader = null;
//                break;
//            default:
//                // Load the bitmap "cover.png"
//                Bitmap mBitmap = BitmapFactory.decodeResource(ctx.getResources(),
//                        R.mipmap.ic_launcher);
//                // Create the shader
//                indetereminateShader = new BitmapShader(mBitmap,
//                        Shader.TileMode.REPEAT,
//                        Shader.TileMode.REPEAT);
//                break;
//        }
//        if (indetereminateShader != null) {
//            indeterminatePaint.setShader(indetereminateShader);
//        } else {
//            indeterminatePaint.setColor(ContextCompat.getColor(ctx, R.color.colorPrimaryDark));
//        }
//        //TODO 100
//        detereminateShader = new RadialGradient(centerX, centerY,
//                100, ColorsFactory.getRainbowColors(ctx), null, Shader.TileMode.MIRROR);
//        determinatePaint.setShader(detereminateShader);
//    }
    /***********************************************************
     *  IndeterminateDrawableIntf instantiation
     **********************************************************/
    /**
     * To call to set the component in its indeterminate state
     * Usually if it's a drawable it launches an animation (because indeterminate state) and call invalidate
     */
    @Override
    public void setIndeterminate() {
        startAnimation();
    }

    /**
     * To call to set the component in its determinate state
     * Usually it stops the running animation and become transparent
     */
    @Override
    public void setDeterminate() {
        stopAnimation();
    }
    /***********************************************************
     * Managing Animation
     **********************************************************/
    /***
     * The ObjectAnimator that handles the animation
     */
    ObjectAnimator animMoving, animEnding;
    /***
     * Is animating right now ?
     */
    boolean animating = false;
    /**
     * Is Toto Animation has been reversed ?
     */
    boolean animTotoRepeating = false;


    /***********************************************************
     *  Managing animations "Life cycle"
     **********************************************************/

    /**
     * Start animating the rondBleu
     */
    @SuppressLint("NewApi")
    private void startAnimation() {
        //MyLog.e(TAG, "StartAnimating postICS=" + postICS);
        //will be done only for ICS and above

        //do chet haaze animation(https://www.youtube.com/watch?v=vCTcmPIKgpM)
        initializeMovingAnimation();
        //run the animation
        if (!animating) {
            animMoving.start();
        }

    }

    /**
     * Call to stop the curent animation if it is running
     */
    @SuppressLint("NewApi")
    private void stopAnimation() {
        if (animMoving != null && animMoving.isRunning()) {
            //it means animSpread stop and then run gathering (define in the listeners of the spread anim)
            animMoving.cancel();
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onStop() {
        if (animMoving != null && animMoving.isRunning()) {
            animMoving.cancel();
        }
        if (animEnding != null && animEnding.isRunning()) {
            animEnding.cancel();
        }
    }


    /***********************************************************
     *  Manage Spreading Animation
     **********************************************************/

    /**
     * The main Method that returns a ObjectAnimator
     *
     * @return The ObjectAnimator that animates the views
     */
    @SuppressLint("NewApi")
    ObjectAnimator getMovingAnimator() {
        return ObjectAnimator.ofInt(this, "moving", 0, w + barLenght);
    }

    /**
     * Initialize the Spreading Animation
     */

    @SuppressLint("NewApi")
    private void initializeMovingAnimation() {
        animMoving = getMovingAnimator();
        animMoving.setDuration(ctx.getResources().getInteger(R.integer.movbarAnimationDuration));
        animMoving.setRepeatMode(ValueAnimator.RESTART);
        animMoving.setRepeatCount(ValueAnimator.INFINITE);
        animMoving.setInterpolator(new LinearInterpolator());
        animMoving.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                animating = true;
                animTotoRepeating = false;
                setAlpha(255);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                MyLog.e(TAG, "MovingAnimation is End");
                animating = false;
                setAlpha(0);
//                launchEndingAnimation();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                MyLog.e(TAG, "MovingAnimation is Canceled");
                animating = false;
                setAlpha(0);
                launchEndingAnimation();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
//                MyLog.e(TAG, "MovingAnimation is repeating");
                animating = true;
                animTotoRepeating = true;
            }
        });
    }

    /**
     * The property to animate
     * It will make the ball move around like after an explosion and bounce on the walls
     *
     * @param parameter value of the state to calculate the animation of the object
     */
    @Keep
    private void setMoving(int parameter) {
        Rect dirtyRect = new Rect((int) x0, barVerticalBottomPosition - barHeight, (int) x1, barVerticalBottomPosition);
        //important to define the dirty area not to have to redraw every pixel
        //paarmete belongs to [0,w] but it should be -barlenght
        x0 = parameter - barLenght;
        x1 = Math.min(x0 + barLenght, w);
        //important to define the dirty area not to have to redraw every pixel
        //left,top,right,bottom
        dirtyRect.union((int) x0, barVerticalBottomPosition - barHeight, (int) x1, barVerticalBottomPosition);

        //then invalidate the dirty rectangle
        decoratedView.invalidate(dirtyRect);
    }


    /***********************************************************
     *  Manage Gathering Animation
     **********************************************************/

    /**
     * The main Method that returns a ObjectAnimator
     *
     * @return The ObjectAnimator that animates the views
     */
    @SuppressLint("NewApi")
    ObjectAnimator getEndingAnimator() {
        return ObjectAnimator.ofInt(this, "ending", 0, 100);
    }

    /**
     * Initialize the Spreading Animation
     */

    @SuppressLint("NewApi")
    private void launchEndingAnimation() {
        delta = (x1 - x0) / 200;
        animEnding = getEndingAnimator();
        animEnding.setDuration(ctx.getResources().getInteger(R.integer.movbarEndingAnimationDuration));
        animEnding.setInterpolator(new DecelerateInterpolator());
        animEnding.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                animating = true;
                animTotoRepeating = false;
                setAlpha(255);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animating = false;
                setAlpha(0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                animating = false;
                setAlpha(0);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                animating = true;
                animTotoRepeating = true;
            }
        });
        animEnding.start();
    }

    /**
     * The property to animate
     * It will make the ball move around like after an explosion and bounce on the walls
     *
     * @param parameter value of the state to calculate the animation of the object
     */
    @Keep
    private void setEnding(int parameter) {
        Rect dirtyRect = new Rect((int) x0, barVerticalBottomPosition - barHeight, (int) x1, barVerticalBottomPosition);
        //important to define the dirty area not to have to redraw every pixel
        x0 = x0 + parameter * delta / 200;
        x1 = x1 - parameter * delta / 200;
        //important to define the dirty area not to have to redraw every pixel
        //left,top,right,bottom
        dirtyRect.union((int) x0, barVerticalBottomPosition - barHeight, (int) x1, barVerticalBottomPosition);
        setAlpha(255 - (255 * parameter / 100));

        //then invalidate the dirty rectangle
        decoratedView.invalidate(dirtyRect);
    }


    /***********************************************************
     * Drawable Abstract methods
     **********************************************************/

    @Override
    public void draw(@NonNull Canvas canvas) {

        if (animating) {
//            indeterminatePaint.setShader(indetereminateShader);
            //draw
            //TODO: try canvas translate
            //TODO: try with svg and animationpath
            //TODO: try with insetDrawable
            canvas.drawLine(x0, barVerticalTopPosition, x1, barVerticalTopPosition, indeterminatePaint);
            canvas.drawLine(minWeight, barVerticalTopPosition, maxWeight, barVerticalTopPosition, indeterminatePaintBackgroundBar);
            canvas.drawCircle(x0, barVerticalTopPosition, barHeight / 2f, indeterminatePaint);//2f = avoid zero radius
            canvas.drawCircle(x1, barVerticalTopPosition, barHeight / 2f, indeterminatePaint);
        }
    }

    @Override
    public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {
        indeterminatePaint.setAlpha(alpha);
        indeterminatePaintBackgroundBar.setAlpha(Math.min(backgroundAlpha, alpha));
        determinatePaint.setAlpha(alpha);
    }


    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }


}
