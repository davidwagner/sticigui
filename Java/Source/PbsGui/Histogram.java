/**
package PbsGui;

public class Histogram extends Canvas

@author P.B. Stark
@version 1.0 26 June 1999.
@copyright 1997, 1998, 1999, P.B. Stark. All rights reserved.

**/

package PbsGui;

import PbsStat.*;
import java.awt.*;
import java.applet.*;

public class Histogram extends Canvas {

   public final Dimension minimumSize = new Dimension(120,150);
   public static final int DEFAULT_COLOR = 0;
   public static final int REVERSE_COLOR = 1;
   private int nBins;
   private double[] count;
   private double[] count2;
   private double[] binEnd;
   private double[] xVal;
   private double[] yVal;
   private double[] yVal2;
   private double hiLiteLo;
   private double hiLiteHi;
   private boolean showCurve;
   private boolean twoHists;
   private Polygon plot;
   private Polygon plot2;

   private double xScale;
   private double yScale;
   private double yMin;
   private double yMax;
   private double[] xTic;

   private int ht;
   private int wd;
   private int digits = 6; // width in places of axis labels
   private int decimals = 2; // max number of decimal places in axis labels


   private Font labelFont = new Font("TimesRoman", Font.PLAIN, 12);
   private FontMetrics labelFontMetrics = getFontMetrics(labelFont);
   private int botSpace = labelFontMetrics.getHeight();
   private int topSpace = labelFontMetrics.getAscent();
   private int labelY;
   private int hOffset = 2*labelFontMetrics.stringWidth("-00.00");
   protected Color in = Color.yellow;
   protected Color out = Color.blue;
   protected Color in2 = Color.magenta;
   protected Color out2 = Color.green;
   protected Color curveColor = out.darker().darker();
   protected Color curve2Color = out2.darker().darker();

   public Histogram() {
   }

   public Histogram(double[] be, double[] c, double hll, double hlh) {
        this(be, c, false, null, hll, hlh, false, null, null, null);
   }

   public Histogram(double[] be, double[] c) {
        this(be, c, false, null, 0, 0, false, null, null, null);
   } // ends Histogram, no hilighting, one histogram

   public Histogram(double[] be, double[] c, double[] c2, double hll, double hlh) {
        this(be, c, true, c2, hll, hlh, false, null, null, null);
   } // ends Histogram w/ two hists, no curve

   public Histogram(double[] be, double[] c, double hll, double hlh, double[] xv,
         double[] yv) {
        this(be, c, false, null, hll, hlh, true, xv, yv, null);
   }

   public Histogram(double[] be, double[] c,
            double hll, double hlh, boolean sc, double[] xv, double[] yv) {
        this(be, c, false, null, hll, hlh, sc, xv, yv, null);
   }

   public Histogram(double[] be, double[] c, double[] c2,
            double hll, double hlh, double[] xv, double[] yv) {
        this(be, c, true, c2, hll, hlh, true, xv, yv, null);
   }

   public Histogram(double[] be, double[] c, double[] c2,
            double[] xv, double[] yv) {
        this(be, c, true, c2, 0, 0, true, xv, yv, null);
   }

   public Histogram(double[] be, double[] c, boolean th, double[] c2,
            double hll, double hlh, boolean sc, double[] xv, double[] yv) {
        this(be, c, th, c2, hll, hlh, sc, xv, yv, null);
   }

   public Histogram(double[] be, double[] c, boolean th, double[] c2, double hll,
            double hlh, boolean sc, double[] xv, double[] yv, double[] yv2) {
        nBins    = be.length - 1;
        binEnd   = be;
        count    = c;
        count2 = c2;
        twoHists = th;
        hiLiteLo = hll;
        hiLiteHi = hlh;
        showCurve = sc;
        xVal = xv;
        yVal = yv;
        yVal2 = yv2;

        histInit();

        if (showCurve) {
            plot = new Polygon();
            plot2 = new Polygon();
            for (int i = 0; i < xVal.length; i++) {
               if (xVal[i] >= xTic[0] && xVal[i] <= xTic[1]) {

                   plot.addPoint(xToPix(xVal[i]), yToPix(yVal[i]));
                   if (yVal2 != null) plot2.addPoint(xToPix(xVal[i]),
                                        yToPix(yVal2[i]));
               }
            }
        }
        repaint();
    }

