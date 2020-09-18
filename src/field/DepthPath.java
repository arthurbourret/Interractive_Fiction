package field;

import java.util.ArrayList;

public class DepthPath
{
    int summit;
    int maxLength;
    Room[] tab;
    ArrayList<Room> array;
    
    public DepthPath(final int maxLength) {
        this.summit = -1;
        this.maxLength = maxLength;
        this.tab = new Room[maxLength];
        this.array = new ArrayList<Room>();
    }
    
    public ArrayList<Room> getGeneration(final Room end) {
        try {
            this.explorate(end);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return this.array;
    }
    
    private void explorate(final Room room) throws Exception {
        if (!this.isMarqued(room)) {
            this.stack(room);
            this.array.add(room);
            for (int i = 0; i < room.getDirection().length; ++i) {
                if (room.getDirection(i) != null) {
                    this.explorate(room.getDirection(i));
                }
            }
            this.unstack();
        }
    }
    
    private boolean isMarqued(final Room room) {
        Room[] tab;
        for (int length = (tab = this.tab).length, i = 0; i < length; ++i) {
            final Room current = tab[i];
            if (current == null) {
                return false;
            }
            if (current.getId().equals(room.getId())) {
                return true;
            }
        }
        return false;
    }
    
    private void stack(final Room element) throws Exception {
        if (this.summit != this.maxLength) {
            ++this.summit;
            this.tab[this.summit] = element;
            return;
        }
        throw new Exception();
    }
    
    private Room unstack() throws Exception {
        if (this.summit != -1) {
            --this.summit;
            return this.tab[this.summit + 1];
        }
        throw new Exception();
    }
}