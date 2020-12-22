package de.samuelhuebner.shopit.database;

import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

public class ShoppingList {
    private String uuid;
    private String name;
    private ArrayList<ListPosition> positions;
    private boolean isComplete;

    public ShoppingList(String name) {
        this.name = name;
        this.positions = new ArrayList<>();
        this.isComplete = false;

        this.uuid = UUID.randomUUID().toString().replace("-", "");
    }

    public void addPosition(ListPosition newPos) {
        positions.add(newPos);
        isComplete = false;
    }

    public ArrayList<ListPosition> getPositions() {
        return positions;
    }

    public String getUuid() { return uuid; }

    public String getName() { return name; }

    /**
     * Gets the count of completed positions
     *
     * @return      The count of completed positions;
     */
    public int getCompleted() {
        int count = 0;
        for (ListPosition pos : positions) {
            if (!pos.isCompleted()) continue;

            count++;
        }
        return count;
    }

    public int getItemCount() {
        return positions.size();
    }

    public boolean isComplete() {
        return isComplete;
    }
}
