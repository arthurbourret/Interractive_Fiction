package game;

import java.io.IOException;
import field.Dungeon;
import java.io.File;

public interface DataSaver
{
    File chooseSaveDestination(final String p0);
    
    void saveGame(final File p0, final Dungeon p1) throws IOException;
}