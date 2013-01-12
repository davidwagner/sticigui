/** class Correlation extends Applet
@author P.B. Stark http://statistics.berkeley.edu/~stark
@version 0.5
@copyright 1997-2001.  All rights reserved.
Last modified 11 August 2001.

Plots a scatterplot of bivariate normal data with a specified correlation
coefficient, if no data are passed as parameters.

If data (x_j) and (y_j) are passed, plots a scatterplot of them instead.

Also plots the SD line and linear regression line on demand.

Points can be added by clicking the mouse in the plot area, and cleared
by clicking a button.

Whether or not those points are used in computing the ave, r, SD, etc.
is controlled by a button.

Another button plots the graph of averages, with a default of 11 points,
which can be overriden with a parameter (GraphOfAvePoints).

Another button toggles the display to a regression residual plot.

Has exposed public methods to set n and r for the simulated normal data,
and to set x and y for passed data.

**/

import java.awt.*;
import java.util.*;
import java.applet.*;
import PbsGui.*;
import PbsStat.*;

public class Correlation extends Applet {

    private TextBar rBar;
    private TextBar nBar;
    private Label titleLabel;
    private Label rLabel;
    private String[] buttonText = { "  SDs ", "  SD Line ",
                                    "  Graph of Ave ",
                                    "  Regression Line ",
                                    "Plot Residuals",
                                    "  Use Added Points  ",
                                    "Clear Added Points"
                                  };
    Button[] myButton = new Button[buttonText.length];
    Panel pSouth;
    Panel pSouthTop;
    Panel pSouthBot;
    Panel pCenter;
    Panel pCenterTop;
    Panel pCenterBot;
    Panel pCenterBot1;
    Panel pCenterBot2;
    Panel nPanel;
    ScatterPlot scat;
//
    private double[] xVal;
    private double[] yVal;
    private double[] yToPlot;
    private double xMean;
    private double yMean;
    private double sdX;
    private double sdY;
    private double[] sdLineX;
    private double[] sdLineY;
    private double[] regressLineX;
    private double[] regressLineY;
    private double[] xPointOfAve;
    private double[] yPointOfAve;
    private int[] nPointOfAve;
    private int graphOfAvePoints;
    private int n;
    private int nAdded; // number of points added by clicking
    private int nToUse; // number of points to use to calculate r, etc.
    private double r;
    private double rHat;

    private boolean sdButton;   // show the SD button?
    private boolean sdLineButton; // show SD line button?
    private boolean showSdLine; // show SD line?
    private boolean regressButtons; // show regression-related buttons?
    private boolean showRegress; // show regression line?
    private boolean useAddedPoints; // recompute using points added by user?
    private boolean showGraphOfAve; // show graph of averages?
    private boolean graphAveButton; // show button for graph of averages?
    private boolean addPoints = false; // show the buttons relating to adding points?
    private boolean changeN = true; // allow n to change in normal case?
    private boolean isNormal;
    private boolean showSds;
    private boolean showResiduals;
    private boolean showRBar = true; // allow user to control r in the simulation
    private boolean showR = true; // display label for r
    private String  title = "Scatter, Correlation, and Regression";
    private double  xAxMin = 0;
    private double  yAxMin = 0;

