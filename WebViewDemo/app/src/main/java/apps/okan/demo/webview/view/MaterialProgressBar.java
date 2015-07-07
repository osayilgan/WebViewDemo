package apps.okan.demo.webview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import apps.okan.demo.webview.util.UIUtils;
import apps.okan.webviewdemo.R;

/**
 * Material Style Custom Progress Bar using CircularProgressDrawable for drawing and animating the Progress Bar.
 *
 * This Class uses attribute colorPrimaryDark as it's drawable Color.
 * In order to set the Color, set colorPrimaryDark in Styles XML.
 *
 * @author Okan SAYILGAN
 */
public class MaterialProgressBar extends View {

    private CircularProgressDrawable mDrawable;
    private int customColorAttribute;

    public MaterialProgressBar(Context context) {
        this(context, null);
    }

    public MaterialProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        /* Check if the Drawable Color is Declared in XML */
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.MaterialProgressBar);
        customColorAttribute = attributes.getColor(R.styleable.MaterialProgressBar_material_progress_bar_color, getResources().getColor(android.R.color.black));

        mDrawable = new CircularProgressDrawable(customColorAttribute, UIUtils.dpToPx(5));
        mDrawable.setCallback(this);
    }

    /**
     * Sets Color of the Paint Color of Drawable Object.
     *
     * @param color
     */
    public void setDrawableColor(int color) {
        mDrawable.setColor(color);
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE && mDrawable != null) {
            mDrawable.start();
        } else {
            if (mDrawable != null) mDrawable.stop();
        }
    }

    /**
     * Starts Circular Animation
     */
    public void startAnimation() {
        if (mDrawable != null) mDrawable.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mDrawable.setBounds(0, 0, w, h);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        mDrawable.draw(canvas);
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return who == mDrawable || super.verifyDrawable(who);
    }
}
