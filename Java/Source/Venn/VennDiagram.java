/** Class VennDiagram extends Canvas
@author Philip B. Stark http://statistics.berkeley.edu/~stark
@version 1.1
@copyright 1997-2004, P.B. Stark. All rights reserved.
last modified 1/20/2004 4:20pm

The graphics canvas for a Venn diagram with up to 3 subsets.
*/

import java.awt.*;
import java.applet.*;
import PbsGui.*;

class VennDiagram extends Canvas {
    Color background = Color.lightGray;
    Color hiColor = Color.blue;
    Color setColor = Color.green;
    Color Acolor = Color.green;
    Color Bcolor = Color.yellow;
    Color Ccolor = Color.red;
    Color ABcolor = Color.green;
    Color BCcolor = Color.orange;
    Color ACcolor = Color.pink;
    Color ABCcolor = Color.darkGray;

    protected Font labelFont = new Font("TimesRoman", Font.BOLD, 12);
    protected FontMetrics labelFM = getFontMetrics(labelFont);
    public final Dimension minimumSize = new Dimension(120,150);

    Rectangle A;
    Rectangle B;
    Rectangle C;
    Rectangle Apix;
    Rectangle Bpix;
    Rectangle Cpix;
    Rectangle ABpix;
    Rectangle ACpix;
    Rectangle BCpix;
    Rectangle ABCpix;
    String hiLite;

    private double xScale; // convert from x to pixels
    private double yScale; // convert from y to pixels
    private int ht; // height of the canvas
    private int wd; // width of the canvas

    private double xMax = 500;
    private double yMax = 500;
    private final int hOffset=60; // horizontal inset*2
    private final int vOffset = 60; // vertical inset*2

    VennDiagram() {
    }

    VennDiagram(Rectangle a, Rectangle b, String h, double xm,
                double ym) {
        redraw(a, b, null, h, xm, ym);
    }

    VennDiagram(Rectangle a, Rectangle b, String h, int n) {
        redraw(a, b, null, h, n, n);
    }

    VennDiagram(Rectangle a, Rectangle b, Rectangle c, String h, int n) {
        redraw(a, b, c, h, n, n);
    }

    VennDiagram(Rectangle a, Rectangle b, Rectangle c, String h, double xm,
                double ym) {
        redraw(a, b, c, h, xm, ym);
    }


    public void redraw(Rectangle a, Rectangle b, Rectangle c, String h,
                       double xm, double ym) {
        A = a;
        B = b;
        C = c;
        hiLite = h;
        xMax = xm;
        yMax = ym;
        redo();
        repaint();
    }

    public void redraw(String h) {
        redraw( A, B, C, h, xMax, yMax);
    }

    public void redraw(Rectangle a, Rectangle b, String h) {
        redraw( a, b, null, h, xMax, yMax );
    }

    public void redraw(Rectangle a, Rectangle b, String h, int n) {
        redraw( a, b, null, h, n, n);
    }

    public void redraw(Rectangle a, Rectangle b, Rectangle c, String h) {
        redraw( a, b, c, h, xMax, yMax );
    }

    public void redraw(Rectangle a, Rectangle b, Rectangle c, String h, int n) {
        redraw( a, b, c, h, n, n);
    }

    protected void redo() {
        Dimension d = size();
        ht = d.height;
        wd = d.width;
        xScale = ((double) (wd - hOffset))/xMax ;
        yScale = ((double) (ht - vOffset))/yMax ;
        Apix = rectToPix(A);
        Bpix = rectToPix(B);
        Cpix = null;
        ABpix = Apix.intersection(Bpix);
        ACpix = null;
        BCpix = null;
        ABCpix = null;
        if (C != null) {
            Cpix = rectToPix(C);
            ACpix = Apix.intersection(Cpix);
            BCpix = Bpix.intersection(Cpix);
            ABCpix = ABpix.intersection(Cpix);
        }

    }

    protected Rectangle rectToPix(Rectangle r) {
        int xp = xToPix(r.x);
        int yp = yToPix(r.y);
        int wp = xToPix(r.width + r.x) - xp;
        int hp = yToPix(r.height + r.y) - yp;
        return new Rectangle(xp, yp, wp, hp);
    }


    private int xToPix(double x) {
        return (int) ( x*xScale ) + hOffset/2;
    } // ends xToPix

    private int yToPix(double y) {
        return (int) ( y*yScale ) + vOffset/2;
    } // ends yToPix

