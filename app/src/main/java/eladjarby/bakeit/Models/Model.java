package eladjarby.bakeit.Models;

import java.util.LinkedList;
import java.util.List;

import eladjarby.bakeit.Models.Recipe.Recipe;
import eladjarby.bakeit.MyApplication;

/**
 * Created by EladJ on 27/6/2017.
 */

public class Model {
    public final static Model instance = new Model();
    private ModelFirebase modelFirebase;
    private ModelSql modelSql;
    private Model() {
        modelFirebase = new ModelFirebase();
        modelSql = new ModelSql(MyApplication.getMyContext());
    }
//
//    public List<Recipe> getRecipeList() {
//        return recipeList;
//    }
}
