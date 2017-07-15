package eladjarby.bakeit.Models.Recipe;

/**
 * Created by EladJ on 14/7/2017.
 */

public class Recipe {
    private String ID;
    private String recipeAuthorID;
    private String recipeTitle;
    private String recipeCategory;
    private String recipeInstructions;
    private String recipeIngredients;
    private Integer recipeTime;
    private String recipeImage;
    private Integer recipeLikes;

    public Recipe() {
    }

    public Recipe(String ID, String recipeAuthorID, String recipeTitle, String recipeCategory, String recipeInstructions, String recipeIngredients, Integer recipeTime, String recipeImage, Integer recipeLikes) {
        this.ID = ID;
        this.recipeAuthorID = recipeAuthorID;
        this.recipeTitle = recipeTitle;
        this.recipeCategory = recipeCategory;
        this.recipeInstructions = recipeInstructions;
        this.recipeIngredients = recipeIngredients;
        this.recipeTime = recipeTime;
        this.recipeImage = recipeImage;
        this.recipeLikes = recipeLikes;
    }



    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getRecipeAuthorID() {
        return recipeAuthorID;
    }

    public void setRecipeAuthorID(String recipeAuthorID) {
        this.recipeAuthorID = recipeAuthorID;
    }

    public String getRecipeTitle() {
        return recipeTitle;
    }

    public void setRecipeTitle(String recipeTitle) {
        this.recipeTitle = recipeTitle;
    }

    public String getRecipeCategory() {
        return recipeCategory;
    }

    public void setRecipeCategory(String recipeCategory) {
        this.recipeCategory = recipeCategory;
    }

    public String getRecipeInstructions() {
        return recipeInstructions;
    }

    public void setRecipeInstructions(String recipeInstructions) {
        this.recipeInstructions = recipeInstructions;
    }

    public String getRecipeIngredients() {
        return recipeIngredients;
    }

    public void setRecipeIngredients(String recipeIngredients) {
        this.recipeIngredients = recipeIngredients;
    }

    public Integer getRecipeTime() {
        return recipeTime;
    }

    public void setRecipeTime(Integer recipeTime) {
        this.recipeTime = recipeTime;
    }

    public String getRecipeImage() {
        return recipeImage;
    }

    public void setRecipeImage(String recipeImage) {
        this.recipeImage = recipeImage;
    }

    public Integer getRecipeLikes() {
        return recipeLikes;
    }

    public void setRecipeLikes(Integer recipeLikes) {
        this.recipeLikes = recipeLikes;
    }
    //private List<Comment> recipeComments;
}

