/** Class Venn3
@author Philip B. Stark http://statistics.berkeley.edu/~stark/
@version 1.0
@copyright 1997-2004, P.B. Stark. All rights reserved.
Last modified 21 January 2004.

This applet plots Venn diagrams of three subsets of a "universal" set.
The subsets can be dragged; the area of their intersection is computed
automatically.
Radio buttons let one hilight various sets: the complement of a given set,
the intersection of the sets, the union of the sets, and each set
intersected with the complement of the other.
 */

import java.awt.*;
import java.applet.*;
import PbsGui.*;

public class Venn3 extends Applet
{
    protected VennDiagram vennDiag;
    protected Panel[] myPanel = new Panel[8];
    protected CheckboxGroup hilight = new CheckboxGroup();
    protected String[] checkLabel = {
                                "A",
                                "B",
                                "C",
                                "Ac",
                                "Bc",
                                "Cc",
                                "AB",
                                "AC",
                                "BC",
                                "A or B",
                                "A or C",
                                "B or C",
                                "ABC",
                                "A or B or C",
                                "ABc",
                                "AcB",
                                "AcBC",
                                "Ac or BC",
                                "S",
                                "{}"
    };
    protected String[] checkCondLabel = {
                              "P(A|B)",
                              "P(Ac|B)",
                              "P(B|A)",
                              "P(A|BC)",
                              "P(Ac|BC)",
                              "P(A|(B or C))"
                              };
    String labelPad = " (P=       )";
    protected Checkbox[] myBox = new Checkbox[checkLabel.length];
    protected Checkbox[] myBox2 = new Checkbox[checkCondLabel.length];
    Rectangle A; // the A rectangle (x, y, wd, ht)
    Rectangle B; // the B rectangle
    Rectangle C; // the C rectangle
    Rectangle AB; // the AB rectangle (may be null)
    Rectangle AC; // may be null
    Rectangle BC; // may be null
    Rectangle ABC; // also may be null
    double pA; // probability of A
    double pB; // probability of B
    double pC; // probability of C
    TextBar ABar; // for P(A)
    TextBar BBar; // for P(B)
    TextBar CBar; // for P(C)
    DoubleValidator v1; //for the TextBars
    int dig = 4; // number of digits in the TextBars
    final int n = 1000; // the basic scale: the Venn Diagram is n by n points
    final double SArea = n*n; // the area of the Venn Diagram
    Label title = new Label("Venn Diagrams");
    Font titleFont = new Font("TimesRoman",Font.BOLD, 12);
    Point downPoint = null; // the point at which the mouse went down.
    Point upPoint;
    Point herePoint;
    boolean moveA = false; // are we moving rectangle A?
    boolean moveB = false;
    boolean moveC = false;
    String select; // the region selected for hilighting


