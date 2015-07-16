package com.beta.tp3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String current_user = preferences.getString("ACTIVE_USER", "");

        if (!current_user.equals("") ) {

            Intent intent = new Intent(this, MyAppActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            setContentView(R.layout.activity_main);
        }
    }

    public void onClickSubscribe(View view) {
        Intent intent = new Intent(this, SubscribeActivity.class);
        startActivity(intent);
    }

    public void onClickLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
