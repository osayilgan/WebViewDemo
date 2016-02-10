package apps.okan.demo.webview.main;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.pdf.PrintedPdfDocument;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.dx.stock.ProxyBuilder;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;

import apps.okan.demo.webview.util.FileUtils;
import apps.okan.demo.webview.view.DarkBackgroundProgressBar;
import apps.okan.demo.webview.view.MaterialCircleButton;
import apps.okan.webviewdemo.R;

/**
 * @author Okan SAYILGAN
 */
public class WebViewFragment extends Fragment implements View.OnClickListener {

    /* UI Elements */
    private WebView mWebView;
    private ProgressBar progressBar;
    private MaterialCircleButton printButton;
    private RelativeLayout printButtonContainer;
    private DarkBackgroundProgressBar loadingBar;

    private Handler handler;

    private String hostName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        handler = new Handler();

        /* Load URL */
        mWebView.loadUrl("http://breezy.com");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View parentView = inflater.inflate(R.layout.web_view_fragment, null, false);

        /* Init UI Elements */
        mWebView = (WebView) parentView.findViewById(R.id.mWebView);

        progressBar = (ProgressBar) parentView.findViewById(R.id.progressBar);
        printButton = (MaterialCircleButton) parentView.findViewById(R.id.printButton);
        printButtonContainer = (RelativeLayout) parentView.findViewById(R.id.printButtonContainer);
        loadingBar = (DarkBackgroundProgressBar) parentView.findViewById(R.id.loadingBar);

        /* Set Layer Type */
        mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        /* Setting a Click Listener will change the Clickable Flag of the View to true */
        printButton.setOnClickListener(this);

        setUpWebView();

        return parentView;
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (printButton.getId() == id) createPdfFile();
    }

    /**
     * Sets up the WebView Settings, Chrome and WebView Clients.
     */
    private void setUpWebView() {

        /* Set Chrome Client to handle progress bar */
        mWebView.setWebChromeClient(new BreezyChromeClient());
        mWebView.setWebViewClient(new BreezyWebViewClient());

        /* Enable Javascript Settings */
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(false);
        mWebView.getSettings().setDisplayZoomControls(false);
    }

    /**
     * By extending from WebChromeClient we can listen page loading progress changes.
     */
    private class BreezyChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {

            /* Update Progress Bar */
            progressBar.setProgress(newProgress);
        }
    }

    /**
     * WebView Client to keep track of WebPage OnPageStart and Finish Events.
     */
    private class BreezyWebViewClient extends WebViewClient {

        private int running = 0;

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String urlNewString) {
            running++;

            mWebView.loadUrl(urlNewString);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            running = Math.max(running, 1); // First request move it to 1.

            /* Do not Respond to Print Button Clicks */
            togglePrintButton(false);

            /* Change Visibilities */
            progressBar.setVisibility(View.VISIBLE);
            mWebView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {

            if (--running == 0) {

                /* Show Print Button */
                togglePrintButton(true);

                /* Hide Progress Bar */
                progressBar.setVisibility(View.GONE);

                /* Set the URL of the WebPage */
                hostName = getHostName();
            }
        }
    }

    /**
     * Called when print Button's State is changed from Enabled/Disabled.
     *
     * @param isEnabled
     */
    private void togglePrintButton(boolean isEnabled) {

        if (printButton.isEnabled() == isEnabled) return;

        /* Enable/Disable for Click Events */
        printButton.setEnabled(isEnabled);
        int start, target = 0;

        if (isEnabled) {
            start = -printButtonContainer.getHeight();
            target = 0;
        } else {
            start = 0;
            target = -printButtonContainer.getHeight();
        }

        final ValueAnimator va = ValueAnimator.ofInt(start, target);
        va.setDuration(200);

        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                ((RelativeLayout.LayoutParams) printButtonContainer.getLayoutParams()).bottomMargin = value.intValue();
                printButtonContainer.requestLayout();
            }
        });
        va.start();
    }

    /**
     * Sets the Visibility of Loading Bar depending on the Given isVisible parameter.
     *
     * @param isVisible
     */
    private void toggleLoadingBar(boolean isVisible) {
        loadingBar.setVisibility((isVisible) ? View.VISIBLE : View.GONE);
    }

    /**
     * Calculates the Letter Size Paper's Height depending on the LetterSize Dimensions and Given width.
     *
     * @param width
     * @return
     */
    private int getLetterSizeHeight(int width) {
        return (int)((float)(11*width)/8.5);
    }

    /**
     * Builds the Print Atrributes of the PDF Document.
     *
     * @return
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private PrintAttributes getPrintAttributes() {

        String fileName = "webPage.pdf";

        return new PrintAttributes.Builder().
                setColorMode(PrintAttributes.COLOR_MODE_COLOR).
                setMediaSize(PrintAttributes.MediaSize.NA_LETTER.asPortrait()).
                setResolution(new PrintAttributes.Resolution(fileName, fileName, 300, 300)).
                setMinMargins(new PrintAttributes.Margins(10, 10, 10, 10)).
                build();
    }

    /**
     * Creates PDF File with Print Service Plugin.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void createPdfFile() {

        /* Show Loading Bar */
        toggleLoadingBar(true);

        /* Disable and Hide Print Button */
        handler.postDelayed(new Runnable() {@Override public void run() {togglePrintButton(false);}},500);

        try {

            /* Create an Empty File to be written by Pdf Writer */
            File emptyPdfFile = createEmptyFile();

            /* Get a print adapter instance */
            PrintDocumentAdapter adapter = mWebView.createPrintDocumentAdapter("Foo");
            PdfWriter pdfWriter = new PdfWriter(getActivity(), adapter, emptyPdfFile);

            pdfWriter.write(new PdfWriter.PdfWriterCallback() {

                @Override
                public void onWriteFinished() {

                    /* Hide Loading Bar */
                    handler.postDelayed(new Runnable() {@Override public void run() {toggleLoadingBar(false);}},500);

                    /* Notify User with Success Message */
                    Toast.makeText(getActivity(), "Pdf Created Successfully", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onWriteFailed() {

                    /* Hide Loading Bar */
                    handler.postDelayed(new Runnable() {@Override public void run() {toggleLoadingBar(false);}},500);

                    /* Notify User with Success Message */
                    Toast.makeText(getActivity(), "Failed to Create Pdf File, Make sure the Application has necessary Permissions", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates an Empty Pdf File with HostName as file name.
     *
     * @return
     * @throws IOException
     */
    private File createEmptyFile() throws IOException {

        String fileName = hostName + ".pdf";

        File directory = FileUtils.getExternalBreezyDirectory();
        final File file = new File(directory, fileName);
        file.createNewFile();
        return file;
    }

    /**
     * Retrieves the Host Name of the Current Visible Page in WebView.
     *
     * @return
     */
    private String getHostName() {

        String hostName = "print_website";

        try {
            hostName = getWebsiteURL();
        } catch (MalformedURLException e) {
            /* This Exception won't be thrown in any case, but it's safe to handle it (Give an initial Value to Host Name) */
            e.printStackTrace();
        }

        return hostName;
    }

    /**
     * Retrieves the Host Name of the Current URL in web view.
     *
     * @return
     * @throws MalformedURLException
     */
    private String getWebsiteURL() throws MalformedURLException{
        return new URL(mWebView.getUrl()).getHost();
    }
}
