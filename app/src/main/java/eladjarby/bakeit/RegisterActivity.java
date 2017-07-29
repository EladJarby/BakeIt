package eladjarby.bakeit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import eladjarby.bakeit.Dialogs.myProgressDialog;
import eladjarby.bakeit.Models.BaseInterface;
import eladjarby.bakeit.Models.Model;
import eladjarby.bakeit.Models.User.User;
import eladjarby.bakeit.Models.User.UserFirebase;

public class RegisterActivity extends Activity {
    private static final String JPEG = ".jpeg";
    private FirebaseAuth mAuth;
    private EditText mUsername;
    private EditText mPassword;
    private Bitmap imageBitmap;
    public myProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mProgressDialog = new myProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mUsername = (EditText) findViewById(R.id.register_user);
        mPassword = (EditText) findViewById(R.id.register_password);
        final Button registerBtn = (Button) findViewById(R.id.register_btn);
        ImageView registerImage = (ImageView) findViewById(R.id.register_image);
        registerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
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

        mProgressDialog.showProgressDialog();

        UserFirebase.registerAccount(RegisterActivity.this, email, password, new BaseInterface.RegisterAccountCallBack() {
            @Override
            public void onComplete(FirebaseUser user, Task<Void> task) {
                if (task.isSuccessful()) {
                    final User newUser = newUser(user.getUid());
                    if(imageBitmap != null) {
                        Model.instance.saveImage(imageBitmap, user.getUid() + Model.instance.randomNumber() + JPEG, new BaseInterface.SaveImageListener() {
                            @Override
                            public void complete(String url) {
                                newUser.setUserImage(url);
                                UserFirebase.addDBUser(newUser);
                                mProgressDialog.hideProgressDialog();
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void fail() {

                            }
                        });
                    } else {
                        UserFirebase.addDBUser(newUser);
                        mProgressDialog.hideProgressDialog();
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                mProgressDialog.hideProgressDialog();
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


    private User newUser(String userID) {
        String ID = userID;
        String userEmail = ((EditText) findViewById(R.id.register_user)).getText().toString();
        String userTown = ((EditText) findViewById(R.id.register_town)).getText().toString();
        String userStreet = ((EditText) findViewById(R.id.register_street)).getText().toString();
        String userImage = "";
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

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            int dimension = getSquareCropDimensionForBitmap();
            imageBitmap = ThumbnailUtils.extractThumbnail(imageBitmap, dimension, dimension);
            ImageView recipePhoto = (ImageView) findViewById(R.id.register_image);
            recipePhoto.setVisibility(View.VISIBLE);
            recipePhoto.setImageBitmap(imageBitmap);
        }
    }


    public int getSquareCropDimensionForBitmap()
    {
        //use the smallest dimension of the image to crop to
        return Math.min(imageBitmap.getWidth(), imageBitmap.getHeight());
    }
}
