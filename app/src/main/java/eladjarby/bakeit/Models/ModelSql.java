package eladjarby.bakeit.Models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import eladjarby.bakeit.Models.Recipe.RecipeSql;

/**
 * Created by EladJ on 27/6/2017.
 */

public class ModelSql extends SQLiteOpenHelper {
    ModelSql(Context context) {
        super(context,"database.db", null , 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        RecipeSql.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        RecipeSql.onUpgrade(db,oldVersion,newVersion);
    }
}
