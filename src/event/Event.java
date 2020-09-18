package event;

import java.util.ArrayList;

public abstract class Event
{
    public static final String[] EVENT_COMMANDE;
    private String descriptionEvenement;
    private ArrayList<String> command;
    private ArrayList<String> fullCommand;
    
    static {
        EVENT_COMMANDE = new String[] { "open", "disarm", "ignore", "escape", "fight" };
    }
    
    public Event() {
    }
    
    public Event(final String description, final String[] command, final String[] fullCommand) {
        this.descriptionEvenement = description;
        this.command = new ArrayList<String>();
        this.fullCommand = new ArrayList<String>();
        for (int i = 0; i < command.length; ++i) {
            this.command.add(command[i]);
            this.fullCommand.add(String.valueOf(fullCommand[i]) + " --> " + command[i]);
        }
    }
    
    public String getDesription() {
        return this.descriptionEvenement;
    }
    
    public ArrayList<String> getCommand() {
        return this.command;
    }
    
    public ArrayList<String> getFullCommand() {
        return this.fullCommand;
    }
}