    public void init() {
      super.init();
      setBackground(Color.white);
      for (int i = 0; i < buttonText.length; i++) {
          myButton[i] = new Button(buttonText[i]);
      }
      rLabel = new Label(" r:    ", Label.CENTER);

      sdButton = Format.strToBoolean(getParameter("sdButton"),true);
      regressButtons = Format.strToBoolean(getParameter("regressButtons"),true);
      graphAveButton = Format.strToBoolean(getParameter("graphAveButton"),true);
      addPoints = Format.strToBoolean(getParameter("addPoints"),true);
      sdLineButton = Format.strToBoolean(getParameter("sdLineButton"),true);
      showRBar = Format.strToBoolean(getParameter("showRBar"), true);
      showR = Format.strToBoolean(getParameter("showR"), true);
      if (!showRBar || !showR) {
          showRBar = false;
          showR = false;
      }
      showSds = false;
      showSdLine = Format.strToBoolean(getParameter("showSdLine"),false);
      showRegress = Format.strToBoolean(getParameter("showRegress"),false);
      useAddedPoints = Format.strToBoolean(getParameter("useAddedPoints"),false);
      nAdded = 0;
      showGraphOfAve = Format.strToBoolean(getParameter("showGraphOfAve"),false);
      showResiduals = Format.strToBoolean(getParameter("showResiduals"),false);
      graphOfAvePoints = Format.strToInt(getParameter("graphOfAvePoints"),11);
      xPointOfAve = new double[graphOfAvePoints+1];
      yPointOfAve = new double[graphOfAvePoints+1];
      nPointOfAve = new int[graphOfAvePoints];

      pCenter = new Panel();
      pCenter.setLayout(new BorderLayout());
      scat = new ScatterPlot();
      pCenter.add("Center",scat);

      pSouth = new Panel();
      pSouthBot = new Panel();
      pSouthBot.setLayout(new FlowLayout());
      pSouthTop = new Panel();
      pSouthBot.setLayout(new FlowLayout());

      if (getParameter("x") == null) {
          isNormal = true;
          if (getParameter("title") != null) {
              title=getParameter("title");
          }
          n = Format.strToInt(getParameter("n"), 50);
          changeN = Format.strToBoolean(getParameter("changeN"),true);
          r = Format.strToDouble(getParameter("r"),0.0);
          scat.setTics(0, 10, 1, 0, 10, 1);
          cNormPoints(n, r);
          setRLabel();
          rBar = new TextBar(0, -1, 1, 5, 2, false,
                            (Validator) new DoubleValidator(),
                             TextBar.TEXT_TOP,"r:");
          nBar = new TextBar(n, 2, 200, 5, 0, false,
                            (Validator) new IntegerValidator(),
                            TextBar.TEXT_TOP,"n:");
          if (showRBar) {
              showR = false;
              pCenterBot = new Panel();
              pCenterBot.setLayout(new FlowLayout());
              pCenterBot.add(rBar);
              pCenterBot.add(nBar);
              pCenter.add("South",pCenterBot);
          }
      } else { // data are given; don't allow n and r to change
          if (getParameter("title") != null) {
              title=getParameter("title");
          } else {
            title="Untitled Scatterplot";
          }
          if (getParameter("graphOfAvePoints") != null) {
              graphOfAvePoints = Format.strToInt(getParameter("graphOfAvePoints"));
              xPointOfAve = new double[graphOfAvePoints+1];
              yPointOfAve = new double[graphOfAvePoints+1];
              nPointOfAve = new int[graphOfAvePoints];
          }

          setVariables(getParameter("x"),getParameter("y"));
          if (showR) pSouthTop.add(rLabel);
      }

      if (sdButton) pSouthTop.add(myButton[0]);
      if (sdLineButton) {
          pSouthTop.add(myButton[1]);
          if (showSdLine) {
              myButton[1].setLabel("No SD Line");
          }
      }
      if (graphAveButton) {
          pSouthTop.add(myButton[2]);
          if (showGraphOfAve) myButton[2].setLabel("No Graph of Ave");
      }
      if (regressButtons) {
          pSouthTop.add(myButton[3]);
          if (showRegress) myButton[3].setLabel("No Regression Line");
          pSouthBot.add(myButton[4]);
          if (showResiduals) myButton[4].setLabel("   Plot Data   ");
      }
      if (addPoints) {
          pSouthBot.add(myButton[5]);
          if (useAddedPoints) myButton[5].setLabel("Ignore Added Points");
          pSouthBot.add(myButton[6]);
      }
      boolean needTop = sdButton|| sdLineButton || graphAveButton || regressButtons || showR;
      boolean needBot = regressButtons || addPoints;
      if (needTop && needBot) {
          pSouth.setLayout(new GridLayout(2,1));
      } else {
        pSouth.setLayout(new FlowLayout(FlowLayout.CENTER));
      }
      if (needTop) { pSouth.add(pSouthTop);}
      if (needBot) {pSouth.add(pSouthBot);}

      titleLabel = new Label(title);
      nPanel = new Panel();
      nPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
      nPanel.setFont(new Font("TimesRoman", Font.BOLD, 18));
      nPanel.add(titleLabel);

      setLayout(new BorderLayout());
      add("Center", pCenter);
      add("South", pSouth);
      add("North",nPanel);
      validate();
      setRLabel();
      showPlot();

    }

