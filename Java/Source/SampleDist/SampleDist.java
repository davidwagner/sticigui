/**
@author P.B. Stark statistics.berkeley.edu/~stark
@version 0.9
@copyright 1997-2007, P.B. Stark. All rights reserved.

Last modified 5/15/07

An applet to study the distribution of the sum, average, and sample variance of draws
   from a box.

**/


import java.awt.*;
import java.applet.*;
import java.util.*;
import PbsGui.*;
import PbsStat.*;

public class SampleDist extends Applet {
    private TextBar sampleSizeBar;           // size of each sample
    private TextBar samplesToTakeBar;        // number of samples to take
    private TextBar binBar;                  // number of bins in the histogram
    private TextBar lo;
    private TextBar hi;
    String[][] curveLabel = { {"No Curve","none"},
                              {"Normal Curve","normal"},
                              {"Student t Curve","t"},
                              {"Chi-Squared Curve","chi-squared"}
                            };
    String[] buttonLabel = {"Take Sample",
                            "Population Histogram",
                           };
    protected Button[] myButton = new Button[buttonLabel.length];
    protected Panel[] myPanel = new Panel[12];
    private TextArea box;                   // holds the population.
    private Label popMeanLabel;             // to display the population mean
    private Label popSdLabel;               // to display the population SD
    private Label statSampleMeanLabel;      // to display mean of sample means
    private Label statSampleSDLabel;        // sample SD of sample means
    private Label statExpLabel;             // theor. Expected value of statistic
    private Label statSELabel;              // to display theor. SD of statistic or d.f. of chi-square
    private Label samplesToTakeLabel;       // display number of samples to take next
    private Label samplesSoFarLabel;        // number of samples of current size taken
    private Label sourceLabel;              // label for the population sampled
    protected Label titleLabel;
    private Label boxLabel;                 // label box as population or category probabilities
    protected Font titleFont = new Font("TimesRoman", Font.PLAIN, 12);
    protected Font labelFont = new Font("ComputerModern",Font.PLAIN, 10);
    protected Label areaLabel;
    protected Label curveAreaLabel;
    protected Histogram hist;
    protected String title;
    private double[] xVal;                  // x coords of curve approx. to sampling distribution
    private double[] yVal;                  // y coords of ditto.
    private int nPop;                       // number of elements in the population
    private int sampleSize;                 // size of current sample
    private int minSampleSize;              // minimum sample size (2 for vars that use ssd)
    private int maxSampleSize;              // maximum sample size (population size if sampling w/o replacement)
    private int samplesToTake;              // number of samples to take of that size
    private int samplesSoFar;               // number of samples taken so far
    private boolean showCurve = false;      // show normal, t, or chi-square approximation toggle
    private int nVars = 5;                  // number of random variable choices
    private int nCurves = 4;                // number of approximating curve choices
    private String[][] rVar = { {"Sample Sum","sum"},
                                {"Sample Mean","mean"},
                                {"Sample t","t"},
                                {"Sample S-Squared","s-squared"},
                                {"Sample Chi-Squared","chi-squared"},
                              };            // random variable options and their abbreviations
    private Hashtable varHash = new Hashtable(nVars);
    private int nSources = 3;
    String[][] rSource =      { {"Normal","normal"},
                                {"Uniform","uniform"},
                                {"Box","box"},
                              };
    private Hashtable sourceHash = new Hashtable(nSources);
    private boolean replaceControl = false;    // add controls for sampling w/ w/o replacement
    private Checkbox replaceCheck = new Checkbox("with replacement");
    private boolean statLabels = true;         // show summary statistics of sample statistic values?
    private boolean binControls = true;        // add the bin controls?
    private boolean curveControls = true;      // add normal or chi-square curve button and label?
    private boolean boxEditable = true;        // are the contents of the box editable?
    private boolean toggleVar = true;          // add Choice to toggle among variables?
    private boolean showBoxHist = true;        // show histogram of the numbers in the box?
    private boolean boxHistControl = true;     // show button to turn box histogram on and off?
    private boolean normalFillButton = false;  // add button to fill box w/ normal sample?
    private Choice varChoice = new Choice();   // options for which random variable to sample
    private Choice curveChoice = new Choice(); // options for which approximating curve to plot
    private Choice sourceChoice = new Choice(); // options for data source (box, normal, uniform)
    private String currVar = null;             // current random variable displayed
    private String lastVar = null;             // previous random variable

    private int nBins;                         // number of bins for histogram
    private int nDigs = 4;                     // number of digits in text bars
    private double xMin;                       // lower limit for histogram
    private double xMax;                       // upper limit for histogram
    private double EX;                         // expected value of the variable plotted
    private double SE;                         // standard error of the variable plotted
    private double popMin;                     // smallest value in pop.
    private double popMax;                     // largest value in pop.
    private double[] pop;                      // elements of the population
    private double[] sample;                   // elements of the current sample
    double[] binEnd;                           // bin endpoints
    double[] countPop;                         // areas of the bins for the pop. histogram
    double[] countSample;                      // areas of bins for the hist. of sample means
    private double popMean;                    // the population mean
    private double popSd;                      // the population SD
    private double sd;                         // sd for normal approx
    private double mu;                         // mean for normal approx
    private double[] sampleMean;               // the history of sample means
    private double[] sampleSSq;                // history of sample s^2
    private double[] sampleT;                  // history of sample t
    private double hiLiteLo;                   // lower limit of hilighting
    private double hiLiteHi;                   // upper limit of highlighting

    private final int maxSamples = 10000;      // max number of samples
    private final int maxSamplesToTake = maxSamples;  // max number of samples left
    private final int maxMaxSampleSize = 500;  // max size of each sample
    private final int maxBins = 100;           // max bins in histogram
    private final int nx = 500;                // number of points in the density curve

