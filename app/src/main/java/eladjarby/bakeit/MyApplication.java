package eladjarby.bakeit;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
    private static Context context;

    // Get the application context.
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    // Getter for the context application.
    public static Context getMyContext(){
        return context;
    }
}
