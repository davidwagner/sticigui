/**
public class SquARePlot extends XYCanvas
Graphics object for SquARe
@author Y. Benjamini benja@math.tau.ac.il and P.B. Stark stark@stat.berkeley.edu
@version 0.1

Relies on a MouseMotionListener to set the acceptance rectangle
_accept_ as the mouse moves.

Relies on a MouseListener to calculate the confidence region when the
mouse is clicked.

*/
//package square;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

//import pbs.pbsStat.*;

public class SquARePlot extends XYCanvas
{
    public final Dimension minSize = new Dimension(120,150);
    public final Dimension prefSize = minSize;

    protected Point datum = new Point();       // ref pt in pixels
    protected double[] dDatum = new double[2]; // ref pt in real units
    protected Point currentPoint;               // mouse point in pixels
    protected double[] dCurrentPoint = new double[2];
                                                // mouse point in real coords
    protected Point[] testPoints;               // points tested
    protected Rectangle accept;                 // acceptance region for currentPoint,
                                                // pixels
    protected Rectangle lastAccept;             // the last accept rectangle;
                                                // used to clip the graphics intelligently
    protected double[] dAccept;                 // accept in real units
    protected double[][] dCR;                   // confidence region in real coords

    protected Color acceptColor = Color.green;  // color accept if contains datum
    protected Color rejectColor = Color.red;    // color accept if not contains datum
    protected Color datumColor = Color.blue;
                                                // color of the point at the cursor
    protected Color confidenceColor = Color.yellow;
                                                // color for confidence region
    protected Font labelFont =
        new Font("TimesRoman", Font.PLAIN, 9);  // axis label font
    protected FontMetrics labelFM;              // fontmetrics for label font

    private int n;                              // number of points in the scatterplot
    private final int dotSize = 4;              // the size of the dots to plot
    private final int nTics = 11;               // number of tics on the axes
    public SquARePlot()
    {
        this(-1, 1, -1, 1, 0, 0);
    }

    public SquARePlot(double xLo, double xHi, double yLo, double yHi,
           double xc, double yc)
    {
        super();
        this.xLo = xLo;
        this.xHi = xHi;
        this.yLo = yLo;
        this.yHi = yHi;
        dDatum[0] = xc;
        dDatum[1] = yc;

        labelFM = getFontMetrics(labelFont);
        yOffset = labelFM.getAscent();

        setScale(size());
        datum.x = xToPix(dDatum[0]);
        datum.y = yToPix(dDatum[1]);
        lastAccept = new Rectangle(0,0,size().width,size().height);
        accept = lastAccept;
//        addMouseListener(this);
    } // ends SquARePlot(double, double, double, double, double, double);

    public void redo()
    {
        lastAccept = new Rectangle(0, 0, size().width, size().height);
        repaint();
    }

    public void update(Graphics g)
    {
        Rectangle clipRect = new Rectangle();
        clipRect.x = Math.min(accept.x, lastAccept.x) - 3;
        clipRect.y = Math.min(accept.y, lastAccept.y) - 3;
        clipRect.width = Math.max(accept.x + accept.width,
                lastAccept.x + lastAccept.width) - clipRect.x + 6;
        clipRect.height = Math.max(accept.y + accept.height,
                lastAccept.y + lastAccept.height) - clipRect.y + 6;
        g.setClip(clipRect);
        g.clearRect(clipRect.x, clipRect.y,
            clipRect.width, clipRect.height);
        lastAccept = accept;
        paint(g);
    }

