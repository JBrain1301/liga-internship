package ru.liga.songtask.util;

import com.leff.midi.MidiFile;
import com.leff.midi.event.meta.Tempo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.liga.App;
import ru.liga.songtask.domain.Note;

import java.util.*;


public class AnalyzeMidi {
    private static Logger logger = LoggerFactory.getLogger(App.class);
    private MidiFile file;
    List<Note> notes;

    public AnalyzeMidi(MidiFile file) {
        this.file = file;
        notes = SongUtils.getVoiceTrack(file);
    }

    public Map<Integer,String> analyzisDiapozon() {
        logger.trace("Track Range Analysis");
        Map<Integer, String> analyzis = new LinkedHashMap<>();
        final int[] min = {Integer.MAX_VALUE};
        final int[] max = {Integer.MIN_VALUE};
        notes.forEach(note -> {
            if (note.sign().getMidi() > max[0]) {
                max[0] = note.sign().getMidi();
                analyzis.put(0, note.sign().fullName());
            }
            if (note.sign().getMidi() < min[0]) {
                min[0] = note.sign().getMidi();
                analyzis.put(1, note.sign().fullName());
            }
        });

        analyzis.put(2,String.valueOf(max[0] - min[0]));
        logger.info("Range :");
        logger.info(" Upper: {}",analyzis.get(0));
        logger.info(" Lower: {}",analyzis.get(1));
        logger.info(" Range: {}",analyzis.get(2));
        return analyzis;
}

    public Map<Integer,Integer> analyzisDuration() {
        logger.trace("Analysis of track notes by duration.");
        Map<Integer, Integer> analysis = new HashMap<>();
        Tempo tempo = SongUtils.getTempo(file);
        notes.forEach(note -> {
            int noteMs = SongUtils.tickToMs(tempo.getBpm(), file.getResolution(), note.durationTicks());
            if (analysis.containsKey(noteMs)) {
                analysis.put(noteMs, analysis.get(noteMs) + 1);
            } else {
                analysis.put(noteMs, 1);
            }
        });
        logger.info("The number of notes by duration");
        for (Map.Entry<Integer, Integer> durations : analysis.entrySet()) {
            logger.info(durations.getKey() + "ms: " + durations.getValue());
        }
        return analysis;
    }

    public Map<String,Long> analyzisHeigh() {
        logger.trace("Analysis of notes by the number of occurrences.");
        Map<String, Long> analysis = new HashMap<>();
        notes.forEach(note -> {
            if (analysis.containsKey(note.sign().fullName())) {
                Long buf = analysis.get(note.sign().fullName());
                analysis.put(note.sign().fullName(), buf + 1);
            } else {
                analysis.put(note.sign().fullName(), 1L);
            }
        });
        logger.info("List of notes by number of occurrences");
        for (Map.Entry<String, Long> heigh : analysis.entrySet()) {
            logger.info(heigh.getKey() + ": " + heigh.getValue());
        }
        return analysis;
    }

    public void fullAnalize() {
        if (notes != null) {
            logger.info("Start analyze track");
            analyzisDiapozon();
            analyzisDuration();
            analyzisHeigh();
        } else {
            logger.info("No voice track");
        }

    }
}
