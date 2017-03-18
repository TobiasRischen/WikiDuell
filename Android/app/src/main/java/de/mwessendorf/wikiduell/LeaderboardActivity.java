package de.mwessendorf.wikiduell;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

/**
 * Created by maximilian on 18.03.17.
 */

public class LeaderboardActivity extends AppCompatActivity {
    //ReadDataLocal dataReader = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        /*
        try {
            ReadDataLocal dataReader = new ReadDataLocal();
        } catch (IOException e) {
            System.out.print(e.getMessage());
        }


        final TextView showScoresView = (TextView) findViewById(R.id.showScoresView);
        try {
            showScoresView.setText(dataReader.readAll());
        } catch (Exception e) {

        }
        */

    }

    public void toMainMenu(View view)
    {
        Intent intent = new Intent(LeaderboardActivity.this, MenuActivity.class);
        startActivity(intent);
    }
}