    private void histInit() {
        Dimension d = size();
        ht = d.height;
        wd = d.width;
        yMin = 0;
        yMax = PbsStat.vMax(count);
        if (twoHists) yMax = Math.max(yMax, PbsStat.vMax(count2));
        if (showCurve) {
            yMax = Math.max(yMax, PbsStat.vMax(yVal));
            if (yVal2 != null) yMax = Math.max(yMax, PbsStat.vMax(yVal2));
        }
        xTic = Format.niceAxis(binEnd[0], binEnd[nBins]);
        labelY = ht - labelFontMetrics.getDescent();
        xScale = (wd-hOffset) / (xTic[1]-xTic[0]);
        yScale = (ht - botSpace - topSpace) / (yMax - yMin);

    } // ends histInit()


    public void setColor(Color o1, Color i1, Color o2, Color i2) {
        out = o1;
        in = i1;
        out2 = o2;
        in2 = i2;
    }

    public Color[] getColor() {
        Color[] c = new Color[4];
        c[0] = in;
        c[1] = out;
        c[2] = in2;
        c[3] = out2;
        return(c);
    }

    public void setColor(int i) {
        if (i == DEFAULT_COLOR) {
            in = Color.yellow;
            out = Color.blue;
            in2 = Color.magenta;
            out2 = Color.green;
            curveColor = out.darker().darker();
            curve2Color = out2.darker().darker();
        } else if (i == REVERSE_COLOR) {
            in = Color.magenta;
            out = Color.green;
            in2 = Color.yellow;
            out2 = Color.blue;
            curveColor = out.darker().darker();
            curve2Color = out2.darker().darker();
        }
    }

    public void setCurveColor(Color c) {
        curveColor = c;
    }

    public void setCurveColor(Color c, Color c2) {
        curveColor = c;
        curve2Color = c2;
    }

    public void setCurveColor(int i) {
        if (i == DEFAULT_COLOR) {
            curveColor = out.darker().darker();
            curve2Color = out2.darker().darker();
        } else if (i == REVERSE_COLOR) {
            curveColor = out2.darker().darker();
            curve2Color = out.darker().darker();
        }
    }

    public Color[] getCurveColor() {
        Color[] c = {curveColor, curve2Color};
        return(c);
    }

    public void setDigits(int digits) {
        this.digits = digits;
    }

    public void setDecimals(int decimals) {
        this.decimals = decimals;
    }

    public void setDigits(int digits, int decimals) {
        this.digits = digits;
        this.decimals = decimals;
    }

    public int getDigits() {
        return(digits);
    }

    public int getDecimals() {
        return(decimals);
    }

    public void redraw(double[] be, double[] c) {
        redraw(be, c, false, null, hiLiteLo, hiLiteHi, false, null, null, null);
    }

    public void redraw(double hll, double hlh) {
        redraw(binEnd, count, twoHists, count2, hll, hlh, showCurve,
               xVal, yVal, null);
    }

    public void redraw(boolean sc, double[] xv, double[] yv) {
        redraw(binEnd, count, twoHists, count2, hiLiteLo, hiLiteHi,
               sc, xv, yv, null);
    }

    public void redraw(double hll, double hlh, double[] c2) {
        redraw(binEnd, count, true, c2, hll, hlh, showCurve, xVal, yVal, null);
    }


    public void redraw(double hll, double hlh, boolean sc,
                       double[] xv, double[] yv) {
        redraw(binEnd, count, twoHists, count2, hll, hlh, sc, xv, yv, null);
    }

    public void redraw(double[] c2) {
        redraw(binEnd, count, true, c2, hiLiteLo, hiLiteHi, showCurve, xVal,
               yVal, null);
    }

    public void redraw(double[] c2, double[] xv, double[] yv) {
        redraw(binEnd, count, true, c2, hiLiteLo, hiLiteHi, true, xv, yv, null);
    }

    public void redraw(double[] c2, boolean sc, double[] xv, double[] yv) {
        redraw(binEnd, count, true, c2, hiLiteLo, hiLiteHi, sc, xv, yv, null);
    }

    public void redraw(double[] be, double[] c, double[] c2,
                       boolean sc, double[] xv, double[] yv) {
        redraw(be, c, true, c2, hiLiteLo, hiLiteHi, sc, xv, yv, null);
    }

