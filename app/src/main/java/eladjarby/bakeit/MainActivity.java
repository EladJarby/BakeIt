package eladjarby.bakeit;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import eladjarby.bakeit.Models.Model;
import eladjarby.bakeit.Models.ModelFirebase;
import eladjarby.bakeit.Models.User.UserFirebase;
import eladjarby.bakeit.fragments.CreateEditFragment;
import eladjarby.bakeit.fragments.FeedFragment;
import eladjarby.bakeit.fragments.RecipeDetailsFragment;
import eladjarby.bakeit.fragments.UserProfileFragment;

public class MainActivity extends AppCompatActivity implements FeedFragment.OnFragmentInteractionListener
,CreateEditFragment.OnFragmentInteractionListener,
RecipeDetailsFragment.OnFragmentInteractionListener,
UserProfileFragment.OnFragmentInteractionListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        Model.instance.syncUser();

        FeedFragment feedFragment = FeedFragment.newInstance("list","");
        getFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, feedFragment)
                .commit();
    }

    @Override
    public void onItemSelected(String recipeId) {
        RecipeDetailsFragment recipeDetailsFragment = RecipeDetailsFragment.newInstance(recipeId);

        getFragmentManager().beginTransaction()
                .add(R.id.main_fragment_container,recipeDetailsFragment).addToBackStack(null)
                .commit();
    }

    @Override
    public void addRecipe() {
        CreateEditFragment createEditFragment = CreateEditFragment.newInstance("","Create");

        getFragmentManager().beginTransaction()
                .add(R.id.main_fragment_container,createEditFragment).addToBackStack(null)
                .commit();
    }

    @Override
    public void editRecipe(String recipeId) {
        CreateEditFragment createEditFragment = CreateEditFragment.newInstance(recipeId,"Edit");

        getFragmentManager().beginTransaction()
                .add(R.id.main_fragment_container,createEditFragment).addToBackStack(null)
                .commit();
    }

    @Override
    public void userProfile() {
        UserProfileFragment userProfileFragment = UserProfileFragment.newInstance();
        getFragmentManager().beginTransaction()
                .add(R.id.main_fragment_container, userProfileFragment).addToBackStack(null)
                .commit();
    }

    @Override
    public void logout() {
        UserFirebase.logoutAccount();
        Intent mainIntent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(mainIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                getFragmentManager().popBackStack();
                showMenu();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount() == 0) {
            finish();
        }
        getFragmentManager().popBackStack();
        showMenu();
    }

    public void showMenu() {
        ActionBar actionBar = ((AppCompatActivity) this).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        ImageView menuAdd = (ImageView) findViewById(R.id.menu_add);
        ImageView menuProfile = (ImageView) findViewById(R.id.menu_profile);
        TextView menuTitle = (TextView) findViewById(R.id.menu_title);
        TextView menuTitleBakeIt = (TextView) findViewById(R.id.menu_title_bakeit);
        menuTitle.setVisibility(View.GONE);
        menuTitleBakeIt.setVisibility(View.VISIBLE);
        menuAdd.setVisibility(View.VISIBLE);
        menuProfile.setVisibility(View.VISIBLE);
    }

}
