package eladjarby.bakeit.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import eladjarby.bakeit.Models.BaseInterface;
import eladjarby.bakeit.Models.Model;
import eladjarby.bakeit.Models.Recipe.Recipe;
import eladjarby.bakeit.Models.User.User;
import eladjarby.bakeit.Models.User.UserFirebase;
import eladjarby.bakeit.MyApplication;
import eladjarby.bakeit.R;


public class FeedFragment extends Fragment {
    List<Recipe> recipeList = new LinkedList<Recipe>();
    RecipeListAdapter adapter = new RecipeListAdapter();
    ListView list;
    private PopupWindow popWindow;
    private FrameLayout layout_MainMenu;

    // Subscribe to recipe updates (new recipe).
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Model.RecipeUpdateEvent event) {
        boolean exist = false;
        // Check if recipe already exist.
        for(Recipe recipe: recipeList) {
            if(recipe.getID().equals(event.recipe.getID())) {
                recipe = event.recipe;
                exist = true;
                break;
            }
        }
        // If recipe not exist , so add recipe to list.
        if(!exist) {
            if(event.recipe.getRecipeIsRemoved() == 0) {
                recipeList.add(0, event.recipe);
            }
            // Notify about change.
            adapter.notifyDataSetChanged();
        }
    }

    // Subscribe if recipe changed (update/delete)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Model.RecipeChangedEvent event) {
        int index = 0;
        // Get the index for recipe in the existed list.
        for(index = 0; index < recipeList.size(); index++) {
            if(recipeList.get(index).getID().equals(event.recipe.getID())) {
                break;
            }
        }
        if(index < recipeList.size()) {
            // Change existed recipe to updated recipe.
            recipeList.set(index,event.recipe);
            // Check if recipe is with remove flag , if it does , so remove from list.
            if(event.recipe.getRecipeIsRemoved() == 1) {
                recipeList.remove(index);
            }
            // Notify about change.
            adapter.notifyDataSetChanged();
        }
    }

    private OnFragmentInteractionListener mListener;

    public FeedFragment() { }

    public static FeedFragment newInstance() {
        return new FeedFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Register to updates from EventBus.
        EventBus.getDefault().register(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Check permission.
        checkPermission();
        // Inflate the layout for this fragment
        View contentView = inflater.inflate(R.layout.fragment_feed, container, false);

        ImageView menuAdd = (ImageView) getActivity().findViewById(R.id.menu_add);
        ImageView menuProfile = (ImageView) getActivity().findViewById(R.id.menu_profile);
        TextView menuTitleBakeIt = (TextView) getActivity().findViewById(R.id.menu_title_bakeit);
        menuTitleBakeIt.setVisibility(View.VISIBLE);

        list = (ListView) contentView.findViewById(R.id.recipeList);

        // Get recipe list and notify about change in the list.
        Model.instance.getRecipeList(new BaseInterface.GetAllRecipesCallback() {
            @Override
            public void onComplete(List<Recipe> list) {
                recipeList = list;
                adapter.notifyDataSetChanged();
            }
        });
        // Catch click on add button , switching fragments to add recipe.
        menuAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.addRecipe();
            }
        });
        // Catch click on profile button , switching fragments to user profile.
        menuProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.userProfile();
            }
        });
        // Set custom adapter.
        list.setAdapter(adapter);
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

    public interface OnFragmentInteractionListener {
        void onItemSelected(String recipeId);
        void addRecipe();
        void editRecipe(String recipeId);
        void userProfile();
    }

    // Check permissions.
    private void checkPermission() {
        boolean hasPermission = (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    // View holder.
    private static class ViewHolder {
        ImageView recipeAuthorImage;
        TextView recipeTitle;
        TextView recipeDate;
        TextView recipeDescription;
        ImageView recipeImage;
        TextView recipeLikes;
        ImageView recipeArrow;
        ImageView likeImage;
    }

    private class RecipeListAdapter extends BaseAdapter {

        ViewHolder holder;
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
                // inflate each row to feed list.
                convertView = getActivity().getLayoutInflater().inflate(R.layout.feed_list_row,null);
                final ViewHolder holder = new ViewHolder();

                // Set all ids to holder.
                holder.recipeAuthorImage = (ImageView) convertView.findViewById(R.id.strow_authorImage);
                holder.recipeTitle = (TextView) convertView.findViewById(R.id.strow_header);
                holder.recipeDescription = (TextView) convertView.findViewById(R.id.strow_description);
                holder.recipeDate = (TextView) convertView.findViewById(R.id.strow_date);
                holder.recipeLikes = (TextView) convertView.findViewById(R.id.strow_likes);
                holder.recipeImage = (ImageView) convertView.findViewById(R.id.strow_image);
                holder.recipeArrow = (ImageView) convertView.findViewById(R.id.strow_arrow);
                holder.likeImage = (ImageView) convertView.findViewById(R.id.strow_likeImage);

                // Catch click on recipe likes , add / remove like for every click
                holder.recipeLikes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Set position with tag.
                        int pos = (int)holder.recipeLikes.getTag();
                        // Get recipe with given position
                        Recipe recipe = recipeList.get(pos);
                        Model.instance.changeLike(recipe);
                        // Set animation for like heart image.
                        Animation pulse = AnimationUtils.loadAnimation(getActivity(), R.anim.pulse);
                        holder.likeImage.setAnimation(pulse);
                    }
                });
                // Catch click on recipe arrow to edit/remove recipe.
                holder.recipeArrow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = (int)holder.recipeLikes.getTag();
                        onShowPopup(v,pos);
                    }
                });
                //Catch click on recipe image to show details about recipe.
                holder.recipeImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = (int)holder.recipeLikes.getTag();
                        mListener.onItemSelected(recipeList.get(pos).getID());
                    }
                });
                convertView.setTag(holder);
            }

            // Get recipe by position.
            final Recipe recipe = recipeList.get(position);
            final ViewHolder holder = (ViewHolder) convertView.getTag();

            //Set spinners
            final ProgressBar authorProgressBar = ((ProgressBar) convertView.findViewById(R.id.authorProgressBar));
            final ProgressBar recipeProgressBar = ((ProgressBar) convertView.findViewById(R.id.recipeProgressBar));
            authorProgressBar.setVisibility(View.VISIBLE);
            recipeProgressBar.setVisibility(View.GONE);

            // Get all recipe details.
            holder.recipeDescription.setText(recipe.getRecipeTitle());

            // Check if recipe is related to user , if it doesnt, so dont show arrow for this recipe.
            if(UserFirebase.getCurrentUserId().equals(recipe.getRecipeAuthorId())) {
                holder.recipeArrow.setVisibility(View.VISIBLE);
            } else {
                holder.recipeArrow.setVisibility(View.INVISIBLE);
            }

            // Set style with spannable for header.
            String userName = recipe.getRecipeAuthorName();
            String recipeHeader = " posted a recipe on ";
            String category = recipe.getRecipeCategory();
            String finalString = userName+recipeHeader + category;
            Spannable sb = new SpannableString(userName+recipeHeader + category);
            Bitmap categoryIcon = BitmapFactory.decodeResource(getResources(),android.R.drawable.ic_menu_my_calendar);
            sb.setSpan(new StyleSpan(Typeface.BOLD), finalString.indexOf(userName),finalString.indexOf(userName)+ userName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            sb.setSpan(new ImageSpan(MyApplication.getMyContext(),Bitmap.createScaledBitmap(categoryIcon, 70, 70, false)),(userName+recipeHeader).length()-1,(userName+recipeHeader).length(),Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            sb.setSpan(new StyleSpan(Typeface.BOLD), finalString.indexOf(category),finalString.indexOf(category)+ category.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            sb.setSpan(new RelativeSizeSpan(0.8f), finalString.indexOf(category),finalString.indexOf(category)+ category.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.recipeTitle.setText(sb, TextView.BufferType.SPANNABLE);

            // Get user image.
            UserFirebase.getUser(recipe.getRecipeAuthorId(), new BaseInterface.GetUserCallback() {
                @Override
                public void onComplete(final User user) {
                    final String userImageUrl = user.getUserImage();
                    // Boolean to check if image updated.
                    boolean authorImageUpdated = false;
                    if(holder.recipeAuthorImage.getTag() == null || !holder.recipeAuthorImage.getTag().equals(userImageUrl)) {
                        holder.recipeAuthorImage.setTag(userImageUrl);
                        authorImageUpdated = true;
                    }
                    if (userImageUrl != null && !userImageUrl.isEmpty() && !userImageUrl.equals("") && authorImageUpdated) {
                        holder.recipeAuthorImage.setImageDrawable(null);
                        authorImageUpdated = false;
                        Model.instance.getImage(userImageUrl, new BaseInterface.GetImageListener() {
                            @Override
                            public void onSuccess(Bitmap image) {
                                String imageUrl = holder.recipeAuthorImage.getTag().toString();
                                if (imageUrl.equals(userImageUrl)) {
                                    holder.recipeAuthorImage.setImageBitmap(image);
                                }
                            }

                            @Override
                            public void onFail() {
                            }
                        });
                    }
                    authorProgressBar.setVisibility(View.GONE);
                }

                @Override
                public void onCancel() {
                }
            });

            // Boolean to check if image updated.
            boolean recipeImageUpdated = false;
            if(holder.recipeImage.getTag() == null || !holder.recipeImage.getTag().equals(recipe.getRecipeImage())) {
                holder.recipeImage.setTag(recipe.getRecipeImage());
                recipeImageUpdated = true;
            }
            if (recipe.getRecipeImage() != null && !recipe.getRecipeImage().isEmpty()
                    && !recipe.getRecipeImage().equals("") && recipeImageUpdated) {
                recipeImageUpdated = false;
                holder.recipeImage.setImageDrawable(null);
                recipeProgressBar.setVisibility(View.VISIBLE);
                Model.instance.getImage(recipe.getRecipeImage(), new BaseInterface.GetImageListener() {
                    @Override
                    public void onSuccess(Bitmap image) {
                        String imageUrl = holder.recipeImage.getTag().toString();
                        if (imageUrl.equals(recipe.getRecipeImage())) {
                            holder.recipeImage.setImageBitmap(image);
                        }
                        recipeProgressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFail() {
                    }
                });
            }
            holder.recipeDate.setText(recipe.getRecipeDate());
            holder.recipeLikes.setText(recipe.getRecipeLikes() + " peoples liked");
            holder.recipeLikes.setTag(position);
            return convertView;
        }
    }

    // Show popup after pressing on arrow to edit/delete recipe.
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

        layout_MainMenu = (FrameLayout) getActivity().findViewById(R.id.main_fragment_container);

        // set dim when popup window show
        layout_MainMenu.getForeground().setAlpha(50);

        // set height depends on the device size
        popWindow = new PopupWindow(inflatedView, size.x, WindowManager.LayoutParams.WRAP_CONTENT, true );

        popWindow.setFocusable(true);

        // make it outside touchable to dismiss the popup window
        popWindow.setOutsideTouchable(true);

        // set animation to popup.
        popWindow.setAnimationStyle(R.style.AnimationPopup);
        popWindow.setBackgroundDrawable(new ColorDrawable(
                android.graphics.Color.TRANSPARENT));

        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                popWindow.dismiss();
                layout_MainMenu.getForeground().setAlpha(0);
            }
        });

        // show the popup at bottom of the screen and set some margin at bottom ie,
        popWindow.showAtLocation(v, Gravity.BOTTOM, 0,0);
    }

    // Set list for edit/remove popup
    void setSimpleList(ListView listView, final int positionList){

        final ArrayList<String> popupList = new ArrayList<String>();

        popupList.add("Edit recipe");
        popupList.add("Delete");

        // Set array adapter.
        listView.setAdapter(new ArrayAdapter<String>(getActivity(),
                R.layout.arrow_popup_row, R.id.arrow_popup_title,popupList));

        // Catch click on edit/delete recipe.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*
                 If position = 1 (remove) , so remove recipe by setting 1 to 'recipeIsRemove',
                 Else position = 0 (edit) , edit recipe.
                  */

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
                // Dismiss popup after clicking on option.
                popWindow.dismiss();
                // Cancel dim.
                layout_MainMenu.getForeground().setAlpha(0);
            }
        });
    }

    @Override
    public void onDestroy() {
        // Unregister for new updates from Eventbus.
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
