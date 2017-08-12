package eladjarby.bakeit.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import eladjarby.bakeit.Dialogs.myProgressDialog;
import eladjarby.bakeit.Models.BaseInterface;
import eladjarby.bakeit.Models.Model;
import eladjarby.bakeit.Models.ModelFiles;
import eladjarby.bakeit.Models.ModelFirebase;
import eladjarby.bakeit.Models.Recipe.Recipe;
import eladjarby.bakeit.Models.User.User;
import eladjarby.bakeit.Models.User.UserFirebase;
import eladjarby.bakeit.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateEditFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreateEditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateEditFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    final static int RESAULT_SUCCESS = 0;
    final static int RESAULT_FAIL = 1;
    private static final String JPEG = ".jpeg";
    private String recipeId;
    private String fragMode;
    View contentView;
    ImageView imageCapture;
    Bitmap imageBitmap;
    myProgressDialog mProgressDialog;
    ArrayList<String> ingredientsList = new ArrayList<>();
//    IngredientsListAdapter adapter = new IngredientsListAdapter();
    private OnFragmentInteractionListener mListener;
    private EditText recipeTitle,recipeIngredients,recipeTime,recipeInstructions;
    private ImageView recipeImage;

    public CreateEditFragment() {
        // Required empty public constructor
    }

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
        // Inflate the layout for this fragment
        checkCameraPermission();
        mProgressDialog = new myProgressDialog(getActivity());
        contentView = inflater.inflate(R.layout.fragment_create_edit, container, false);
        Spinner categoryDropdown = (Spinner) contentView.findViewById(R.id.recipeCategory);
        String[] items = new String[]{"Browines","Cakes","Loaves","Cupcakes & Muffins","Gluten free"};
        ArrayAdapter<String> dropdownAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,items);
        categoryDropdown.setAdapter(dropdownAdapter);
        Button saveButton = (Button) contentView.findViewById(R.id.saveButton);
        recipeTitle = (EditText) contentView.findViewById(R.id.recipeName);
        recipeIngredients = (EditText) contentView.findViewById(R.id.recipeIngredients);
        recipeTime = (EditText) contentView.findViewById(R.id.recipeTime);
        recipeInstructions = (EditText) contentView.findViewById(R.id.recipeInstructions);
        recipeImage = (ImageView) contentView.findViewById(R.id.recipePhoto);
        //ImageView recipeAddIngredient = (ImageView) contentView.findViewById(R.id.recipeAddIngredient);
        //final EditText recipeIngredientET = (EditText) contentView.findViewById(R.id.recipeIngredients);
        //ListView recipeIngredientsLV = (ListView) contentView.findViewById(R.id.recipeIngredientsList);
        //recipeIngredientsLV.setAdapter(adapter);
