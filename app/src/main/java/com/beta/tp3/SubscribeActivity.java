package com.beta.tp3;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SubscribeActivity extends AppActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);

        // Auto-complete suggestions user account emails
        ArrayList<String> accountsEmails = getUserAccountEmail();
        setAutoCompleteText(R.id.act_subscribe_email, accountsEmails);

        EditText passwordField = (EditText) findViewById(R.id.act_subscribe_password);
        passwordField.setTransformationMethod(new PasswordTransformationMethod());

    }

    public void onClickSubscribe(View view) {

        final String name = getEditTextValue(R.id.act_subscribe_name);
        final String email = getEditTextValue(R.id.act_subscribe_email);
        final String password = getEditTextValue(R.id.act_subscribe_password);

        if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {

            final ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage("Loading...");
            pDialog.show();

            String url = "http://questioncode.fr:10007/api/users";
            String json = "{\"name\": \""+ name +"\", \"email\": \""+ email +"\", \"password\": \""+ password +"\"}";

            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, json,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            pDialog.hide();

                            try {
                                JSONObject json = new JSONObject(response.toString());
                                String token = json.getString("token");

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

                                Toast toast = Toast.makeText(getApplicationContext(), R.string.act_login_error, Toast.LENGTH_LONG);
                                toast.show();
                            }
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
                    return headers;
                }
            };
            VolleyApplication.getInstance().getRequestQueue().add(jsObjRequest);

        } else {
            Toast toast = Toast.makeText(this, R.string.from_empty, Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    public void toggleVisibility(View view) {

        EditText editText = (EditText) findViewById(R.id.act_subscribe_password);

        CheckBox checkBox = (CheckBox) findViewById(R.id.togglePasswordVisibility);

        int start = editText.getSelectionStart();
        int end = editText.getSelectionEnd();

        if (checkBox.isChecked()) {
            editText.setTransformationMethod(null);
        }
        else {
            editText.setTransformationMethod(new PasswordTransformationMethod());
        }

        editText.setSelection(start, end);
    }
}
