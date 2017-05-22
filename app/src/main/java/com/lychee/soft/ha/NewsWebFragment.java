package com.lychee.soft.ha;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewsWebFragment extends WebViewFragment {
    private String mURL;

    public static NewsWebFragment getInstance(String url) {
        NewsWebFragment f = new NewsWebFragment();
        Bundle args = new Bundle();
        args.putString("url", url);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mURL = getArguments().getString("url");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    /*    ProgressDialog progressDialog = new ProgressDialog(ContestActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
*/
        final WebView webView = getWebView();
        webView.setBackgroundColor(0x00000000); // set background to transparent
        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
                    Log.d("webView OnKey","Go back");
                    ProgressBar pb = (ProgressBar)getActivity().findViewById(R.id.progressBar);
                    if (pb != null)
                        pb.setVisibility(View.GONE);
                    webView.goBack();
                    return true;
                }
                return false;
            }
        });
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url,Bitmap favicon){
                super.onPageStarted(view,url,favicon);
              //  view.setBackgroundColor(getActivity().getApplicationContext().getResources().getColor(R.color.white));
                ProgressBar pb = (ProgressBar)getActivity().findViewById(R.id.progressBar);
                if (pb!=null)
                 pb.setVisibility(View.VISIBLE);
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                try {
                    super.onPageFinished(view, url);
                    ProgressBar pb = (ProgressBar)getActivity().findViewById(R.id.progressBar);
                    if (pb != null)
                      pb.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                    //Log.d("N-Web-Fr OnPageFinished",e.getMessage());
                }
            }
        });
/*
        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
                    ProgressBar pb = (ProgressBar)getActivity().findViewById(R.id.progressBar);
                    pb.setVisibility(View.GONE);
                    if(webView.canGoBack() == true){
                        Log.d("setOnClickListener","goBack");
                        webView.goBack();
                    }else{
                        Log.d("setOnClickListener","can't goBack");
                    }

                    return true;
                }
                return true;
            }
        });
        */



        if (webView != null) {
            if (webView.getOriginalUrl() == null) {
                webView.getSettings().setJavaScriptEnabled(true);
                webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
                webView.loadUrl(mURL);
            }
        }


    }
}