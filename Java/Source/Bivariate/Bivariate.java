/** class Bivariate extends Applet
@author P.B. Stark http://statistics.berkeley.edu/~stark
@version 1.1
@copyright 1997-2003. P.B. Stark.  All rights reserved.

Last modified 9 September 2003.

An applet for common bivariate techniques:
     scatterplot, graph of averages, correlation,
     regression, sd line, residual plot.

Uses background loading of datasets with the multithreaded data
     server DataManager written by D. Temple Lang and P.B. Stark.


**/

import PbsGui.*;
import PbsStat.*;
import DataManager.*;
import java.awt.*;
import java.applet.*;
import java.net.*;
import java.io.*;
import java.util.*;


public class Bivariate extends Applet {
    private final Label xLabel = new Label("vs.",Label.CENTER);
    private final Label yLabel = new Label(" ");
    private Label datLabel = new Label("Data:",Label.RIGHT);
    private Label posLabel;
    private Label titleLabel;
    private Label corLabel;
    private final Label urlLabel = new Label("URL:",Label.RIGHT);
    private TextField urlField = new TextField(10);

    private Choice[] xChoice;
    private Choice[] yChoice;

    protected Choice datasetChoice = new Choice();
    DataSet currentDataset;
    private Vector fileName = new Vector(1);

    private String[] buttonText = { "  SDs ",
                                    "  SD Line ",
                                    "  Graph of Ave  ",
                                    "  Regression Line ",
                                    "Plot Residuals",
                                    "  Use Added Points  ",
                                    "Clear Added Points",
                                    "Univariate Stats",
                                    "List Data",
                                  };
    String text = null;
    String[][] list = new String[2][];

    Button[] myButton = new Button[buttonText.length];
    TextFrame statFrame = new TextFrame("Summary Statistics",20,30);
    int[] r = {1, 19};
    int[] c = {60, 60};
    ListFrame listFrame = new ListFrame("Data Listing", r, c, (Applet) this);
    Panel pSouth;
    Panel pSouthTop;
    Panel pSouthBot;
    Panel pCenter;
    Panel pCenterTop;
    Panel pCenterBot;
    Panel pCenterBot1;
    Panel pCenterBot2;
    Panel pCenterBot3;
    Panel nPanel; // will contain the title
    Panel xChoicePanel; // will have the choices of x in card layout
    Panel yChoicePanel; // will have the choices of y in card layout
    CardLayout xChoiceLayout;
    CardLayout yChoiceLayout;
    ScatterPlot scat; // the scatterplot
    Color[] colorVec; // array of colors for plotting symbols
    private Color normalColor = Color.blue;
    private Color addedColor = Color.green;
    private Color specialColor = Color.yellow;
    private int[] hiLitPoints; // indices of points to plot in specialColor.

    private double[] xVal; // vector of x values
    private double[] yVal; // vector of y values
    private double[] yToPlot; // y values to plot (data or residuals)
    private double xMean; // mean of the x values
    private double yMean; // mean of the y values
    private double sdX; // sd of the x values
    private double sdY; // sd of the y values
    private double[] sdLineX; // x coordinates of the endpoints of SD line
    private double[] sdLineY; // y coordinates of endpoints of SD line
    private double[] regressLineX; // x coordinates of ends of regression line
    private double[] regressLineY; // y coordinates of ends of regression line
    private double[] xPointOfAve; // x coordinates of points in graph of averages
    private double[] yPointOfAve; // y coordinates of points in graph of averages
    private int[] nPointOfAve; // number of points in each bin in graph of aves
    private int graphOfAvePoints = 9; // how many points in the graph of averages
    private int n; // number of data, including added points
    private int nAdded; // number of points added by clicking
    private int nToUse; // number of points to use to calculate r, etc.
    private double rHat; // computed correlation coefficient
    private int digits = 6; // number of digits in labels
    private int decimals = 2;
    private double[] vmnmx = new double[2]; // gets min and max of data
    private Component[] vComp; // for lists of components
    private int maxPosLblWidth; // maximum width of the position label so far.
    private int nFiles; // number of data files

