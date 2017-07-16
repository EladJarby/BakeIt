package eladjarby.bakeit.Models;

import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

import eladjarby.bakeit.Models.User.UserFirebase;

/**
 * Created by EladJ on 27/6/2017.
 */

public class ModelFirebase {
    private FirebaseAuth mAuth;
    private EditText mUsername;
    private EditText mPassword;

    public String getCurrentUserId() {
        return UserFirebase.getCurrentUserId();
    }
}
