/** Class Venn
@author Philip B. Stark http://statistics.berkeley.edu/~stark/
@version 1.0
@copyright 1977-2001, P.B. Stark. All rights reserved.
Last modified 14 March 2001.

This applet plots Venn diagrams of two subsets of a "universal" set.
The subsets can be dragged; the area of their intersection is computed
automatically.
Radio buttons let one hilight various sets: the complement of a given set,
the intersection of the two sets, the union of the two sets, and each set
intersected with the complement of the other.
 */

import java.awt.*;
import java.applet.*;
import PbsGui.*;

public class Venn extends Applet
{
    protected VennDiagram vennDiag;
    protected Panel[] myPanel = new Panel[8];
    protected CheckboxGroup hilight = new CheckboxGroup();
    protected String[] checkLabel = {"A",
                            "Ac",
                            "B",
                            "Bc",
                            "A or B",
                            "AB",
                            "ABc",
                            "AcB",
                            "S",
                            "{}",
                           };
    protected Checkbox[] myBox = new Checkbox[checkLabel.length];
    Rectangle A; // the A rectangle (x, y, wd, ht)
    Rectangle B; // the B rectangle
    Rectangle AB; // the AB rectangle (may be null)
    double pA; // probability of A
    double pB; // probability of B
    TextBar ABar; // for P(A)
    TextBar BBar; // for P(B)
    DoubleValidator v1; //for the TextBars
    int dig = 3; // number of digits in the TextBars
    final int n = 1000; // the basic scale: the Venn Diagram is n by n points
    final double SArea = n*n; // the area of the Venn Diagram
    String[] labelText = {
                          "P(AB):    ",
                          "P(A or B):     ",
                         };
    Label[] myLabel = new Label[labelText.length];
    Label title = new Label("Venn Diagrams");
    Font titleFont = new Font("TimesRoman",Font.BOLD, 12);
    Point downPoint = null; // the point at which the mouse went down.
    Point upPoint;
    Point herePoint;
    boolean moveA = false; // are we moving rectangle A?
    boolean moveB = false;
    String select; // the region selected for hilighting


    public void init()
    {

        super.init();
        setBackground(Color.white);
        for (int i = 0; i < myPanel.length; i++) {
            myPanel[i] = new Panel();
        }
        for (int i = 0; i < checkLabel.length; i++) {
            myBox[i] = new Checkbox(checkLabel[i], hilight, false);
        }

        setLayout(new BorderLayout());
        myPanel[0].setLayout(new BorderLayout());
        myPanel[1].setLayout(new FlowLayout(FlowLayout.CENTER));
        myPanel[1].setFont(titleFont);
        myPanel[1].add(title);
        myPanel[2].setLayout(new BorderLayout());
        vennDiag = new VennDiagram();
        myPanel[2].add("Center", vennDiag);
        add("Center", myPanel[2]);
        add("North", myPanel[1]);
        myPanel[4].setLayout(new GridLayout(5,2));
        for (int i = 0; i < checkLabel.length; i++)
        {
            myPanel[4].add(myBox[i]);
        }
        add("East",myPanel[4]);
        myPanel[5].setLayout(new FlowLayout(FlowLayout.CENTER));
        v1 = new DoubleValidator();
        ABar = new TextBar(pA*100, 0.0, 100, dig,
                     (Validator) v1, TextBar.TEXT_TOP,"P(A) (%)");
        BBar = new TextBar(pB*100, 0.0, 100, dig,
                     (Validator) v1, TextBar.TEXT_TOP, "P(B) (%)");
        for (int i = 0; i < myLabel.length; i++)
        {
            myLabel[i] = new Label(labelText[i]);
        }
        myPanel[5].add(ABar);
        myPanel[5].add(BBar);
        myPanel[6].setLayout(new GridLayout(2,1));
        for (int i = 0; i < myLabel.length; i++)
        {
            myPanel[6].add(myLabel[i]);
        }
        myPanel[5].add(myPanel[6]);
        add("South", myPanel[5]);

        pA = 0.3;
        pB = 0.2;
        A = new Rectangle(new Point(n/5,n/5));
        B = new Rectangle(new Point(n/3,n/3));
        setSides();
        setArea();
        ABar.setValue((100 * pA));
        BBar.setValue((100 * pB));
        validate();
        select = "{}";
        vennDiag.redraw( A, B, select, n);



    }

    private void setArea()
    {
        AB = A.intersection(B);
        double pAB = ((double) Math.max(0, AB.height*AB.width))/SArea;
        if (pA == 0.0 || pB == 0.0) pAB = 0.0;
        myLabel[0].setText("P(AB): " + Format.doubleToPct(pAB));
        myLabel[1].setText("P(A or B): " + Format.doubleToPct(pA + pB - pAB));
    }


    private void setSides()
    {
// try to make squares if they fit.  If not, adjust.
// pA = A.width * A.height / SArea.  SArea is n by n
        int aw = (int) Math.round( Math.sqrt(pA*SArea));
        if (A.x + aw <= n && A.y + aw <= n)
        { // a square fits
            A.reshape(A.x, A.y, aw, aw);
        }
        else if (A.x > A.y)
        { // more room in the y direction, so make the width go to the edge
            A.reshape(A.x, A.y, n - A.x,
                (int) Math.round(SArea*pA/((double) (n - A.x))));
        }
        else
        { // make rectangle extend to the bottom.
            A.reshape(A.x, A.y,
                (int) Math.round(SArea*pA/((double) (n - A.y))), n - A.y);
        }

        int bw = (int) Math.round( Math.sqrt(pB*SArea) );
        if (B.x + bw < n && B.y + bw < n)
        { // just like A
            B.reshape(B.x, B.y, bw, bw);
        }
        else if (B.x > B.y)
        {
            B.reshape(B.x, B.y, n - B.x,
                (int) Math.round(SArea*pB/((double) (n - B.x))));
        }
        else
        {
            B.reshape(B.x, B.y,
                (int) Math.round(SArea*pB/((double) (n - B.y))), n - B.y);
        }
     }

