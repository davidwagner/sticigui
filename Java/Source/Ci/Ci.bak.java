/**
class Ci extends Applet
@author P.B. Stark http://statistics.berkeley.edu/~stark
@version 0.9
@copyright 1997-2001, P.B. Stark. All rights reserved.
Last modified 4/14/01

An applet to learn about confidence intervals for the mean
by sampling from a box.

The student takes samples from a box with known contents, and
determines what multiple of the sample SD is needed to get a
certain coverage probability. This can be done with several
sample sizes.

If the box has only zeros and ones, there is an option to
use Chebychev's inequality to make a conservative confidence
interva.

Then a "hidden" box is sampled, and the student tries to
find a confidence interval for its mean using what was learned
from boxes with known contents.

The contents of the hidden box are simulated from a normal distribution
whose mean is uniformly distributed on [-100, 100], unless the
population is constrained to consist of just zeros and ones.

*/

import java.awt.*;
import java.util.*;
import java.applet.*;
import PbsGui.*;
import PbsStat.*;

public class Ci extends Applet {
    protected TextBar sampleSizeBar; // size of each sample
    protected TextBar samplesToTakeBar; // number of samples to take
    protected TextBar facBar; // factor to blow up intervals by

    String[] myText = { "cover",
                        "Ave(box): ",
                        "Samples: ",
                        "SD(box): ",
                        };
    String[] buttonLabel = {"Take Sample",
                            " Use True SE ",
                            "Hide Box",
                            };
    protected Label[] myLabel = new Label[myText.length];
    protected Button[] myButton = new Button[buttonLabel.length];
    protected Panel[] myPanel = new Panel[11];
    int nSources = 3;
    String[][] rSource =  { {"Normal","normal"},
                            {"Uniform","uniform"},
                            {"Box","box"},
                            {"0-1 Box","0-1 box"}
                          };
    private Hashtable sourceHash = new Hashtable(nSources);
    private Choice sourceChoice = new Choice(); // options for data source (box, normal, uniform)
    private Choice seChoice = new Choice();     // options for using true SE, estimated SE or bound
    private TextArea box; // holds the population.

    protected Label titleLabel;
    private Label sourceLabel;   // label for the population sampled
    protected Font titleFont = new Font("TimesRoman", Font.PLAIN, 12);
    protected Font labelFont = new Font("ComputerModern",Font.PLAIN,10);
    protected FontMetrics labelFM =  getFontMetrics(labelFont);
    CiPlot myCiPlot;
    private String title;
    private String lastItem;

    private int nPop;            // number of elements in the box
    private int sampleSize;      // size of current sample
    private int samplesToTake;   // number of samples to take of that size
    private int samplesSoFar;    // number of samples taken so far
    private double cover;        // number of intervals that cover

    private double[] pop;        // elements of the population
    private double[] sample;     // elements of the current sample
    private double boxAve;       // the population mean
    private double boxSd;        // the population SD
    private double[] sampleMean; // the history of sample means
    private double[] sampleSe;   // the history of sample SE(mean)'s
    private double[] seUsed;     // vector of SE's to use for intervals
    protected double factor;     // the blow-up factor for the intervals
    protected boolean showTruth; // show the box contents and the true mean
    protected boolean toggleTruth; // allow toggling hide/show
    protected boolean useTrueSe; // toggle true SE vs. sample SE
    protected boolean toggleSe;   // allow toggling true/sample SE
    protected boolean editBox;   // allow box contents to be edited
    private final int maxSamples = 1000; // max number of samples
    private final int maxSampleSize = 250; // max sample size
    private int lastLabelWidth = 0; // width of last label showing coverage%
    private final int nDigs = 4;  // number of digits in numbers in box
    private StringTokenizer st;

