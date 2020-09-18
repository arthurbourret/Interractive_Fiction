package event;

public class Trap extends Event
{
    protected static final String[] COMMAND;
    private static final String[] FULL_COMMAND;
    private int damage;
    private double disarming_chance;
    private double activating_chance;
    private boolean active;
    
    static {
        COMMAND = new String[] { "disarm", "ignore" };
        FULL_COMMAND = new String[] { "Disarm the trap", "Ignore the trap" };
    }
    
    public Trap() {
    }
    
    public Trap(final String description, final int damage, final double disarm, final double activate) {
        super(description, Trap.COMMAND, Trap.FULL_COMMAND);
        this.damage = damage;
        this.disarming_chance = disarm;
        this.activating_chance = activate;
        this.setActive(true);
    }
    
    public int getDamage() {
        return this.damage;
    }
    
    public double getDisarming_chance() {
        return this.disarming_chance;
    }
    
    public double getActivating_chance() {
        return this.activating_chance;
    }
    
    public boolean isActive() {
        return this.active;
    }
    
    public void setActive(final boolean active) {
        this.active = active;
    }
}