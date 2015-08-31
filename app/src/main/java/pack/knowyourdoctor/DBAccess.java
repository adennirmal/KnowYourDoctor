package pack.knowyourdoctor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Home on 3/29/2015.
 */
public class DBAccess extends SQLiteOpenHelper {
    //database version
    private static final int database_VERSION = 1;
    // database name
    private static final String database_NAME = "KYDDB";
    private static final String table_Ratings = "ratings";
    private static final String comment_ID = "cid";

    public DBAccess(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, database_NAME, factory, database_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_BOOK_TABLE = "CREATE TABLE " + table_Ratings + " ( " + comment_ID + " INTEGER PRIMARY KEY )";
        db.execSQL(CREATE_BOOK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + table_Ratings);
        onCreate(db);
    }

    //Method to Insert a book
    public void insertCommentID(int cid) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(comment_ID, cid);

        db.insert(table_Ratings, null, values);

        db.close();
    }

    //Method to get All Book Details
    public ArrayList<Integer> getAllCommentedIds() {
        ArrayList<Integer> commentedIDs = new ArrayList<Integer>();

        String query = "SELECT * FROM " + table_Ratings;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            Integer commentId = new Integer(cursor.getString(0));
            commentedIDs.add(commentId);
        }
        return commentedIDs;
    }

    // Deleting single book
    public void deleteCommentID(Integer cid) {
        // get reference of the BookDB database
        SQLiteDatabase db = this.getWritableDatabase();

        // delete book
        db.delete(table_Ratings, comment_ID + " = ?", new String[]{String.valueOf(cid)});
        db.close();
    }


}
