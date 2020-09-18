package view;

public class RoomView
{
    private boolean[] dir;
    private boolean[] event;
    String id;
    private int posX;
    private int posY;
    
    public RoomView(final String id, final int posX, final int posY, final boolean[] dir, final boolean[] event) {
        this.id = id;
        this.posX = posX;
        this.posY = posY;
        this.dir = dir;
        this.event = event;
    }
    
    public String getId() {
        return this.id;
    }
    
    public int x() {
        return this.posX;
    }
    
    public int y() {
        return this.posY;
    }
    
    public boolean[] getDir() {
        return this.dir;
    }
    
    public boolean[] getEvent() {
        return this.event;
    }
    
    public void setEvent(final boolean[] value) {
        this.event = value;
    }
}