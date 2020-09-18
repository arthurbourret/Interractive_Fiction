package field;

import game.Console;
import event.Chest;
import event.Shortcut;
import event.Event;
import java.util.ArrayList;

public class Room
{
    public static final String[] DIRECTION;
    private String id;
    private boolean visited;
    private String description;
    private Room[] direction;
    private String[] generate_dir;
    private ArrayList<Event> events;
    
    static {
        DIRECTION = new String[] { "north", "east", "south", "west" };
    }
    
    public Room(final String id) {
        this(id, "", null, new ArrayList<Event>(), false);
    }
    
    public Room(final String id, String description, final String[] generate_dir, final ArrayList<Event> events, final boolean visited) {
        this.id = id;
        if (description == null || description.equals("")) {
            description = "You enter a room";
        }
        this.description = description;
        this.generate_dir = generate_dir;
        this.direction = new Room[4];
        this.setEvents(events);
        this.visited = visited;
    }
    
    @Override
    public String toString() {
        return "Room : " + this.id + "\t" + this.description + ";";
    }
    
    public String getId() {
        return this.id;
    }
    
    public boolean isVisited() {
        return this.visited;
    }
    
    public void setVisitedOn() {
        this.visited = true;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(final String description) {
        this.description = description;
    }
    
    public Room[] getDirection() {
        return this.direction;
    }
    
    public Room getDirection(final int dir) {
        return this.direction[dir];
    }
    
    public void setDirection(final Room[] directions) {
        this.direction = directions;
    }
    
    public void setDirection(int dir, final Room room) {
        if (room != null && this.getDirection(dir) != room) {
            this.direction[dir] = room;
            if (dir > 1) {
                dir -= 2;
            }
            else {
                dir += 2;
            }
            room.setDirection(dir, this);
        }
    }
    
    public void setDirection(final Room north, final Room east, final Room south, final Room west) {
        this.setDirection(0, north);
        this.setDirection(1, east);
        this.setDirection(2, south);
        this.setDirection(3, west);
    }
    
    public ArrayList<Event> getEvents() {
        return this.events;
    }
    
    public Event getEvent(final Object type) {
        for (final Event current : this.events) {
            if (current.getClass().isInstance(type)) {
                return current;
            }
        }
        return null;
    }
    
    public void setEvents(final ArrayList<Event> events) {
        this.events = events;
    }
    
    public ArrayList<String> getPossibleEvent() {
        final ArrayList<String> possible_action = new ArrayList<String>();
        for (final Event currentEvent : this.events) {
            if (!(currentEvent instanceof Shortcut) && !(currentEvent instanceof Chest)) {
                this.addEvent(currentEvent, possible_action);
            }
        }
        return possible_action;
    }
    
    public ArrayList<String> getPossibleDirection() {
        final ArrayList<String> possible_action = new ArrayList<String>();
        final Event shortcut = this.getEvent(new Shortcut());
        if (shortcut != null) {
            this.addEvent(shortcut, possible_action);
        }
        final Event chest = this.getEvent(new Chest());
        if (chest != null) {
            this.addEvent(chest, possible_action);
        }
        for (int i = 0; i < this.getDirection().length; ++i) {
            if (this.getDirection(i) != null) {
                possible_action.add(Room.DIRECTION[i].toLowerCase());
                Console.printText("go to the " + Room.DIRECTION[i] + " --> " + Room.DIRECTION[i], "command");
            }
        }
        return possible_action;
    }
    
    private void addEvent(final Event event, final ArrayList<String> receiver) {
        for (int i = 0; i < event.getCommand().size(); ++i) {
            receiver.add(event.getCommand().get(i).toLowerCase());
            Console.printText(event.getFullCommand().get(i), "command");
        }
    }
    
    public String[] getGenerate_dir() {
        return this.generate_dir;
    }
}

