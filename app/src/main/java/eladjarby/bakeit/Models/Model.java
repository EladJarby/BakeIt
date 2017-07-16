package eladjarby.bakeit.Models;

import org.greenrobot.eventbus.EventBus;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import eladjarby.bakeit.Models.Recipe.Recipe;
import eladjarby.bakeit.Models.Recipe.RecipeFirebase;
import eladjarby.bakeit.Models.Recipe.RecipeSql;
import eladjarby.bakeit.MyApplication;

/**
 * Created by EladJ on 27/6/2017.
 */

public class Model {
    public final static Model instance = new Model();
    private ModelFirebase modelFirebase;
    private ModelSql modelSql;
    private Model() {
        modelSql = new ModelSql(MyApplication.getMyContext());
        modelFirebase = new ModelFirebase();
    }

    public class RecipeUpdateEvent {

        public final Recipe recipe;

        public RecipeUpdateEvent(Recipe recipe) {
            this.recipe = recipe;
        }
    }

    public void getRecipeList(BaseInterface.GetAllRecipesCallback callback) {
        callback.onComplete(RecipeSql.getRecipeList(modelSql.getReadableDatabase()));
    }

    public String getCurrentUserId() {
        return modelFirebase.getCurrentUserId();
    }

    public void addRecipe(Recipe recipe) {
        RecipeSql.addRecipe(modelSql.getWritableDatabase(),recipe);
        RecipeFirebase.addRecipe(recipe);
        EventBus.getDefault().post(new RecipeUpdateEvent(recipe));
    }

    public String randomNumber() {
        Random r = new Random();
        return "" + System.currentTimeMillis() + r.nextInt(10000);
    }
}
