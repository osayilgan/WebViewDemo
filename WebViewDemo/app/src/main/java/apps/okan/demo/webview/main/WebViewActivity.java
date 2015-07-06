package apps.okan.demo.webview.main;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import apps.okan.webviewdemo.R;

public class WebViewActivity extends AppCompatActivity {

    /* Fragment Manager */
    private FragmentManager fragmentManager;

    /* ID for Fragment Contanier */
    private int containerId = R.id.fragment_container;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        /* init Fragment Manager */
        this.fragmentManager = getFragmentManager();

        /* Push WebVew Fragment to Fragment Container */
        replaceFragment(new WebViewFragment(), containerId ,true, true);
    }

    /**
     * Replaces Fragment with the one inside the Fragment Container.
     * Animation : Fade-In-Out Animation with 500ms animation duration.
     *
     * @param fragment      Fragment to insert into given FragmentContainer.
     * @param containerId   ContainerId to insert Fragment.
     */
    protected void replaceFragment(Fragment fragment, int containerId, boolean addToBackStack, boolean isAnimated) {

        /* Invalidation Errors Check */
        if (containerId == 0 || fragment == null || fragmentManager == null) return;

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (addToBackStack) transaction.addToBackStack(fragment.getClass().getSimpleName());

        if (isAnimated) transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out,
                android.R.animator.fade_in, android.R.animator.fade_out);

        transaction.replace(containerId, fragment, fragment.getClass().getSimpleName());
        transaction.commit();
    }
}
