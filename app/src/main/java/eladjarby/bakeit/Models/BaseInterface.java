package eladjarby.bakeit.Models;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by EladJ on 13/07/2017.
 */

public interface BaseInterface {
    interface RegisterAccountCallBack {
        void onComplete(FirebaseUser user, Task<Void> task);
        void onFailure(String errorMessage);
    }

    interface LoginAccountCallBack {
        void onComplete(FirebaseUser user , Task<AuthResult> task);
        void onFailure(String errorMessage);
    }
}
