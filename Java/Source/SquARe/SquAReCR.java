/**
public class SquAReCR extends MouseAdaptor

computes the confidence region for a parameter located at the
point where the mouse is clicked, for the Squ(are) A(cceptance)
Re(gion) non-equivariant confidence region

@author Y. Benjamini benja@math.tau.ac.il and P.B. Stark stark@stat.berkeley.edu
@version 0.1
*/

//package square;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class SquAReCR extends MouseAdapter
{
    protected SquARePlot squarePlot;    // listen for MouseEvent from this
                                        // component
    protected SquAReAccept squareAccept;// this gives the acceptance regions
                                        // and various defined parameters
    protected Point datum = new Point();
                                        // pixel coords of the datum
    protected double[] dDatum = new double[2];
                                        // real coords of the datum
    protected Rectangle convex;         // pixel rectangle of convex hull
                                        // of confidence region
    protected double[] dConvex = new double[4];
                                        // real confidence rectangle,
                                        // stored in same format as a Jasva
                                        // rectangle:  x coord of upper left corner,
                                        // y coord of upper left corner,
                                        // width, height
    protected double dx = 0.1;        // increment for searching for
                                        // x limits of confidence set
    protected double dy = 0.1;        // increment for searching for
                                        // y limits of confidence set


    public SquAReCR()
    {
        super();
    }

    public SquAReCR(SquARePlot squarePlot, SquAReAccept squareAccept)
    {
        super();
        setSquarePlot(squarePlot);
        this.squareAccept = squareAccept;
    }

    public SquAReCR(SquARePlot squarePlot)
    {
        this(squarePlot, null);
    }

    public void setSquarePlot(SquARePlot squarePlot)
    {
        this.squarePlot = squarePlot;
        squarePlot.addMouseListener(this);
    }

    public void setSquareAccept(SquAReAccept squareAccept)
    {
        this.squareAccept = squareAccept;
    }

    public SquAReAccept getSquareAccept()
    {
        return(squareAccept);
    }

    public SquARePlot getSquarePlot()
    {
        return squarePlot;
    }

    public void mouseClicked(MouseEvent e)
    {
        datum.x = e.getX();
        datum.y = e.getY();
        dDatum[0] = squarePlot.pixToX(datum.x);
        dDatum[1] = squarePlot.pixToY(datum.y);
        squarePlot.setDDatum(dDatum);           // set the "real" coords of
                                                // the datum
        squarePlot.setDCR(makeDCRect());
        squarePlot.redo();
    }

    protected double[][] makeDCRect()
    {
        double r = squareAccept.getR();
        double cAlpha = squareAccept.getCAlpha();
        double maxReach = squareAccept.getMaxReach();
        double xmin = dDatum[0] + maxReach;
        double xmax = dDatum[0] - maxReach;
        double ymin = dDatum[1] + maxReach;
        double ymax = dDatum[1] - maxReach;
        Vector v = new Vector(100);
        double[] thisX;

        for (double xt = dDatum[0] -maxReach;
                    xt<= dDatum[0] + maxReach; xt+=dx)
        {
            for (double yt = dDatum[1] - maxReach;
                    yt <= dDatum[1] + maxReach; yt += dy)
            {
                if(squareAccept.makeSquare(xt, yt).contains(datum))
                {
                    thisX = new double[3];
                    thisX[0] = xt;
                    thisX[1] = yt;
                    for (double ytop = dDatum[1] + maxReach;
                            ytop >= yt; ytop -= dy)
                    {
                        if(squareAccept.makeSquare(xt, ytop).contains(datum))
                        {
                            thisX[2] = ytop;
                            break;
                        }
                    }
                    v.addElement(thisX);
                    xmin = Math.min(xmin, xt);
                    xmax = Math.max(xmax, xt);
                    ymin = Math.min(ymin, yt);
                    ymax = Math.max(ymax, thisX[2]);
                    break;
                }
            }
        }
        v.trimToSize();
        double[][] dCR = new double[1+v.size()][];
        dCR[0] = new double[4];
        dCR[0][0] = xmin;
        dCR[0][1] = ymax;
        dCR[0][2] = xmax - xmin;
        dCR[0][3] = ymax - ymin;
        for (int i = 1; i <= v.size(); i++)  // convert the vector to double[]
        {
            dCR[i] = new double[3];
            dCR[i] = (double[]) v.elementAt(i-1);
        }
        return(dCR);
    }



}