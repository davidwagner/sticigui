/**
package PbsGui

public class CiPlot extends Canvas

@author P.B. Stark http://statistics.berkeley.edu/~stark
@version 0.1
@copyright 1997, P.B. Stark. All rights reserved.

The graphics display of the confidence intervals for applet Ci.

*/

package PbsGui;
import java.awt.*;
import PbsStat.*;

public class CiPlot extends Canvas
{
   public final Dimension minimumSize = new Dimension(100,50);
   public Dimension preferredSize = minimumSize;

   private int nInt;                     // number of CIs
   private double[] center;              // centers of the intervals
   private double truth;                 // true mean
   private boolean showTruth;            // show line at the true mean?
   private double[] se;                  // list of standard errors
   private double factor;                // multiple of se for CI length
   private boolean[] cover;              // does the interval cover?

   private double xScale;                // scale to convert x to pixels
   private double yScale;                // scale to convert y to pixels
   private double xMin;                  // smallest LH endpoint
   private double xMax;                  // largest RH endpoint

   private int ht;                       // height of the canvas
   private int wd;                       // width of the canvas
   private int digits = 3;               // digits in axis labels
   private double[] xTic;                // tic marks for x axis


   protected Font labelFont = new Font("TimesRoman", Font.PLAIN, 12);
   protected FontMetrics labelFontMetrics = getFontMetrics(labelFont);
   protected int botSpace = labelFontMetrics.getHeight();
   protected int labelY;
   protected int hOffset = 2*labelFontMetrics.stringWidth("-00.00");
   protected Color in = Color.green;    // color of CI's that cover
   protected Color out = Color.red;     // color of CI's that don't cover
   protected Color hide = Color.yellow; // color when truth is unknown
   protected int thick = 2;             // thickness of the CI rectangles
   protected int nLabels = 10;          // maximum number of x-axis labels

    public CiPlot()
    {
    }

    public CiPlot(double tr, boolean sT, double[] ctr, double[] s,
            double fac)
    {
        truth = tr;
        showTruth = sT;
 		center = ctr;
		se = s;
		factor = fac;
		ciInit();
		repaint();
    }


	protected void ciInit()
	{
	    if (center == null || center.length == 0)
	    {
	        nInt = 0;
	        xMin = 0;
	        xMax = 1;
	    }
	    else
	    {
	        nInt = center.length;
	        xMin = truth;
	        xMax = xMin;
	       	cover = new boolean[nInt];
		    double lo;
		    double hi;
            for (int i = 0; i < nInt; i++)
            {
    		   lo = center[i] - factor*se[i];
    		   hi = center[i] + factor*se[i];
    		   if ( lo <= truth && truth <= hi )
    		   {
    		       cover[i] = true;
    		   }
    		   else
    		   {
    		       cover[i] = false;
    		   }
               if (xMin > lo) xMin = lo;
               if (xMax < hi) xMax = hi;
		    }
		}
		Dimension d = size();
		ht = d.height;
        wd = d.width;
        labelY = ht - labelFontMetrics.getDescent();
        xTic = Format.niceAxis(xMin, xMax);
        xScale = ((double) (wd-hOffset)) / (xTic[1]-xTic[0]);
        yScale = ((double) (ht - botSpace - 2*thick)) / (nInt+1);

    } // ends ciInit()


    public void setColor(Color o1, Color i1, Color h1)
    {
        out = o1;
        in = i1;
        hide = h1;
    }

	public void setThick(int t)
	{
	    thick = t;
    }

    public void redraw(double[] ctr)
    {
        redraw(truth, showTruth, ctr, se, factor);
    } // ends redraw(double[])

    public void redraw(double fac)
    {
        redraw(truth, showTruth, center, se, fac);
    }

    public void redraw(double[] ctr, double[] s, double fac)
    {
        redraw(truth, showTruth, ctr, s, fac);
    }

    public void redraw(double[] ctr, double s, double fac)
    {
	    se = new double[ctr.length];
		for (int i = 0; i < ctr.length; i++)
		{
		    se[i] = s;
	    }
		redraw(truth, showTruth, ctr, se, fac);
    }

    public void redraw(double tr, boolean sT, double[] ctr, double[] s,
                       double fac)
    {
        truth = tr;
        showTruth = sT;
		center = ctr;
		se = s;
		factor = fac;
		ciInit();
        repaint();
    } // ends redraw(double[], double[], double[], double)

    private int xToPix(double x)
    {
        int p = (int) (( x - xTic[0] )*xScale + hOffset/2);
        return p;
    }

    private int yToPix(double y)
    {
        int p = ht - botSpace - (int)( y  * yScale);
        return p;
    }

    public void setDigits(int digits)
    {
        this.digits = digits;
    }

    public int getDigits()
    {
        return(digits);
    }

    public void paint(Graphics g)
    {

      int y = ht - botSpace;
      g.setFont(labelFont);
      if ( nInt > 0 )
      {
          int x = 0;
          double lo = 0;
          double hi = 0;
          int wide = 0;
          for (int i = 0; i < nInt; i++)
          {
             lo = center[i] - factor*se[i];
        	 hi = center[i] + factor*se[i];
             x = xToPix(lo);
        	 wide = xToPix(hi) - x;
        	 y = yToPix(i);
        	 if (showTruth)
        	 {
        	    if (cover[i])   // fill the rectangle the appropriate color
        	    {
        	        g.setColor(in);
        		    g.fillRect(x, y-thick, wide, thick);
                }
        	    else  // fill the rectangle with the other color
        	    {
        	        g.setColor(out);
        		    g.fillRect(x,y-thick, wide, thick);
        	    }
        	 }
        	 else
        	 {
        	    g.setColor(hide);
        	    g.fillRect(x, y-thick, wide, thick);
        	 }
        	 g.setColor(Color.black);
             g.drawRect(x, y-thick, wide, thick);  // outline the rectangle
             g.fillRect(xToPix(center[i]) - thick/2, y-thick, thick, thick);
                                     // show the mean in black
          }
      }

// draw x-axis labels
      double xHere = xTic[0];
      String thisLabel;
      int lastLabelx = xToPix(xTic[0]) -
                       labelFontMetrics.stringWidth(Format.doubleToStr(xTic[0],digits))/2 - 1;
      int x;
      while (xHere <= xTic[1])
      {
         x = xToPix(xHere);
         thisLabel = Format.doubleToStr(xHere,digits);
         int labelWidth = labelFontMetrics.stringWidth(thisLabel);
         if (x - labelWidth/2 > lastLabelx )
         {
            lastLabelx = x + labelWidth/2 + 5;
            g.drawString(thisLabel, x - labelWidth/2, labelY);
            g.drawLine(x, ht-botSpace - 10, x, ht - botSpace + 4);
         }
         xHere += xTic[2];
      }

// draw a line at the truth, if desired.
      if (showTruth)
      {
          g.setColor(Color.blue);
	      g.drawLine(xToPix(truth),yToPix(0),xToPix(truth),yToPix(nInt+1));
	  }
// make the x axis
      g.setColor(Color.black);
      g.drawLine(hOffset/2,ht-botSpace,wd-hOffset/2,ht-botSpace);

   } // ends paint

}