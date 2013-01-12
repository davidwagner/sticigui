/**
@author P.B. Stark statistics.berkeley.edu/~stark
@version 0.9
@copyright 1997-2001, P.B. Stark. All rights reserved.
Last modified 14 March 2001.

An applet to study the distribution of the sum or average of draws
from a box.

**/

import java.awt.*;
import java.applet.*;
import java.util.*;
import PbsGui.*;
import PbsStat.*;

public class SampleMean extends Applet {

    /* want controls for sample size, number of samples to take,
    number of bins, show normal approximation, toggle sum/average
    take sample.
    */

    private TextBar sampleSizeBar; // size of each sample
    private TextBar samplesToTakeBar; // number of samples to take
    private TextBar binBar; // number of bins in the histogram
    private TextBar lo;
    private TextBar hi;
    String[] buttonLabel = {"Show Normal Curve",
                            "Take Sample","Box Histogram",
                           };
    protected Button[] myButton = new Button[buttonLabel.length];
    protected Panel[] myPanel = new Panel[12];

    private TextArea box; // holds the population.

    private Label popMeanLabel; // to display the population mean
    private Label popSdLabel; // to display the population SD
    private Label sampleMeanMeanLabel; // to display mean of sample means
    private Label normalSdLabel; // to display theor. SD of sample mean
    private Label samplesToTakeLabel; // display number of samples to take next
    private Label samplesSoFarLabel;// number of samples of current size taken
    protected Label titleLabel;
    protected Font titleFont = new Font("TimesRoman", Font.PLAIN, 12);
    protected Font labelFont = new Font("ComputerModern",Font.PLAIN, 10);
    protected Label areaLabel;
    protected Label normAreaLabel;

    Histogram hist;
    private String title;

    private double[] xVal; // x coords of normal approx. to sample mean
    private double[] yVal; // y coords of ditto.
    private int nPop; // number of elements in the population
    private int sampleSize; // size of current sample
    private int samplesToTake; // number of samples to take of that size
    private int samplesSoFar; // number of samples taken so far
    private boolean showNormal = false; // show normal approximation toggle
    private boolean sampleSum; // toggle sample sum vs. sample mean
    private boolean binControls = true; // add the bin controls?
    private boolean normalControls = true; // add normal curve button and label?
    private boolean boxEditable = true; // are the contents of the box editable?
    private boolean toggleMean = true; // add button to toggle between sample mean and sum?
    private boolean showBoxHist = true; // show histogram of the numbers in the box?
    private boolean boxHistControl = true; // show button to turn box histogram on and off?
    private Choice meanChoice = new Choice();

    private int nBins;      // number of bins for histogram
    private int nDigs = 4;  // number of digits in text bars
    private double xMin;    // lower limit for histogram
    private double xMax;    // upper limit for histogram
    private double EX;      // expected value of the variable plotted
    private double SE;      // standard error of the variable plotted
    private double popMin;  // smallest value in pop.
    private double popMax;  // largest value in pop.
    private double[] pop;   // elements of the population
    private double[] sample; // elements of the current sample
    double[] binEnd;        // bin endpoints
    double[] countPop;      // areas of the bins for the pop. histogram
    double[] countSample;   // areas of bins for the hist. of sample means
    private double popMean; // the population mean
    private double[] sampleMean; // the history of sample means
    private double popSd;   // the population SD

    private double hiLiteLo; // lower limit of hilighting
    private double hiLiteHi; // upper limit of highlighting

    private final int maxSamples = 10000; // max number of samples
    private final int maxSamplesToTake = maxSamples;
    private final int maxSampleSize = 500;
    private final int maxBins = 100; // max bins in histogram
    private final int nx = 500; // number of points in the normal curve

