import com.leff.midi.MidiFile;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.liga.songtask.actions.AnalyzeMidi;

import java.io.File;
import java.io.IOException;

public class TestApp {
    AnalyzeMidi analyzeMidi;
    static MidiFile file;

    @BeforeClass
    public static void initializeMidi() throws IOException {
        file = new MidiFile(new File("C:/Belle.mid"));
    }

    @Before
    public void initializeAnalyze() {
        analyzeMidi = new AnalyzeMidi(file);
    }

    @Test
    public void Right_AnalysisDiapoz() {
        Assertions.assertThat(analyzeMidi.analyzisDiapozon().get(0)).isEqualTo("G#4");
        Assertions.assertThat(analyzeMidi.analyzisDiapozon().get(1)).isEqualTo("A#2");
        Assertions.assertThat(analyzeMidi.analyzisDiapozon().get(2)).isEqualTo("22");
    }

    @Test
    public void Right_AnalysisHeight() {
        Assertions.assertThat(analyzeMidi.analyzisHeigh().get("H3")).isEqualTo(21);
        Assertions.assertThat(analyzeMidi.analyzisHeigh().get("A#2")).isEqualTo(1);
    }

    @Test
    public void Right_AnalysisDuration() {
        Assertions.assertThat(analyzeMidi.analyzisDuration().get(1250)).isEqualTo(8);
        Assertions.assertThat(analyzeMidi.analyzisDuration().get(357)).isEqualTo(162);
    }
}
