package de.mwessendorf.wikiduell;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by maximilian on 18.03.17.
 */

public class MenuActivity extends AppCompatActivity {

    private String username = "Marc";
    private String id;
    private boolean isLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        boolean idExists = false;

        try{
            File myFile = new File(this.getApplicationContext().getFilesDir(), "log.txt");
            FileInputStream fIn = new FileInputStream(myFile);
            BufferedReader bis = new BufferedReader(new InputStreamReader(fIn));
            String data = bis.readLine();
            bis.close();
            fIn.close();
            username = data.split(" ")[0];
            id = data.split(" ")[1];
            Log.d(id + " " + username,"error");
        }catch(Exception e){
            Log.d(" " + e.getMessage(),"error");
        }

        if(id!=null && !id.equals("")) {
            idExists = true;
            isLoaded = true;
        } else {
            //final TextView textViewTitle = (TextView) findViewById(R.id.textViewTitle);
            //textViewTitle.setText(username + " " + id);
        }


        if (!idExists) {
            new Thread(new Runnable() {

                @Override
                public void run() {

                    try {
                        URL url = new URL("http://207.154.218.60/create_user" + "?name=" + username);
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
                        id = new String(baos.toByteArray(), encoding);

                    } catch (Exception e) {
                        //System.out.println("some error occured " + e.getMessage() + (wikipediaURL + urlNAme));
                    }
                isLoaded = true;
                }
            }).start();

            while(!isLoaded) {
                try {
                    Thread.sleep(10);
                } catch(Exception e) {}

            }

            try{
                File myFile = new File(this.getApplicationContext().getFilesDir(), "log.txt");
                myFile.createNewFile();
                FileOutputStream fOut = new FileOutputStream(myFile);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                myOutWriter.append(username + " " + id);
                myOutWriter.close();
                fOut.close();
            }catch(Exception e){
                Log.d(" " + e.getMessage(),"error");
            }
        }

        try {
            //final TextView textViewTitle = (TextView) findViewById(R.id.textViewTitle);
            //textViewTitle.setText(username + " " + id);
        } catch (Exception e) {
            //final TextView textViewTitle = (TextView) findViewById(R.id.textViewTitle);
            //textViewTitle.setText(e.getMessage());
        }

    }



    public void startSingleplayer(View view) {
        Intent intent = new Intent(MenuActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void startLeaderboard(View view) {
        Intent intent = new Intent(MenuActivity.this, LeaderboardActivity.class);
        startActivity(intent);
    }

    public void startMultiplayer(View view) {
        Intent intent = new Intent(MenuActivity.this, MultiplayerActivity.class);
        startActivity(intent);
    }

}