    public void init() {
        super.init();
        setBackground(Color.white);
        for (int i = 0; i < myPanel.length; i++) {
            myPanel[i] = new Panel();
            myPanel[i].setFont(labelFont);
        }
        for (int i = 0; i < myButton.length; i++) {
             myButton[i] = new Button(buttonLabel[i]);
        }

        showNormal = Format.strToBoolean(getParameter("showNormal"),false);
        if (showNormal) {
            myButton[0].setLabel("Hide Normal Curve");
        }
        binControls = Format.strToBoolean(getParameter("binControls"),true);
        normalControls = Format.strToBoolean(getParameter("normalControls"),true);
        nPop = Format.strToInt(getParameter("n"),5);
        sampleSize = Format.strToInt(getParameter("sampleSize"),1);
        samplesToTake = Format.strToInt(getParameter("samplesToTake"),1);
        sampleSum = Format.strToBoolean(getParameter("sampleSum"),true);
        nBins = Format.strToInt(getParameter("bins"),maxBins);
        boxEditable = Format.strToBoolean(getParameter("boxEditable"),true);
        toggleMean = Format.strToBoolean(getParameter("toggleMean"),true);
        showBoxHist = Format.strToBoolean(getParameter("showBoxHist"),true);
        boxHistControl = Format.strToBoolean(getParameter("boxHistControl"),false);

        sampleMean = new double[maxSamples];
        samplesSoFar = 0;

        if (getParameter("boxContents") != null) {
            Vector datVec = new Vector(5);
            StringTokenizer st = new StringTokenizer(getParameter("boxContents"),"\n\t, ",false);
            for (int j=0; st.hasMoreTokens(); j++) {
               datVec.addElement(new Double(Format.strToDouble(st.nextToken())));
            }
            nPop = datVec.size();
            pop = new double[nPop];
            for (int j=0; j < nPop; j++) {
                pop[j] = ( (Double) datVec.elementAt(j)).doubleValue();
            }
        } else {
            nPop = 5;
            pop = new double[nPop];
            for (int j=0; j < nPop; j++) {
                pop[j] = j+1;
            }
        }
        countSample = new double[nBins];
        setLayout(new BorderLayout());
        popMeanLabel = new Label("");
        popSdLabel = new Label("");
        sampleMeanMeanLabel = new Label("");
        normalSdLabel = new Label("SE(mean): ");
        areaLabel = new Label("");
        normAreaLabel = new Label("");
        samplesSoFarLabel = new Label("Samples: " + samplesSoFar);

        double[] vmx = PbsStat.vMinMax(pop);
        xMin = vmx[0];
        xMax = vmx[1];
// make the scrollbars
        lo = new TextBar(xMin, xMin, xMax, nDigs,
                     new DoubleValidator(), TextBar.TEXT_FIRST,"Area from");
        hi = new TextBar(xMin, xMin, xMax, nDigs,
                     new DoubleValidator(), TextBar.TEXT_FIRST, "to");
        binBar = new TextBar(nBins, 1, maxBins, nDigs,
                       (Validator) new PositiveIntegerValidator(),
                       TextBar.NO_BAR,"Bins:");
        samplesToTakeBar = new TextBar(samplesToTake, 1, maxSamplesToTake, nDigs,
                     new PositiveIntegerValidator(), TextBar.NO_BAR, "Take ");
        sampleSizeBar = new TextBar(sampleSize, 1, maxSampleSize, nDigs,
                     new PositiveIntegerValidator(), TextBar.NO_BAR, "Sample Size:");
        initPop();

// the layout begins.
        titleLabel = new Label();
        titleLabel.setFont(titleFont);
        myPanel[0].setLayout(new FlowLayout(FlowLayout.CENTER));
        myPanel[0].add(titleLabel);
        if (toggleMean) {
            titleLabel.setText("Distribution of the");
            meanChoice.addItem("Sample Mean");
            meanChoice.addItem("Sample Sum");
            myPanel[0].add(meanChoice);
            if (sampleSum) {
                meanChoice.select("Sample Sum");
            } else {
                meanChoice.select("Sample Mean");
            }
        } else {
            if (sampleSum) {
                titleLabel.setText("Distribution of the Sample Sum");
            } else {
                titleLabel.setText("Distribution of the Sample Mean");
            }
        }
        myPanel[0].add(myButton[1]);
        add("North", myPanel[0]);

        hist = new Histogram(); // change the color scheme
        hist.setBackground(Color.white);
        myPanel[1].setLayout(new BorderLayout());
        myPanel[1].add("Center", hist);
        add("Center", myPanel[1]);

        myPanel[3].setLayout(new BorderLayout());
        myPanel[9].setLayout(new FlowLayout(FlowLayout.CENTER));
        myPanel[9].add(lo);
        myPanel[9].add(hi);
        myPanel[10].setLayout(new FlowLayout());
        myPanel[10].add(areaLabel);
        if (normalControls || showNormal) {
            myPanel[10].add(normAreaLabel);
            if (normalControls) {
                myPanel[10].add(myButton[0]);
            }
        }
        if (boxHistControl) {
            myPanel[10].add(myButton[2]);
            if (showBoxHist) {
                myButton[2].setLabel("No Box Histogram");
            }
        }
        myPanel[4].setLayout(new GridLayout(4,1));
        myPanel[4].add(popMeanLabel);
        myPanel[4].add(popSdLabel);
        myPanel[4].add(normalSdLabel);
        myPanel[4].add(samplesSoFarLabel);
        myPanel[11].setLayout(new FlowLayout(FlowLayout.CENTER));
        myPanel[11].add(myPanel[4]);
        add("West", myPanel[11]);

        myPanel[6].setLayout(new FlowLayout(FlowLayout.CENTER));
        myPanel[6].add(sampleSizeBar);
        myPanel[6].add(samplesToTakeBar);
        samplesToTakeLabel = new Label("samples");
        myPanel[6].add(samplesToTakeLabel);
        if (binControls) {
            myPanel[6].add(binBar);
        }
        myPanel[3].add("Center",myPanel[9]);
        myPanel[3].add("North",myPanel[10]);
        myPanel[3].add("South",myPanel[6]);
        add("South", myPanel[3]);

        myPanel[5].setLayout(new GridLayout(3,1));
        box = new TextArea(12,8);
        box.setEditable(boxEditable);
        box.setBackground(Color.white);
        add("East", box);
        String[] thePop = new String[nPop];
        for (int i = 0; i < nPop; i++) {
            thePop[i] = pop[i] + "\n"; // print the population in
            box.appendText(thePop[i]); // the text box.
        }
        initPop(); // calculate population parameters from population; set labels
        setBins();
        validate(); // wishful thinking
        showPlot(); // refresh the histogram
    }

