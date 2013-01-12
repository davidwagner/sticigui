/* class NormHiLite extends Applet
@author P.B. Stark http://statistics.berkeley.edu/~stark/
@version 0.9
@copyright 1997-2001 by P.B. Stark. All rights reserved.


Plots the standard normal curve, the Student-t density, or the chi-square density.
Scrollbars let you highlight part of
the curve, and the area of the selected part of the curve is displayed.

Last Modified 14 March 2001
*/

import java.awt.*;
import java.applet.*;
import java.util.*;
import java.io.*;
import PbsGui.*;
import PbsStat.*;

public class NormHiLite extends Applet {
   private final int nx = 500;
   private int digits = 6; // number of digits in text bars
   private int decimals = 3; // decimal places in text bars
   private double[] yVal;
   private double[] xVal;
   private Hashtable distType = new Hashtable(3);
   private String currDist;
   private double hiLiteLo;
   private double hiLiteHi;
   private double xLimLo;
   private double xLimHi;
   private double yLimLo;
   private double yLimHi;
   private double[] yLims;
   private final double maxY = 50;
   private final double maxDf = 350;
   private double mu = 0;
   private double sigma = 1;
   private int df = 2;
   private int minDf = 1;

   private TextBar hi;
   private TextBar lo;
   private TextBar dfBar;
   private Label areaLabel;
   private Label tLabel;
   private Panel h;
   private Panel t;
   private Panel b;
   private Panel span;
   XYPlot polyGram;

   public void init() {
      super.init();
      setBackground(Color.white);
      distType.put("normal","Standard Normal Curve");
      distType.put("Student-t","Student's t Curve");
      distType.put("chi-square","Chi-Square Curve");
      df = Format.strToInt(getParameter("df"),2);
      hiLiteLo = Format.strToDouble(getParameter("hiLiteLo"),0);
      hiLiteHi = Format.strToDouble(getParameter("hiLiteHi"),0);
      mu = Format.strToDouble(getParameter("mean"),0);
      sigma = Format.strToDouble(getParameter("SD"),1);
      yVal = new double[nx];
      xVal = new double[nx];
      currDist = getParameter("distribution");
      if (currDist == null || !distType.containsKey(currDist)) {currDist = "normal";}
      setXYvals();

// set up the layout
      setLayout(new BorderLayout());
// add the title to the north panel
      t = new Panel();
      t.setLayout(new FlowLayout(FlowLayout.CENTER));
      Font titleFont = new Font("TimesRoman", Font.BOLD, 18);
      FontMetrics titleFontMetrics = getFontMetrics(titleFont);
      t.setFont(titleFont);
      tLabel = new Label((String) distType.get(currDist));
      t.add(tLabel);
      add("North",t);

// the center panel gets the canvas
      h = new Panel();
      h.setLayout(new BorderLayout());
      yLims = getYs();
      polyGram = new XYPlot(xVal, yVal, hiLiteLo, yLims[0], hiLiteHi, yLims[1]);
      polyGram.axes(true, false);
      h.add("Center",polyGram);
// the South panel gets the scroll controls
      Panel p = new Panel();
      p.setLayout(new FlowLayout());
      DoubleValidator dv = new DoubleValidator();
      p.add(lo = new TextBar(hiLiteLo, xVal[0], xVal[nx-1], digits, decimals,
                             (Validator) dv, TextBar.TEXT_TOP, "Lower endpoint"));
      p.add(hi = new TextBar(hiLiteHi, xVal[0], xVal[nx-1], digits, decimals,
                             (Validator) dv, TextBar.TEXT_TOP, "Upper endpoint"));
      if (currDist != "normal") { // add control for degrees of freedom, if required
        IntegerValidator iv = new IntegerValidator();
        p.add(dfBar = new TextBar(df, minDf, maxDf, 3, 0, (Validator) iv,
                             TextBar.TEXT_TOP, "Degrees of Freedom"));
      }
      Panel b = new Panel();
      b.setLayout(new FlowLayout());
      b.add(areaLabel = new Label(" HighLighted Area: 000.00% "));

      Panel span = new Panel();
      span.setLayout(new GridLayout(2,1));
      span.add(p);
      span.add(b);

      add("Center", h);
      add("South",span);
      if (hiLiteLo >= hiLiteHi) hiLiteLo = hiLiteHi;
      areaLabel.setText(" Highlighted area:   " + Format.doubleToPct(hiLitArea()) + "   ");
      polyGram.redraw(hiLiteLo, yLims[0], hiLiteHi, yLims[1]);
    }// ends init

