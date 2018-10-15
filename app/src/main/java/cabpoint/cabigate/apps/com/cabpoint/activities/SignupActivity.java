package cabpoint.cabigate.apps.com.cabpoint.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.rilixtech.Country;
import com.rilixtech.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import cabpoint.cabigate.apps.com.cabpoint.R;
import cabpoint.cabigate.apps.com.cabpoint.database.SharedPreferencesHelper;
import cabpoint.cabigate.apps.com.cabpoint.models.signupRequest.SignupModel;
import cabpoint.cabigate.apps.com.cabpoint.models.signupResponse.SignupResponse;
import cabpoint.cabigate.apps.com.cabpoint.utilities.Constants;
import cabpoint.cabigate.apps.com.cabpoint.utilities.Helpers;
import cabpoint.cabigate.apps.com.cabpoint.utilities.HttpHandler;

public class SignupActivity extends Activity {
    EditText etFirstNmae, etLastName, etPhonenumber, etEmail, etPassword;
    TextView txtterms, txtdashboard;
    Button btnSignup;
    ImageView ivBack;
    String countryCodeAndroid = "";
    CountryCodePicker ccp;


   public  SharedPreferencesHelper sharedPreferencesHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Initialize();
        sharedPreferencesHelper = new SharedPreferencesHelper(this);
        countryCodeAndroid = ccp.getSelectedCountryCode();

    }

    private void Initialize() {
        etFirstNmae = findViewById(R.id.et_firstname);
        etLastName = findViewById(R.id.et_last_name);
        etPhonenumber = findViewById(R.id.et_phonenumber);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
       // txtterms = findViewById(R.id.txt_terms);
        btnSignup = findViewById(R.id.btn_signup);
        ivBack = findViewById(R.id.toolbar_iv_back);
        //ccp.setDefaultCountryUsingPhoneCode(44);
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SignupTask().execute();
            }
        });
        txtdashboard = findViewById(R.id.tv_dashboard);
        txtdashboard.setVisibility(View.VISIBLE);
        txtdashboard.setText("Sign Up");
       // Helpers.customTextView(txtterms);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected(Country country) {
                countryCodeAndroid = country.getPhoneCode();
                //Toast.makeText(this, "Updated " +  country.getPhoneCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class SignupTask extends AsyncTask<String, Void, String>

    {

        private HttpHandler httpHandler;
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SignupActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setIndeterminate(false);
            pDialog.show();
            pDialog.setCancelable(false);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... Url) {
            String response = "";
            try {
                httpHandler = new HttpHandler();
                HashMap<String, String> headerParams = new HashMap<>();
                HashMap<String, String> bodyParams = new HashMap<>();
                Constants.PHONE_NUMBER = etPhonenumber.getText().toString();
                if (Constants.PHONE_NUMBER.charAt(0) == '0') {
                    Constants.PHONE_NUMBER = "+" + countryCodeAndroid + Constants.PHONE_NUMBER.substring(1);
                } else {
                    Constants.PHONE_NUMBER = "+" + countryCodeAndroid + Constants.PHONE_NUMBER;
                }
                String jsonObject = new Gson().toJson(requestBody(), SignupModel.class);
                bodyParams.put("data", jsonObject);
                response = httpHandler.httpPost(Constants.CABPOINT_SIGNUP, headerParams, bodyParams, null);
                Log.e("Signup Url", Constants.CABPOINT_SIGNUP);
                Log.e("body", String.valueOf(bodyParams));
                Log.e("Response", response);
                SignupResponse signup = new Gson().fromJson(response, SignupResponse.class);
                if(signup.getStatus().equals("1"))
                {
                    if(signup.getResponse()!=null)
                    {
                        sharedPreferencesHelper.setString(Constants.TOKEN,signup.getResponse().getToken() == null? null :signup.getResponse().getToken() );
                        sharedPreferencesHelper.setString(Constants.KEMATCH,signup.getResponse().getKeymatch() == null? null :signup.getResponse().getKeymatch() );
                        sharedPreferencesHelper.setString(Constants.USERID,signup.getResponse().getUserid() == null? null :signup.getResponse().getUserid() );
                        sharedPreferencesHelper.setString(Constants.FNAME,signup.getResponse().getFname() == null? null :signup.getResponse().getFname() );
                        sharedPreferencesHelper.setString(Constants.OPT,signup.getResponse().getOPT() == null? null :signup.getResponse().getOPT());
                        Intent phonecode = new Intent(SignupActivity.this,PhoneCodeActivity.class);
                        startActivity(phonecode);
                    }
                }
                else
                {

                    JSONObject json = null;
                    try {
                        json = new JSONObject(response);
                        String errorMessage = json.getString("error_msg");
                        String status = json.getString("status");
                        if(status.equals("0")) {
                            Helpers.displayMessage(SignupActivity.this, true, errorMessage);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                }


              //  return response.toString();
            } catch (Exception exception) {
                if (response.equals("")) {
                    Helpers.displayMessage(SignupActivity.this, true, exception.getMessage());
                    //showResponseDialog( mContext.getResources().getString(R.string.alert),exception.getMessage());
                    //pDialog.dismiss();
                } else {
                    Helpers.displayMessage(SignupActivity.this, true, exception.getMessage());
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            pDialog.dismiss();

//            parseErrorResponse(result);
        }


    }

    private SignupModel requestBody() {
        SignupModel headerRequest = new SignupModel();
        headerRequest.setCompanyid(Constants.COMPANYID);
        headerRequest.setEmail(etEmail.getText().toString());
        headerRequest.setFname(etFirstNmae.getText().toString());
        headerRequest.setLname(etLastName.getText().toString());
        headerRequest.setPhone(Constants.PHONE_NUMBER);
        headerRequest.setPassword(etPassword.getText().toString());
        return headerRequest;
    }

}
