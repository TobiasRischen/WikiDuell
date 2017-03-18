package de.mwessendorf.wikiduell;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import java.io.EOFException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    StoreDataLocal dataStorage = null;
    ReadDataLocal dataReader = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final WebView myWebView = (WebView) findViewById(R.id.webview);

        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        final String startURL = "https://de.m.wikipedia.org/wiki/Karlsruhe";
        final String finishURL = "https://de.m.wikipedia.org/wiki/Land_(Deutschland)";

        final TextView textView9 = (TextView) findViewById(R.id.textViewStartpage);
        textView9.setText(startURL.substring(32));
        final TextView textViewTargetpage = (TextView) findViewById(R.id.textViewFinishpage);
        textViewTargetpage.setText(finishURL.substring(32));

        try {
            StoreDataLocal dataStorage = new StoreDataLocal();
        } catch (IOException e) {

        }
        try {
            ReadDataLocal dataReader = new ReadDataLocal();
        } catch (IOException e) {

        }


        myWebView.loadUrl(startURL);

        myWebView.setWebViewClient(new WebViewClient()
        {
            Integer i = 0;


            @Override
            public boolean shouldOverrideUrlLoading(WebView  view, String  url){
                // Zählt die Clicks hoch
                i += 1;
                final TextView textViewClick = (TextView) findViewById(R.id.textViewClicks);
                textViewClick.setText(String.valueOf(i) + " Clicks");

                // Laed die neue URL
                myWebView.loadUrl(url);


                // Wenn die zu ladene Seite die Zeilseite ist
                if (url.equals(finishURL)) {
                    final TextView textViewTime = (TextView) findViewById(R.id.textViewTime);
                    textViewTime.setText("WIN!");
                    if (dataStorage != null) {
                        try {
                            dataStorage.printString(startURL + " " + finishURL + " " + i);
                        } catch (IOException e) {

                        }
                    }


                    ShowFinishedActivity.startURL = startURL;
                    ShowFinishedActivity.finishURL = finishURL;
                    ShowFinishedActivity.clickNumber = i;


                    changeToShow(view);


                }
                final TextView textViewTime = (TextView) findViewById(R.id.textViewTime);
                try {
                    textViewTime.setText(dataReader.readAll()[0]);
                } catch (Exception e) {

                }

                return true;
            }

        });

    }

    public void changeToShow(View view) {
        try {
            Intent intent = new Intent(MainActivity.this, ShowFinishedActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }

    }

}
