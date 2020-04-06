package ru.liga;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.liga.songtask.util.ActionListener;

import java.io.IOException;


public class App {
    private static Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            ActionListener.start(args);
        } else {
            logger.info("No args");
        }
    }
}
