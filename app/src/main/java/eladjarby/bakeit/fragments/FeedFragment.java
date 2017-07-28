package eladjarby.bakeit.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import eladjarby.bakeit.Models.BaseInterface;
import eladjarby.bakeit.Models.Model;
import eladjarby.bakeit.Models.Recipe.Recipe;
import eladjarby.bakeit.Models.User.User;
import eladjarby.bakeit.Models.User.UserFirebase;
import eladjarby.bakeit.MyApplication;
import eladjarby.bakeit.R;

import static android.support.v7.content.res.AppCompatResources.getDrawable;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FeedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedFragment extends Fragment {
    List<Recipe> recipeList = new LinkedList<Recipe>();
    RecipeListAdapter adapter = new RecipeListAdapter();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ListView list;
    private PopupWindow popWindow;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Model.RecipeUpdateEvent event) {
        //Toast.makeText(getActivity(),"got new recipe",Toast.LENGTH_SHORT).show();
        boolean exist = false;
        for(Recipe recipe: recipeList) {
            if(recipe.getID().equals(event.recipe.getID())) {
                recipe = event.recipe;
                exist = true;
                break;
            }
        }
        if(!exist) {
            recipeList.add(0 , event.recipe);
            adapter.notifyDataSetChanged();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Model.RecipeChangedEvent event) {
        //Toast.makeText(getActivity(),"got new recipe",Toast.LENGTH_SHORT).show();
        int index = 0;
        for(index = 0; index < recipeList.size(); index++) {
            if(recipeList.get(index).getID().equals(event.recipe.getID())) {
                break;
            }
        }
        if(index < recipeList.size()) {
            recipeList.set(index,event.recipe);
            adapter.notifyDataSetChanged();
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Model.RecipeRemoveEvent event) {
        //Toast.makeText(getActivity(),"got new recipe",Toast.LENGTH_SHORT).show();
        boolean exist = false;
        Recipe recipe = null;
        for(Recipe r: recipeList) {
            if(r.getID().equals(event.recipeId)){
                 recipe = r;
                exist = true;
                break;
            }
        }
        if(exist && recipe != null) {
            recipeList.remove(recipe);
            adapter.notifyDataSetChanged();
        }
    }
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
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        checkPermission();
        // Inflate the layout for this fragment
        getActivity().setTitle("");

        View contentView = inflater.inflate(R.layout.fragment_feed, container, false);
        list = (ListView) contentView.findViewById(R.id.recipeList);

        Model.instance.getRecipeList(new BaseInterface.GetAllRecipesCallback() {
            @Override
            public void onComplete(List<Recipe> list) {
                recipeList = list;
                adapter.notifyDataSetChanged();
            }
        });
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListener.onItemSelected(recipeList.get(position).getID());
            }
        });

        ImageView menuAdd = (ImageView) getActivity().findViewById(R.id.menu_add);
        menuAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.addRecipe();
            }
        });
        list.setAdapter(adapter);
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
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
        void addRecipe();
        void editRecipe(String recipeId);
    }

    private void checkPermission() {
        boolean hasPermission = (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
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
        public View getView(final int position, View convertView, ViewGroup parent) {
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
            final ImageView recipeImage = (ImageView) convertView.findViewById(R.id.strow_image);
            TextView recipeDate = (TextView) convertView.findViewById(R.id.strow_date);
            TextView recipeLikes = (TextView) convertView.findViewById(R.id.strow_likes);
            final ImageView recipeAuthorImage = (ImageView) convertView.findViewById(R.id.strow_authorImage);
            TextView recipeCategory = (TextView) convertView.findViewById(R.id.strow_category);
            final TextView recipeHeader = (TextView) convertView.findViewById(R.id.strow_header);
            ImageView recipeArrow = (ImageView) convertView.findViewById(R.id.strow_arrow);
            recipeArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onShowPopup(v,position);
                }
            });
            final Recipe recipe = recipeList.get(position);
            recipeDescription.setText(recipe.getRecipeTitle());
            UserFirebase.getUser(recipe.getRecipeAuthorId(), new BaseInterface.GetUserCallback() {
                @Override
                public void onComplete(final User user) {
                    recipeHeader.setText(user.getUserFirstName() + " " + user.getUserLastName() + " posted a recipe on");
                    final String userImageUrl = user.getUserImage();
                    recipeAuthorImage.setTag(userImageUrl);
                    recipeAuthorImage.setImageDrawable(getDrawable(getActivity(), R.drawable.bakeitlogo));

                    if (userImageUrl != null && !userImageUrl.isEmpty() && !userImageUrl.equals("")) {
                        Model.instance.getImage(userImageUrl, new BaseInterface.GetImageListener() {
                            @Override
                            public void onSuccess(Bitmap image) {
                                String imageUrl = recipeAuthorImage.getTag().toString();
                                if (imageUrl.equals(userImageUrl)) {
                                    recipeAuthorImage.setImageBitmap(image);
                                }
                            }

                            @Override
                            public void onFail() {

                            }
                        });
                    }
                }

                @Override
                public void onCancel() {

                }
            });
            recipeImage.setTag(recipe.getRecipeImage());
            recipeImage.setImageDrawable(getDrawable(getActivity(), R.drawable.bakeitlogo));

            if(recipe.getRecipeImage() != null && !recipe.getRecipeImage().isEmpty() && !recipe.getRecipeImage().equals("")) {
                Model.instance.getImage(recipe.getRecipeImage(), new BaseInterface.GetImageListener() {
                    @Override
                    public void onSuccess(Bitmap image) {
                        String imageUrl = recipeImage.getTag().toString();
                        if(imageUrl.equals(recipe.getRecipeImage())) {
                            recipeImage.setImageBitmap(image);
                        }
                    }

                    @Override
                    public void onFail() {

                    }
                });
            }
            recipeCategory.setText(recipe.getRecipeCategory());
            recipeDate.setText(recipe.getRecipeDate());
            recipeLikes.setText(recipe.getRecipeLikes() + " peoples liked");
            recipeLikes.setTag(position);
            return convertView;
        }
    }


    public void onShowPopup(View v,int position){

        LayoutInflater layoutInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // inflate the custom popup layout
        final View inflatedView = layoutInflater.inflate(R.layout.arrow_popup, null,false);
       // find the ListView in the popup layout
        ListView listView = (ListView)inflatedView.findViewById(R.id.arrow_popup_list);

        // get device size
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);

        // fill the data to the list items
        setSimpleList(listView,position);

        // set height depends on the device size
        popWindow = new PopupWindow(inflatedView, size.x, WindowManager.LayoutParams.WRAP_CONTENT, true );

        // make it focusable to show the keyboard to enter in `EditText`
        popWindow.setFocusable(true);
        // make it outside touchable to dismiss the popup window
        popWindow.setOutsideTouchable(true);

        popWindow.setAnimationStyle(R.style.AnimationPopup);
        // show the popup at bottom of the screen and set some margin at bottom ie,
        popWindow.showAtLocation(v, Gravity.BOTTOM, 0,0);
    }

    void setSimpleList(ListView listView, final int positionList){

        final ArrayList<String> popupList = new ArrayList<String>();

        popupList.add("Edit recipe");
        popupList.add("Delete");
        listView.setAdapter(new ArrayAdapter<String>(getActivity(),
                R.layout.arrow_popup_row, R.id.arrow_popup_title,popupList));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 1) {
                    Log.d("TAG","" + position);
                    Model.instance.removeRecipe(recipeList.get(positionList).getID(), new BaseInterface.GetRecipeCallback() {
                        @Override
                        public void onComplete() {
                        }

                        @Override
                        public void onCancel() {

                        }
                    });
                } else if(position == 0) {
                    mListener.editRecipe(recipeList.get(positionList).getID());
                }
                popWindow.dismiss();
            }
        });
    }
    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
