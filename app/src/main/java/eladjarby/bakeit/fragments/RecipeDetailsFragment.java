package eladjarby.bakeit.fragments;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;

import eladjarby.bakeit.Models.BaseInterface;
import eladjarby.bakeit.Models.Model;
import eladjarby.bakeit.Models.ModelFiles;
import eladjarby.bakeit.Models.Recipe.Recipe;
import eladjarby.bakeit.Models.User.User;
import eladjarby.bakeit.Models.User.UserFirebase;
import eladjarby.bakeit.R;

public class RecipeDetailsFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";

    private String recipeId;

    private OnFragmentInteractionListener mListener;

    public RecipeDetailsFragment() {}

    // New instance for recipe details.
    public static RecipeDetailsFragment newInstance(String recipeId) {
        RecipeDetailsFragment fragment = new RecipeDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, recipeId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recipeId = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Get the action bar from activity.
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Set menu.
        ImageView menuAdd = (ImageView) getActivity().findViewById(R.id.menu_add);
        ImageView menuProfile = (ImageView) getActivity().findViewById(R.id.menu_profile);
        TextView menuTitle = (TextView) getActivity().findViewById(R.id.menu_title);
        TextView menuTitleBakeIt = (TextView) getActivity().findViewById(R.id.menu_title_bakeit);
        menuTitleBakeIt.setVisibility(View.GONE);
        menuTitle.setVisibility(View.VISIBLE);
        menuAdd.setVisibility(View.GONE);
        menuProfile.setVisibility(View.GONE);
        menuTitle.setText("Recipe details");

        // Inflate the layout for this fragment
        final View contentView = inflater.inflate(R.layout.fragment_recipe_details, container, false);

        // Get the details to fill all the fields.
        final Recipe recipe = Model.instance.getRecipe(recipeId);
        ((TextView)contentView.findViewById(R.id.details_title)).setText(recipe.getRecipeTitle());
        ((TextView)contentView.findViewById(R.id.details_ingredients)).setText(recipe.getRecipeIngredients());
        ((TextView)contentView.findViewById(R.id.details_time)).setText("" + recipe.getRecipeTime());
        ((TextView)contentView.findViewById(R.id.details_instructions)).setText(recipe.getRecipeInstructions());
        ((TextView)contentView.findViewById(R.id.details_date)).setText(recipe.getRecipeDate());
        ((TextView)contentView.findViewById(R.id.details_likes)).setText("" + recipe.getRecipeLikes());
        ((TextView)contentView.findViewById(R.id.details_category)).setText(recipe.getRecipeCategory());
        UserFirebase.getUser(recipe.getRecipeAuthorId(), new BaseInterface.GetUserCallback() {
            @Override
            public void onComplete(User user) {
                if(user.getUserImage() != null && !user.getUserImage().isEmpty() && !user.getUserImage().equals("")) {
                    ((ImageView) contentView.findViewById(R.id.details_author_image)).setImageBitmap(getCroppedBitmap(ModelFiles.loadImageFromFile(URLUtil.guessFileName(user.getUserImage(), null, null)),1));
                } else {
                    ((ImageView) contentView.findViewById(R.id.details_author_image)).setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.bakeitlogo));
                }
            }
            @Override
            public void onCancel() {

            }
        });
        if(recipe.getRecipeImage() != null && !recipe.getRecipeImage().isEmpty() && !recipe.getRecipeImage().equals("")) {
            ((ImageView) contentView.findViewById(R.id.details_image)).setImageBitmap(ModelFiles.loadImageFromFile(URLUtil.guessFileName(recipe.getRecipeImage(), null, null)));
        } else {
            ((ImageView) contentView.findViewById(R.id.details_image)).setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.bakeitlogo));
        }
        // Inflate the layout for this fragment
        return contentView;
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
        // TODO: Update argument type and name
    }

    // Get cropped circle bitmap of user image.
    public Bitmap getCroppedBitmap(Bitmap bitmap,int borderWidth) {
        final int width = bitmap.getWidth() + borderWidth;
        final int height = bitmap.getHeight() + borderWidth;

        Bitmap canvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(shader);

        Canvas canvas = new Canvas(canvasBitmap);
        float radius = width > height ? ((float) height) / 2f : ((float) width) / 2f;
        canvas.drawCircle(width / 2, height / 2, radius, paint);
        paint.setShader(null);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(borderWidth);
        canvas.drawCircle(width / 2, height / 2, radius - borderWidth / 2, paint);
        return canvasBitmap;
    }
}
