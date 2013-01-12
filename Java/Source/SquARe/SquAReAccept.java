/**
public class SquAReAccept extends MouseMotionAdaptor

computes the acceptance region for a parameter located at the
cursor every time the mouse moves, for the Squ(are) A(cceptance)
Re(gion) non-equivariant confidence region

@author Y. Benjamini benja@math.tau.ac.il and P.B. Stark stark@stat.berkeley.edu
@version 0.1
*/

//package square;

import java.awt.*;
import java.awt.event.*;
import PbsStat.*;

public class SquAReAccept extends MouseMotionAdapter
{
    protected SquARePlot squarePlot;    // listen for MouseMotion from this
                                        // component
    protected double r;                 // max length ratio for acceptance region
    protected double alpha;             // significance level for tests
    protected double cAlpha;            // equivariant region half-length
    protected double dx = 0.005;        // increment for searching for
                                        // offset to get coverage probability
    protected double dr = 0.005;        // decrement to r if coverage is
                                        // too high using squares 2*r*cAlpha
                                        // on a side.

    protected Label sideLabel =
            new Label("01234567890123456789012345678901234567890");
                                        // to display the four side lengths
    protected Label probLabel =
            new Label("012345678901234567890");
                                        // display the acceptance prob.
    String[] sidePretext = {"N: ",
                            "S: ",
                            "E: ",
                            "W: ",
                            };
    protected int digits = 5;           // number of digits to display
                                        // on labels

    public SquAReAccept()
    {
        super();
    }

    public SquAReAccept(SquARePlot squarePlot)
    {
        super();
        setSquarePlot(squarePlot);
    }

    public double getMaxReach()
    {
//! This will bomb unless setAlpha has been called!
/*  returns an upper bound on how far the acceptance region can
    extend from the parameter in any direction.

    The maximum reach is attained when one component is centered
    in an interval of maximum width, 2*r*cAlpha, and the other is
    as asymmetric as possible s.t. prob = 1-alpha.

    The probability gotten by the centered interval is
    normCdf(r*cAlpha) - normCdf(-r*cAlpha) = 2*normCdf(r*cAlpha) - 1.
    That prob. times pmin must equal 1-alpha, so
    normCdf(maxReach) - normCdf(maxReach - 2*r*cAlpha) = pmin.
    This is solved by searching; the attained p is monotone decreasing
    as the interval becomes more asymmetric.
*/
        double pmin = (1.0 - alpha)/(2.0*PbsStat.normCdf(r*cAlpha) - 1.0);
        double maxReach = r*cAlpha; // start symmetric and move away
        double trca = 2*r*cAlpha;
        double pAttained = 2* PbsStat.normCdf(maxReach) - 1.0;
        while (pAttained > pmin && maxReach <= trca)
        {
            maxReach += dx;
            pAttained = PbsStat.normCdf(maxReach) -
                        PbsStat.normCdf(maxReach - trca);
        }
        return(maxReach);
    }


    public void setR(double r)
    {
        this.r = r;
    }

    public double getR()
    {
        return(r);
    }

    public void setDr(double dr)
    {
        this.dr = dr;
    }

    public double getDr()
    {
        return(dr);
    }

    public void setDx(double dx)
    {
        this.dx = dx;
    }

    public double getDx()
    {
        return(dx);
    }

    public void setAlpha(double alpha)
    {
        this.alpha = alpha;
        cAlpha = PbsStat.normInv((1.0 + Math.sqrt(1-alpha))/2.0);
    }

    public double getAlpha()
    {
        return(alpha);
    }

    public void setCAlpha(double cAlpha)
    {
        this.cAlpha = cAlpha;
    }

    public double getCAlpha()
    {
        return(cAlpha);
    }

    public Label getProbLabel()
    {
        return(probLabel);
    }

    public Label getSideLabel()
    {
        return(sideLabel);
    }

    public String[] getSidePretext()
    {
        return(sidePretext);
    }

    public void setSidePretext(String[] sidePretext)
    {
        this.sidePretext = sidePretext;
    }

    public void setDigits(int digits)
    {
        this.digits = digits;
    }

    public int getDigits()
    {
        return(digits);
    }


    public void setSquarePlot(SquARePlot squarePlot)
    {
        this.squarePlot = squarePlot;
        squarePlot.addMouseMotionListener(this);
    }

    public SquARePlot getSquarePlot()
    {
        return squarePlot;
    }

    public void mouseMoved(MouseEvent e)
    {
        double x = squarePlot.pixToX(e.getX());
        double y = squarePlot.pixToY(e.getY());

        squarePlot.setAccept(makeSquare(x,y, true));
        squarePlot.setCurrentPoint(new Point(e.getX(),e.getY()));
        squarePlot.setDCurrentPoint(x,y);
        squarePlot.repaint();
    }

