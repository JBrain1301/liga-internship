package ru.liga.songtask.util;

import com.leff.midi.MidiFile;
import com.leff.midi.event.meta.Tempo;
import lombok.extern.slf4j.Slf4j;
import ru.liga.songtask.domain.Note;

import java.util.*;

@Slf4j
public class AnalyzeMidi {
    private MidiFile file;

    public AnalyzeMidi(MidiFile file) {
        this.file = file;
    }

    public void analyzisDiapozon() {
        log.trace("Анализ диапозона трека");
        Map<Integer, String> analyzis = new LinkedHashMap<>();
        List<Note> list = SongUtils.getNoteFromTrack(file);
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (Note s : list) {
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
        List<Note> notes = SongUtils.getNoteFromTrack(file);
        Tempo tempo = SongUtils.getTempo(file);
        for (Note note : notes) {
            int noteMs = SongUtils.tickToMs(tempo.getBpm(),file.getResolution(),note.durationTicks());
            analysis.put(noteMs, analysis.getOrDefault(noteMs, 1));
        }
        log.info("Количество нот по длительностям");
        for (Map.Entry<Integer, Integer> durations : analysis.entrySet()) {
            log.info(durations.getKey() + ": " + durations.getValue());
        }
    }
}
