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
                + " zip REAL,"
                + " provider TEXT"
                + " );";
        this.getWritableDatabase().execSQL(CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(db);
    }

    /* Add a user profile object to DB*/
    public void addUserToDB(User user, String TABLE_NAME) {

        User newUser = this.getUserDB(TABLE_NAME, user.getEmail());
        SQLiteDatabase db = this.getWritableDatabase();

        if (newUser == null) {
            ContentValues values = new ContentValues();
            values.put("first_name", user.getFirstName());
            values.put("last_name", user.getLastName());
            values.put("email", user.getEmail());
            values.put("zip", user.getZip());
            values.put("provider", user.getProvider());

            db.insert(TABLE_NAME, null, values);
        } else {
            ContentValues values = new ContentValues();
            values.put("first_name", user.getFirstName());
            values.put("last_name", user.getLastName());
            values.put("zip", user.getZip());
            values.put("provider", user.getProvider());

            db.update(TABLE_NAME, values, "email= '" + user.getEmail() + "'", null);
        }
        db.close();
    }

    public List<User> getUsersFromDB(String TABLE_NAME, String email) {
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        List<User> userList = new ArrayList<>();
        while (cursor.moveToNext()) {
            User user = new User(cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getLong(3),
                    cursor.getString(4)
            );
            userList.add(user);
        }
        db.close();
        return userList;
    }

    public User getUserDB(String TABLE_NAME, String email) {
        String query = "SELECT * FROM " + TABLE_NAME + "WHERE email= '" + email + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        User user = null;
        if (cursor.moveToNext()) {
            user = new User(cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getLong(3),
                    cursor.getString(4)
            );
        }
        db.close();
        return user;
    }

    public void deleteUserFromDB(String TABLE_NAME, String email) {
        String delQuery = "DELETE FROM " + TABLE_NAME + " WHERE email= '" + email + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(delQuery);
        db.close();
    }
}
