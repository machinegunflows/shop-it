package de.samuelhuebner.shopit.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.se.omapi.SEService;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NavUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseHelper extends SQLiteOpenHelper {
    // database version
    private static final int DATABASE_VERSION = 1;

    // database name
    private static final String DATABASE_NAME = "shopIt.db";

    // table name constants
    private static final String SHOPPING_LIST_TABLE_NAME = "shoppingList";
    private static final String LIST_POSITION_TABLE_NAME = "listPosition";
    private static final String SHOPPING_ITEM_TABLE_NAME = "shoppingItem";
    private static final String HISTORY_EVENT_TABLE_NAME = "historyEvent";

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
    private static final String LIST_POSITION_ID_FIELD = "listPositionId";
    private static final String LIST_POSITION_STATUS_FIELD = "isCompleted";
    private static final String LIST_POSITION_SHOPPING_LIST_ID_FIELD = "shoppingListId";

    // field constants history event
    private static final String HISTORY_EVENT_ID_FIELD = "historyEventId";
    private static final String HISTORY_EVENT_TEXT_FIELD = "historyEventText";
    private static final String HISTORY_EVENT_TYPE_FIELD = "historyEventType";

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
                LIST_POSITION_STATUS_FIELD + " INTEGER DEFAULT 0 NOT NULL, " +
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

        db.execSQL("CREATE TABLE IF NOT EXISTS " + HISTORY_EVENT_TABLE_NAME + " (" +
                HISTORY_EVENT_ID_FIELD + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE, " +
                HISTORY_EVENT_TYPE_FIELD + " VARCHAR(15) NOT NULL, " +
                HISTORY_EVENT_TEXT_FIELD + " VARCHAR NOT NULL" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SHOPPING_ITEM_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + LIST_POSITION_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SHOPPING_LIST_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + HISTORY_EVENT_TABLE_NAME);

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
        db.close();
    }

    /**
     * Loads the list of lists from our database
     *
     * @return      The list of all shopping lists
     */
    public ArrayList<ShoppingList> loadLists() {
        // Raw query string that is used to join the three tables
        String queryString = "SELECT " +
                SHOPPING_LIST_TABLE_NAME + "." + SHOPPING_LIST_UUID_FIELD + ", " +
                SHOPPING_LIST_TABLE_NAME + "." + SHOPPING_LIST_NAME_FIELD + ", " +
                LIST_POSITION_TABLE_NAME + "." + LIST_POSITION_ID_FIELD + ", " +
                LIST_POSITION_TABLE_NAME + "." + LIST_POSITION_STATUS_FIELD + ", " +
                SHOPPING_ITEM_TABLE_NAME + "." + SHOPPING_ITEM_ID_FIELD + ", " +
                SHOPPING_ITEM_TABLE_NAME + "." + SHOPPING_ITEM_NAME_FIELD + ", " +
                SHOPPING_ITEM_TABLE_NAME + "." + SHOPPING_ITEM_CATEGORY_FIELD + ", " +
                SHOPPING_ITEM_TABLE_NAME + "." + SHOPPING_ITEM_NOTES_FIELD + ", " +
                SHOPPING_ITEM_TABLE_NAME + "." + SHOPPING_ITEM_URL_FIELD +
                " FROM "
                + SHOPPING_LIST_TABLE_NAME + " LEFT JOIN " +  LIST_POSITION_TABLE_NAME + " " +
                "ON " + SHOPPING_LIST_TABLE_NAME + "." + SHOPPING_LIST_UUID_FIELD + " = " + LIST_POSITION_TABLE_NAME + "." + LIST_POSITION_SHOPPING_LIST_ID_FIELD
                + " LEFT JOIN " + SHOPPING_ITEM_TABLE_NAME + " " +
                "ON " + LIST_POSITION_TABLE_NAME + "." + LIST_POSITION_ID_FIELD + " = " + SHOPPING_ITEM_TABLE_NAME + "." + SHOPPING_ITEM_LIST_POSITION_ID_FIELD;


        SQLiteDatabase db = getReadableDatabase();
        Cursor dbCursor = db.rawQuery(queryString, null);

        ArrayList<ShoppingList> allLists = new ArrayList<>();
        HashMap<String, ShoppingList> map = new HashMap<>();

        if (dbCursor.moveToFirst()) {
            while (!dbCursor.isAfterLast()) {
                String uuid = dbCursor.getString(dbCursor.getColumnIndex(SHOPPING_LIST_UUID_FIELD));
                int listPosId = dbCursor.getInt(dbCursor.getColumnIndex(LIST_POSITION_ID_FIELD));
                int shoppingItemId = dbCursor.getInt(dbCursor.getColumnIndex(SHOPPING_ITEM_ID_FIELD));
                Boolean isCompleted = dbCursor.getLong(dbCursor.getColumnIndex(LIST_POSITION_STATUS_FIELD)) == 1;
                String shoppingItemName = dbCursor.getString(dbCursor.getColumnIndex(SHOPPING_ITEM_NAME_FIELD));
                String shoppingItemCat = dbCursor.getString(dbCursor.getColumnIndex(SHOPPING_ITEM_CATEGORY_FIELD));
                String shoppingItemNotes = dbCursor.getString(dbCursor.getColumnIndex(SHOPPING_ITEM_NOTES_FIELD));
                String shoppingItemUrl = dbCursor.getString(dbCursor.getColumnIndex(SHOPPING_ITEM_URL_FIELD));

                ShoppingList list;
                if (map.containsKey(uuid)) {
                    list = map.get(uuid);
                } else {
                    String name = dbCursor.getString(dbCursor.getColumnIndex(SHOPPING_LIST_NAME_FIELD));
                    list = new ShoppingList(name, uuid);
                    map.put(uuid, list);
                }

                if (!(listPosId == 0)) {
                    Category cat = (shoppingItemCat == null || shoppingItemCat.toLowerCase().equals("no-category"))
                            ? null : Category.valueOf(shoppingItemCat);

                    ShoppingItem newShoppingItem = new ShoppingItem(shoppingItemName, cat);

                    newShoppingItem.setId(shoppingItemId);
                    newShoppingItem.setNotes(shoppingItemNotes);
                    newShoppingItem.setItemUrl(shoppingItemUrl);

                    ListPosition listPosition = new ListPosition(newShoppingItem, 1, uuid);
                    listPosition.setId(listPosId);
                    listPosition.setCompleted(isCompleted);
                    list.getPositions().add(listPosition);
                }
                dbCursor.moveToNext();
            }
        }

        dbCursor.close();
        db.close();

        String[] keys = map.keySet().toArray(new String[0]);
        for (String key : keys) {
            ShoppingList tmp = map.get(key);
            allLists.add(tmp);
        }
        return allLists;
    }

    public ArrayList<ListPosition> getPositions(String listUuid) {
        // TODO: Implement this
        return null;
    }

    public void createPosition(ListPosition position) {
        SQLiteDatabase db = getReadableDatabase();

        ContentValues listPosVals = new ContentValues();
//        db.insert()
    }

    /**
     * deletes a shopping list given by its uuid
     *
     * @param uuid  The uuid of the shopping list that has to be deleted
     */
    public void deleteShoppingList(String uuid) {
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL("DELETE FROM " + SHOPPING_LIST_TABLE_NAME + " WHERE " + SHOPPING_LIST_UUID_FIELD + "='" + uuid + "';");
        db.close();
    }

    public void createListPosition(ListPosition newListPos) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues val = new ContentValues();
        val.put(LIST_POSITION_SHOPPING_LIST_ID_FIELD, newListPos.getListUuid());

        long rowId = db.insert(LIST_POSITION_TABLE_NAME, null, val);

        newListPos.setId(rowId);

        val = new ContentValues();
        val.put(SHOPPING_ITEM_LIST_POSITION_ID_FIELD, rowId);
        val.put(SHOPPING_ITEM_NAME_FIELD, newListPos.getShoppingItem().getItemName());
        val.put(SHOPPING_ITEM_CATEGORY_FIELD, newListPos.getShoppingItem().getCategory());
        val.put(SHOPPING_ITEM_NOTES_FIELD, newListPos.getShoppingItem().getNotes());
        val.put(SHOPPING_ITEM_URL_FIELD, newListPos.getShoppingItem().getItemUrl());

        rowId = db.insert(SHOPPING_ITEM_TABLE_NAME, null, val);
        newListPos.getShoppingItem().setId(rowId);

        db.close();
    }

    public void updatePosStatus(long id, boolean checked) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LIST_POSITION_STATUS_FIELD, (checked) ? 1 : 0);

        db.update(LIST_POSITION_TABLE_NAME, values, LIST_POSITION_ID_FIELD + "=?", new String[] {String.valueOf(id)});
        db.close();
    }

    public void storeHistoryEvent(HistoryEvent event) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(HISTORY_EVENT_TYPE_FIELD, event.getType().toString());
        values.put(HISTORY_EVENT_TEXT_FIELD, event.getEventText());

        db.insert(HISTORY_EVENT_TABLE_NAME, null, values);
        db.close();
    }

    public ArrayList<HistoryEvent> loadEvents() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<HistoryEvent> events = new ArrayList<>();

        Cursor dbCursor = db.query(
                HISTORY_EVENT_TABLE_NAME,
                new String[] {HISTORY_EVENT_ID_FIELD, HISTORY_EVENT_TYPE_FIELD, HISTORY_EVENT_TEXT_FIELD},
                null,
                null,
                null,
                null,
                HISTORY_EVENT_ID_FIELD + " DESC"
        );

        if (dbCursor.moveToFirst()) {
            while (!dbCursor.isAfterLast()) {
                String text = dbCursor.getString(dbCursor.getColumnIndex(HISTORY_EVENT_TEXT_FIELD));
                String type = dbCursor.getString(dbCursor.getColumnIndex(HISTORY_EVENT_TYPE_FIELD));

                events.add(new HistoryEvent(text, EventType.valueOf(type)));

                dbCursor.moveToNext();
            }
        }
        dbCursor.close();
        db.close();
        return events;
    }

    public void deleteHistory() {
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("DELETE FROM " + HISTORY_EVENT_TABLE_NAME);
        db.close();
    }
}