    public boolean handleEvent(Event e) {
        if (e.id == Event.WINDOW_DESTROY) {
            System.exit(0);
        } else if (e.id == Event.SCROLL_ABSOLUTE
                 || e.id == Event.SCROLL_LINE_DOWN
                 || e.id == Event.SCROLL_LINE_UP
                 || e.id == Event.SCROLL_PAGE_DOWN
                 || e.id == Event.SCROLL_PAGE_UP) {
            if (isNormal) {
                if (changeN) {
                    n = (int) nBar.getValue();
                    nAdded = 0;
                } else {
                    nBar.setValue(n);
                }
                r = rBar.getValue();
                cNormPoints(n, r);
                rBar.setValue(rHat);
                showPlot();
                return true;
            } else {}
        } else if (e.id == Event.ACTION_EVENT) {
            if (e.arg == "  SD Line ") {
                showSdLine = !showSdLine;
                ( (Button) e.target).setLabel("No SD Line");
                showPlot();
                return true;
            } else if (e.arg == "No SD Line") {
                showSdLine = !showSdLine;
                ( (Button) e.target).setLabel("  SD Line ");
                showPlot();
                return(true);
            } else if (e.arg == "  Regression Line ") {
                showRegress = !showRegress;
                ( (Button) e.target).setLabel("No Regression Line");
                showPlot();
                return(true);
            } else if (e.arg == "No Regression Line") {
                showRegress = !showRegress;
                ( (Button) e.target).setLabel("  Regression Line ");
                showPlot();
                return(true);
            } else if (e.arg == "  SDs ") {
                showSds = !showSds;
                ( (Button) e.target).setLabel("No SDs");
                showPlot();
                return(true);
            } else if (e.arg == "No SDs") {
                showSds = !showSds;
                ( (Button) e.target).setLabel("  SDs ");
                showPlot();
                return(true);
            } else if (e.arg == "  Use Added Points  ") {
                useAddedPoints = !useAddedPoints;
                ( (Button) e.target).setLabel("Ignore Added Points");
                compAll();
                setRLabel();
                if (isNormal) {
                    rBar.setValue(rHat);
                    nBar.setValue(n-nAdded);
                }
                showPlot();
                return(true);
            } else if (e.arg == "Ignore Added Points") {
                useAddedPoints = !useAddedPoints;
                ( (Button) e.target).setLabel("  Use Added Points  ");
                compAll();
                setRLabel();
                if (isNormal) {
                    rBar.setValue(rHat);
                    nBar.setValue(n);
                }
                showPlot();
                return(true);
            } else if (e.arg == "Clear Added Points") {
                n = n - nAdded;
                nAdded = 0;
                double[] xx = xVal;
                double[] yy = yVal;
                xVal = new double[n];
                yVal = new double[n];
                System.arraycopy(xx, 0, xVal, 0, n);
                System.arraycopy(yy, 0, yVal, 0, n);
                compAll();
// reset the relevant labels.
                setRLabel();
                if (isNormal && useAddedPoints) {
                    rBar.setValue(rHat);
                    nBar.setValue(n);
                }
                validate();
                showPlot();
                return(true);
            } else if (e.arg == "  Graph of Ave " ) {
                showGraphOfAve = !showGraphOfAve;
                ( (Button) e.target).setLabel("No Graph of Ave");
                showPlot();
                return(true);
            } else if (e.arg == "No Graph of Ave") {
                showGraphOfAve = !showGraphOfAve;
                ( (Button) e.target).setLabel("  Graph of Ave ");
                showPlot();
                return(true);
            } else if (e.arg == "Plot Residuals") {
                showResiduals = !showResiduals;
                ( (Button) e.target).setLabel("   Plot Data   ");
                if (isNormal) scat.setTics(0, 10, 1, -5, 5, 1);
                compAll();
                showPlot();
                return(true);
            } else if (e.arg == "   Plot Data   ") {
                showResiduals = !showResiduals;
                ( (Button) e.target).setLabel("Plot Residuals");
                if (isNormal) scat.setTics(0, 10, 1, 0, 10, 1);
                compAll();
                showPlot();
                return(true);
            }
        } else if (e.id == Event.MOUSE_DOWN && e.target == scat && addPoints) {
            n++;
            nAdded++;
            double[] xVal2 = new double[n];
            double[] yVal2 = new double[n];
            xVal2[n-1] = scat.pixToX(e.x - scat.location().x -
                                     pCenter.location().x);
            yVal2[n-1] = scat.pixToY(e.y - scat.location().y -
                                     pCenter.location().y);
            for (int i = 0; i < n-1; i++) {
                xVal2[i] = xVal[i];
                yVal2[i] = yVal[i];
            }
            xVal = new double[n];
            yVal = new double[n];
            xVal = xVal2;
            yVal = yVal2;

            compAll();
            rLabel.setText(" r: " + Format.numToString(rHat,4,2,true));
            if (isNormal && useAddedPoints) {
                rBar.setValue(rHat);
                nBar.setValue(n);
            }
            showPlot();
            return(true);
        }
        return(super.handleEvent(e));
    } // ends handleEvent

