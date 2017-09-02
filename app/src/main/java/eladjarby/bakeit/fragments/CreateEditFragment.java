package eladjarby.bakeit.fragments;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import eladjarby.bakeit.Dialogs.myProgressDialog;
import eladjarby.bakeit.MainActivity;
import eladjarby.bakeit.Models.BaseInterface;
import eladjarby.bakeit.Models.Model;
import eladjarby.bakeit.Models.ModelFiles;
import eladjarby.bakeit.Models.Recipe.Recipe;
import eladjarby.bakeit.R;

import static android.content.Context.INPUT_METHOD_SERVICE;


public class CreateEditFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String JPEG = ".jpeg";
    private String recipeId;
    private String fragMode;
    View contentView;
    ImageView imageCapture;
    Bitmap imageBitmap;
    myProgressDialog mProgressDialog;
    ArrayList<String> ingredientsList = new ArrayList<>();
    IngredientsListAdapter adapter = new IngredientsListAdapter();
    private OnFragmentInteractionListener mListener;
    private EditText recipeTitle,recipeIngredients,recipeTime,recipeInstructions;
    private ListView ingredientsListLV;
    private ImageView recipeAddIngredient;
    private Animation slideRight;

    public CreateEditFragment() {}

    public static CreateEditFragment newInstance(String param1,String param2) {
        CreateEditFragment fragment = new CreateEditFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recipeId = getArguments().getString(ARG_PARAM1);
            fragMode = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Check permission before continue.
        checkCameraPermission();
        mProgressDialog = new myProgressDialog(getActivity());
        // Inflate the layout for this fragment
        contentView = inflater.inflate(R.layout.fragment_create_edit, container, false);

        Spinner categoryDropdown = (Spinner) contentView.findViewById(R.id.recipeCategory);
        // Set the categories in dropdown.
        String[] items = new String[]{"Browines","Cakes","Loaves","Cupcakes & Muffins","Gluten free"};
        // Create and set the array adapter for category dropdown.
        ArrayAdapter<String> dropdownAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,items);
        categoryDropdown.setAdapter(dropdownAdapter);

        Button saveButton = (Button) contentView.findViewById(R.id.saveButton);
        recipeTitle = (EditText) contentView.findViewById(R.id.recipeName);
        recipeIngredients = (EditText) contentView.findViewById(R.id.recipeIngredients);
        recipeAddIngredient = (ImageView) contentView.findViewById(R.id.recipeAddIngredient);
        final Animation slideLeft = AnimationUtils.loadAnimation(getActivity(), R.anim.slideleft);
        slideRight = AnimationUtils.loadAnimation(getActivity(), R.anim.slideright);
        recipeIngredients.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d("TAG","bla");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("TAG","bla");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(ingredientsList.size() == 0 && fragMode.equals("Create")) {
                    if(s.length() == 1 && recipeAddIngredient.getVisibility() == View.GONE) {
                        recipeAddIngredient.setVisibility(View.VISIBLE);
                        recipeAddIngredient.startAnimation(slideLeft);
                    } else if(s.length() == 0) {
                        recipeAddIngredient.setVisibility(View.GONE);
                        recipeAddIngredient.startAnimation(slideRight);
                    }
                }
            }
        });
        ingredientsListLV = (ListView) contentView.findViewById(R.id.recipeIngredientsList);
        // Set a custom adapter fron ingredients list view.
        ingredientsListLV.setAdapter(adapter);
        // Catch click on add ingredient button to add a new ingredient to the list.
        recipeAddIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validation to check if ingredient have at least 1 character.
                if(!recipeIngredients.getText().toString().isEmpty()) {
                    ingredientsList.add(recipeIngredients.getText().toString());
                    // Initialize for next ingredient
                    recipeIngredients.setText("");
                    // Notify about change.
                    adapter.notifyDataSetChanged();
                    // Set the height after updating the list.
                    setListViewHeightBasedOnChildren(ingredientsListLV);
                } else {
                    recipeIngredients.setError("At least 1 character.");
                }
            }
        });

        recipeTime = (EditText) contentView.findViewById(R.id.recipeTime);
        recipeInstructions = (EditText) contentView.findViewById(R.id.recipeInstructions);
        ImageView recipeImage = (ImageView) contentView.findViewById(R.id.recipePhoto);

        // Get the action bar from activity.
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Set menu
        ImageView menuAdd = (ImageView) getActivity().findViewById(R.id.menu_add);
        ImageView menuProfile = (ImageView) getActivity().findViewById(R.id.menu_profile);
        TextView menuTitle = (TextView) getActivity().findViewById(R.id.menu_title);
        TextView menuTitleBakeIt = (TextView) getActivity().findViewById(R.id.menu_title_bakeit);
        menuTitleBakeIt.setVisibility(View.GONE);
        menuTitle.setVisibility(View.VISIBLE);
        menuAdd.setVisibility(View.GONE);
        menuProfile.setVisibility(View.GONE);

        // Switch case to check if we are on 'create mode' or 'edit mode'.
        switch (fragMode) {
            case "Create":
                // Set the title to create recipe.
                menuTitle.setText("Create recipe");
                ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Home");
                recipeImage.setVisibility(View.GONE);
                saveButton.setText("Upload recipe");
                // Catch click on save button to create a new recipe.
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Validate the form before create a new recipe.
                        if (!validateForm()) {
                            return;
                        }
                        closeKeyboard();
                        mProgressDialog.showProgressDialog();
                        // Create a new recipe and get a refernce.
                        final Recipe recipe = createRecipe();
                        // Save image and save recipe.
                        if(imageBitmap != null) {
                            Model.instance.saveImage(imageBitmap, recipe.getID() + Model.instance.randomNumber() + JPEG, new BaseInterface.SaveImageListener() {
                                @Override
                                public void complete(String url) {
                                    recipe.setRecipeImage(url);
                                    // Add recipe to firebase.
                                    Model.instance.addRecipe(recipe);
                                    mProgressDialog.hideProgressDialog();
                                    ((MainActivity)getActivity()).showMenu();
                                    getFragmentManager().popBackStack();
                                }

                                @Override
                                public void fail() {
                                }
                            });
                        } else {
                            // Add recipe to firebase.
                            Model.instance.addRecipe(recipe);
                            mProgressDialog.hideProgressDialog();
                            ((MainActivity)getActivity()).showMenu();
                            getFragmentManager().popBackStack();
                        }
                    }
                });
                break;
            case "Edit":
                // Set the title to edit recipe.
                menuTitle.setText("Edit recipe");
                getRecipeData(Model.instance.getRecipe(recipeId));
                if(ingredientsList.size() != 0) {
                    recipeAddIngredient.setVisibility(View.VISIBLE);
                }
                recipeImage.setVisibility(View.VISIBLE);
                saveButton.setText("Edit recipe");
                // Catch click on save button to edit existed recipe.
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Validate the form before create a new recipe.
                        if (!validateForm()) {
                            return;
                        }
                        closeKeyboard();
                        mProgressDialog.showProgressDialog();
                        // Create recipe with new details.
                        final Recipe recipe = createRecipe();
                        if(imageBitmap != null) {
                            Model.instance.saveImage(imageBitmap, recipe.getID() + Model.instance.randomNumber() + JPEG, new BaseInterface.SaveImageListener() {
                                @Override
                                public void complete(String url) {
                                    recipe.setRecipeImage(url);
                                    //Update recipe on firebase.
                                    Model.instance.editRecipe(recipe);
                                    mProgressDialog.hideProgressDialog();
                                    ((MainActivity)getActivity()).showMenu();
                                    getFragmentManager().popBackStack();
                                }

                                @Override
                                public void fail() {
                                    Log.d("TAG","fail to save image.");
                                }
                            });
                        } else {
                            //Update recipe on firebase.
                            Model.instance.addRecipe(recipe);
                            mProgressDialog.hideProgressDialog();
                            ((MainActivity)getActivity()).showMenu();
                            getFragmentManager().popBackStack();
                        }
                    }
                });
                break;
        }
        imageCapture = (ImageView) contentView.findViewById(R.id.imageCapture);
        // Catch click on image capture for take a new recipe image.
        imageCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        ImageView imageGallery = (ImageView) contentView.findViewById(R.id.imageGallery);
        // Catch click on gallery image to grab image from gallery.
        imageGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePictureFromGallery();
            }
        });

        return contentView;
    }

    // Close keyboard.
    private void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }

    // Create a new recipe
    private Recipe createRecipe() {
        String recipeId = null;
        String recipeImage = null;
        HashMap<String, Boolean> recipeLikesList = null;
        int recipeLikes = 0;
        String recipeIngredients = "";
        String recipeTitle = ((EditText) contentView.findViewById(R.id.recipeName)).getText().toString();

        // Get the ingredients list and transfer each line to one string with bullets.
        for(int i=0; i < ingredientsList.size()-1; i++) {
            recipeIngredients = recipeIngredients + "\u2022 " + ingredientsList.get(i).toString() + "\n";
        }
        recipeIngredients = recipeIngredients + "\u2022 " + ingredientsList.get(ingredientsList.size()-1).toString();

        // Get instructions from input
        String recipeInstructions = ((EditText) contentView.findViewById(R.id.recipeInstructions)).getText().toString();
        // Get category from dropdown.
        String recipeCategory = ((Spinner) contentView.findViewById(R.id.recipeCategory)).getSelectedItem().toString();

        // Switch case to check if we are on 'create mode' or 'edit mode'.
        switch (fragMode) {
            case "Create":
                // Get random recipe id.
                recipeId = Model.instance.getCurrentUserId() + Model.instance.randomNumber();
                // Set default image.
                recipeImage = "https://firebasestorage.googleapis.com/v0/b/bakeit-f8116.appspot.com/o/noimage.png?alt=media&token=9ec9fdea-b5d4-480f-b8af-3c3f12c70aef";
                // Set likes to 0.
                recipeLikes = 0;
                break;
            case "Edit":
                // Get existed recipe id.
                recipeId = this.recipeId;
                Recipe recipe = Model.instance.getRecipe(recipeId);
                recipeImage = recipe.getRecipeImage();
                recipeLikes = recipe.getRecipeLikes();
                recipeLikesList = recipe.getRecipeLikesList();
                break;
        }
        int recipeIsRemoved = 0;
        int recipeTime = Integer.parseInt(((EditText) contentView.findViewById(R.id.recipeTime)).getText().toString());
        String userFullName = Model.instance.getCurrentUser().getUserFirstName() + " " + Model.instance.getCurrentUser().getUserLastName();
        // Get current time on format: d MMM yyyy at H:mm , for example: 28 Aug 2017 at 21:30.
        String currentTime = new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH).format(new Date()) + " at " + new SimpleDateFormat("H:mm").format(new Date());
        return new Recipe(recipeId,Model.instance.getCurrentUserId(), userFullName ,recipeTitle,recipeCategory,recipeInstructions,recipeIngredients,recipeTime,recipeImage,recipeLikes,recipeLikesList,currentTime,recipeIsRemoved);
    }

    // Validate form function , to check all inputs.
    private boolean validateForm() {
        boolean valid = true;

        String instructions = recipeInstructions.getText().toString();
        if (TextUtils.isEmpty(instructions)) {
            recipeInstructions.requestFocus();
            recipeInstructions.setError("Required.");
            valid = false;
        } else {
            recipeInstructions.setError(null);
        }

        String time = recipeTime.getText().toString();
        if (TextUtils.isEmpty(time)) {
            recipeTime.requestFocus();
            recipeTime.setError("Required.");
            valid = false;
        } else {
            recipeTime.setError(null);
        }

        if (ingredientsList.size() == 0) {
            recipeIngredients.requestFocus();
            recipeIngredients.setError("Required at least 1.");
            valid = false;
        } else {
            recipeIngredients.setError(null);
        }

        String recipeName = recipeTitle.getText().toString();
        if (TextUtils.isEmpty(recipeName)) {
            recipeTitle.requestFocus();
            recipeTitle.setError("Required.");
            valid = false;
        } else {
            recipeTitle.setError(null);
        }
        return valid;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        closeKeyboard();
    }

    public interface OnFragmentInteractionListener {}

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_GALLERY = 2;

    // An intent to get a picture from gallery.
    private void takePictureFromGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_IMAGE_GALLERY);
    }

    // An intent to take a picture from camera.
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    // Get the result from camera / gallery and set the image.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            ImageView recipePhoto = (ImageView) contentView.findViewById(R.id.recipePhoto);
            recipePhoto.setVisibility(View.VISIBLE);
            recipePhoto.setImageBitmap(imageBitmap);
        } else if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == getActivity().RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                imageBitmap = BitmapFactory.decodeStream(imageStream);
                ImageView recipePhoto = (ImageView) contentView.findViewById(R.id.recipePhoto);
                recipePhoto.setVisibility(View.VISIBLE);
                recipePhoto.setImageBitmap(imageBitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(getActivity(), "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }

    // Check camera permission.
    private void checkCameraPermission() {
        boolean hasPermission = (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.CAMERA}, 1);
        }
    }

    // Get recipe data (on edit mode) to set all inputs with existed data.
    private void getRecipeData(Recipe recipe) {
        ((EditText)contentView.findViewById(R.id.recipeName)).setText(recipe.getRecipeTitle());
        String recipeIngredients = recipe.getRecipeIngredients();
        // Remove all bullets
        recipeIngredients = recipeIngredients.replace("\u2022 ","");
        // Set a new array list that splitted with '\n'.
        ingredientsList = new ArrayList<String>(Arrays.asList(recipeIngredients.split("\n")));
        // Set the view list height based on children.
        setListViewHeightBasedOnChildren(ingredientsListLV);
        ((EditText)contentView.findViewById(R.id.recipeTime)).setText("" + recipe.getRecipeTime());
        ((EditText)contentView.findViewById(R.id.recipeInstructions)).setText(recipe.getRecipeInstructions());
        Spinner categorySpinner = ((Spinner)contentView.findViewById(R.id.recipeCategory));
        categorySpinner.setSelection(getSpinnerIndex(categorySpinner, recipe.getRecipeCategory()));
        if(recipe.getRecipeImage() != null && !recipe.getRecipeImage().isEmpty() && !recipe.getRecipeImage().equals("")) {
            ((ImageView) contentView.findViewById(R.id.recipePhoto)).setImageBitmap(ModelFiles.loadImageFromFile(URLUtil.guessFileName(recipe.getRecipeImage(), null, null)));
        } else {
            ((ImageView) contentView.findViewById(R.id.recipePhoto)).setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.bakeitlogo));
        }
    }

    // Get spinner index to set the selection on spinner dropdown.
    private int getSpinnerIndex(Spinner spinner, String value) {
        int index = 0;
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)){
                index = i;
                break;
            }
        }
        return index;
    }


    // View holder.
    private static class ViewHolder {
        TextView ingredientName;
        ImageView deleteIngredient;
    }

    // Custom adapter for ingredients list.
    private class IngredientsListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return ingredientsList.size();
        }

        @Override
        public Object getItem(int position) {
            return ingredientsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                // inflate each row for list.
                convertView = getActivity().getLayoutInflater().inflate(R.layout.ingredients_list_row,parent,false);
                final ViewHolder holder = new ViewHolder();

                // Set all ids to holder.
                holder.ingredientName = (TextView) convertView.findViewById(R.id.ingredient_name);
                holder.deleteIngredient = (ImageView) convertView.findViewById(R.id.ingredient_delete);

                holder.deleteIngredient.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Set the position by tag.
                        int pos = (int)holder.ingredientName.getTag();
                        // Remove ingredient from the list by position
                        ingredientsList.remove(pos);
                        // Notify about change.
                        adapter.notifyDataSetChanged();
                        if(ingredientsList.size() == 0 && recipeIngredients.getText().length() == 0 && fragMode.equals("Create")) {
                            recipeAddIngredient.setVisibility(View.GONE);
                            recipeAddIngredient.startAnimation(slideRight);
                        }
                        // Set new height after removing ingredient.
                        setListViewHeightBasedOnChildren(ingredientsListLV);
                    }
                });
                // Set view holder tag.
                convertView.setTag(holder);
            }
            // Get view holder tag.
            ViewHolder holder = (ViewHolder) convertView.getTag();
            // Set text by list.
            holder.ingredientName.setText(ingredientsList.get(position));
            // Set position tag to ingredient name to remember the position when deleting.
            holder.ingredientName.setTag(position);
            return convertView;
        }
    }

    // Change the height of list view when adding/removing ingredient.
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
