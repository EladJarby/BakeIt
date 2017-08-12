package eladjarby.bakeit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import eladjarby.bakeit.Models.Model;
import eladjarby.bakeit.Models.ModelFirebase;
import eladjarby.bakeit.fragments.CreateEditFragment;
import eladjarby.bakeit.fragments.FeedFragment;
import eladjarby.bakeit.fragments.RecipeDetailsFragment;

public class MainActivity extends AppCompatActivity implements FeedFragment.OnFragmentInteractionListener
,CreateEditFragment.OnFragmentInteractionListener,
RecipeDetailsFragment.OnFragmentInteractionListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        final SearchView searchView = (SearchView) findViewById(R.id.item_search);
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("Search");
        searchView.setFocusable(false);


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
        FeedFragment feedProfile = FeedFragment.newInstance("profile","");
        getFragmentManager().beginTransaction()
                .add(R.id.main_fragment_container, feedProfile).addToBackStack(null)
                .commit();
    }

    public boolean onCreateOptionsMenu(Menu menu){
        return super.onCreateOptionsMenu(menu);
    }
    
}
