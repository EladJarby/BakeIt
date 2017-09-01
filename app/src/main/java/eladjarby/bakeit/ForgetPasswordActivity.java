package eladjarby.bakeit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import eladjarby.bakeit.Dialogs.myProgressDialog;
import eladjarby.bakeit.Models.BaseInterface;
import eladjarby.bakeit.Models.User.UserFirebase;

public class ForgetPasswordActivity extends Activity {

    private myProgressDialog mProgressDialog;
    private EditText emailET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        mProgressDialog = new myProgressDialog(this);
        emailET = (EditText) findViewById(R.id.forget_email);
        TextView loginBtn = (TextView) findViewById(R.id.forget_loginBtn);
        Button resetBtn = (Button) findViewById(R.id.forget_resetBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate form before forget password
                if (!validateForm()) {
                    return;
                }

                String email = emailET.getText().toString().trim();
                mProgressDialog.showProgressDialog();
                UserFirebase.sendPasswordResetEmail(email, new BaseInterface.ForgetPasswordCallBack() {
                    @Override
                    public void onComplete(String successMessage) {
                        Toast.makeText(ForgetPasswordActivity.this,successMessage,Toast.LENGTH_SHORT).show();
                        mProgressDialog.hideProgressDialog();
                        onBackPressed();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(ForgetPasswordActivity.this,errorMessage,Toast.LENGTH_SHORT).show();
                        mProgressDialog.hideProgressDialog();
                    }
                });
            }
        });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = emailET.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailET.requestFocus();
            emailET.setError("Required.");
            valid = false;
        } else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailET.requestFocus();
            emailET.setError("Please enter a valid email address.");
            valid = false;
        } else {
            emailET.setError(null);
        }

        return valid;
    }

    // Catch back press and return to login activity.
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ForgetPasswordActivity.this , LoginActivity.class);
        startActivity(intent);
        finish();
    }
}