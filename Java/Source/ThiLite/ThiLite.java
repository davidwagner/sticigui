/* class THiLite extends Applet
@author P.B. Stark http://statistics.berkeley.edu/~stark/
@version 0.9
@copyright 1977-2001. All rights reserved.
Last modified 14 March 2001.

Plots the t density curve. A scrollbar controls the
degrees of freedom. Other scrollbars let you hilight part of
the curve, and the area of the selected part of the curve is displayed.
Uses the trapezoid rule to calculate the area, rather than an analytic
approximation.

*/

import java.awt.*;
import java.applet.*;
import java.io.*;

public class THiLite extends Applet
{
   private final int nx = 500;
   private double[] yVal;
   private double[] xVal;
   private String title;
   private double hiLiteLo;
   private double hiLiteHi;
   private double slideScale;
   private final double xLim = 7;
   private final double yLim = 0.5;
   private double mu = 0;
   private double sigma = 1;
   private int dof = 1;

   private Label hiLabel;
   private Label loLabel;
   private Label areaLabel;
   private Label nullLabel;
   private Label tLabel;
   private Label dofLabel;
   private Panel h;
   private Panel t;

   myScrollbar hi;
   myScrollbar lo;
   myScrollbar dofBar;
   PolyPlot polyGram;


   public void init()
   {
      super.init();
      setBackground(Color.white);
      try
      {
         mu = strToDouble(getParameter("mean"));
      }
      catch (NullPointerException e)
      {
         mu = 0;
      }
      try
      {
         sigma = strToDouble(getParameter("SD"));
      }
      try
      {
         dof = strToInt(getParameter("dof"));
      }
      catch (NullPointerException e)
      {
         dof = 1;
      }
      catch  (NullPointerException e)
      {
         sigma = 1;
      }
      try
      {
         hiLiteLo = strToDouble(getParameter("hiLiteLo"));
      }
      catch (NullPointerException e)
      {
         hiLiteLo = 0;
      }
      try
      {
         hiLiteHi = strToDouble(getParameter("hiLiteHi"));
      }
      catch (NullPointerException e)
      {
         hiLiteHi = 0;
      }
      yVal = new double[nx];
      xVal = new double[nx];
      title = "The t-distribution Curve";
      for (int j = 0; j < nx; j++)
      {
         xVal[j] = -xLim*sigma + j*2*xLim*sigma/(nx-1);
         yVal[j] = tCurve(mu, sigma, dof, xVal[j]);
      }



// set up the layout

      setLayout(new BorderLayout());
// add the title to the north panel
      t = new Panel();
      t.setLayout(new FlowLayout(FlowLayout.CENTER));
      Font titleFont = new Font("TimesRoman", Font.BOLD, 18);
      FontMetrics titleFontMetrics = getFontMetrics(titleFont);
      t.setFont(titleFont);
      tLabel = new Label(title);
      t.add(tLabel);
      add("North",t);

// the center panel gets the canvas
      h = new Panel();
      h.setLayout(new BorderLayout());
      polyGram = new PolyPlot(xVal, yVal,
         hiLiteLo, tCurve(mu, sigma, hiLiteLo),
         hiLiteHi, tCurve(mu, sigma, hiLiteHi));
      h.add("Center",polyGram);

// the South panel gets the scroll controls
      Panel p = new Panel();
      p.setLayout(new GridLayout(4, 2));
      p.add(hiLabel = new Label(""));
      hi = new myScrollbar(Scrollbar.HORIZONTAL);
      p.add(hi);
      p.add(loLabel = new Label(""));
      lo = new myScrollbar(Scrollbar.HORIZONTAL);
      p.add(lo);
      p.add(dofLabel = new Label(""));
      dofBar = new myScrollbar(Scrollbar.HORIZONTAL);
      p.add(dofBar)

      p.add(areaLabel = new Label(""));
      p.add(nullLabel = new Label(""));

      add("Center", h);
      add("South",p);
      slideScale = 500/(xVal[nx-1]-xVal[0]);

      hi.setValues((int) ((hiLiteHi-xVal[0])*slideScale), 1, 0, 500);
      lo.setValues((int) ((hiLiteLo-xVal[0])*slideScale), 1, 0, 500);


      hiLiteLo = (((double) lo.getValue())/slideScale+xVal[0]);
      hiLiteHi = (((double) hi.getValue())/slideScale+xVal[0]);
      loLabel.setText(" Lower Boundary: " + hiLiteLo);
      hiLabel.setText(" Upper Boundary: " + hiLiteHi);
      if (hiLiteLo >= hiLiteHi) hiLiteLo = hiLiteHi;
      areaLabel.setText(" Hilighted area: " + doubleToPct(hiLitArea()));
      polyGram.redraw(hiLiteLo, tCurve(mu, sigma, hiLiteLo),
         hiLiteHi, tCurve(mu, sigma, hiLiteHi));

    }// ends init

