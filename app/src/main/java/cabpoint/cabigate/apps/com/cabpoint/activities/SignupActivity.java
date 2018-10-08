package cabpoint.cabigate.apps.com.cabpoint.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import cabpoint.cabigate.apps.com.cabpoint.R;

public class SignupActivity extends Activity {
    EditText etFirstNmae, etLastName, etPhonenumber, etEmail, etPassword;
    TextView txtterms,txtdashboard;
    Button btnSignup;
    ImageView ivBack;

    private ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Initialize();

    }

    private void Initialize() {
        etFirstNmae = findViewById(R.id.et_firstname);
        etLastName = findViewById(R.id.et_last_name);
        etPhonenumber = findViewById(R.id.et_phonenumber);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        txtterms = findViewById(R.id.txt_terms);
        btnSignup = findViewById(R.id.btn_signup);
        ivBack = findViewById(R.id.toolbar_iv_back);
      txtdashboard = findViewById(R.id.tv_dashboard);
      txtdashboard.setVisibility(View.VISIBLE);
      txtdashboard.setText("Sign Up");
        customTextView(txtterms);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
        }
    private void customTextView(TextView view) {
        SpannableStringBuilder spanTxt = new SpannableStringBuilder(
                "By Clicking Sign up. you agree to cabpoint's");
        spanTxt.append("Terms of Use");
        spanTxt.setSpan(new ForegroundColorSpan(Color.parseColor("#E63939")), 44, spanTxt.length(), 0);
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                //openTermOrPolicyFiles("file:///android_asset/terms.html",1);  // 1 for terms ;
            }
        }, spanTxt.length() - "Terms of Use".length(), spanTxt.length(), 0);
        spanTxt.append(" and acknowledge you have read the");
        //spanTxt.setSpan(new ForegroundColorSpan(Color.parseColor("#E63939")), 32, spanTxt.length(), 0);
        spanTxt.append(" Privacy Policy");
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                //openTermOrPolicyFiles("file:///android_asset/private_policy.html",2);   // 2 for policy ;
            }
        }, spanTxt.length() - " Privacy Policy".length(), spanTxt.length(), 0);
        view.setMovementMethod(LinkMovementMethod.getInstance());
        view.setText(spanTxt, TextView.BufferType.SPANNABLE);
    }
}