//        recipeAddIngredient.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ingredientsList.add(recipeIngredientET.getText().toString());
//                adapter.notifyDataSetChanged();
//                recipeIngredientET.setText("");
//            }
//        });
        switch (fragMode) {
            case "Create":
                recipeImage.setVisibility(View.GONE);
                saveButton.setText("Upload recipe");
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!validateForm()) {
                            return;
                        }
                        mProgressDialog.showProgressDialog();
                        final Recipe recipe = createRecipe();
                        if(imageBitmap != null) {
                            Model.instance.saveImage(imageBitmap, recipe.getID() + Model.instance.randomNumber() + JPEG, new BaseInterface.SaveImageListener() {
                                @Override
                                public void complete(String url) {
                                    recipe.setRecipeImage(url);
                                    Model.instance.addRecipe(recipe);
                                    mProgressDialog.hideProgressDialog();
                                    getFragmentManager().popBackStack();
                                }

                                @Override
                                public void fail() {

                                }
                            });
                        } else {
                            Model.instance.addRecipe(recipe);
                            mProgressDialog.hideProgressDialog();
                            getFragmentManager().popBackStack();
                        }
                    }
                });
                break;
            case "Edit":
                getRecipeData(Model.instance.getRecipe(recipeId));
                recipeImage.setVisibility(View.VISIBLE);
                saveButton.setText("Edit recipe");
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mProgressDialog.showProgressDialog();
                        final Recipe recipe = createRecipe();
                        if(imageBitmap != null) {
                            Model.instance.saveImage(imageBitmap, recipe.getID() + Model.instance.randomNumber() + JPEG, new BaseInterface.SaveImageListener() {
                                @Override
                                public void complete(String url) {
                                    recipe.setRecipeImage(url);
                                    Model.instance.editRecipe(recipe);
                                    mProgressDialog.hideProgressDialog();
                                    getFragmentManager().popBackStack();
                                }

                                @Override
                                public void fail() {

                                }
                            });
                        } else {
                            Model.instance.addRecipe(recipe);
                            mProgressDialog.hideProgressDialog();
                            getFragmentManager().popBackStack();
                        }
                    }
                });
                break;
        }
        imageCapture = (ImageView) contentView.findViewById(R.id.imageCapture);
        imageCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        ImageView imageGallery = (ImageView) contentView.findViewById(R.id.imageGallery);
        imageGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePictureFromGallery();
            }
        });

        return contentView;
    }

    private Recipe createRecipe() {
        String recipeId = null;
        String recipeImage = null;
        int recipeLikes = 0;
        String recipeTitle = ((EditText) contentView.findViewById(R.id.recipeName)).getText().toString();
        String recipeIngredients = ((EditText) contentView.findViewById(R.id.recipeIngredients)).getText().toString();
        String recipeInstructions = ((EditText) contentView.findViewById(R.id.recipeInstructions)).getText().toString();
        String recipeCategory = ((Spinner) contentView.findViewById(R.id.recipeCategory)).getSelectedItem().toString();
        switch (fragMode) {
            case "Create":
                recipeId = Model.instance.getCurrentUserId() + Model.instance.randomNumber();
                recipeImage = "https://firebasestorage.googleapis.com/v0/b/bakeit-f8116.appspot.com/o/noimage.png?alt=media&token=9ec9fdea-b5d4-480f-b8af-3c3f12c70aef";
                recipeLikes = 0;
                break;
            case "Edit":
                recipeId = this.recipeId;
                Recipe recipe = Model.instance.getRecipe(recipeId);
                recipeImage = recipe.getRecipeImage();
                recipeLikes = recipe.getRecipeLikes();
                break;
        }
        int recipeIsRemoved = 0;
        int recipeTime = Integer.parseInt(((EditText) contentView.findViewById(R.id.recipeTime)).getText().toString());
        String userFullName = Model.instance.getCurrentUser().getUserFirstName() + " " + Model.instance.getCurrentUser().getUserLastName();
        return new Recipe(recipeId,Model.instance.getCurrentUserId(), userFullName ,recipeTitle,recipeCategory,recipeInstructions,recipeIngredients,recipeTime,recipeImage,recipeLikes,new SimpleDateFormat("yyyy-MM-dd").format(new Date()),recipeIsRemoved);
    }

    private boolean validateForm() {
        boolean valid = true;

        String recipeName = recipeTitle.getText().toString();
        if (TextUtils.isEmpty(recipeName)) {
            recipeTitle.setError("Required.");
            valid = false;
        } else {
            recipeTitle.setError(null);
        }

        String ingredients = recipeIngredients.getText().toString();
        if (TextUtils.isEmpty(ingredients)) {
            recipeIngredients.setError("Required.");
            valid = false;
        } else {
            recipeIngredients.setError(null);
        }

        String time = recipeTime.getText().toString();
        if (TextUtils.isEmpty(time)) {
            recipeTime.setError("Required.");
            valid = false;
        } else {
            recipeTime.setError(null);
        }

        String instructions = recipeInstructions.getText().toString();
        if (TextUtils.isEmpty(instructions)) {
            recipeInstructions.setError("Required.");
            valid = false;
        } else {
            recipeInstructions.setError(null);
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
    }

    public interface OnFragmentInteractionListener {

    }

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_GALLERY = 2;

    private void takePictureFromGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_IMAGE_GALLERY);
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

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

    private void checkCameraPermission() {
        boolean hasPermission = (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.CAMERA}, 1);
        }
    }

    private void getRecipeData(Recipe recipe) {
        ((EditText)contentView.findViewById(R.id.recipeName)).setText(recipe.getRecipeTitle());
        ((EditText)contentView.findViewById(R.id.recipeIngredients)).setText(recipe.getRecipeIngredients());
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


//    static class ViewHolder {
//        public TextView ingredientName;
//        public ImageView deleteIngredient;
//    }
//    private class IngredientsListAdapter extends BaseAdapter {
//        @Override
//        public int getCount() {
//            return ingredientsList.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return ingredientsList.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//
//        @Override
//        public View getView(final int position, View convertView, ViewGroup parent) {
//            if(convertView == null) {
//                convertView = getActivity().getLayoutInflater().inflate(R.layout.ingredients_list_row,null);
//                final ViewHolder holder = new ViewHolder();
//                holder.ingredientName = (TextView) convertView.findViewById(R.id.ingredient_name);
//                holder.deleteIngredient = (ImageView) convertView.findViewById(R.id.ingredient_delete);
//                holder.deleteIngredient.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        ingredientsList.remove(position);
//                    }
//                });
//                convertView.setTag(holder);
//            }
//            ViewHolder holder = (ViewHolder) convertView.getTag();
//            holder.ingredientName.setText(ingredientsList.get(position));
//            return null;
//        }
//    }
}
