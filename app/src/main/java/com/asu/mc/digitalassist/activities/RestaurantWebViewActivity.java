package com.asu.mc.digitalassist.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.asu.mc.digitalassist.R;
import com.google.android.gms.vision.text.Text;

public class RestaurantWebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_web_view);

        String url = getIntent().getStringExtra("EXTRA_URL");
        if (!url.equals("") && url != null) {
            WebView myWebView = (WebView) findViewById(R.id.restaurant_webview);
            WebSettings webSettings = myWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            myWebView.loadUrl(url);
        }
    }
}
