/**
@author P.B. Stark statistics.berkeley.edu/~stark
@version 0.9
@copyright 1997-2001. P.B. Stark. All rights reserved.
Last modified 3/14/01.

An applet to study the law of large numbers

**/

import java.awt.*;
import java.applet.*;
import java.util.*;
import PbsGui.*;

public class Lln extends Applet {

    /* want controls for sample size, probability of success,
   , toggle number/percentage
    */

    private TextBar trialBar; // number of trials
    private TextBar pBar; // probability of success in each trial
    String[] buttonLabel = {"Percentage"
                           };
    protected Button[] myButton = new Button[buttonLabel.length];
    protected Panel[] myPanel = new Panel[3];
    protected Label titleLabel;
    protected Font titleFont = new Font("TimesRoman", Font.BOLD, 16);
    XYPlot plot;
    private String title;
    private double p; // probability of success in each trial
    private double[] xVal; // will get the trial numbers 1, 2, ..., nTrials
    private double[] yVal; // cumulative fraction or number of successes.
    private int nTrials; // number of trials
    private boolean percent; // toggle percentage vs. number

    private final int maxTrials = 20000; // max number of trials

    public void init() {
        super.init();
        setBackground(Color.white);
        for (int i = 0; i < myPanel.length; i++) {
            myPanel[i] = new Panel();
        }
        for (int i = 0; i < buttonLabel.length; i++) {
             myButton[i] = new Button(buttonLabel[i]);
        }
        p = 0.5;
        percent = Format.strToBoolean(getParameter("percent"), false);
        if (percent) {
            myButton[0].setLabel("Number");
        }
        nTrials = 0;
        extendTrials(maxTrials/25);
// the layout begins.
        setLayout(new BorderLayout());
        title = "Law of Large Numbers: %Successes - %Expected";
        titleLabel = new Label(title);
        myPanel[0].setFont(titleFont);
        myPanel[0].add(titleLabel);
        add("North", myPanel[0]);

        plot = new XYPlot(); // new plot
        myPanel[1].setLayout(new BorderLayout());
        myPanel[1].add("Center", plot);
        myPanel[2].setLayout(new FlowLayout(FlowLayout.CENTER));
        myPanel[2].add(pBar = new TextBar(100*p, 0, 100, 4,
                       (Validator) new DoubleValidator(),
                        TextBar.TEXT_TOP, "Chance of success (%)"));
        myPanel[2].add(trialBar = new TextBar(maxTrials/25, 1, maxTrials, 5,
                       (Validator) new IntegerValidator(),
                        TextBar.TEXT_TOP, "# Trials"));
        myPanel[2].add(myButton[0]);
        add("South", myPanel[2]);
        add("Center", myPanel[1]);

        validate(); // wishful thinking
        if (!percent) titleLabel.setText(
                           "Law of Large Numbers: Successes - Expected");
        plot.redraw(xVal, yVal); // refresh the histogram
    }

    public boolean handleEvent(Event e) {
        if (e.id == Event.WINDOW_DESTROY) {
            System.exit(0);
        } else if (e.id == Event.SCROLL_ABSOLUTE
                 || e.id == Event.SCROLL_LINE_DOWN
                 || e.id == Event.SCROLL_LINE_UP
                 || e.id == Event.SCROLL_PAGE_DOWN
                 || e.id == Event.SCROLL_PAGE_UP) {
            if (e.target == pBar) { //
                p = pBar.getValue()/100;
                int n = nTrials;
                nTrials = 0;
                extendTrials(n);
                plot.redraw(xVal, yVal);
            } else if (e.target == trialBar) {
                int newTr = (int) trialBar.getValue();
                if (newTr <= nTrials) {
                    nTrials = newTr;
                    double[] dum = new double[nTrials];
                    System.arraycopy(yVal, 0, dum, 0, nTrials);
                    yVal = new double[nTrials];
                    yVal = dum;
                    dum = new double[nTrials];
                    System.arraycopy(xVal, 0, dum, 0, nTrials);
                    xVal = dum;
                } else { // add some more trials; extend xVal and yVal
                    extendTrials(newTr - nTrials);
                }

                plot.redraw(xVal, yVal);
                return(true);

            }

        } else if (e.id == Event.ACTION_EVENT) {
            if (e.arg == "Percentage") {
                percent = true;
                myButton[0].setLabel("Number");
                for (int i = 0; i < nTrials; i++) {
                    yVal[i] /= xVal[i]/100;
                }
                myPanel[0].remove(titleLabel);
                titleLabel= new Label(
                        "Law of Large Numbers: %Successes - %Expected");
                myPanel[0].add(titleLabel);
                myPanel[0].validate();
                plot.redraw(xVal, yVal);
                return(true);
            } else if (e.arg == "Number") {
                percent = false;
                myButton[0].setLabel("Percentage");
                myButton[0].invalidate();
                for (int i = 0; i < nTrials; i++) {
                    yVal[i] *= xVal[i]/100;
                }
                titleLabel.setText(
                           "Law of Large Numbers: Successes - Expected");
                titleLabel.invalidate();
                validate();
                plot.redraw(xVal, yVal);
                return(true);
            }

        }
        return(super.handleEvent(e));
    }

    void extendTrials(int n) {
        int ext = Math.min( n, maxTrials - nTrials );
        double[] ySave = yVal;
        double[] xSave = xVal;
        yVal = new double[nTrials + ext];
        xVal = new double[nTrials + ext];
        double r = 0;
        if (nTrials > 0 ) {
            System.arraycopy(xSave, 0, xVal, 0, nTrials);
            System.arraycopy(ySave, 0, yVal, 0, nTrials);
        } else if (nTrials == 0 && ext >= 1) {
            nTrials = 1;
            ext -= 1;
            r = Math.random();
            yVal[0] = -p;
            xVal[0] = 1;
            if ( r <= p ) {
                yVal[0] += 1;
            }
            if (percent) {
                yVal[0] *= 100;
            }
        }
        if (!percent) {
            for (int i = nTrials; i < nTrials + ext; i++) {
                r = Math.random();
                yVal[i] = yVal[i-1] - p;
                if (r <= p) {
                    yVal[i] += 1;
                }
                xVal[i] = i+1;
            }
        } else {
            for (int i = nTrials; i < nTrials + ext; i++) {
                r = Math.random();
                yVal[i] = (i * yVal[i-1] - 100.0 * p )/(i+1);
                if (r <= p) {
                    yVal[i] += (100.0 / ( (double) (i+1)));
                }
                xVal[i] = i+1;
            }
        }
        nTrials += ext;
    }


}