    public boolean handleEvent(Event e) {
        if (e.id == Event.WINDOW_DESTROY) System.exit(0);
        else if (e.id == Event.SCROLL_ABSOLUTE
                 || e.id == Event.SCROLL_LINE_DOWN
                 || e.id == Event.SCROLL_LINE_UP
                 || e.id == Event.SCROLL_PAGE_DOWN
                 || e.id == Event.SCROLL_PAGE_UP) {
            hiLiteLo = lo.getValue();
            hiLiteHi = hi.getValue();
            if (hiLiteLo >= hiLiteHi) hiLiteLo = hiLiteHi;
            areaLabel.setText(" Highlighted area: " + Format.doubleToPct(hiLitArea()));
            if (e.target == dfBar) {
                df = (int) dfBar.getValue();
                setXYvals();
                lo.setValues(hiLiteLo, xLimLo, xLimHi, digits, decimals);
                hi.setValues(hiLiteHi, xLimLo, xLimHi, digits, decimals);
            }
            yLims = getYs();
		    polyGram.redraw(xVal, yVal, hiLiteLo, yLims[0], hiLiteHi, yLims[1]);
            return true;
        }
        return super.handleEvent(e);
    }// ends handle.Event

   private void setXYvals() {
      if (currDist.equals("normal")) {
          xLimLo = -5;
          xLimHi = 5;
          for (int j = 0; j < nx; j++) {
              xVal[j] = xLimLo*sigma + j*(xLimHi-xLimLo)*sigma/(nx-1);
              yVal[j] = PbsStat.normPdf(mu, sigma, xVal[j]);
          }
      } else if (currDist.equals("Student-t")) {
          xLimLo = -6;
          xLimHi = 6;
          minDf = 1;
          if (df < minDf) {
              df = minDf;
          }
          for (int j = 0; j < nx; j++) {
              xVal[j] = xLimLo + j*(xLimHi-xLimLo)/(nx-1);
              yVal[j] = Math.min(PbsStat.tPdf(xVal[j],df),maxY);
              if (Double.isNaN(yVal[j])) {yVal[j] = maxY;}
          }
      } else if (currDist.equals("chi-square")) {
          xLimLo = 0;
          xLimHi = 6*df;
          minDf = 1;
          if (df < minDf) {
             df = minDf;
          }
          for (int j = 0; j < nx; j++) {
             xVal[j] = xLimLo + j*(xLimHi-xLimLo)/(nx-1);
             yVal[j] = Math.min(PbsStat.chi2Pdf(xVal[j],df),maxY);
             if (Double.isNaN(yVal[j])) {
                yVal[j] = maxY;
             }
          }
          yVal[0] = 0;
      } else {
            System.out.println("error 1 in NormHiLite.java: impossible value");
      }
      hiLiteLo = Math.min(Math.max(hiLiteLo,xLimLo),xLimHi);
      hiLiteHi = Math.max(Math.min(hiLiteHi,xLimHi),xLimLo);
   }

   private double[] getYs() {
      double[] v = new double[2];
      if (currDist.equals("normal")) {
         v[0] = PbsStat.normPdf(mu, sigma, hiLiteLo);
         v[1] = PbsStat.normPdf(mu, sigma, hiLiteHi);
      }
      else if (currDist.equals("Student-t")) {
         v[0] = PbsStat.tPdf(hiLiteLo,df);
         if (Double.isNaN(v[0]) || v[0] > maxY) v[0] = maxY;
         v[1] = PbsStat.tPdf(hiLiteHi,df);
         if (Double.isNaN(v[1]) || v[1] > maxY) v[1] = maxY;
      }
      else if (currDist.equals("chi-square")) {
         v[0] = PbsStat.chi2Pdf(hiLiteLo,df);
         if (Double.isNaN(v[0]) || v[0] > maxY) v[0] = maxY;
         v[1] = PbsStat.chi2Pdf(hiLiteHi,df);
         if (Double.isNaN(v[1]) || v[1] > maxY) v[1] = maxY;
      }
      return(v);
   }

   private double hiLitArea() {
      double area = 0;
      if (hiLiteHi > hiLiteLo) {
           if (currDist.equals("normal")) {
              area = PbsStat.normCdf(hiLiteHi) - PbsStat.normCdf(hiLiteLo);
           }
           else if (currDist.equals("Student-t")) {
              area = PbsStat.tCdf(hiLiteHi,dfBar.getValue()) -
                     PbsStat.tCdf(hiLiteLo,dfBar.getValue());
           }
           else if (currDist.equals("chi-square")) {
              area = PbsStat.chi2Cdf(hiLiteHi,dfBar.getValue()) -
                     PbsStat.chi2Cdf(hiLiteLo,dfBar.getValue());
           }
      }
      return(area);
   }// ends HiLitArea

   public void setLimits(double down, double up) {
      lo.setValue(down);
      hi.setValue(up);
      Event e = new Event(lo, Event.SCROLL_ABSOLUTE,"outside");
      this.handleEvent(e);
   }

   public void setLimits(String down, String up) {
      setLimits(Format.strToDouble(down,0),Format.strToDouble(up,0));
   }

   public void setDf(int d) {
      dfBar.setValue(d);
      Event e = new Event(lo, Event.SCROLL_ABSOLUTE,"outside");
      this.handleEvent(e);
   }

   public void setDf(String d) {
      setDf(Format.strToInt(d,2));
   }



}// ends class NormHiLite




