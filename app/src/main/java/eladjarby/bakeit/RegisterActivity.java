package eladjarby.bakeit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import eladjarby.bakeit.Models.BaseInterface;
import eladjarby.bakeit.Models.User.User;
import eladjarby.bakeit.Models.User.UserFirebase;

public class RegisterActivity extends Activity {
    private FirebaseAuth mAuth;
    private EditText mUsername;
    private EditText mPassword;
    public ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        mUsername = (EditText) findViewById(R.id.register_user);
        mPassword = (EditText) findViewById(R.id.register_password);
        final Button registerBtn = (Button) findViewById(R.id.register_btn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp(mUsername.getText().toString(),mPassword.getText().toString());
            }
        });

        final TextView loginTV = (TextView) findViewById(R.id.register_loginBtn);
        loginTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void signUp(String email, String password) {
        Log.d("TAG", "signUp:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        UserFirebase.registerAccount(RegisterActivity.this, email, password, new BaseInterface.RegisterAccountCallBack() {
            @Override
            public void onComplete(FirebaseUser user, Task<Void> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    UserFirebase.addDBUser(newUser(user.getUid()));
                    hideProgressDialog();
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                hideProgressDialog();
                Toast.makeText(RegisterActivity.this, errorMessage , Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mUsername.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mUsername.setError("Required.");
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

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private User newUser(String userID) {
        String ID = userID;
        String userEmail = ((EditText) findViewById(R.id.register_user)).getText().toString();
        String userTown = ((EditText) findViewById(R.id.register_town)).getText().toString();
        String userStreet = ((EditText) findViewById(R.id.register_street)).getText().toString();
        String userImage = "link";
        String userFirstName = ((EditText) findViewById(R.id.register_firstName)).getText().toString();
        String userLastName = ((EditText) findViewById(R.id.register_lastName)).getText().toString();
        return new User(ID,userEmail,userTown,userStreet,userImage,userFirstName,userLastName);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(RegisterActivity.this , LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
