/** public class HistPanel extends Panel
@author P.B. Stark, stark@stat.berkeley.edu
@copyright 1997, all rights reserved.
@version 1.0

a panel that contains a histogram and controls for it, including superposing
a normal curve, and hilighting a region and calculating its area.
Either displays a histogram of data, or a binomial histogram
uses validated text entry for the limits to highlight, etc.

*/

import java.awt.*;
import java.io.*;

public class HistPanel extends Panel
{
   protected double[] data;
   protected int nBins;
   protected double[] count;
   protected double[] binEnd;
   protected double hiLiteLo;
   protected double hiLiteHi;

// for the normal curve
   protected boolean showNormal;
   protected double mu;
   protected double sd;
   protected int nx = 400;
   protected double[] xVal = new double[nx];
   protected double[] yVal = new double[nx];


// for the binomial histogram
   protected boolean binHist;
   protected int n;
   protected double p;

   private Label areaLabel;
   private Label normAreaLabel;

   protected Panel[] myPanel = new Panel[6];


   protected TextBar hi;
   protected TextBar lo;
   protected TextBar pBar;
   protected TextBar nBar;
   protected Button showNormalButton;
   protected Histogram hist;

   Histogram()
   {
        this(????)
   }

   Histogram(double[] data, boolean binHist, boolean showNormal,
             double[] binEnd, int nBins, double p,
             double hiLiteLo, double hiLiteHi)
   {
        this.data = data;
        this.binHist = binHist;
        this.showNormal = showNormal;
        this.binEnd = binEnd;
        this.nBins = nBins;
        this.p = p;
        this.hiLiteLo = hiLiteLo;
        this.hiLiteHi = hiLiteHi;
        showNormal = false;
        init();
   }

   Histogram(double[] data, int nBins)
   {
        this(data, false, false, null, nBins, null, 0, 0);
   }

   Histogram(int n, double p)
   {
        this(null, true, false, null, n+1, p, 0, 0);
   }


   public void init()
   {
      super.init();
      for (int i = 0; i < myPanel.length; i++)
      {
          myPanel[i] = new Panel();
      }
      showNormal = true;

      count = new double[nBins];
      binEnd = new double[nBins+1];


      if (!binHist)
      {// there should be some data out there!
         for (int j = 0; j < nBins; j++)
         {
            count[j] = strToDouble(getParameter("count_" + (j+1)));
            binEnd[j] = strToDouble(getParameter("end_" + (j+1)));
         }
         binEnd[nBins] = strToDouble(getParameter("end_" + (nBins+1)));
      }
      else
      {
         makeBin(); // assume the caller wants a binomial; override sd&mu
         makePhi();
      }


// find bin heights from areas; normalize total area to unity
      double tot=0;
      for (int i=0; i < nBins; i++) tot=tot+count[i];
      if (Math.abs(tot) < 1.0D-5 || tot < 0 )
      {
         System.out.println("This is probably not a histogram; "+
            "the total area is small or negative."  +
            "Check your parameter settings.");
         return;
      }
      for (int i=0; i < nBins; i++)
      {
         count[i] /= tot*(binEnd[i+1]-binEnd[i]);
      }

// set up the normal curve,
      showNormalButton = new Button();
      makePhi();
      showNormalButton.setLabel("Show Normal Curve");



// set up the layout

    setLayout(new BorderLayout());

//the center panel gets the histogram in a BorderLayout
    myPanel[1].setLayout(new BorderLayout());
    hist = new Histogram();
    myPanel[1].add("Center", hist);

// bottom center gets scroll bars.
    Validator v1;
    Validator v2;
    if (binHist)
    {
        v1 = new HalfIntegerValidator();
        v2 = new HalfIntegerValidator();
    }
    else
    {
        v1 = new DoubleValidator();
        v2 = new DoubleValidator();
    }
    myPanel[2].setLayout(new FlowLayout(FlowLayout.CENTER));
    lo = new TextBar(hiLiteLo, binEnd[0], binEnd[nBins], 5,
                     (Validator) v1, TextBar.TEXT_TOP,"Lower Boundary");
    myPanel[2].add(lo);
    hi = new TextBar(hiLiteHi, binEnd[0], binEnd[nBins], 5,
                     (Validator) v2, TextBar.TEXT_TOP, "Upper Boundary");
    myPanel[2].add(hi);
    myPanel[1].add("South", myPanel[2]);

// labels for area go in the south of the main panel.
    myPanel[3].setLayout(new FlowLayout(FlowLayout.CENTER));
    myPanel[3].add(showNormalButton);
    myPanel[3].add(areaLabel = new Label("Hilighted area:     " +
                   doubleToPct(hiLitArea())));
    myPanel[3].add(normAreaLabel = new Label());
    if (binHist)
    {
        myPanel[4].setLayout(new FlowLayout(FlowLayout.CENTER));
        myPanel[4].add(nBar = new TextBar(n, 1, 100, 3,
                        (Validator) new IntegerValidator(),
                        TextBar.TEXT_TOP,"n:"));
        myPanel[4].add(pBar = new TextBar(p*100, 0, 100, 4,
                        (Validator) new DoubleValidator(),
                        TextBar.TEXT_TOP,"p (%):"));
        myPanel[5].setLayout(new BorderLayout());
        myPanel[5].add("North",myPanel[3]);
        myPanel[5].add("Center",myPanel[4]);
    }
    else
    {
        myPanel[5].setLayout(new FlowLayout(FlowLayout.CENTER));
        myPanel[5].add(myPanel[3]);
    }
    normAreaLabel.setText(" Normal approx.:     " +
            doubleToPct(normHiLitArea()));
    add("Center", myPanel[1]);
    add("South",myPanel[5]);
    add("North", myPanel[0]);

    validate();
    hist.redraw(binEnd, count, hiLiteLo, hiLiteHi, showNormal, xVal, yVal);
    validate();

    }