    private boolean showRmsRes; // show rms residuals?
    private boolean showR;      // show the correlation coefficient?
    private boolean sdButton;   // show the SD button?
    private boolean showSds; // show SDs?
    private boolean sdLineButton; // show SD line button?
    private boolean showSdLine; // show SD line?
    private boolean regressButtons; // show regression-related buttons?
    private boolean residualsButton; // show the residual button?
    private boolean regressButton; // show button for the regression line itself?
    private boolean showRegress; // show regression line?
    private boolean useAddedPoints; // recompute using points added by user?
    private boolean showGraphOfAve; // show graph of averages?
    private boolean graphAveButton; // show button for graph of averages?
    private boolean showResiduals; // show data or residuals?
    private boolean addPoints = false; // show the buttons relating to adding points?
    private boolean listData = true; // provide button to list data?
    private boolean univariateStats = false; // provide button to show univariate stats?
    private boolean showMeter = true; // show the x,y coordinates of the mouse?
    private boolean urlBox = false; // show a box for the URL from which to load new data?

    protected String title = "   Scatter, Correlation, and Regression   ";
    DataManager dmgr = new DataManager();
    DataManager docMgr = new DataManager();
    Vector dataURL = new Vector(1);
    URL currentURL;
    Vector myRunnableDataSet = new Vector(1);
    Vector myRunnableDocument = new Vector(1);
    char commentChar='#';
    static int X=0, Y=0;
    Hashtable xChoiceHash = new Hashtable(2);
    Hashtable yChoiceHash = new Hashtable(2);
    protected Font labelFont = new Font("ComputerModern",Font.PLAIN, 10);
    protected FontMetrics labelFM = getFontMetrics(labelFont);


