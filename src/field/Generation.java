package field;

import event.Shortcut;
import java.util.ArrayList;
import game.Console;
import event.Battle;
import game.ExternalFile;
import java.io.InputStream;
import game.DataLoader;

public class Generation
{
    private DataLoader loader;
    private Room[][] generation;
    private int column;
    private int line;
    private Room start;
    private Room end;
    
    public Generation(final InputStream filename, final Room end) {
        this.start = new Room("start");
        this.end = end;
        (this.loader = new ExternalFile()).setGame(filename);
        Battle.setPlayer(this.loader.loadPlayer());
        this.column = this.loader.getGenerationAttribute("column");
        this.line = this.loader.getGenerationAttribute("line");
        if (this.column > 0 && this.line > 0) {
            this.generation = new Room[this.column][this.line];
            for (int i = 0; i < this.column; ++i) {
                for (int j = 0; j < this.line; ++j) {
                    this.generation[i][j] = new Room(String.valueOf(String.valueOf(i)) + String.valueOf(j));
                }
            }
            final ArrayList<Room> all_rooms = this.loader.loadRooms();
            final int type = this.loader.getGenerationAttribute("type");
            if (type < 2) {
                this.loadPathFromXml(all_rooms);
            }
            if (type > 0) {
                this.loadGeneratedWithBase();
            }
            this.loadEventsFromXml(all_rooms);
        }
        else {
            Console.printText("can not load dungeon", "error");
        }
    }
    
    public int getColumn() {
        return this.column;
    }
    
    public int getLine() {
        return this.line;
    }
    
    public DataLoader getLoader() {
        return this.loader;
    }
    
    public Room getGamePositionFromXml(final String option) {
        Room position = null;
        final String id = this.getLoader().gameGameRoom(option);
        for (int i = 0; i < this.column; ++i) {
            for (int j = 0; j < this.line; ++j) {
                if (this.generation[i][j].getId().equals(id)) {
                    position = this.generation[i][j];
                }
            }
        }
        if (this.start.getId().equals(id)) {
            position = this.start;
        }
        if (this.end.getId().equals(id)) {
            position = this.end;
        }
        return position;
    }
    
    public void loadPathFromXml(final ArrayList<Room> all_rooms) {
        for (int i = 0; i < this.column; ++i) {
            for (int j = 0; j < this.line; ++j) {
                this.setDirFromBase(all_rooms, this.generation[i][j]);
            }
        }
        this.setDirFromBase(all_rooms, this.start);
        this.setDirFromBase(all_rooms, this.end);
    }
    
    private void setDirFromBase(final ArrayList<Room> all_rooms, final Room destination) {
        if (destination != null) {
            final Room base = this.getRoomWithId(all_rooms, destination.getId());
            if (base != null) {
                for (int z = 0; z < base.getGenerate_dir().length; ++z) {
                    base.setDirection(z, this.getRoomWithId(all_rooms, base.getGenerate_dir()[z]));
                }
                destination.setDirection(base.getDirection());
            }
        }
    }
    
    public void loadEventsFromXml(final ArrayList<Room> all_rooms) {
        for (int i = 0; i < this.column; ++i) {
            for (int j = 0; j < this.line; ++j) {
                this.initRoom(all_rooms, this.generation[i][j]);
            }
        }
        this.initRoom(all_rooms, this.start);
        this.initRoom(all_rooms, this.end);
    }
    
    private void initRoom(final ArrayList<Room> all_rooms, final Room destination) {
        if (destination != null) {
            final Room base = this.getRoomWithId(all_rooms, destination.getId());
            if (base != null) {
                if (base.getEvents() != null) {
                    destination.setEvents(base.getEvents());
                }
                if (base.getDescription() != null) {
                    destination.setDescription(base.getDescription());
                }
                final Shortcut shortcut = (Shortcut)destination.getEvent(new Shortcut());
                if (shortcut != null) {
                    final String id = shortcut.getDirId();
                    if (id != null && !id.equals("")) {
                        if (id.equals("start")) {
                            shortcut.setRoom(this.start);
                        }
                        else if (id.equals("end")) {
                            shortcut.setRoom(this.end);
                        }
                        else {
                            final int x = id.charAt(0) - '0';
                            final int y = id.charAt(1) - '0';
                            if (x >= 0 && x < this.column && y >= 0 && y < this.line) {
                                shortcut.setRoom(this.generation[x][y]);
                            }
                        }
                    }
                }
                if (Dungeon.getView() != null && (base.isVisited() || base.getId().equals("start"))) {
                    Dungeon.getView().addRoom(destination);
                }
            }
        }
    }
    
    private Room getRoomWithId(final ArrayList<Room> all_rooms, final String id) {
        for (int i = 0; i < all_rooms.size(); ++i) {
            if (all_rooms.get(i).getId().equals(id)) {
                return all_rooms.get(i);
            }
        }
        return null;
    }
    
    private void loadGeneratedWithBase() {
        while (this.hasARoomAlone()) {
            for (int i = 0; i < this.column; ++i) {
                for (int j = 0; j < this.line; ++j) {
                    if (this.isRoomAlone(i, j)) {
                        final int rand_nb_direction = (int)Math.random() * 4;
                        while (this.possibleDirection(i, j) < rand_nb_direction) {
                            final int rand = (int)Math.random() * 4;
                            if (rand == 0 && j > 0) {
                                this.generation[i][j].setDirection(rand, this.generation[i][j - 1]);
                            }
                            if (rand == 1 && i < this.column - 1) {
                                this.generation[i][j].setDirection(rand, this.generation[i + 1][j]);
                            }
                            if (rand == 2 && j < this.line - 1) {
                                this.generation[i][j].setDirection(rand, this.generation[i][j + 1]);
                            }
                            if (rand == 3 && i > 0) {
                                this.generation[i][j].setDirection(rand, this.generation[i - 1][j]);
                            }
                        }
                    }
                }
            }
        }
    }
    
    private int possibleDirection(final int x, final int y) {
        int n = 0;
        if (y > 0) {
            ++n;
        }
        if (x < this.column - 1) {
            ++n;
        }
        if (y < this.line - 1) {
            ++n;
        }
        if (x > 0) {
            ++n;
        }
        return n;
    }
    
    private boolean hasARoomAlone() {
        for (int i = 0; i < this.column; ++i) {
            for (int j = 0; j < this.line; ++j) {
                if (this.isRoomAlone(i, j)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean isRoomAlone(final int x, final int y) {
        boolean north = false;
        boolean east = false;
        boolean south = false;
        boolean west = false;
        if (y > 0 && this.generation[x][y - 1] != null) {
            north = true;
        }
        if (x < this.column - 1 && this.generation[x + 1][y] != null) {
            east = true;
        }
        if (y < this.line - 1 && this.generation[x][y + 1] != null) {
            south = true;
        }
        if (x > 0 && this.generation[x - 1][y] != null) {
            west = true;
        }
        return !north && !east && south && !west;
    }
}