package eladjarby.bakeit.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import eladjarby.bakeit.MainActivity;
import eladjarby.bakeit.Models.BaseInterface;
import eladjarby.bakeit.Models.Model;
import eladjarby.bakeit.Models.ModelFiles;
import eladjarby.bakeit.Models.User.User;
import eladjarby.bakeit.Models.User.UserFirebase;
import eladjarby.bakeit.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserProfileFragment extends Fragment {
    private static final String JPEG = ".jpeg";
    View contentView;
    User user;
    private OnFragmentInteractionListener mListener;
    private Bitmap imageBitmap;

    public UserProfileFragment() {
        // Required empty public constructor
    }

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
        contentView = inflater.inflate(R.layout.fragment_user_profile, container, false);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        ImageView menuAdd = (ImageView) getActivity().findViewById(R.id.menu_add);
        ImageView menuProfile = (ImageView) getActivity().findViewById(R.id.menu_profile);
        TextView menuTitle = (TextView) getActivity().findViewById(R.id.menu_title);
        TextView menuTitleBakeIt = (TextView) getActivity().findViewById(R.id.menu_title_bakeit);
        menuTitleBakeIt.setVisibility(View.GONE);
        menuTitle.setVisibility(View.VISIBLE);
        menuAdd.setVisibility(View.GONE);
        menuProfile.setVisibility(View.GONE);
        menuTitle.setText("User Details");

        user = Model.instance.getCurrentUser();
        getUserDetails();
        Button updateBtn = (Button) contentView.findViewById(R.id.profile_update_btn);
        ImageView profileImage = (ImageView) contentView.findViewById(R.id.profile_image);
        Button logoutBtn = (Button) contentView.findViewById(R.id.profile_logout);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.logout();
            }
        });
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final User user = updateUser();
                if(imageBitmap != null) {
                    Model.instance.saveImage(imageBitmap, user.getID() + Model.instance.randomNumber() + JPEG, new BaseInterface.SaveImageListener() {
                        @Override
                        public void complete(String url) {
                            user.setUserImage(url);
                            UserFirebase.addDBUser(user);
                            ((MainActivity)getActivity()).showMenu();
                            getFragmentManager().popBackStack();
                        }

                        @Override
                        public void fail() {

                        }
                    });
                } else {
                    UserFirebase.addDBUser(user);
                    ((MainActivity)getActivity()).showMenu();
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

    private User updateUser() {
        String userId = null;
        String userEmail = null;
        String userImage = null;
        String userFirstName = ((EditText) contentView.findViewById(R.id.profile_firstName)).getText().toString();
        String userLastName = ((EditText) contentView.findViewById(R.id.profile_lastName)).getText().toString();
        String userTown = ((EditText) contentView.findViewById(R.id.profile_town)).getText().toString();
        String userStreet = ((EditText) contentView.findViewById(R.id.profile_street)).getText().toString();
        userId = user.getID();
        userEmail = user.getUserEmail();
        userImage = user.getUserImage();
        return new User(userId,userEmail,userTown,userStreet,userImage,userFirstName,userLastName);

    }
    private void getUserDetails() {
        EditText profileFirstName = (EditText) contentView.findViewById(R.id.profile_firstName);
        EditText profileLastName = (EditText) contentView.findViewById(R.id.profile_lastName);
        EditText profileTown = (EditText) contentView.findViewById(R.id.profile_town);
        EditText profileStreet = (EditText) contentView.findViewById(R.id.profile_street);
        profileFirstName.setText(user.getUserFirstName());
        profileLastName.setText(user.getUserLastName());
        profileTown.setText(user.getUserTown());
        profileStreet.setText(user.getUserStreet());
        if(user.getUserImage() != null && !user.getUserImage().isEmpty() && !user.getUserImage().equals("")) {
            ((ImageView) contentView.findViewById(R.id.profile_image)).setImageBitmap(ModelFiles.loadImageFromFile(URLUtil.guessFileName(user.getUserImage(), null, null)));
        } else {
            ((ImageView) contentView.findViewById(R.id.recipePhoto)).setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.bakeitlogo));
        }
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

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


    public int getSquareCropDimensionForBitmap()
    {
        //use the smallest dimension of the image to crop to
        return Math.min(imageBitmap.getWidth(), imageBitmap.getHeight());
    }
}
