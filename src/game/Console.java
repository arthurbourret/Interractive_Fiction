package game;

import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import java.util.Iterator;
import java.util.StringTokenizer;
import javafx.scene.text.Text;
import java.util.ArrayList;
import controller.MainController;

public class Console
{
    public static final String SEPARATOR = "|";
    public static final String STYLE_TITLE = "title";
    public static final String STYLE_COMMAND = "command";
    public static final String STYLE_INPUT = "input";
    public static final String STYLE_ERROR = "error";
    private static MainController controller;
    
    public static void setController(final MainController controller) {
        Console.controller = controller;
    }
    
    private static ArrayList<Text> separateLines(final String txt, final String format) {
        final ArrayList<Text> lines = new ArrayList<Text>();
        final StringTokenizer st = new StringTokenizer(txt, "|");
        while (st.hasMoreTokens()) {
            final Text temp = new Text(st.nextToken());
            temp.getStyleClass().add(format);
            lines.add(temp);
        }
        return lines;
    }
    
    public static void printText(final String txt) {
        printText(txt, "");
    }
    
    public static void printText(final String txt, final String format) {
        if (!txt.equals("") && txt != null) {
            printText(separateLines(txt, format));
        }
    }
    
    public static void printText(final ArrayList<Text> txt) {
        final Iterator<Text> it = txt.iterator();
        while (it.hasNext()) {
            affText(it.next());
        }
    }
    
    private static void affText(final Text txt) {
        if (txt.getStyleClass().contains("command")) {
            txt.setText("- " + txt.getText());
            txt.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(final MouseEvent event) {
                    Console.controller.checkText(getCommandFromText(txt.getText()));
                }
            });
        }
        if (txt.getStyleClass().contains("error")) {
            txt.setText("Error - " + txt.getText());
        }
        Console.controller.dispText(txt);
    }
    
    private static String getCommandFromText(final String txt) {
        final int pos = txt.lastIndexOf(32);
        if (pos + 1 < txt.length()) {
            return txt.substring(pos + 1);
        }
        return null;
    }
}