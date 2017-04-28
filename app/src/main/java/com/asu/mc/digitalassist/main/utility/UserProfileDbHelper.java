package com.asu.mc.digitalassist.main.utility;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.asu.mc.digitalassist.main.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mlenka on 2/21/2017.
 */

public class UserProfileDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private String TABLE_NAME = null;

    public UserProfileDbHelper(Context context, final String DATABASE_NAME) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    public void createTable(String TABLE_NAME) {
        this.TABLE_NAME = TABLE_NAME;
        String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS "
                + TABLE_NAME
                + " ( "
                + " first_name TEXT, "
                + " last_name TEXT, "
                + " email TEXT, "
                + " zip REAL"
                + " );";
        this.getWritableDatabase().execSQL(CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(db);
    }

    /* Add a sensor sample to DB*/
    public void addUserToDB(User user, String TABLE_NAME) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("first_name", user.getFirstName());
        values.put("last_name", user.getLastName());
        values.put("email", user.getEmail());
        values.put("zip", user.getZip());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    /*Get k most recent samples from DB*/
    public List<User> getUsersFromDB(String TABLE_NAME, String email) {
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY zip WHERE email = '" + email+ "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        List<User> userList = new ArrayList<>();
        while (cursor.moveToNext()) {
            User user = new User(cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getLong(3)
            );
            userList.add(user);
        }
        return userList;
    }
}
