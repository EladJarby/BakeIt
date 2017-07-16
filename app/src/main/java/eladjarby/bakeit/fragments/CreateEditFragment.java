package eladjarby.bakeit.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;

import eladjarby.bakeit.Models.Model;
import eladjarby.bakeit.Models.Recipe.Recipe;
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

    // TODO: Rename and change types of parameters
    private String userId;
    View contentView;

    private OnFragmentInteractionListener mListener;

    public CreateEditFragment() {
        // Required empty public constructor
    }

    public static CreateEditFragment newInstance(String param1) {
        CreateEditFragment fragment = new CreateEditFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        contentView = inflater.inflate(R.layout.fragment_create_edit, container, false);
        Spinner categoryDropdown = (Spinner) contentView.findViewById(R.id.recipeCategory);
        String[] items = new String[]{"Browines","Cakes","Loaves","Cupcakes & Muffins","Gluten free"};
        ArrayAdapter<String> dropdownAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,items);
        categoryDropdown.setAdapter(dropdownAdapter);
        Button saveButton = (Button) contentView.findViewById(R.id.saveButton);
        saveButton.setText("Upload recipe");
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Model.instance.addRecipe(createRecipe());
                getFragmentManager().popBackStack();
            }
        });
        return contentView;
    }

    private Recipe createRecipe() {
        String recipeId = userId + Model.instance.randomNumber();
        String recipeTitle = ((EditText) contentView.findViewById(R.id.recipeName)).getText().toString();
        String recipeIngredients = ((EditText) contentView.findViewById(R.id.recipeIngredients)).getText().toString();
        String recipeInstructions = ((EditText) contentView.findViewById(R.id.recipeInstructions)).getText().toString();
        String recipeCategory = ((Spinner) contentView.findViewById(R.id.recipeCategory)).getSelectedItem().toString();
        String recipeImage = "image";
        int recipeLikes = 0;
        int recipeTime = Integer.parseInt(((EditText) contentView.findViewById(R.id.recipeTime)).getText().toString());
        return new Recipe(recipeId,userId,recipeTitle,recipeCategory,recipeInstructions,recipeIngredients,recipeTime,recipeImage,recipeLikes,new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
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

}
