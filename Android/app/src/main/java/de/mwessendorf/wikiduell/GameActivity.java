package de.mwessendorf.wikiduell;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GameActivity extends AppCompatActivity {

    String startURL;
    String finishURL;
    boolean isLoaded=false;
    String startUrlTitle;
    String finishUrlTitle;
    //StoreDataLocal dataStorage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String s = getIntent().getStringExtra("EXTRA_SESSION_ID");
        startURL = s.split(" ")[1];
        finishURL = s.split(" ")[2];

        final WebView myWebView = (WebView) findViewById(R.id.webview);

        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);



        new Thread(new Runnable(){

            @Override
            public void run() {

                try{
                    URL url = new URL("https://de.m.wikipedia.org" + startURL);
                    URLConnection con = url.openConnection();
                    InputStream in = con.getInputStream();

                    startURL = String.valueOf(con.getURL()).replace("https://de.m.wikipedia.org","");

                    String encoding = con.getContentEncoding();  // ** WRONG: should use "con.getContentType()" instead but it returns something like "text/html; charset=UTF-8" so this value must be parsed to extract the actual encoding
                    encoding = encoding == null ? "UTF-8" : encoding;
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buf = new byte[8192];
                    int len = 0;
                    while ((len = in.read(buf)) != -1) {
                        baos.write(buf, 0, len);
                    }
                    String body = new String(baos.toByteArray(), encoding);

                    Pattern linkPattern = Pattern.compile("(<h1 [^>]+>.+?</h1>)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
                    Matcher pageMatcher = linkPattern.matcher(body);
                    Set<String> links = new TreeSet<String>();
                    while (pageMatcher.find()) {
                        links.add(pageMatcher.group());
                    }
                    startUrlTitle = (((String)links.toArray()[0]).substring(((String)links.toArray()[0]).indexOf(">")+1, ((String)links.toArray()[0]).indexOf("</h1>")));
                } catch (Exception e) {
                    //System.out.println("some error occured " + e.getMessage() + (wikipediaURL + urlNAme));
                }
                try{
                    URL url = new URL("https://de.wikipedia.org" + finishURL);
                    URLConnection con = url.openConnection();
                    InputStream in = con.getInputStream();

                    String encoding = con.getContentEncoding();  // ** WRONG: should use "con.getContentType()" instead but it returns something like "text/html; charset=UTF-8" so this value must be parsed to extract the actual encoding
                    encoding = encoding == null ? "UTF-8" : encoding;
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buf = new byte[8192];
                    int len = 0;
                    while ((len = in.read(buf)) != -1) {
                        baos.write(buf, 0, len);
                    }
                    String body = new String(baos.toByteArray(), encoding);

                    Pattern linkPattern = Pattern.compile("(<h1 [^>]+>.+?</h1>)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
                    Matcher pageMatcher = linkPattern.matcher(body);
                    Set<String> links = new TreeSet<String>();
                    while (pageMatcher.find()) {
                        links.add(pageMatcher.group());
                    }
                    finishUrlTitle = (((String)links.toArray()[0]).substring(((String)links.toArray()[0]).indexOf(">")+1, ((String)links.toArray()[0]).indexOf("</h1>")));
                } catch (Exception e) {
                    //System.out.println("some error occured " + e.getMessage() + (wikipediaURL + urlNAme));
                }
                isLoaded = true;

            }
        }).start();

        while(!isLoaded) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        final TextView textViewStartpage = (TextView) findViewById(R.id.textViewStartpage);
        textViewStartpage.setText(startUrlTitle);
        final TextView textViewTargetpage = (TextView) findViewById(R.id.textViewFinishpage);
        textViewTargetpage.setText(finishUrlTitle);

        startURL = "https://de.m.wikipedia.org" + startURL;
        finishURL = "https://de.m.wikipedia.org" + finishURL;

        myWebView.loadUrl(startURL);

        myWebView.setWebViewClient(new WebViewClient()
        {
            Integer i = 0;

            public void onPageFinished(WebView view, String url)
            {
                myWebView.loadUrl("javascript:(function() { " +
                        "document.querySelector('.header-container').style.display = 'none'; " +
                        "})()");
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView  view, String  url){
                // Zählt die Clicks hoch
                i += 1;
                final TextView textViewClick = (TextView) findViewById(R.id.textViewClicks);
                textViewClick.setText(String.valueOf(i) + " Clicks");

                // Laed die neue URL
                myWebView.loadUrl("javascript:(function() { " +
                        "document.querySelector('.header-container').style.display = 'none'; " +
                        "})()");
                myWebView.loadUrl(url);


                // Wenn die zu ladene Seite die Zeilseite ist
                if (url.equals(finishURL)) {
                    final TextView textViewTime = (TextView) findViewById(R.id.textViewTime);
                    textViewTime.setText("WIN!");

                    /*
                    if (dataStorage != null) {
                        try {
                            dataStorage.printString(startURL + " " + finishURL + " " + i);
                        } catch (IOException e) {
                            System.out.print(e.getMessage());
                        }
                    }
                    */


                    ShowFinishedActivity.startURL = startURL;
                    ShowFinishedActivity.finishURL = finishURL;
                    ShowFinishedActivity.clickNumber = i;


                    changeToShow(view);


                }

                return true;
            }

        });
        /*
        try {
            StoreDataLocal dataStorage = new StoreDataLocal();
        } catch (IOException e) {

        }
        */






    }

    public void changeToShow(View view) {
        try {
            Intent intent = new Intent(GameActivity.this, ShowFinishedActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }

    }

}