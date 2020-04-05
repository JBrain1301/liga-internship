package ru.liga.songtask.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.liga.App;

import java.io.IOException;

public class ArgsUtill {
    private static Logger logger = LoggerFactory.getLogger(ArgsUtill.class);

    public static void getArgs(String[] args) throws IOException {
        String params = args[1].trim();
        if (params.equals("analyze")) {
            ActionListener.analyze(args[0]);
        } else {
            Integer trans = null;
            Float tempo = null;
            if (params.equals("change")) {
                if (args[2].equals("-trans")) {
                    try {
                        trans = Integer.parseInt(args[3]);
                    } catch (Exception e) {
                        logger.debug("Wrong agr: {}", e.getMessage());
                    }
                }
                if (args[4].equals("-tempo")) {
                    try {
                        tempo = Float.parseFloat(args[5]);
                    } catch (Exception e) {
                        logger.debug("Wrong arg: {}", e.getMessage());
                    }
                }
            }
            ActionListener.change(args[0], trans, tempo);
        }
    }
}
