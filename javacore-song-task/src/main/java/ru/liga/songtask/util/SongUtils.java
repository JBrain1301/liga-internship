package ru.liga.songtask.util;

import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;
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

    /**
     * Перевод тиков в миллисекунды
     *
     * @param bpm          - количество ударов в минуту (темп)
     * @param resolution   - midiFile.getResolution()
     * @param amountOfTick - то что переводим в миллисекунды
     * @return
     */
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
        for (MidiTrack ew :midiFile.getTracks()) {
            boolean isText = true;
            List<MidiEvent> events = new ArrayList<>(ew.getEvents());
            for (int h = 1;h < events.size();h++){
                if (!(events.get(h).getClass().getSimpleName().equals(Text.class.getSimpleName()))) {
                    isText = false;
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
        MidiTrack midiTrack = getVoiceTrack(midiFile);
        if (midiTrack != null) {
            Tempo last = (Tempo) getVoiceTrack(midiFile).getEvents().last();
            log.trace("Извлечён Tempo={}", last);
            return last;
        }else {
            log.trace("Отсутствует подходящая дорожка");
            return null;
        }

    }

    public static List<Note> getNoteFromTrack(MidiFile track) {
        log.info("Получение списка нот из дорожки");
        return SongUtils.eventsToNotes(SongUtils.getVoiceTrack(track).getEvents());
    }

    public static List<List<Note>> getAllTracksAsNoteLists(MidiFile midiFile) {
        log.trace("Процедура извлечерия треков из файла в виде List<Notes>");
        List<List<Note>> allTracks = new ArrayList();

        for(int i = 0; i < midiFile.getTracks().size(); ++i) {
            List<Note> tmp = eventsToNotes(midiFile.getTracks().get(i)).getEvents());
            if (tmp.size() > 0) {
                allTracks.add(tmp);
            }
        }

        log.trace("Извлечены все треки {} из файла.", allTracks.size());
        return allTracks;
    }
}
