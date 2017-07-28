package eladjarby.bakeit.Models.Recipe;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
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
    public static final String RECIPE_TABLE = "Recipes";
    private static final String RECIPE_ID = "ID";
    private static final String RECIPE_AUTHOR_ID = "recipeAuthorId";
    private static final String RECIPE_TITLE = "recipeTitle";
    private static final String RECIPE_CATEGORY = "recipeCategory";
    private static final String RECIPE_INSTRUCTIONS = "recipeInstructions";
    private static final String RECIPE_INGREDIENTS = "recipeIngredients";
    private static final String RECIPE_TIME = "recipeTime";
    private static final String RECIPE_IMAGE = "recipeImage";
    private static final String RECIPE_LIKES = "recipeLikes";
    private static final String RECIPE_DATE = "recipeDate";
    private static final String RECIPE_LAST_UPDATE_DATE = "recipeLastUpdateDate";

    private static FirebaseDatabase db = FirebaseDatabase.getInstance();
    private static DatabaseReference myRef = db.getReference(RECIPE_TABLE);

    public static void addRecipe(Recipe recipe) {
        Map<String,Object> values = new HashMap<String , Object>();
        values.put(RECIPE_ID,recipe.getID());
        values.put(RECIPE_AUTHOR_ID,recipe.getRecipeAuthorId());
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

    public static void updateRecipe(Recipe recipe) {
        addRecipe(recipe);
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

    public static void uploadRecipeUpdates(long lastUpdateDate, final BaseInterface.RecipeUpdates callback) {
        myRef.orderByChild(RECIPE_LAST_UPDATE_DATE).startAt(lastUpdateDate).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("TAG","onChildAdded called");
                Recipe recipe = dataSnapshot.getValue(Recipe.class);
                callback.onRecipeUpdate(recipe);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("TAG","onChildAdded called");
                Recipe recipe = dataSnapshot.getValue(Recipe.class);
                callback.onRecipeChanged(recipe);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Recipe recipe = dataSnapshot.getValue(Recipe.class);
                callback.onRecipeRemove(recipe);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d("TAG","onChildAdded called");
                Recipe recipe = dataSnapshot.getValue(Recipe.class);
                callback.onRecipeUpdate(recipe);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
