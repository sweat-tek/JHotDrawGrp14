/*
 * @(#)SVGTextArea.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.samples.svg.figures;

import dk.sdu.mmmi.featuretracer.lib.FeatureEntryPoint;
import org.checkerframework.checker.units.qual.A;
import org.jhotdraw.draw.figure.TextHolderFigure;
import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.text.*;
import java.util.*;
import org.jhotdraw.draw.*;
import static org.jhotdraw.draw.AttributeKeys.FILL_COLOR;
import static org.jhotdraw.draw.AttributeKeys.FONT_SIZE;
import static org.jhotdraw.draw.AttributeKeys.FONT_UNDERLINE;
import static org.jhotdraw.draw.AttributeKeys.STROKE_COLOR;
import static org.jhotdraw.draw.AttributeKeys.STROKE_WIDTH;
import static org.jhotdraw.draw.AttributeKeys.TEXT;
import static org.jhotdraw.draw.AttributeKeys.TRANSFORM;
import org.jhotdraw.draw.handle.BoundsOutlineHandle;
import org.jhotdraw.draw.handle.FontSizeHandle;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.handle.ResizeHandleKit;
import org.jhotdraw.draw.handle.TextOverflowHandle;
import org.jhotdraw.draw.handle.TransformHandleKit;
import org.jhotdraw.draw.tool.TextAreaEditingTool;
import org.jhotdraw.draw.tool.Tool;
import org.jhotdraw.geom.Dimension2DDouble;
import org.jhotdraw.geom.Geom;
import org.jhotdraw.geom.Insets2D;
import org.jhotdraw.samples.svg.Gradient;
import org.jhotdraw.samples.svg.SVGAttributeKeys;
import static org.jhotdraw.samples.svg.SVGAttributeKeys.*;

/**
 * SVGTextArea.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SVGTextAreaFigure extends SVGAttributedFigure
        implements SVGFigure, TextHolderFigure {

    private static final long serialVersionUID = 1L;
    private Rectangle2D.Double bounds = new Rectangle2D.Double();
    private boolean editable = true;

    private static final BasicStroke DASHES = new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0f, new float[]{4f, 4f}, 0f);
    /**
     * This is a cached value to improve the performance of method isTextOverflow();
     */
    private Boolean isTextOverflow;
    /**
     * This is used to perform faster drawing and hit testing.
     */
    private transient Rectangle2D.Double cachedDrawingArea;
    private transient Shape cachedTextShape;

    /**
     * Creates a new instance.
     */
    public SVGTextAreaFigure() {
        this("Text");
    }

    public SVGTextAreaFigure(String text) {
        setText(text);
        SVGAttributeKeys.setDefaults(this);
        setConnectable(false);
    }

    // DRAWING
    // Feature text tool draw
    @FeatureEntryPoint(value="TextWrite - drawText")
    @Override
    protected void drawText(java.awt.Graphics2D g) {
    }


    @Override
    protected void drawFill(Graphics2D g) {
        g.fill(getTextShape());
    }


    @Override
    protected void drawStroke(Graphics2D g) {
        g.draw(getTextShape());
    }

    // SHAPE AND BOUNDS

    @Override
    public Rectangle2D.Double getBounds() {
        return (Rectangle2D.Double) bounds.clone();
    }


    @Override
    public Rectangle2D.Double getDrawingArea() {

        if (cachedDrawingArea == null) {
            Rectangle2D.Double r = getBounds();
            double g = SVGAttributeKeys.getPerpendicularHitGrowth(this, 1.0);
            Geom.grow(r, g, g);
            if (get(TRANSFORM) == null) {
                cachedDrawingArea = r;
            } else {
                cachedDrawingArea = new Rectangle2D.Double();
                cachedDrawingArea.setRect(get(TRANSFORM).createTransformedShape(r).getBounds2D());
            }
        }
        return (Rectangle2D.Double) cachedDrawingArea.clone();
    }

    /**
     * Checks if a Point2D.Double is inside the figure.
     */
    // This could be a Test Case
    @Override
    public boolean contains(Point2D.Double p) {
        if (get(TRANSFORM) != null) {
            try {
                p = (Point2D.Double) get(TRANSFORM).inverseTransform(p, new Point2D.Double());
            } catch (NoninvertibleTransformException ex) {
                ex.printStackTrace();
            }
        }
        Rectangle2D r = getTextShape().getBounds2D();
        return r.isEmpty() ? getBounds().contains(p) : r.contains(p);
    }
    private Shape getTextShape() {
        if (cachedTextShape == null) {
            Path2D.Double shape;
            cachedTextShape = shape = new Path2D.Double();

            if (containsEditableText()) {
                Font font = getFont();
                boolean isUnderlined = get(FONT_UNDERLINE);

                // create Text Rectangle
                TextBox textBox = new TextBox();

                if (textBox.hasInnerArea) {
                    float [] tabStops = findTabs(textBox);

                    if (getText() != null) {
                        String[] paragraphs = getText().split("\n"); //Strings.split(getText(), '\n');
                        for (int i = 0; i < paragraphs.length; i++) {
                            if (paragraphs[i].length() == 0) {
                                paragraphs[i] = " ";
                            }
                            AttributedString as = new AttributedString(paragraphs[i]);
                            as.addAttribute(TextAttribute.FONT, font);
                            if (isUnderlined) {
                                as.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_ONE_PIXEL);
                            }
                            int tabCount = paragraphs[i].split("\t").length - 1;
                            Paragraph paragraph = new Paragraph(shape, as.getIterator(),
                                    textBox, tabStops, tabCount);
                            Rectangle2D.Double paragraphBounds = appendParagraph(paragraph);
                            textBox.verticalPos = (float) (paragraphBounds.y + paragraphBounds.height);
                            if (textBox.verticalPos > textBox.textRect.y + textBox.textRect.height) {
                                break;
                            }
                        }
                    }
                }
            }
        }
        return cachedTextShape;
    }

    private float[] findTabs(TextBox textBox) {
        float tabWidth = (float) (getTabSize() * getFont().getStringBounds("m", getFontRenderContext()).getWidth());
        float[] tabStops = new float[(int) (textBox.textRect.width / tabWidth)];
        for (int i = 0; i < tabStops.length; i++) {
            tabStops[i] = (float) (textBox.textRect.x + (int) (tabWidth * (i + 1)));
        }
        return tabStops;
    }

    /**
     * Appends a paragraph of text at the specified y location and returns
     * the bounds of the paragraph.
     * @return Returns the actual bounds of the paragraph.
     */

    @FeatureEntryPoint(value="TextAreaWrite-AppendParagraph")
    public Rectangle2D.Double appendParagraph(Paragraph paragraph) {
       // locations of tabs in text
        int[] tabLocations = stylizeTextTabs(paragraph);
        // iterator for breaks in text layout for paragraph
        LineBreakMeasurer measurer = new LineBreakMeasurer(paragraph.styledText, getFontRenderContext());
        // counter for curent text segment
        int currentTab = 0;

        // draw each text line segment in a paragraph
        while (measurer.getPosition() < paragraph.styledText.getEndIndex()) {
            ParagraphLine paragraphLine = new ParagraphLine(paragraph);
            // draw paragraph layout
            while (!paragraphLine.isComplete) {
                // iterate through the contents of the line
                float wrappingWidth = paragraph.rightMargin - paragraphLine.horizontalPos;
                paragraphLine.textLayout = measurer.nextLayout(wrappingWidth,
                        tabLocations[currentTab] + 1, paragraphLine.containsText);
                // check if Line contains any text, and draw text for this line
                drawTextLayoutForLine(paragraphLine);
                // check if line contains any tabs
                boolean tabFound = measurer.getPosition() == tabLocations[currentTab] + 1;
                if (tabFound) { currentTab++;}
                // check if ParagraphLine is complete
                breakLoopIfLineIsComplete(paragraph, paragraphLine, measurer);
                // iterates through all tabs to adjust line position in paragraph
                adjustParagraphLinePosition(paragraph, paragraphLine);
            }
            // set paragraph vertical position after all lines are complete
            paragraph.verticalPos += paragraphLine.maxAscent;
            // break loop when paragraph is complete
            boolean endOfParagraphReached= paragraph.verticalPos > paragraph.maxVerticalPos;
            if (endOfParagraphReached) { break; }
            // set the final paragraph position
            drawParagraphBounds(paragraph, paragraphLine);
        }
        return paragraph.paragraphBounds;
    }

    private void breakLoopIfLineIsComplete(Paragraph paragraph, ParagraphLine paragraphLine, LineBreakMeasurer measurer) {
        boolean lineTextEndReached = measurer.getPosition() == paragraph.styledText.getEndIndex();
        boolean endBoundryOfLineReached = paragraph.tabStops.length == 0 || paragraphLine.horizontalPos >= paragraph.tabStops[paragraph.tabStops.length - 1];

        if (lineTextEndReached) { paragraphLine.isComplete = true;}
        else if (endBoundryOfLineReached) {
            paragraphLine.isComplete = true;
        }
    }

    private void drawParagraphBounds(Paragraph paragraph, ParagraphLine paragraphLine) {
        Iterator<TextLayout> layoutEnum = paragraphLine.layouts.iterator();
        Iterator<Float> positionEnum = paragraphLine.penPositions.iterator();
        drawLayouts(layoutEnum,positionEnum,paragraph.verticalPos,paragraph.shape,paragraph.paragraphBounds);
        paragraph.verticalPos += paragraphLine.maxDescent;
    }

    private void adjustParagraphLinePosition(Paragraph paragraph, ParagraphLine paragraphLine) {
        // check if ParagraphLine is complete and therefore has no tabs
        if (!paragraphLine.isComplete) {
            // move to next tab stop in the line
            int j;
            for (j = 0; paragraphLine.horizontalPos >= paragraph.tabStops[j]; j++) {
            }
            paragraphLine.horizontalPos = paragraph.tabStops[j];
        }
    }

    private void drawTextLayoutForLine(ParagraphLine paragraphLine) {
        // check if line is empty and therefore already complete
        if(paragraphLine.textLayout == null){
            paragraphLine.isComplete = true;
        }else {
            paragraphLine.layouts.add(paragraphLine.textLayout);
            paragraphLine.penPositions.add(paragraphLine.horizontalPos);
            paragraphLine.horizontalPos += paragraphLine.textLayout.getAdvance();
            paragraphLine.maxAscent = Math.max(paragraphLine.maxAscent, paragraphLine.textLayout.getAscent());
            paragraphLine.maxDescent = Math.max(paragraphLine.maxDescent, paragraphLine.textLayout.getDescent() + paragraphLine.textLayout.getLeading());
        }
        paragraphLine.containsText = true;
    }

    public int[] stylizeTextTabs(Paragraph paragraph) {
        // get tab locations from a paragraph
        int[] tabLocations = new int[paragraph.tabCount + 1];
        int i = 0;
        for (char c = paragraph.styledText.first();
             c != CharacterIterator.DONE; c = paragraph.styledText.next()) {
            if (c == '\t') {
                tabLocations[i++] = paragraph.styledText.getIndex();
            }
        }
        tabLocations[paragraph.tabCount] = paragraph.styledText.getEndIndex() - 1;
        // For convenience, the last entry
        // is tabLocations is the offset of the last character in the text.
        return  tabLocations;
    }

    //Extracted method draw Layouts, to reduce loops in loops
    public void drawLayouts(Iterator<TextLayout>layoutEnum,
                            Iterator<Float> positionEnum, float verticalPos,
                            Path2D.Double shape,Rectangle2D.Double paragraphBounds){
        while (layoutEnum.hasNext()) {
            TextLayout nextLayout = layoutEnum.next();
            float nextPosition = positionEnum.next();
            AffineTransform tx = new AffineTransform();
            tx.translate(nextPosition, verticalPos);
            if (shape != null) {
                Shape outline = nextLayout.getOutline(tx);
                shape.append(outline, false);
            }
            Rectangle2D layoutBounds = nextLayout.getBounds();
            paragraphBounds.add(new Rectangle2D.Double(layoutBounds.getX() + nextPosition,
                    layoutBounds.getY() + verticalPos,
                    layoutBounds.getWidth(),
                    layoutBounds.getHeight()));
        }
    }


    @Override
    public void setBounds(Point2D.Double anchor, Point2D.Double lead) {
        bounds.x = Math.min(anchor.x, lead.x);
        bounds.y = Math.min(anchor.y, lead.y);
        bounds.width = Math.max(0.1, Math.abs(lead.x - anchor.x));
        bounds.height = Math.max(0.1, Math.abs(lead.y - anchor.y));
        invalidate();
    }

    /**
     * Transforms the figure.
     *
     * @param tx the transformation.
     */
    @FeatureEntryPoint(value="TextAreaEdit - Transform")
    @Override
    public void transform(AffineTransform tx) {
        if (get(TRANSFORM) != null
                || (tx.getType()
                & (AffineTransform.TYPE_TRANSLATION /*| AffineTransform.TYPE_MASK_SCALE*/))
                != tx.getType()) {
            if (get(TRANSFORM) == null) {
                set(TRANSFORM, (AffineTransform) tx.clone());
            } else {
                AffineTransform t = TRANSFORM.getClone(this);
                t.preConcatenate(tx);
                set(TRANSFORM, t);
            }
        } else {
            Point2D.Double anchor = getStartPoint();
            Point2D.Double lead = getEndPoint();
            setBounds(
                    (Point2D.Double) tx.transform(anchor, anchor),
                    (Point2D.Double) tx.transform(lead, lead));
            if (get(FILL_GRADIENT) != null
                    && !get(FILL_GRADIENT).isRelativeToFigureBounds()) {
                Gradient g = FILL_GRADIENT.getClone(this);
                g.transform(tx);
                set(FILL_GRADIENT, g);
            }
            if (get(STROKE_GRADIENT) != null
                    && !get(STROKE_GRADIENT).isRelativeToFigureBounds()) {
                Gradient g = STROKE_GRADIENT.getClone(this);
                g.transform(tx);
                set(STROKE_GRADIENT, g);
            }
        }
        invalidate();
    }


    @Override
    public void restoreTransformTo(Object geometry) {
        Object[] restoreData = (Object[]) geometry;
        bounds = (Rectangle2D.Double) ((Rectangle2D.Double) restoreData[0]).clone();
        TRANSFORM.setClone(this, (AffineTransform) restoreData[1]);
        FILL_GRADIENT.setClone(this, (Gradient) restoreData[2]);
        STROKE_GRADIENT.setClone(this, (Gradient) restoreData[3]);
        invalidate();
    }


    @Override
    public Object getTransformRestoreData() {
        return new Object[]{
                bounds.clone(),
                TRANSFORM.getClone(this),
                FILL_GRADIENT.getClone(this),
                STROKE_GRADIENT.getClone(this)};
    }