    public void paint(Graphics g)
    {

        setScale(size());
        datum.x = xToPix(dDatum[0]);
        datum.y = yToPix(dDatum[1]);
        if (dCR != null)  // there's a confidence region
        {
            g.setColor(confidenceColor);
            g.fillRect(xToPix(dCR[0][0]), yToPix(dCR[0][1]),
                        (int) (xScale*dCR[0][2]),
                        (int) (yScale*dCR[0][3]));
            int nPoints = dCR.length - 1;
            int[] xpts = new int[2*nPoints]; // double the number of points
            int[] ypts = new int[2*nPoints]; // to have room for top & bottom
            for (int i = 0; i < nPoints; i++)
            {
                xpts[i] = xToPix(dCR[i+1][0]);
                xpts[2*nPoints - i - 1] = xpts[i];
                ypts[i] = yToPix(dCR[i+1][1]);
                ypts[2*nPoints - i - 1] = yToPix(dCR[i+1][2]);
            }
            g.setColor(Color.black);
            g.drawPolygon(xpts, ypts, 2*nPoints);

        }

        g.setColor(datumColor);
        g.fillOval(datum.x - dotSize/2,
            datum.y - dotSize/2, dotSize, dotSize);

        if (accept.contains(datum))
        {
            g.setColor(acceptColor);
        }
        else
        {
            g.setColor(rejectColor);
        }
        g.drawOval(xToPix(dCurrentPoint[0]) - dotSize/2,
            yToPix(dCurrentPoint[1]) - dotSize/2, dotSize, dotSize);

        g.drawRect(accept.x, accept.y, accept.width, accept.height);


// let's draw some axes!
        g.setColor(Color.black);
        g.setFont(labelFont);
        int x0 = xToPix(Math.max(0,xLo));
        int y0 = yToPix(Math.max(0,yLo));
        g.drawLine(x0, yToPix(yHi), x0, yToPix(yLo));
        g.drawLine(xToPix(xLo), y0, xToPix(xHi), y0);
// axis labels
        String lbl;
        int wide;
        int nChars;
        int xh;
        int yh;
        for (int i = 0; i < nTics; i++)
        {
            double xHere = xLo + i*(xHi - xLo)/(nTics - 1);
            int dig = (int) (Math.floor( Math.log( Math.abs(xHere))
                             /Math.log( 10 )));
            if (dig < 0) dig = 1;
            lbl = ("" + xHere);
            nChars = lbl.length();
            if (xHere > 0) lbl = lbl.substring(0, Math.min(dig+3,nChars));
            else lbl = lbl.substring(0, Math.min(dig+4, nChars));
            wide = labelFM.stringWidth(lbl);
            xh = xToPix(xHere);
            g.drawString(lbl, xh - wide/2, y0 + yOffset);
            g.drawLine( xh, y0 - 3, xh, y0 + 3);
        }
        for (int i = 0; i < nTics; i++)
        {
            double yHere = yLo + i*(yHi - yLo)/(nTics - 1);
            int dig = (int) (Math.floor( Math.log( Math.abs(yHere))
                             /Math.log( 10 )));
            if (dig < 1 ) dig = 1;
            lbl = "" + yHere;
            nChars = lbl.length();
            if (yHere > 0) lbl = lbl.substring(0, Math.min(dig+3, nChars));
            else lbl = lbl.substring(0, Math.min(dig+4, nChars));
            wide = labelFM.stringWidth(lbl);
            yh = yToPix(yHere);
            g.drawString(lbl, x0 - wide - 4,
                         yh + yOffset/2);
            g.drawLine( x0 - 3, yh, x0 + 3, yh);
        }


    } // ends paint

    public void setAcceptColor(Color c)
    {
        acceptColor = c;
    }

    public Color getAcceptColor()
    {
        return(acceptColor);
    }

    public Color getRejectColor()
    {
        return(rejectColor);
    }

    public void setRejectColor(Color c)
    {
        rejectColor = c;
    }

    public void setConfidenceColor(Color c)
    {
        confidenceColor = c;
    }

    public Color getConfidenceColor()
    {
        return(confidenceColor);
    }

    public Point getCurrentPoint()
    {
        return(currentPoint);
    }

    public void setCurrentPoint(Point p)
    {
        currentPoint = p;
    }

    public double[] getDCurrentPoint()
    {
        return(dCurrentPoint);
    }

    public void setDatum(Point p)
    {
        datum = p;
    }

    public Point getDatum()
    {
        return(datum);
    }

    public void setDDatum(double[] x)
    {
        dDatum = x;
    }

    public double[] getDDatum()
    {
        return(dDatum);
    }

    public void setDCurrentPoint(double x, double y)
    {
        dCurrentPoint[0] = x;
        dCurrentPoint[1] = y;
    }

    public void setDCR(double[][] dCR)
    {
        this.dCR = dCR;
    }

    public double[][] getDCR()
    {
        return(dCR);
    }

    public Rectangle getAccept()
    {
        return(accept);
    }

    public void setAccept(Rectangle r)
    {
        accept = r;
    }

    public Dimension preferredSize()
    {
        return(prefSize);
    }

    public Dimension minimumSize()
    {
        return(minSize);
    }

/*    public void mouseClicked(MouseEvent e)
    {
        setScale(size());
        datum.x = e.getX();
        datum.y = e.getY();
        dDatum[0] = pixToX(datum.x);
        dDatum[1] = pixToY(datum.y);
        repaint();
    }


    public void mouseEntered(MouseEvent e) { }

    public void mouseExited(MouseEvent e) { }

    public void mousePressed(MouseEvent e) { }

    public void mouseReleased(MouseEvent e) { }
*/
}


