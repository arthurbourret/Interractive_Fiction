package game;

import java.util.ArrayList;

public class Log
{
    private static ArrayList<String> command;
    private static int pos;
    
    public static void initialize() {
        Log.command = new ArrayList<String>();
        Log.pos = 0;
    }
    
    public static void addLog(final String input) {
        Log.command.add(input);
        Log.pos = Log.command.size() - 1;
    }
    
    public static String upPos() {
        final String tmp = Log.command.get(Log.pos);
        if (Log.pos > 0) {
            --Log.pos;
        }
        return tmp;
    }
    
    public static String downPos() {
        final String tmp = Log.command.get(Log.pos);
        if (Log.pos < Log.command.size() - 1) {
            ++Log.pos;
        }
        return tmp;
    }
    
    public static void reset() {
        Log.pos = Log.command.size() - 1;
    }
}