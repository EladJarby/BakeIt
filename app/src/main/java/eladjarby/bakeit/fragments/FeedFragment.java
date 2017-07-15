package eladjarby.bakeit.fragments;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import eladjarby.bakeit.Models.Model;
import eladjarby.bakeit.Models.Recipe.Recipe;
import eladjarby.bakeit.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FeedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedFragment extends Fragment {
    List<Recipe> recipeList;
    RecipeListAdapter adapter = new RecipeListAdapter();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FeedFragment() {
        // Required empty public constructor
    }
    // TODO: Rename and change types and number of parameters
    public static FeedFragment newInstance() {
        FeedFragment fragment = new FeedFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle("");

        View contentView = inflater.inflate(R.layout.fragment_feed, container, false);
        ListView list = (ListView) contentView.findViewById(R.id.recipeList);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListener.onItemSelected(recipeList.get(position).getID());
            }
        });

        recipeList = Model.instance.getRecipeList();
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        //((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onItemSelected(String recipeId);
    }

    class RecipeListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return recipeList.size();
        }

        @Override
        public Object getItem(int position) {
            return recipeList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.feed_list_row,null);
                TextView likesTv = (TextView) convertView.findViewById(R.id.strow_likes);
                likesTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Problem could happend: will raise likes every click
                        int pos = (int)v.getTag();
                        Recipe recipe = recipeList.get(pos);
                        recipe.setRecipeLikes(recipe.getRecipeLikes()+1);
                    }
                });
            }
            TextView recipeDescription = (TextView) convertView.findViewById(R.id.strow_description);
            ImageView recipeImage = (ImageView) convertView.findViewById(R.id.strow_image);
            TextView recipeDate = (TextView) convertView.findViewById(R.id.strow_date);
            TextView recipeLikes = (TextView) convertView.findViewById(R.id.strow_likes);
            ImageView recipeAuthorImage = (ImageView) convertView.findViewById(R.id.strow_authorImage);
            TextView recipeCategory = (TextView) convertView.findViewById(R.id.strow_category);
            TextView recipeHeader = (TextView) convertView.findViewById(R.id.strow_header);
            ImageView recipeArrow = (ImageView) convertView.findViewById(R.id.strow_arrow);
            Recipe recipe = recipeList.get(position);
            recipeDescription.setText(recipe.getRecipeTitle());
            recipeCategory.setText(recipe.getRecipeCategory());
            recipeHeader.setText("Elad posted a reicope on");
            recipeDate.setText("3hrs");
            recipeLikes.setText(recipe.getRecipeLikes() + " peoples liked");
            recipeLikes.setTag(position);
            return convertView;
        }
    }
}
