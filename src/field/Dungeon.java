package field;

import java.util.ArrayList;
import event.Chest;
import event.Shortcut;
import event.Trap;
import event.Battle;
import game.Console;
import java.io.InputStream;
import game.ExternalFile;
import view.FieldView;
import controller.PossibleAction;
import entity.Player;

public class Dungeon
{
    private Generation map;
    private Player player;
    private PossibleAction checker;
    private static FieldView view;
    private Room start;
    private Room end;
    private Room current;
    private Room previous;
    
    public Dungeon() {
        this(new ExternalFile().SAVE_BASE);
    }
    
    public Dungeon(final InputStream filename) {
        this.end = new Room("end");
        this.map = new Generation(filename, this.end);
        this.start = this.map.getGamePositionFromXml("position");
        Console.printText(this.map.getLoader().loadFileText("Text_start.xml"));
        this.player = (Player)Battle.getPlayer();
    }
    
    public Generation getMap() {
        return this.map;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public PossibleAction getChecker() {
        return this.checker;
    }
    
    public void setChecker(final PossibleAction checker) {
        this.checker = checker;
    }
    
    public static FieldView getView() {
        return Dungeon.view;
    }
    
    public static void setView(final FieldView view) {
        Dungeon.view = view;
    }
    
    public static void resetView() {
        Dungeon.view.getGeneration().clear();
    }
    
    public Room getStart() {
        return this.start;
    }
    
    public Room getEnd() {
        return this.end;
    }
    
    public Room getCurrent() {
        return this.current;
    }
    
    public void setCurrent(final Room current) {
        this.previous = this.current;
        this.current = current;
        final String id = this.current.getId();
        if (id.equals(this.end.getId())) {
            this.win();
        }
        if (!this.current.isVisited()) {
            Dungeon.view.addRoom(this.current);
        }
        Dungeon.view.dispView(id);
    }
    
    public Room getPrevious() {
        return this.previous;
    }
    
    public void setPreviousFromXml() {
        this.previous = this.map.getGamePositionFromXml("previous");
    }
    
    public void doEvent() {
        Console.printText(" |" + this.current.getDescription());
        if (this.current.getEvent(new Battle()) != null || this.current.getEvent(new Trap()) != null) {
            if (this.current.getEvent(new Battle()) != null) {
                Console.printText(this.current.getEvent(new Battle()).getDesription());
            }
            if (this.current.getEvent(new Trap()) != null) {
                Console.printText(this.current.getEvent(new Trap()).getDesription());
            }
            Console.printText("What will you do!| ");
            this.checker.setPossible_input(this.current.getPossibleEvent());
        }
        else {
            this.doMoveRoom();
        }
    }
    
    public void doMoveRoom() {
        if (this.player.getHp() > 0) {
            Console.printText("You look around the room and ...");
            final Shortcut shortcut = (Shortcut)this.current.getEvent(new Shortcut());
            final Chest chest = (Chest)this.current.getEvent(new Chest());
            if (this.current.getDirection().length > 0 || shortcut != null || chest != null) {
                if (shortcut != null) {
                    Console.printText(shortcut.getDesription());
                }
                if (chest != null) {
                    Console.printText(chest.getDesription());
                }
                for (int i = 0; i < this.current.getDirection().length; ++i) {
                    if (this.current.getDirection(i) != null) {
                        Console.printText("There is a room on the " + Room.DIRECTION[i]);
                    }
                }
                Console.printText(" ");
                final ArrayList<String> possible_dir = this.current.getPossibleDirection();
                if (possible_dir.isEmpty()) {
                    Console.printText("There is nowhere to go");
                }
                this.checker.setPossible_input(possible_dir);
            }
            else {
                Console.printText("There is nothing here");
            }
        }
        else {
            this.death();
        }
    }
    
    private void win() {
        final ExternalFile loader = new ExternalFile();
        Console.printText(" ");
        Console.printText(loader.loadFileText("Text_win.xml"));
    }
    
    private void death() {
        final ExternalFile loader = new ExternalFile();
        Console.printText(" ");
        Console.printText(loader.loadFileText("Text_loose.xml"));
    }
}