package eladjarby.bakeit.Models.Recipe;

import java.util.HashMap;

/**
 * Created by EladJ on 14/7/2017.
 */

public class Recipe {
    private String ID;
    private String recipeAuthorId;
    private String recipeAuthorName;
    private String recipeTitle;
    private String recipeCategory;
    private String recipeInstructions;
    private String recipeIngredients;
    private Integer recipeTime;
    private String recipeImage;
    private Integer recipeLikes;
    private HashMap<String,Boolean> recipeLikesList;
    private String recipeDate;
    private long recipeLastUpdateDate = 0;
    private Integer recipeIsRemoved = 0;

    // Default constructor
    public Recipe() {
    }

    // Constructor
    public Recipe(String ID, String recipeAuthorId,String recipeAuthorName, String recipeTitle, String recipeCategory, String recipeInstructions, String recipeIngredients, Integer recipeTime, String recipeImage, Integer recipeLikes,HashMap<String,Boolean> recipeLikesList, String recipeDate, Integer recipeIsRemoved) {
        this.ID = ID;
        this.recipeAuthorId = recipeAuthorId;
        this.recipeAuthorName = recipeAuthorName;
        this.recipeTitle = recipeTitle;
        this.recipeCategory = recipeCategory;
        this.recipeInstructions = recipeInstructions;
        this.recipeIngredients = recipeIngredients;
        this.recipeTime = recipeTime;
        this.recipeImage = recipeImage;
        this.recipeLikes = recipeLikes;
        this.recipeLikesList = recipeLikesList;
        this.recipeDate = recipeDate;
        this.recipeIsRemoved = recipeIsRemoved;
    }

    // Constructor for sql lite.
    public Recipe(String ID, String recipeAuthorId,String recipeAuthorName, String recipeTitle, String recipeCategory, String recipeInstructions, String recipeIngredients, Integer recipeTime, String recipeImage, Integer recipeLikes,HashMap<String,Boolean> recipeLikesList, String recipeDate, long recipeLastUpdateDate, Integer recipeIsRemoved) {
        this.ID = ID;
        this.recipeAuthorId = recipeAuthorId;
        this.recipeAuthorName = recipeAuthorName;
        this.recipeTitle = recipeTitle;
        this.recipeCategory = recipeCategory;
        this.recipeInstructions = recipeInstructions;
        this.recipeIngredients = recipeIngredients;
        this.recipeTime = recipeTime;
        this.recipeImage = recipeImage;
        this.recipeLikes = recipeLikes;
        this.recipeLikesList = recipeLikesList;
        this.recipeDate = recipeDate;
        this.recipeLastUpdateDate = recipeLastUpdateDate;
        this.recipeIsRemoved = recipeIsRemoved;
    }

    /*
    Getters and setters.
     */
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

    public String getRecipeAuthorName() {
        return recipeAuthorName;
    }

    public void setRecipeAuthorName(String recipeAuthorName) {
        this.recipeAuthorName = recipeAuthorName;
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

    public HashMap<String, Boolean> getRecipeLikesList() {
        return recipeLikesList;
    }

    public void setRecipeLikesList(HashMap<String, Boolean> recipeLikesList) {
        this.recipeLikesList = recipeLikesList;
    }

    public void setRecipeLikes(Integer recipeLikes) {
        this.recipeLikes = recipeLikes;
    }

    public Integer getRecipeIsRemoved() {
        return recipeIsRemoved;
    }

    public void setRecipeIsRemoved(Integer recipeIsRemoved) {
        this.recipeIsRemoved = recipeIsRemoved;
    }
//private List<Comment> recipeComments;
}

