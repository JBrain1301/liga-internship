package ru.liga;


import com.leff.midi.MidiFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.liga.songtask.util.AnalyzeMidi;
import ru.liga.songtask.util.ChangeMidi;

import java.io.File;
import java.io.IOException;


public class App {
    private static Logger logger = LoggerFactory.getLogger(App.class);
    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            getArgs(args);
        } else {
            logger.info("No args");
        }
    }

    public static void analyze(String path) throws IOException {
        logger.debug("Analyze midi file");
        MidiFile midiFile = new MidiFile(new File(path));
        AnalyzeMidi analyze = new AnalyzeMidi(midiFile);
        analyze.fullAnalize();
    }

    public static void getArgs(String[] args) throws IOException {
        String params = args[1].trim();
        if (params.equals("analyze")) {
            analyze(args[0]);
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
            change(args[0], trans, tempo);
        }
    }

    private static void change(String arg, Integer trans, Float tempo) {
        logger.info("Changing the file {}, with transposing in {} semitones and changing the tempo by {}%", arg, trans, tempo);
        File file = new File(arg);
        try {
            MidiFile midiFile = new MidiFile(file);
            MidiFile newMidi = ChangeMidi.changeMidi(midiFile, trans, tempo);
            String newPath = getPath(trans, tempo, file);
            newMidi.writeToFile(new File(newPath));
            logger.info("Modified file: {}", newPath);
        } catch (IOException e) {
            logger.trace("Midi file error");
        }
    }

    private static String getPath(int trans, float tempo, File file) {
        logger.info("File changed");
        String builder = file.getName().replace(".mid", "") +
                "-trans" + trans + "-tempo" + tempo + ".mid";
        return file.getParentFile().getAbsolutePath() + File.separator + builder;
    }
}