    protected void initPop() {
// compute population statistics
        nPop = pop.length;
        popMean = 0;
        popSd = 0;
        if (pop.length == 0) {
            for ( int i= 0; i < nBins; i++) countPop[i] = 0;
            xVal = new double[1];
            yVal = new double[1];
            xVal[0] = 0;
            yVal[0] = 0;
            return;
        }
        popMean = PbsStat.Mean(pop);
        popSd = PbsStat.Sd(pop);
        double[] vmx = PbsStat.vMinMax(pop);
        popMin = vmx[0];
        popMax = vmx[1];
        if (sampleSum) {
            xMin = sampleSize * popMin; // these are the limits for the
            xMax = sampleSize * popMax; // histogram
        } else {
            xMin = popMin;
            xMax = popMax;
        }
// make the histogram of the population
        setBins(); // set the class intervals; make the counts
// reset the range of the hilite scrollbars
        setBars(xMin,xMin); // set the hilight scrollbar scales
// reset the labels
        popMeanLabel.setText("Ave(Box): " + Format.doubleToStr(popMean,3));
        popSdLabel.setText("SD(Box): " + Format.doubleToStr(popSd,3));
        sampleMeanMeanLabel.setText("Ave. of Sample Means: undef.");
        if (sampleSum) {
            SE = popSd*Math.sqrt(sampleSize);
            EX = sampleSize*popMean;
            normalSdLabel.setText("SE(sum): " +
                               Format.doubleToStr(SE,3)
                               + "  ");
        } else {
            SE = popSd/Math.sqrt(sampleSize);
            EX = popMean;
            normalSdLabel.setText("SE(mean): " +
                               Format.doubleToStr(SE,4)
                               + "  ");
        }
        myPanel[4].invalidate();
        myPanel[11].validate();
        setAreas();

    }

