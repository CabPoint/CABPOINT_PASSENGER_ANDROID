package cabpoint.cabigate.apps.com.cabpoint.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cabpoint.cabigate.apps.com.cabpoint.R;
import cabpoint.cabigate.apps.com.cabpoint.database.SharedPreferencesHelper;
import cabpoint.cabigate.apps.com.cabpoint.models.forgetpasswordinput.ForgetPasswordModel;
import cabpoint.cabigate.apps.com.cabpoint.models.forgetpasswordoutput.ForgetOutput;
import cabpoint.cabigate.apps.com.cabpoint.models.loginInput.LoginModel;
import cabpoint.cabigate.apps.com.cabpoint.models.loginoutput.LoginOutput;
import cabpoint.cabigate.apps.com.cabpoint.utilities.Constants;
import cabpoint.cabigate.apps.com.cabpoint.utilities.Helpers;
import cabpoint.cabigate.apps.com.cabpoint.utilities.HttpHandler;

public class LoginActivity extends Activity {
    EditText etEmail, etPassword;

    Button btnSignup, btnLogin, btnForgetPass;
    ImageView ivBack;
    TextView txtdashboard;
    SharedPreferencesHelper sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnSignup = findViewById(R.id.btn_signup);
        btnForgetPass = findViewById(R.id.btn_forget_password);
        btnLogin = findViewById(R.id.btn_login);
        ivBack = findViewById(R.id.toolbar_iv_back);
        ivBack.setVisibility(View.GONE);
        sharedPreferences = new SharedPreferencesHelper(this);
        txtdashboard = findViewById(R.id.tv_dashboard);
        if(sharedPreferences.getBoolean(Constants.isLogin))
        {
            Intent n = new Intent(this,MainActivity.class);
            startActivity(n);
        }

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(i);
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new LoginTask().execute();

            }
        });
        btnForgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showResponseDialog();
            }
        });

    }

    private class LoginTask extends AsyncTask<String, Void, String>

    {

        private HttpHandler httpHandler;
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LoginActivity.this);
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
                String jsonObject = new Gson().toJson(requestBody(), LoginModel.class);
                bodyParams.put("data", jsonObject);
                response = httpHandler.httpPost(Constants.CABPOINT_LOGIN, headerParams, bodyParams, null);
                Log.e("lOGIN Url", Constants.CABPOINT_LOGIN);
                Log.e("body", String.valueOf(bodyParams));
                Log.e("Response", response);

                LoginOutput logincode = new Gson().fromJson(response, LoginOutput.class);
                if (logincode.getStatus().equals("1")) {
                    if (logincode.getResponse() != null) {
                        sharedPreferences.setString(Constants.USERID, logincode.getResponse().getUserid() == null ? null : logincode.getResponse().getUserid());
                        sharedPreferences.setString(Constants.TOKEN, logincode.getResponse().getToken() == null ? null : logincode.getResponse().getToken());
                        sharedPreferences.putBooelan(Constants.isLogin,true);
                        Helpers.displayMessage(LoginActivity.this, true, "Successfully Login");
                        Intent main = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(main);


                    }
                } else {

                    JSONObject json = null;
                    try {
                        json = new JSONObject(response);
                        if (json.getString("error") != null) {
                            String error = json.getString("error");
                            Helpers.displayMessage(LoginActivity.this, true, error);
                        }
                        String errorMessage = json.getString("error_msg");
                        String status = json.getString("status");
                        if (status.equals("0")) {
                            Helpers.displayMessage(LoginActivity.this, true, errorMessage);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }


                //  return response.toString();
            } catch (Exception exception) {
                if (response.equals("")) {
                    Helpers.displayMessage(LoginActivity.this, true, exception.getMessage());
                    //showResponseDialog( mContext.getResources().getString(R.string.alert),exception.getMessage());
                    //pDialog.dismiss();
                } else {
                    Helpers.displayMessage(LoginActivity.this, true, exception.getMessage());
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

    private LoginModel requestBody() {
        LoginModel headerRequest = new LoginModel();
        headerRequest.setAuth("fb");
        headerRequest.setCompanyid(Constants.COMPANYID);
        headerRequest.setEmail(etEmail.getText().toString());
        headerRequest.setPassword(etPassword.getText().toString());
        return headerRequest;
    }

    private class ForgetPasswordTask extends AsyncTask<String, Void, String>

    {

        private HttpHandler httpHandler;
        ProgressDialog pDialog;
        String email = "";

        public ForgetPasswordTask(String email) {
            this.email = email;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LoginActivity.this);
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
                String jsonObject = new Gson().toJson(forgetPasswordrequestBody(email), ForgetPasswordModel.class);
                bodyParams.put("data", jsonObject);
                response = httpHandler.httpPost(Constants.CABPOINT_FORGET_PASSWORD, headerParams, bodyParams, null);
                Log.e("Forget Url", Constants.CABPOINT_FORGET_PASSWORD);
                Log.e("body", String.valueOf(bodyParams));
                Log.e("Response", response);
                ForgetOutput forget = new Gson().fromJson(response, ForgetOutput.class);
                if (forget.getStatus().equals("1")) {
                    if (forget.getResponse() != null) {
                        if (forget.getResponse().getMsg() != null) {

                            Helpers.displayMessage(LoginActivity.this, true, forget.getResponse().getMsg());

                        }


                    }
                } else {

                    JSONObject json = null;
                    try {
                        json = new JSONObject(response);
                        String errorMessage = json.getString("error_msg");
                        String status = json.getString("status");
                        if (status.equals("0")) {
                            Helpers.displayMessage(LoginActivity.this, true, errorMessage);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }


                //  return response.toString();
            } catch (Exception exception) {
                if (response.equals("")) {
                    Helpers.displayMessage(LoginActivity.this, true, exception.getMessage());
                    //showResponseDialog( mContext.getResources().getString(R.string.alert),exception.getMessage());
                    //pDialog.dismiss();
                } else {
                    Helpers.displayMessage(LoginActivity.this, true, exception.getMessage());
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

    private ForgetPasswordModel forgetPasswordrequestBody(String email) {
        ForgetPasswordModel headerRequest = new ForgetPasswordModel();
        headerRequest.setEmail(email);
        headerRequest.setCompanyid(Constants.COMPANYID);
        return headerRequest;
    }
    @Override
    public void onBackPressed() {
        finish();
        moveTaskToBack(true);
        System.exit(0);
    }

    private void showResponseDialog() {
        (LoginActivity.this).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                LayoutInflater li = LayoutInflater.from(LoginActivity.this);
                View promptsView = li.inflate(R.layout.forget_pass_dialouge, null);
                final android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(LoginActivity.this);
                alertDialogBuilder.setView(promptsView);
                final android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();
                ImageView ivClose = promptsView.findViewById(R.id.iv_close);
                TextView tvTitle = promptsView.findViewById(R.id.tv_option);
                EditText tvBody = promptsView.findViewById(R.id.tv_body);
                final String email = tvBody.getText().toString();
                ivClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.cancel();
                    }
                });

                Button btnYes = promptsView.findViewById(R.id.btn_yes);

                //tvTitle.setText(title);
                //tvBody.setText(body);
                //btnYes.setText(getResources().getString(R.string.dismiss));


                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.cancel();
                        new ForgetPasswordTask(email).execute();
                    }
                });
                alertDialogBuilder.setCancelable(true);
                alertDialog.show();

            }
        });
    }
}
