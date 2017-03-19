package de.mwessendorf.wikiduell;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by maximilian on 19.03.17.
 */

public class JoinActivity extends AppCompatActivity {
    boolean isClosed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);


    }

    public void backMenu(View view) {
        Intent intent = new Intent(JoinActivity.this, MenuActivity.class);
        startActivity(intent);
    }

    public void clickJoin(View view) {
        final EditText mEdit   = (EditText)findViewById(R.id.editText);

        final TextView textViewTitle = (TextView) findViewById(R.id.textView);
        textViewTitle.setText(mEdit.getText().toString());

        new Thread(new Runnable() {

            @Override
            public void run() {

                try {
                    URL url = new URL("http://207.154.218.60/join_game" + "?short_id=" + mEdit.getText().toString());
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
        new Thread(new Runnable() {

            @Override
            public void run() {
                //while() {
                    String jsonData = "";
                    String jsonOld = "";
                    try {
                    /*URL url = new URL("http://207.154.218.60/game_status" + "?userId=" + id + "&gameId=" + gameId);
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
                    if(!jsonData.equals(jsonOld)) {
                        updateTextField(jsonData);
                        jsonOld = jsonData;
                    }
                    Thread.sleep(200);*/
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
               // }
            }
        }).start();

    }
    public void updateTextField(String json) {
        final TextView textViewPlayers = (TextView) findViewById(R.id.textViewPlayers);
        textViewPlayers.setText(json);
    }

}
