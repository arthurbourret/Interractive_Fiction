package controller;

import java.util.ArrayList;

public interface PossibleAction
{
    ArrayList<String> getPossible_input();
    
    void setPossible_input(final ArrayList<String> p0);
    
    void setPossible_input(final String[] p0);
}