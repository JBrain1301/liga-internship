package ru.liga.songtask.util;

import com.leff.midi.MidiFile;
import com.leff.midi.event.meta.Tempo;
import lombok.extern.slf4j.Slf4j;
import ru.liga.songtask.domain.Note;

import java.util.*;

@Slf4j
public class AnalyzeMidi {
    private MidiFile file;
    List<Note> notes;

    public AnalyzeMidi(MidiFile file) {
        this.file = file;
        notes = SongUtils.getNoteFromTrack(file);
    }

    public void analyzisDiapozon() {
        log.trace("Анализ диапозона трека");
        Map<Integer, String> analyzis = new LinkedHashMap<>();
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (Note s : notes) {
            if (s.sign().getMidi() > max) {
                max = s.sign().getMidi();
                analyzis.put(0, s.sign().fullName());
            }
            if (s.sign().getMidi() < min) {
                min = s.sign().getMidi();
                analyzis.put(1, s.sign().fullName());
            }
        }
        analyzis.put(2, String.valueOf(max - min));
        log.info("Диапозон :");
        log.info(" Верхняя: {}", analyzis.get(0));
        log.info(" Нижняя: {}", analyzis.get(1));
        log.info(" Диапозон: {}", analyzis.get(2));
    }

    public void analyzisDuration() {
        log.trace("Анализ нот трека по длительности.");
        Map<Integer, Integer> analysis = new HashMap<>();
        Tempo tempo = SongUtils.getTempo(file);
        for (Note note : notes) {
            int noteMs = SongUtils.tickToMs(tempo.getBpm(), file.getResolution(), note.durationTicks());
            if (analysis.containsKey(noteMs)) {
                analysis.put(noteMs,analysis.get(noteMs)+1);
            }else {
                analysis.put(noteMs,1);
            }
        }
        log.info("Количество нот по длительностям");
        for (Map.Entry<Integer, Integer> durations : analysis.entrySet()) {
            log.info(durations.getKey() + "мс: " + durations.getValue());
        }
    }

    public void analyzisHeigh() {
        log.trace("Анализ нот по числу вхождений.");
        Map<String, Long> analysis = new HashMap<>();
        for (Note s : notes) {
            if (analysis.containsKey(s.sign().fullName())) {
                Long buf = analysis.get(s.sign().fullName());
                analysis.put(s.sign().fullName(), buf + 1);
            } else {
                analysis.put(s.sign().fullName(), 1L);
            }
        }
        log.info("Список нот по кол-ву вхождений");
        for (Map.Entry<String,Long> heigh : analysis.entrySet()) {
            log.info(heigh.getKey() + ": " + heigh.getValue());
        }
    }

    public void fullAnalize() {
        analyzisDiapozon();
        analyzisDuration();
        analyzisHeigh();
    }
}
