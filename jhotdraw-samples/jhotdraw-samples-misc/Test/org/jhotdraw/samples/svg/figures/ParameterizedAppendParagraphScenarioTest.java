package org.jhotdraw.samples.svg.figures;

import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.junit.ScenarioTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import steps.GivenParagraph;
import steps.ThenParagraphBounds;
import steps.WhenAppendParagraph;
import java.awt.geom.Rectangle2D;


@RunWith( Parameterized.class )
public class ParameterizedAppendParagraphScenarioTest extends ScenarioTest<GivenParagraph, WhenAppendParagraph, ThenParagraphBounds> {
    @ProvidedScenarioState
    private final SVGTextAreaFigure svgTextAreaFigure = new SVGTextAreaFigure();

    Rectangle2D.Double expectedParagraphBounds;
    SVGTextAreaFigure.Paragraph paragraph;
    SVGTextAreaFigure.TextBox textBox;


    public ParameterizedAppendParagraphScenarioTest(SVGTextAreaFigure.TextBox textBox, SVGTextAreaFigure.Paragraph paragraph, Rectangle2D.Double expectedBounds ) {
        this.textBox = textBox;
        this.paragraph = paragraph;
        this.expectedParagraphBounds = expectedBounds;
    }

    @Test
    public void textArea_bounds_can_be_calculated_from_a_TextBox_a_stylyzedText_a_TextAreaFigure() {
        given().a_textBox().
                and().a_Paragraph(textBox);

        when().textArea_is_appaened_with_a_paragraph(paragraph);

        then().the_correct_paragraph_bounds_are(expectedParagraphBounds);
    }

}
