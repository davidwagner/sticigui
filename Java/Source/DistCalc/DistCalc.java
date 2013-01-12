/*

public class DistCalc extends Applet

A gui interface tool for finding probabilities for a variety
of distributions:
Normal, Binomial, Exponential, Geometric, Hypergeometric, Negative Binomial,
Poisson, Student t

@author P.B. Stark stark AT stat.berkeley.edu
@copyright 1997-2001. All rights reserved
@version 0.9
Last modified 13 March 2001

*/

import java.awt.*;
import java.util.*;
import java.applet.*;
import PbsGui.*;
import PbsStat.*;


public class DistCalc extends Applet {
    Panel[] myPanel = new Panel[5];
    String[][] distributions = { {"Normal", "Mean", "SD"},
                                 {"Binomial","Trials", "P(success)"},
                                 {"Chi-square","degrees of freedom"},
                                 {"Exponential","Mean"},
                                 {"Geometric","P(success)"},
                                 {"Hypergeometric","Population Size",
                                    "#Good in Population","Sample Size"},
                                 {"Negative Binomial","P(success)",
                                    "#Successes"},
                                 {"Poisson","Mean"},
                                 {"Student t","degrees of freedom"},
                               };
    CardLayout distLayout = new CardLayout();
    CardLayout rangeLayout = new CardLayout();
    Panel[] distPanel = new Panel[distributions.length];
    private boolean[] include = new boolean[distributions.length];
    Choice distChoice = new Choice();
    private int dig = 6;
    private int pDecimals = 6; // decimal places in probability inputs
    Checkbox ub = new Checkbox();
    Checkbox lb = new Checkbox();
    Label probLabel;
    Label statsLabel = new Label("E(X)=   , and  SE(X)=   ");
    TextBar lo = new TextBar(0.0, Double.NEGATIVE_INFINITY,
            Double.POSITIVE_INFINITY, dig,
            (Validator) new DoubleValidator(),
            TextBar.NO_BAR, "X >=");
    TextBar hi = new TextBar(0.0, Double.NEGATIVE_INFINITY,
            Double.POSITIVE_INFINITY, dig,
            (Validator) new DoubleValidator(),
            TextBar.NO_BAR, "X <=");
    TextBar normMean = new TextBar(0.0, Double.NEGATIVE_INFINITY,
            Double.POSITIVE_INFINITY, dig,
            (Validator) new DoubleValidator(),
            TextBar.NO_BAR, distributions[0][1]);
    TextBar normSd = new TextBar(1, 1.e-10, Double.POSITIVE_INFINITY, dig,
            (Validator) new PositiveDoubleValidator(),
            TextBar.NO_BAR, distributions[0][2]);
    TextBar binN = new TextBar(1, 1, Double.POSITIVE_INFINITY, dig,
            (Validator) new PositiveIntegerValidator(),
            TextBar.NO_BAR, distributions[1][1]);
    TextBar binP = new TextBar(0.5, 0.0, 1.0, dig+2, pDecimals,
            (Validator) new ProbabilityValidator(),
            TextBar.NO_BAR, distributions[1][2]);
    TextBar chi2df = new TextBar(1, 1, Double.POSITIVE_INFINITY, dig,
            (Validator) new PositiveIntegerValidator(),
            TextBar.NO_BAR,distributions[2][1]);
    TextBar expMean = new TextBar(1,1.e-10, Double.POSITIVE_INFINITY, dig,
            (Validator) new PositiveDoubleValidator(),
            TextBar.NO_BAR, distributions[3][1]);
    TextBar geoP = new TextBar(0.5, 0.0, 1.0, dig+2, pDecimals,
            (Validator) new ProbabilityValidator(),
            TextBar.NO_BAR, distributions[4][1]);
    TextBar hyGeoPop = new TextBar(2, 0.0, Double.POSITIVE_INFINITY, dig,
            (Validator) new PositiveIntegerValidator(),
            TextBar.NO_BAR, distributions[5][1]);
    TextBar hyGeoGood = new TextBar(1.0, 0.0, Double.POSITIVE_INFINITY, dig,
            (Validator) new PositiveIntegerValidator(),
            TextBar.NO_BAR, distributions[5][2]);
    TextBar hyGeoSam = new TextBar(1.0, 0.0, Double.POSITIVE_INFINITY, dig,
            (Validator) new PositiveIntegerValidator(),
            TextBar.NO_BAR, distributions[5][3]);
    TextBar negBinP = new TextBar(0.5, 0.0, 1.0, dig+2, pDecimals,
            (Validator) new ProbabilityValidator(),
            TextBar.NO_BAR, distributions[6][1]);
    TextBar negBinSuc = new TextBar(1.0, 0.0, Double.POSITIVE_INFINITY, dig,
            (Validator) new PositiveIntegerValidator(),
            TextBar.NO_BAR, distributions[6][2]);
    TextBar poissonMean = new TextBar(1, 0, Double.POSITIVE_INFINITY, dig,
            (Validator) new PositiveDoubleValidator(),
            TextBar.NO_BAR, distributions[7][1]);
    TextBar tdf = new TextBar(2, 1, Double.POSITIVE_INFINITY, dig,
            (Validator) new PositiveIntegerValidator(),
            TextBar.NO_BAR,distributions[8][1]);
    double prob;
    double EX;
    double SDX;
    Vector distName = new Vector(3);

