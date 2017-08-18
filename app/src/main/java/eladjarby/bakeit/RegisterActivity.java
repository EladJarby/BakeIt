package eladjarby.bakeit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eladjarby.bakeit.Dialogs.myProgressDialog;
import eladjarby.bakeit.Models.BaseInterface;
import eladjarby.bakeit.Models.Model;
import eladjarby.bakeit.Models.User.User;
import eladjarby.bakeit.Models.User.UserFirebase;
import eladjarby.bakeit.Util.HttpHandler;

import static com.facebook.login.widget.ProfilePictureView.TAG;

public class RegisterActivity extends Activity {
    private static final String JPEG = ".jpeg";
    private FirebaseAuth mAuth;
    private EditText mUsername;
    private EditText mPassword;
    private Bitmap imageBitmap;
    public myProgressDialog mProgressDialog;
    private EditText registerCity;

    private static String url = "http://api.geonames.org/searchJSON?username=bakeit&country=il&maxRows=1000&style=SHORT";
    Set<String> citiesSet;
    ArrayList<String> citiesList;
    private ProgressDialog pDialog;

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
        registerCity = (EditText) findViewById(R.id.register_city);
//        ImageView registerSearchFilter = (ImageView) findViewById(R.id.register_city_search_filter);
        citiesSet = new HashSet<String>();
        citiesList = new ArrayList<String>();
        new GetCities().execute();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, getCitiesList());
        AutoCompleteTextView registerCity = (AutoCompleteTextView) findViewById(R.id.register_city);
        registerCity.setAdapter(adapter);
//        registerSearchFilter.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                searchGoogleTown();
//            }
//        });
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
        } else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mUsername.setError("Please enter a valid email address.");
            valid = false;
        } else {
            mUsername.setError(null);
        }

        String password = mPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPassword.setError("Required.");
            valid = false;
        } else if(mPassword.length() < 6) {
            mPassword.setError("Password length should be higher then 6");
            valid = false;
        } else {
            mPassword.setError(null);
        }

        return valid;
    }


    private User newUser(String userID) {
        String ID = userID;
        String userEmail = ((EditText) findViewById(R.id.register_user)).getText().toString();
        String userImage = "https://firebasestorage.googleapis.com/v0/b/bakeit-f8116.appspot.com/o/avatar.png?alt=media&token=e3b93b4f-2fd8-4cdd-a03d-5d1116d1367f";
        String userTown = ((EditText) findViewById(R.id.register_city)).getText().toString();
        String userFirstName = ((EditText) findViewById(R.id.register_firstName)).getText().toString();
        String userLastName = ((EditText) findViewById(R.id.register_lastName)).getText().toString();
        return new User(ID,userEmail,userTown,userImage,userFirstName,userLastName);
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

//    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 2;
//    private void searchGoogleTown() {
//        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
//                .setCountry("IL")
//                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
//                .build();
//        try {
//            Intent intent =
//                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
//                            .setFilter(typeFilter)
//                            .build(this);
//            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
//        } catch (GooglePlayServicesRepairableException e) {
//            // TODO: Handle the error.
//        } catch (GooglePlayServicesNotAvailableException e) {
//            // TODO: Handle the error.
//        }
//    }
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
//        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
//            if (resultCode == RESULT_OK) {
//                Place place = PlaceAutocomplete.getPlace(this, data);
//                registerCity.setText(place.getName());
//            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
//                Status status = PlaceAutocomplete.getStatus(this, data);
//                // TODO: Handle the error.
//                Log.i(TAG, status.getStatusMessage());
//
//            } else if (resultCode == RESULT_CANCELED) {
//                // The user canceled the operation.
//            }
//        }
    }


    public int getSquareCropDimensionForBitmap()
    {
        //use the smallest dimension of the image to crop to
        return Math.min(imageBitmap.getWidth(), imageBitmap.getHeight());
    }

    private class GetCities extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(RegisterActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e("TAG", "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray cities = jsonObj.getJSONArray("geonames");

                    // looping through All Contacts
                    for (int i = 0; i < cities.length(); i++) {
                        JSONObject c = cities.getJSONObject(i);
                        String name = c.getString("name");
                        citiesSet.add(name);
                    }
                    citiesList.addAll(citiesSet);
                } catch (final JSONException e) {
                    Log.e("TAG", "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e("TAG", "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
        }
    }

    public List<String> getCitiesList() {
        return citiesList;
    }
}
