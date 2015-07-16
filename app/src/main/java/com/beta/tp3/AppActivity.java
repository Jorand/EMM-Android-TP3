package com.beta.tp3;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.ArrayList;

/**
 * Common Class added in AppCompatActivity
 * @author Jorand Le Pape
 */
public abstract class AppActivity extends AppCompatActivity {

    protected SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    /**
     * Get user accounts email
     * See setAutoCompleteText for set this on text field
     * @return List of emails or empty array
     */
    public ArrayList<String> getUserAccountEmail() {

        ArrayList<String> stringArrayList = new ArrayList<>();

        try{
            AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
            Account[] accounts = manager.getAccountsByType("com.google");
            //Pattern emailPattern = Patterns.EMAIL_ADDRESS;

            for (Account account : accounts) {
                stringArrayList.add(account.name);
            }
        }
        catch(Exception e)
        {
            Log.i("Exception", "Exception:" + e) ;
        }

        return stringArrayList;
    }

    /**
     * Add auto-complete suggestions list on text field
     * Use AutoCompleteTextView text field
     * See getUserAccountEmail for example list
     * @param AutoCompleteTextViewId Id of AutoCompleteTextView
     * @param stringArrayList Suggestions list
     */
    public void setAutoCompleteText(int AutoCompleteTextViewId, ArrayList<String> stringArrayList) {

        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(AutoCompleteTextViewId);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, stringArrayList);
        textView.setAdapter(adapter);
    }

    /**
     * Get value of EditText
     * @param EditTextId EditText Id
     * @return Value of field
     */
    public String getEditTextValue(int EditTextId) {

        EditText editTextEmail = (EditText) findViewById(EditTextId);
        return editTextEmail.getText().toString();
    }

    /**
     * Check if device is connected on internet
     * @return boolean
     */
    public boolean deviceIsConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