    public void init() {
      super.init();
      setBackground(Color.white);
      try {
           commentChar = getParameter("commentChar").trim().charAt(0);
      } catch (NullPointerException e) {
      }
      addPoints = Format.strToBoolean(getParameter("addPoints"),true);
      sdButton = Format.strToBoolean(getParameter("sdButton"),true);
      sdLineButton = Format.strToBoolean(getParameter("sdLineButton"),true);
      showR = Format.strToBoolean(getParameter("showR"),true);
      showRmsRes = Format.strToBoolean(getParameter("rmsResiduals"),false);
      listData = Format.strToBoolean(getParameter("listData"),true);
      univariateStats = Format.strToBoolean(getParameter("univariateStats"),true);
      regressButtons = Format.strToBoolean(getParameter("regressButtons"),true);
      regressButton = Format.strToBoolean(getParameter("regressButton"),false);
      residualsButton = Format.strToBoolean(getParameter("residualsButton"),false);
      graphAveButton = Format.strToBoolean(getParameter("graphAveButton"),true);
      urlBox = Format.strToBoolean(getParameter("urlBox"),false);
      if (regressButtons) {
          residualsButton = true;
          regressButton = true;
      }
      if (getParameter("files") != null) {
          StringTokenizer st = new StringTokenizer(getParameter("files"),"\n\t, ",false);
          for (int j=0; st.hasMoreTokens(); j++) {
             fileName.addElement(st.nextToken());
          }
          makeUrls();
          nFiles = fileName.size();
// start DataManager with the thread to load the first dataset.
          dmgr.addDataSet( (RunnableDataSet) myRunnableDataSet.elementAt(0),
                             DataManager.DefaultPriority);
      } else {
          urlBox = true;
      }
      for (int i = 0; i < buttonText.length; i++) {
          myButton[i] = new Button(buttonText[i]);
          myButton[i].setFont(labelFont);
      }
      corLabel = new Label(" r:    ", Label.RIGHT);
      corLabel.setFont(labelFont);
      showSds = Format.strToBoolean(getParameter("showSds"),false);
      showSdLine = Format.strToBoolean(getParameter("showSdLine"),false);;
      showRegress = Format.strToBoolean(getParameter("showRegress"),false);;
      useAddedPoints = Format.strToBoolean(getParameter("useAddedPoints"),false);;
      nAdded = 0;
      showGraphOfAve = Format.strToBoolean(getParameter("showGraphOfAve"),false);;
      showResiduals = Format.strToBoolean(getParameter("showResiduals"),false);;
      xPointOfAve = new double[graphOfAvePoints+1];
      yPointOfAve = new double[graphOfAvePoints+1];
      nPointOfAve = new int[graphOfAvePoints];

      pCenter = new Panel();
      pCenter.setLayout(new BorderLayout());
      scat = new ScatterPlot();
      pCenter.add("Center",scat);
      scat.setColors(normalColor, specialColor, addedColor);

      pCenterBot1 = new Panel();
      pCenterBot1.setLayout(new FlowLayout());

// set up the panel for the variables in each dataset
      xChoicePanel = new Panel();
      xChoiceLayout = new CardLayout();
      xChoicePanel.setLayout(xChoiceLayout);
      yChoicePanel = new Panel();
      yChoiceLayout = new CardLayout();
      yChoicePanel.setLayout(yChoiceLayout);
      currentDataset = new DataSet();
      currentURL = (URL) dataURL.elementAt(0);
      currentDataset = (DataSet) dmgr.getDataSet(currentURL.toString());
/* Add the variables for this data set */
      if (currentURL != null) {
          insertVariables(currentDataset, getParameter("Xinit"), getParameter("Yinit"));
    // add the choices of dataset and variables
          String item;
          for(int i=0; i<fileName.size(); i++) {
              item = (String) fileName.elementAt(i);
              if (item.lastIndexOf('.') > -1) item=item.substring(0,item.lastIndexOf('.'));
              datasetChoice.addItem(item);
          }
      }
      nPanel = new Panel();
      nPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
      nPanel.setFont(new Font("TimesRoman", Font.BOLD, 12));
      if (fileName.size() == 1 && !urlBox) {
          datLabel = new Label("Dataset: " + datasetChoice.getSelectedItem(),Label.CENTER);
          nPanel.add(datLabel);
      } else {
          if (urlBox) {
               nPanel.add(urlLabel);
               nPanel.add(urlField);
          }
          nPanel.add(datLabel);
          nPanel.add(datasetChoice);
      }
      nPanel.add(yLabel);
      nPanel.add(yChoicePanel);
      nPanel.add(xLabel);
      nPanel.add(xChoicePanel);

      pSouth = new Panel();
      boolean needTop = showR || sdButton || sdLineButton || regressButton || residualsButton;
      boolean needBot = listData || univariateStats || addPoints || showMeter;
      if (needTop && needBot) {
          pSouth.setLayout(new GridLayout(2,1));
      } else {
          pSouth.setLayout(new FlowLayout(FlowLayout.CENTER));
      }
      pSouthBot = new Panel();
      pSouthBot.setLayout(new FlowLayout());
      pSouthTop = new Panel();
      pSouthBot.setLayout(new FlowLayout());

/* Get the initial data.
*/
      if (fileName.size() > 0) {
           xVal = getData(((Choice) xChoiceHash.get(currentDataset.name)).getSelectedItem());
           yVal = getData(((Choice) yChoiceHash.get(currentDataset.name)).getSelectedItem());
           compAll();
           setRLabel();
      }
      if (showR) {
        pSouthTop.add(corLabel);
      }
      if (sdButton) {
        pSouthTop.add(myButton[0]);
      }
      if (showSds) {
        myButton[0].setLabel("No SDs");
      }
      if (sdLineButton) {
        pSouthTop.add(myButton[1]);
      }
      if (showSdLine) {
        myButton[1].setLabel("No SD Line");
      }
      if (graphAveButton) {
        pSouthTop.add(myButton[2]);
      }
      if (showGraphOfAve) {
        myButton[2].setLabel("No Graph of Ave");
      }
      if (regressButton) {
          pSouthTop.add(myButton[3]);
          if (showRegress) {
            myButton[3].setLabel("No Regression Line");
          }
      }
      if (residualsButton) {
          pSouthTop.add(myButton[4]);
          if (showResiduals) {
            myButton[4].setLabel("   Plot Data   ");
          }
      }
      if (listData) {
        pSouthBot.add(myButton[8]);
      }
      if (univariateStats) {
        pSouthBot.add(myButton[7]);
      }
      if (addPoints) {
          for (int i = 5; i < 7; i++)  {
            pSouthBot.add(myButton[i]);
          }
          if (useAddedPoints) {
            myButton[5].setLabel("Ignore Added Points");
          }
      }
      posLabel = new Label("x= 0.00  y= 0.00 ",Label.CENTER);
      posLabel.setFont(labelFont);
      maxPosLblWidth = labelFM.stringWidth(posLabel.getText());
      if (showMeter) {
        pSouthBot.add(posLabel);
      }
      if (needTop) {
        pSouth.add(pSouthTop);
      }
      if (needBot) {
        pSouth.add(pSouthBot);
      }

      setLayout(new BorderLayout());
      add("Center", pCenter);
      add("South", pSouth);
      add("North",nPanel);

      validate();
      colorVec = new Color[xVal.length];
      for (int i=0; i< colorVec.length; i++) {
        colorVec[i] = normalColor;
      }

      showPlot();
// set up the univariate statistics display
      statFrame.setBackground(Color.white);
      makeStats();
      statFrame.hide();
      listFrame.setBackground(Color.white);
      listFrame.setTitle("Listing of dataset: " + datasetChoice.getSelectedItem());
      listFrame.hide();

      for(int i = 1; i < myRunnableDataSet.size(); i++) { // spawn threads to load other datasets
          dmgr.addDataSet((RunnableDataSet) myRunnableDataSet.elementAt(i));
      }
      RunnableDocument[] rd = new RunnableDocument[myRunnableDocument.size()];
      for (int i=0; i < myRunnableDocument.size(); i++) {
            rd[i] = (RunnableDocument) myRunnableDocument.elementAt(i);
      }
      docMgr = new DataManager(rd, null, true, DataManager.DefaultPriority);// load the first dataset as text

    } // ends init()

