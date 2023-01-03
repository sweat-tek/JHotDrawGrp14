package org.jhotdraw.samples.svg.figures;
import static org.junit.Assert.*;

import org.junit.Test;
public class SVGRectFigureTest {
    @Test
   public void testConstructors() {
        SVGRectFigure f1 = new SVGRectFigure();
        assertEquals(0, f1.getX(), 0);
        assertEquals(0, f1.getY(), 0);
        assertEquals(0, f1.getWidth(), 0);
        assertEquals(0, f1.getHeight(), 0);
        assertEquals(0, f1.getArcWidth(), 0);
        assertEquals(0, f1.getArcHeight(), 0);

        SVGRectFigure f2 = new SVGRectFigure(1, 2, 3, 4);
        assertEquals(1, f2.getX(), 0);
        assertEquals(2, f2.getY(), 0);
        assertEquals(3, f2.getWidth(), 0);
        assertEquals(4, f2.getHeight(), 0);
        assertEquals(0, f2.getArcWidth(), 0);
        assertEquals(0, f2.getArcHeight(), 0);

        SVGRectFigure f3 = new SVGRectFigure(1, 2, 3, 4, 5, 6);
        assertEquals(1, f3.getX(), 0);
        assertEquals(2, f3.getY(), 0);
        assertEquals(3, f3.getWidth(), 0);
        assertEquals(4, f3.getHeight(), 0);
        assertEquals(5, f3.getArcWidth(), 0);
        assertEquals(6, f3.getArcHeight(), 0);
    }

}