    public void setN(String n) {
        try {
            this.n = (new Integer(n)).intValue();
            setN(n);
        } catch (NumberFormatException e) {
        }
    }

    public void setN(int n) {
        if (isNormal) {
            this.n = n;
            nBar.setValue(n);
            cNormPoints(n,r);
            showPlot();
        }
    }

    public double getR() {
        return(rHat);
    }

    public void setR(String r) {
        try {
            rHat = (new Double(r)).doubleValue();
            setR(rHat);
        } catch (NumberFormatException e) {
        }
    }

    public void setR(double r) {
        if (isNormal) {
            this.r = r;
            cNormPoints(n, r);
            rBar.setValue(rHat);
            setRLabel();
            showPlot();
        }
    }

    private void setRLabel() {
        String s = "" + rHat;
        if (rHat < 0)  s = s.substring(0,Math.min(5,s.length()));
        else s = " " + s.substring(0,Math.min(4,s.length()));
        rLabel.setText(" r: " + s);
    }

    public void showPlot() {
        if (!showGraphOfAve) {
            double[] xm = new double[1];
            double[] ym = new double[1];
            xm[0] = xMean;
            ym[0] = yMean;

            if (showSdLine && showRegress && !showResiduals) {
                scat.redraw(xVal, yToPlot, true, true, xm, ym,
                            showSds, sdLineX, sdLineY, regressLineX,
                            regressLineY, nAdded);
            } else if (showSdLine && !showRegress && !showResiduals) {
                scat.redraw(xVal, yToPlot, true, true, xm, ym,
                            showSds, sdLineX, sdLineY, nAdded);
            } else if (!showSdLine && showRegress) {
                scat.redraw(xVal, yToPlot, true, true, xm, ym,
                            showSds, regressLineX, regressLineY, nAdded);
            } else {
                scat.redraw(xVal, yToPlot, true, true, xm, ym,
                            showSds, nAdded);
            }
        } else {
            if (showSdLine && showRegress && !showResiduals) {
                scat.redraw(xVal, yToPlot, true, true, xPointOfAve,
                            yPointOfAve, showSds, sdLineX, sdLineY,
                            regressLineX, regressLineY, nAdded);
            } else if (showSdLine && !showRegress && !showResiduals) {
                scat.redraw(xVal, yToPlot, true, true, xPointOfAve,
                            yPointOfAve, showSds, sdLineX, sdLineY,
                            nAdded);
            } else if (!showSdLine && showRegress ) {
                scat.redraw(xVal, yToPlot, true, true, xPointOfAve,
                            yPointOfAve, showSds, regressLineX,
                            regressLineY, nAdded);
            } else {
                scat.redraw(xVal, yToPlot, true, true, xPointOfAve,
                            yPointOfAve, showSds, nAdded);
            }
        }

    } // ends showPlot()


