package entity;

import java.util.Iterator;
import item.Item;
import game.Console;
import item.Weapon;
import item.Potion;
import item.Key;
import java.util.ArrayList;

public class Player extends Entity
{
    private int mp;
    ArrayList<Key> keychain;
    ArrayList<Potion> potions;
    
    public Player(final String name, final String specialAtt, final Stat stat, final Weapon weapon) {
        super(name, specialAtt, stat, weapon);
        this.resetSpec_point();
        this.keychain = new ArrayList<Key>();
        this.potions = new ArrayList<Potion>();
    }
    
    public int getMp() {
        return this.mp;
    }
    
    public void resetSpec_point() {
        this.mp = this.getStat().getMp();
    }
    
    @Override
    public boolean heal() {
        if (this.hasPotions()) {
            final Potion potion = this.usePotion(this.getStat().getHp() - this.getHp());
            if (potion != null) {
                if (potion.getHeal() > this.getStat().getHp() - this.getHp()) {
                    Console.printText(String.valueOf(super.getName()) + " is totally healed!");
                }
                else {
                    Console.printText(String.valueOf(super.getName()) + " gaigned " + potion.getHeal() + " health points");
                }
                super.setHp(super.getHp() + potion.getHeal());
                this.potions.remove(potion);
                return true;
            }
        }
        Console.printText("try to heal with inexistant potion", "error");
        return false;
    }
    
    public Potion usePotion(final int amount) {
        int max = 0;
        int index = -1;
        for (int i = 0; i < this.potions.size(); ++i) {
            if (this.potions.get(i).getHeal() >= max) {
                max = this.potions.get(i).getHeal();
                index = i;
            }
        }
        for (int i = 0; i < this.potions.size(); ++i) {
            if (this.potions.get(i).getHeal() <= amount) {
                index = i;
            }
        }
        if (index > -1) {
            return this.potions.get(index);
        }
        return null;
    }
    
    @Override
    public boolean castSpecialAtt(final Opponent enemy) {
        if (this.mp > 0) {
            --this.mp;
            if (this.mp < 0) {
                Console.printText(String.valueOf(this.getName()) + " won't be able to do another special attack");
            }
            return super.castSpecialAtt(enemy);
        }
        Console.printText(String.valueOf(this.getName()) + " hadn't enought energy for a special attack");
        return false;
    }
    
    public ArrayList<Key> getKeychain() {
        return this.keychain;
    }
    
    public ArrayList<Potion> getPotions() {
        return this.potions;
    }
    
    public ArrayList<String> getInventory() {
        final ArrayList<String> array = new ArrayList<String>();
        for (int i = 0; i < this.keychain.size(); ++i) {
            array.add(" - " + this.keychain.get(i).getName());
        }
        for (int i = 0; i < this.potions.size(); ++i) {
            array.add(" - " + this.potions.get(i).getDescription());
        }
        if (super.getWeapon() != null) {
            array.add(" - " + super.getWeapon().getName() + " " + super.getWeapon().getDescription());
        }
        return array;
    }
    
    public void addItem(final Item item) {
        if (item instanceof Key && !this.hasSuperiorKey(((Key)item).getLevel())) {
            this.keychain.add((Key)item);
        }
        if (item instanceof Potion) {
            this.potions.add((Potion)item);
        }
    }
    
    public boolean canOpenChest(final int level) {
        return level == 0 || this.hasSuperiorKey(level);
    }
    
    private boolean hasSuperiorKey(final int level) {
        final Iterator<Key> it = this.keychain.iterator();
        while (it.hasNext()) {
            if (it.next().getLevel() >= level) {
                return true;
            }
        }
        return false;
    }
    
    public boolean hasPotions() {
        return !this.potions.isEmpty();
    }
}