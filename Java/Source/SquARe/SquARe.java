/**
public class SquARe extends Applet

A gui-based application to study non-equivariant confidence intervals
obtained by inverting tests with Squ(are) A(cceptance) Re(gions) whose
centers and side-lengths vary non-equivariantly.

@author Y. Benjamini benja@math.tau.ac.il and P.B. Stark stark@stat.berkeley.edu
@version 0.1
*/

//package square;

import java.awt.*;
import java.awt.event.*;
import java.applet.*;
//import pbs.pbsGui.*;

public class SquARe extends Applet
{
    public Font titleFont = new Font("Helvetica",Font.PLAIN,14);
//protected TextBar alphaBar;
//protected TextBar rBar;


    protected double r = 1.20;
    protected double alpha = 0.05;

    protected int xLoLim = -10;
    protected int yLoLim = -10;
    protected int xHiLim = 10;
    protected int yHiLim = 10;

    protected SquAReAccept squareAccept;
    protected SquAReCR squareCR;
    protected XYCoords xycoords;

    protected SquARePlot squarePlot;

    protected Label xlabel;
    protected Label ylabel;

    public void init() {

        super.init();
/*        alphaBar = new TextBar(alpha,0.001,0.2,6,
                    new DoubleValidator(),
                    TextBar.TEXT_FIRST," alpha:");
        rBar = new TextBar(r,1,5,4,
                    new DoubleValidator(),
                    TextBar.TEXT_FIRST," r: "); */
        setLayout(new BorderLayout());
        Label titleLabel = new Label("SquARe");
        Panel titPan = new Panel();
        titPan.setFont(titleFont);
        titPan.add(titleLabel);
        add("North",titPan);

// make the exotic components
        squarePlot = new SquARePlot();
        squareAccept = new SquAReAccept(squarePlot);
        squareCR = new SquAReCR(squarePlot, squareAccept);
        xycoords = new XYCoords((XYComponent) squarePlot);
//

        Panel sqPan = new Panel();
        sqPan.setLayout(new BorderLayout());
        sqPan.add("Center",squarePlot);

        add("Center",sqPan);
        Panel contPan = new Panel();
/*      contPan.add(alphaBar);
        contPan.add(rBar);
*/
        contPan.add(xycoords.getXLabel());
        contPan.add(xycoords.getYLabel());
        contPan.add(squareAccept.getSideLabel());
        contPan.add(squareAccept.getProbLabel());
        add("South",contPan);
        squareAccept.setR(r);
        squareAccept.setAlpha(alpha);
        squarePlot.setAxes(xLoLim, xHiLim, yLoLim, yHiLim);
    }
/*   public boolean handleEvent(Event event) {
        if (event.id = Event.ACTION_EVENT )
        {
            if (event.target == rBar)
            {
                r = rBar.getValue();
                squarePlot.setR(r);
            }
            else if (event.target == alphaBar)
            {
                alpha = alphaBar.getValue();
                squarePlot.setAlpha(alpha);
            }
        }
        return super.handleEvent(event);
    }
*/

}
