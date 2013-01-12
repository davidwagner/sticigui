/**
package PbsGui
public class XYPlot extends Canvas
@author P.B. Stark, stark@stat.berkeley.edu
@version 0.9
@copyright 1997, 1998, 1999 by P.B. Stark.  All rights reserved.

**/
package PbsGui;

import java.awt.*;
import java.applet.*;
import PbsStat.*;

public class XYPlot extends Canvas {
   private double[] yVal;
   private double[] xVal;
   private double hiLiteLo; // lower endpoint of hilighted region
   private double yLiteLo; // the y value at x=hiLiteLo
   private double hiLiteHi; // upper endpoint of hilighted region.
   private double yLiteHi; // the y value corresponding to the max hilighted
                           // x value (hiLiteHi)
   double xMin;
   double yMin;
   double xMax;
   double yMax;
   int ht; // height of plot in pixels
   int wd; // width of plot in pixels
   double xScale; // scale to convert from x to pixels
   double yScale; // scale to convert from y to pixels
   protected boolean hasXaxis = true; // plot an x axis?
   protected boolean hasYaxis = true; // plot a y axis?
   private boolean autoTic = true; // automatically scale and label axes

   private int nTics = 11; // number of tics on the axes
   private double[] xTic; // min x, max x, and tic increment for x axis
   private double[] yTic; // min y, max y, and tic increment for y axis

   private Polygon plot;
   private Polygon fill;
   public Dimension minimumSize = new Dimension(150, 150);
   public Dimension preferredSize = new Dimension(250, 300);

   protected Font labelFont = new Font("TimesRoman", Font.PLAIN, 9);
   protected FontMetrics labelFM = getFontMetrics(labelFont);
   private int botSpace = labelFM.getHeight();
   private int topSpace = labelFM.getAscent();
   private int botUp = labelFM.getDescent();


   protected int hOffset = 60;

   public XYPlot() {
   }

   public XYPlot(double[] x, double[] y,
        double hll, double yl, double hlh, double yh)
   {
        redraw( x, y, hll, yl, hlh, yh);

   }

   public XYPlot(double[] x, double[] y) {
        redraw(x, y, 0, 0, 0, 0);
   }

   public void axes(boolean hasXaxis, boolean hasYaxis) {
        this.hasXaxis = hasXaxis;
        this.hasYaxis = hasYaxis;
   }


   public void redraw(double[] x, double[] y, double hll, double yl,
                       double hlh, double yh)
   {
        xVal   = x;
        yVal    = y;
        hiLiteLo = hll;
        yLiteLo = yl;
        hiLiteHi = hlh;
        yLiteHi = yh;
        double[] xMinMax = PbsStat.vMinMax(x);
        double[] yMinMax = PbsStat.vMinMax(y);
        if (autoTic) {
             xTic = Format.niceAxis(xMinMax[0], xMinMax[1]);
             yTic = Format.niceAxis(yMinMax[0], yMinMax[1]);
        }
        xScale = (wd-hOffset) / (xTic[1]-xTic[0]);
        yScale = (ht - botSpace - topSpace) / (yTic[1] - yTic[0]);
        repaint();
    } // ends redraw

    public void redraw(double hll, double yl, double hlh, double yh) {
        redraw(xVal, yVal, hll, yl, hlh, yh);
    }// ends redraw

    public void redraw(double[] x, double[] y) {
        redraw(x, y, 0, 0, 0, 0);
    }

    private int xToPix(double x) {
        int pix = (int) ((x - xTic[0])*xScale ) + hOffset/2;
        return pix;
    } // ends xToPix

    private int yToPix(double y) {
        int pix = ht - botSpace - (int) ((y-yTic[0])*yScale );
        return pix;
    } // ends yToPix