    public boolean handleEvent(Event e) {
        if (e.id == Event.WINDOW_DESTROY) {
            System.exit(0);
        } else if (e.id == Event.SCROLL_ABSOLUTE
                 || e.id == Event.SCROLL_LINE_DOWN
                 || e.id == Event.SCROLL_LINE_UP
                 || e.id == Event.SCROLL_PAGE_DOWN
                 || e.id == Event.SCROLL_PAGE_UP) {
            if (e.target == binBar) { // update # bins and redraw histogram
                nBins = (int) binBar.getValue();
                setBins(); // reset the class intervals, make the counts
                showPlot(); // refresh the histogram
                return(true);
            } else if (e.target == sampleSizeBar) { // clear history, reset sample size, redisplay pop histogram
                sampleSize = (int) sampleSizeBar.getValue();
                samplesSoFar = 0;
                samplesSoFarLabel.setText("Samples: " + samplesSoFar);
                double sSd = popSd/Math.sqrt(sampleSize);
                if (sampleSum) {
                    xMin = sampleSize * popMin; // reset histogram limits
                    xMax = sampleSize * popMax;
                    setBins();
                    setBars(xMin, xMin);
                }
                makePhi(); // make normal curve w/ new SD
                if (sampleSum) {
                    EX = sampleSize*popMean;
                    SE = popSd*Math.sqrt(sampleSize);
                    normalSdLabel.setText("SE(sum): " +
                            Format.doubleToStr(SE,3) + "  ");
                } else {
                    EX = popMean;
                    SE = popSd/Math.sqrt(sampleSize);
                    normalSdLabel.setText("SE(mean): " +
                            Format.doubleToStr(SE,4) + "  ");
                }
                myPanel[4].invalidate();
                myPanel[11].validate();
                setAreas();
                showPlot();
                return(true);
            } else if (e.target == samplesToTakeBar) {
                samplesToTake = (int) samplesToTakeBar.getValue();
                return(true);
            } else if (e.target == lo || e.target == hi) {
                hiLiteLo = lo.getValue();
                hiLiteHi = hi.getValue();
                if (hiLiteLo >= hiLiteHi) hiLiteLo = hiLiteHi;
                setAreas();
                showPlot();
                return(true);
            }
        } else if (e.target == box && e.id == Event.LOST_FOCUS) {
            Vector vPop = new Vector(5);
            String newBox = box.getText();
            StringTokenizer t = new StringTokenizer(newBox);
            String s;
            while (t.hasMoreTokens()) {
                s = t.nextToken();
                Double d = null;
                try {
                    d = new Double(s);
                    vPop.addElement(d);
                } catch (NumberFormatException exc) {
                }
            }
            pop = new double[vPop.size()];
            nPop = pop.length;
            for (int i = 0; i < nPop; i++) {
                pop[i] = ((Double) vPop.elementAt(i)).doubleValue();
            }
            initPop();
            samplesSoFar = 0;
            samplesSoFarLabel.setText("Samples: " +
                                       samplesSoFar);
            setAreas();
            showPlot();
            return(true);
        } else if (e.id == Event.ACTION_EVENT) {
            if (e.arg == "Take Sample") {
                int lim = maxSamples - samplesSoFar; // number possible
                for (int i = 0; i <
                     Math.min(samplesToTake, lim); i++) {
                        xBar();
                }
                samplesSoFarLabel.setText("Samples: " +
                                           samplesSoFar);
                samplesSoFarLabel.invalidate();
                myPanel[4].invalidate();
                validate();
                countSample = listToHist(sampleMean, samplesSoFar);
                setAreas();
                showPlot();
                return(true);
            } else if (e.target == meanChoice) {
                if (meanChoice.getSelectedItem() == "Sample Mean") {
                    sampleSum = false;
                } else if (meanChoice.getSelectedItem() == "Sample Sum") {
                    sampleSum = true;
                }
                if (!sampleSum) {
                    for (int i = 0; i < samplesSoFar; i++) {
                        sampleMean[i] /= sampleSize;
                    }
                    xMin = popMin;
                    xMax = popMax;
                    hiLiteLo /= sampleSize;
                    hiLiteHi /= sampleSize;
                    SE = popSd/Math.sqrt(sampleSize);
                    EX = popMean;
                    normalSdLabel.setText("SE(mean): " +
                               Format.doubleToStr(SE,4) + "  ");
                } else {
                    for (int i = 0; i < samplesSoFar; i++) {
                        sampleMean[i] *= sampleSize;
                    }
                    xMin = popMin * sampleSize;
                    xMax = popMax * sampleSize;
                    hiLiteLo *= sampleSize;
                    hiLiteHi *= sampleSize;
                    EX = sampleSize*popMean;
                    SE = popSd*Math.sqrt(sampleSize);
                    normalSdLabel.setText("SE(sum): " +
                                   Format.doubleToStr(SE,3) + "  ");
                }
                setBins();
                setBars(hiLiteLo, hiLiteHi);
                makePhi();
                setAreas();
                myPanel[4].invalidate();
                myPanel[11].validate();
                showPlot();
                return(true);
            } else if (e.arg == "Show Normal Curve") {
                showNormal = true;
                ((Button) e.target).setLabel("Hide Normal Curve");
                setAreas();
                showPlot();
                return(true);
            } else if (e.arg == "Hide Normal Curve") {
                showNormal = false;
                ((Button) e.target).setLabel("Show Normal Curve");
                setAreas();
                showPlot();
                return(true);
            } else if (e.arg == "Box Histogram") {
                showBoxHist = true;
                myButton[2].setLabel("No Box Histogram");
                showPlot();
                return(true);
            } else if (e.arg == "No Box Histogram") {
                showBoxHist = false;
                myButton[2].setLabel("Box Histogram");
                showPlot();
                return(true);
            }

        }
        return(super.handleEvent(e));
    }

