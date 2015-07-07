package apps.okan.demo.webview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import apps.okan.webviewdemo.R;

/**
 * Forked from Material Widget Library.
 * https://github.com/keithellis/MaterialWidget
 *
 * @author Okan SAYILGAN
 */
public class MaterialCircleButton extends View {

    private static final float SHADOW_RADIUS = 10.0f;
    private static final float SHADOW_OFFSET_X = 0.0f;
    private static final float SHADOW_OFFSET_Y = 3.0f;

    private int mButtonWidth;
    private int mButtonHeight;
    private int mColor;
    private Bitmap mIcon;
    private Rect mFingerRect;
    private boolean mMoveOutside;

    private Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint iconPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public MaterialCircleButton(Context context) {
        this(context, null);
    }

    public MaterialCircleButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialCircleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mButtonWidth = this.getWidth();
        mButtonHeight = this.getHeight();

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.MaterialCircleButton);

        mColor = attributes.getColor(R.styleable.MaterialCircleButton_material_circle_button_color, getResources().getColor(R.color.breezy_blue));

        float shadowRadius = attributes.getFloat(R.styleable.MaterialCircleButton_material_circle_button_radius, SHADOW_RADIUS);

        float shadowOffsetX = attributes.getFloat(R.styleable.MaterialCircleButton_material_circle_button_offset_x, SHADOW_OFFSET_X);
        float shadowOffsetY = attributes.getFloat(R.styleable.MaterialCircleButton_material_circle_button_offset_y, SHADOW_OFFSET_Y);

        int shadowColor = attributes.getColor(R.styleable.MaterialCircleButton_material_circle_button_shadow_color,
                getResources().getColor(android.R.color.black));

        Drawable drawable = attributes.getDrawable(R.styleable.MaterialCircleButton_material_circle_button_icon);
        if (drawable != null) {
            mIcon = ((BitmapDrawable) drawable).getBitmap();
        }
        attributes.recycle();
        circlePaint.setColor(mColor);
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setShadowLayer(shadowRadius, shadowOffsetX, shadowOffsetY, shadowColor);
        setWillNotDraw(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    /**
     * Set Icon Drawable of the Circle Button.
     *
     * @param icon
     */
    public void setIcon(int icon) {

        /* Bitmap from Resources */
        this.mIcon = BitmapFactory.decodeResource(getResources(), icon);

        /* Force View to ReDraw the View */
        invalidate();
    }

    private int darkenColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f;
        return Color.HSVToColor(hsv);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size;
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthSpecMode == MeasureSpec.EXACTLY) {
            if (widthSpecSize < mButtonWidth) {
                size = mButtonWidth;
            } else {
                size = widthSpecSize;
            }
        } else {
            size = mButtonWidth;
        }
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if (heightSpecMode == MeasureSpec.EXACTLY) {
            if (heightSpecSize < mButtonHeight) {
                size = mButtonHeight;
            } else {
                size = heightSpecSize;
            }
        } else {
            size = mButtonHeight;
        }
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:

                mMoveOutside = false;
                mFingerRect = new Rect(getLeft(), getTop(), getRight(), getBottom());
                circlePaint.setColor(darkenColor(mColor));
                invalidate();
                break;

            case MotionEvent.ACTION_MOVE:

                if (!mFingerRect.contains(getLeft() + (int) event.getX(),
                        getTop() + (int) event.getY())) {
                    mMoveOutside = true;
                    circlePaint.setColor(mColor);
                    invalidate();
                }
                break;

            case MotionEvent.ACTION_UP:

                circlePaint.setColor(mColor);
                invalidate();
                if (!mMoveOutside) {
                    performClick();
                }
                break;

            case MotionEvent.ACTION_CANCEL:

                circlePaint.setColor(mColor);
                invalidate();
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, (float) (getWidth() / 2.6), circlePaint);
        if (mIcon != null) {
            float x = (getWidth() - mIcon.getWidth()) / 2;
            float y = (getHeight() - mIcon.getHeight()) / 2;
            canvas.drawBitmap(mIcon, x, y, iconPaint);
        }
    }
}