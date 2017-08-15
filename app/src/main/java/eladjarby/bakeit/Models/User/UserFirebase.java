package eladjarby.bakeit.Models.User;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import eladjarby.bakeit.LoginActivity;
import eladjarby.bakeit.Models.BaseInterface;
import eladjarby.bakeit.RegisterActivity;

/**
 * Created by EladJ on 27/6/2017.
 */

public class UserFirebase {
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final String USERS_TABLE = "Users";
    private static DatabaseReference myRef = database.getReference(USERS_TABLE);

    public static void registerAccount(RegisterActivity registerActivity, final String email, String password, final BaseInterface.RegisterAccountCallBack callBack) {
        // [START sign_in_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(registerActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "createUserWithEmail:success");
                            final FirebaseUser user = mAuth.getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(email)
                                    .build();
                            if (user != null) {
                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()) {
                                                    callBack.onComplete(user, task);
                                                } else {
                                                    callBack.onFailure("Failed to register");
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
        // [END sign_in_with_email]
    }

    public static void logoutAccount() {
        mAuth.signOut();
    }

    public static void loginAccount(LoginActivity loginActivity , String email , String password , final BaseInterface.LoginAccountCallBack callback) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(loginActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            callback.onComplete(user,task);
                        } else {
                            callback.onFailure("Failed to login");
                        }
                        // [END_EXCLUDE]
                    }
                });
    }
    public static void
    addDBUser(User user) {
        myRef.child("" + user.getID()).setValue(user);
    }

    public static String getCurrentUserId() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null) {
            return currentUser.getUid();
        } else {
            return null;
        }
    }

    public static void getUser(String userId , final BaseInterface.GetUserCallback callback) {
        myRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                callback.onComplete(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