    public void init() {
        super.init();
        setLayout(new BorderLayout());
        setBackground(Color.white);
        for (int i=0; i < myPanel.length; i++) myPanel[i] = new Panel();
        String distList;
        if (getParameter("distributions") == null) {
            for (int i=0; i < include.length; i++) { include[i] = true; }
        } else {
            StringTokenizer st = new StringTokenizer(getParameter("distributions"),"\n\t,",false);
            for (int j=0; st.hasMoreTokens(); j++) {
                distName.addElement(st.nextToken().trim());
            }
            for (int j=0; j < distName.size(); j++) {
                distList = (String) distName.elementAt(j);
                if (distList.equals("all")) {
                    for (int i=0; i < include.length; i++) {
                        include[i] = true;
                    }
                } else if (distList.equals("discrete")) {
                    include[0] = false;
                    include[1] = true;
                    include[2] = false;
                    include[3] = false;
                    for (int i=4; i < distributions.length-1; i++) {
                        include[i] = true;
                    }
                    include[8] = false;
                } else if (distList.equals("continuous")) {
                    include[0] = true;
                    include[1] = false;
                    include[2] = true;
                    include[3] = true;
                    for (int i=4; i < distributions.length - 1; i++) {
                        include[i] = false;
                    }
                    include[8] = true;
                } else {
                    for (int i=0; i < distributions.length; i++ ) {
                        if (distList.toLowerCase().equals(distributions[i][0].toLowerCase())) {
                            include[i] = true;
                        }
                    }
                }
            }
        }
        myPanel[0].setLayout(distLayout);
        for (int i=0; i < distributions.length; i++) {
            distPanel[i] = new Panel();
            if (include[i]) {
                distChoice.addItem(distributions[i][0]);
                myPanel[0].add(distributions[i][0], distPanel[i]);
            }
        }
        myPanel[2].setLayout(new FlowLayout(FlowLayout.CENTER));
        myPanel[2].add(new Label("If X has a "));
        myPanel[2].add(distChoice);
        myPanel[2].add(new Label("distribution, with parameters"));
        add("North", myPanel[2]);
        distPanel[0].add(normMean);
        distPanel[0].add(normSd);
        distPanel[1].add(binN);
        distPanel[1].add(binP);
        distPanel[2].add(chi2df);
        distPanel[3].add(expMean);
        distPanel[4].add(geoP);
        distPanel[5].add(hyGeoPop);
        distPanel[5].add(hyGeoGood);
        distPanel[5].add(hyGeoSam);
        distPanel[6].add(negBinP);
        distPanel[6].add(negBinSuc);
        distPanel[7].add(poissonMean);
        distPanel[8].add(tdf);
        add("Center",myPanel[0]);
        myPanel[1].setLayout(new FlowLayout(FlowLayout.CENTER));
        myPanel[1].add(new Label("the chance that"));
        myPanel[1].add(lb);
        myPanel[1].add(lo);
        myPanel[1].add(new Label("and"));
        myPanel[1].add(ub);
        myPanel[1].add(hi);
        myPanel[1].add(probLabel = new Label("is:"));
        myPanel[3].setLayout(new FlowLayout(FlowLayout.CENTER));
        myPanel[3].add(statsLabel);
        myPanel[4].setLayout(new GridLayout(2,1));
        myPanel[4].add(myPanel[1]);
        myPanel[4].add(myPanel[3]);
        add("South",myPanel[4]);
        distLayout.show(myPanel[0], distChoice.getSelectedItem());
        setArea();
        setStats();
    }