    public boolean handleEvent(Event e)
    {
        if (e.id == Event.WINDOW_DESTROY) System.exit(0);
        else if (e.id == Event.SCROLL_ABSOLUTE
                 || e.id == Event.SCROLL_LINE_DOWN
                 || e.id == Event.SCROLL_LINE_UP
                 || e.id == Event.SCROLL_PAGE_DOWN
                 || e.id == Event.SCROLL_PAGE_UP)
        {
            hiLiteLo = (((double) lo.getValue())/slideScale+xVal[0]);
            hiLiteHi = (((double) hi.getValue())/slideScale+xVal[0]);
            loLabel.setText(" Lower Boundary: " + hiLiteLo);
            hiLabel.setText(" Upper Boundary: " + hiLiteHi);

            if (hiLiteLo >= hiLiteHi) hiLiteLo = hiLiteHi;
            areaLabel.setText(" Hilighted area: " + doubleToPct(hiLitArea()));
		    polyGram.redraw(hiLiteLo, tCurve(mu, sigma, hiLiteLo),
                  hiLiteHi, tCurve(mu, sigma, hiLiteHi));
            return true;
        }

        return super.handleEvent(e);
    }// ends handle.Event



   private double hiLitArea()
   {
      double area = 0;
      for (int i=0; i < nx-1; i++)
      {
         if( xVal[i]  > hiLiteHi ||  xVal[i+1] <= hiLiteLo)
         {// not in the hilighted region
         }
         else if (xVal[i] >= hiLiteLo && xVal[i+1] <= hiLiteHi)
         {// entirely hilit
	         area += trapZoid(
	            xVal[i], yVal[i], xVal[i+1], yVal[i+1], xVal[i+1]);
         }
         else if (xVal[i] >= hiLiteLo && xVal[i+1] > hiLiteHi)
         {// bottom is hilit
		     area += trapZoid(
	            xVal[i], yVal[i], xVal[i+1], yVal[i+1], hiLiteHi);
	     }
         else if (xVal[i] <= hiLiteLo && xVal[i+1] <= hiLiteHi)
	     {// top is hilit--area = total-bottom
		     area += trapZoid(
	            xVal[i], yVal[i], xVal[i+1], yVal[i+1], xVal[i+1]);
	         area -= trapZoid(
	            xVal[i], yVal[i], xVal[i+1], yVal[i+1], hiLiteLo);
	     }
         else if (xVal[i] <= hiLiteLo && xVal[i+1] >= hiLiteHi)
	     {// middle is hilit: area = (area to hiLiteHi) - (area to hiLiteLo)
		     area += trapZoid(
	            xVal[i], yVal[i], xVal[i+1], yVal[i+1], hiLiteHi);
	         area -= trapZoid(
	            xVal[i], yVal[i], xVal[i+1], yVal[i+1], hiLiteLo);
         }
      }
      return area;
   }// ends HiLitArea


   private double trapZoid(
        double xLo, double yLo, double xHi, double yHi, double x)
   {
/* linearly interpolates between (xLo, yLo) and (xHi, yHi) and integrates
   the result up to the point x \in (xLo, xHi)
*/
      double m = (yHi - yLo)/(xHi - xLo);
      double area = yLo * (x - xLo);
      area += (x-xLo)*(x-xLo)*m/2;
      return area;
   }// ends trapZoid

   public double tCurve(double m, double s, double x)
   {
       double p = 1/(Math.sqrt(2*Math.PI)*s);
       p *=  Math.exp(-(x-m)*(x-m)/(2*s*s));
       return p;
   } // ends tCurve

   public int strToInt(String s)
   {
      int v = 0;
      try
      {
        v = Integer.parseInt(s.trim());
      }
      catch (NumberFormatException e)
      {
        v = 0;
      }
      return v;
   }// ends strToInt

   public double strToDouble(String s)
   {
      double v = 0;
      try
      {
         v = new Double(s.trim()).doubleValue();
      }
         catch (NumberFormatException e)
      {
         v = 0;
      }
      return v;
   }// ends strToDouble

