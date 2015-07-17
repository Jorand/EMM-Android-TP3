package com.beta.tp3;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MyAppActivity extends AppActivity {

    ListView mListView;
    private String user_token;
    private MySingleton mySingleton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_app);

        mySingleton = MySingleton.getInstance(this);

        String user_email = preferences.getString("ACTIVE_USER", "");
        String user_name = preferences.getString(user_email + "_name", "");
        user_token = preferences.getString(user_email + "_token", "");

        final TextView hello = (TextView) findViewById(R.id.helloText);
        String welcome = getResources().getString(R.string.act_myapp_welcome);
        hello.setText(welcome + " " + user_name);

        groupsList();

        /*
        String url = "http://questioncode.fr:10007/api/groups";

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, (String) null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        Toast toast = Toast.makeText(getApplicationContext(), "cool", Toast.LENGTH_LONG);
                        toast.show();



                    }

                },

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast toast = Toast.makeText(getApplicationContext(), "non", Toast.LENGTH_LONG);
                        toast.show();
                    }

                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        VolleyApplication.getInstance().getRequestQueue().add(jsObjRequest);
        */



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
            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
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
            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void groupsList() {

        String url = "http://questioncode.fr:10007/api/groups";

        JsonArrayRequest jsObjRequest = new JsonArrayRequest(Request.Method.GET, url, (String) null,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {

                        Log.d("TAG", response.toString());

                        try {

                            ArrayList<Groups> groups = new ArrayList<>();

                            for (int i = 0; i < response.length(); i++) {

                                JSONObject groupObj = (JSONObject) response.get(i);

                                String title = groupObj.getString("name");
                                String id = groupObj.getString("_id");

                                groups.add(new Groups(title, id));

                            }

                            Collections.reverse(groups);

                            setGroupListAdapter(groups);

                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }

                },

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        VolleyLog.d("Error", "Error: " + error.getMessage());
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
    }

    private void setGroupListAdapter(final ArrayList<Groups> groups) {

        GroupListAdapter adapter = new GroupListAdapter(this, groups);

        final ListView listView = (ListView) findViewById(R.id.groupListView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Groups group = groups.get(position);
                Log.d("TE", group.id);

                Intent intent = new Intent(getApplicationContext(), GroupRoomActivity.class);
                intent.putExtra("GROUP_ID", group.id);
                startActivity(intent);
            }
        });
    }

    public void addGroup(MenuItem item) {
        Intent intent = new Intent(getApplicationContext(), NewGroupActivity.class);
        startActivity(intent);
    }


}