    public void redraw(double[] be, double[] c,  boolean sc, double[] xv, double[] yv) {
        redraw(be, c, false, null, hiLiteLo, hiLiteHi, sc, xv, yv, null);
    }


    public void redraw(double[] be, double[] c, double hll, double hlh,
                       boolean sc, double[] xv, double[] yv) {
        redraw(be, c, false, null, hll, hlh, sc, xv, yv, null);
    }


    public void redraw(double[] be, double[] c, double[] c2,
                       double hll, double hlh,
                       double[] xv, double[] yv) {
        redraw(be, c, true, c2, hll, hlh, true, xv, yv, null);
    }


    public void redraw(double[] be, double[] c, double[] c2,
                       double hll, double hlh,
                       boolean sc, double[] xv, double[] yv) {
        redraw(be, c, true, c2, hll, hlh, sc, xv, yv, null);
    }

    public void redraw(double[] be, double[] c, boolean th, double[] c2,
                       double hll, double hlh, boolean sc,
                       double[] xv, double[] yv) {
        redraw(be, c, th, c2, hll, hlh, sc, xv, yv, null);
    }

    public void redraw(double[] be, double[] c, boolean th, double[] c2,
                       double hll, double hlh, boolean sc,
                       double[] xv, double[] yv, double[] yv2) {
        nBins = be.length - 1;
        binEnd = be;
        count = c;
        count2 = c2;
        twoHists = th;
        showCurve = sc;
        xVal = xv;
        yVal = yv;
        yVal2 = yv2;
        hiLiteLo = hll;
        hiLiteHi = hlh;
        histInit();
        if (showCurve && xVal != null && yVal != null) {
            plot = new Polygon();
            plot2 = new Polygon();
            for (int i = 0; i < xVal.length; i++) {
               if (xVal[i] >= xTic[0] && xVal[i] <= xTic[1]) {
                   plot.addPoint(xToPix(xVal[i]), yToPix(yVal[i]));
                   if (yVal2 != null) {
                      plot2.addPoint(xToPix(xVal[i]), yToPix(yVal2[i]));
                   }
               }
            }
        }
        repaint();
    } // ends redraw

    private int xToPix(double x) {
        int p = (int) (( x - xTic[0] )*xScale + hOffset/2);
        return(p);
    }

    private int yToPix(double y) {
        int p = ht - botSpace - (int)( ( y - yMin ) * yScale);
        return(p);
    }

