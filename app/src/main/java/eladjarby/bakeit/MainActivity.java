package eladjarby.bakeit;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Places;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import eladjarby.bakeit.Models.Model;
import eladjarby.bakeit.Models.ModelFirebase;
import eladjarby.bakeit.Models.User.UserFirebase;
import eladjarby.bakeit.Util.HttpHandler;
import eladjarby.bakeit.fragments.CreateEditFragment;
import eladjarby.bakeit.fragments.FeedFragment;
import eladjarby.bakeit.fragments.RecipeDetailsFragment;
import eladjarby.bakeit.fragments.UserProfileFragment;

public class MainActivity extends AppCompatActivity implements FeedFragment.OnFragmentInteractionListener
,CreateEditFragment.OnFragmentInteractionListener,
RecipeDetailsFragment.OnFragmentInteractionListener,
UserProfileFragment.OnFragmentInteractionListener{

    private static String url = "http://api.geonames.org/searchJSON?username=bakeit&country=il&maxRows=1000&style=SHORT";
    Set<String> citiesSet;
    ArrayList<String> citiesList;
    private ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FrameLayout layout_MainMenu = (FrameLayout) findViewById(R.id.main_fragment_container);
        layout_MainMenu.getForeground().setAlpha(0);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        citiesSet = new HashSet<String>();
        citiesList = new ArrayList<String>();
        Model.instance.syncUser();

        FeedFragment feedFragment = FeedFragment.newInstance();
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
        if(citiesSet.size() == 0) {
            new GetCities().execute();
        }
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
                closeKeyboard();
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

    // Close keyboard.
    private void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
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

    public class GetCities extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e("TAG", "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray cities = jsonObj.getJSONArray("geonames");

                    // looping through All Contacts
                    for (int i = 0; i < cities.length(); i++) {
                        JSONObject c = cities.getJSONObject(i);
                        String name = c.getString("name");
                        citiesSet.add(name);
                    }
                    citiesList.addAll(citiesSet);
                } catch (final JSONException e) {
                    Log.e("TAG", "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e("TAG", "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
        }
    }

    public List<String> getCitiesList() {
        return citiesList;
    }

}