     public String doubleToPct(double v)
     {
         String s = "" + 100*v;
         String a = s.substring(0,(int) Math.min(s.length(),4));
         a = a + "%";
         return a;
     } // ends doubleToPct


}// ends class NormHiLite

class PolyPlot extends Canvas
{
   private double[] yVal;
   private double[] xVal;
   private double hiLiteLo;
   private double yLiteLo;
   private double hiLiteHi;
   private double yLiteHi;
   private Polygon plot;
   private Polygon fill;

   private final int hOffset = 20;
   private final int xLabels = 11;


   PolyPlot()
   {
        resize(300, 200);
   }

   PolyPlot(double[] x, double[] y,
        double hll, double yl, double hlh, double yh)
    {
        xVal   = x;
        yVal    = y;
        hiLiteLo = hll;
        yLiteLo = yl;
        hiLiteHi = hlh;
        yLiteHi = yh;
        repaint();
    }

    public void redraw(double hll, double yl, double hlh, double yh)
    {
        hiLiteLo = hll;
        yLiteLo = yl;
        hiLiteHi = hlh;
        yLiteHi = yh;
        repaint();
    }// ends redraw

    public void paint(Graphics g)
    {
      Font labelFont = new Font("TimesRoman", Font.PLAIN, 12);
      FontMetrics labelFontMetrics = g.getFontMetrics(labelFont);

      int nx = xVal.length;
      double minY = 0;
      double maxY = 0;
      for (int i = 0; i < yVal.length; i++)
      {  if (minY > yVal[i]) minY = yVal[i];
         if (maxY < yVal[i]) maxY = yVal[i];
      }

      Dimension d = size();
      int wd = d.width;
      int ht = d.height;
      int botSpace = labelFontMetrics.getHeight();

      double xScale = (wd-hOffset) / (xVal[nx-1]-xVal[0]);
      double yScale = (ht - botSpace);
      if (maxY != minY)
      {
         yScale /= (maxY - minY);
      }
      else
      {
         yScale /= 2*maxY;// put the line in the middle
      }
      g.setFont(labelFont);

      plot = new Polygon();
      fill = new Polygon();

// initialize the polygon to be filled
      int x0 = (int) ((hiLiteLo - xVal[0])*xScale + hOffset/2);
      int y0 = ht-botSpace;
      fill.addPoint(x0, y0);
      int ylo = (int) (ht - botSpace + (yLiteLo-minY)*yScale);
      fill.addPoint(x0, ylo);

// make the plot and to-be-filled polygons
      for (int i = 0; i < nx; i++)
      {
         int x1 = (int) ((xVal[i]-xVal[0])*xScale + hOffset/2);
         int y1 = ht - botSpace - (int) (yScale * (yVal[i] - minY));
         plot.addPoint(x1, y1);

// construct the polygon to fill
         if (xVal[i] >= hiLiteLo && xVal[i] <= hiLiteHi)
            fill.addPoint(x1, y1);
     }

// add the last two points to the polygon to fill
      x0 = (int) ((hiLiteHi - xVal[0])*xScale + hOffset/2);
      int yhi = (int) (ht - botSpace + (yLiteHi - minY) * yScale);
      fill.addPoint(x0, yhi);
      fill.addPoint(x0, y0);


// make the x axis heavier to hide roundoff errors
      for (int i=-1; i<2; i++)
         g.drawLine(hOffset/2,ht-botSpace+i,wd-hOffset/2,ht-botSpace+i);

// draw and fill the polygons
      g.setColor(Color.yellow);
      g.fillPolygon(fill);
      g.setColor(Color.black);
      g.drawPolygon(plot);

// draw the axis labels
      int y = ht - labelFontMetrics.getDescent();
      int ticWidth = labelFontMetrics.stringWidth("|");
      double here = Math.floor(xVal[0]);
      double there = Math.ceil(xVal[nx-1]);
      int dL = (int) ( (there - here )/ (xLabels-1) );
      int h = (int) Math.floor(here);
      for (int i=0; i < xLabels ; i++)
      {
        h = (int) Math.floor(here);
        int labelWidth = labelFontMetrics.stringWidth("" + h );
        int xHere = (int) ((h - xVal[0]) * xScale +hOffset/2);
        g.drawString("" + h, xHere - labelWidth/2, y );
        g.drawString("|", xHere - ticWidth/2 , ht-botSpace);
        here += dL;
      }

   }// ends paint



}// ends PolyPlot