    public void init() {
        super.init();
        setBackground(Color.white);
        for (int i = 0; i < myPanel.length; i++) {
            myPanel[i] = new Panel();
        }
        for (int i = 0; i < checkLabel.length; i++) {
            myBox[i] = new Checkbox(checkLabel[i] +  labelPad, hilight, false);
        }
        for (int i=0; i < checkCondLabel.length; i++) {
            myBox2[i] = new Checkbox(checkCondLabel[i] + labelPad, hilight, false);
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
        myPanel[4].setLayout(new GridLayout(5,4));
        for (int i = 0; i < checkLabel.length; i++) {
            myPanel[4].add(myBox[i]);
        }
        add("East",myPanel[4]);
        myPanel[5].setLayout(new FlowLayout(FlowLayout.CENTER));
        v1 = new DoubleValidator();
        ABar = new TextBar(pA*100, 0.0, 100, dig,
                     (Validator) v1, TextBar.NO_BAR,"P(A) (%)");
        BBar = new TextBar(pB*100, 0.0, 100, dig,
                     (Validator) v1, TextBar.NO_BAR, "P(B) (%)");
        CBar = new TextBar(pC*100, 0.0, 100, dig,
                     (Validator) v1, TextBar.NO_BAR, "P(C) (%)");
        myPanel[5].add(ABar);
        myPanel[5].add(BBar);
        myPanel[5].add(CBar);
        myPanel[6].setLayout(new GridLayout(2,2));
        for (int i = 0; i < checkCondLabel.length; i++) {
            myPanel[6].add(myBox2[i]);
        }
        myPanel[5].add(myPanel[6]);
        add("South", myPanel[5]);

        pA = 0.3;
        pB = 0.2;
        pC = 0.1;
        A = new Rectangle(new Point(n/5,n/5));
        B = new Rectangle(new Point(n/3,n/3));
        C = new Rectangle(new Point(n/2,n/2));
        setSides();
        setArea();
        ABar.setValue((100 * pA));
        BBar.setValue((100 * pB));
        CBar.setValue((100 * pC));
        validate();
        select = "{}";
        vennDiag.redraw( A, B, C, select, n);
    }

    private void setArea() {
        AB = A.intersection(B);
        AC = A.intersection(C);
        BC = B.intersection(C);
        ABC = AB.intersection(C);
        double pAB = ((double) Math.max(0, AB.height*AB.width))/SArea;
        double pAC = ((double) Math.max(0, AC.height*AC.width))/SArea;
        double pBC = ((double) Math.max(0, BC.height*BC.width))/SArea;
        double pABC = ((double) Math.max(0, ABC.height*ABC.width))/SArea;
        if (AB.isEmpty()) {
            pAB = 0.0;
            pABC = 0.0;
        }
        if (AC.isEmpty()) {
            pAC = 0.0;
            pABC = 0.0;
        }
        if (BC.isEmpty()) {
            pBC = 0.0;
            pABC = 0.0;
        }
        if (ABC.isEmpty()) {
            pABC = 0.0;
        }
        if (pA == 0.0 || pB == 0.0) {
            pAB = 0.0;
        }
        if (pA == 0.0 || pC == 0.0) {
            pAC = 0.0;
        }
        if (pB == 0.0 || pC == 0.0) {
            pBC = 0.0;
        }
        if (pAB == 0.0 || pC == 0) {
            pABC = 0.0;
        }
        myBox[0].setLabel("A (P= " + Format.doubleToPct(pA) + ")");
        myBox[1].setLabel("B (P= " + Format.doubleToPct(pB) + ")");
        myBox[2].setLabel("C (P= " + Format.doubleToPct(pC) + ")");
        myBox[3].setLabel("Ac (P= " + Format.doubleToPct(1.0-pA) + ")");
        myBox[4].setLabel("Bc (P= " + Format.doubleToPct(1.0-pB) + ")");
        myBox[5].setLabel("Cc (P= " + Format.doubleToPct(1.0-pC) + ")");
        myBox[6].setLabel("AB (P= " + Format.doubleToPct(pAB) + ")");
        myBox[7].setLabel("AC (P= " + Format.doubleToPct(pAC) + ")");
        myBox[8].setLabel("BC (P= " + Format.doubleToPct(pBC) + ")");
        myBox[9].setLabel("A or B (P= " + Format.doubleToPct(pA + pB - pAB) + ")");
        myBox[10].setLabel("A or C (P= " + Format.doubleToPct(pA + pC - pAC) + ")");
        myBox[11].setLabel("B or C (P= " + Format.doubleToPct(pB + pC - pBC) + ")");
        myBox[12].setLabel("ABC (P= " + Format.doubleToPct(pABC) + ")");
        myBox[13].setLabel("A or B or C (P= " +
            Format.doubleToPct(pA + pB + pC - pAB - pAC - pBC + pABC) + ")");
        myBox[14].setLabel("ABc (P= " + Format.doubleToPct(pA - pAB) + ")");
        myBox[15].setLabel("AcB (P= " + Format.doubleToPct(pB - pAB) + ")");
        myBox[16].setLabel("AcBC (P= " + Format.doubleToPct(pBC - pABC) + ")");
        myBox[17].setLabel("Ac or BC (P = " +
            Format.doubleToPct(1 - pA + pABC) + ")");
        myBox[18].setLabel("S (P = 100%)");
        myBox[19].setLabel("{} (P = 0%)");

        myBox2[0].setLabel("P(A|B): " + Format.doubleToPct(pAB/pB));
        myBox2[1].setLabel("P(Ac|B): " + Format.doubleToPct((pB-pAB)/pB));
        myBox2[2].setLabel("P(B|A): " + Format.doubleToPct(pAB/pA));
        myBox2[3].setLabel("P(A|BC): " + Format.doubleToPct(pABC/pBC));
        myBox2[4].setLabel("P(Ac|BC): " +
                Format.doubleToPct((pBC - pABC)/pBC));
        myBox2[5].setLabel("P(A|(B or C)): " +
                Format.doubleToPct((pAB + pAC - pABC)/(pB + pC - pBC)));
    }

    private Rectangle setShape(Rectangle R, double pR) {
// try to make squares if they fit.  If not, adjust.
// pR = R.width * R.height / SArea.  SArea is n by n
        int rw = (int) Math.round( Math.sqrt(pR*SArea));
        if (R.x + rw <= n && R.y + rw <= n) { // a square fits
            R.reshape(R.x, R.y, rw, rw);
        } else if (R.x > R.y) {
            // more room in the y direction, so make the width go to the edge
            R.reshape(R.x, R.y, n - R.x,
                (int) Math.round(SArea*pR/((double) (n - R.x))));
        } else { // make rectangle extend to the bottom.
            R.reshape(R.x, R.y,
                (int) Math.round(SArea*pR/((double) (n - R.y))), n - R.y);
        }
        return(R);
    }

    private void setSides() {
        A = setShape(A, pA);
        B = setShape(B, pB);
        C = setShape(C, pC);
    }

    public boolean handleEvent(Event e) {
        if (e.id == Event.WINDOW_DESTROY) {
            System.exit(0);
        } else if (e.id == Event.SCROLL_ABSOLUTE
                 || e.id == Event.SCROLL_LINE_DOWN
                 || e.id == Event.SCROLL_LINE_UP
                 || e.id == Event.SCROLL_PAGE_DOWN
                 || e.id == Event.SCROLL_PAGE_UP) {
            if (e.target == ABar) {
                double newPA = ABar.getValue()/100;
                if (newPA <= pA) { // the reshaped rectangle must fit.
                    pA = newPA;
                    setSides();
                } else { // might not fit
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
                return(true);
            } else if (e.target == BBar) {
                double newPB = BBar.getValue()/100;
                if (newPB <= pB) {
                    pB = newPB;
                    setSides();
                } else {
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
                return(true);
            } else if (e.target == CBar) {
                double newPC = CBar.getValue()/100;
                if (newPC <= pC) {
                    pC = newPC;
                    setSides();
                } else {
                    int wide = Math.max(C.width, 2);
                    int xGrow = (int) Math.min( n - C.x,
                                              Math.sqrt(newPC/pC)*wide);
                    int yGrow = (int) Math.min( n - C.y,
                                              newPC*SArea/xGrow);

                    C.reshape(C.x, C.y, xGrow, yGrow);
                }
                pC = ((double) (C.width * C.height))/SArea;
                CBar.setValue(100*pC);
                vennDiag.redraw(select);
                setArea();
                return(true);
            }
        } else {
            for ( int i = 0; i < checkLabel.length; i++) {
                if (e.target == myBox[i] && e.id == Event.ACTION_EVENT) {
                    select = checkLabel[i];
                    vennDiag.redraw(select);
                    return(true);
                }
            }
            for ( int i = 0; i < checkCondLabel.length; i++) {
                if (e.target == myBox2[i] && e.id == Event.ACTION_EVENT) {
                    select = checkCondLabel[i];
                    vennDiag.redraw(select);
                    return(true);
                }
            }
        }
        return super.handleEvent(e);
    }

    public boolean mouseDown(Event e, int x, int y) {
        if (e.target == vennDiag) {
            int xPixHere = (int) Math.floor(vennDiag.pixToX(e.x - vennDiag.location().x -
                                       myPanel[2].location().x));
            int yPixHere = (int) Math.floor(vennDiag.pixToY(e.y - vennDiag.location().y -
                                       myPanel[2].location().y));
            if (A.inside(xPixHere, yPixHere)) {
                moveA = true;
            }
            if (B.inside(xPixHere, yPixHere)) {
                moveB = true;
            }
            if (C.inside(xPixHere, yPixHere)) {
                            moveC = true;
            }
            downPoint = new Point(xPixHere, yPixHere);
        }
        return(true);
    }

    public boolean mouseUp(Event e, int x, int y) {
        int newX;
        int newY;
        if (downPoint != null) {
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
            if (moveC)
            {
                newX = Math.min(C.x + dx, n - C.width);
                newX = Math.max(newX, 0);
                newY = Math.min(C.y + dy, n - C.height);
                newY = Math.max(newY, 0);
                C.move( newX, newY);
                moveC = false;
            }

            vennDiag.redraw(A, B, C, select, n);
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

            if (moveC)
            {
                newX = Math.min(C.x + dx, n - C.width);
                newX = Math.max(newX, 0);
                newY = Math.min(C.y + dy, n - C.height);
                newY = Math.max(newY, 0);
                C.move( newX, newY);
            }

            setArea();
            vennDiag.redraw(A, B, C, select, n);
        }
        return(true);
    }




}