    void cNormPoints(int n, double r) {
  // generate pseudorandom normal bivariate w/ specified realized correlation coefficient
        xVal = new double[n];
        yVal = new double[n];
        for ( int i=0; i < n ; i++ ) {
            xVal[i]= PbsStat.rNorm();
            yVal[i] = PbsStat.rNorm();
        }
        double rAtt = PbsStat.CorrCoef(xVal, yVal);
        double s = PbsStat.sgn(rAtt)*PbsStat.sgn(r);
        double xBarAtt = PbsStat.Mean(xVal);
        double yBarAtt = PbsStat.Mean(yVal);
        double xSdAtt = PbsStat.Sd(xVal);
        double ySdAtt = PbsStat.Sd(yVal);
        double[] pred = new double[n];
        double[] resid = new double[n];
        for ( int i=0; i < n; i++) {
            xVal[i] = (xVal[i] - xBarAtt)/xSdAtt;
            pred[i] = s*rAtt*xVal[i]*ySdAtt+ yBarAtt;
            resid[i] = s*yVal[i] - pred[i];
        }
        double resNrm = PbsStat.Rms(resid);
        for (int i = 0; i < n; i++) {
            yVal[i] = Math.sqrt(1-r*r)*resid[i]/resNrm + r*xVal[i];
        }
        double[] ymnmx = PbsStat.vMinMax(yVal);
        double[] xmnmx = PbsStat.vMinMax(xVal);
        double xscl = 8.5/(xmnmx[1] - xmnmx[0]);
        double yscl = 8.5/(ymnmx[1] - ymnmx[0]);
        for (int i=0; i < n; i++) {
            xVal[i] = (xVal[i] - xmnmx[0]) * xscl  + 1.0;
            yVal[i] = (yVal[i] - ymnmx[0]) * yscl + 1.0;
        }
        compAll();

    } // ends cNormPoints

    void cTargetNormPoints(int n, double r) {
  // generate pseudorandom normal bivariate w/ specified target correlation coefficient
        xVal = new double[n];
        yVal = new double[n];
        for ( int i=0; i<n ; i++ ) {
            xVal[i]= PbsStat.rNorm();
            yVal[i] = r*xVal[i] + Math.sqrt(1-r*r)*PbsStat.rNorm();
        }
        compAll();
    }// ends cTargetNormPoints

