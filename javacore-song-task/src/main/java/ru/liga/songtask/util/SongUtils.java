package ru.liga.songtask.util;

import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;
import com.leff.midi.event.meta.Tempo;
import com.leff.midi.event.meta.Text;
import com.leff.midi.event.meta.TrackName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.liga.App;
import ru.liga.songtask.domain.Note;
import ru.liga.songtask.domain.NoteSign;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;


public class SongUtils {
    private static Logger logger = LoggerFactory.getLogger(App.class);

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

    public static List<List<Note>> getVoiceTracks(MidiFile midiFile) {
        logger.trace("Getting fit tracks");
        List<List<Note>> allTracks = SongUtils.getAllTracksAsNoteLists(midiFile);
        return voiceTrackFinder(allTracks);
    }

    private static List<List<Note>> voiceTrackFinder(List<List<Note>> allTracks) {
        logger.trace("Track with voice");
        List<List<Note>> voices = allTracks.stream().filter((notes) -> {
            return isVoice(notes) && !notes.isEmpty();
        }).collect(Collectors.toList());
        logger.trace("Found {} tracks suitable for performance by voice.", voices.size());
        return voices;
    }

    public static List<Note> getVoiceTrack(MidiFile midiFile) {
        logger.debug("Try to find track");
        List<List<Note>> maybe = getVoiceTracks(midiFile);
        long countOfTextEvents = getCountOfTextEvents(midiFile);
        logger.debug("All TextEvent in file {}", countOfTextEvents);
        List<Long> difference = maybe.stream().map((notes) -> {
            return Math.abs((long)notes.size() - countOfTextEvents);
        }).collect(Collectors.toList());
        long minDifference = Collections.min(difference);
        return maybe.get(difference.indexOf(minDifference));
    }

    private static long getCountOfTextEvents(MidiFile midiFile) {
        return midiFile.getTracks().stream().flatMap((midiTrack) -> {
            return midiTrack.getEvents().stream();
        }).filter((midiEvent) -> {
            return midiEvent.getClass().equals(Text.class);
        }).count();
    }

    private static boolean isVoice(List<Note> track) {
        logger.trace("Checking the track for voice performance.");
        long exNoteEndTick = 0L;

        Note n;
        for(Iterator var3 = track.iterator(); var3.hasNext(); exNoteEndTick = n.startTick() + n.durationTicks()) {
            n = (Note)var3.next();
            if (exNoteEndTick > n.startTick()) {
                logger.trace("Not good track");
                return false;
            }
        }

        logger.trace("Good track");
        return true;
    }
    public static Tempo getTempo(MidiFile midiFile) {
        Tempo tempo = (Tempo) ((MidiTrack) midiFile.getTracks().get(0)).getEvents().stream()
                .filter((value) -> value instanceof Tempo).findFirst().get();
        logger.trace("Retrieved event Tempo = {}", tempo);
        return tempo;
    }


    public static List<List<Note>> getAllTracksAsNoteLists(MidiFile midiFile) {
        logger.trace("Extract tracks");
        List<List<Note>> allTracks = new ArrayList();
        for (int i = 0; i < midiFile.getTracks().size(); ++i) {
            List<Note> tmp = eventsToNotes(midiFile.getTracks().get(i).getEvents());
            if (tmp.size() > 0) {
                allTracks.add(tmp);
            }
        }
        logger.trace("Tracks Extracted {} from file.", allTracks.size());
        return allTracks;
    }
}