    private void setArea() {
        if (lb.getState() && ub.getState() && lo.getValue() > hi.getValue()) {
            probLabel.setText("is: 0%");
        } else if ( !lb.getState() && !ub.getState() ) {
            probLabel.setText("is: 100%");
        } else {
            prob = 1.0;
            if (distChoice.getSelectedItem().equals("Normal")) {
                if (ub.getState()) {
                    prob = PbsStat.normCdf(
                        (hi.getValue() - normMean.getValue())/normSd.getValue());
                }
                if (lb.getState()) {
                    prob -= PbsStat.normCdf(
                        (lo.getValue() - normMean.getValue())/normSd.getValue());
                }
            } else if (distChoice.getSelectedItem().equals("Binomial")) {
                if (ub.getState() && lb.getState()) {
                    prob = 0.0;
                    int n = (int) Math.floor(binN.getValue());
                    for (int i= (int) Math.floor(lo.getValue());
                         i <= (int) Math.floor(hi.getValue()); i++ ) {
                                prob += PbsStat.binomialPmf(n, i, binP.getValue());
                    }
                } else if (ub.getState()) {
                    prob = PbsStat.binomialCdf((int) Math.floor(binN.getValue()),
                            binP.getValue(), (int) Math.floor(hi.getValue()));
                } else if (lb.getState()) {
                    prob -= PbsStat.binomialCdf((int) Math.floor(binN.getValue()),
                            binP.getValue(), (int) Math.floor(lo.getValue()) - 1);
                }
            } else if (distChoice.getSelectedItem().equals("Chi-square")) {
                if (ub.getState()) {
                    prob = PbsStat.chi2Cdf(hi.getValue(), chi2df.getValue());
                }
                if (lb.getState()) {
                    prob -= PbsStat.chi2Cdf(lo.getValue(), chi2df.getValue());
                }
            } else if (distChoice.getSelectedItem().equals("Exponential")) {
                if (ub.getState()) {
                    prob = PbsStat.expCdf(hi.getValue(), expMean.getValue());
                }
                if (lb.getState()) {
                    prob -= PbsStat.expCdf(lo.getValue(), expMean.getValue());
                }
            } else if (distChoice.getSelectedItem().equals("Geometric")) {
                if (ub.getState() && lb.getState()) {
                    prob = 0.0;
                    for (int i= (int) Math.floor(lo.getValue());
                         i <= (int) Math.floor(hi.getValue()); i++ ) {
                                prob += PbsStat.geoPmf(geoP.getValue(),i);
                    }
                } else if (ub.getState()) {
                    prob = PbsStat.geoCdf(geoP.getValue(),
                            (int) Math.floor(hi.getValue()));
                } else if (lb.getState()) {
                    prob -= PbsStat.geoCdf(geoP.getValue(),
                            (int) Math.floor(lo.getValue()) - 1);
                }
            } else if (distChoice.getSelectedItem().equals("Hypergeometric")) {
                int N = (int) Math.floor(hyGeoPop.getValue());
                int n = (int) Math.floor(hyGeoSam.getValue());
                int G = (int) Math.floor(hyGeoGood.getValue());
                if (ub.getState() && lb.getState()) {
                    prob = 0.0;
                    for (int i= (int) Math.floor(lo.getValue());
                         i <= (int) Math.floor(hi.getValue()); i++ ) {
                                prob += PbsStat.hyperGeoPmf(N, G, n, i);
                    }
                } else if (ub.getState()) {
                    prob = PbsStat.hyperGeoCdf(N,G,n,(int) Math.floor(hi.getValue()));
                } else if (lb.getState()) {
                    prob -= PbsStat.hyperGeoCdf(N,G,n,(int) Math.floor(lo.getValue())-1);
                }
            } else if (distChoice.getSelectedItem().equals("Negative Binomial")) {
                if (ub.getState() && lb.getState()) {
                    int r = (int) Math.floor(negBinSuc.getValue());
                    prob = 0.0;
                    for (int i= (int) Math.floor(lo.getValue());
                         i <= (int) Math.floor(hi.getValue()); i++ ) {
                                prob += PbsStat.negBinomialPmf(negBinP.getValue(), r, i);
                    }
                } else if (ub.getState()) {
                    prob = PbsStat.negBinomialCdf(negBinP.getValue(),
                            (int) Math.floor(negBinSuc.getValue()),
                            (int) Math.floor(hi.getValue()));
                } else if (lb.getState()) {
                    prob -= PbsStat.negBinomialCdf(negBinP.getValue(),
                            (int) Math.floor(negBinSuc.getValue()),
                            (int) Math.floor(lo.getValue()) - 1);
                }
            } else if (distChoice.getSelectedItem().equals("Poisson")) {
                if (ub.getState() && lb.getState()) {
                    prob = 0.0;
                    for (int i= (int) Math.floor(lo.getValue());
                         i <= (int) Math.floor(hi.getValue()); i++ ) {
                                prob += PbsStat.poissonPmf(poissonMean.getValue(), i);
                    }
                } else if (ub.getState()) {
                    prob = PbsStat.poissonCdf(poissonMean.getValue(),
                            (int) Math.floor(hi.getValue()));
                } else if (lb.getState()) {
                    prob -= PbsStat.poissonCdf(poissonMean.getValue(),
                            (int) Math.floor(lo.getValue()) - 1);
                }
            } else if (distChoice.getSelectedItem().equals("Student t")) {
                if (ub.getState()) {
                    prob = PbsStat.tCdf(hi.getValue(),(int) tdf.getValue());
                }
                if (lb.getState()) {
                    prob -= PbsStat.tCdf(lo.getValue(),(int) tdf.getValue());
                }
            }
            int dDisp = dig;
            if (prob < 0.01) { dDisp++;}
            probLabel.setText("is: " + Format.doubleToPct(prob, dDisp, 3, true));
        }
        probLabel.invalidate();
        lb.invalidate();
        ub.invalidate();
        lo.invalidate();
        hi.invalidate();
        myPanel[1].invalidate();
        myPanel[4].validate();
        myPanel[4].repaint();
    }