    public void paint(Graphics g) {
        int nx = xVal.length;
        Dimension d = size();
        wd = d.width;
        ht = d.height;
        xScale = (wd-hOffset) / (xTic[1]-xTic[0]);
        yScale = (ht - botSpace - topSpace) / (yTic[1] - yTic[0]);
        g.setFont(labelFont);

        plot = new Polygon();
        fill = new Polygon();

// initialize the polygon to be filled
        int x0 = xToPix(hiLiteLo);
        int y0 = yToPix(0);
        fill.addPoint(x0, y0);
        int ylo = yToPix(yLiteLo);
        fill.addPoint(x0, ylo);

// make the plot and to-be-filled polygons
        for (int i = 0; i < nx; i++) {
            int x1 = xToPix(xVal[i]);
            int y1 = yToPix(yVal[i]);
            plot.addPoint(x1, y1);
// construct the polygon to fill
            if (xVal[i] >= hiLiteLo && xVal[i] <= hiLiteHi)
                fill.addPoint(x1, y1);
        }

// add the last two points to the polygon to fill
        x0 = xToPix(hiLiteHi);
        int yhi = yToPix(yLiteHi);
        fill.addPoint(x0, yhi);
        fill.addPoint(x0, y0);

// draw and fill the polygons
        g.setColor(Color.yellow);
        g.fillPolygon(fill);
        g.setColor(Color.black);
        PbsPlot.drawOpenPolygon(g, plot);

// let's draw some axes!
        g.setColor(Color.black);
        g.setFont(labelFont);
        x0 = xToPix(Math.max(0,xTic[0]));
        y0 = yToPix(Math.max(0,yTic[0]));
        if (hasYaxis) g.drawLine(x0, yToPix(yTic[1]), x0, yToPix(yTic[0]));
        if (hasXaxis) g.drawLine(xToPix(xTic[0]), y0, xToPix(xTic[1]), y0);
// axis labels
        String lbl;
        int wide;
        int nChars;
        int xh;
        int yh;
        int vShift = labelFM.getAscent();
        if (hasXaxis)
        {
            double xHere = xTic[0];
            while (xHere <= xTic[1]) // X-axis labels
            {
                int dig = (int) (Math.floor( Math.log( Math.abs(xHere))
                                 /Math.log( 10 )));
                if (dig < 0) dig = 1;
                lbl = Format.doubleToStr(xHere, 3);
                wide = labelFM.stringWidth(lbl);
                xh = xToPix(xHere);
                g.drawString(lbl, xh - wide/2, y0 + vShift + 2);
                g.drawLine( xh, y0 - 3, xh, y0 + 3);
                xHere += xTic[2];
            }
        }
        if (hasYaxis)
        {
            double yHere = yTic[0];
            vShift = labelFM.getDescent()/2;
            while (yHere <= yTic[1]) // Y-axis labels
            {
                int dig = (int) (Math.floor( Math.log( Math.abs(yHere))
                                 /Math.log( 10 )));
                if (dig < 1 ) dig = 1;
                lbl = Format.doubleToStr(yHere,3);
                wide = labelFM.stringWidth(lbl);
                yh = yToPix(yHere);
                g.drawString(lbl, x0 - wide - 4,
                             yh + botSpace/2 - vShift);
                g.drawLine( x0 - 3, yh, x0 + 3, yh);
                yHere += yTic[2];
            }
        }

   }// ends paint

    public void setTics(double[] xT, double[] yT)
    {
        this.xTic = xT;
        this.yTic = yT;
        autoTic = false;
    }

    public void setTics(double xMin, double xMax, double dx,
                        double yMin, double yMax, double dy)
    {
        double[] xT = {xMin, xMax, dx};
        double[] yT = {yMin, yMax, dy};
        setTics(xT, yT);
    }

    public void setAutoTic(boolean b)
    {
        this.autoTic = b;
    }

    public void setAutoTic()
    {
        setAutoTic(true);
    }

    public void setManualTic()
    {
        setAutoTic(false);
    }


    public boolean getAutoTic()
    {
        return(autoTic);
    }




}// ends PolyPlot