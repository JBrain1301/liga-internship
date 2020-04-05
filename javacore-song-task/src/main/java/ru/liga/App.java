package ru.liga;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.liga.songtask.util.ArgsUtill;

import java.io.IOException;


public class App {
    private static Logger logger = LoggerFactory.getLogger(App.class);
    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            ArgsUtill.getArgs(args);
        } else {
            logger.info("No args");
        }
    }
}
