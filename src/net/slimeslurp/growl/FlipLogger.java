package net.slimeslurp.growl;

import java.io.PrintStream;
import org.apache.tools.ant.DefaultLogger;

/**
 * Flips Ant output
 */
public class FlipLogger extends DefaultLogger {


    protected void printMessage(java.lang.String message,
                                java.io.PrintStream stream,
                                int priority)
    {
        try {
            PrintStream ps = new PrintStream(super.out,true, "UTF-8");
            super.printMessage(Flip.flip(message), ps, priority);
        } catch(java.io.UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

}