    private void setStats() {
        if (distChoice.getSelectedItem().equals("Normal")) {
            EX = normMean.getValue();
            SDX = normSd.getValue();
        } else if (distChoice.getSelectedItem().equals("Binomial")) {
            EX = binN.getValue()*binP.getValue();
            SDX = Math.sqrt(binN.getValue()*binP.getValue()*(1.0-binP.getValue()));
        } else if (distChoice.getSelectedItem().equals("Chi-square")) {
            EX = chi2df.getValue();
            SDX = Math.sqrt(2.0*chi2df.getValue());
        } else if (distChoice.getSelectedItem().equals("Exponential")) {
            EX = expMean.getValue();
            SDX = expMean.getValue();
        } else if (distChoice.getSelectedItem().equals("Geometric")) {
            EX = 1.0/geoP.getValue();
            SDX = Math.sqrt(1-geoP.getValue())/geoP.getValue();
        } else if (distChoice.getSelectedItem().equals("Hypergeometric")) {
            double hyp = hyGeoGood.getValue()/hyGeoPop.getValue();
            EX = hyGeoSam.getValue()*hyp;
            SDX = Math.sqrt((hyGeoPop.getValue() - hyGeoSam.getValue())/
                (hyGeoPop.getValue()-1)*EX*(1-hyp));
        } else if (distChoice.getSelectedItem().equals("Negative Binomial")) {
            EX = negBinSuc.getValue()/negBinP.getValue();
            SDX = Math.sqrt(negBinSuc.getValue()*(1.0-negBinP.getValue()))/
                    negBinP.getValue();
        } else if (distChoice.getSelectedItem().equals("Poisson")) {
            EX = poissonMean.getValue();
            SDX = Math.sqrt(poissonMean.getValue());
        } else if (distChoice.getSelectedItem().equals("Student t")) {
            EX = Double.NaN;
            SDX = Double.NaN;
            if (tdf.getValue() == 2) EX = 0;
            else if (tdf.getValue() > 2) {
                EX = 0;
                SDX = Math.sqrt(tdf.getValue()/(tdf.getValue()-2));
            }
        }
        statsLabel.setText("E(X)=" + Format.doubleToStr(EX, dig) + ", and SE(X)="
                            + Format.doubleToStr(SDX, dig));
        statsLabel.invalidate();
        myPanel[3].invalidate();
        myPanel[4].validate();
        myPanel[4].repaint();
    }


    public boolean handleEvent(Event e) {
        if (e.id == Event.WINDOW_DESTROY) {
            System.exit(0);
        } else if (e.id == Event.SCROLL_ABSOLUTE ||
                 e.id == Event.SCROLL_PAGE_UP ||
                 e.id == Event.SCROLL_PAGE_DOWN ||
                 e.id == Event.SCROLL_LINE_UP ||
                 e.id == Event.SCROLL_LINE_DOWN ) {
            setArea();
            setStats();
            return true;
        } else if (e.id == Event.ACTION_EVENT) {
            if (e.target == distChoice) {
                distLayout.show(myPanel[0], distChoice.getSelectedItem());
                setArea();
                setStats();
            } else {
                setArea();
                setStats();
            }
        }
        return super.handleEvent(e);
    }
}
