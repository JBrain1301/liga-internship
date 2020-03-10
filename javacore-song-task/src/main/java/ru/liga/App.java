package ru.liga;


import com.leff.midi.MidiFile;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;
import com.leff.midi.event.meta.Tempo;
import lombok.extern.slf4j.Slf4j;
import ru.liga.songtask.domain.Note;
import ru.liga.songtask.domain.NoteSign;
import ru.liga.songtask.util.AnalyzeMidi;
import ru.liga.songtask.util.ChangeMidi;
import ru.liga.songtask.util.SongUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

import static ru.liga.songtask.util.SongUtils.eventsToNotes;

@Slf4j
public class App {

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            getArgs(args);
        } else {
            log.info("Нет аргументов");
        }
    }

    public static void analyze(String path) throws IOException {
        log.debug("Анализ midi файла");
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
                        log.debug("Неправильный агрумент: {}", e.getMessage());
                    }
                }
                if (args[4].equals("-tempo")) {
                    try {
                        tempo = Float.parseFloat(args[5]);
                    } catch (Exception e) {
                        log.debug("Неправильный агрумент: {}", e.getMessage());
                    }
                }
            }
            change(args[0], trans, tempo);
        }
    }

    private static void change(String arg, Integer trans, Float tempo) {
        log.info("Изменение файла {}, с транспонированием на {} полутонов и изменением темпа на {}%", arg, trans, tempo);
        File file = new File(arg);
        try {
            MidiFile midiFile = new MidiFile(file);
            MidiFile newMidi = ChangeMidi.changeMidi(midiFile, trans, tempo);
            String newPath = getPath(trans, tempo, file);
            newMidi.writeToFile(new File(newPath));
            log.info("Изменённый файл: {}", newPath);
        } catch (IOException e) {
            log.trace("Ошибка Midi файла");
        }
    }

    private static String getPath(int trans, float tempo, File file) {
        StringBuilder builder = new StringBuilder();
        builder.append(file.getName().replace(".mid","")).
                append("-trans").append(trans).append("-tempo").append(tempo).append(".mid");
        log.info("Файл изменён.");
        return file.getParentFile().getAbsolutePath() + File.separator + builder.toString();
    }
}
