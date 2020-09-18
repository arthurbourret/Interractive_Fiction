package item;

public abstract class Item
{
    private String name;
    private String description;
    
    public Item(final String name) {
        this(name, "");
    }
    
    public Item(final String name, final String description) {
        this.name = name;
        this.description = description;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getDescription() {
        return this.description;
    }
}