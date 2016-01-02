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

import com.android.dx.stock.ProxyBuilder;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

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
     * Creates a PDF Multi Page Document depending on the Ratio of Letter Size.
     * This method does not close the Document. It should be Closed after writing to a File.
     *
     * @return
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private PdfDocument createMultiPagePdfDocument(int webViewWidth, int webViewHeight) {

        /* Find the Letter Size Height depending on the Letter Size Ratio and given Page Width */
        int letterSizeHeight = getLetterSizeHeight(webViewWidth);

        PdfDocument document = new PrintedPdfDocument(getActivity(), getPrintAttributes());

        final int numberOfPages = (webViewHeight/letterSizeHeight) + 1;

        for (int i = 0; i < numberOfPages; i++) {

            int webMarginTop = i*letterSizeHeight;

            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(webViewWidth, letterSizeHeight, i+1).create();
            PdfDocument.Page page = document.startPage(pageInfo);

            /* Scale Canvas */
            page.getCanvas().translate(0, -webMarginTop);
            mWebView.draw(page.getCanvas());

            document.finishPage(page);
        }

        return document;
    }

    /**
     * Creates PDF File with Print Service Plugin.
     */
    private void createPdfFile() {

        /* Disable and Hide Print Button */
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                togglePrintButton(false);
            }
        }, 500);

        final PrintAttributes attrs = new PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                .setColorMode(PrintAttributes.COLOR_MODE_COLOR)
                .setResolution(new PrintAttributes.Resolution("1", "Foo", 300, 300))
                .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                .build();

        // Get a print adapter instance
        final PrintDocumentAdapter adapter = mWebView.createPrintDocumentAdapter("Foo");

        /* Retrieve host name from Current Page's URL */
        final String fileName = "webPage.pdf";

        try {

            // TODO
            PrintDocumentAdapter.LayoutResultCallback layoutResultCallback = getLayoutResultCallback(new MyInvocationHandler());

            adapter.onLayout(attrs, attrs, null, layoutResultCallback, null);

        } catch (Exception e) {
            e.printStackTrace();
        }

        handler.postDelayed(new Runnable() {

            @Override
            public void run() {

                try {

                    File directory = FileUtils.getExternalBreezyDirectory();
                    final File file = new File(directory, fileName);
                    file.createNewFile();

                    // TODO
                    PrintDocumentAdapter.WriteResultCallback writeResultCallback = getWriteResultCallback(new MyInvocationHandler());

                    ParcelFileDescriptor pfd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_WRITE);
                    adapter.onWrite(new PageRange[]{PageRange.ALL_PAGES}, pfd, null, writeResultCallback);

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 10000);
    }

    private PrintDocumentAdapter.LayoutResultCallback test2(PrintDocumentAdapter adapter) throws Exception {
        Constructor<?> ctor = PrintDocumentAdapter.LayoutResultCallback.class.getDeclaredConstructors()[0];
        ctor.setAccessible(true);
        return (PrintDocumentAdapter.LayoutResultCallback) ctor.newInstance(adapter);
    }

    private PrintDocumentAdapter.WriteResultCallback test3(PrintDocumentAdapter adapter) throws Exception {
        Constructor<?> ctor = PrintDocumentAdapter.WriteResultCallback.class.getDeclaredConstructors()[0];
        ctor.setAccessible(true);
        return (PrintDocumentAdapter.WriteResultCallback) ctor.newInstance(adapter);
    }

    private PrintDocumentAdapter.LayoutResultCallback getLayoutResultCallback(InvocationHandler invocationHandler) throws  IOException {
        return ProxyBuilder.forClass(PrintDocumentAdapter.LayoutResultCallback.class)
                .dexCache(FileUtils.getDexCacheDirectory(getActivity()))
                .handler(invocationHandler)
                .build();
    }

    private PrintDocumentAdapter.WriteResultCallback getWriteResultCallback(InvocationHandler invocationHandler) throws  IOException {
        return ProxyBuilder.forClass(PrintDocumentAdapter.WriteResultCallback.class)
                .dexCache(FileUtils.getDexCacheDirectory(getActivity()))
                .handler(invocationHandler)
                .build();
    }

    private class MyInvocationHandler implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return null;
        }
    }
}
