package eladjarby.bakeit.Models.Recipe;

/**
 * Created by EladJ on 14/7/2017.
 */

public class Recipe {
    private String ID;
    private String recipeAuthorId;
    private String recipeTitle;
    private String recipeCategory;
    private String recipeInstructions;
    private String recipeIngredients;
    private Integer recipeTime;
    private String recipeImage;
    private Integer recipeLikes;
    private String recipeDate;
    private long recipeLastUpdateDate = 0;

    public Recipe() {
    }

    public Recipe(String ID, String recipeAuthorId, String recipeTitle, String recipeCategory, String recipeInstructions, String recipeIngredients, Integer recipeTime, String recipeImage, Integer recipeLikes, String recipeDate) {
        this.ID = ID;
        this.recipeAuthorId = recipeAuthorId;
        this.recipeTitle = recipeTitle;
        this.recipeCategory = recipeCategory;
        this.recipeInstructions = recipeInstructions;
        this.recipeIngredients = recipeIngredients;
        this.recipeTime = recipeTime;
        this.recipeImage = recipeImage;
        this.recipeLikes = recipeLikes;
        this.recipeDate = recipeDate;
    }

    public Recipe(String ID, String recipeAuthorId, String recipeTitle, String recipeCategory, String recipeInstructions, String recipeIngredients, Integer recipeTime, String recipeImage, Integer recipeLikes, String recipeDate, long recipeLastUpdateDate) {
        this.ID = ID;
        this.recipeAuthorId = recipeAuthorId;
        this.recipeTitle = recipeTitle;
        this.recipeCategory = recipeCategory;
        this.recipeInstructions = recipeInstructions;
        this.recipeIngredients = recipeIngredients;
        this.recipeTime = recipeTime;
        this.recipeImage = recipeImage;
        this.recipeLikes = recipeLikes;
        this.recipeDate = recipeDate;
        this.recipeLastUpdateDate = recipeLastUpdateDate;
    }

    public String getRecipeDate() {
        return recipeDate;
    }

    public void setRecipeDate(String recipeDate) {
        this.recipeDate = recipeDate;
    }

    public long getRecipeLastUpdateDate() {
        return recipeLastUpdateDate;
    }

    public void setRecipeLastUpdateDate(long recipeLastUpdateDate) {
        this.recipeLastUpdateDate = recipeLastUpdateDate;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getRecipeAuthorId() {
        return recipeAuthorId;
    }

    public void setRecipeAuthorId(String recipeAuthorId) {
        this.recipeAuthorId = recipeAuthorId;
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

