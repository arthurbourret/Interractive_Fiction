package entity;

public class Stat
{
    public static final int MAX_ATTDEF = 10;
    public static final int MAX_SPEED = 50;
    private int hp;
    private int mp;
    private int att;
    private int def;
    private int defUp;
    private int speed;
    
    public Stat(final int hp, final int att, final int def, final int speed) {
        this(hp, 0, att, def, speed);
    }
    
    public Stat(final int hp, final int mp, int att, int def, int speed) {
        this.hp = hp;
        this.mp = mp;
        if (att > 10) {
            att = 10;
        }
        this.att = att;
        if (def > 10) {
            def = 10;
        }
        this.def = def;
        this.defUp = this.def;
        if (speed > 50) {
            speed = 50;
        }
        this.speed = speed;
    }
    
    @Override
    public String toString() {
        return "[att=" + this.att + ", def=" + this.def + ", speed=" + this.speed + "]";
    }
    
    public int getHp() {
        return this.hp;
    }
    
    public int getMp() {
        return this.mp;
    }
    
    public int getAtt() {
        return this.att;
    }
    
    public int getDef() {
        return this.def;
    }
    
    public int getSpeed() {
        return this.speed;
    }
    
    public int getDefUp() {
        return this.defUp;
    }
    
    public void setDefUp(final int defUp) {
        this.defUp = defUp;
    }
}