package apps.okan.demo.webview.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import apps.okan.webviewdemo.R;

/**
 * Custom View which has Progress Bar inside a Relative Layout.
 * Parent, Relative Layout applies a dark color filter as background color.
 *
 * So when the Progress Bar is Displayed it has dark background.
 *
 * Created by Okan SAYILGAN on 02/12/14.
 */
public class DarkBackgroundProgressBar extends RelativeLayout {

    private Context context;
    private MaterialProgressBar progressBar;

    public DarkBackgroundProgressBar(Context context) {
        super(context);
        this.context = context;
        inflate();
    }

    public DarkBackgroundProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        inflate();
    }

    public DarkBackgroundProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        inflate();
    }

    public void inflate() {
        RelativeLayout.inflate(context, R.layout.custom_progress_bar, this);
        progressBar = (MaterialProgressBar) this.findViewById(R.id.materialProgressBar);

        /* Changing Visibility with Fade-in-out takes some time for progress bar to gets it's visibility changes */
        /* So we need to start animation right away, when it's initialised */
        progressBar.startAnimation();
    }

    @Override
    public void setVisibility(int visibility) {

        Animation anim;

        if (visibility == VISIBLE) {
            anim = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        } else {
            anim = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        }
        setAnimation(anim);

        super.setVisibility(visibility);
    }

    /**
     * Changes the Visibility Without animation.
     *
     * @param visibility
     */
    public void setVisibilityWithNoAnimation(int visibility) {
        super.setVisibility(visibility);
    }
}
