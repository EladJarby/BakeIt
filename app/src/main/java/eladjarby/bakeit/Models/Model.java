package eladjarby.bakeit.Models;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.URLUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.Random;

import eladjarby.bakeit.Models.Recipe.Recipe;
import eladjarby.bakeit.Models.Recipe.RecipeFirebase;
import eladjarby.bakeit.Models.Recipe.RecipeSql;
import eladjarby.bakeit.Models.User.User;
import eladjarby.bakeit.Models.User.UserFirebase;
import eladjarby.bakeit.MyApplication;

import static eladjarby.bakeit.Models.ModelFiles.saveImageToFile;

/**
 * Created by EladJ on 27/6/2017.
 */

public class Model {
    // Create a new model , Singleton.
    public final static Model instance = new Model();
    private ModelFirebase modelFirebase;
    private ModelSql modelSql;
    private User currentUser;
    private static final String RECIPE_LAST_UPDATE_DATE = "recipeLastUpdateDate";

    // Constructor
    private Model() {
        modelSql = new ModelSql(MyApplication.getMyContext());
        modelFirebase = new ModelFirebase();
    }

    // Sync user when user get to main activity.
    public void syncUser() {
        UserFirebase.getUser(UserFirebase.getCurrentUserId(), new BaseInterface.GetUserCallback() {
            @Override
            public void onComplete(User user) {
                currentUser = user;
                syncAndRegisterRecipeUpedates();
            }

            @Override
            public void onCancel() {

            }
        });
    }

    // When recipe updated.
    public class RecipeUpdateEvent {
        public final Recipe recipe;
        public RecipeUpdateEvent(Recipe recipe) {
            this.recipe = recipe;
        }
    }

    // When recipe changed.
    public class RecipeChangedEvent {
        public final Recipe recipe;
        public RecipeChangedEvent(Recipe recipe) {
            this.recipe = recipe;
        }
    }

    // Get recipe list
    public void getRecipeList(BaseInterface.GetAllRecipesCallback callback) {
        callback.onComplete(RecipeSql.getRecipeList(modelSql.getReadableDatabase()));
    }

    // Get current user id
    public String getCurrentUserId() {
        return modelFirebase.getCurrentUserId();
    }

    // Get current user.
    public User getCurrentUser() {
        return currentUser;
    }
    public Recipe getRecipe(String recipeId) {
        return RecipeSql.getRecipe(modelSql.getReadableDatabase(),recipeId);
    }

    // Add recipe function.
    public void addRecipe(Recipe recipe) {
        RecipeFirebase.addRecipe(recipe);
    }

    // Edit recipe function.
    public void editRecipe(Recipe recipe) {
        RecipeSql.updateRecipe(modelSql.getWritableDatabase(),recipe);
        RecipeFirebase.updateRecipe(recipe);
    }

    // Remove recipe function.
    public void removeRecipe(final String recipeId , final BaseInterface.GetRecipeCallback callback) {
        final Recipe recipe = Model.instance.getRecipe(recipeId);
        RecipeFirebase.removeRecipe(recipe, new BaseInterface.GetRecipeCallback() {
            @Override
            public void onComplete() {
                Log.d("TAG","Recipe removed");
            }

            @Override
            public void onCancel() {
                Log.d("TAG","Recipe remove canceled.");
            }
        });
    }

    // Randomize a random number.
    public String randomNumber() {
        Random r = new Random();
        return "" + System.currentTimeMillis() + r.nextInt(10000);
    }

