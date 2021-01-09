package de.samuelhuebner.shopit.database;

public class ListPosition {
    // the parent shopping lists uuid
    private final String listUuid;

    // the primary key from the database
    private long id;

    // the name of the list position
    private String name;

    // the associated shopping item
    private ShoppingItem shoppingItem;

    // status of the list position
    private boolean isCompleted;

    // property which holds the count
    private int count;

    /**
     * Constructor for creating a ListPostition
     *
     * @param shoppingItem      The corresponding shopping item
     * @param count             The count (defaults to 1)
     * @param listUuid          The uuid of the parent list
     *
     */
    public ListPosition(ShoppingItem shoppingItem, Integer count, String listUuid) {
        this.listUuid = listUuid;
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

    public String getCategory() {
        return (shoppingItem.getCategory() == null) ? "no-category" : shoppingItem.getCategory();
    }

    public String getListUuid() {
        return listUuid;
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

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
        this.shoppingItem.setItemName(name);
    }
}
