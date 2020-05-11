package com.example.ac_twitterclone;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.shashank.sony.fancytoastlib.FancyToast;

public class activity_login extends AppCompatActivity implements View.OnClickListener {

    private EditText edtEmailLogin,edtPasswordLogin;
    private Button btnSignUpActivity,btnLoginActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setTitle("Log In");

        edtEmailLogin=findViewById(R.id.edtEmailLogin);
        edtPasswordLogin=findViewById(R.id.edtPasswordLogin);
        btnSignUpActivity=findViewById(R.id.btnSignUpActivity);
        btnLoginActivity=findViewById(R.id.btnLogInActivity);
        edtPasswordLogin.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if(keyCode==KeyEvent.KEYCODE_ENTER && event.getAction()==KeyEvent.ACTION_DOWN){
                    onClick(btnLoginActivity);
                }
                return false;
            }
        });

        btnLoginActivity.setOnClickListener(this);
        btnSignUpActivity.setOnClickListener(this);

        if(ParseUser.getCurrentUser()!=null){
            ParseUser.getCurrentUser().logOut();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnLogInActivity:
                if(edtEmailLogin.getText().toString().equals("") || edtPasswordLogin.getText().toString().equals("")) {
                    FancyToast.makeText(activity_login.this, "Email, Username, Password is required!", FancyToast.LENGTH_LONG, FancyToast.INFO, false).show();
                }else {
                    final ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.setMessage("Logging In " + edtEmailLogin.getText().toString());
                    progressDialog.show();
                    ParseUser.logInInBackground(edtEmailLogin.getText().toString(), edtPasswordLogin.getText().toString(), new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            if (user != null && e == null) {
                                FancyToast.makeText(activity_login.this, user.get("username") + " is logged in Successfully.", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
                                transitionToSocialMediaActivity();

                            } else {
                                FancyToast.makeText(activity_login.this, e.getMessage(), FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show();
                            }
                            progressDialog.dismiss();
                        }
                    });
                }
                break;
            case R.id.btnSignUpActivity:
                Intent intent = new Intent(activity_login.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
    public void loginLayoutTapped(View view){
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void transitionToSocialMediaActivity(){
        Intent intent=new Intent(activity_login.this,twitter_user.class);
        startActivity(intent);
        finish();
    }
}
