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
    public final static Model instance = new Model();
    private ModelFirebase modelFirebase;
    private ModelSql modelSql;
    private User currentUser;
    private static final String RECIPE_LAST_UPDATE_DATE = "recipeLastUpdateDate";

    private Model() {
        modelSql = new ModelSql(MyApplication.getMyContext());
        modelFirebase = new ModelFirebase();
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

    public class RecipeUpdateEvent {

        public final Recipe recipe;

        public RecipeUpdateEvent(Recipe recipe) {
            this.recipe = recipe;
        }
    }

    public class RecipeChangedEvent {

        public final Recipe recipe;

        public RecipeChangedEvent(Recipe recipe) {
            this.recipe = recipe;
        }
    }

    public class RecipeRemoveEvent {

        public final String recipeId;

        public RecipeRemoveEvent(String recipeId) {
            this.recipeId = recipeId;
        }
    }
    public void getRecipeList(BaseInterface.GetAllRecipesCallback callback) {
        callback.onComplete(RecipeSql.getRecipeList(modelSql.getReadableDatabase()));
    }

    public String getCurrentUserId() {
        return modelFirebase.getCurrentUserId();
    }

    public User getCurrentUser() {
        return currentUser;
    }
    public Recipe getRecipe(String recipeId) {
        return RecipeSql.getRecipe(modelSql.getReadableDatabase(),recipeId);
    }
    public void addRecipe(Recipe recipe) {
        //RecipeSql.addRecipe(modelSql.getWritableDatabase(),recipe);
        RecipeFirebase.addRecipe(recipe);
    }

    public void editRecipe(Recipe recipe) {
        RecipeSql.updateRecipe(modelSql.getWritableDatabase(),recipe);
        RecipeFirebase.updateRecipe(recipe);
    }

    public void removeRecipe(final String recipeId , final BaseInterface.GetRecipeCallback callback) {
        final Recipe recipe = Model.instance.getRecipe(recipeId);
        //RecipeSql.removeRecipe(modelSql.getWritableDatabase(),recipeId);
        RecipeFirebase.removeRecipe(recipe, new BaseInterface.GetRecipeCallback() {
            @Override
            public void onComplete() {
            }

            @Override
            public void onCancel() {

            }
        });
    }

    public String randomNumber() {
        Random r = new Random();
        return "" + System.currentTimeMillis() + r.nextInt(10000);
    }

    private void syncAndRegisterRecipeUpedates() {
        SharedPreferences pref = MyApplication.getMyContext().getSharedPreferences("TAG", Context.MODE_PRIVATE);
        final long lastUpdateDate = pref.getLong(RECIPE_LAST_UPDATE_DATE, 0);
        Log.d("TAG","lastUpdateDate: " + lastUpdateDate);

        RecipeFirebase.uploadRecipeUpdates(lastUpdateDate, new BaseInterface.RecipeUpdates() {
            @Override
            public void onRecipeUpdate(Recipe recipe) {
                boolean isChanged = false;
                if(RecipeSql.getRecipe(modelSql.getReadableDatabase(),recipe.getID()) == null) {
                    RecipeSql.addRecipe(modelSql.getWritableDatabase(), recipe);
                } else {
                    RecipeSql.updateRecipe(modelSql.getWritableDatabase(),recipe);
                    isChanged = true;
                }
                SharedPreferences pref = MyApplication.getMyContext().getSharedPreferences("TAG", Context.MODE_PRIVATE);
                final long lastUpdateDate = pref.getLong(RECIPE_LAST_UPDATE_DATE, 0);
                if(lastUpdateDate < recipe.getRecipeLastUpdateDate()) {
                    SharedPreferences.Editor prefEditor = MyApplication.getMyContext().getSharedPreferences("TAG" ,Context.MODE_PRIVATE).edit();
                    prefEditor.putLong(RECIPE_LAST_UPDATE_DATE,recipe.getRecipeLastUpdateDate()).apply();
                }
                if(!isChanged) {
                    EventBus.getDefault().post(new RecipeUpdateEvent(recipe));
                } else {
                    EventBus.getDefault().post(new RecipeChangedEvent(recipe));
                }
            }

        });
    }

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

    public void changeLike(final Recipe recipe) {
        RecipeFirebase.changeLike(recipe,new BaseInterface.GetLikesCallback() {

            @Override
            public void onComplete(Recipe newRecipe) {
                editRecipe(newRecipe);
            }

            @Override
            public void onCancel() {

            }
        });
    }
}
