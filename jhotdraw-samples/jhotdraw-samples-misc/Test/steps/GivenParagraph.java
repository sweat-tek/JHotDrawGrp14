package steps;

import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import org.jhotdraw.samples.svg.figures.SVGTextAreaFigure;
import org.mockito.Mockito;

public class GivenParagraph extends Stage<GivenParagraph> {
    @ProvidedScenarioState
    Rectangle2D.Double paragraphBounds = new Rectangle2D.Double();

    public GivenParagraph a_textBox() {
        // mock TextBox class
        SVGTextAreaFigure.TextBox textBox = Mockito.mock(SVGTextAreaFigure.TextBox.class);
        // mock the text box boundries
        Mockito.when(textBox.getVerticalPos()).thenReturn(70.0F);
        Mockito.when(textBox.getMaxVerticalPos()).thenReturn(3.0F);
        Mockito.when(textBox.getLeftMargin()).thenReturn(0.0F);
        Mockito.when(textBox.getRightMargin()).thenReturn(128.0F);
        return a_Paragraph(textBox);
    }

    public GivenParagraph a_Paragraph(SVGTextAreaFigure.TextBox textBox) {
        // create the paragraph here
        Path2D.Double shape = new Path2D.Double();
        AttributedString as = new AttributedString("Test text");
        AttributedCharacterIterator styledText = as.getIterator();
        float[] tabStops = new float[0];
        int tabCount = 1;
        SVGTextAreaFigure.Paragraph paragraph = new SVGTextAreaFigure.Paragraph(shape, styledText, textBox, tabStops, tabCount );
        return this;
    }

}