package de.mwessendorf.wikiduell;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by maximilian on 18.03.17.
 */

public class ShowFinishedActivity extends AppCompatActivity {
    public static String startURL = "";
    public static String finishURL = "";
    public static int clickNumber = 0;
    public static String time = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showfinished);


        final TextView startURLView = (TextView) findViewById(R.id.startURLView);
        startURLView.setText(startURL);

        final TextView finishURLView = (TextView) findViewById(R.id.finishURLView);
        finishURLView.setText(finishURL);

        final TextView clicksView = (TextView) findViewById(R.id.clicksView);
        clicksView.setText(String.valueOf(clickNumber));

        final TextView timeView = (TextView) findViewById(R.id.timeView);
        timeView.setText(time);

    }

    public void toMainMenu(View view)
    {
        Intent intent = new Intent(ShowFinishedActivity.this, MenuActivity.class);
        startActivity(intent);
    }
}
