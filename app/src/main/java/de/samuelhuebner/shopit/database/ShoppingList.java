package de.samuelhuebner.shopit.database;

import java.util.ArrayList;

public class ShoppingList {
    private String name;
    private ArrayList<ListPosition> positions;
    private boolean isComplete;

    public ShoppingList(String name) {
        this.name = name;
        this.positions = new ArrayList<>();
        this.isComplete = false;
    }

    public void addPosition(ListPosition newPos) {
        positions.add(newPos);
        isComplete = false;
    }

    public ListPosition[] getPositions() {
        return (ListPosition[]) positions.toArray();
    }
}
