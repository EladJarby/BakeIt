package eladjarby.bakeit.Models.Recipe;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import eladjarby.bakeit.Models.BaseInterface;

/**
 * Created by EladJ on 16/07/2017.
 */

public class RecipeFirebase {
    public static final String SORT_RECIPE_LAST_UPDATE = "recipe_last_update_date";
    public static final String RECIPE_TABLE = "Recipes";
    private static final String RECIPE_ID = "recipe_id";
    private static final String RECIPE_AUTHOR_ID = "recipe_author_id";
    private static final String RECIPE_TITLE = "recipe_title";
    private static final String RECIPE_CATEGORY = "recipe_category";
    private static final String RECIPE_INSTRUCTIONS = "recipe_instructions";
    private static final String RECIPE_INGREDIENTS = "recipe_ingredients";
    private static final String RECIPE_TIME = "recipe_time";
    private static final String RECIPE_IMAGE = "recipe_image";
    private static final String RECIPE_LIKES = "recipe_likes";
    private static final String RECIPE_DATE = "recipe_date";
    private static final String RECIPE_LAST_UPDATE_DATE = "recipe_last_update_date";

    private static FirebaseDatabase db = FirebaseDatabase.getInstance();
    private static DatabaseReference myRef = db.getReference(RECIPE_TABLE);

    public static void addRecipe(Recipe recipe) {
        Map<String,Object> values = new HashMap<String , Object>();
        values.put(RECIPE_ID,recipe.getID());
        values.put(RECIPE_AUTHOR_ID,recipe.getRecipeAuthorID());
        values.put(RECIPE_TITLE,recipe.getRecipeTitle());
        values.put(RECIPE_CATEGORY,recipe.getRecipeCategory());
        values.put(RECIPE_INSTRUCTIONS,recipe.getRecipeInstructions());
        values.put(RECIPE_INGREDIENTS,recipe.getRecipeIngredients());
        values.put(RECIPE_TIME,recipe.getRecipeTime());
        values.put(RECIPE_IMAGE,recipe.getRecipeImage());
        values.put(RECIPE_LIKES,recipe.getRecipeLikes());
        values.put(RECIPE_DATE,recipe.getRecipeDate());
        values.put(RECIPE_LAST_UPDATE_DATE, ServerValue.TIMESTAMP);
        myRef.child("" + recipe.getID()).setValue(values);
    }

    public static void removeRecipe(String recipeId , final BaseInterface.GetRecipeCallback callback) {
        myRef.child(recipeId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().removeValue();
                callback.onComplete();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onCancel();
            }
        });
    }
}
