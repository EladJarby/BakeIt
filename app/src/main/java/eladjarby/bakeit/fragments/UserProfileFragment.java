package eladjarby.bakeit.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import eladjarby.bakeit.Dialogs.myProgressDialog;
import eladjarby.bakeit.MainActivity;
import eladjarby.bakeit.Models.BaseInterface;
import eladjarby.bakeit.Models.Model;
import eladjarby.bakeit.Models.ModelFiles;
import eladjarby.bakeit.Models.User.User;
import eladjarby.bakeit.Models.User.UserFirebase;
import eladjarby.bakeit.R;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class UserProfileFragment extends Fragment {
    private static final String JPEG = ".jpeg";
    View contentView;
    User user;
    private OnFragmentInteractionListener mListener;
    private Bitmap imageBitmap;
    private myProgressDialog mProgressDialog;
    private EditText profileCity;
    private EditText profileFirstName;
    private EditText profileLastName;

    public UserProfileFragment() {}

    // New instance for user profile.
    public static UserProfileFragment newInstance() {
        UserProfileFragment fragment = new UserProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        contentView = inflater.inflate(R.layout.fragment_user_profile, container, false);

        mProgressDialog = new myProgressDialog(getActivity());

        // Get the action bar from activity.
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Set menu
        ImageView menuAdd = (ImageView) getActivity().findViewById(R.id.menu_add);
        ImageView menuProfile = (ImageView) getActivity().findViewById(R.id.menu_profile);
        TextView menuTitle = (TextView) getActivity().findViewById(R.id.menu_title);
        TextView menuTitleBakeIt = (TextView) getActivity().findViewById(R.id.menu_title_bakeit);
        menuTitleBakeIt.setVisibility(View.GONE);
        menuTitle.setVisibility(View.VISIBLE);
        menuAdd.setVisibility(View.GONE);
        menuProfile.setVisibility(View.GONE);
        menuTitle.setText("User Details");

        // Get logged in current user
        user = Model.instance.getCurrentUser();
        // Get all user details to set input fields.
        getUserDetails();

        Button updateBtn = (Button) contentView.findViewById(R.id.profile_update_btn);
        ImageView profileImage = (ImageView) contentView.findViewById(R.id.profile_image);
        Button logoutBtn = (Button) contentView.findViewById(R.id.profile_logout);

        // Set array adapter for cities autocomplete.
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, ((MainActivity) getActivity()).getCitiesList());
        AutoCompleteTextView profileCity = (AutoCompleteTextView) contentView.findViewById(R.id.profile_city);
        profileCity.setAdapter(adapter);

        // Catch click on profile image for take a new profile image.
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        // Catch click on log out button , to log out from app.
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.logout();
            }
        });
        // Catch click on update button , to update the user details.
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateForm()) {
                    return;
                }
                // Close keyboard.
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                mProgressDialog.showProgressDialog();
                final User user = updateUser();
                if(imageBitmap != null) {
                    Model.instance.saveImage(imageBitmap, user.getID() + Model.instance.randomNumber() + JPEG, new BaseInterface.SaveImageListener() {
                        @Override
                        public void complete(String url) {
                            user.setUserImage(url);
                            // Update user details.
                            UserFirebase.addDBUser(user);
                            ((MainActivity)getActivity()).showMenu();
                            mProgressDialog.hideProgressDialog();
                            getFragmentManager().popBackStack();
                        }

                        @Override
                        public void fail() {
                            Log.d("TAG","fail to save image.");
                        }
                    });
                } else {
                    // Update user details.
                    UserFirebase.addDBUser(user);
                    ((MainActivity)getActivity()).showMenu();
                    mProgressDialog.hideProgressDialog();
                    getFragmentManager().popBackStack();
                }
            }
        });
        return contentView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void logout();
    }

    // Update user with new details.
    private User updateUser() {
        String userId = null;
        String userEmail = null;
        String userImage = null;
        String userFirstName = ((EditText) contentView.findViewById(R.id.profile_firstName)).getText().toString();
        String userLastName = ((EditText) contentView.findViewById(R.id.profile_lastName)).getText().toString();
        String userCity = ((EditText) contentView.findViewById(R.id.profile_city)).getText().toString();
        userId = user.getID();
        userEmail = user.getUserEmail();
        userImage = user.getUserImage();
        return new User(userId,userEmail,userCity,userImage,userFirstName,userLastName);

    }

    // Get user details to fill the input fields and images.
    private void getUserDetails() {
        profileFirstName = (EditText) contentView.findViewById(R.id.profile_firstName);
        profileLastName = (EditText) contentView.findViewById(R.id.profile_lastName);
        profileCity = (EditText) contentView.findViewById(R.id.profile_city);
        profileFirstName.setText(user.getUserFirstName());
        profileLastName.setText(user.getUserLastName());
        profileCity.setText(user.getUserTown());
        final ProgressBar profileProgressBar = (ProgressBar) contentView.findViewById(R.id.profileProgressBar);
        profileProgressBar.setVisibility(View.VISIBLE);
        Model.instance.getImage(user.getUserImage(), new BaseInterface.GetImageListener() {
            @Override
            public void onSuccess(Bitmap image) {
                profileProgressBar.setVisibility(View.GONE);
                ((ImageView)contentView.findViewById(R.id.profile_image)).setImageBitmap(image);
            }

            @Override
            public void onFail() {
                profileProgressBar.setVisibility(View.GONE);
                Log.d("TAG","fail to get image.");
            }
        });
    }

    // Validate the update form for each input.
    private boolean validateForm() {
        boolean valid = true;

        String city = profileCity.getText().toString();
        if (TextUtils.isEmpty(city)) {
            profileCity.requestFocus();
            profileCity.setError("Required.");
            valid = false;
        }else {
            profileCity.setError(null);
        }

        String lastName = profileLastName.getText().toString();
        if (TextUtils.isEmpty(lastName)) {
            profileLastName.requestFocus();
            profileLastName.setError("Required.");
            valid = false;
        }else {
            profileLastName.setError(null);
        }

        String firstName = profileFirstName.getText().toString();
        if (TextUtils.isEmpty(firstName)) {
            profileFirstName.requestFocus();
            profileFirstName.setError("Required.");
            valid = false;
        }else {
            profileFirstName.setError(null);
        }

        return valid;
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    // An intent to take a picture.
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    // Get the result from camera and set the image.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            int dimension = getSquareCropDimensionForBitmap();
            imageBitmap = ThumbnailUtils.extractThumbnail(imageBitmap, dimension, dimension);
            ImageView userImage = (ImageView) contentView.findViewById(R.id.profile_image);
            userImage.setImageBitmap(imageBitmap);
        }
    }

    // Get square cropped image.
    public int getSquareCropDimensionForBitmap()
    {
        //use the smallest dimension of the image to crop to
        return Math.min(imageBitmap.getWidth(), imageBitmap.getHeight());
    }
}