    public void init() {
        super.init();
        setBackground(Color.white);
        cover = 0;
        for (int i = 0; i < myPanel.length; i++) {
            myPanel[i] = new Panel();
            myPanel[i].setFont(labelFont);
        }
        for (int i = 0; i < myButton.length; i++) {
             myButton[i] = new Button(buttonLabel[i]);
        }
        for (int i = 0; i < myText.length; i++) {
             myLabel[i] = new Label(myText[i]);
        }
        for (int i=0; i < nSources; i++ ) {                // set up population hash
            sourceHash.put(rSource[i][1],rSource[i][0]);
        }
// make the scrollbars
        facBar = new TextBar(1, 0, Double.POSITIVE_INFINITY, 6, 3,
                             (Validator) new PositiveDoubleValidator(),
                             TextBar.NO_BAR,"#SE's:");
        samplesToTakeBar = new TextBar(1, 1, maxSamples, 3,
                             (Validator) new IntegerValidator(),
                             TextBar.NO_BAR,"Samples to take:");
        sampleSizeBar = new TextBar(1, 1, maxSampleSize, 3,
                             (Validator) new IntegerValidator(),
                             TextBar.NO_BAR,"Sample size:");
        facBar.setFont(labelFont);
        samplesToTakeBar.setFont(labelFont);
        sampleSizeBar.setFont(labelFont);

// read the parameters
        factor = Format.strToDouble(getParameter("factor"),1);
        showTruth = Format.strToBoolean(getParameter("showTruth"),true);
        useTrueSe = Format.strToBoolean(getParameter("useTrueSe"),true);
        useBoundSe = Format.strToBoolean(getParameter("useBoundSe"),false);
        toggleSe = Format.strToBoolean(getParameter("toggleSe"),true);
        toggleTruth = Format.strToBoolean(getParameter("toggleTruth"),true);
        editBox = Format.strToBoolean(getParameter("editBox"),true);
        nPop = Format.strToInt(getParameter("n"),0);
        sampleSize = Format.strToInt(getParameter("sampleSize"),2);
        sampleMean = new double[maxSamples];
        sampleSe = new double[maxSamples];
        seUsed = new double[maxSamples];
        samplesSoFar = 0;
        samplesToTake = 1;
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
                if (s.toLowerCase().equals("box") || s.toLowerCase().equals("0-1 box")) {
                    selectBoxFirst = true;
                }
            }
        } else {                                                                  // if not set, allow only box
            sourceChoice.addItem("Box");
            selectBoxFirst = true;
        }
        if (selectBoxFirst) {
            sourceChoice.select("Box");
        } else {
            sourceChoice.select(sourceChoice.getItem(0));
        }
        lastItem = sourceChoice.getSelectedItem();
        
// set up SE choices

// adjust button labels for parameter values
        if (useTrueSe) {
            myButton[1].setLabel("Use Sample SE");
        }  else {
            myButton[1].setLabel(" Use True SE ");
        }
        if (!showTruth) {
            myButton[2].setLabel("Show Box");
        }

// the layout begins.
        setLayout(new BorderLayout());
        title = "Confidence Intervals for the Mean";
        titleLabel = new Label(title);
        titleLabel.setFont(titleFont);
        sourceLabel = new Label("Sample from");
        myPanel[0].add(titleLabel);
        myButton[0].setFont(labelFont);
        myButton[1].setFont(labelFont);
        myButton[2].setFont(labelFont);
        if (sourceChoice.countItems() == 1) {
            sourceLabel.setText(sourceLabel.getText() + " " + sourceChoice.getSelectedItem());
            myPanel[0].add(sourceLabel);
        } else if (sourceChoice.countItems() > 1) {
            myPanel[0].add(sourceLabel);
            myPanel[0].add(sourceChoice);
        }
        myPanel[0].add(myButton[0]);
        if (toggleSe) {
            myPanel[0].add(seChoice);
        }
        if (toggleTruth) {
            myPanel[0].add(myButton[2]);
        }
        add("North", myPanel[0]);


        myPanel[3].setLayout(new FlowLayout(FlowLayout.CENTER));
        myPanel[3].add(sampleSizeBar);
        myPanel[3].add(samplesToTakeBar);
        myPanel[3].add(facBar);
        myPanel[3].add(myLabel[0]);

        add("South", myPanel[3]);

        myPanel[4].setLayout(new GridLayout(3,1));
        myPanel[5].setLayout(new FlowLayout(FlowLayout.CENTER));
        myPanel[6].setLayout(new BorderLayout());
        myPanel[7].setLayout(new FlowLayout(FlowLayout.CENTER));
        myPanel[8].setLayout(new FlowLayout(FlowLayout.CENTER));
        myPanel[9].setLayout(new FlowLayout(FlowLayout.CENTER));
        myPanel[7].add(myLabel[2]);
        myPanel[8].add(myLabel[3]);
        myPanel[9].add(myLabel[1]);
        myPanel[4].add(myPanel[7]);
        myPanel[4].add(myPanel[8]);
        myPanel[4].add(myPanel[9]);
        myPanel[5].add(myPanel[4]);
        myPanel[6].add("Center",myPanel[5]);
        add("West", myPanel[6]);

        myPanel[10].setLayout(new BorderLayout());
        box = new TextArea(8,8);
        box.setEditable(editBox);
        myPanel[10].add("Center", box);
        add("East", myPanel[10]);

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
        setSe();   // initialize the vector of SEs to use