    public boolean insertVariables(DataSet ds) {
        return(insertVariables(ds, null, null));
    }

    public boolean insertVariables(DataSet ds, String xName, String yName) {
        String varName ="";
        if (!xChoiceHash.containsKey(ds.name)) {
            Choice xc = new Choice();
            Choice yc = new Choice();
            Choice rc = new Choice();
            for (Enumeration e = ds.Var.keys(); e.hasMoreElements();) {
              varName = (String) e.nextElement();
              xc.addItem(varName);
              yc.addItem(varName);
            }
            xChoiceHash.put(ds.name, xc);
            yChoiceHash.put(ds.name, yc);
            xChoicePanel.add(ds.name, xc);
            xChoicePanel.validate();
            yChoicePanel.add(ds.name, yc);
            yChoicePanel.validate();
        }
        if (xName != null) {
            ((Choice) xChoiceHash.get(ds.name)).select(xName);
        } else {
            ((Choice) xChoiceHash.get(ds.name)).select(0);
        }
        if (yName != null) {
            ((Choice) yChoiceHash.get(ds.name)).select(yName);
        } else {
            if ( ((Choice) yChoiceHash.get(ds.name)).countItems() > 1) {
                ((Choice) yChoiceHash.get(ds.name)).select(1);
            } else {
                ((Choice) yChoiceHash.get(ds.name)).select(0);
            }
        }
        validate();
        return(true);
    }

