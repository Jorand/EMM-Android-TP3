package com.beta.tp3;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NewGroupActivity extends AppActivity {

    private String user_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        String user_email = preferences.getString("ACTIVE_USER", "");
        user_token = preferences.getString(user_email + "_token", "");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        Toast toast = Toast.makeText(getApplicationContext(), id, Toast.LENGTH_LONG);
        toast.show();

        switch (id) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addNewGroup(View view) {

        String url = "http://questioncode.fr:10007/api/groups";

        final String name = getEditTextValue(R.id.act_new_group_name);
        final String emails = getEditTextValue(R.id.act_new_group_emails);

        if (!name.isEmpty()) {

            final ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage("Loading...");
            pDialog.show();

            String json = "{\"name\": \""+ name +"\", \"emails\": [\""+ emails +"\"]}";

            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, json,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            pDialog.hide();

                            Intent mainIntent = new Intent(getApplicationContext(), MyAppActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(mainIntent);
                            finish();
                        }

                    },

                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pDialog.hide();

                            if (error instanceof NoConnectionError) {
                                Toast toast = Toast.makeText(getApplicationContext(), R.string.error_no_connection, Toast.LENGTH_LONG);
                                toast.show();
                            }
                            else {

                                NetworkResponse response = error.networkResponse;

                                if (response != null && response.data != null) {

                                    try {
                                        String responseBody = new String(error.networkResponse.data);
                                        JSONObject jsonObject = new JSONObject(responseBody);
                                        String message = jsonObject.getString("message");
                                        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
                                        toast.show();

                                    } catch (JSONException e) {
                                        e.printStackTrace();

                                        Toast toast = Toast.makeText(getApplicationContext(), R.string.error_oups, Toast.LENGTH_LONG);
                                        toast.show();
                                    }
                                }
                                else {
                                    Toast toast = Toast.makeText(getApplicationContext(), R.string.error_oups, Toast.LENGTH_LONG);
                                    toast.show();
                                }
                            }
                        }

                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", "Bearer " + user_token);
                    return headers;
                }
            };
            VolleyApplication.getInstance().getRequestQueue().add(jsObjRequest);

        } else {
            Toast toast = Toast.makeText(this, R.string.from_empty, Toast.LENGTH_SHORT);
            toast.show();
        }

    }
}
