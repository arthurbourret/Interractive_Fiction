package event;

import field.Room;

public class Shortcut extends Event
{
    public static final String[] COMMAND;
    private static final String[] FULL_COMMAND;
    private Room room;
    private String dirId;
    
    static {
        COMMAND = new String[] { "short" };
        FULL_COMMAND = new String[] { "Take the shortcut" };
    }
    
    public Shortcut() {
    }
    
    public Shortcut(final String description, final Room direction) {
        super(description, Shortcut.COMMAND, Shortcut.FULL_COMMAND);
        this.room = direction;
    }
    
    public Shortcut(final String description, final String roomId) {
        super(description, Shortcut.COMMAND, Shortcut.FULL_COMMAND);
        this.dirId = roomId;
    }
    
    public void setRoom(final Room room) {
        this.room = room;
    }
    
    public Room getRoom() {
        return this.room;
    }
    
    public String getDirId() {
        return this.dirId;
    }
}