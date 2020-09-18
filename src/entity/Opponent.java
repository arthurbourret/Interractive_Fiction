package entity;

import event.Battle;

public interface Opponent
{
    boolean attack(final Opponent p0);
    
    boolean castSpecialAtt(final Opponent p0);
    
    void defend();
    
    void resetDef();
    
    boolean heal();
    
    boolean flee(final Battle p0);
    
    boolean canEscape();
    
    void hurted(final int p0);
    
    int getHp();
    
    Stat getStat();
    
    String getName();
}