    public boolean handleEvent(Event e)
    {
        if (e.id == Event.WINDOW_DESTROY) System.exit(0);
        else if (e.id == Event.SCROLL_ABSOLUTE ||
                 e.id == Event.SCROLL_PAGE_UP ||
                 e.id == Event.SCROLL_PAGE_DOWN ||
                 e.id == Event.SCROLL_LINE_UP ||
                 e.id == Event.SCROLL_LINE_DOWN )

        {
            if (e.target == lo || e.target == hi)
            {
                hiLiteLo = lo.getValue();
                hiLiteHi = hi.getValue();
                if (hiLiteLo >= hiLiteHi) hiLiteLo = hiLiteHi;
                areaLabel.setText("Hilighted area: " + doubleToPct(hiLitArea()));
                if (showNormal) normAreaLabel.setText("Normal Approx.: "
                       + doubleToPct(normHiLitArea()));
		        hist.redraw(hiLiteLo,hiLiteHi);
                return true;
            }
            else if (e.target == nBar || e.target == pBar)
            {
                n = (int) nBar.getValue();
                nBins = n+1;
                p = pBar.getValue()/100;
                makeBin();
                makePhi();
                areaLabel.setText("Hilighted area: " + doubleToPct(hiLitArea()));
                if (showNormal) normAreaLabel.setText("Normal Approx.: "
                       + doubleToPct(normHiLitArea()));
                if (hiLiteHi > binEnd[nBins]) hiLiteHi = binEnd[nBins];
                if (hiLiteLo < binEnd[0]) hiLiteLo = binEnd[0];
                hi.setValues(hiLiteHi, -.5, n+0.5, 5);
                lo.setValues(hiLiteLo, -.5, n+0.5, 5);

                hist.redraw(binEnd, count, hiLiteLo, hiLiteHi, showNormal,
                            xVal, yVal);
            }
        }
        else if (e.id == Event.ACTION_EVENT)
        {
            if (e.arg == "Show Normal Curve")
            {
                showNormal = true;
                showNormalButton.setLabel("Hide Normal Curve");
                hist.redraw(binEnd, count, hiLiteLo, hiLiteHi, showNormal,
                            xVal, yVal);
            }
            else if (e.arg == "Hide Normal Curve")
            {
                showNormal = false;
                showNormalButton.setLabel("Show Normal Curve");
                hist.redraw(binEnd, count, hiLiteLo, hiLiteHi, showNormal,
                            xVal, yVal);
            }
        }
        return super.handleEvent(e);
    }

