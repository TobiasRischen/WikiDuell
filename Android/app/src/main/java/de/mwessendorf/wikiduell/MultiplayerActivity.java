package de.mwessendorf.wikiduell;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by maximilian on 19.03.17.
 */

public class MultiplayerActivity extends AppCompatActivity {
    public String Code = "1111";
    private boolean isLoaded = false;
    private String id;
    private String[] gameData;
    private String username;
    boolean isClosed = false;
    boolean changedStatus = false;

    String gameId;
    String startUrl;
    String endUrl;
    String shortID;
    String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joingame);

        try{
            File myFile = new File(this.getApplicationContext().getFilesDir(), "log.txt");
            FileInputStream fIn = new FileInputStream(myFile);
            BufferedReader bis = new BufferedReader(new InputStreamReader(fIn));
            String data = bis.readLine();
            bis.close();
            fIn.close();
            username = data.split(" ")[0];
            id = data.split(" ")[1].replace("\"}","").replace("{\"userid\":\"","");
            Log.d(id + " " + username,"error");
        }catch(Exception e){
            Log.d(" " + e.getMessage(),"error");
        }

        new Thread(new Runnable() {

            @Override
            public void run() {



                try {
                    URL url = new URL("http://207.154.218.60/create_game" + "?userId=" + id);
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
                     data  = new String(baos.toByteArray(), encoding);
                     gameId = data.substring(11).split("\",\"")[0];
                     startUrl = data.substring(data.indexOf(",\"start\":\"")+10).split("\",\"end\":\"")[0];
                     endUrl = data.substring(data.indexOf(",\"start\":\"")).split("\",\"end\":\"")[1].split("\",\"shortId\"")[0];
                     shortID = data.substring(data.indexOf("\"shortId\":\"")+11).replace("\"}","");
                    //{"gameId":"1ebc7d2c-88f1-42cc-af3b-89569f408905","start":"/wiki/DDR-Meisterschaften_im_Biathlon_1974","end":"/wiki/IBU-Cup","shortId":"1EDF"}
                } catch (Exception e) {
                    e.printStackTrace();
                }
                isLoaded = true;
            }
        }).start();

        while(!isLoaded) {
            try {
                Thread.sleep(10);
            } catch(Exception e) {}

        }

        final TextView textViewCode = (TextView) findViewById(R.id.textViewCode);
        textViewCode.setText(shortID);

        new Thread(new Runnable() {

            @Override
            public void run() {

                while(!changedStatus) {
                    String jsonData = "";
                    String jsonOld = "";
                    try {
                        URL url = new URL("http://207.154.218.60/game_status" + "?userId=" + id + "&gameId=" + gameId);
                        url.openStream();
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
                        jsonData = new String(baos.toByteArray(), encoding);
                        if (!jsonData.equals(jsonOld)) {
                            updateTextField(jsonData);
                            jsonOld = jsonData;
                        }
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void updateTextField(String json) {
        final TextView textViewPlayers = (TextView) findViewById(R.id.textViewPlayers);
        textViewPlayers.setText(json);
    }

    public void closeGame(View view) {
        changedStatus = true;
        new Thread(new Runnable() {

            @Override
            public void run() {

                try {
                    URL url = new URL("http://207.154.218.60/end_game" + "?userId=" + id + "&gameId=" + gameId);
                    url.openStream();
                    URLConnection con = url.openConnection();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                isClosed = true;
            }
        }).start();

        while (!isClosed) {
            try {
                Thread.sleep(10);
            }catch(Exception e) {

            }
        }
        Intent intent = new Intent(MultiplayerActivity.this, MenuActivity.class);
        startActivity(intent);
    }

    public void startGame(View view) {
        changedStatus = true;
        new Thread(new Runnable() {

            @Override
            public void run() {

                try {
                    URL url = new URL("http://207.154.218.60/start_game" + "?userId=" + id + "&gameId=" + gameId);
                    url.openStream();
                    URLConnection con = url.openConnection();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                isClosed = true;
            }
        }).start();

        while (!isClosed) {
            try {
                Thread.sleep(10);
            }catch(Exception e) {

            }
        }
        Intent intent = new Intent(MultiplayerActivity.this, GameActivity.class);
        intent.putExtra("EXTRA_SESSION_ID", "" + gameId + " " + startUrl + " " + endUrl + " " + shortID);
        startActivity(intent);
    }
}