    public void init() {
        super.init();
        setBackground(Color.white);
        String str;
        for (int i = 0; i < myPanel.length; i++) {         // make the sub-panels
            myPanel[i] = new Panel();
            myPanel[i].setFont(labelFont);
        }

        for (int i = 0; i < myButton.length; i++) {        // initialize buttons
             myButton[i] = new Button(buttonLabel[i]);
        }

        for (int i=0; i < nVars; i++) {                    // set up the variable hashtable
             varHash.put(rVar[i][1],rVar[i][0]);
        }

        for (int i=0; i < nSources; i++ ) {                // set up population hash
            sourceHash.put(rSource[i][1],rSource[i][0]);
        }

// get parameters
        showCurve = Format.strToBoolean(getParameter("showCurve"),false);         // default is no curve
        curveControls = Format.strToBoolean(getParameter("curveControls"),true);  // default curve controls
        binControls = Format.strToBoolean(getParameter("binControls"), true);      // default control bin size
        sampleSize = Format.strToInt(getParameter("sampleSize"), 5);               // default sample size 5                                                         // will be reset later
        samplesToTake = Format.strToInt(getParameter("samplesToTake"), 1);         // default take one sample
        nBins = Format.strToInt(getParameter("bins"), maxBins);                    // default bins in histograms
        boxEditable = Format.strToBoolean(getParameter("boxEditable"), true);      // default allow population change
        toggleVar = Format.strToBoolean(getParameter("toggleVar"), true);          // default allow variable chance
        showBoxHist = Format.strToBoolean(getParameter("showBoxHist"), true);      // default plot population histogram
        boxHistControl = Format.strToBoolean(getParameter("boxHistControl"), false); // default toggle pop histogram
        replaceControl = Format.strToBoolean(getParameter("replaceControl"), false); // default no control
        replaceCheck.setState(Format.strToBoolean(getParameter("replace"), true));  // default w/ replacement
        statLabels = Format.strToBoolean(getParameter("statLabels"), true);         // default show summary statistics
        hiLiteLo = Format.strToDouble(getParameter("hiLiteLo"), 0.0);              // low end of hilit region
        hiLiteHi = Format.strToDouble(getParameter("hiLiteHi"), 0.0);              // high end of hilit region

        StringTokenizer st;

        if (getParameter("variables") != null) {                                  // set variable choices
            st = new StringTokenizer(getParameter("variables"),"\n\t, ",false);
            for (int j=0; st.hasMoreTokens(); j++) {
                String s = st.nextToken();
                if (varHash.containsKey(s.toLowerCase())) {
                    varChoice.addItem((String) varHash.get(s));
                } else if (s.equals("all")) {                                     // all is the keyword to show all vars
                    for (Enumeration e = varHash.elements(); e.hasMoreElements(); ) {
                        varChoice.addItem((String) e.nextElement());
                    }
                }
            }
        } else {                                                                  // if not set, allow all
            for (Enumeration e = varHash.elements(); e.hasMoreElements(); ) {
                        varChoice.addItem((String) e.nextElement());
            }
        }

        if (varChoice.countItems() == 0) {                                        // give at least one variable
            varChoice.addItem((String) varHash.get("mean"));
        }
        if (varChoice.countItems() == 1) {                                        // suppress choice if there is none
            toggleVar = false;
        }

        if (getParameter("startWith") != null) {                                  // identify starting variable
            String s = getParameter("startWith");
            if (varHash.containsKey(s)) {
                currVar = (String) varHash.get(s);
            } else {
                currVar = varChoice.getItem(0);
            }
        } else {
            currVar = varChoice.getItem(0);
        }
        curveChoice.addItem(curveLabel[0][0]);
        str = getParameter("curves");
        if ( str != null) {                                                       // set curve choices
            for (int i=1; i < nCurves; i++ ) {
                if ( (str.indexOf(curveLabel[i][1]) >= 0 ) || (str.indexOf("all") >= 0)) {
                    curveChoice.addItem(curveLabel[i][0]);
                }
            }
        } else if ( curveControls ) {
            for (int i=1; i < nCurves; i++ ) {
                curveChoice.addItem(curveLabel[i][0]);
            }
        }
        curveChoice.select("No Curve");
        if (showCurve) {
            if (currVar.equals("Sample Mean") || currVar.equals("Sample Sum")) {
                curveChoice.select("Normal Curve");
            } else if (currVar.equals("Sample S-Squared") || currVar.equals("Sample Chi-Squared")) {
                curveChoice.select("Chi-Squared Curve");
            } else if (currVar.equals("Sample t")) {
				curveChoice.select("Student t curve");
			}
        }
// set up population choices
        boolean selectBoxFirst = false;
        if (getParameter("sources") != null) {                                  // set population choices
            st = new StringTokenizer(getParameter("sources"),"\n\t, ",false);
            for (int j=0; st.hasMoreTokens(); j++) {
                String s = st.nextToken();
                if (sourceHash.containsKey(s.toLowerCase())) {
                    sourceChoice.addItem((String) sourceHash.get(s));
                } else if (s.equals("all")) {                                    // all ==> show all options
                    for (Enumeration e = sourceHash.elements(); e.hasMoreElements(); ) {
                        sourceChoice.addItem((String) e.nextElement());
                    }
                    selectBoxFirst = true;
                }
                if (s.toLowerCase().equals("box")) {
                    selectBoxFirst = true;
                }
            }
        } else {                                                                  // if not set, allow all
            for (Enumeration e = sourceHash.elements(); e.hasMoreElements(); ) {
                        sourceChoice.addItem((String) e.nextElement());
            }
            selectBoxFirst = true;
        }

        if (selectBoxFirst) {
            sourceChoice.select("Box");
        } else {
            sourceChoice.select(sourceChoice.getItem(0));
        }

// initialize counts and tags
        countSample = new double[nBins];
        setLayout(new BorderLayout());
        popMeanLabel = new Label("");
        popSdLabel = new Label("");
        statSampleMeanLabel = new Label("");
        statSampleSDLabel = new Label("");
        statExpLabel = new Label("E(mean):");
        statSELabel = new Label("SE(mean): ");
        areaLabel = new Label("");
        curveAreaLabel = new Label("");
        sourceLabel = new Label("Sample from");
        sourceLabel.setFont(labelFont);
        boxLabel = new Label("");
        boxLabel.setFont(labelFont);
        sampleMean = new double[maxSamples];
        sampleSSq = new double[maxSamples];
        sampleT = new double[maxSamples];
        samplesSoFar = 0;
        samplesSoFarLabel = new Label("Samples: " + samplesSoFar);
        xMin = 0.0;
        xMax = 10.0;
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
        sampleSizeBar = new TextBar(sampleSize, 1, maxMaxSampleSize, nDigs,
                        new PositiveIntegerValidator(), TextBar.NO_BAR, "Sample Size:");

// the layout begins.
// myPanel[0] is North
        titleLabel = new Label();
        titleLabel.setFont(titleFont);
        myPanel[0].setLayout(new FlowLayout(FlowLayout.CENTER));
        myPanel[0].add(titleLabel);

        if (toggleVar) {                             // make the Choice of variables
            titleLabel.setText("Distribution of");
            myPanel[0].add(varChoice);
            varChoice.select(currVar);
        } else {                                     // no Choice
            titleLabel.setText("Distribution of " + currVar);
        }
        if ( toggleVar || (currVar != "Sample Chi-Squared") ) { // box only
            if (sourceChoice.countItems() == 1) {
                sourceLabel.setText(sourceLabel.getText() + " " + sourceChoice.getSelectedItem());
                myPanel[0].add(sourceLabel);
            } else if (sourceChoice.countItems() > 1) {
                myPanel[0].add(sourceLabel);
                myPanel[0].add(sourceChoice);
            }
        }
        if (replaceControl) {
            myPanel[0].add(replaceCheck);
        } else if ( toggleVar || (currVar != "Sample Chi-Squared") ) { // replacement only
            if (replaceCheck.getState()) {
                myPanel[0].add(new Label("with replacement"));
            } else {
                myPanel[0].add(new Label("without replacement"));
            }
        }
        myPanel[0].add(myButton[0]);
        add("North", myPanel[0]);

// myPanel[1] is Center
        hist = new Histogram();
        hist.setBackground(Color.white);             // change the color scheme
        myPanel[1].setLayout(new BorderLayout());
        myPanel[1].add("Center", hist);
        add("Center", myPanel[1]);

// myPanel[11] is West
        myPanel[3].setLayout(new BorderLayout());
        myPanel[9].setLayout(new FlowLayout(FlowLayout.CENTER));
        myPanel[9].add(lo);
        myPanel[9].add(hi);
        myPanel[10].setLayout(new FlowLayout());
        myPanel[10].add(areaLabel);

        if (curveControls || showCurve) {
            myPanel[10].add(curveAreaLabel);          // leave space for the approximating area
            if (curveControls) {
                myPanel[10].add(curveChoice);
            }
        }

        if (boxHistControl) {
            myPanel[10].add(myButton[1]);
            if (showBoxHist) myButton[1].setLabel("No Population Histogram");
        }

        if (statLabels) {
            myPanel[4].setLayout(new GridLayout(7,1));
        } else {
            myPanel[4].setLayout(new GridLayout(4,1));
        }
        myPanel[4].add(popMeanLabel);
        myPanel[4].add(popSdLabel);
        myPanel[4].add(statExpLabel);
        myPanel[4].add(statSELabel);
        if (statLabels) {
            myPanel[4].add(statSampleMeanLabel);
            myPanel[4].add(statSampleSDLabel);
        }
        myPanel[4].add(samplesSoFarLabel);
        myPanel[11].setLayout(new FlowLayout(FlowLayout.CENTER));
        myPanel[11].add(myPanel[4]);
        add("West", myPanel[11]);

// myPanel[3] is South
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

// box is in myPanel[5], East
        myPanel[5].setLayout(new BorderLayout());
        box = new TextArea(12,8);
        box.setEditable(boxEditable);
        box.setBackground(Color.white);
        str = getParameter("sources");
        if (str == null || str.toLowerCase().indexOf("box") >= 0 || str.toLowerCase().indexOf("all") >= 0) {
            myPanel[5].add("Center",box);
            if (currVar.equals("Sample Chi-Squared")) {
                boxLabel.setText("Category Probabilities");
            } else {
                boxLabel.setText("Population");
            }
            myPanel[5].add("South", boxLabel);
        }
        add("East", myPanel[5]);
        String bc = "";
        if (getParameter("boxContents") != null) {
            bc = getParameter("boxContents");
        } else if (sourceChoice.getSelectedItem().equals("Normal")) {
            bc = "Normal";
        } else if (sourceChoice.getSelectedItem().equals("Uniform")) {
            bc = "Uniform";
        } else {
            for (int j=0; j < 5; j++) {
                bc += j + " ";
            }
        }
        setBox(bc, true);
        double[] vmx = PbsStat.vMinMax(pop);
        xMin = vmx[0];
        xMax = vmx[1];
        initPop();
        setCurve();                                   // set the approximating curve
        setBins();                                    // make the histogram counts
        setBars(hiLiteLo, hiLiteHi);
        adjustSampleSize();
        validate();                                   // wishful thinking
        showPlot();                                   // refresh the histogram
    }