    private double hiLitArea()
    {
        double area = 0;
        for (int i=0; i < nBins; i++)
        {
            if( binEnd[i]  > hiLiteHi ||  binEnd[i+1] <= hiLiteLo)
            {
            }
            else if (binEnd[i] >= hiLiteLo && binEnd[i+1] <= hiLiteHi)
            {
	            area += count[i]*(binEnd[i+1]-binEnd[i]);
            }
            else if (binEnd[i] >= hiLiteLo && binEnd[i+1] > hiLiteHi)
            {
		        area += count[i]*(hiLiteHi - binEnd[i]);
	        }
            else if (binEnd[i] <= hiLiteLo && binEnd[i+1] <= hiLiteHi)
	        {
		        area += count[i]*(binEnd[i+1]-hiLiteLo);
	        }
            else if (binEnd[i] < hiLiteLo && binEnd[i+1] > hiLiteHi)
	        {
		        area += count[i]*(hiLiteHi - hiLiteLo);
            }
        }
        return area;
   }

   private void makeBin()
   {
        nBins = n+1;
        count = new double[nBins];
        count[0] = Math.pow((1-p),n);
        binEnd = new double[nBins+1];

        binEnd[0] = -0.5;
        for ( int i=1; i < nBins; i++)
        {
             if (p < 1)
             {
                 count[i] = count[i-1]*p*(n-i+1)/((1-p)*i);
             }
             else
             {
                 count[i] = 0;
             }
             binEnd[i] = i-0.5;
        }
        binEnd[nBins] = n + 0.5;
        if ( p == 1)
        {
             count[nBins - 1] = 1;
        }
        sd = Math.sqrt(n*p*(1-p));
        mu = n*p;
   }// ends makeBin


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
   }

   public String doubleToPct(double v)
   {
      String s = "" + 100*v;
      String a = s.substring(0,(int) Math.min(s.length(),4));
      a = a + "%";
      return a;
   } // ends doubleToPct

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


   public String doubleToStr(double x, int p)
   { // finds the string representation of x with at most p decimal places
       int dig = (int) (Math.floor( Math.log( Math.abs(x))
                             /Math.log( 10 )));
       if (dig < 0) dig = 1;
       String lbl = ("" + x);
       int nChars = lbl.length();
       if (x > 0) lbl = lbl.substring(0, Math.min(dig+p+1,nChars));
       else lbl = lbl.substring(0, Math.min(dig+p+2, nChars));
       return lbl;
   } // ends doubleToStr

   private double normHiLitArea()
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
             {// top is hilit: area = total-bottom
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
   }// ends normHiLitArea


   private void makePhi()
   {
       for (int i = 0; i < nx; i++)
       {
           xVal[i] = binEnd[0] + i*(binEnd[nBins]-binEnd[0])/(nx-1);
           yVal[i] = phiCurve(mu, sd, xVal[i]);
       }
   }

   public double phiCurve(double m, double s, double x)
   {
       double p = 1/(Math.sqrt(2*Math.PI)*s);
       p *=  Math.exp(-(x-m)*(x-m)/(2*s*s));
       return p;
   } // ends phiCurve

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

   public static void sort(double[] a)
   { // shell sort double vector in place
        int n = a.length;
        int incr=n/2;
        while ( incr >= 1 )
        {   for (int i = incr; i < n ; i++ )
            {   double temp = a[i];
                int j=i ;
                while ( j >= incr && temp < a[j-incr])
                {   a[j]=a[j-incr];
                    j -= incr;
                }
                a[j] = temp;
            }
            incr /= 2;
        }
    }

}// ends class HistHiLite

