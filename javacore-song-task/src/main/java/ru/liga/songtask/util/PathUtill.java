package ru.liga.songtask.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class PathUtill {
    private static Logger logger = LoggerFactory.getLogger(PathUtill.class);

    protected static String getPath(int trans, float tempo, File file) {
        logger.info("File changed");
        String builder = file.getName().replace(".mid", "") +
                "-trans" + trans + "-tempo" + tempo + ".mid";
        return file.getParentFile().getAbsolutePath() + File.separator + builder;
    }
}