    protected void initPop() {                        // compute population statistics
        if (sourceChoice.getSelectedItem().equals("Box")) {
            nPop = pop.length;
            popMean = 0;
            popSd = 0;
            if (pop.length == 0) {
                System.out.println("Error in SampleDist.initPop(): Population is empty!\n");
                for ( int i= 0; i < nBins; i++) {
                    countPop[i] = 0;
                }
                xVal = new double[1];
                yVal = new double[1];
                xVal[0] = 0;
                yVal[0] = 0;
                return;
            }
            popMean = PbsStat.Mean(pop);
            popSd = PbsStat.Sd(pop);
// FIX ME! need to handle probabilities here.
        } else if ( sourceChoice.getSelectedItem().equals("Normal")) {
            popMean = 0;
            popSd = 1;
            replaceCheck.setState(true);
        } else if (sourceChoice.getSelectedItem().equals("Uniform")) {
            popMean = 0.5;
            popSd = Math.sqrt(1.0/12.0);
            replaceCheck.setState(true);
        }
        double[] vmx = PbsStat.vMinMax(pop);
        popMin = vmx[0];
        popMax = vmx[1];
        setLims();                                  // set plot limits
// make the histogram of the population
        setBins();                                    // set the class intervals; make the counts
        setBars(xMin,xMin);                           // set the hilight scrollbar scales
// reset the labels
        if (varChoice.getSelectedItem().equals("Sample Chi-Squared")) {
            popMeanLabel.setText("Categories: " + nPop);
            popSdLabel.setText("E(Chi-Squared): " + (nPop-1));
            replaceCheck.setState(true);
        } else {
            popMeanLabel.setText("Ave(Box): " + Format.doubleToStr(popMean,3));
            popSdLabel.setText("SD(Box): " + Format.doubleToStr(popSd,3));
            statSampleMeanLabel.setText("Mean(values) undefined");
            statSampleSDLabel.setText("SD(values) undefined");
        }
        setCurve();
        setCurveLabel();
        myPanel[4].invalidate();
        myPanel[11].validate();
        setAreas();
    }

