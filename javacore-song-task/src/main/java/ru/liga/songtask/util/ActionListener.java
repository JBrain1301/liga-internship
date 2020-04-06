package ru.liga.songtask.util;

import com.leff.midi.MidiFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.liga.songtask.actions.AnalyzeMidi;
import ru.liga.songtask.actions.ChangeMidi;

import java.io.File;
import java.io.IOException;

public class ActionListener {
    private static Logger logger = LoggerFactory.getLogger(ActionListener.class);

    public static void start(String[] args) throws IOException {
        ArgsUtill.getArgs(args);
    }

    public static void analyze(String path) throws IOException {
        logger.debug("Analyze midi file");
        MidiFile midiFile = new MidiFile(new File(path));
        AnalyzeMidi analyze = new AnalyzeMidi(midiFile);
        analyze.fullAnalize();
    }

    protected static void change(String arg, Integer trans, Float tempo) {
        logger.info("Changing the file {}, with transposing in {} semitones and changing the tempo by {}%", arg, trans, tempo);
        File file = new File(arg);
        try {
            MidiFile midiFile = new MidiFile(file);
            MidiFile newMidi = ChangeMidi.changeMidi(midiFile, trans, tempo);
            String newPath = PathUtill.getPath(trans, tempo, file);
            newMidi.writeToFile(new File(newPath));
            logger.info("Modified file: {}", newPath);
        } catch (IOException e) {
            logger.trace("Midi file error");
        }
    }
}
