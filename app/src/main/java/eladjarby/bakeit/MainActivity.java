package eladjarby.bakeit;

import android.app.Activity;
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

public class MainActivity extends AppCompatActivity implements FeedFragment.OnFragmentInteractionListener
,CreateEditFragment.OnFragmentInteractionListener{

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


        FeedFragment feedFragment = FeedFragment.newInstance();
        getFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, feedFragment)
                .commit();
    }

    @Override
    public void onItemSelected(String recipeId) {
        Log.d("tag",recipeId);
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

    public boolean onCreateOptionsMenu(Menu menu){
        return super.onCreateOptionsMenu(menu);
    }
}
