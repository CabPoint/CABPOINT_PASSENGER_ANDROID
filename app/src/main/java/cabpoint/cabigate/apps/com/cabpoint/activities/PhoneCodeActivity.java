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
import com.alimuzaffar.lib.pin.PinEntryEditText;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import cabpoint.cabigate.apps.com.cabpoint.R;
import cabpoint.cabigate.apps.com.cabpoint.database.SharedPreferencesHelper;
import cabpoint.cabigate.apps.com.cabpoint.models.phoencodeoutput.PhoneCodeInput;
import cabpoint.cabigate.apps.com.cabpoint.models.phonecodeinput.PhoneCodeModel;
import cabpoint.cabigate.apps.com.cabpoint.models.signupRequest.SignupModel;
import cabpoint.cabigate.apps.com.cabpoint.models.signupResponse.SignupResponse;
import cabpoint.cabigate.apps.com.cabpoint.utilities.Constants;
import cabpoint.cabigate.apps.com.cabpoint.utilities.Helpers;
import cabpoint.cabigate.apps.com.cabpoint.utilities.HttpHandler;

public class PhoneCodeActivity extends Activity {
    ImageView ivBack,img_forward;
    TextView txtdashboard;
    SharedPreferencesHelper sharedPreferencesHelper;
    PinEntryEditText sendCodeValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_code);

        ivBack = findViewById(R.id.toolbar_iv_back);
        img_forward = findViewById(R.id.forward);
        sendCodeValue = findViewById(R.id.sendCodeValue);
        ivBack.setVisibility(View.VISIBLE);
        txtdashboard = findViewById(R.id.tv_dashboard);
        sharedPreferencesHelper = new SharedPreferencesHelper(this);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PhoneCodeActivity.this, SignupActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
        img_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new PhoneCodeTask().execute();
            }
        });


    }
    private class PhoneCodeTask extends AsyncTask<String, Void, String>

    {

        private HttpHandler httpHandler;
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(PhoneCodeActivity.this);
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
                String jsonObject = new Gson().toJson(requestBody(), PhoneCodeModel.class);
                bodyParams.put("data", jsonObject);
                response = httpHandler.httpPost(Constants.CABPOINT_VERIFY_PHONE, headerParams, bodyParams, null);
                Log.e("PhoneCode Url", Constants.CABPOINT_VERIFY_PHONE);
                Log.e("body", String.valueOf(bodyParams));
                Log.e("Response", response);
                PhoneCodeInput phonecode = new Gson().fromJson(response, PhoneCodeInput.class);
                if(phonecode.getStatus().equals("1"))
                {
                    if(phonecode.getResponse()!=null)
                    {
                       if(phonecode.getResponse().getStatus().equals("verified") )
                       {
                           Helpers.displayMessage(PhoneCodeActivity.this, true, "Successfully Verified");
                       }

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
                            Helpers.displayMessage(PhoneCodeActivity.this, true, errorMessage);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                }


                //  return response.toString();
            } catch (Exception exception) {
                if (response.equals("")) {
                    Helpers.displayMessage(PhoneCodeActivity.this, true, exception.getMessage());
                    //showResponseDialog( mContext.getResources().getString(R.string.alert),exception.getMessage());
                    //pDialog.dismiss();
                } else {
                    Helpers.displayMessage(PhoneCodeActivity.this, true, exception.getMessage());
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

    private PhoneCodeModel requestBody() {
        PhoneCodeModel headerRequest = new PhoneCodeModel();
       headerRequest.setKeymatch(sharedPreferencesHelper.getString(Constants.KEMATCH));
       headerRequest.setOPT(sendCodeValue.getText().toString());
       headerRequest.setToken(sharedPreferencesHelper.getString(Constants.TOKEN));
       headerRequest.setUserid(Integer.valueOf(sharedPreferencesHelper.getString(Constants.USERID)));
       return headerRequest;
    }
}
