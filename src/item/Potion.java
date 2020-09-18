package item;

public class Potion extends Item
{
    private int heal;
    
    public Potion(final int heal) {
        super("Healt potion", "a health potion which heals " + heal + " hp");
        this.heal = heal;
    }
    
    @Override
    public String toString() {
        return "Potion [" + this.getName() + ", " + this.getDescription() + "]";
    }
    
    public int getHeal() {
        return this.heal;
    }
}