// ATTRIBUTES

    @Override
    public String getText() {
        return get(TEXT);
    }

    @Override
    public int getTextColumns() {
        return (getText() == null) ? 4 : Math.max(getText().length(), 4);
    }

    @Override
    public <T> void set(AttributeKey<T> key, T newValue) {
        if (key.equals(SVGAttributeKeys.TRANSFORM)
                || key.equals(SVGAttributeKeys.FONT_FACE)
                || key.equals(SVGAttributeKeys.FONT_BOLD)
                || key.equals(SVGAttributeKeys.FONT_ITALIC)
                || key.equals(SVGAttributeKeys.FONT_SIZE)
                || key.equals(SVGAttributeKeys.STROKE_WIDTH)
                || key.equals(SVGAttributeKeys.STROKE_COLOR)
                || key.equals(SVGAttributeKeys.STROKE_GRADIENT)) {
            invalidate();
        }
        super.set(key, newValue);
    }

    /**
     * Sets the text shown by the text figure.
     */

    @Override
    public void setText(String newText) {
        set(TEXT, newText);
    }

    /**
     * Returns the insets used to draw text.
     */
    @Override
    public Insets2D.Double getInsets() {
        double sw = (get(STROKE_COLOR) == null) ? 0 : Math.ceil(get(STROKE_WIDTH) / 2);
        Insets2D.Double insets = new Insets2D.Double(0, 0, 0, 0);
        return new Insets2D.Double(insets.top + sw, insets.left + sw, insets.bottom + sw, insets.right + sw);
    }

    @Override
    public double getBaseline() {
        return getFont().getLineMetrics(getText(), getFontRenderContext()).getAscent() + getInsets().top;
    }

    @Override
    public int getTabSize() {
        return 8;
    }

    @Override
    public TextHolderFigure getLabelFor() {
        return this;
    }

    @Override
    public Font getFont() {
        return SVGAttributeKeys.getFont(this);
    }

    @Override
    public Color getTextColor() {
        return get(FILL_COLOR);
        //   return TEXT_COLOR.get(this);
    }

    @Override
    public Color getFillColor() {
        return get(FILL_COLOR).equals(Color.white) ? Color.black : Color.WHITE;
    }

    @Override
    public void setFontSize(float size) {
        Point2D.Double p = new Point2D.Double(0, size);
        AffineTransform tx = get(TRANSFORM);
        if (tx != null) {
            try {
                tx.inverseTransform(p, p);
                Point2D.Double p0 = new Point2D.Double(0, 0);
                tx.inverseTransform(p0, p0);
                p.y -= p0.y;
            } catch (NoninvertibleTransformException ex) {
                ex.printStackTrace();
            }
        }
        set(FONT_SIZE, Math.abs(p.y));
    }

    @Override
    public float getFontSize() {
        Point2D.Double p = new Point2D.Double(0, get(FONT_SIZE));
        AffineTransform tx = get(TRANSFORM);
        if (tx != null) {
            tx.transform(p, p);
            Point2D.Double p0 = new Point2D.Double(0, 0);
            tx.transform(p0, p0);
            p.y -= p0.y;
            /*
        try {
        tx.inverseTransform(p, p);
        } catch (NoninvertibleTransformException ex) {
        ex.printStackTrace();
        }*/
        }
        return (float) Math.abs(p.y);
    }
