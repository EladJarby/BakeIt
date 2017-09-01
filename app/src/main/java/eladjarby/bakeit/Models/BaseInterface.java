package eladjarby.bakeit.Models;

import android.graphics.Bitmap;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import eladjarby.bakeit.Models.Recipe.Recipe;
import eladjarby.bakeit.Models.User.User;

/**
 * Created by EladJ on 13/07/2017.
 */

// Base interface that allocated all callback that needed.
public interface BaseInterface {
    interface RegisterAccountCallBack {
        void onComplete(FirebaseUser user, Task<Void> task);
        void onFailure(String errorMessage);
    }

    interface LoginAccountCallBack {
        void onComplete(FirebaseUser user , Task<AuthResult> task);
        void onFailure(String errorMessage);
    }

    interface ForgetPasswordCallBack {
        void onComplete(String successMessage);
        void onFailure(String errorMessage);
    }

    interface GetRecipeCallback {
        void onComplete();
        void onCancel();
    }


    interface GetLikesCallback {
        void onComplete(Recipe newRecipe);
        void onCancel();
    }

    interface GetUserCallback {
        void onComplete(User user);
        void onCancel();
    }

    interface  GetAllRecipesCallback {
        void onComplete(List<Recipe> list);
    }

    interface RecipeUpdates {
        void onRecipeUpdate(Recipe recipe);
    }

    interface GetImageListener {
        void onSuccess(Bitmap image);
        void onFail();
    }

    interface SaveImageListener {
        void complete(String url);
        void fail();
    }

    interface LoadImageFromFileAsynch{
        void onComplete(Bitmap bitmap);
    }

}
