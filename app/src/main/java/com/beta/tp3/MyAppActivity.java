package com.beta.tp3;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class MyAppActivity extends AppCompatActivity {

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_app);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String user_email = preferences.getString("ACTIVE_USER", "");
        String user_name = preferences.getString(user_email + "_name", "");
        String token = preferences.getString(user_email + "_token", "");

        TextView hello = (TextView) findViewById(R.id.helloText);
        String welcome = getResources().getString(R.string.act_myapp_welcome);
        hello.setText(welcome + " " + user_name);

        TextView tok = (TextView) findViewById(R.id.tokenText);
        tok.setText("token: " + token);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_app, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_logout) {

            confirmExit();
        }

        return super.onOptionsItemSelected(item);
    }

    public void confirmExit() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(R.string.dialog_exit_msg)
                .setTitle(R.string.dialog_exit_title);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(R.string.yes,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        String user_email = preferences.getString("ACTIVE_USER", "");

                        SharedPreferences.Editor editor = preferences.edit();
                        editor.remove("ACTIVE_USER");
                        editor.remove(user_email + "_token");
                        editor.apply();

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