    /*
    Sync and register new updates , each time user get to main activity,
    User get the delta between last update date(from his user) to last update date on firebase,
    Because of that , he get all the new recipes.
     */
    private void syncAndRegisterRecipeUpedates() {
        // Get the shared preference.
        SharedPreferences pref = MyApplication.getMyContext().getSharedPreferences("TAG", Context.MODE_PRIVATE);
        // Get the last recipe date , default on beginning: 0.
        final long lastUpdateDate = pref.getLong(RECIPE_LAST_UPDATE_DATE, 0);
        Log.d("TAG","lastUpdateDate: " + lastUpdateDate);

        RecipeFirebase.uploadRecipeUpdates(lastUpdateDate, new BaseInterface.RecipeUpdates() {
            // Each time child updated/changed , this function called.
            @Override
            public void onRecipeUpdate(Recipe recipe) {
                boolean isChanged = false;
                // Check if recipe is exist in sql lite, if it doesnt , so add recipe to sql lite , if is already existed , so update recipe.
                if(RecipeSql.getRecipe(modelSql.getReadableDatabase(),recipe.getID()) == null) {
                    RecipeSql.addRecipe(modelSql.getWritableDatabase(), recipe);
                } else {
                    RecipeSql.updateRecipe(modelSql.getWritableDatabase(),recipe);
                    isChanged = true;
                }
                // Get the last recipe date , default on beginning: 0.
                SharedPreferences pref = MyApplication.getMyContext().getSharedPreferences("TAG", Context.MODE_PRIVATE);
                final long lastUpdateDate = pref.getLong(RECIPE_LAST_UPDATE_DATE, 0);
                // Check if the recipe date is newer then in shared preferences, if it does , so update the newer to be in shared prefernces for next update.
                if(lastUpdateDate < recipe.getRecipeLastUpdateDate()) {
                    SharedPreferences.Editor prefEditor = MyApplication.getMyContext().getSharedPreferences("TAG" ,Context.MODE_PRIVATE).edit();
                    prefEditor.putLong(RECIPE_LAST_UPDATE_DATE,recipe.getRecipeLastUpdateDate()).apply();
                }
                // check if recipe new or updated and send an event.
                if(!isChanged) {
                    EventBus.getDefault().post(new RecipeUpdateEvent(recipe));
                } else {
                    EventBus.getDefault().post(new RecipeChangedEvent(recipe));
                }
            }

        });
    }

    // Save image in firebase.
    public void saveImage(final Bitmap imageBmp, final String name, final BaseInterface.SaveImageListener listener) {
        modelFirebase.saveImage(imageBmp, name, new BaseInterface.SaveImageListener() {
            @Override
            public void complete(String url) {
                String fileName = URLUtil.guessFileName(url, null, null);
                saveImageToFile(imageBmp,fileName);
                listener.complete(url);
            }

            @Override
            public void fail() {
                listener.fail();
            }
        });
    }

    // Get image from local storage , if its not exist , so get the image from firebase.
    public void getImage(final String url, final BaseInterface.GetImageListener listener) {
        //check if image exsist localy
        final String fileName = URLUtil.guessFileName(url, null, null);
        ModelFiles.loadImageFromFileAsynch(fileName, new BaseInterface.LoadImageFromFileAsynch() {
            @Override
            public void onComplete(Bitmap bitmap) {
                if (bitmap != null){
                    Log.d("TAG","getImage from local success " + fileName);
                    listener.onSuccess(bitmap);
                }else {
                    modelFirebase.getImage(url, new BaseInterface.GetImageListener() {
                        @Override
                        public void onSuccess(Bitmap image) {
                            String fileName = URLUtil.guessFileName(url, null, null);
                            Log.d("TAG","getImage from FB success " + fileName);
                            saveImageToFile(image,fileName);
                            listener.onSuccess(image);
                        }

                        @Override
                        public void onFail() {
                            Log.d("TAG","getImage from FB fail ");
                            listener.onFail();
                        }
                    });

                }
            }
        });
    }

    // Change likes in firebase for each recipe.
    public void changeLike(final Recipe recipe) {
        RecipeFirebase.changeLike(recipe,new BaseInterface.GetLikesCallback() {

            @Override
            public void onComplete(Recipe newRecipe) {
                editRecipe(newRecipe);
            }

            @Override
            public void onCancel() {
                Log.d("TAG","changeLike canceled.");
            }
        });
    }
}
