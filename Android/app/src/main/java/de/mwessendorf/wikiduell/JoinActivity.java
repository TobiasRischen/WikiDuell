package de.mwessendorf.wikiduell;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

/**
 * Created by maximilian on 19.03.17.
 */

public class JoinActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);


    }

    public void backMenu(View view) {
        Intent intent = new Intent(JoinActivity.this, MenuActivity.class);
        startActivity(intent);
    }

    public void clickJoin() {
        EditText mEdit   = (EditText)findViewById(R.id.editText);
        
    }
}
