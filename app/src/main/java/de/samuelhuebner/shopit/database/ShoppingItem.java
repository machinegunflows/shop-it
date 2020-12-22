package de.samuelhuebner.shopit.database;

public class ShoppingItem {
    private String category;
    private String itemName;
    private String itemUrl;
    private String notes;
    private Double price;

    /**
     * Constructor for a ListItem with only the item name
     * All other values will be set to an empty string or in the price case to -1.0
     *
     * @param itemName      The name of the ListItem
     */
    public ShoppingItem(String itemName) {
        this.itemName = itemName;
        this.itemUrl = "";
        this.notes = "";
        this.price = -1.0;
    }

    public ShoppingItem(String itemName, Category category) {
        this.itemName = itemName;
        this.itemUrl = "";
        this.notes = "";
        this.price = -1.0;

        if (category != null) this.category = category.toString();
    }

    public ShoppingItem() {
        this.itemName = "";
        this.itemUrl = "";
        this.notes = "";
        this.price = -1.0;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setItemUrl(String itemUrl) {
        this.itemUrl = itemUrl;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getPrice() {
        return price;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemUrl() {
        return itemUrl;
    }

    public String getCategory() {
        return category;
    }

    public String getNotes() {
        return notes;
    }

    @Override
    public String toString() {
        return "ShoppingItem{" +
                "category='" + category + '\'' +
                ", itemName='" + itemName + '\'' +
                ", itemUrl='" + itemUrl + '\'' +
                ", notes='" + notes + '\'' +
                ", price=" + price +
                '}';
    }
}
