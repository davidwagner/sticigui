/**
package SticiGui;

public class ScatHist extends Applet
@author P.B. Stark http://statistics.berkeley.edu/~stark
@version 0.9
@copyright 1997.  All rights reserved.

An applet for studying variability in slices through a scatterplot;
Combines a scatterplot with a histogram.

Uses background loading of datasets with the multithreaded data
     server DataManager written by D. Temple Lang and P.B. Stark.


**/
//package SticiGui;

import PbsGui.*;
import PbsStat.*;
import DataManager.*;
import java.awt.*;
import java.applet.*;
import java.net.*;
import java.io.*;
import java.util.*;



public class ScatHist extends Applet
{
    private final Label xLabel = new Label("  X variable:");
    private final Label yLabel = new Label("  Y variable:");
    private Label posLabel;
    private Label titleLabel;
    private Label rLabel;
    private Choice[] xChoice;
    private Choice[] yChoice;
    private Choice[] rCHoice;

    protected Choice datasetChoice = new Choice();
    DataSet currentDataset;
    private String[] fileName =  {
                                   "cities.dat",
                                   "cities47.dat",
                                   "gmat.dat",
                                   "ccv.dat"
                                 };

    private String[] buttonText = { "Show SDs", "Show SD Line",
                                    "  Graph of Ave  ",
                                    "Show Regression",
                                    "Plot Residuals",
                                    "   Use Added Points  ",
                                    "Clear Added Points"
                                  };

    Button[] myButton = new Button[buttonText.length];
    Panel pSouth;
    Panel pSouthTop;
    Panel pSouthBot;
    Panel pCenter;
    Panel pCenterWest;
    Panel pCenterEast;
    Panel pCenterTop;
    Panel pCenterBot;
    Panel pCenterBot1;
    Panel pCenterBot2;
    Panel nPanel; // will contain the title
    Panel xChoicePanel; // will have the choices of x in card layout
    Panel yChoicePanel; // will have the choices of y in card layout
    Panel rChoicePanel; // will have choices of restriction variable in card layout
    CardLayout xChoiceLayout;
    CardLayout yChoiceLayout;
    CardLayout rChoiceLayout;
    Checkbox lessBox = new Checkbox("values <=");
    Checkbox moreBox = new Checkbox("values >=");
    TextBar lessArea;
    TextBar moreArea;
    ScatterPlot scat; // the scatterplot
    Histogram hist; // the histogram

    private double[] xVal; // vector of x values
    private double[] yVal; // vector of y values
    private double[] rVal; // vector of data used to apply restrictions
    private double[] yrVal; // vector of restricted data
    private double[] yToPlot; // y values to plot (data or residuals)
    private double xMean; // mean of the x values
    private double yMean; // mean of the y values
    private double sdX; // sd of the x values
    private double sdY; // sd of the y values
    private double rMin; // minimum value of the restriction variable
    private double rMax; // maximum value of the restriction variable
    private double[] sdLineX; // x coordinates of the endpoints of SD line
    private double[] sdLineY; // y coordinates of endpoints of SD line
    private double[] regressLineX; // x coordinates of ends of regression line
    private double[] regressLineY; // y coordinates of ends of regression line
    private double[] xPointOfAve; // x coordinates of points in graph of averages
    private double[] yPointOfAve; // y coordinates of points in graph of averages
    private int[] nPointOfAve; // number of points in each bin in graph of aves
    private int graphOfAvePoints; // how mane points in the graph of averages
    private int n; // number of data, including added points
    private int nAdded; // number of points added by clicking
    private int nToUse; // number of points to use to calculate r, etc.
    private double rHat; // computed correlation coefficient

    private boolean showSds; // show SDs?
    private boolean showSdLine; // show SD line?
    private boolean showRegress; // show regression line?
//    private boolean useNewPoints; // recompute using points added by user?
    private boolean restrictLess; // apply upper bound restriction
    private boolean restrictMore; // apply lower bound restriction
    private boolean showGraphOfAve; // show graph of averages?
    private boolean showResiduals; // show data or residuals?
    protected String title = "   Scatter, Correlation, and Regression   ";
    DataManager dmgr;
    URL[] dataURL = null;
    static int X=0, Y=0;
    Hashtable xChoiceHash = new Hashtable(fileName.length);
    Hashtable yChoiceHash = new Hashtable(fileName.length);
    Hashtable rChoiceHash = new Hashtable(fileName.length);


