package eladjarby.bakeit.Models.Recipe;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by EladJ on 14/7/2017.
 */

public class RecipeSql {
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

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + RECIPE_TABLE + "(" +
                RECIPE_ID + " TEXT PRIMARY KEY, " +
                RECIPE_AUTHOR_ID + " TEXT, " +
                RECIPE_TITLE + " TEXT, " +
                RECIPE_CATEGORY + " TEXT, " +
                RECIPE_INSTRUCTIONS + " TEXT, " +
                RECIPE_INGREDIENTS + " TEXT, " +
                RECIPE_TIME + " NUMBER, " +
                RECIPE_IMAGE + " TEXT, " +
                RECIPE_LIKES + " NUMBER, " +
                RECIPE_DATE + " DATE, " +
                RECIPE_LAST_UPDATE_DATE + " NUMBER);");
    }

    public static void onUpgrade(SQLiteDatabase db , int oldVersion , int newVersion) {
        db.execSQL("drop " + RECIPE_TABLE);
        onCreate(db);
    }

    public static List<Recipe> getRecipeList(SQLiteDatabase db) {
        Cursor cursor = db.query(RECIPE_TABLE , null , null , null , null , null , null);
        List<Recipe> recipeList = new LinkedList<Recipe>();
        if(cursor.moveToFirst()) {
            do {
                recipeList.add(0 , getSQLRecipe(cursor));
            } while(cursor.moveToNext());
        }
        cursor.close();
        return recipeList;
    }

    public static void addRecipe(SQLiteDatabase db , Recipe recipe) {
        db.insert(RECIPE_TABLE,RECIPE_ID,getRecipeValues(recipe));
    }

    public static void removeRecipe(SQLiteDatabase db , String recipeId) {
            db.delete(RECIPE_TABLE,"ID=?", new String[]{recipeId});
    }

    public static void updateRecipe(SQLiteDatabase db , Recipe recipe) {
        db.update(RECIPE_TABLE, getRecipeValues(recipe), "ID=?",new String[]{recipe.getID()});
    }

    public static Recipe getRecipe (SQLiteDatabase db , String recipeId) {
        Cursor cursor = db.query(RECIPE_TABLE,null,"ID=?", new String[]{recipeId}, null, null, null);
        if(cursor.moveToFirst()) {
            Recipe recipe = getSQLRecipe(cursor);
            cursor.close();
            return recipe;
        } else {
            cursor.close();
            return null;
        }
    }

    private static ContentValues getRecipeValues(Recipe recipe) {
        ContentValues values = new ContentValues();
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
        values.put(RECIPE_LAST_UPDATE_DATE,recipe.getRecipeLastUpdateDate());
        return values;
    }

    private static Recipe getSQLRecipe(Cursor cursor) {
        int recipeIdIndex = cursor.getColumnIndex(RECIPE_ID);
        int recipeAuthorIdIndex = cursor.getColumnIndex(RECIPE_AUTHOR_ID);
        int recipeTitleIndex = cursor.getColumnIndex(RECIPE_TITLE);
        int recipeCategoryIndex = cursor.getColumnIndex(RECIPE_CATEGORY);
        int recipeInstructionsIndex = cursor.getColumnIndex(RECIPE_INSTRUCTIONS);
        int recipeIngredientsIndex = cursor.getColumnIndex(RECIPE_INGREDIENTS);
        int recipeTimeIndex = cursor.getColumnIndex(RECIPE_TIME);
        int recipeImageIndex = cursor.getColumnIndex(RECIPE_IMAGE);
        int recipeLikesIndex = cursor.getColumnIndex(RECIPE_LIKES);
        int recipeDateIndex = cursor.getColumnIndex(RECIPE_DATE);
        int recipeLastUpdateIndex = cursor.getColumnIndex(RECIPE_LAST_UPDATE_DATE);

        Recipe recipe = new Recipe(cursor.getString(recipeIdIndex),
                cursor.getString(recipeAuthorIdIndex),
                cursor.getString(recipeTitleIndex),
                cursor.getString(recipeCategoryIndex),
                cursor.getString(recipeInstructionsIndex),
                cursor.getString(recipeIngredientsIndex),
                Integer.parseInt(cursor.getString(recipeTimeIndex)),
                cursor.getString(recipeImageIndex),
                Integer.parseInt(cursor.getString(recipeLikesIndex)),
                cursor.getString(recipeDateIndex),
                cursor.getLong(recipeLastUpdateIndex));

        return recipe;
    }
}