    void compAll() {
        yToPlot = new double[n];
        xMean = 0;
        double xMax = 0;
        double xMin = 0;
        yMean = 0;
        double yMax = 0;
        double yMin = 0;
        for (int i = 0; i < graphOfAvePoints; i++) {
            xPointOfAve[i] = 0;
            yPointOfAve[i] = 0;
            nPointOfAve[i] = 0;
        }
        sdX = 0;
        sdY = 0;
        nToUse = n;
        if (!useAddedPoints ) nToUse = n - nAdded;
        xMean = PbsStat.Mean(xVal,nToUse);
        yMean = PbsStat.Mean(yVal,nToUse);
        double[] extremes = new double[2];
        extremes = PbsStat.vMinMax(xVal,nToUse);
        xMin = extremes[0];
        xMax = extremes[1];
        extremes = PbsStat.vMinMax(yVal,nToUse);
        yMin = extremes[0];
        yMax = extremes[1];
        rHat = PbsStat.CorrCoef(xVal, yVal, nToUse);
        sdX = PbsStat.Sd(xVal,nToUse);
        sdY = PbsStat.Sd(yVal,nToUse);

        double dx = (xMax - xMin)/(graphOfAvePoints);
        xPointOfAve[graphOfAvePoints-1] = xMax - dx/2;
        for (int i = 0; i < nToUse; i++) {
            for (int j = 0; j < graphOfAvePoints - 1; j++) {
                xPointOfAve[j] = xMin + dx/2 + j*dx;
                if (xVal[i] >=  xPointOfAve[j] - dx/2 && xVal[i] < xPointOfAve[j] + dx/2) {
                    nPointOfAve[j]++;
                    yPointOfAve[j] += yVal[i];
                }
            }
            if ( xVal[i] >= xMax - dx) {
                nPointOfAve[graphOfAvePoints -1]++;
                yPointOfAve[graphOfAvePoints -1] += yVal[i];
            }
        }

        for (int i = 0; i < graphOfAvePoints; i++) {
            if (nPointOfAve[i] != 0) yPointOfAve[i] /= nPointOfAve[i];
        }

        xPointOfAve[graphOfAvePoints] = xMean;
        yPointOfAve[graphOfAvePoints] = yMean;

        sdLineX = new double[2];
        sdLineY = new double[2];
        regressLineX = new double[2];
        regressLineY = new double[2];

        if (rHat >= 0) {
            double k= Math.min( (xMean-xMin)/sdX, (yMean-yMin)/sdY );
            sdLineX[0] = xMean - k*sdX;
            sdLineY[0] = yMean - k*sdY;
            k = Math.min( (xMax - xMean)/sdX, (yMax - yMean)/sdY );
            sdLineX[1] = xMean + k*sdX;
            sdLineY[1] = yMean + k*sdY;
            k = Math.min( (xMean - xMin)/sdX, (yMean - yMin)/(rHat*sdY) );
            regressLineX[0]= xMean - k*sdX;
            regressLineY[0] = yMean - k*rHat*sdY;
            k = Math.min( (xMax - xMean)/sdX, (yMax - yMean)/(rHat*sdY) );
            regressLineX[1]= xMean + k*sdX;
            regressLineY[1] = yMean + k*rHat*sdY;
        } else {
            double k= Math.min( (xMean-xMin)/sdX, (yMax-yMean)/sdY );
            sdLineX[0] = xMean - k*sdX;
            sdLineY[0] = yMean + k*sdY;
            k = Math.min( (xMax - xMean)/sdX, (yMean - yMin)/sdY );
            sdLineX[1] = xMean + k*sdX;
            sdLineY[1] = yMean - k*sdY;
            k = Math.min( (xMean - xMin)/sdX, (yMax - yMean)/Math.abs(rHat*sdY) );
            regressLineX[0]= xMean - k*sdX;
            regressLineY[0] = yMean - k*rHat*sdY;
            k = Math.min( (xMax - xMean)/sdX, (yMean - yMin)/Math.abs(rHat*sdY) );
            regressLineX[1]= xMean + k*sdX;
            regressLineY[1] = yMean + k*rHat*sdY;
        }

        if (showResiduals) {
            for (int i = 0; i < n; i++) {
                yToPlot[i] = yVal[i] - (yMean + rHat * sdY/sdX *
                                 (xVal[i] - xMean));
            }
            for (int i = 0; i < graphOfAvePoints + 1; i++) {
                yPointOfAve[i] = yPointOfAve[i] -
                                (yMean + rHat * sdY/sdX *
                                (xPointOfAve[i] - xMean));
            }
            yMean = 0;
            regressLineY[0] = 0;
            regressLineY[1] = 0;
        } else {
            yToPlot = yVal;
        }

    }// ends compAll

   public boolean setVariables(String xStr, String yStr) { // sets xVal and yVal
          StringTokenizer st = new StringTokenizer(xStr,"\n\t, ",false);
          Vector datVec = new Vector(1);
          for (int j=0; st.hasMoreTokens(); j++) {
             datVec.addElement(new Double(Format.strToDouble(st.nextToken())));
          }
          n = datVec.size();
          xVal = new double[n];
          for (int j=0; j <n; j++) {
            xVal[j] = ((Double) datVec.elementAt(j)).doubleValue();
          }
          datVec = new Vector(1);
          st = new StringTokenizer(yStr,"\n\t, ",false);
          for (int j=0; st.hasMoreTokens(); j++) {
              datVec.addElement(new Double(Format.strToDouble(st.nextToken())));
          }
          int nOfy = datVec.size();
          if (nOfy != n) {
               System.out.println("lengths of x and y do not match");
               return(false);
          }
          yVal = new double[n];
          for (int j=0; j < n; j++) {
            yVal[j] = ((Double) datVec.elementAt(j)).doubleValue();
          }
          isNormal = false;
          showRBar = false;
          compAll();
          setRLabel();
          showPlot();
          return(true);
   }



} // ends applet Correlation


