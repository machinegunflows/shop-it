package de.samuelhuebner.shopit.database;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;

public class Database {
    private final DatabaseHelper dbHelper;

    private static boolean hasLoaded = false;
    private static final HashMap<String, ShoppingList> map = new HashMap<>();;
    private static final ArrayList<String> names = new ArrayList<>();
    private static final ArrayList<String> uuids = new ArrayList<>();

    private static final ArrayList<HistoryEvent> historyEvents = new ArrayList<>();

    public Database(Context context) {
        dbHelper = new DatabaseHelper(context, null);

        if (!hasLoaded) {
            ArrayList<ShoppingList> lists = dbHelper.loadLists();

            for (ShoppingList list : lists) {
                map.put(list.getUuid(), list);
                names.add(list.getName());
                uuids.add(list.getUuid());
            }


            ArrayList<HistoryEvent> events = dbHelper.loadEvents();
            historyEvents.addAll(events);
            hasLoaded = true;
        }
    }

    public static boolean contains(String uuid) {
        return map.containsKey(uuid);
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
        uuids.add(newList.getUuid());

        this.dbHelper.createShoppingList(newList);
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

    public void deleteShoppingList(@NonNull String uuid) {
        int toRemovePos = -1;
        for (int i = 0; i < uuids.size() ; i++) {
            if (uuids.get(i).equals(uuid)) toRemovePos = i;
        }

        uuids.remove(toRemovePos);
        names.remove(toRemovePos);
        map.remove(uuid);

        this.dbHelper.deleteShoppingList(uuid);
    }

    public void addListPosition(ListPosition newListPos) {
        this.dbHelper.createListPosition(newListPos);
    }

    public void updatePosStatus(long id, boolean checked) {
        this.dbHelper.updatePosStatus(id, checked);
    }

    public ArrayList<HistoryEvent> getHistoryEvents() {
        return historyEvents;
    }

    public void addHistoryEvent(HistoryEvent event) {
        historyEvents.add(0, event);
        this.dbHelper.storeHistoryEvent(event);
    }

    public void clearHistory() {
        historyEvents.clear();
        this.dbHelper.deleteHistory();
    }

    public void deleteListPosition(ListPosition position) {
        ShoppingList list = map.get(position.getListUuid());
        list.removeListPosition(position);
        addHistoryEvent(new HistoryEvent("Deleted '" + position.getName() +"' from '" + list.getName() + "'", EventType.DELETED_POS));
        this.dbHelper.deleteListPosition(position);
    }

    public void addListPosition(ListPosition deletedListPos, long id) {
        addHistoryEvent(new HistoryEvent("Restored '" + deletedListPos.getName() + "'", EventType.RESTORED_POS));

        this.dbHelper.addBackListPosition(deletedListPos, id);
    }

    public void updateList(ShoppingList list) {
        this.dbHelper.updateShoppingList(list);
    }
}