    void showPlot() {
        if (showBoxHist) {
            hist.setColor(Color.blue, Color.blue,
                      Color.green.darker().darker(), Color.yellow);
            hist.setCurveColor(hist.REVERSE_COLOR);
        } else {
            hist.setColor(Color.green.darker().darker(), Color.yellow,
                      Color.blue, Color.blue);
            hist.setCurveColor(hist.DEFAULT_COLOR);
        }
        if (samplesSoFar > 0) {
               if (showBoxHist) {
                    hist.redraw(binEnd, countPop, countSample, hiLiteLo,
                             hiLiteHi, showNormal, xVal, yVal);
               } else {
                    hist.redraw(binEnd, countSample, hiLiteLo,
                           hiLiteHi, showNormal, xVal, yVal);
               }
        } else {
               if (showBoxHist) {
                    hist.redraw(binEnd, countPop, hiLiteLo,
                           hiLiteHi, showNormal, xVal, yVal);
               } else {
                    hist.redraw(binEnd, countSample, hiLiteLo,
                           hiLiteHi, showNormal, xVal, yVal);
               }
        }
    }

    void xBar() {
        double xb = 0;
        for (int i = 0; i < sampleSize; i++) {
             xb += pop[ (int) (Math.random()*nPop)];
        }
        if (!sampleSum)  {
            xb /= sampleSize;
        }
        sampleMean[samplesSoFar++] = xb;
        if (xb < xMin || xb > xMax) {
            xMin = Math.min(xb, xMin);
            xMax = Math.max(xb, xMax);
            setBins();
            setBars(hiLiteLo, hiLiteHi);
            makePhi();
        }
    }

