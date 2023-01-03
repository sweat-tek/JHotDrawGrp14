package steps;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Set;


import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.ScenarioState;
import org.jhotdraw.samples.svg.figures.SVGTextAreaFigure;



//tag::state[]
//@JGivenStage
public class WhenAppendParagraph extends Stage<WhenAppendParagraph> {

    @ScenarioState
            //import paragraph from given class ...

    @ExpectedScenarioState
     Rectangle2D.Double paragraphBounds;

    @ProvidedScenarioState
    Set<String> dough;
    SVGTextAreaFigure textFigure;

    @ProvidedScenarioState
    SVGTextAreaFigure.Paragraph paragraph;

    public WhenAppendParagraph textArea_is_appaened_with_a_paragraph(SVGTextAreaFigure.Paragraph paragraph) {
        assertThat( paragraph ).isNotNull();
        assertThat( textFigure ).isNotNull();
        paragraphBounds = textFigure.appendParagraph(paragraph);
        return this;
    }


}