    public boolean handleEvent(Event e) {
        if (e.id == Event.WINDOW_DESTROY) System.exit(0);
        if (e.id == Event.LIST_SELECT || e.id == Event.LIST_DESELECT) {
            if (e.target == listFrame && ((Integer) e.arg).intValue() == 1) {
                hiLitPoints = listFrame.list[1].getSelectedIndexes();
                showPlot();
                return(true);
            }
        }
        if (e.id == Event.ACTION_EVENT) {
            if (e.target == xChoiceHash.get(currentDataset.name)) {
                xVal = getData(((Choice) xChoiceHash.get(currentDataset.name)).getSelectedItem());
                reTitle();
                showPlot();
                return(true);
            } else if (e.target == yChoiceHash.get(currentDataset.name)) {
                yVal = getData(((Choice) yChoiceHash.get(currentDataset.name)).getSelectedItem());
                reTitle();
                showPlot();
                return(true);
            } else if (e.target.equals(datasetChoice)) {
                return(datasetChoiceChanged());
            } else if (e.target.equals(urlField)) {
                return(addDataSet(urlField.getText()));
            } else if (e.arg == "  SD Line ") {
                showSdLine = true;
                ( (Button) e.target).setLabel("No SD Line");
                showPlot();
                return(true);
            } else if (e.arg == "No SD Line") {
                showSdLine = false;
                ( (Button) e.target).setLabel("  SD Line ");
                showPlot();
                return(true);
            } else if (e.arg == "  Regression Line ") {
                showRegress = true;
                ( (Button) e.target).setLabel("No Regression Line");
                showPlot();
                return(true);
            } else if (e.arg == "No Regression Line") {
                showRegress = false;
                ( (Button) e.target).setLabel("  Regression Line ");
                showPlot();
                return(true);
            } else if (e.arg == "  SDs ") {
                showSds = true;
                ( (Button) e.target).setLabel("No SDs");
                showPlot();
                return(true);
            } else if (e.arg == "No SDs") {
                showSds = false;
                ( (Button) e.target).setLabel("  SDs ");
                showPlot();
                return(true);
            } else if (e.arg == "  Use Added Points  ") {
                useAddedPoints = true;
                ( (Button) e.target).setLabel("Ignore Added Points");
                compAll();
                setRLabel();
                showPlot();
                return(true);
            } else if (e.arg == "Ignore Added Points") {
                useAddedPoints = false;
                ( (Button) e.target).setLabel("  Use Added Points  ");
                compAll();
                setRLabel();
                showPlot();
                return(true);
            } else if (e.arg == "Clear Added Points" ) {
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
                return(true);
            } else if (e.arg == "  Graph of Ave  " ) {
                showGraphOfAve = true;
                ( (Button) e.target).setLabel("No Graph of Ave");
                showPlot();
                return(true);
            } else if (e.arg == "No Graph of Ave") {
                showGraphOfAve = false;
                ( (Button) e.target).setLabel("  Graph of Ave  ");
                showPlot();
                return(true);
            } else if (e.arg == "Plot Residuals") {
                showResiduals = true;
                ( (Button) e.target).setLabel("   Plot Data   ");
                compAll();
                showPlot();
                return(true);
            } else if (e.arg == "   Plot Data   ") {
                showResiduals = false;
                ( (Button) e.target).setLabel("Plot Residuals");
                compAll();
                showPlot();
                return(true);
            } else if (e.arg == "Univariate Stats") {
                return(toggleStats());
            } else if (e.arg == "List Data") {
                return(toggleList());
            }
        } else if (e.id == Event.MOUSE_MOVE && e.target == scat) {
            double xHere = scat.pixToX(e.x - scat.location().x -
                                       pCenter.location().x);
            double yHere = scat.pixToY(e.y - scat.location().y -
                                       pCenter.location().y);
            posLabel.setText("x= " + Format.numToString(xHere,digits,decimals,true) + "  y= " +
                    Format.numToString(yHere,digits,decimals,true));
            int newLblWidth = labelFM.stringWidth(posLabel.getText());
            if ( newLblWidth > maxPosLblWidth ) {
                maxPosLblWidth = newLblWidth;
                posLabel.invalidate();
                pSouthBot.invalidate();
                pSouth.validate();
            }
            return(true);
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
            String s = "" + rHat;
            if (rHat < 0)  s = s.substring(0,Math.min(5,s.length()));
            else s = s.substring(0,Math.min(4,s.length()));
            corLabel.setText(" r: " + s);
            showPlot();
            return(true);
        }
        return(super.handleEvent(e));
    } // ends handleEvent

    private boolean toggleStats() {
        if (statFrame.isShowing()) {
            statFrame.hide();
        } else {
            statFrame.show();
            statFrame.toFront();
            statFrame.sizeToText();
        }
        return(true);
    }

    private boolean toggleList() { // the List data button was pushed
        if (listFrame.isShowing()) {
            listFrame.hide();
        } else {
            text = ( (Document) docMgr.getDataSet(currentURL.toString()) ).contents;
            listFrame.list[0].clear();
            listFrame.list[0].addItem(text.substring(0, text.indexOf("\n")));
            listFrame.list[1].clear();
            int here = text.indexOf("\n");
            int there;
            while (here > -1) {
                there = text.indexOf("\n",here+1);
                if (there < 0) there = text.length();
                listFrame.list[1].addItem(text.substring(here+1,there));
                text = text.substring(here+1,text.length());
                here = text.indexOf("\n");
            }
            listFrame.show();
            listFrame.toFront();
            listFrame.sizeToText();
            if (hiLitPoints != null) {
                for (int i=0; i < hiLitPoints.length; i++) {
                    listFrame.list[1].select(hiLitPoints[i]);
                }
            }
        }
        return(true);
    }

