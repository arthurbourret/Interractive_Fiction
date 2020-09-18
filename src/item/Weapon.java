package item;

public class Weapon extends Item
{
    private String specialAtt;
    private int attack;
    
    public Weapon(final String name, final String specialAtt, final int attack) {
        super(name, "a weapon wich can " + ((specialAtt == null || specialAtt.equals("")) ? "do nothing special" : specialAtt));
        this.attack = attack;
        this.specialAtt = specialAtt;
    }
    
    @Override
    public String toString() {
        return "Weapon : " + this.getName() + " [" + ((this.specialAtt != null) ? this.specialAtt : " ") + "]";
    }
    
    public int getAttack() {
        return this.attack;
    }
    
    public String getSpecialAtt() {
        return this.specialAtt;
    }
}