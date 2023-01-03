package org.jhotdraw.samples.svg.figures;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.jhotdraw.api.app.Application;
import org.jhotdraw.app.SDIApplication;
import org.jhotdraw.samples.svg.SVGApplicationModel;
import org.jhotdraw.samples.svg.SVGView;
import org.jhotdraw.samples.svg.figures.SVGTextAreaFigure.Paragraph;
import org.mockito.Mockito;

import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

import static org.assertj.swing.launcher.ApplicationLauncher.application;

class SVGTextAreaFigureTest extends AssertJSwingJUnitTestCase {
    //parameters used in tests for making a Paragraph object
    Path2D.Double shape = new Path2D.Double();
    AttributedString as = new AttributedString("Test text");
    AttributedCharacterIterator styledText = as.getIterator();
    float[] tabStops = new float[0];
    int tabCount = 1;
    Paragraph paragraph;
    SVGTextAreaFigure.TextBox textBox;

    @Test
    void paragraph_bounds_are_calculated_correctly() {
        // mock TextBox class
        textBox = Mockito.mock(SVGTextAreaFigure.TextBox.class);
        // mock the text box boundries
        Mockito.when(textBox.getVerticalPos()).thenReturn(60.0F);
        Mockito.when(textBox.getMaxVerticalPos()).thenReturn(3.0F);
        Mockito.when(textBox.getLeftMargin()).thenReturn(10.0F);
        Mockito.when(textBox.getRightMargin()).thenReturn(500.0F);
        // instantiating a paragraph object using the mocked TextBox size
        paragraph = new Paragraph(shape, styledText, textBox, tabStops, tabCount );
        // Setting up the asserts
        SVGTextAreaFigure svgTextAreaFigure = new SVGTextAreaFigure();
        Rectangle2D.Double expectedParagraphBounds = new Rectangle2D.Double(10.0, 60.0, 0.0, 0.0);
        Rectangle2D.Double actualParagraphBounds = svgTextAreaFigure.appendParagraph(paragraph);
        // Asserts that the appendParagraph() method will calculate the correct paragraph bounds
        Assertions.assertEquals(expectedParagraphBounds, actualParagraphBounds);
        // Asserts that the paragraph bounds can never be empty after appendParagraph() method was executed
        Rectangle2D.Double emptyParagraphBounds = new Rectangle2D.Double(0.0, 0.0, 0.0, 0.0);
        Assertions.assertNotEquals(emptyParagraphBounds,actualParagraphBounds);
        // Asserts that paragraph bounds are never null
        Assertions.assertNotEquals(null,actualParagraphBounds);
        }

    @Test
    void paragraph_tabs_are_counted_by_stylizeTextTabs() {
        // add a text containing a tab to stylized text
        StringBuilder sb = new StringBuilder();
        sb.append("text"); sb.append('\t'); sb.append("text");
        as = new AttributedString(sb.toString());
        styledText = as.getIterator();
        // mock TextBox class
        textBox = Mockito.mock(SVGTextAreaFigure.TextBox.class);
        // mock the text box boundries
        Mockito.when(textBox.getVerticalPos()).thenReturn(70.0F);
        Mockito.when(textBox.getMaxVerticalPos()).thenReturn(3.0F);
        Mockito.when(textBox.getLeftMargin()).thenReturn(0.0F);
        Mockito.when(textBox.getRightMargin()).thenReturn(128.0F);

        // instantiating a paragraph object using the mocked TextBox
        paragraph = new Paragraph(shape, styledText, textBox, tabStops, tabCount );
        SVGTextAreaFigure svgTextAreaFigure = new SVGTextAreaFigure();
        // expected results an array of tab locations must contain two indexes, the location of the tab, which
        // is 4 because it comes right after word "text", and number 8 which is
        // the offset of the last character in the text in the original JHotDraw
        int [] expectedTabLocations = new int[]{4,8};
        int [] actualTabLocations = svgTextAreaFigure.stylizeTextTabs(paragraph);
        Assertions.assertArrayEquals(expectedTabLocations,actualTabLocations);
        // asserts that the tabLocations array is never null after the stylizeTextTabs is executed
        Assertions.assertNotEquals(null,actualTabLocations);
    }



    @Override
    protected void onSetUp() {
        application(org.jhotdraw.samples.svg.Main.class).start();
        Application app  = new SDIApplication();
        SVGApplicationModel model = new SVGApplicationModel();
        model.setViewClassName("org.jhotdraw.samples.svg.SVGView");
        app.setModel(model);
        SVGView view = (SVGView) model.createView();
        view.init();
        model.initView(app, view);
    }

    public void readTextFromIterator(AttributedCharacterIterator styledText){
        String a = "";
        a+=styledText.current();
        while (styledText.getIndex() <styledText.getEndIndex())
            a += styledText.next();
        a=a.substring(0,a.length()-1);
        System.out.println(a);
    }
}