    public void paint(Graphics g) {
      int y = ht - botSpace;
      g.setFont(labelFont);
      int height = 0;
      int height2 = 0;
      int hllPix = xToPix(hiLiteLo);
      int hlhPix = xToPix(hiLiteHi);
      int x;
      int x2;
      int lastLabelx = xToPix(xTic[0]) -
          labelFontMetrics.stringWidth(Format.numToString(xTic[0],digits,decimals,true))/2 - 1;
      String thisLabel;

      for (int i = 0; i < nBins; i++) {
         x = xToPix(binEnd[i]);
         x2 = xToPix(binEnd[i+1]);
         height = (int) ( (count[i]- yMin)*yScale );
         if ( twoHists ) {
            height2 = (int) ( (count2[i] - yMin)*yScale );
         }

         if (binEnd[i] >= hiLiteHi || binEnd[i+1] <= hiLiteLo) {// not in the selected region
             if ( twoHists && height2 > height ) {
                 g.setColor(out2);
                 g.fillRect(x, y - height2, x2 - x, height2);
             }
             g.setColor(out);
             g.fillRect(x, y - height, x2 - x, height);
             if (twoHists && height2 <= height) {
                 g.setColor(out2);
                 g.fillRect(x, y - height2, x2 - x, height2);
             }
         } else if (binEnd[i] >= hiLiteLo && binEnd[i+1] <= hiLiteHi) {// entirely selected
             if (twoHists && height2 > height) {
                 g.setColor(in2);
                 g.fillRect(x, y - height2, x2-x, height2);
             }
             g.setColor(in);
             g.fillRect(x, y - height, x2 - x, height);
             if (twoHists && height2 <= height) {                      // which is on top?
                 g.setColor(in2);
                 g.fillRect(x, y - height2, x2 - x, height2);
             }
         } else if (binEnd[i] >= hiLiteLo && binEnd[i+1] >= hiLiteHi) {// bottom is selected; edge is hiLiteHi
             if (twoHists && height2 > height) {
                 g.setColor(in2);
                 g.fillRect(x, y - height2, hlhPix - x, height2);
                 g.setColor(out2);
                 g.fillRect(hlhPix, y - height2, x2 - hlhPix, height2);
             }
             g.setColor(in);
             g.fillRect(x, y - height, hlhPix - x, height);
             g.setColor(out);
             g.fillRect(hlhPix, y - height, x2 - hlhPix, height);
             if (twoHists && height2 <= height) {
                 g.setColor(in2);
                 g.fillRect(x, y - height2, hlhPix - x, height2);
                 g.setColor(out2);
                 g.fillRect(hlhPix, y - height2, x2 - hlhPix, height2);
             }
         } else if (binEnd[i] <= hiLiteLo && binEnd[i+1] <= hiLiteHi) {// top is selected; edge is hiLiteLo
             if (twoHists && height2 > height) {
                 g.setColor(out2);
                 g.fillRect(x, y - height2, hllPix - x, height2);
                 g.setColor(in2);
                 g.fillRect(hllPix, y - height2, x2 - hllPix, height2);
             }
             g.setColor(out);
             g.fillRect(x, y - height, hllPix - x, height);
             g.setColor(in);
             g.fillRect(hllPix, y - height, x2 - hllPix, height);
             if (twoHists && height >= height2) {
                 g.setColor(out2);
                 g.fillRect(x, y - height2, hllPix - x, height2);
                 g.setColor(in2);
                 g.fillRect(hllPix, y - height2, x2 - hllPix, height2);
             }
         } else {                                                       // middle is selected; 2 edges
             if (twoHists && height2 > height) {
                 g.setColor(out2);
                 g.fillRect(x, y - height2, hllPix - x, height2);
                 g.fillRect(hlhPix, y - height2, x2 - hlhPix, height2);
                 g.setColor(in2);
                 g.fillRect(hllPix, y - height2, hlhPix - hllPix, height2);
             }
             g.setColor(out);
             g.fillRect(x, y - height, hllPix - x, height);
             g.fillRect(hlhPix, y - height, x2 - hlhPix, height);
             g.setColor(in);
             g.fillRect(hllPix, y - height, hlhPix - hllPix, height);
             if (twoHists && height2 <= height) {
                 g.setColor(out2);
                 g.fillRect(x, y - height2, hllPix - x, height2);
                 g.fillRect(hlhPix, y - height2, x2 - hlhPix, height2);
                 g.setColor(in2);
                 g.fillRect(hllPix, y - height2, hlhPix - hllPix, height2);
             }
         }
      }

// add labels
      g.setColor(Color.black);
      double xHere = xTic[0];
      while (xHere <= xTic[1]) {
         x = xToPix(xHere);
         thisLabel = Format.numToString(xHere,digits,decimals,true);
         int labelWidth = labelFontMetrics.stringWidth(thisLabel);
         if (x - labelWidth/2 > lastLabelx ) {
            lastLabelx = x + labelWidth/2 + 5;
            g.drawString(thisLabel, x - labelWidth/2, labelY);
            g.drawLine(x, ht-botSpace - 10, x, ht - botSpace + 4);
         }
         xHere += xTic[2];
      }

// do we plot a curve too?
      if (showCurve && xVal != null && yVal != null) {
          g.setColor(curveColor);
          PbsPlot.drawOpenPolygon(g,plot);
          if (yVal2 != null) {
              g.setColor(curve2Color);
              PbsPlot.drawOpenPolygon(g,plot2);
          }
      }

// outline the bins
      g.setColor(Color.black);
      for (int i = 0; i < nBins; i++) {
         x = xToPix(binEnd[i]);
         x2 = xToPix(binEnd[i+1]);
         height = (int) ( (count[i]- yMin)*yScale );
         g.drawRect(x, y - height, x2 - x, height);
         if ( twoHists ) {
            height2 = (int) ( (count2[i] - yMin)*yScale );
            g.drawRect(x, y - height2, x2 - x, height2);
         }
      }

// make the x axis heavier to hide roundoff errors
      for (int i=-1; i<2; i++) {
         g.drawLine(hOffset/2,ht-botSpace+i,wd-hOffset/2,ht-botSpace+i);
      }

   } // ends paint

// ends Histogram

}

