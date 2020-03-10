package ru.liga.songtask.util;

import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;
import com.leff.midi.event.meta.Lyrics;
import com.leff.midi.event.meta.Tempo;
import com.leff.midi.event.meta.Text;
import lombok.extern.slf4j.Slf4j;
import ru.liga.songtask.domain.Note;
import ru.liga.songtask.domain.NoteSign;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.TreeSet;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public class SongUtils {


    public static int tickToMs(float bpm, int resolution, long amountOfTick) {
        return (int) (((60 * 1000) / (bpm * resolution)) * amountOfTick);
    }

    public static List<Note> eventsToNotes(TreeSet<MidiEvent> events) {
        List<Note> vbNotes = new ArrayList<>();

        Queue<NoteOn> noteOnQueue = new LinkedBlockingQueue<>();
        for (MidiEvent event : events) {
            if (event instanceof NoteOn || event instanceof NoteOff) {
                if (isEndMarkerNote(event)) {
                    NoteSign noteSign = NoteSign.fromMidiNumber(extractNoteValue(event));
                    if (noteSign != NoteSign.NULL_VALUE) {
                        NoteOn noteOn = noteOnQueue.poll();
                        if (noteOn != null) {
                            long start = noteOn.getTick();
                            long end = event.getTick();
                            vbNotes.add(
                                    new Note(noteSign, start, end - start));
                        }
                    }
                } else {
                    noteOnQueue.offer((NoteOn) event);
                }
            }
        }
        return vbNotes;
    }

    public static Integer extractNoteValue(MidiEvent event) {
        if (event instanceof NoteOff) {
            return ((NoteOff) event).getNoteValue();
        } else if (event instanceof NoteOn) {
            return ((NoteOn) event).getNoteValue();
        } else {
            return null;
        }
    }

    public static boolean isEndMarkerNote(MidiEvent event) {
        if (event instanceof NoteOff) {
            return true;
        } else if (event instanceof NoteOn) {
            return ((NoteOn) event).getVelocity() == 0;
        } else {
            return false;
        }

    }

    public static MidiTrack getVoiceTrack(MidiFile midiFile) {
        log.trace("Попытка получить пригодный трек");
        for (MidiTrack ew : midiFile.getTracks()) {
            boolean isText = false;
            List<MidiEvent> events = new ArrayList<>(ew.getEvents());
            for (int h = 1; h < events.size(); h++) {
                if (events.get(h).getClass().getSimpleName().equals(Lyrics.class.getSimpleName())) {
                    isText = true;
                }
            }
            if (isText) {
                log.trace("Трек найден");
                return ew;
            }

        }
        log.trace("Трек не найден");
        return null;
    }

    public static Tempo getTempo(MidiFile midiFile) {
        Tempo tempo = (Tempo)((MidiTrack)midiFile.getTracks().get(0)).getEvents().stream()
                .filter((value) -> value instanceof Tempo).findFirst().get();
        log.trace("Извлечён event Tempo={}", tempo);
        return tempo;
    }

    public static List<Note> getNoteFromTrack(MidiFile midifile) {
        log.info("Получение списка нот из дорожки");
        System.out.println(SongUtils.eventsToNotes(SongUtils.getVoiceTrack(midifile).getEvents()).size());
        return SongUtils.eventsToNotes(SongUtils.getVoiceTrack(midifile).getEvents());
    }

    public static List<List<Note>> getAllTracksAsNoteLists(MidiFile midiFile) {
        log.trace("Извлечение треков");
        List<List<Note>> allTracks = new ArrayList();
        for (int i = 0; i < midiFile.getTracks().size(); ++i) {
            List<Note> tmp = eventsToNotes(midiFile.getTracks().get(i).getEvents());
            if (tmp.size() > 0) {
                allTracks.add(tmp);
            }
        }
        log.trace("Извлечены треки {} из файла.", allTracks.size());
        return allTracks;
    }
}
