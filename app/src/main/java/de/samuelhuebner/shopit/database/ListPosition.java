package de.samuelhuebner.shopit.database;

public class ListPosition {
    private String name;
    private ShoppingItem shoppingItem;
    private boolean isCompleted;
    private int count;

    public ListPosition(ShoppingItem shoppingItem, Integer count) {
        this.name = shoppingItem.getItemName();
        this.shoppingItem = shoppingItem;

        // if count is null we assume that the count is 1
        if (null == count) this.count = 1;
        else this.count = count;

        this.isCompleted = false;
    }

    public int getCount() {
        return count;
    }

    public String getName() {
        return name;
    }

    public ShoppingItem getShoppingItem() {
        return shoppingItem;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