    public void init()
    {
      super.init();
      int nFiles = 0;
      try
      {
          nFiles = Format.strToInt(getParameter("nFiles"));
      }
      catch (NullPointerException e)
      {
      }
      if (nFiles > 0)
      {
          fileName = new String[nFiles];
          for (int i = 0; i < nFiles; i++ )
          {
               fileName[i] = getParameter("file_"+(i+1));
          }
      }
      dataURL = new URL[fileName.length];
      RunnableDataSet[] myRunnableDataSet = new RunnableDataSet[fileName.length];
      for (int i = 0; i < fileName.length; i++) // form URLs from the names
      {
          try
          {
              dataURL[i] = new URL(getCodeBase(), fileName[i]);
          }
          catch (MalformedURLException e)
          {
              System.out.println(" Malformed URL " + fileName[i]);
          }
          myRunnableDataSet[i] = new RunnableDataSet(dataURL[i]);
      }
      RunnableDataSet[] firstSet = new RunnableDataSet[1];
      firstSet[0] =  myRunnableDataSet[0];
// start DataManager with the thread to load the first dataset.
      dmgr = new DataManager(firstSet, null, true,
                             DataManager.DefaultPriority);

      for (int i = 0; i < buttonText.length; i++)
      {
          myButton[i] = new Button(buttonText[i]);
      }
      rLabel = new Label(" r:    ", Label.CENTER);
      showSds = false;
      showSdLine = false;
      showRegress = false;
//      useNewPoints = false;
      nAdded = 0;
      showGraphOfAve = false;
      showResiduals = false;
      graphOfAvePoints = 9;
      xPointOfAve = new double[graphOfAvePoints+1];
      yPointOfAve = new double[graphOfAvePoints+1];
      nPointOfAve = new int[graphOfAvePoints];

      pCenter = new Panel();
      pCenter.setLayout(new BorderLayout());
      pCenterWest = new Panel();
      pCenterWest.setLayout(new BorderLayout());
      pCenterWest.add
      scat = new ScatterPlot();
      pCenterWest.add("Center",scat);
      pCenterEast = new Panel();
      pCenterEast.setLayout(new BorderLayout());
      hist = new Histogram();
      pCenterEast.add("Center", hist);
      pCenter.add("Center",pCenterWest);
      pCenter.add("Center",CenterWest);

      pCenterBot1 = new Panel();
      pCenterBot1.setLayout(new FlowLayout());

// set up the panel for the variables in each dataset
      xChoicePanel = new Panel();
      xChoiceLayout = new CardLayout();
      xChoicePanel.setLayout(xChoiceLayout);
      yChoicePanel = new Panel();
      yChoiceLayout = new CardLayout();
      yChoicePanel.setLayout(yChoiceLayout);
      rChoicePanel = new Panel();
      rChoiceLayout = new CardLayout();
      rChoicePanel.setLayout(rChoiceLayout);
      currentDataset = new DataSet();

      currentDataset = (DataSet) dmgr.getDataSet(dataURL[0].toString());
/* Add the variables for this data set */
      insertVariables(currentDataset);
// add the choices of dataset and variables

    lo = new TextBar(hiLiteLo, binEnd[0], binEnd[nBins], 5,
                     (Validator) new DoubleValidator(), TextBar.TEXT_TOP,
                     "Lower Boundary");
    hi = new TextBar(hiLiteHi, binEnd[0], binEnd[nBins], 5,
                     (Validator) new DoubleValidator(), TextBar.TEXT_TOP,
                     "Upper Boundary");
    bin = new TextBar(nBins, 1, data.length, 4,
                     (Validator) new IntegerValidator(),
                      TextBar.TEXT_TOP,"Bins:"));
    less = new TextBar(Vmax(rVal), Vmin(rval), Vmax(rval), 5,
                     (Validator) new DoubleValidator(),
                      TextBar.NO_BAR, null));
    more = new TextBar(0,
    }
    myPanel[1].add("South", myPanel[2]);


      String item;
      for(int i=0;i<fileName.length;i++)
      {
          item = fileName[i].substring(0,fileName[i].length()-4);
          datasetChoice.addItem(item);
      }
      pCenterBot1.add(datasetChoice);
      pCenterBot1.add(xLabel);
      pCenterBot1.add(xChoicePanel);
      pCenterBot1.add(yLabel);
      pCenterBot1.add(yChoicePanel);
      pCenter.add("South",pCenterBot1);

      pSouth = new Panel();
      pSouth.setLayout(new GridLayout(2,1));
      pSouthBot = new Panel();
      pSouthBot.setLayout(new FlowLayout());
      pSouthTop = new Panel();
      pSouthBot.setLayout(new FlowLayout());

