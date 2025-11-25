public class Item {
    private String itemId;
    private String name;
    private String category;
    private String type;
    private String location;
    private String contact;

    public Item(String itemId, String name, String category, String type, String location, String contact) {
        this.itemId = itemId;
        this.name = name;
        this.category = category;
        this.type = type;
        this.location = location;
        this.contact = contact;
    }

    public String getItemId() { return itemId; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getType() { return type; }
    public String getLocation() { return location; }
    public String getContact() { return contact; }
}
