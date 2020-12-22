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
        ShoppingList tmp = new ShoppingList("Extremely stable list");

        tmp.addPosition(new ListPosition(new ShoppingItem("Orange", Category.GROCERIES), 23));
        tmp.addPosition(new ListPosition(new ShoppingItem("Brot", Category.GROCERIES), 1));

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
     * Returns the shopping list associated to the given id
     *
     * @param uuid  The uuid of the shopping list
     * @return      The shopping list
     */
    public ShoppingList getShoppingList(String uuid) {
        return map.get(uuid);
    }

    /**
     * Returns a ShoppingLists array of the current shopping lists
     * @return
     */
    public ShoppingList[] getShoppingLists() {
        String[] keys = map.keySet().toArray(new String[0]);

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
