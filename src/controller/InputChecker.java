package controller;

import java.util.ArrayList;

public class InputChecker implements PossibleAction
{
    private static ArrayList<String> possible_input;
    
    public InputChecker() {
        this.setPossible_input(new ArrayList<String>());
    }
    
    @Override
    public ArrayList<String> getPossible_input() {
        return InputChecker.possible_input;
    }
    
    @Override
    public void setPossible_input(final String[] possible_input) {
        final ArrayList<String> tmp = new ArrayList<String>();
        for (int i = 0; i < possible_input.length; ++i) {
            tmp.add(possible_input[i]);
        }
        this.setPossible_input(tmp);
    }
    
    @Override
    public void setPossible_input(final ArrayList<String> possible_input) {
        InputChecker.possible_input = possible_input;
        for (int i = 0; i < MainController.POSSIBLE_INPUT.length; ++i) {
            InputChecker.possible_input.add(MainController.POSSIBLE_INPUT[i]);
        }
    }
    
    public boolean isSaisieValid(final String input) {
        return InputChecker.possible_input.contains(input);
    }
}