// set the labels
        myLabel[1].setText("#SEs:");
        if (showTruth) {
             myLabel[0].setText(Format.doubleToPct(0)+ "cover");
             lastLabelWidth = labelFM.stringWidth(myLabel[0].getText());
             myLabel[1].setText("Ave(Box): " + Format.doubleToStr(boxAve, 2));
             myLabel[3].setText("SD(Box): " + Format.doubleToStr(boxSd, 2));
        } else {
             myLabel[0].setText(" ");
             myLabel[1].setText(" ");
             myLabel[3].setText(" ");
        }
        myLabel[2].setText("Samples: " + samplesSoFar);
        for (int i=0; i< myLabel.length; i++) {
            myLabel[i].invalidate();
        }
        samplesToTakeBar.setValue(samplesToTake );
        sampleSizeBar.setValue(sampleSize);
        facBar.setValue(factor);

        myCiPlot = new CiPlot(boxAve, showTruth, null, seUsed, factor);
        myCiPlot.setBackground(Color.white);
        myPanel[1].setLayout(new BorderLayout());
        myPanel[1].add("Center", myCiPlot);
        add("Center", myPanel[1]);
        myPanel[3].invalidate();
        myPanel[5].invalidate();
        myPanel[6].invalidate();
        validate(); // wishful thinking
        showPlot(); // refresh the ciplot
    }

    protected void setSe() {
        if (useTrueSe) {
            if (sourceChoice.getSelectedItem().equals("Box")) {
                for (int i = 0; i < samplesSoFar; i++) {
                    seUsed[i] = boxSd/Math.sqrt(sampleSize + 0.0);
                }
            } else if (sourceChoice.getSelectedItem().equals("Normal")) {
                for (int i = 0; i < samplesSoFar; i++) {
                    seUsed[i] = 1.0/Math.sqrt(sampleSize + 0.0);
                }
            } else if (sourceChoice.getSelectedItem().equals("Uniform")) {
                for (int i = 0; i < samplesSoFar; i++) {
                    seUsed[i] = (1.0/12.0)/Math.sqrt(sampleSize + 0.0);
                }
            } else {
                System.out.println("Error in Ci.setSE(): unsupported source " + sourceChoice.getSelectedItem());
            }
        } else {
            for (int i = 0; i < samplesSoFar; i++) {
                seUsed[i] = sampleSe[i];
            }
        }
        return;
    }

    protected void initPop() {
// compute population statistics
        if (sourceChoice.getSelectedItem().equals("Box")) {
            nPop = pop.length;
            boxAve = 0.0;
            boxSd = 0.0;
            for (int i = 0; i < nPop; i++) {
                boxAve += pop[i];
            }
            boxAve /= nPop;
            for (int i = 0; i < nPop; i++) {
                boxSd += (pop[i] - boxAve)*(pop[i] - boxAve);
            }
            boxSd = Math.sqrt(boxSd/nPop);
        } else if (sourceChoice.getSelectedItem().equals("Normal")) {
            nPop = 0;
            boxAve = 0.0;
            boxSd = 1.0;
        } else if (sourceChoice.getSelectedItem().equals("Uniform")) {
            nPop = 0;
            boxAve = 0.5;
            boxSd = Math.sqrt(1.0/12.0);
        }
// reset the labels
        if (showTruth) {
             myLabel[0].setText(Format.doubleToPct(0) + " cover");
             myLabel[1].setText("Ave(Box): " + Format.doubleToStr(boxAve, 3));
             myLabel[3].setText("SD(Box): " + Format.doubleToStr(boxSd, 3));
        } else {
             myLabel[0].setText(" ");
             myLabel[1].setText(" ");
             myLabel[3].setText(" ");
        }
        for (int i=0; i < myLabel.length; i++) {
            myLabel[i].invalidate();
        }
        myPanel[3].invalidate();
        myPanel[5].invalidate();
        myPanel[6].invalidate();
        validate();
    }

    public boolean handleEvent(Event e) {
        if (e.id == Event.WINDOW_DESTROY) {
            System.exit(0);
        } else if (e.id == Event.SCROLL_ABSOLUTE
                 || e.id == Event.SCROLL_LINE_DOWN
                 || e.id == Event.SCROLL_LINE_UP
                 || e.id == Event.SCROLL_PAGE_DOWN
                 || e.id == Event.SCROLL_PAGE_UP)  {
            if  (e.target == sampleSizeBar) { // clear history, reset sample size, redisplay pop histogram
                sampleSize = (int) sampleSizeBar.getValue();
                samplesSoFar = 0;
                cover = 0;
                double sSd = boxSd/Math.sqrt(sampleSize);
                if (showTruth) {
                   myLabel[0].setText(Format.doubleToPct(0) + " cover");
                   myLabel[1].setText("Ave(Box): " + Format.doubleToStr(boxAve,2));
                }
                myLabel[2].setText("Samples: " + samplesSoFar);
                for (int i=0; i < myLabel.length; i++) myLabel[i].invalidate();
                myPanel[3].invalidate();
                myPanel[5].invalidate();
                myPanel[6].validate();
                validate();
                showPlot();
                return(super.handleEvent(e));
            } else if (e.target == facBar) {
                factor = facBar.getValue();
                setCover();
                showPlot();
            } else if (e.target == samplesToTakeBar) {
                samplesToTake = (int) samplesToTakeBar.getValue();
            }
        } else if (e.target == sourceChoice) {
            String thisItem = sourceChoice.getSelectedItem();
            if (thisItem != lastItem) {
                lastItem = thisItem;
                if ( sourceChoice.getSelectedItem().equals("Box") ) {
                    setBox(box.getText(),true);
                } else {
                    setBox(sourceChoice.getSelectedItem());
                }
                showTruth = true;
                myButton[2].setLabel("Hide Box");
                myButton[2].invalidate();
                showPlot();
            }
            return(super.handleEvent(e));
        } else if (e.target == box && e.id == Event.LOST_FOCUS && box.isEditable()) {
            setBox(box.getText(),false);
            showPlot();
            return(super.handleEvent(e));
        } else if (e.id == Event.ACTION_EVENT) {
            if (e.arg == "Take Sample") {
                int lim = maxSamples - samplesSoFar; // number possible
                for (int i = 0; i < Math.min(samplesToTake, lim); i++) {
                    xBar();
                }
                myLabel[2].setText("Samples: " + samplesSoFar);
                if (showTruth) {
                    myLabel[0].setText(Format.doubleToPct(cover/samplesSoFar)+ " cover");
                }
                for (int i=0; i < myLabel.length; i++) {
                    myLabel[i].invalidate();
                }
                myPanel[3].validate();
                myPanel[6].validate();
                showPlot();
                return(super.handleEvent(e));
            } else if (e.arg == "Use Sample SE") {
                useTrueSe = false;
                myButton[1].setLabel(" Use True SE ");
                myButton[1].invalidate();
                myPanel[0].validate();
                setSe();
                setCover();
                showPlot();
            } else if (e.arg == " Use True SE ") {
                useTrueSe = true;
                myButton[1].setLabel("Use Sample SE");
                myButton[1].invalidate();
                myPanel[0].validate();
                setSe();
                setCover();
                showPlot();
                return(super.handleEvent(e));
            } else if (e.arg == "Hide Box") {
                showTruth = false;
                sourceChoice.select("Box");
                lastItem = "Box";
                myButton[2].setLabel("Show Box");
                myButton[2].invalidate();
                myPanel[0].validate();
                box.setText("Contents \n Hidden");
                randBox();
                samplesSoFar = 0;
                setCover();
                myLabel[2].setText("Samples: " + samplesSoFar);
                myLabel[2].invalidate();
                myPanel[6].validate();
                showPlot();
                return(super.handleEvent(e));
            } else if (e.arg == "Show Box") {
                showTruth = true;
                myButton[2].setLabel("Hide Box");
                myButton[2].invalidate();
                myPanel[0].validate();
                String thePop = "";
                for (int i = 0; i < nPop; i++) {
                    thePop += Format.doubleToStr(pop[i],nDigs) + "\n"; // print the population
                }
                setBox(thePop,true, false);
                myPanel[6].validate();
                setCover();
                showPlot();
                return(super.handleEvent(e));
            }
        }
        return(super.handleEvent(e));
    }

    void showPlot() {
        if (samplesSoFar > 0) {
            double[] sv = new double[samplesSoFar];
            System.arraycopy(sampleMean, 0, sv, 0, samplesSoFar);
            myCiPlot.redraw(boxAve, showTruth, sv, seUsed, factor);
        } else {
            myCiPlot.redraw(boxAve, showTruth, null, seUsed, factor);
        }
        return;
    }

    void setCover() {
        cover = 0;
        double wide = 0;
        for (int i = 0; i < samplesSoFar; i++) {
            wide = factor*seUsed[i];
            if (Math.abs(sampleMean[i] - boxAve) <= wide) cover++;
        }
        if (showTruth) {
            if (samplesSoFar > 0)
                 myLabel[0].setText(Format.doubleToPct(cover/samplesSoFar) +
                                    " cover");
            else myLabel[0].setText(Format.doubleToPct(0) +
                                    " cover");
        } else {
            myLabel[0].setText(" ");
        }
        myLabel[0].invalidate();
        myPanel[3].invalidate();
        myPanel[5].invalidate();
        myPanel[6].invalidate();
        validate();
    }

    public void randBox() {
        nPop = 10;
        pop = new double[nPop];
        double lim = 50*Math.random();
        double ctr = 25*Math.random();
        for (int i = 0; i < nPop; i++) {
            pop[i] = lim*Math.random() - ctr;
        }
        initPop();
    }

    public void setBox(String popStr) {
        setBox(popStr, true, true);
    }

    public void setBox(String popStr, boolean updateBox) {
        setBox(popStr, updateBox, true);
    }

    public void setBox(String newBox, boolean updateBox, boolean reInit) { // parse new population
        if (newBox.toLowerCase().equals("normal")) {
            pop = new double[2];
            pop[0] = -4;
            pop[1] = 4;
            box.setText("Normal");
            box.invalidate();
            sourceChoice.select("Normal");
        } else if (newBox.toLowerCase().equals("uniform")) {
            pop = new double[2];
            pop[0] = 0;
            pop[1] = 1;
            box.setText("Uniform");
            box.invalidate();
            sourceChoice.select("Uniform");
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
            if (updateBox) {
                box.setText("");
                if (showTruth) {
                    String popStr = "";
                    for (int i = 0; i < nPop; i++) {              // print the population in the box
                        popStr = Format.numToString(pop[i],nDigs)  + "\n";
                        box.appendText(popStr);
                    }
                } else {
                    box.appendText("Contents Hidden");
                }
                box.invalidate();
            }
            sourceChoice.select("Box");
        }
        if (reInit) {
            initPop();
            samplesSoFar = 0;
            setCover();
            myLabel[2].setText("Samples: " + samplesSoFar);
        }
    }  // ends setBox(String, boolean)

    protected void xBar() {
        double xb = 0;
        double sse = 0;
        double[] x = new double[sampleSize];
        if (sourceChoice.getSelectedItem().equals("Box")) {
            for (int i = 0; i < sampleSize; i++) {
                 x[i] = pop[ (int) (Math.random()*nPop)];
                 xb += x[i];
            }
        } else if (sourceChoice.getSelectedItem().equals("Uniform")) {
            for (int i = 0; i < sampleSize; i++) {
                 x[i] = Math.random();
                 xb += x[i];
            }
        } else if (sourceChoice.getSelectedItem().equals("Normal")) {
            for (int i = 0; i < sampleSize; i++) {
                 x[i] = PbsStat.rNorm();
                 xb += x[i];
            }
        }
        xb /= sampleSize;
        sse = PbsStat.sampleSd(x)/Math.sqrt(sampleSize);
        sampleMean[samplesSoFar] = xb;
        sampleSe[samplesSoFar] = sse;
        if (useTrueSe) {
            seUsed[samplesSoFar++] = boxSd/Math.sqrt((double) sampleSize);
        } else {
            seUsed[samplesSoFar++] = sse;
        }
        if (Math.abs(xb - boxAve) <= factor*seUsed[samplesSoFar - 1]) {
            cover++;
        }
    }

}