    public boolean handleEvent(Event e) {
        if (e.id == Event.WINDOW_DESTROY) {
            System.exit(0);
        } else if ( e.id == Event.SCROLL_ABSOLUTE
                 || e.id == Event.SCROLL_LINE_DOWN
                 || e.id == Event.SCROLL_LINE_UP
                 || e.id == Event.SCROLL_PAGE_DOWN
                 || e.id == Event.SCROLL_PAGE_UP) {
            if (e.target == binBar) {                   // update # bins and redraw histogram
                nBins = (int) binBar.getValue();
                setBins();                              // reset the class intervals, make the counts
                showPlot();                             // refresh the histogram
                return(super.handleEvent(e));
            } else if (e.target == sampleSizeBar) {     // clear history, reset sample size, redisplay histogram
                sampleSize = (int) sampleSizeBar.getValue();
                setBars(xMin, xMin);
                samplesSoFar = 0;
                setSamLabel();
                setLims();
                setCurveLabel();
                setBins();
                setCurve();
                myPanel[4].invalidate();
                myPanel[11].validate();
                setAreas();
                showPlot();
                return(super.handleEvent(e));
            } else if (e.target == samplesToTakeBar) {
                samplesToTake = (int) samplesToTakeBar.getValue();
                return(super.handleEvent(e));
            } else if (e.target == lo || e.target == hi) {
                hiLiteLo = lo.getValue();
                hiLiteHi = hi.getValue();
                if (hiLiteLo >= hiLiteHi) hiLiteLo = hiLiteHi;
                setAreas();
                showPlot();
                return(super.handleEvent(e));
            }
        } else if (e.target == box && e.id == Event.LOST_FOCUS) {
            setBox(box.getText());
            return(super.handleEvent(e));
        } else if (e.id == Event.ACTION_EVENT) {
            if (e.arg.equals("Take Sample")) {
                int lim = maxSamples - samplesSoFar;            // number remaining samples
                drawSample(Math.min(samplesToTake, lim));
                setSamLabel();
                return(super.handleEvent(e));
            } else if (e.target == sourceChoice) {
                if ( sourceChoice.getSelectedItem().equals("Box") ) {
                    setBox(box.getText());
                } else {
                    setBox(sourceChoice.getSelectedItem());
                    replaceCheck.setState(true);
                }
                return(super.handleEvent(e));
            } else if (e.target == varChoice) {
                String lastVar = currVar;
                currVar = varChoice.getSelectedItem();
                if (currVar.equals(lastVar)) {
                    return(super.handleEvent(e));
                } else {
                    newVariable(currVar);
                    showPlot();
                    return(super.handleEvent(e));
                }
            } else if (e.target == curveChoice) {
                setCurve();
                setAreas();
                showPlot();
                return(super.handleEvent(e));
            } else if (e.target == replaceCheck) {
                if (replaceOK(replaceCheck.getState())) {
                    samplesSoFar = 0;
                    setLims();
                    setBins();
                    setSamLabel();
                    setBins();
                    setBars(hiLiteLo, hiLiteHi);
                    setCurveLabel();
                    setCurve();
                    setAreas();
                } else {
                    replaceCheck.setState(true);
                }
                return(super.handleEvent(e));
            } else if (e.arg == "Population Histogram") {
                showBoxHist = true;
                setLims();
                setBins();
                setBars(hiLiteLo, hiLiteHi);
                setCurve();
                myButton[1].setLabel("No Population Histogram");
                showPlot();
                return(super.handleEvent(e));
            } else if (e.arg == "No Population Histogram") {
                showBoxHist = false;
                setLims();
                setBins();
                setBars(hiLiteLo, hiLiteHi);
                setCurve();
                myButton[1].setLabel("Population Histogram");
                showPlot();
                return(super.handleEvent(e));
            }
        }
        return(super.handleEvent(e));
    }

    protected boolean replaceOK(boolean rep) {
        boolean v = true;
        if (!rep) {
            if (!sourceChoice.getSelectedItem().equals("Box")) {
                v = false;
            } else {
                String s = varChoice.getSelectedItem();
                if (! ( s.equals("Sample Sum") || s.equals("Sample Mean") ||
                        s.equals("Sample S-Squared") || s.equals("Sample t")
                      )
                    ) {
                    v = false;
                }
            }
        }
        return(v);
    }