// EDITING

    @Override
    public boolean isEditable() {
        return editable;
    }

    public boolean containsEditableText() {
        return (getText() != null || isEditable());
    }


    public void setEditable(boolean b) {
        this.editable = b;
    }

    @Override
    public Collection<Handle> createHandles(int detailLevel) {
        LinkedList<Handle> handles = new LinkedList<Handle>();
        switch (detailLevel % 2) {
            case -1: // Mouse hover handles
                handles.add(new BoundsOutlineHandle(this, false, true));
                break;
            case 0:
                ResizeHandleKit.addResizeHandles(this, handles);
                handles.add(new FontSizeHandle(this));
                handles.add(new TextOverflowHandle(this));
                handles.add(new LinkHandle(this));
                break;
            case 1:
                TransformHandleKit.addTransformHandles(this, handles);
                break;
            default:
                break;
        }
        return handles;
    }

    /**
     * Returns a specialized tool for the given coordinate.
     * <p>
     * Returns null, if no specialized tool is available.
     */
    @Override
    public Tool getTool(Point2D.Double p) {
        if (isEditable() && contains(p)) {
            TextAreaEditingTool tool = new TextAreaEditingTool(this);
            return tool;
        }
        return null;
    }
// CONNECTING
// COMPOSITE FIGURES
// CLONING
// EVENT HANDLING

    /**
     * Gets the text shown by the text figure.
     */
    @Override
    public boolean isEmpty() {
        return getText() == null || getText().length() == 0;
    }

    @Override
    public void invalidate() {
        super.invalidate();
        cachedDrawingArea = null;
        cachedTextShape = null;
        isTextOverflow = null;
    }

    @Override
    public boolean isTextOverflow() {
        if (isTextOverflow == null) {
            Insets2D.Double insets = getInsets();
            isTextOverflow = getPreferredTextSize(getBounds().width - insets.left - insets.right).height > getBounds().height - insets.top - insets.bottom;
        }
        return isTextOverflow;
    }

    /**
     * Returns the preferred text size of the TextAreaFigure.
     * <p>
     * If you want to use this method to determine the bounds of the TextAreaFigure,
     * you need to add the insets of the TextAreaFigure to the size.
     *
     * @param maxWidth the maximal width to use. Specify Double.MAX_VALUE
     * if you want the width to be unlimited.
     * @return width and height needed to lay out the text.
     */

    public Dimension2DDouble getPreferredTextSize(double maxWidth) {
        Rectangle2D.Double textRect = new Rectangle2D.Double();
        if (getText() != null) {
            Font font = getFont();
            boolean isUnderlined = get(FONT_UNDERLINE);
            TextBox textBox = new TextBox(0,(float) maxWidth - 1,0);

            if (textBox.hasInnerArea) {
                float [] tabStops = findTabs(textBox);

                if (getText() != null) {
                    String[] paragraphs = getText().split("\n"); //Strings.split(getText(), '\n');
                    for (int i = 0; i < paragraphs.length; i++) {
                        if (paragraphs[i].length() == 0) {
                            paragraphs[i] = " ";
                        }
                        AttributedString as = new AttributedString(paragraphs[i]);
                        as.addAttribute(TextAttribute.FONT, font);
                        if (isUnderlined) {
                            as.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_ONE_PIXEL);
                        }
                        int tabCount = paragraphs[i].split("\t").length - 1;
                        Paragraph paragraph = new Paragraph(null, as.getIterator(), textBox, tabStops, tabCount);
                        Rectangle2D.Double paragraphBounds = appendParagraph(paragraph);
                        textBox.verticalPos = (float) (paragraphBounds.y + paragraphBounds.height);
                        textRect.add(paragraphBounds);
                    }
                }
            }
        }
        return new Dimension2DDouble(Math.abs(textRect.x) + textRect.width, Math.abs(textRect.y) + textRect.height);
    }
    @Override
    public SVGTextAreaFigure clone() {
        SVGTextAreaFigure that = (SVGTextAreaFigure) super.clone();
        that.bounds = (Rectangle2D.Double) this.bounds.clone();
        return that;
    }
    public class TextBox {
        private Rectangle2D.Double textRect;
        private float leftMargin;
        private float rightMargin;
        private float verticalPos;
        private float maxVerticalPos;
        private boolean hasInnerArea;
        public TextBox() {
            this.textRect = createNewTextRectangle();
            this.leftMargin = (float) textRect.x;
            this.rightMargin = (float) Math.max(leftMargin + 1, textRect.x + textRect.width);
            this.verticalPos = (float) textRect.y;
            this.maxVerticalPos = (float) (textRect.y + textRect.height);
            this.hasInnerArea = (leftMargin < rightMargin);
        }
        public TextBox(int leftMargin, float rightMargin, int  verticalPos) {
            this.textRect = createNewTextRectangle();
            this.leftMargin = (float) leftMargin;
            this.rightMargin = rightMargin;
            this.verticalPos = (float) verticalPos;
            this.maxVerticalPos = Float.MAX_VALUE;
            this.hasInnerArea = (leftMargin < rightMargin);
        }

        //getters
        public float getLeftMargin() { return leftMargin; }
        public float getRightMargin() { return rightMargin; }
        public float getVerticalPos() { return verticalPos; }
        public float getMaxVerticalPos() { return maxVerticalPos; }

    }
    public Rectangle2D.Double createNewTextRectangle(){
        SVGTextAreaFigure svgTextAreaFigure = new SVGTextAreaFigure();
        Insets2D.Double insets = svgTextAreaFigure.getInsets();
        Rectangle2D.Double textRect = new Rectangle2D.Double(
                bounds.x + insets.left,
                bounds.y + insets.top,
                bounds.width - insets.left - insets.right,
                bounds.height - insets.top - insets.bottom);
        return textRect;
    }

    public static class Paragraph {
        private Path2D.Double shape;
        AttributedCharacterIterator styledText;
        private float verticalPos;
        private float maxVerticalPos;
        private float leftMargin;
        private  float rightMargin;
        private float[] tabStops;
        private int tabCount;
        private TextBox textBox;
        private Rectangle2D.Double paragraphBounds;
        public Paragraph(Path2D.Double shape, AttributedCharacterIterator styledText,
                         TextBox textBox, float[] tabStops, int tabCount) {
            this.shape = shape;
            this.textBox = textBox;
            this.styledText = styledText;
            this.verticalPos = textBox.getVerticalPos();
            this.maxVerticalPos = textBox.getMaxVerticalPos();
            this.leftMargin = textBox.getLeftMargin();
            this.rightMargin = textBox.getRightMargin();
            this.tabStops = tabStops;
            this.tabCount = tabCount;
            this.paragraphBounds =  new Rectangle2D.Double(this.leftMargin, this.verticalPos, 0, 0);
        }

        @Override
        public String toString() {
            return "Paragraph{" +
                    "shape=" + shape +
                    ", styledText=" + styledText.toString() +
                    ", verticalPos=" + verticalPos +
                    ", maxVerticalPos=" + maxVerticalPos +
                    ", leftMargin=" + leftMargin +
                    ", rightMargin=" + rightMargin +
                    ", tabStops=" + Arrays.toString(tabStops) +
                    ", tabCount=" + tabCount +
                    ", textBoxLeftMargin=" + textBox.getLeftMargin() +
                    ", textBoxRightMargin=" +textBox.getRightMargin() +
                    ", textMaxVertical=" +textBox.getMaxVerticalPos()+
                    ", textVerticalPos=" +textBox.getVerticalPos()+
                    ", paragraphBounds=" + paragraphBounds +
                    '}';
        }

        // getters and setters
        public float[] getTabStops() { return tabStops; }
        public void setTabStops(float[] tabStops) { this.tabStops = tabStops;}
        public int getTabCount() { return tabCount;}
        public void setTabCount(int tabCount) { this.tabCount = tabCount; }
    }
    public class ParagraphLine{
        private boolean containsText;
        private boolean isComplete;
        private TextLayout textLayout;
        private float maxAscent;
        private float maxDescent;
        private float horizontalPos;
        private LinkedList<TextLayout> layouts;
        private LinkedList<Float> penPositions;

        public ParagraphLine(Paragraph paragraph) {
            this.containsText = false;
            this.isComplete = false;
            this.textLayout = null;
            this.maxAscent = 0;
            this.maxDescent = 0;
            this.horizontalPos = paragraph.leftMargin;
            this.layouts = new LinkedList<TextLayout>();
            this.penPositions = new LinkedList<Float>();
        }
    }
}
