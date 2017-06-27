package eladjarby.bakeit;

import android.app.Activity;
import android.os.Bundle;

import eladjarby.bakeit.fragments.FeedFragment;

public class MainActivity extends Activity implements FeedFragment.OnFragmentInteractionListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FeedFragment feedFragment = FeedFragment.newInstance();
        getFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, feedFragment)
                .commit();
    }
}