    private boolean datasetChoiceChanged() { // the data set choice was changed
        int which = datasetChoice.getSelectedIndex();
        currentURL = (URL) dataURL.elementAt(which);
        currentDataset = (DataSet) dmgr.getDataSet(currentURL.toString());
        listFrame.setTitle("Listing of dataset: " + datasetChoice.getSelectedItem());
        if (listFrame.isShowing()) {
             text = ( (Document) docMgr.getDataSet(currentURL.toString()) ).contents;
             listFrame.list[0].clear();
             listFrame.list[0].addItem(text.substring(0, text.indexOf("\n")));
             listFrame.list[1].clear();
             int here = text.indexOf("\n");
             int there;
             while (here > -1) {
                there = text.indexOf("\n",here+1);
                if (there < 0) there = text.length();
                listFrame.list[1].addItem(text.substring(here+1,there));
                text = text.substring(here+1,text.length());
                here = text.indexOf("\n");
             }
             listFrame.sizeToText();
        }
        insertVariables(currentDataset);
        xChoiceLayout.show(xChoicePanel, currentDataset.name);
        yChoiceLayout.show(yChoicePanel, currentDataset.name);
        xVal = getData(((Choice) xChoiceHash.get(currentDataset.name)).getSelectedItem());
        yVal = getData(((Choice) yChoiceHash.get(currentDataset.name)).getSelectedItem());
        reTitle();
        hiLitPoints = null;
        showPlot();
        makeStats();
        return(true);
    } // ends datasetChoiceChanged()


    private double[] getData(String v) {
        double[] data;
        data = (currentDataset.getVariable(v)).data();
        n = data.length;
        return data;
    }

    public void setXY(String xName, String yName) {
        try {
            xVal = getData(xName);
            yVal = getData(yName);
            ((Choice) xChoiceHash.get(currentDataset.name)).select(xName);
            ((Choice) xChoiceHash.get(currentDataset.name)).invalidate();
            ((Choice) yChoiceHash.get(currentDataset.name)).select(yName);
            ((Choice) yChoiceHash.get(currentDataset.name)).invalidate();
            validate();
            reTitle();
            hiLitPoints = null;
            showPlot();
        } catch (Exception e) {
        }
    }

    private void reTitle() {
        compAll();
        setRLabel();
    }

    private void setRLabel() {
        corLabel.setText(" r: " + Format.numToString(rHat, digits, decimals, true));
    }

    private void showPlot() {
        if (xVal.length != yVal.length) return;
        colorVec = new Color[xVal.length];
        for (int i=0; i < xVal.length-nAdded; i++) {
            colorVec[i] = normalColor;
        }
        for (int i = xVal.length-nAdded; i < xVal.length; i++) {
            colorVec[i] = addedColor;
        }
        if (hiLitPoints != null) {
            for (int i=0; i < hiLitPoints.length; i++) {
                colorVec[hiLitPoints[i]] = specialColor;
            }
        }
        if (!showGraphOfAve) {
            double[] xm = new double[1];
            double[] ym = new double[1];
            xm[0] = xMean;
            ym[0] = yMean;
            if (showSdLine && showRegress && !showResiduals) {
                scat.redraw(xVal, yToPlot, colorVec, true, true, xm, ym,
                            showSds && !showResiduals, sdLineX,
                            sdLineY, regressLineX,
                            regressLineY);
            } else if (showSdLine && !showRegress && !showResiduals) {
                scat.redraw(xVal, yToPlot, colorVec, true, true, xm, ym,
                            showSds && !showResiduals, sdLineX,
                            sdLineY);
            } else if (!showSdLine && showRegress) {
                scat.redraw(xVal, yToPlot, colorVec, true, true, xm, ym,
                            showSds && !showResiduals, regressLineX,
                            regressLineY);
            } else {
                scat.redraw(xVal, yToPlot, colorVec, true, true, xm, ym,
                            showSds && !showResiduals);
            }
        } else {
            if (showSdLine && showRegress && !showResiduals) {
                scat.redraw(xVal, yToPlot, colorVec, true, true, xPointOfAve,
                            yPointOfAve, showSds, sdLineX, sdLineY,
                            regressLineX, regressLineY);
            } else if (showSdLine && !showRegress && !showResiduals) {
                scat.redraw(xVal, yToPlot, colorVec, true, true, xPointOfAve,
                            yPointOfAve, showSds, sdLineX, sdLineY);
            } else if (!showSdLine && showRegress ) {
                scat.redraw(xVal, yToPlot, colorVec, true, true, xPointOfAve,
                            yPointOfAve, showSds && !showResiduals,
                            regressLineX,
                            regressLineY);
            } else {
                scat.redraw(xVal, yToPlot, colorVec, true, true, xPointOfAve,
                            yPointOfAve, showSds && !showResiduals);
            }
        }
    } // ends showPlot()

