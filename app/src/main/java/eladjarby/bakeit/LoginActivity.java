package eladjarby.bakeit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import eladjarby.bakeit.Dialogs.myProgressDialog;
import eladjarby.bakeit.Models.BaseInterface;
import eladjarby.bakeit.Models.User.UserFirebase;

public class LoginActivity extends Activity {
    private EditText mUsername;
    private EditText mPassword;
    public myProgressDialog mProgressDialog;

    @Override
    protected void onStart() {
        super.onStart();
        mProgressDialog = new myProgressDialog(this);
        // Get the current user (if exist).
        FirebaseUser currentUser = UserFirebase.getCurrentUser();
        // Check if the user is already logged in , if it does , start a new intent to main activity.
        if (currentUser != null) {
            Intent intent = new Intent(LoginActivity.this , MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUsername = (EditText) findViewById(R.id.login_user);
        mPassword = (EditText) findViewById(R.id.login_password);
        TextView forgetBtn = (TextView) findViewById(R.id.login_forgetBtn);
        final Button loginBtn = (Button) findViewById(R.id.login_btn);
        final TextView registerTV = (TextView) findViewById(R.id.login_registerBtn);

        // Catch click on login button and login with user email and password.
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(mUsername.getText().toString(),mPassword.getText().toString());
            }
        });

        // Catch click on register button to register a new user.
        registerTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this , RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Catch click on forget button and start activity to get new password by enter an email.
        forgetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this , ForgetPasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    private void signIn(String email, String password) {
        Log.d("TAG", "signIn:" + email);
        // Validate form before login user
        if (!validateForm()) {
            return;
        }
        mProgressDialog.showProgressDialog();
        UserFirebase.loginAccount(LoginActivity.this, email, password, new BaseInterface.LoginAccountCallBack() {
            @Override
            public void onComplete(FirebaseUser user, Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    // If login successful , move to main activity.
                    Intent intent = new Intent(LoginActivity.this , MainActivity.class);
                    mProgressDialog.hideProgressDialog();
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                mProgressDialog.hideProgressDialog();
                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Validate the login form for each input.
    private boolean validateForm() {
        boolean valid = true;

        String email = mUsername.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mUsername.setError("Required.");
            valid = false;
        } else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mUsername.requestFocus();
            mUsername.setError("Please enter a valid email address.");
            valid = false;
        } else {
            mUsername.setError(null);
        }

        String password = mPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPassword.setError("Required.");
            valid = false;
        } else {
            mPassword.setError(null);
        }

        return valid;
    }
}
