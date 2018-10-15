package cabpoint.cabigate.apps.com.cabpoint.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import cabpoint.cabigate.apps.com.cabpoint.R;

public class PhoneCodeActivity extends Activity {
    EditText etEmail, etPassword;

    Button btnSignup,btnLogin,btnForgetPass;
    ImageView ivBack;
    TextView txtdashboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnSignup = findViewById(R.id.btn_signup);
        ivBack = findViewById(R.id.toolbar_iv_back);
        ivBack.setVisibility(View.GONE);
        txtdashboard = findViewById(R.id.tv_dashboard);


    }
}