    public double getR() {
        compAll();
        return(rHat);
    }

    void cNormPoints(int n, double r) {
        xVal = new double[n];
        yVal = new double[n];
        for ( int i=0; i<n ; i++ ) {
            xVal[i]= PbsStat.rNorm();
            yVal[i] = r*xVal[i] + Math.sqrt(1-r*r)*PbsStat.rNorm();
        }
        compAll();
    }// ends cNormPoints

    void compAll() {
        if (xVal.length != yVal.length) {
            return;
        }
        yToPlot = new double[n];
        for (int i = 0; i < graphOfAvePoints; i++) {
            xPointOfAve[i] = 0;
            yPointOfAve[i] = 0;
            nPointOfAve[i] = 0;
        }
        nToUse = n;
        if (!useAddedPoints ) {
            nToUse = n - nAdded;
        }
        double[] xV = new double[nToUse];
        double[] yV = new double[nToUse];
        System.arraycopy(xVal,0,xV,0,nToUse);
        System.arraycopy(yVal,0,yV,0,nToUse);
        double[] xmnmx = PbsStat.vMinMax(xV);
        double[] ymnmx = PbsStat.vMinMax(yV);
        xMean = PbsStat.Mean(xV);
        yMean = PbsStat.Mean(yV);
        sdX = PbsStat.Sd(xV);
        sdY = PbsStat.Sd(yV);
        double dx = (xmnmx[1] - xmnmx[0])/(graphOfAvePoints);
        xPointOfAve[graphOfAvePoints-1] = xmnmx[1] - dx/2;
        for (int i = 0; i < nToUse; i++) {
            for (int j = 0; j < graphOfAvePoints - 1; j++) {
                xPointOfAve[j] = xmnmx[0] + dx/2 + j*dx;
                if (xVal[i] >=  xPointOfAve[j] - dx/2 &&
                    xVal[i] <   xPointOfAve[j] + dx/2)     {
                    nPointOfAve[j]++;
                    yPointOfAve[j] += yVal[i];
                }
            }
            if ( xVal[i] >= xmnmx[1] - dx) {
                nPointOfAve[graphOfAvePoints -1]++;
                yPointOfAve[graphOfAvePoints -1] += yVal[i];
            }
        }
        for (int i = 0; i < graphOfAvePoints; i++) {
            if (nPointOfAve[i] != 0) {
                yPointOfAve[i] /= nPointOfAve[i];
            }
        }
        xPointOfAve[graphOfAvePoints] = xMean;
        yPointOfAve[graphOfAvePoints] = yMean;
        rHat = PbsStat.CorrCoef(xV, yV);
        sdLineX = new double[2];
        sdLineY = new double[2];
        regressLineX = new double[2];
        regressLineY = new double[2];
        if (rHat >= 0) {
            double k= Math.min( (xMean-xmnmx[0])/sdX, (yMean-ymnmx[0])/sdY );
            sdLineX[0] = xMean - k*sdX;
            sdLineY[0] = yMean - k*sdY;
            k = Math.min( (xmnmx[1] - xMean)/sdX, (ymnmx[1] - yMean)/sdY );
            sdLineX[1] = xMean + k*sdX;
            sdLineY[1] = yMean + k*sdY;
            k = Math.min( (xMean - xmnmx[0])/sdX, (yMean - ymnmx[0])/(rHat*sdY) );
            regressLineX[0]= xMean - k*sdX;
            regressLineY[0] = yMean - k*rHat*sdY;
            k = Math.min( (xmnmx[1] - xMean)/sdX, (ymnmx[1] - yMean)/(rHat*sdY) );
            regressLineX[1]= xMean + k*sdX;
            regressLineY[1] = yMean + k*rHat*sdY;
        } else {
            double k= Math.min( (xMean-xmnmx[0])/sdX, (ymnmx[1]-yMean)/sdY );
            sdLineX[0] = xMean - k*sdX;
            sdLineY[0] = yMean + k*sdY;
            k = Math.min( (xmnmx[1] - xMean)/sdX, (yMean - ymnmx[0])/sdY );
            sdLineX[1] = xMean + k*sdX;
            sdLineY[1] = yMean - k*sdY;
            k = Math.min( (xMean - xmnmx[0])/sdX, (ymnmx[1] - yMean)/Math.abs(rHat*sdY) );
            regressLineX[0]= xMean - k*sdX;
            regressLineY[0] = yMean - k*rHat*sdY;
            k = Math.min( (xmnmx[1] - xMean)/sdX, (yMean - ymnmx[0])/Math.abs(rHat*sdY) );
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

    protected void makeStats() {  // make the univariate statistics of the variables in a new frame
        double[] data;
        double[] cleanData;
        double[] vm;
        double[] vq;
        String varName;
        statFrame.setText("");
        statFrame.setTitle("Summary of " + datasetChoice.getSelectedItem() + " data");
        for (Enumeration e = currentDataset.Var.keys(); e.hasMoreElements();) {
            varName = (String) e.nextElement();
            data = getData(varName);
            cleanData = PbsStat.removeNaN(data);
            vq = PbsStat.quartiles(cleanData);
            statFrame.appendText(varName + '\n');
            statFrame.appendText("  Cases " + cleanData.length + '\n');
            statFrame.appendText("  Mean " +
                Format.numToString(PbsStat.Mean(cleanData),digits, decimals, true)
                    + '\n' + "  SD " +
                    Format.numToString(PbsStat.Sd(cleanData),digits, decimals, true) + '\n');
            statFrame.appendText("  Min " + vq[0] + '\n' + "  LQ " + vq[1] + '\n' +
                    "  Median " + vq[2] + '\n' +
                    "  UQ " + vq[3] + '\n' + "  Max " + vq[4] + '\n' + '\n');
        }
        statFrame.validate();
    }

    public void destroy() {
        statFrame.dispose();
        listFrame.dispose();
    }

    private void makeUrls() { //
        URL u;
        URL cb = getCodeBase();
        RunnableDataSet rds;
        RunnableDocument rd;
        for (int i = 0; i < fileName.size(); i++) { // form URLs from the names
              u = Format.stringToURL((String) fileName.elementAt(i), cb);
              rds = new RunnableDataSet(u);
              rds.setCommentChar(commentChar);
              rd = new RunnableDocument(u);
              dataURL.addElement(u);
              myRunnableDataSet.addElement(rds);
              myRunnableDocument.addElement(rd);
        }
    }


   public boolean addDataSet(String dsName)
   { // public method to add a new dataset via its URL, given as a String
          URL u = Format.stringToURL(dsName, getCodeBase());
          RunnableDataSet rds;
          RunnableDocument rdmt;
          fileName.addElement(dsName);
          dataURL.addElement(u);
          rds = new RunnableDataSet(u);
          rdmt = new RunnableDocument(u);
          myRunnableDataSet.addElement(rds);
          myRunnableDocument.addElement(rdmt);
          dmgr.addDataSet(rds);
          docMgr.addDataSet(rdmt);
          String s = dsName.substring(dsName.lastIndexOf("/")+1,dsName.length()); // strip root
          if (s.lastIndexOf(".") > -1) {
            s = s.substring(0, s.lastIndexOf("."));
          }
          datasetChoice.addItem(s);
          datasetChoice.select(s);
          datasetChoiceChanged();
          nPanel.validate();
          nPanel.repaint();
          return(true);
   }

} // ends applet Bivariate



