package steps;

import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;

import java.awt.geom.Rectangle2D;

public class ThenParagraphBounds extends Stage<ThenParagraphBounds> {
    @ExpectedScenarioState
    Rectangle2D.Double expectedParagraphBounds = new Rectangle2D.Double(10.0, 60.0, 0.0, 0.0);

    public void the_correct_paragraph_bounds_are( Rectangle2D.Double expectedBounds ) {
        assertThat( expectedParagraphBounds ).isEqualTo( expectedBounds);
    }
}

