package game;

import field.Room;
import entity.Player;
import java.io.InputStream;
import javafx.scene.text.Text;
import java.util.ArrayList;
import java.io.File;

public interface DataLoader
{
    File chooseLoadDestination(final String p0);
    
    void unZip(final String p0);
    
    ArrayList<Text> loadFileText(final String p0);
    
    void setGame(final InputStream p0);
    
    int getGenerationAttribute(final String p0);
    
    String gameGameRoom(final String p0);
    
    Player loadPlayer();
    
    ArrayList<Room> loadRooms();
}

