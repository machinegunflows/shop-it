package de.samuelhuebner.shopit.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    // database version
    private static final int DATABASE_VERSION = 1;

    // database name
    private static final String DATABASE_NAME = "shopIt.db";

    // table name constants
    private static final String SHOPPING_LIST_TABLE_NAME = "shoppingList";
    private static final String LIST_POSITION_TABLE_NAME = "listPosition";
    private static final String SHOPPING_ITEM_TABLE_NAME = "shoppingItem";

    // field constants shoppingList
    private static final String SHOPPING_LIST_UUID_FIELD = "uuid";
    private static final String SHOPPING_LIST_NAME_FIELD = "name";

    // field constants shopping item
    private static final String SHOPPING_ITEM_ID_FIELD = "id";
    private static final String SHOPPING_ITEM_NAME_FIELD = "itemName";
    private static final String SHOPPING_ITEM_URL_FIELD = "itemUrl";
    private static final String SHOPPING_ITEM_NOTES_FIELD = "notes";
    private static final String SHOPPING_ITEM_CATEGORY_FIELD = "category";
    private static final String SHOPPING_ITEM_LIST_POSITION_ID_FIELD = "listPositionId";

    // field constants list position
    private static final String LIST_POSITION_ID_FIELD = "id";
    private static final String LIST_POSITION_SHOPPING_LIST_ID_FIELD = "shoppingListId";

    public DatabaseHelper(@NonNull Context context, @Nullable SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // First we will have to create the shopping list table
        db.execSQL("CREATE TABLE IF NOT EXISTS " + SHOPPING_LIST_TABLE_NAME + " (" +
                SHOPPING_LIST_UUID_FIELD + " VARCHAR NOT NULL UNIQUE, " +
                SHOPPING_LIST_NAME_FIELD + " VARCHAR(30) NOT NULL, " +
                "PRIMARY KEY("+ SHOPPING_LIST_UUID_FIELD +"))");

        // Secondly we have to create the list position table and set a foreign key to the shopping list table
        db.execSQL("CREATE TABLE IF NOT EXISTS " + LIST_POSITION_TABLE_NAME + " (" +
                LIST_POSITION_ID_FIELD + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE, " +
                LIST_POSITION_SHOPPING_LIST_ID_FIELD + " VARCHAR NOT NULL REFERENCES " + SHOPPING_LIST_TABLE_NAME + "(uuid) ON DELETE CASCADE" +
                ")");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + SHOPPING_ITEM_TABLE_NAME + " (" +
                SHOPPING_ITEM_ID_FIELD + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE," +
                SHOPPING_ITEM_NAME_FIELD + " VARCHAR(30) NOT NULL, " +
                SHOPPING_ITEM_URL_FIELD + " VARCHAR, " +
                SHOPPING_ITEM_NOTES_FIELD + " VARCHAR, " +
                SHOPPING_ITEM_CATEGORY_FIELD + " VARCHAR(15), " +
                SHOPPING_ITEM_LIST_POSITION_ID_FIELD + " INTEGER NOT NULL REFERENCES "+ LIST_POSITION_TABLE_NAME + "(ID) ON DELETE CASCADE" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SHOPPING_ITEM_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + LIST_POSITION_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SHOPPING_LIST_TABLE_NAME);

        onCreate(db);
    }

    /**
     * Creates a new shopping list
     *
     * @param newList   The new list that has to be inserted into the database
     */
    public void createShoppingList(ShoppingList newList) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues val = new ContentValues();
        val.put(SHOPPING_LIST_UUID_FIELD, newList.getUuid());
        val.put(SHOPPING_LIST_NAME_FIELD, newList.getName());

        db.insert(SHOPPING_LIST_TABLE_NAME, null, val);
    }

    /**
     * Loads the list of lists from our database
     *
     * @return      The list of all shopping lists
     */
    public ArrayList<ShoppingList> loadLists() {
        SQLiteDatabase db = getReadableDatabase();

        Cursor dbCursor = db.query(SHOPPING_LIST_TABLE_NAME,
                new String[] {SHOPPING_LIST_UUID_FIELD, SHOPPING_LIST_NAME_FIELD},
                null,
                null,
                null,
                null,
                null
        );

        ArrayList<ShoppingList> allLists = new ArrayList<>();

        if (dbCursor.moveToFirst()) {
            while (!dbCursor.isAfterLast()) {
                String uuid = dbCursor.getString(dbCursor.getColumnIndex(SHOPPING_LIST_UUID_FIELD));
                String name = dbCursor.getString(dbCursor.getColumnIndex(SHOPPING_LIST_NAME_FIELD));

                allLists.add(new ShoppingList(name, uuid));

                dbCursor.moveToNext();
            }
        }

        dbCursor.close();
        return allLists;
    }

    public ArrayList<ListPosition> getPositions(String listUuid) {
        // TODO: Implement this
        return null;
    }
}