/* Get the initial data.
*/
      xVal = getData(((Choice) xChoiceHash.get(currentDataset.name)).getSelectedItem());
      yVal = getData(((Choice) yChoiceHash.get(currentDataset.name)).getSelectedItem());
      rVal = getData(((Choice) rChoiceHash.get(currentDataset.name)).getSelectedItem());
      compAll();
      setRLabel();
      pSouthTop.add(rLabel);

      for (int i = 0; i < 4; i++)  pSouthTop.add(myButton[i]);
      for (int i = 4; i < 7; i++)  pSouthBot.add(myButton[i]);


      pSouthBot.add(posLabel = new Label("x=   0.00    y=   0.00    "));
      pSouth.add(pSouthTop);
      pSouth.add(pSouthBot);

      titleLabel = new Label(
                    ((Choice)yChoiceHash.get(currentDataset.name)).getSelectedItem()
                    + " vs. " +
                    ((Choice)xChoiceHash.get(currentDataset.name)).getSelectedItem());
      nPanel = new Panel();
      nPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
      nPanel.setFont(new Font("TimesRoman", Font.BOLD, 18));
      nPanel.add(titleLabel);

      setLayout(new BorderLayout());
      add("Center", pCenter);
      add("South", pSouth);
      add("North",nPanel);

      validate();

      scat.redraw(xVal, yToPlot, true);

      for(int i = 1; i < myRunnableDataSet.length; i++) // spawn threads to load other datasets
      {
          dmgr.addDataSet(myRunnableDataSet[i]);
      }

    } // ends init()

    public boolean insertVariables(DataSet ds)
    {
         String varName ="";

         if( !xChoiceHash.containsKey(ds.name))
         {
              Choice xc = new Choice();
              Choice yc = new Choice();
              Choice rc = new Choice();
              for (Enumeration e = ds.Var.keys(); e.hasMoreElements();)
              {
                  varName = (String) e.nextElement();
                  xc.addItem(varName);
                  yc.addItem(varName);
                  rc.addItem(varName);
              }

              xChoiceHash.put(ds.name, xc);
              yChoiceHash.put(ds.name, yc);
              rChoiceHash.put(ds.name, rc);
              xChoicePanel.add(ds.name, xc);
              xChoicePanel.validate();
              yChoicePanel.add(ds.name, yc);
              yChoicePanel.validate();
              rChoicePanel.add(ds.name, rc);
              rChoicePanel.validate();
              ((Choice) xChoiceHash.get(ds.name)).select(0);
              ((Choice) yChoiceHash.get(ds.name)).select(1);
              ((Choice) rChoiceHash.get(ds.name)).select(0);
              validate();
         }
         else
         {
              ((Choice) xChoiceHash.get(ds.name)).select(0);
              ((Choice) yChoiceHash.get(ds.name)).select(1);
              ((Choice) rChoiceHash.get(ds.name)).select(0);
         }

         return true;
    }

    public boolean handleEvent(Event e)
    {
        if (e.id == Event.WINDOW_DESTROY) System.exit(0);
        else if (e.id == Event.ACTION_EVENT)
        {
            if (e.target == xChoiceHash.get(currentDataset.name))
            {
                xVal = getData(((Choice) xChoiceHash.get(currentDataset.name)).getSelectedItem());
                reTitle();
                showPlot();
                return true;
            }
            else if (e.target == yChoiceHash.get(currentDataset.name))
            {
                yVal = getData(((Choice) yChoiceHash.get(currentDataset.name)).getSelectedItem());
                reTitle();
                showPlot();
                return true;
            }
            else if (e.target == rChoiceHasn.get(currentDataset.name))
            {
                rVal = getData(((Choice) rChoiceHash.get(currentDataset.name)).getSelectedItem());
                restrictY();
                showPlot();
            }
            else if (e.target.equals(datasetChoice))
            {
               int which = ((Choice)e.target).getSelectedIndex();
               currentDataset = (DataSet) dmgr.getDataSet(dataURL[which].toString());
               insertVariables(currentDataset);
               xChoiceLayout.show(xChoicePanel, currentDataset.name);
               yChoiceLayout.show(yChoicePanel, currentDataset.name);
               xVal = getData(((Choice) xChoiceHash.get(currentDataset.name)).getSelectedItem());
               yVal = getData(((Choice) yChoiceHash.get(currentDataset.name)).getSelectedItem());
               reTitle();
               showPlot();
            }
            else if (e.arg == "Show SD Line")
            {
                showSdLine = !showSdLine;
                ( (Button) e.target).setLabel("Hide SD Line");
                showPlot();
                return true;
            }
            else if (e.arg == "Hide SD Line")
            {
                showSdLine = !showSdLine;
                ( (Button) e.target).setLabel("Show SD Line");
                showPlot();
                return true;
            }
            else if (e.arg == "Show Regression")
            {
                showRegress = !showRegress;
                ( (Button) e.target).setLabel("Hide Regression");
                showPlot();
                return true;
            }
            else if (e.arg == "Hide Regression")
            {
                showRegress = !showRegress;
                ( (Button) e.target).setLabel("Show Regression");
                showPlot();
                return true;
            }
            else if (e.arg == "Show SDs")
            {
                showSds = !showSds;
                ( (Button) e.target).setLabel("Hide SDs");
                showPlot();
                return true;
            }
            else if (e.arg == "Hide SDs")
            {
                showSds = !showSds;
                ( (Button) e.target).setLabel("Show SDs");
                showPlot();
                return true;
            }
            else if (e.arg == "   Use Added Points  ")
            {
                useNewPoints = !useNewPoints;
                ( (Button) e.target).setLabel("Ignore Added Points");
                compAll();
                setRLabel();
                showPlot();
                return true;
            }
            else if (e.arg == "Ignore Added Points")
            {
                useNewPoints = !useNewPoints;
                ( (Button) e.target).setLabel("   Use Added Points  ");
                compAll();
                setRLabel();
                showPlot();
                return true;
            }
            else if (e.arg == "Clear Added Points" )
            {
                n = n - nAdded;
                nAdded = 0;
                double[] xx = xVal;
                double[] yy = yVal;
                xVal = new double[n];
                yVal = new double[n];
                System.arraycopy(xx, 0, xVal, 0, n);
                System.arraycopy(yy, 0, yVal, 0, n);
                compAll();
                setRLabel();
                showPlot();
                return true;
            }
            else if (e.arg == "  Graph of Ave  " )
            {
                showGraphOfAve = !showGraphOfAve;
                ( (Button) e.target).setLabel("No Graph of Ave");
                showPlot();
                return true;
            }
            else if (e.arg == "No Graph of Ave")
            {
                showGraphOfAve = !showGraphOfAve;
                ( (Button) e.target).setLabel("  Graph of Ave  ");
                showPlot();
                return true;
            }
            else if (e.arg == "Plot Residuals")
            {
                showResiduals = !showResiduals;
                ( (Button) e.target).setLabel("Plot Data");
                compAll();
                showPlot();
                return true;
            }
            else if (e.arg == "Plot Data")
            {
                showResiduals = !showResiduals;
                ( (Button) e.target).setLabel("Plot Residuals");
                compAll();
                showPlot();
                return true;
            }
        }
        else if (e.id == Event.MOUSE_MOVE && e.target == scat)
        {
            double xHere = scat.pixToX(e.x - scat.location().x -
                                       pCenter.location().x);
            double yHere = scat.pixToY(e.y - scat.location().y -
                                       pCenter.location().y);
            int dig = (int) (Math.floor( Math.log( Math.abs(xHere))
                             /Math.log( 10 )));
            if (dig < 1 ) dig = 1;
            String xLbl = "" + xHere;
            int nChars = xLbl.length();
            xLbl = xLbl.substring(0, Math.min(dig+4, nChars));
            dig = (int) (Math.floor( Math.log( Math.abs(yHere))
                             /Math.log( 10 )));
            if (dig < 1 ) dig = 1;
            String yLbl = "" + yHere;
            nChars = yLbl.length();
            yLbl = yLbl.substring(0, Math.min(dig+4, nChars));
            posLabel.setText("x= " + xLbl + "  y= " + yLbl);
            posLabel.validate();
            pSouthBot.validate();
            pSouth.validate();
            return true;
        }
        else if (e.id == Event.MOUSE_DOWN && e.target == scat)
        {
            n++;
            nAdded++;
            double[] xVal2 = new double[n];
            double[] yVal2 = new double[n];
            xVal2[n-1] = scat.pixToX(e.x - scat.location().x -
                                     pCenter.location().x);
            yVal2[n-1] = scat.pixToY(e.y - scat.location().y -
                                     pCenter.location().y);
            for (int i = 0; i < n-1; i++)
            {
                xVal2[i] = xVal[i];
                yVal2[i] = yVal[i];
            }
            xVal = new double[n];
            yVal = new double[n];
            xVal = xVal2;
            yVal = yVal2;
            compAll();
            String s = "" + rHat;
            if (rHat < 0)  s = s.substring(0,Math.min(5,s.length()));
            else s = s.substring(0,Math.min(4,s.length()));
            rLabel.setText(" r: " + s);
            showPlot();
            return true;
        }
        return super.handleEvent(e);
    } // ends handleEvent

    private double[] getData(String v)
    {
        double[] data;
        data = (currentDataset.getVariable(v)).data();
        n = data.length;
        return data;
    }

    private void reTitle()
    {
        nPanel.remove(titleLabel);
        titleLabel = new Label(
                    ((Choice)yChoiceHash.get(currentDataset.name)).getSelectedItem()
                    + " vs. " +
                    ((Choice)xChoiceHash.get(currentDataset.name)).getSelectedItem());
        nPanel.add(titleLabel);
        nPanel.validate();
        nPanel.repaint();
        compAll();
        setRLabel();
    }

    private void setRLabel()
    {
        String s = "" + rHat;
        if (rHat < 0)  s = s.substring(0,Math.min(5,s.length()));
        else s = " " + s.substring(0,Math.min(4,s.length()));
        rLabel.setText(" r: " + s);
    }

    private void showPlot()
    {
        if (xVal.length != yVal.length) return;
        if (!showGraphOfAve)
        {
            double[] xm = new double[1];
            double[] ym = new double[1];
            xm[0] = xMean;
            ym[0] = yMean;

            if (showSdLine && showRegress && !showResiduals)
            {
                scat.redraw(xVal, yToPlot, true, true, xm, ym,
                            showSds && !showResiduals, sdLineX,
                            sdLineY, regressLineX,
                            regressLineY, nAdded);
            }
            else if (showSdLine && !showRegress && !showResiduals)
            {
                scat.redraw(xVal, yToPlot, true, true, xm, ym,
                            showSds && !showResiduals, sdLineX,
                            sdLineY, nAdded);
            }
            else if (!showSdLine && showRegress)
            {
                scat.redraw(xVal, yToPlot, true, true, xm, ym,
                            showSds && !showResiduals, regressLineX,
                            regressLineY, nAdded);
            }
            else
            {
                scat.redraw(xVal, yToPlot, true, true, xm, ym,
                            showSds && !showResiduals, nAdded);
            }
        }
        else
        {
            if (showSdLine && showRegress && !showResiduals)
            {
                scat.redraw(xVal, yToPlot, true, true, xPointOfAve,
                            yPointOfAve, showSds, sdLineX, sdLineY,
                            regressLineX, regressLineY, nAdded);
            }
            else if (showSdLine && !showRegress && !showResiduals)
            {
                scat.redraw(xVal, yToPlot, true, true, xPointOfAve,
                            yPointOfAve, showSds, sdLineX, sdLineY,
                            nAdded);
            }
            else if (!showSdLine && showRegress )
            {
                scat.redraw(xVal, yToPlot, true, true, xPointOfAve,
                            yPointOfAve, showSds && !showResiduals,
                            regressLineX,
                            regressLineY, nAdded);
            }
            else
            {
                scat.redraw(xVal, yToPlot, true, true, xPointOfAve,
                            yPointOfAve, showSds && !showResiduals,
                            nAdded);
            }
        }

    } // ends showPlot()


    void cNormPoints(int n, double r)
    {
        xVal = new double[n];
        yVal = new double[n];
        for ( int i=0; i<n ; i++ )
        {
            xVal[i]= rNorm();
            yVal[i] = r*xVal[i] + Math.sqrt(1-r*r)*rNorm();
        }
        compAll();
    }// ends cNormPoints

    void compAll()
    {
        if (xVal.length != yVal.length) return;
        yToPlot = new double[n];
        xMean = PbsStat.Mean(xVal);
        yMean = PbsStat.Mean(yVal);
        rMean = PbsStat.Mean(rVal);
        sdX = PbsStat.Sd(xVal);
        sdY = PbsStat.Mean(yVal);
        sdR = PbsStat.Sd(rVal);
        xMin = PbsStat.vMin(xVal);
        yMin = PbsStat.vMin(yVal);
        rMin = PbsStat.vMin(rVal);
        xMax = PbsStat.vMax(xVal);
        yMax = PbsStat.vMax(yVal);
        rMax = PbsStat.vMax(rVal);
        for (int i = 0; i < graphOfAvePoints; i++)
        {
            xPointOfAve[i] = 0;
            yPointOfAve[i] = 0;
            nPointOfAve[i] = 0;
        }
        nToUse = n;
        if (!useNewPoints ) nToUse = n - nAdded;

        double dx = (xMax - xMin)/(graphOfAvePoints);
        xPointOfAve[graphOfAvePoints-1] = xMax - dx/2;
        for (int i = 0; i < nToUse; i++)
        {
            for (int j = 0; j < graphOfAvePoints - 1; j++)
            {
                xPointOfAve[j] = xMin + dx/2 + j*dx;
                if (xVal[i] >=  xPointOfAve[j] - dx/2 &&
                    xVal[i] <   xPointOfAve[j] + dx/2)
                {
                    nPointOfAve[j]++;
                    yPointOfAve[j] += yVal[i];
                }
            }
            if ( xVal[i] >= xMax - dx)
            {
                nPointOfAve[graphOfAvePoints -1]++;
                yPointOfAve[graphOfAvePoints -1] += yVal[i];
            }
        }

        for (int i = 0; i < graphOfAvePoints; i++)
        {
            if (nPointOfAve[i] != 0) yPointOfAve[i] /= nPointOfAve[i];
        }
        xPointOfAve[graphOfAvePoints] = xMean;
        yPointOfAve[graphOfAvePoints] = yMean;

        rHat = 0;
        for (int i = 0; i < nToUse; i++)
        {
            rHat += (xVal[i] - xMean)*(yVal[i] - yMean);
        }
        rHat = rHat/(sdX*sdY*nToUse);

        sdLineX = new double[2];
        sdLineY = new double[2];
        regressLineX = new double[2];
        regressLineY = new double[2];

        if (rHat >= 0)
        {
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
        }
        else
        {
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


        if (showResiduals)
        {
            for (int i = 0; i < n; i++)
            {
                yToPlot[i] = yVal[i] - (yMean + rHat * sdY/sdX *
                                 (xVal[i] - xMean));
            }
            for (int i = 0; i < graphOfAvePoints + 1; i++)
            {
                yPointOfAve[i] = yPointOfAve[i] -
                                (yMean + rHat * sdY/sdX *
                                (xPointOfAve[i] - xMean));
            }
            yMean = 0;
            regressLineY[0] = 0;
            regressLineY[1] = 0;
        }
        else
        {
            yToPlot = yVal;
        }

    }// ends compAll

    double rNorm()
    {
        double y = Math.sqrt(12) *
	      ((Math.random() + Math.random() + Math.random() + Math.random()
	       + Math.random() + Math.random() + Math.random() + Math.random()
	       + Math.random() + Math.random() + Math.random() + Math.random())
	        / 12.0 - 0.5);
	    return y;
	} // ends rNorm()


} // ends applet ScatHist



