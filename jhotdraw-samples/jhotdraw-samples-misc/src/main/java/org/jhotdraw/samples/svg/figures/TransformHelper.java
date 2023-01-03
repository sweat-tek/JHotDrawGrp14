package org.jhotdraw.samples.svg.figures;

import java.awt.geom.*;
import static org.jhotdraw.draw.AttributeKeys.TRANSFORM;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.samples.svg.Gradient;
import static org.jhotdraw.samples.svg.SVGAttributeKeys.*;

public class TransformHelper {
    public TransformHelper(){

    }
    public void transform(AffineTransform affineTransform, Figure fig){
        if (fig.get(TRANSFORM) != null || (affineTransform.getType() & (AffineTransform.TYPE_TRANSLATION)) != affineTransform.getType()) {
            if (fig.get(TRANSFORM) == null) {
                fig.set(TRANSFORM, (AffineTransform) affineTransform.clone());
            } else {
                AffineTransform transformer = TRANSFORM.getClone(fig);
                transformer.preConcatenate(affineTransform);
                fig.set(TRANSFORM, transformer);
            }
        } else {
            Point2D.Double startPoint = fig.getStartPoint();
            Point2D.Double endPoint = fig.getEndPoint();
            fig.setBounds(
                    (Point2D.Double) affineTransform.transform(startPoint, startPoint),
                    (Point2D.Double) affineTransform.transform(endPoint, endPoint));
            if (fig.get(FILL_GRADIENT) != null
                    && !fig.get(FILL_GRADIENT).isRelativeToFigureBounds()) {
                Gradient gradient = FILL_GRADIENT.getClone(fig);
                gradient.transform(affineTransform);
                fig.set(FILL_GRADIENT, gradient);
            }
            if (fig.get(STROKE_GRADIENT) != null
                    && !fig.get(STROKE_GRADIENT).isRelativeToFigureBounds()) {
                Gradient gradient = STROKE_GRADIENT.getClone(fig);
                gradient.transform(affineTransform);
                fig.set(STROKE_GRADIENT, gradient);
            }
        }
    }
}
