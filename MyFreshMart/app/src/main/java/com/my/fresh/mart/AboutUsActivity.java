package com.my.fresh.mart;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.util.Locale;

public class AboutUsActivity extends AppCompatActivity {


    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        mWebView = (WebView) findViewById(R.id.mainWebView);
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setDatabasePath(getFilesDir().getParentFile().getPath() + "/databases");

        // If there is a previous instance restore it in the webview
        if (savedInstanceState != null) {
            // TODO: If app was minimized and Locale language was changed, we need to reload webview with changed language
            mWebView.restoreState(savedInstanceState);
        } else {
            // Load webview with current Locale language
            mWebView.loadUrl("file:///android_asset/aboutUs.html?lang=" + Locale.getDefault().getLanguage());
        }


//        TextView textView=findViewById(R.id.tv_about_us);

        String about="<h5>OVERVIEW</h5>\n" +
                "<p>My Fresh Mart is daily need products supplying chain that aims to offer  a wide range of basic home and personal products to customer\\'s door step. Each home utility products - including food, toiletries, beauty products, garments, kitchenware, bed and bath linen, home appliances and more - available at competitive prices in local market and supermarket that our customers appreciate. Our core objective is to offer customers good products at great value.\n" +
                "\n PARIDZ DOOERSTEP SERVICES is started by Mr. Rahul B Khiradkar and his family to address the growing needs of the Indian family. From the launch of its first store in Khamgaon in 2017, With our mission to be the lowest priced retailer in the regions we operate, our business continues to grow with new locations planned in more cities.\n" +
                "\n The supermarket chain of PARIDIZ DOORSTEP SERVICES has its headoffice in Khamgaon Maharashtra DIST Buldhana.\n" +
                "</p>\n" +
                "<h5>OUR MISSION</h5>\n" +
                "At PARIDIZ DOORSTEP SERVICES, we research, identify and make available new products and categories that suit the everyday needs of the Indian family. Our mission is to provide the best value possible for our customers, so that every rupee they spend on shopping with us gives them more value for money than they would get anywhere else.";

//        textView.setText(Html.fromHtml(about));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