    public boolean handleEvent(Event e)
    {
        if (e.id == Event.WINDOW_DESTROY) System.exit(0);
        else if (e.id == Event.SCROLL_ABSOLUTE
                 || e.id == Event.SCROLL_LINE_DOWN
                 || e.id == Event.SCROLL_LINE_UP
                 || e.id == Event.SCROLL_PAGE_DOWN
                 || e.id == Event.SCROLL_PAGE_UP)
        {
            if (e.target == ABar)
            {
                double newPA = ABar.getValue()/100;
                if (newPA <= pA)
                { // the reshaped rectangle must fit.
                    pA = newPA;
                    setSides();
                }
                else
                { // might not fit
                    int wide = Math.max(A.width, 2);
// how much can we / do we want to / grow?
                    int xGrow = (int) Math.min( n - A.x,
                                              Math.sqrt(newPA/pA)*wide);
                    int yGrow = (int) Math.min( n - A.y,
                                              newPA*SArea/xGrow);

                    A.reshape(A.x, A.y, xGrow, yGrow);
                }
                pA = ((double) (A.width*A.height))/SArea;
                ABar.setValue(100*pA);
                vennDiag.redraw(select);
                setArea();
                return true;
            }
            else if (e.target == BBar)
            {
                double newPB = BBar.getValue()/100;
                if (newPB <= pB)
                {
                    pB = newPB;
                    setSides();
                }
                else
                {
                    int wide = Math.max(B.width, 2);
                    int xGrow = (int) Math.min( n - B.x,
                                              Math.sqrt(newPB/pB)*wide);
                    int yGrow = (int) Math.min( n - B.y,
                                              newPB*SArea/xGrow);

                    B.reshape(B.x, B.y, xGrow, yGrow);
                }
                pB = ((double) (B.width * B.height))/SArea;
                BBar.setValue(100*pB);
                vennDiag.redraw(select);
                setArea();
                return true;
            }
        }
        else
        {   for ( int i = 0; i < checkLabel.length; i++)
            if (e.target == myBox[i] && e.id == Event.ACTION_EVENT)
            {
                select = myBox[i].getLabel();
                vennDiag.redraw(select);
                return true;
            }
        }
        return super.handleEvent(e);
    }

    public boolean mouseDown(Event e, int x, int y)
    {
        if (e.target == vennDiag)
        {

            int xPixHere = (int) Math.floor(vennDiag.pixToX(e.x - vennDiag.location().x -
                                       myPanel[2].location().x));
            int yPixHere = (int) Math.floor(vennDiag.pixToY(e.y - vennDiag.location().y -
                                       myPanel[2].location().y));
            if (A.inside(xPixHere, yPixHere))
            {
                moveA = true;
            }
            if (B.inside(xPixHere, yPixHere))
            {
                moveB = true;
            }
            downPoint = new Point(xPixHere, yPixHere);
        }
        return true;
    }

    public boolean mouseUp(Event e, int x, int y)
    {
        int newX;
        int newY;
        if (downPoint != null)
        {
            int dx = (int) Math.floor(vennDiag.pixToX(e.x -
                                      vennDiag.location().x
                                      - myPanel[2].location().x))
                                      - downPoint.x;
            int dy = (int) Math.floor(vennDiag.pixToY(e.y -
                                      vennDiag.location().y -
                                       myPanel[2].location().y))
                                       - downPoint.y;
            downPoint = null;
            if (moveA)
            {
                newX = Math.min(A.x + dx, n - A.width);
                newX = Math.max(newX, 0);
                newY = Math.min(A.y + dy, n - A.height);
                newY = Math.max(newY, 0);
                A.move( newX, newY);
                moveA = false;
            }

            if (moveB)
            {
                newX = Math.min(B.x + dx, n - B.width);
                newX = Math.max(newX, 0);
                newY = Math.min(B.y + dy, n - B.height);
                newY = Math.max(newY, 0);
                B.move( newX, newY);
                moveB = false;
            }
            vennDiag.redraw(A, B, select, n);
            setArea();
        }

        return true;
    }


    public boolean mouseDrag(Event e, int x, int y)
    {
        int newX;
        int newY;
        if (downPoint != null)
        {
            int xh = (int) Math.floor(vennDiag.pixToX(e.x -
                                      vennDiag.location().x
                                      - myPanel[2].location().x));
            int dx = xh - downPoint.x;
            int yh = (int) Math.floor(vennDiag.pixToY(e.y -
                                      vennDiag.location().y -
                                       myPanel[2].location().y));
            int dy = yh - downPoint.y;
            downPoint = new Point(xh, yh);
            if (moveA)
            {
                newX = Math.min(A.x + dx, n - A.width);
                newX = Math.max(newX, 0);
                newY = Math.min(A.y + dy, n - A.height);
                newY = Math.max(newY, 0);
                A.move( newX, newY);
            }

            if (moveB)
            {
                newX = Math.min(B.x + dx, n - B.width);
                newX = Math.max(newX, 0);
                newY = Math.min(B.y + dy, n - B.height);
                newY = Math.max(newY, 0);
                B.move( newX, newY);
            }

            setArea();
            vennDiag.redraw(A, B, select, n);
        }
        return true;
    }




}
