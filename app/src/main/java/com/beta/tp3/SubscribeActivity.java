package com.beta.tp3;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SubscribeActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        final ArrayList<String> stringArrayList = new ArrayList<>();

        try{
            AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
            Account[] accounts = manager.getAccountsByType("com.google");

            for (Account account : accounts) {
                stringArrayList.add(account.name);
            }

            AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.act_subscribe_email);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, stringArrayList);
            textView.setAdapter(adapter);

        }
        catch(Exception e)
        {
            Log.i("Exception", "Exception:" + e) ;
        }
    }

    public void onClickSubscribe(View view) {

        EditText editTextName = (EditText) findViewById(R.id.act_subscribe_name);
        final String name = editTextName.getText().toString();

        EditText editTextEmail = (EditText) findViewById(R.id.act_subscribe_email);
        final String email = editTextEmail.getText().toString();

        EditText editTextPassword = (EditText) findViewById(R.id.act_subscribe_password);
        final String password = editTextPassword.getText().toString();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {

            Toast toast = Toast.makeText(this, R.string.from_empty, Toast.LENGTH_SHORT);
            toast.show();
        }
        else {

            String json = "{\"name\": \""+ name +"\", \"email\": \""+ email +"\", \"password\": \""+ password +"\"}";

            if (isOnline()) {

                JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, "http://questioncode.fr:10007/api/users", json,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {

                                String token;

                                JSONObject json;

                                try {
                                    json = new JSONObject(response.toString());
                                    token = json.getString("token");

                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString(email + "_name", name);
                                    editor.putString(email + "_password", password);
                                    editor.putString(email + "_token", token);
                                    editor.putString("ACTIVE_USER", email);
                                    editor.apply();

                                    Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(mainIntent);
                                    finish();

                                } catch (JSONException e) {

                                }

                            }
                        },

                        new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {

                            /*
                             * http://stackoverflow.com/a/21868734/1969761
                             */

                                String json;

                                NetworkResponse response = error.networkResponse;
                                if (response.data != null) {

                                    if (response.statusCode == 422) {
                                        Toast toast = Toast.makeText(getApplicationContext(), R.string.error_user_exist, Toast.LENGTH_LONG);
                                        toast.show();
                                    } else if (response.statusCode == 400) {
                                        Toast toast = Toast.makeText(getApplicationContext(), R.string.error_server_off, Toast.LENGTH_LONG);
                                        toast.show();
                                    } else {
                                        json = new String(response.data);
                                        json = MySingleton.trimMessage(json, "message");
                                        if (json != null) {
                                            Toast toast = Toast.makeText(getApplicationContext(), json, Toast.LENGTH_LONG);
                                            toast.show();
                                        }
                                    }
                                }
                            }
                        }
                );
                VolleyApplication.getInstance().getRequestQueue().add(jsObjRequest);
            }
            else {
                Toast toast = Toast.makeText(getApplicationContext(), R.string.error_offline, Toast.LENGTH_LONG);
                toast.show();
            }

        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
