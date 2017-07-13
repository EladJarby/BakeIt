package eladjarby.bakeit;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import eladjarby.bakeit.fragments.FeedFragment;

public class MainActivity extends AppCompatActivity implements FeedFragment.OnFragmentInteractionListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        FeedFragment feedFragment = FeedFragment.newInstance();
        getFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, feedFragment)
                .commit();
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.item_search);
        final SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("Search");
        return super.onCreateOptionsMenu(menu);
    }

}
