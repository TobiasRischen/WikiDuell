package de.mwessendorf.wikiduell;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {

    private static final String serverRandomURL = "http://207.154.218.60/random_scenario";
    String startURL;
    String finishURL;
    boolean isLoaded=false;
    String startUrlTitle;
    String finishUrlTitle;
    String description;
    //StoreDataLocal dataStorage = null;




    TextView timerTextView;
    long startTime = 0;

    //runs without a timer by reposting this handler at the end of the runnable
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            timerTextView.setText(String.format("%d:%02d", minutes, seconds));

            timerHandler.postDelayed(this, 500);
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final WebView myWebView = (WebView) findViewById(R.id.webview);

        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);


        timerTextView = (TextView) findViewById(R.id.textViewTime);
        timerHandler.removeCallbacks(timerRunnable);
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);


        new Thread(new Runnable(){

            @Override
            public void run() {
                try{
                    URL url = new URL(serverRandomURL);
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
                    Log.d(body, "error");
                    //startURL = body;
                    startURL = body.split("\"start\":\"")[1].split("\",\"end\"")[0];
                    finishURL = body.split("\"start\":\"")[1].split("\",\"end\":\"")[1].split("\",\"complexity\"")[0];



                    //{"start":"/wiki/Uromyces_trifolii-repentis","end":"/wiki/Carduus","complexity":4}
                } catch (Exception e) {
                    Log.d("some error occured " + e.getMessage(), "error");
                    e.printStackTrace();
                }

                try{
                    URL url = new URL("https://de.m.wikipedia.org" + startURL);
                    URLConnection con = url.openConnection();
                    url.openStream();
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
                    URL url = new URL("https://de.m.wikipedia.org" + finishURL);
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
                    description = body.substring(body.indexOf("<p>")+3,body.indexOf("</p>")).replaceAll("\\<[^>]*>","");

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

        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle(finishUrlTitle);
        alertDialog.setMessage(description);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

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
                        "document.querySelector('.header-container').style.display = 'none';" +
                        "})()");
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView  view, String  url){
                if (!url.contains("wikipedia")) {
                    return true;
                } else if (!url.contains("/wiki/")) {
                    return true;
                }

                // Zählt die Clicks hoch
                i += 1;
                final TextView textViewClick = (TextView) findViewById(R.id.textViewClicks);
                textViewClick.setText(String.valueOf(i) + " Clicks");

                // Laed die neue URL
                myWebView.loadUrl("javascript:(function() { " +
                        "document.querySelector('.header-container').style.display = 'none';" +
                        "})()");
                myWebView.loadUrl(url);


                // Wenn die zu ladene Seite die Zeilseite ist
                if (url.equals(finishURL)) {
                    final TextView textViewTime = (TextView) findViewById(R.id.textViewTime);
                    textViewTime.setText(timerTextView.getText().toString());

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
                    ShowFinishedActivity.time = timerTextView.getText().toString();


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
            Intent intent = new Intent(MainActivity.this, ShowFinishedActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }

    }

}
