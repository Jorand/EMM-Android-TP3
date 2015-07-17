package com.beta.tp3;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GroupRoomActivity extends AppActivity {

    private String groupId;
    private String user_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_room);

        String user_email = preferences.getString("ACTIVE_USER", "");
        user_token = preferences.getString(user_email + "_token", "");

        Bundle extras = getIntent().getExtras();

        groupId = extras.getString("GROUP_ID");
        Log.d("TE", groupId);

        getMessage();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group_room, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getMessage() {

        String url = "http://questioncode.fr:10007/api/groups/"+ groupId +"/messages";

        JsonArrayRequest jsObjRequest = new JsonArrayRequest(Request.Method.GET, url, (String) null,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {

                        Log.d("TAG", response.toString());

                        try {

                            ArrayList<Message> messages = new ArrayList<>();

                            for (int i = 0; i < response.length(); i++) {

                                JSONObject messageObj = (JSONObject) response.get(i);


                                String sendDate = messageObj.getString("creation_date");
                                String message = messageObj.getString("content");

                                JSONObject senderObj = (JSONObject) messageObj.get("_creator");

                                String sendId = senderObj.getString("_id");
                                String fromName = senderObj.getString("name");

                                String user_email = preferences.getString("ACTIVE_USER", "");

                                messages.add(new Message(sendDate, fromName, message, false));

                            }

                            setMessageListAdapter(messages);

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

    private void setMessageListAdapter(final ArrayList<Message> messages) {

        MessagesListAdapter adapter = new MessagesListAdapter(this, messages);

        final ListView listView = (ListView) findViewById(R.id.list_view_messages);
        listView.setAdapter(adapter);

    }

    public void sendMessage(View view) {

        EditText inputMsg = (EditText) findViewById(R.id.inputMsg);

        final String msg = inputMsg.getText().toString();

        sendMessageToServer(msg);

        inputMsg.setText("");
    }

    private void sendMessageToServer(String message) {

        if (!message.isEmpty()) {

            final ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage("Loading...");
            pDialog.show();

            String url = "http://questioncode.fr:10007/api/messages";
            String json = "{\"group\": \""+ groupId +"\", \"content\": \""+ message +"\"}";

            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, json,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            pDialog.hide();

                            Toast toast = Toast.makeText(getApplicationContext(), R.string.act_msg_sended, Toast.LENGTH_LONG);
                            toast.show();


                        }

                    },

                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pDialog.hide();

                            Toast t = Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT);
                            t.show();

                            if (error instanceof NoConnectionError) {
                                Toast toast = Toast.makeText(getApplicationContext(), R.string.error_no_connection, Toast.LENGTH_LONG);
                                toast.show();
                            }
                            else {

                                NetworkResponse response = error.networkResponse;

                                if (response != null && response.data != null) {

                                    if (response.statusCode == 400) {
                                        Toast toast = Toast.makeText(getApplicationContext(), R.string.error_server_off, Toast.LENGTH_LONG);
                                        toast.show();
                                    }
                                    else if (response.statusCode == 422) {
                                        Toast toast = Toast.makeText(getApplicationContext(), R.string.error_user_exist, Toast.LENGTH_LONG);
                                        toast.show();
                                    }
                                    else {
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
                                }
                            }
                        }

                    }
            ) {
                /**
                 * Passing some request headers
                 **/
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
