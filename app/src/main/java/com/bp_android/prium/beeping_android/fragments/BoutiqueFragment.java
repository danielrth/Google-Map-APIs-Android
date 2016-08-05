package com.bp_android.prium.beeping_android.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bp_android.prium.beeping_android.MainActivity;
import com.bp_android.prium.beeping_android.R;

/**
 * Created by Vaibhav on 2/23/16.
 */
public class BoutiqueFragment extends android.support.v4.app.Fragment {

    public BoutiqueFragment() {

    }

    WebView myWebView;
    final static String myBlogAddr = "http://www.clubic.com/gps/actualite-773528-beepings-zen-gps-tracker-economique-autonome.html";
    String myUrl;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_webfragment, container, false);
        ((MainActivity) getActivity()).setToolbarColor(Color.BLACK);
        myWebView = (WebView) view.findViewById(R.id.mywebview);

        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new MyWebViewClient());

        if (myUrl == null) {
            myUrl = myBlogAddr;
        }
        myWebView.loadUrl(myUrl);

        return view;

    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            myUrl = url;
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }
}
