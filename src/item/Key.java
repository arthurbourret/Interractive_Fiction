package item;

public class Key extends Item
{
    private int level;
    
    public Key(final int level) {
        super("level " + level + " key", "a key which can open chest with lock of security level " + level);
        this.level = level;
    }
    
    @Override
    public String toString() {
        return "Key [" + this.getName() + ", " + this.getDescription() + "]";
    }
    
    public int getLevel() {
        return this.level;
    }
}