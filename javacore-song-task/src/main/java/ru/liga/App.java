package ru.liga;


import com.leff.midi.MidiFile;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;
import com.leff.midi.event.meta.Tempo;
import lombok.extern.slf4j.Slf4j;
import ru.liga.songtask.domain.Note;
import ru.liga.songtask.domain.NoteSign;
import ru.liga.songtask.util.SongUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.TreeSet;
import java.util.concurrent.LinkedBlockingQueue;

import static ru.liga.songtask.util.SongUtils.eventsToNotes;
@Slf4j
public class App {

    /**
     * Это пример работы, можете всё стирать и переделывать
     * Пример, чтобы убрать у вас начальный паралич разработки
     * Также посмотрите класс SongUtils, он переводит тики в миллисекунды
     * Tempo может быть только один
     */
    public static void main(String[] args) throws IOException {
        MidiFile midiFile = new MidiFile(new FileInputStream("C:\\Users\\Xiaomi\\IdeaProjects\\liga-internship\\javacore-song-task\\src\\main\\resources\\Wrecking Ball.mid"));
        List<Note> notes = eventsToNotes(midiFile.getTracks().get(3).getEvents());
        Tempo last = (Tempo) midiFile.getTracks().get(0).getEvents().last();
        Note ninthNote = notes.get(8);
        System.out.println("Длительность девятой ноты (" + ninthNote.sign().fullName() + "): " + SongUtils.tickToMs(last.getBpm(), midiFile.getResolution(), ninthNote.durationTicks()) + "мс");
        System.out.println("Все ноты:");
        System.out.println(notes);
    }

    /**
     * Этот метод, чтобы вы не афигели переводить эвенты в ноты
     *
     * @param events эвенты одного трека
     * @return список нот
     */

}