    public void showPlot() { // test what is to be plotted; adjust variables accordingly
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
                         hiLiteHi, showCurve, xVal, yVal);
           } else {
                hist.redraw(binEnd, countSample, hiLiteLo,
                       hiLiteHi, showCurve, xVal, yVal);
           }
        } else {
           if (showBoxHist) {
                hist.redraw(binEnd, countPop, hiLiteLo,
                       hiLiteHi, showCurve, xVal, yVal);
           } else {
                hist.redraw(binEnd, countSample, hiLiteLo,
                       hiLiteHi, showCurve, xVal, yVal);
           }
        }
    }

    protected void newVariable(String lastVar) {     // set things up when the variable is changed
        if ( ( currVar.equals("Sample S-Squared") || currVar.equals("Sample t") )
                && (sampleSize == 1) ) {
            samplesSoFar = 0;
        }
        adjustSampleSize();
        if (lastVar.equals("Sample Chi-Squared") || currVar.equals("Sample Chi-Squared")) {
            samplesSoFar = 0;
        }
        if (currVar.equals("Sample Chi-Squared")) {
            boxLabel.setText("Category Probabilities");
            setBox(box.getText(),true);
        } else {
            boxLabel.setText("Population");
        }
        if ( ! (currVar.equals("Sample Mean") || currVar.equals("Sample Sum") ||
                currVar.equals("Sample S-Squared")) || currVar.equals("Sample t") ) {
            replaceCheck.setState(true);
        }
        setSamLabel();
        setLims();
        setBins();
        setBars(hiLiteLo, hiLiteHi);
        setCurveLabel();
        setCurve();
        setAreas();
    } // ends newVariable(String)

    public void setBox(String newBox, boolean updateBox) {               // parse new population
        if (newBox.toLowerCase().equals("normal")) {
            replaceCheck.setState(true);
            pop = new double[2];
            pop[0] = -4;
            pop[1] = 4;
            box.setText("Normal");
            box.invalidate();
            sourceChoice.select("Normal");
            if (varChoice.getSelectedItem().equals("Sample Chi-Squared")) {
                System.out.println("Warning in SampleDist.setBox(): normal incompatible " +
                                    "with Sample Chi-Squared");
                varChoice.select("Sample Mean");
            }
        } else if (newBox.toLowerCase().equals("uniform")) {
            replaceCheck.setState(true);
            pop = new double[2];
            pop[0] = 0;
            pop[1] = 1;
            box.setText("Uniform");
            box.invalidate();
            sourceChoice.select("Uniform");
            if (varChoice.getSelectedItem().equals("Sample Chi-Squared")) {
                System.out.println("Warning in SampleDist.setBox(): uniform incompatible " +
                                    "with Sample Chi-Squared");
                varChoice.select("Sample Mean");
            }
        } else {
            StringTokenizer t = new StringTokenizer(newBox,"\n\t, ",false);
            String s;
            Vector vPop = new Vector(5);
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
            if (varChoice.getSelectedItem().equals("Sample Chi-Squared")) {
                pop = PbsStat.removeZeroNaN(pop);
                nPop = pop.length;
                pop = PbsStat.scalVMult(1.0/PbsStat.vTot(pop), pop);
                updateBox = true;
            }
            if (updateBox) {
                box.setText("");
                String popStr = "";
                for (int i = 0; i < nPop; i++) {              // print the population in the box
                    popStr = Format.numToString(pop[i],nDigs)  + "\n";
                    box.appendText(popStr);
                }
                box.invalidate();
            }
            sourceChoice.select("Box");
        }
        initPop();
        samplesSoFar = 0;
        setSamLabel();
    }  // ends setBox(String, boolean)

    public void setBox(String newBox) {
        setBox(newBox, false);
    }  // ends setBox(String)

    protected void drawSample(int nSams) {
        double[] theSample = new double[sampleSize];
        int[] indices = new int[sampleSize];
        double xb;
        double ssq;
        double tStat;
        double tmp;
        for (int j=0; j < nSams; j++) {
            xb = 0;
            ssq = 0;
            tStat = 0;
            if ( varChoice.getSelectedItem().equals("Sample Chi-Squared") ) {
                if (sourceChoice.getSelectedItem().equals("Box")) {
                    double[] cum = PbsStat.vCumSum(pop);
                    double[] count = new double[pop.length];
                    for (int i=0; i < pop.length; i++) {
                        count[i] = 0.0;
                    }
                    for (int i=0; i < sampleSize; i++) {
                        tmp = Math.random();
                        if (tmp <= cum[0]) {
                            count[0]++;
                        }
                        for (int k=1; k < count.length; k++) {
                            if ( tmp > cum[k-1] && tmp <= cum[k] ) {
                                count[k]++;
                            }
                        }
                    }
                    ssq = 0.0;
                    for ( int i=0; i < pop.length; i++) {
                        tmp = sampleSize*pop[i];
                        ssq += (count[i] - tmp)*(count[i] - tmp)/tmp;
                    }
                    sampleSSq[samplesSoFar++] = ssq;
                    if (ssq < xMin || ssq > xMax) {
                        xMin = Math.min(ssq, xMin);
                        xMax = Math.max(ssq, xMax);
                    }
                } else {
                    System.out.println("Error in SampleDist.drawSample(): cannot draw from " +
                                        "this distribution with Sample Chi-Square!");
                }
            } else {
                if (sourceChoice.getSelectedItem().equals("Box")) {
                    if (replaceCheck.getState()) {
                        indices = PbsStat.listOfRandInts(sampleSize, 0, nPop-1);
                    } else {
                        indices = PbsStat.listOfDistinctRandInts(sampleSize, 0, nPop-1);
                    }
                    for (int i = 0; i < sampleSize; i++) {
                        theSample[i] = pop[ indices[i] ];
                        xb += theSample[i];
                    }
                } else if (sourceChoice.getSelectedItem().equals("Normal")) {
                    for (int i = 0; i < sampleSize; i++) {
                         theSample[i] = PbsStat.rNorm();
                         xb += theSample[i];
                    }
                } else if (sourceChoice.getSelectedItem().equals("Uniform")) {
                    for (int i = 0; i < sampleSize; i++) {
                         theSample[i] = Math.random();
                         xb += theSample[i];
                    }
                }
                xb /= sampleSize;
                for (int i = 0; i < sampleSize; i++) {
                    ssq += (theSample[i] - xb)*(theSample[i] - xb);
                }
                if (sampleSize > 1) {                             // if n>1, log the sample s^2 and t
                    ssq /= (sampleSize-1);
                    sampleSSq[samplesSoFar] = ssq;
                    tStat = xb/(Math.sqrt(ssq)/Math.sqrt(sampleSize));
                    sampleT[samplesSoFar] = tStat;
                } else {                                          // otherwise, set to 0.
                    sampleSSq[samplesSoFar] = 0;
                    sampleT[samplesSoFar] = 0;
                }
                sampleMean[samplesSoFar++] = xb;                  // log the sample mean
                if (currVar.equals("Sample Mean")) {
                    if (xb < xMin || xb > xMax) {
                        xMin = Math.min(xb, xMin);
                        xMax = Math.max(xb, xMax);
                    }
                } else if (currVar.equals("Sample t")) {
                    if (tStat < xMin || tStat > xMax) {
                        xMin = Math.min(tStat, xMin);
                        xMax = Math.max(tStat, xMax);
					}
                } else if ( currVar.equals("Sample Sum")) {
                    tmp = xb * sampleSize;
                    if (tmp < xMin || tmp > xMax) {
                        xMin = Math.min(tmp, xMin);
                        xMax = Math.max(tmp, xMax);
                    }
                } else if (currVar.equals("Sample S-Squared")) {
                    if (ssq < xMin || ssq > xMax) {
                        xMin = Math.min(ssq, xMin);
                        xMax = Math.max(ssq, xMax);
                    }
                }
            }
        }
        setBins();
        setBars(hiLiteLo, hiLiteHi);
    } // ends drawSample(nSams)

    protected void setSamLabel() {
        samplesSoFarLabel.setText("Samples: " + samplesSoFar);
        samplesSoFarLabel.invalidate();
        if (currVar.equals("Sample Mean")) {
            countSample = listToHist(sampleMean, samplesSoFar);
            statSampleMeanLabel.setText("Mean(values) = " +
                Format.doubleToStr(PbsStat.Mean(sampleMean, samplesSoFar), nDigs));
            statSampleSDLabel.setText("SD(values) = " +
                Format.doubleToStr(PbsStat.Sd(sampleMean, samplesSoFar), nDigs));
        } else if (currVar.equals("Sample Sum")) {
            countSample = listToHist(PbsStat.scalVMult(sampleSize, sampleMean), samplesSoFar);
            statSampleMeanLabel.setText("Mean(values) = " +
                Format.doubleToStr(sampleSize*PbsStat.Mean(sampleMean, samplesSoFar), nDigs));
            statSampleSDLabel.setText("SD(values) = " +
                Format.doubleToStr(sampleSize*PbsStat.Sd(sampleMean, samplesSoFar), nDigs));
        } else if (currVar.equals("Sample t")) {
            countSample = listToHist(sampleT, samplesSoFar);
            statSampleMeanLabel.setText("Mean(values) = " +
                Format.doubleToStr(PbsStat.Mean(sampleT, samplesSoFar), nDigs));
            statSampleSDLabel.setText("SD(values) = " +
                Format.doubleToStr(PbsStat.Sd(sampleT, samplesSoFar), nDigs));
        } else if (currVar.equals("Sample S-Squared") || currVar.equals("Sample Chi-Squared")) {
            countSample = listToHist(sampleSSq, samplesSoFar);
            statSampleMeanLabel.setText("Mean(values) = " +
                Format.doubleToStr(PbsStat.Mean(sampleSSq, samplesSoFar), nDigs));
            statSampleSDLabel.setText("SD(values) = " +
                Format.doubleToStr(PbsStat.Sd(sampleSSq, samplesSoFar), nDigs));
        }
        statSampleMeanLabel.invalidate();
        statSampleSDLabel.invalidate();
        setAreas();
        myPanel[4].invalidate();
        myPanel[11].validate();
        validate();
        showPlot();
    }

    public void setAreas() {
        areaLabel.setText(" Selected area: " + Format.doubleToPct(hiLitArea()));
        if (curveChoice.getSelectedItem().equals("Normal Curve")) {
                 curveAreaLabel.setText(" Normal approx: "
                         + Format.doubleToPct(normHiLitArea()));
        } else if (curveChoice.getSelectedItem().equals("Student t Curve")) {
                 curveAreaLabel.setText(" Student t approx: "
                         + Format.doubleToPct(tHiLitArea()));
        } else if (curveChoice.getSelectedItem().equals("Chi-Squared Curve")) {
                 curveAreaLabel.setText(" Chi-squared approx: "
                         + Format.doubleToPct(chiHiLitArea()));
        } else {
            curveAreaLabel.setText("");
        }
        areaLabel.invalidate();
        curveAreaLabel.invalidate();
        myPanel[3].validate();
    } // ends setAreas()

    public void setBars(double l, double h) {    // set the TextBars for hilight and sampleSize
        hi.setValues(h, xMin, xMax, nDigs);
        lo.setValues(l, xMin, xMax, nDigs);
        hiLiteLo = l;
        hiLiteHi = h;
        adjustSampleSize();
    } // ends setBars(double double)

    public void setBars(String l, String h) { // set TextBars
        setBars(Format.strToDouble(l), Format.strToDouble(h));
    }

    protected void setBins() {
        binEnd = new double[nBins+1];
        for (int i=0; i < nBins+1; i++) {
            binEnd[i] = xMin + i*(xMax - xMin)/nBins;
        }
        countPop = new double[nBins];
        countSample = new double[nBins];
        if (sourceChoice.getSelectedItem().equals("Box") && nPop > 0) {
            if (varChoice.getSelectedItem().equals("Sample Chi-Squared")) {
                setCurve();
            } else {
                countPop = listToHist(pop, nPop);
                setCurve();
            }
        } else if (sourceChoice.getSelectedItem().equals("Normal")) {
            for (int i=0; i < nBins; i++ ) {
                countPop[i] = (PbsStat.normCdf(binEnd[i+1]) -
                                 PbsStat.normCdf(binEnd[i]))/(binEnd[i+1] - binEnd[i]);
            }
        } else if (sourceChoice.getSelectedItem().equals("Uniform")) {
            double midPt;
            for (int i=0; i < nBins; i++) {
                midPt = (binEnd[i]+binEnd[i+1])/2;
                if (midPt >= 0 && midPt <= 1) {
                    countPop[i] = 1;
                } else {
                    countPop[i] = 0;
                }
            }
        }
        if (samplesSoFar > 0 ) {
            if (currVar.equals("Sample S-Squared") || currVar.equals("Sample Chi-Squared")) {
                countSample = listToHist(sampleSSq, samplesSoFar);
            } else if (currVar.equals("Sample Mean")) {
                countSample = listToHist(sampleMean, samplesSoFar);
			} else if (currVar.equals("Sample t")) {
				countSample = listToHist(sampleT, samplesSoFar);
            } else if (currVar.equals("Sample Sum")) {
                countSample = listToHist(PbsStat.scalVMult(sampleSize,sampleMean), samplesSoFar);
            }
        } else {
            for (int i=0; i < nBins; i++) {
                countSample[i] = 0;
            }
        }
    } // ends setBins()

    private void setLims() {
        if (currVar.equals("Sample Sum")) {
            xMin = sampleSize * popMin; // these are the limits for the histogram
            xMax = sampleSize * popMax;
        } else if (currVar.equals("Sample Chi-Squared")) {
            xMin = 0.0;
            xMax = 10*Math.sqrt(pop.length - 1); // 5 SD
        } else if (currVar.equals("Sample S-Squared")) {
            xMin = 0.0;
            double maxDev = Math.max(popMean-popMin, popMax-popMean);
            xMax = 3*maxDev*maxDev/Math.sqrt(sampleSize);
        } else if (currVar.equals("Sample Mean")) {
            xMin = popMean-4*popSd/Math.sqrt(sampleSize);
            xMax = popMax+4*popSd/Math.sqrt(sampleSize);
        } else if (currVar.equals("Sample t")) {
			if (sampleSize > 2) {
				xMin = -3*Math.sqrt((sampleSize+0.0)/(sampleSize - 2.0));
			    xMax =  3*Math.sqrt((sampleSize+0.0)/(sampleSize - 2.0));
			} else {
				xMin = -5;
				xMax = 5;
			}
		}
        if (showBoxHist) {
            xMin = Math.min(popMin, xMin);
            xMax = Math.max(popMax, xMax);
        }

    } // ends setLims


    protected double[] listToHist(double[] list) {
        return(listToHist(list, list.length));
    }

    protected double[] listToHist(double[] list, int lim) {
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

    protected void setCurveLabel() {
        double fpc = 1.0;
        if (!replaceCheck.getState()) {
            fpc = Math.sqrt( (nPop - sampleSize + 0.0)/(nPop-1.0));
        }
        if (currVar.equals("Sample Sum")) {
            SE = fpc*popSd*Math.sqrt(sampleSize + 0.0);
            EX = sampleSize*popMean;
            statExpLabel.setText("E(sum): " + Format.doubleToStr(EX,3)  + "  ");
            statSELabel.setText("SE(sum): " + Format.doubleToStr(SE,3)  + "  ");
        } else if (currVar.equals("Sample Mean")) {
            SE = fpc*popSd/Math.sqrt(sampleSize + 0.0);
            EX = popMean;
            statExpLabel.setText("E(mean): " + Format.doubleToStr(EX,4)  + "  ");
            statSELabel.setText("SE(mean): " + Format.doubleToStr(SE,4) + "  ");
        } else if (currVar.equals("Sample t")) {
			if ( sampleSize > 2 ) {
                SE = Math.sqrt((sampleSize + 0.0)/(sampleSize - 2.0));
			} else {
				SE = Double.NaN;
			}
            EX = popMean;
            statExpLabel.setText("E(t): " + Format.doubleToStr(EX,4)  + "  ");
            statSELabel.setText("SE(t): " + Format.doubleToStr(SE,4) + "  ");
        } else if (currVar.equals("Sample S-Squared")) {
            if (replaceCheck.getState()) {
                EX = popSd*popSd;
            } else {
                EX = popSd*popSd*nPop/(nPop-1.0);
            }
            SE = Math.sqrt(2.0/(sampleSize-1.0))*popSd*popSd;
            statExpLabel.setText("E(S-squared): " + Format.doubleToStr(EX,3)  + "  ");
            statSELabel.setText("df: " +  (sampleSize-1) + "  ");
        } else if (currVar.equals("Sample Chi-Squared")) {
            EX = pop.length - 1;
            SE = Math.sqrt(2.0*(pop.length-1.0));
            popMeanLabel.setText("Categories: " + nPop);
            popSdLabel.setText("E(Chi-Squared): " + (pop.length - 1));
            statExpLabel.setText("df: " + (pop.length - 1) + " ");
            statSELabel.setText("      ");
        }
    } // ends setCurveLabel()

    public void setSampleSize(int size) {
        sampleSize = size;
        adjustSampleSize();
        showPlot();
    }

    public void setSampleSize(String size) {
        setSampleSize(Format.strToInt(size));
    }

    protected void adjustSampleSize() {
        minSampleSize = 1;
        if (currVar.equals("Sample S-Squared") || currVar.equals("Sample t")) {
            minSampleSize = 2;
        }
        if ( !replaceCheck.getState() ) {
            maxSampleSize = nPop;
        } else {
            maxSampleSize = maxMaxSampleSize;
        }
        sampleSize = Math.max(sampleSize,minSampleSize);
        sampleSize = Math.min(sampleSize,maxSampleSize);
        sampleSizeBar.setValues(sampleSize,minSampleSize,maxSampleSize,1);
    }

    protected boolean setCurve() {
        double fpc = 1.0;
        double popVar = popSd*popSd;
        if ( !replaceCheck.getState() ) {
            popVar = popVar*nPop/(nPop-1.0);
        }
        if (!replaceCheck.getState()) {
            fpc = Math.sqrt( (nPop - sampleSize + 0.0)/(nPop-1.0));
        }
        xVal = new double[nx];
        yVal = new double[nx];
        if (curveChoice.getSelectedItem().equals("No Curve")) {
            showCurve = false;
        } else {
            showCurve = true;
            if (curveChoice.getSelectedItem().equals("Chi-Squared Curve")) {
                if (varChoice.getSelectedItem().equals("Sample S-Squared")) {
                    double scale = (sampleSize - 1.0)/(popVar);
                                         // change of variables: (n-1)*S^2/sigma^2 ~ Chi^2_{n-1}
                    for (int i = 0; i < nx; i++) {
                        xVal[i] = xMin + i*(xMax - xMin)/(nx-1.0);
                        yVal[i] = scale*PbsStat.chi2Pdf(xVal[i]*scale, sampleSize-1.0);
                    }
                } else if (varChoice.getSelectedItem().equals("Sample Chi-Squared")) {
                    for (int i=0; i < nx; i++) {
                        xVal[i] = xMin + i*(xMax - xMin)/(nx-1.0);
                        yVal[i] = PbsStat.chi2Pdf(xVal[i], pop.length-1.0);
                    }
                } else {
                    System.out.println("Warning in SampleDist.setCurve(): Chi-squared " +
                                       "approximation to " +
                                        varChoice.getSelectedItem() + " Not Supported!");
                    curveChoice.select("No Curve");
                    showCurve = false;
                    return(false);
                }
            } else if (curveChoice.getSelectedItem().equals("Normal Curve")) {
                if (varChoice.getSelectedItem().equals("Sample Mean")) {
                    sd = fpc*popSd/Math.sqrt(sampleSize + 0.0);
                    mu = popMean;
                } else if (varChoice.getSelectedItem().equals("Sample Sum")) {
                    sd = fpc*popSd * Math.sqrt(sampleSize + 0.0);
                    mu = popMean * sampleSize;
                } else if (varChoice.getSelectedItem().equals("Sample S-Squared")) {
// E(chi^2) = (n-1), so E( sigma^2 chi^2 / (n-1) = sigma^2.
// SD(chi^2) = sqrt(2(n-1)), so SD( sigma^2 chi^2/ (n-1)) = sqrt(2/(n-1)) sigma^2.
                    sd = Math.sqrt(2.0/(sampleSize-1.0))*popSd*popSd; // FIX ME!
                                                                      // doesn't account for no replacement
                    mu = popVar;
                } else if (varChoice.getSelectedItem().equals("Sample Chi-Squared")) {
                    sd = Math.sqrt(2.0*(pop.length-1.0));
                    mu = pop.length-1;
                } else if (varChoice.getSelectedItem().equals("Sample t")) {
					if (sampleSize > 2) {
						sd = sampleSize/(sampleSize-2.0);
					} else {
						sd = Double.NaN;
						System.out.println("Warning in SampleDist.setCurve(): normal " +
						                   "approximation to Student t with sample size <= 2 " +
						                   " Not Supported!");
						curveChoice.select("No Curve");
						showCurve = false;
                    	return(false);
					}
					mu = 0;
				}
                for (int i = 0; i < nx; i++) {
                    xVal[i] = xMin + i*(xMax - xMin)/(nx-1);
                    yVal[i] = PbsStat.normPdf(mu, sd, xVal[i]);
                }
           } else if (curveChoice.getSelectedItem().equals("Student t Curve")) {
			    if (varChoice.getSelectedItem().equals("Sample t")) {
				    for (int i = 0; i < nx; i++) {
						xVal[i] = xMin + i*(xMax - xMin)/(nx-1);
                    	yVal[i] = PbsStat.tPdf(xVal[i], sampleSize-1);
					}
				} else {
					System.out.println("Warning in SampleDist.setCurve(): Student t " +
					                   "approximation to " + varChoice.getSelectedItem() +
					                   " Not Supported!");
					curveChoice.select("No Curve");
					showCurve = false;
                    return(false);
			    }
           }
        }
        return(true);
    } // ends setCurve()

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
        double fpc = 1.0;
        if (!replaceCheck.getState()) {
            fpc = Math.sqrt( (nPop - sampleSize + 0.0)/(nPop-1.0));
        }
        if (hiLiteHi > hiLiteLo) {
           area = PbsStat.normCdf((hiLiteHi - EX)/(fpc*SE)) - PbsStat.normCdf((hiLiteLo - EX)/(fpc*SE));
        }
        return(area);
    }// ends normHiLitArea

    private double chiHiLitArea() {
       double area = 0;
       if (hiLiteHi > hiLiteLo) {
           if (varChoice.getSelectedItem().equals("Sample S-Squared")) {
               double scale = (sampleSize - 1.0)/(popSd*popSd);
               area = PbsStat.chi2Cdf(scale*hiLiteHi, sampleSize-1) -
                      PbsStat.chi2Cdf(scale*hiLiteLo, sampleSize-1);
           } else if (varChoice.getSelectedItem().equals("Sample Chi-Squared")) {
               area = PbsStat.chi2Cdf(hiLiteHi, pop.length-1) -
                      PbsStat.chi2Cdf(hiLiteLo, pop.length-1);
           } else {
               System.out.println("Error in SampleDist.chiHiLitArea(): " + varChoice.getSelectedItem() +
                      " not supported. ");
               area = 0.0;
           }
       }
       return(area);
    }// ends chiHiLitArea

    private double tHiLitArea() {
       double area = 0;
       if (hiLiteHi > hiLiteLo) {
           if (varChoice.getSelectedItem().equals("Sample t")) {
               area = PbsStat.tCdf(hiLiteHi, sampleSize-1) -
                      PbsStat.tCdf(hiLiteLo, sampleSize-1);
           } else {
               System.out.println("Error in SampleDist.tHiLitArea(): " + varChoice.getSelectedItem() +
                      " not supported. ");
               area = 0.0;
           }
       }
       return(area);
    }// ends chiHiLitArea


} // ends applet SampleDist
