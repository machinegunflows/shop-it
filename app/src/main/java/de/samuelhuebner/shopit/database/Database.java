package de.samuelhuebner.shopit.database;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Database {
    private final DatabaseHelper dbHelper;

    private static final HashMap<String, ShoppingList> map = new HashMap<>();;
    private static ArrayList<String> names = new ArrayList<>();

    static {
        ShoppingList tmp = new ShoppingList("Test1");

        map.put(tmp.getUuid(), tmp);
        names.add(tmp.getName());
    }

    public Database(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    /**
     * Creates a new shopping list and puts it into the hash map
     * @param name      The name of the new shopping list
     * @return          The new ShoppingList object
     */
    public ShoppingList createShoppingList(String name) {
        ShoppingList newList = new ShoppingList(name);
        map.put(newList.getUuid(), newList);

        names.add(newList.getName());
        return newList;
    }

    /**
     * Returns a ShoppingLists array of the current shopping lists
     * @return
     */
    public ShoppingList[] getShoppingLists() {
        String[] keys = (String[]) map.keySet().toArray();

        ShoppingList[] shoppingLists = new ShoppingList[keys.length];

        for (int i = 0; i < shoppingLists.length; i++) {
            shoppingLists[i] = map.get(keys[i]);
        }

        return shoppingLists;
    }

    public ArrayList<String> getShoppingListsNames() {
        for (String name : names) {

        }
        return names;
    }
}
