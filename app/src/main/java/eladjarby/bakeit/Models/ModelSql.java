package eladjarby.bakeit.Models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import eladjarby.bakeit.Models.Recipe.RecipeSql;

/**
 * Created by EladJ on 27/6/2017.
 */

public class ModelSql extends SQLiteOpenHelper {

    // Constructor to make a new sql lite db called: database.db with version: 1.
    ModelSql(Context context) {
        super(context,"database.db", null , 1);
    }

    // On create execute RecipeSql with sql lite db.
    @Override
    public void onCreate(SQLiteDatabase db) {
        RecipeSql.onCreate(db);
    }

    // When upgrading db , onUpgrade is called.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        RecipeSql.onUpgrade(db,oldVersion,newVersion);
    }
}