    void setAreas() {
        areaLabel.setText(" Selected area: " + Format.doubleToPct(hiLitArea()));
        if (showNormal) {
            normAreaLabel.setText(" Normal approx: "
                 + Format.doubleToPct(normHiLitArea()));
        } else {
            normAreaLabel.setText("");
        }
        areaLabel.invalidate();
        normAreaLabel.invalidate();
        myPanel[3].validate();
    } // ends setAreas()

    void setBars(double l, double h) {
        hi.setValues(h, xMin, xMax, nDigs);
        lo.setValues(l, xMin, xMax, nDigs);
    } // ends setBars(double double)

    void setBins() {
        binEnd = new double[nBins+1];
        for (int i=0; i < nBins+1; i++) {
            binEnd[i] = xMin + i*(xMax - xMin)/(nBins);
        }
        countPop = new double[nBins];
        countSample = new double[nBins];
        if (nPop > 0 ) {
            countPop = listToHist(pop, nPop);
            makePhi();
        }
        if (samplesSoFar > 0 ) {
            countSample = listToHist(sampleMean, samplesSoFar);
        } else {
            for (int i=0; i < nBins; i++) {
                countSample[i] = 0;
            }
        }
    } // ends setBins()


    double[] listToHist(double[] list) {
        return(listToHist(list, list.length));
    }

    double[] listToHist(double[] list, int lim) {
        double[] c = new double[nBins];
        for (int i = 0; i < nBins - 1; i++) {
            c[i] = 0;
        }
        double mass = 1.0/( (double) lim );

        for (int i = 0; i < lim; i++) {
            for (int j = 0; j < nBins - 1; j++) {
                if (list[i] >= binEnd[j] && list[i] < binEnd[j+1]) {
                    c[j] += mass/(binEnd[j+1] - binEnd[j]);
                }
            }
            if (list[i] >= binEnd[nBins - 1] && list[i] <= binEnd[nBins]) {
                c[nBins-1] += mass/(binEnd[nBins] - binEnd[nBins - 1]);
            }
        }
        return(c);
    } // ends double listToHist(double[], int)

   void makePhi() {
        xVal = new double[nx];
        yVal = new double[nx];
        double sd, mu;
        if (sampleSum) {
            sd = popSd * Math.sqrt(sampleSize);
            mu = popMean * sampleSize;
        } else {
            sd = popSd / Math.sqrt(sampleSize);
            mu = popMean;
        }
        for (int i = 0; i < nx; i++) {
            xVal[i] = xMin + i*(xMax - xMin)/(nx-1);
            yVal[i] = PbsStat.normPdf(mu, sd,
                               xVal[i]);
        }
    }


   private double hiLitArea() {
      double area = 0;
      for (int i=0; i < nBins; i++) {
         if( binEnd[i]  > hiLiteHi ||  binEnd[i+1] <= hiLiteLo) {
         } else if (binEnd[i] >= hiLiteLo && binEnd[i+1] <= hiLiteHi) {
	         area += countSample[i]*(binEnd[i+1]-binEnd[i]);
         } else if (binEnd[i] >= hiLiteLo && binEnd[i+1] > hiLiteHi) {
		     area += countSample[i]*(hiLiteHi - binEnd[i]);
	     } else if (binEnd[i] <= hiLiteLo && binEnd[i+1] <= hiLiteHi) {
	    	area += countSample[i]*(binEnd[i+1]-hiLiteLo);
	     } else if (binEnd[i] < hiLiteLo && binEnd[i+1] > hiLiteHi) {
		    area += countSample[i]*(hiLiteHi - hiLiteLo);
         }
      }
      return(area);
   } // ends hiLitArea()


   private double normHiLitArea() {
      double area = 0;
      if (hiLiteHi > hiLiteLo) {
           area = PbsStat.normCdf((hiLiteHi - EX)/SE) -
                   PbsStat.normCdf((hiLiteLo - EX)/SE);
      }
      return(area);
   }// ends normHiLitArea


} // ends applet SampleMean