    protected Rectangle makeSquare(double x, double y)
    {
        return(makeSquare(x, y, false));
    }

    protected Rectangle makeSquare(double x, double y, boolean relabel)
    {
        double xl, yl, xh, yh, covPr;
        boolean xPos = true, yPos = true, yBigger = true;
        if (x < 0.0)
        {
            xPos = false;
            x = Math.abs(x);
        }
        if (y < 0.0)
        {
            yPos = false;
            y = Math.abs(y);
        }
        if ((x >= cAlpha && y >= cAlpha) ||
                (x == 0.0 && y == 0.0)) // use the standard region
        {
            covPr = 1 - alpha;
            xl = x - cAlpha;
            yl = y - cAlpha;
            xh = x + cAlpha;
            yh = y + cAlpha;
        }
        else                            // touch the axis
        {
            if (x > y)
            {
                yBigger = false;
                double tmp = x;
                x = y;
                y = tmp;
            }
            double d = 0.0;
            double bLo = Math.min(y, r*cAlpha);
            covPr = (PbsStat.normCdf(2*r*cAlpha - x) -
                            PbsStat.normCdf(-x))*
                           (PbsStat.normCdf(2*r*cAlpha - bLo) -
                            PbsStat.normCdf(-bLo));
            if (covPr > 1-alpha)
            {                           // reduce rho until covPr=1-alpha
                                        // two cases: x = 0; x > 0.
                double rho = r;
                while (covPr > 1-alpha && rho >= 1.0)
                {
                    rho -= dr;
                    bLo = Math.min(y, rho*cAlpha);
                    covPr = (PbsStat.normCdf(2*rho*cAlpha - x) -
                             PbsStat.normCdf(-x))*
                            (PbsStat.normCdf(2*rho*cAlpha - bLo) -
                             PbsStat.normCdf(-bLo));
                }
                xl = 0;
                xh = 2*rho*cAlpha;
                yl = y - bLo;
                yh = y + 2*rho*cAlpha - bLo;
            }
            else
            {                           // increase d until covPr=1-alpha
                while (covPr < 1- alpha && d <= cAlpha)
                {
                    d += dx;
                    bLo = Math.min(y+d, r*cAlpha);
                    covPr = (PbsStat.normCdf(2*r*cAlpha - x - d) -
                             PbsStat.normCdf(-x-d))*
                            (PbsStat.normCdf(2*r*cAlpha - bLo) -
                             PbsStat.normCdf(-bLo));
                }
                xl = -d;
                yl = y - bLo;
                xh = 2*r*cAlpha - d;
                yh = y + 2*r*cAlpha - bLo;
            }
        }
 // restore original coordinate system
        if (!yBigger)           // exchange x and y
        {
            double tmp = yh;
            yh = xh;
            xh = tmp;
            tmp = yl;
            yl = xl;
            xl = tmp;
            tmp = x;
            x = y;
            y = tmp;
        }
        if ( !xPos)             // reflect the x-axis
        {
            double tmp = xh;
            xh = - xl;
            xl = - tmp;
            x = -x;
        }
        if (!yPos)              // reflect the y-axis
        {
            double tmp = yh;
            yh = - yl;
            yl = - tmp;
            y = -y;
        }

        if (relabel)
        {
// Set the labels
            sideLabel.setText(sidePretext[0]
                   + PbsStat.numToString(yh - y, digits) + "  "
                   + sidePretext[1]
                   + PbsStat.numToString(y - yl, digits) + "  "
                   + sidePretext[2]
                   + PbsStat.numToString(xh - x, digits) + "  "
                   + sidePretext[3]
                   + PbsStat.numToString(x - xl, digits)
                          );
            sideLabel.validate();
            probLabel.setText("Prob: " +
                    PbsStat.numToString(covPr,digits));
        }

        int xp = squarePlot.xToPix(xl);
        int yp = squarePlot.yToPix(yh);
        return(new Rectangle(xp, yp, squarePlot.xToPix(xh) - xp,
                squarePlot.yToPix(yl) - yp));
    }

    private double smallLo(double x)
    {
        return(Math.min(x, cAlpha));
    }

    private double smallHi(double x, double rho)
    {
        return(2*rho*cAlpha - smallLo(x));
    }

    private double largeLo(double y, double rho)
    {
        return(Math.min(y, rho*cAlpha));
    }

    private double largeHi(double y, double rho)
    {
        return(2*rho*cAlpha - largeLo(y, rho));
    }


}