    public double pixToX(int p) {
    // converts the pixel value to the corresponding value of x
        return ((double) (p - hOffset/2))/xScale;
    }

    public double pixToY(int p) {
        return ((double) (p - vOffset/2))/yScale;
    }

    public boolean inA(int x, int y) {
        return Apix.inside(x, y);
    }

    public boolean inB(int x, int y) {
        return Bpix.inside(x, y);
    }

    public boolean inC(int x, int y) {
        return Cpix.inside(x, y);
    }

    public void paint(Graphics g) {
// first do the colors, then the borders, then the labels.
// do the hilighted region.
        Dimension d = size();
        ht = d.height;
        wd = d.width;
        redo();

        if (hiLite.equals("A")) {
            g.setColor(background);
            g.fillRect(xToPix(0), yToPix(0), xToPix(xMax) - xToPix(0),
                        yToPix(yMax) - yToPix(0));
            g.setColor(hiColor);
            g.fillRect(Apix.x, Apix.y, Apix.width, Apix.height);
        } else if (hiLite.equals("Ac")) {
            g.setColor(hiColor);
            g.fillRect(xToPix(0), yToPix(0), xToPix(xMax) - xToPix(0),
                        yToPix(yMax) - yToPix(0));
            g.setColor(background);
            g.fillRect(Apix.x, Apix.y, Apix.width, Apix.height);
        } else if (hiLite.equals("B")) {
            g.setColor(background);
            g.fillRect(xToPix(0), yToPix(0), xToPix(xMax) - xToPix(0),
                        yToPix(yMax) - yToPix(0));
            g.setColor(hiColor);
            g.fillRect(Bpix.x, Bpix.y, Bpix.width, Bpix.height);
        } else if (hiLite.equals("Bc")) {
            g.setColor(hiColor);
            g.fillRect(xToPix(0), yToPix(0), xToPix(xMax) - xToPix(0),
                        yToPix(yMax) - yToPix(0));
            g.setColor(background);
            g.fillRect(Bpix.x, Bpix.y, Bpix.width, Bpix.height);
        } else if (hiLite.equals("C")) {
            g.setColor(background);
            g.fillRect(xToPix(0), yToPix(0), xToPix(xMax) - xToPix(0),
                        yToPix(yMax) - yToPix(0));
            g.setColor(hiColor);
            g.fillRect(Cpix.x, Cpix.y, Cpix.width, Cpix.height);
        } else if (hiLite.equals("Cc")) {
            g.setColor(hiColor);
            g.fillRect(xToPix(0), yToPix(0), xToPix(xMax) - xToPix(0),
                        yToPix(yMax) - yToPix(0));
            g.setColor(background);
            g.fillRect(Cpix.x, Cpix.y, Cpix.width, Cpix.height);
        } else if (hiLite.equals("AB")) {
            g.setColor(background);
            g.fillRect(xToPix(0), yToPix(0), xToPix(xMax) - xToPix(0),
                        yToPix(yMax) - yToPix(0));
            g.setColor(hiColor);
            g.fillRect(ABpix.x, ABpix.y, ABpix.width, ABpix.height);
        } else if (hiLite.equals("AC")) {
            g.setColor(background);
            g.fillRect(xToPix(0), yToPix(0), xToPix(xMax) - xToPix(0),
                        yToPix(yMax) - yToPix(0));
            g.setColor(hiColor);
            g.fillRect(ACpix.x, ACpix.y, ACpix.width, ACpix.height);
        } else if (hiLite.equals("BC")) {
            g.setColor(background);
            g.fillRect(xToPix(0), yToPix(0), xToPix(xMax) - xToPix(0),
                        yToPix(yMax) - yToPix(0));
            g.setColor(hiColor);
            g.fillRect(BCpix.x, BCpix.y, BCpix.width, BCpix.height);
        } else if (hiLite.equals("A or B")) {
            g.setColor(background);
            g.fillRect(xToPix(0), yToPix(0), xToPix(xMax) - xToPix(0),
                        yToPix(yMax) - yToPix(0));
            g.setColor(hiColor);
            g.fillRect(Apix.x, Apix.y, Apix.width, Apix.height);
            g.fillRect(Bpix.x, Bpix.y, Bpix.width, Bpix.height);
        } else if (hiLite.equals("A or C")) {
            g.setColor(background);
            g.fillRect(xToPix(0), yToPix(0), xToPix(xMax) - xToPix(0),
                        yToPix(yMax) - yToPix(0));
            g.setColor(hiColor);
            g.fillRect(Apix.x, Apix.y, Apix.width, Apix.height);
            g.fillRect(Cpix.x, Cpix.y, Cpix.width, Cpix.height);
        } else if (hiLite.equals("B or C")) {
            g.setColor(background);
            g.fillRect(xToPix(0), yToPix(0), xToPix(xMax) - xToPix(0),
                        yToPix(yMax) - yToPix(0));
            g.setColor(hiColor);
            g.fillRect(Bpix.x, Bpix.y, Bpix.width, Bpix.height);
            g.fillRect(Cpix.x, Cpix.y, Cpix.width, Cpix.height);
        } else if (hiLite.equals("AcB")) {
            g.setColor(background);
            g.fillRect(xToPix(0), yToPix(0), xToPix(xMax) - xToPix(0),
                        yToPix(yMax) - yToPix(0));
            g.setColor(hiColor);
            g.fillRect(Bpix.x, Bpix.y, Bpix.width, Bpix.height);
            g.setColor(background);
            g.fillRect(Apix.x, Apix.y, Apix.width, Apix.height);
        } else if (hiLite.equals("ABc")) {
            g.setColor(background);
            g.fillRect(xToPix(0), yToPix(0), xToPix(xMax) - xToPix(0),
                        yToPix(yMax) - yToPix(0));
            g.setColor(hiColor);
            g.fillRect(Apix.x, Apix.y, Apix.width, Apix.height);
            g.setColor(background);
            g.fillRect(Bpix.x, Bpix.y, Bpix.width, Bpix.height);
        } else if (hiLite.equals("A or B or C")) {
            g.setColor(background);
            g.fillRect(xToPix(0), yToPix(0), xToPix(xMax) - xToPix(0),
                        yToPix(yMax) - yToPix(0));
            g.setColor(hiColor);
            g.fillRect(Apix.x, Apix.y, Apix.width, Apix.height);
            g.fillRect(Bpix.x, Bpix.y, Bpix.width, Bpix.height);
            g.fillRect(Cpix.x, Cpix.y, Cpix.width, Cpix.height);
        } else if (hiLite.equals("ABC")) {
            g.setColor(background);
            g.fillRect(xToPix(0), yToPix(0), xToPix(xMax) - xToPix(0),
                        yToPix(yMax) - yToPix(0));
            g.setColor(hiColor);
            g.fillRect(ABCpix.x, ABCpix.y, ABCpix.width, ABCpix.height);
        } else if (hiLite.equals("ABCc")) {
            g.setColor(background);
            g.fillRect(xToPix(0), yToPix(0), xToPix(xMax) - xToPix(0),
                        yToPix(yMax) - yToPix(0));
            g.setColor(hiColor);
            g.fillRect(ABpix.x, ABpix.y, ABpix.width, ABpix.height);
            g.setColor(background);
            g.fillRect(Cpix.x, Cpix.y, Cpix.width, Cpix.height);
        } else if (hiLite.equals("AB or Cc")) {
            g.setColor(hiColor);
            g.fillRect(xToPix(0), yToPix(0), xToPix(xMax) - xToPix(0),
                        yToPix(yMax) - yToPix(0));
            g.setColor(background);
            g.fillRect(Cpix.x, Cpix.y, Cpix.width, Cpix.height);
            g.setColor(hiColor);
            g.fillRect(ABpix.x, ABpix.y, ABpix.width, ABpix.height);
        } else if (hiLite.equals("AcBC")) {
            g.setColor(background);
            g.fillRect(xToPix(0), yToPix(0), xToPix(xMax) - xToPix(0),
                        yToPix(yMax) - yToPix(0));
            g.setColor(hiColor);
            g.fillRect(BCpix.x, BCpix.y, BCpix.width, BCpix.height);
            g.setColor(background);
            g.fillRect(Apix.x, Apix.y, Apix.width, Apix.height);
        } else if (hiLite.equals("Ac or BC")) {
            g.setColor(hiColor);
            g.fillRect(xToPix(0), yToPix(0), xToPix(xMax) - xToPix(0),
                        yToPix(yMax) - yToPix(0));
            g.setColor(background);
            g.fillRect(Apix.x, Apix.y, Apix.width, Apix.height);
            g.setColor(hiColor);
            g.fillRect(BCpix.x, BCpix.y, BCpix.width, BCpix.height);
        } else if (hiLite.equals("S")) {
            g.setColor(hiColor);
            g.fillRect(xToPix(0), yToPix(0), xToPix(xMax) - xToPix(0),
                        yToPix(yMax) - yToPix(0));
        } else if (hiLite.equals("P(A|B)")) {
            g.setColor(background);
            g.fillRect(xToPix(0), yToPix(0), xToPix(xMax) - xToPix(0),
                        yToPix(yMax) - yToPix(0));
            g.setColor(hiColor);
            g.fillRect(Bpix.x, Bpix.y, Bpix.width, Bpix.height);
            g.setColor(setColor);
            g.fillRect(ABpix.x, ABpix.y, ABpix.width, ABpix.height);
        } else if (hiLite.equals("P(Ac|B)")) {
            g.setColor(background);
            g.fillRect(xToPix(0), yToPix(0), xToPix(xMax) - xToPix(0),
                                    yToPix(yMax) - yToPix(0));
            g.setColor(setColor);
            g.fillRect(Bpix.x, Bpix.y, Bpix.width, Bpix.height);
            g.setColor(hiColor);
            g.fillRect(ABpix.x, ABpix.y, ABpix.width, ABpix.height);
        } else if (hiLite.equals("P(B|A)")) {
            g.setColor(background);
            g.fillRect(xToPix(0), yToPix(0), xToPix(xMax) - xToPix(0),
                        yToPix(yMax) - yToPix(0));
            g.setColor(hiColor);
            g.fillRect(Apix.x, Apix.y, Apix.width, Apix.height);
            g.setColor(setColor);
            g.fillRect(ABpix.x, ABpix.y, ABpix.width, ABpix.height);
        } else if (hiLite.equals("P(A|BC)")) {
            g.setColor(background);
            g.fillRect(xToPix(0), yToPix(0), xToPix(xMax) - xToPix(0),
                        yToPix(yMax) - yToPix(0));
            g.setColor(hiColor);
            g.fillRect(BCpix.x, BCpix.y, BCpix.width, BCpix.height);
            g.setColor(setColor);
            g.fillRect(ABCpix.x, ABCpix.y, ABCpix.width, ABCpix.height);
        } else if (hiLite.equals("P(Ac|BC)")) {
            g.setColor(background);
            g.fillRect(xToPix(0), yToPix(0), xToPix(xMax) - xToPix(0),
                                    yToPix(yMax) - yToPix(0));
            g.setColor(setColor);
            g.fillRect(BCpix.x, BCpix.y, BCpix.width, BCpix.height);
            g.setColor(hiColor);
            g.fillRect(ABCpix.x, ABCpix.y, ABCpix.width, ABCpix.height);
        } else if (hiLite.equals("P(A|(B or C))")) {
            g.setColor(background);
            g.fillRect(xToPix(0), yToPix(0), xToPix(xMax) - xToPix(0),
                                    yToPix(yMax) - yToPix(0));
            g.setColor(hiColor);
            g.fillRect(Bpix.x, Bpix.y, Bpix.width, Bpix.height);
            g.fillRect(Cpix.x, Cpix.y, Cpix.width, Cpix.height);
            g.setColor(setColor);
            g.fillRect(ABpix.x, ABpix.y, ABpix.width, ABpix.height);
            g.fillRect(ACpix.x, ACpix.y, ACpix.width, ACpix.height);
        } else {
            g.setColor(background);
            g.fillRect(xToPix(0), yToPix(0), xToPix(xMax) - xToPix(0),
                        yToPix(yMax) - yToPix(0));
        }

// do the borders.
        g.setColor(Color.black);

        g.drawRect(xToPix(0), yToPix(0), xToPix(xMax) - xToPix(0),
                        yToPix(yMax) - yToPix(0));
        g.drawRect(Apix.x, Apix.y, Apix.width, Apix.height);
        g.drawRect(Bpix.x, Bpix.y, Bpix.width, Bpix.height );
        if (C != null) {
            g.drawRect(Cpix.x, Cpix.y, Cpix.width, Cpix.height );
        }
        g.setFont(labelFont);
        int lw = labelFM.stringWidth("S");
        int lh = labelFM.getAscent();
        g.drawString("S",xToPix(0)+lw/2 + 2, yToPix(0) + lh + 2);
        g.drawString("A", Apix.x +lw/2 + 2, Apix.y + lh + 2);
        g.drawString("B", Bpix.x + lw/2 + 2, Bpix.y + lh + 2);
        if (C != null) {
            g.drawString("C", Cpix.x + lw/2 + 2, Cpix.y + lh + 2);
        }
    }


} // ends class VennDiagram

