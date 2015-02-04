package wandoujia.com.newgate.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import wandoujia.com.newgate.R;
import wandoujia.com.newgate.app.AppController;

public class FeedbackActivity extends ActionBarActivity {

    private EditText mDesc;
    private EditText mContact;
    private Button mSubmit;
    private static String PREF_FEEDBACK_DESC = "feedback_desc";
    private static String PREF_FEEDBACK_CONTACT = "feedback_contact";

    private SharedPreferences sp;

    private OnClickListener onSubmitClick = new OnClickListener(){

        @Override
        public void onClick(View v) {
            if(validate()){
                submitFeedback();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_form);

        final Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.feedback_title);

        mDesc = (EditText) findViewById(R.id.form_desc);
        mContact = (EditText) findViewById(R.id.form_contact);
        mSubmit = (Button) findViewById(R.id.form_submit);

        mSubmit.setOnClickListener(onSubmitClick);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        String feedbackDesc = sp.getString(PREF_FEEDBACK_DESC, "");
        String feedbackContact = sp.getString(PREF_FEEDBACK_CONTACT, "");

        mDesc.setText(feedbackDesc);
        mContact.setText(feedbackContact);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    private void submitFeedback(){
        StringRequest sr = new StringRequest(Request.Method.POST, AppController.API_FEEDBACK, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), R.string.tips_received, Toast.LENGTH_LONG).show();
                SharedPreferences.Editor editor = sp.edit();
                editor.remove(PREF_FEEDBACK_CONTACT);
                editor.remove(PREF_FEEDBACK_DESC);
                editor.commit();
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyError e = error;
                System.console().printf(error.getMessage());
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("desc", mDesc.getText().toString());
                params.put("contact", mContact.getText().toString());
                params.put("os_version", System.getProperty("os.version") + " (" + android.os.Build.VERSION.INCREMENTAL + ")");
                params.put("api_level", "" + android.os.Build.VERSION.SDK_INT);
                params.put("device", android.os.Build.DEVICE);
                params.put("model", android.os.Build.MODEL + " ("+ android.os.Build.PRODUCT + ")");
                params.put("udid", ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(sr);
    }

    private boolean validate(){
        String desc = mDesc.getText().toString();
        if(TextUtils.isEmpty(desc.trim())){
            mDesc.requestFocus();
            Toast.makeText(this, R.string.tips_not_desc, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        SharedPreferences.Editor editor = sp.edit();
        if(!TextUtils.isEmpty(mDesc.getText().toString())){
            editor.putString(PREF_FEEDBACK_DESC, mDesc.getText().toString());
            editor.commit();
        }
        if(!TextUtils.isEmpty(mContact.getText().toString())){
            editor.putString(PREF_FEEDBACK_CONTACT, mContact.getText().toString());
            editor.commit();
        }

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

}
