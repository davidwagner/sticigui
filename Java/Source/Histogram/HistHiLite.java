/* public class HistHiLite extends Applet
@copyright 1997-2009 by P.B. Stark, stark AT stat.berkeley.edu. All rights reserved.

Applet to show a histogram of data with fixed or variable-width bins, or a binomial
probability histogram.
Incorporates the ability to "slice" multidimensional data sets.
Can load data from a URL or a file list.
@author P.B. Stark
@version 1.1 1/31/09
*/

import PbsStat.*;
import PbsGui.*;
import DataManager.*;

import java.awt.*;
import java.applet.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class HistHiLite extends Applet {
// for the selection of data sets and restriction variables
   private Label boxLabel; // for the URL TextField
   private final Label dataLabel = new Label(" Data:",Label.RIGHT);
   private final Label xLabel = new Label(" Variable:", Label.RIGHT);
   private final Label rLabel = new Label(" Restrict to ", Label.RIGHT);
   private final Label meanLabel = new Label(" Mean=", Label.RIGHT);
   private final Label cMeanLabel = new Label(" Mean= ", Label.RIGHT);
   private final Label sdLabel = new Label("SD=  ",Label.RIGHT);
   private final Label cSdLabel = new Label("SD=  ",Label.RIGHT);
   private final Label nLabel = new Label(" n=   ",Label.RIGHT);
   private final Label cNLabel = new Label(" Subset: n=    ",Label.RIGHT);
   private Choice[] xChoice;   // the array of choices of variables for each data set
   private Choice[] rChoice;   // array of choices of variables to restrict on
   protected Choice datasetChoice = new Choice(); // choice of data sets
   DataSet currentDataset;
   private char commentChar='#';
   CardLayout xChoiceLayout;
   CardLayout rChoiceLayout;
   CardLayout restrictLayout;
   FlowLayout titleLayout;
   DataManager dmgr;
   DataManager docMgr;
   Vector myRunnableDataSet = new Vector(1);
   Vector myRunnableDocument = new Vector(1);
   RunnableDocument[] mRdmt;
   Vector dataURL = new Vector(1);
   URL currentURL;
   Hashtable xChoiceHash;       // choices of variable to display
   Hashtable rChoiceHash;       // choices of variables on which to restrict
   Hashtable rApplyHash;        // restrictions on the variables
   Vector fileName = new Vector(1);
   TextField urlField = new TextField(12); // field to get URL from which to read new data
   private int nFiles;
   private int digits = 6;      // number of digits in TextBars
   private int decimals = 2;    // number of places to the right of the decimal in displays.

// generalities for the histogram
   private int nBins;
   private double[] count;  // the vector of bin heights
   private double[] cCount; // vector of bin heights for restricted variable
   private double[] binEnd; // the vector of endpoints of the bins
   private String title;    // title
   private double hiLiteLo;
   private double hiLiteHi;


// for the normal curve
   private boolean showNormal; // show the normal curve?
   private boolean normalControls; // show button to toggle normal?
   private boolean binControl; // control for # bins?
   private boolean restrict; // give option of restricting values?
   private boolean listData;    // option of listing data?
   private boolean univariateStats; // option of displaying univariate statistics?
   private boolean urlBox;      // provide a box into which to type a URL?
   private double mu; // mean of the data, calculated or given
   private double sd; // sd of the data, calculated or given
   private double cMu; // mean of data after restriction
   private double cSd; // sd of data after restriction
   private final int nx = 400; // number of points in the normal curve
   private double[] xVal = new double[nx]; // x points in normal curve
   private double[] yVal = new double[nx]; // y points in normal curve
   private double[] cYVal = new double[nx]; // y points in 2nd normal curve
   private double[] data; // data, if they are given
   private double[] cData; // data with restrictions applied
   private double[] rData; // values of variable to restrict on
   private double[] dMnMx; // min and max of data
   private double[] rMnMx; // min and max of rData


// for the binomial histogram
   private boolean binHist;
   private boolean binomialBars; // show scrollbars for binomial n and p?
   private int n;
   private double p;

   private Label areaLabel;
   private Label cAreaLabel;
   private Label normAreaLabel;
   private Label cNormAreaLabel;
   protected Label titleLabel;

   private Panel[] myPanel = new Panel[9];
   Panel xChoicePanel; // will have the choices of x in card layout
   Panel rChoicePanel; // choices of variables to restrict on
   Panel restrictPanel; // restrictions on those variables
   Checkbox showOriginal; // show histogram of the original data
   Checkbox showRestricted; // show histogram of the restricted data

// for the data listing
   int[] r = {1, 19};
   int[] c = {60, 60};
   String text;
   String[] list = new String[2];
   protected Font listFont = new Font("Courier",Font.PLAIN,12);
   TextFrame listFrame = new TextFrame("Data Listing", r, c, listFont);
   TextFrame statFrame = new TextFrame("Summary Statistics",20,30);
   protected Font titleFont = new Font("TimesRoman", Font.PLAIN, 12);
   protected FontMetrics titleFontMetrics = getFontMetrics(titleFont);
   protected Font labelFont = new Font("ComputerModern",Font.PLAIN, 10);


   TextBar bin;  // select the number of bins, if the raw data are given
   TextBar hi;   // select upper boundary of highlighted region
   TextBar lo;   // select lower boundary of highlighted region
   TextBar pBar; // select p for binomial histogram
   TextBar nBar; // select n for binomial histogram
   Button showNormalButton; // control whether or not to show the normal curve
   Button clearRestrict;    // clear all the restrictions
   Button listButton = new Button("List Data");   // list the data
   Button univStatsButton = new Button("Univariate Stats"); // show univariate statistics of the data
   Histogram hist; // the main thing


   public void init() {
      super.init();
      setBackground(Color.white);
      binHist = false;
      showNormal = Format.strToBoolean(getParameter("showNormal"),false);
      normalControls = Format.strToBoolean(getParameter("normalControls"),false);
      binControl = Format.strToBoolean(getParameter("binControl"),true);
      urlBox = Format.strToBoolean(getParameter("urlBox"),false);
      univariateStats = Format.strToBoolean(getParameter("univariateStats"),false);
      restrict = false;
      for (int i = 0; i < myPanel.length; i++) {
         myPanel[i] = new Panel();
         myPanel[i].setFont(labelFont);
      }
      if (getParameter("files") != null) {
          nFiles = 0;
          StringTokenizer st = new StringTokenizer(getParameter("files"),"\n\t, ",false);
          while (st.hasMoreTokens()) {
             fileName.addElement(st.nextToken());
             nFiles++;
          }
      }
      if (nFiles > 0) {
          restrict = Format.strToBoolean(getParameter("restrict"),false);
          listData = Format.strToBoolean(getParameter("listData"),true);
          xChoiceHash = new Hashtable(nFiles);
          makeUrls();
          currentURL = (URL) dataURL.elementAt(0);
          RunnableDataSet[] firstSet = new RunnableDataSet[1];
          firstSet[0] =  (RunnableDataSet) myRunnableDataSet.elementAt(0);
          try {
               commentChar = getParameter("commentChar").trim().charAt(0);
          }
          catch (NullPointerException e) {}
          firstSet[0].setCommentChar(commentChar);
// start DataManager with the thread to load the first dataset.
          dmgr = new DataManager(firstSet, null, true,
                                 DataManager.DefaultPriority);
// instantiate the panels needed to select the dataset, and restrictions, if allowed
          xChoicePanel = new Panel();
          xChoiceLayout = new CardLayout();
          xChoicePanel.setLayout(xChoiceLayout);
          if (restrict) {
               rChoiceHash = new Hashtable(nFiles);
               rChoicePanel = new Panel();
               rChoiceLayout = new CardLayout();
               rChoicePanel.setLayout(rChoiceLayout);
               rApplyHash = new Hashtable(5);
               restrictPanel = new Panel();
               restrictLayout = new CardLayout();
               restrictPanel.setLayout(restrictLayout);
               showOriginal = new Checkbox("Show original data");
               showRestricted = new Checkbox("Show restricted data");
               showOriginal.setState(true);
               showRestricted.setState(true);
          }
      }

      nBins = Format.strToInt(getParameter("bins"),10); // nBins must be given if counts are
      if (nFiles == 0 && getParameter("counts") == null) {
          binHist = true;
          n = Format.strToInt(getParameter("n"),2);
          p = Format.strToDouble(getParameter("p"),0.5);
          binomialBars = Format.strToBoolean("binomialBars",true);
          normalControls = Format.strToBoolean(getParameter("normalControls"),true);
          nBins = n+1;
      }
      count = new double[nBins];
      if (restrict) {
          cCount = new double[nBins];
      }
      binEnd = new double[nBins+1];
      title = getParameter("title");
      hiLiteLo = Format.strToDouble(getParameter("hiLiteLo"),0);
      hiLiteHi = Format.strToDouble(getParameter("hiLiteHi"),0);
      if (hiLiteLo > hiLiteHi) hiLiteLo = hiLiteHi;
      double hlh = hiLiteHi;
      double hll = hiLiteLo;
      if (nFiles == 0) {
         mu = Format.strToDouble(getParameter("mean"),0);
         sd = Format.strToDouble(getParameter("SD"),1);
      }
      if (getParameter("mean") != null && getParameter("SD") != null) {
        showNormal = true;
      }
      if (!binHist && nFiles == 0) {
      // there should either be counts or a data file out there!
         StringTokenizer st = new StringTokenizer(getParameter("counts"),"\n\t, ",false);
         for (int j=0; st.hasMoreTokens(); j++) {
             count[j] = Format.strToDouble(st.nextToken());
         }
         st = new StringTokenizer(getParameter("ends"),"\n\t, ",false);
         for (int j=0; st.hasMoreTokens(); j++) {
             binEnd[j] = Format.strToDouble(st.nextToken());
         }
         listData = false;
         univariateStats = false;
      } else if (binHist) {
         makeBin(); // caller wants a binomial; override sd&mu
         makePhi();
         if (title == null) title="Binomial Probability Histogram";
         listData = false;
         univariateStats = false;
      } else { // force the data to be read now; calculate the counts, etc.
         currentDataset = new DataSet();
         currentDataset = (DataSet) dmgr.getDataSet(((URL) dataURL.elementAt(0)).toString());
// Add the variables for this data set
         insertVariables(currentDataset);
// add the choices of dataset and variables
         String s;
         for(int i=0;i<fileName.size();i++) {
            s = (String) fileName.elementAt(i);
            s = s.substring(s.lastIndexOf("/")+1,s.length()); // strip root
            if (s.lastIndexOf(".") > -1) { s = s.substring(0, s.lastIndexOf("."));}
            datasetChoice.addItem(s);
         }
         data = getData(((Choice) xChoiceHash.get(currentDataset.name)).getSelectedItem());
         makeCounts();
         if (restrict)  restrictCounts();
      }

// find bin heights from areas; normalize total area to unity
      if (nFiles == 0) {
           double tot=0;
           for (int i=0; i < nBins; i++) tot += count[i];
           if (Math.abs(tot) < 1.0D-5 || tot < 0 ) {
               System.out.println("This is probably not a histogram; "+
                                  "the total area is small or negative."  +
                                  "Check your parameter settings.");
               return;
           }
           for (int i=0; i < nBins; i++) {
               count[i] /= tot*(binEnd[i+1]-binEnd[i]);
           }
      }

// set up the normal curve, if there is one.
      showNormalButton = new Button();
      if (showNormal) {
          makePhi();
          showNormalButton.setLabel("Hide Normal Curve");
      } else if (normalControls) {
          makePhi();
          showNormalButton.setLabel("Show Normal Curve");
      }

// set up the layout

    setLayout(new BorderLayout());
// the top panel gets the title.
    titleLayout = new FlowLayout(FlowLayout.CENTER);
    myPanel[0].setLayout(titleLayout);
    if (nFiles == 0 ) {
         myPanel[0].setFont(titleFont);
         myPanel[0].add(titleLabel = new Label(title,Label.CENTER));
    }

//the center panel gets the histogram in a BorderLayout
    myPanel[1].setLayout(new BorderLayout());
    hist = new Histogram();
    myPanel[1].add("Center", hist);

// bottom center gets scroll bars.
    Validator v1;
    Validator v2;
    if (binHist) {
        v1 = new HalfIntegerValidator();
        v2 = new HalfIntegerValidator();
    } else {

        v1 = new DoubleValidator();
        v2 = new DoubleValidator();
    }
    myPanel[2].setLayout(new FlowLayout(FlowLayout.CENTER));
    lo = new TextBar(hiLiteLo, binEnd[0], binEnd[nBins], digits, decimals,
                     (Validator) v1, TextBar.TEXT_FIRST,"Area from");
    myPanel[2].add(lo);
    hi = new TextBar(hiLiteHi, binEnd[0], binEnd[nBins], digits, decimals,
                     (Validator) v2, TextBar.TEXT_FIRST, "to");
    myPanel[2].add(hi);
    if (nFiles > 0 ) {
        bin = new TextBar(nBins, 1, data.length, 4, 0,
                       (Validator) new IntegerValidator(),
                       TextBar.NO_BAR,"Bins:");
        if (binControl) {
            myPanel[2].add(bin);
        }
    }

// labels for area go in the south of the main panel.
    myPanel[3].setLayout(new FlowLayout(FlowLayout.CENTER));
    myPanel[3].add(areaLabel = new Label("Selected area: " +
                   Format.doubleToPct(hiLitArea(count),5)));
    myPanel[3].add(normAreaLabel = new Label());
    if (restrict) {
        myPanel[3].add(cAreaLabel = new Label(" Subset data:" +
                   Format.doubleToPct(hiLitArea(cCount),5)));
        myPanel[3].add(cNormAreaLabel = new Label());
    }
    if (normalControls) myPanel[3].add(showNormalButton);

// Binomials and external data sets get extra panels
    if (binHist) {
        int barType;
        if (binomialBars) barType = TextBar.TEXT_TOP;
        else barType = TextBar.NO_BAR;
        myPanel[4].setLayout(new FlowLayout(FlowLayout.CENTER));
        myPanel[4].add(nBar = new TextBar(n, 1, 500, 3, 0,
                        (Validator) new IntegerValidator(),
                        barType,"n:"));
        myPanel[4].add(pBar = new TextBar(p*100, 0, 100, 4, 2,
                        (Validator) new DoubleValidator(),
                        barType,"p (%):"));
        myPanel[5].setLayout(new BorderLayout());
        myPanel[5].add("North",myPanel[3]);
        myPanel[5].add("Center",myPanel[2]);
        myPanel[5].add("South",myPanel[4]);
    } else if (nFiles > 0) {
        if (urlBox) {
            myPanel[0].add(boxLabel = new Label("URL:", Label.RIGHT));
            myPanel[0].add(urlField);
        }
        myPanel[0].add(dataLabel);
        if (nFiles > 1 || urlBox) {
            myPanel[0].add(datasetChoice);
        } else {
            myPanel[0].add(titleLabel = new Label(datasetChoice.getSelectedItem(),Label.CENTER));
            titleLabel.setFont(new Font("TimesRoman", Font.PLAIN, 12));
        }
        myPanel[0].add(xLabel);
        myPanel[0].add(xChoicePanel);
        myPanel[6].setLayout(new FlowLayout(FlowLayout.CENTER));
        if (listData) {
            myPanel[6].add(listButton);
        }
        if (univariateStats) {
            myPanel[6].add(univStatsButton);
        }
        myPanel[6].add(nLabel);
        myPanel[6].add(meanLabel);
        myPanel[6].add(sdLabel);
        if (!restrict) {
            myPanel[7].add(myPanel[6]);
        } else {
            myPanel[0].add(showOriginal);
            myPanel[0].add(showRestricted);
            myPanel[7].setLayout(new GridLayout(2,1));
            myPanel[8].setLayout(new FlowLayout(FlowLayout.CENTER));
            myPanel[8].add(rLabel);
            myPanel[8].add(rChoicePanel);
            myPanel[8].add(restrictPanel);
            myPanel[8].add(clearRestrict = new Button("Clear Restrictions"));
            myPanel[6].add(cNLabel);
            myPanel[6].add(cMeanLabel);
            myPanel[6].add(cSdLabel);
            myPanel[7].add(myPanel[8]);
            myPanel[7].add(myPanel[6]);
        }
        myPanel[5].setLayout(new BorderLayout());
        myPanel[5].add("North",myPanel[3]);
        myPanel[5].add("Center",myPanel[2]);
        myPanel[5].add("South",myPanel[7]);
        reTitle();
    } else {
        myPanel[5].setLayout(new BorderLayout());
        myPanel[5].add("North", myPanel[3]);
        myPanel[5].add("South",myPanel[2]);
    }
    if (showNormal) {
        normAreaLabel.setText(" Normal approx: " +
            Format.doubleToPct(normHiLitArea(mu,sd),5));
        if (restrict) {
            cNormAreaLabel.setText(" Normal approx: " +
                Format.doubleToPct(normHiLitArea(cMu, cSd),5));
        }
        myPanel[3].invalidate();
        validate();
    } else if (binHist) {
        normAreaLabel.setText(" ");
    }

    add("Center", myPanel[1]);
    add("South",myPanel[5]);
    add("North", myPanel[0]);
    if (!restrict) {
        cCount = null;
    } else {
        restrictLayout.show(restrictPanel,
            (String) ((Choice) rChoiceHash.get(currentDataset.name)).getSelectedItem());
        restrictPanel.invalidate();
    }
    validate();
    showHist();

    if (listData) {
          listFrame.setBackground(Color.white);
          listFrame.setTitle("Listing of dataset: " + datasetChoice.getSelectedItem());
          listFrame.hide();
          mRdmt = new RunnableDocument[myRunnableDocument.size()];
          for (int i=0; i < myRunnableDocument.size(); i++ ) {
                mRdmt[i] = (RunnableDocument) myRunnableDocument.elementAt(i);
          }
          docMgr = new DataManager(mRdmt, null, true,
                         DataManager.DefaultPriority);   // load the first dataset as text
    }
    if (univariateStats) {
        statFrame.setBackground(Color.white);
        makeStats();
        statFrame.hide();
    }
// Spawn threads to load the other data sets, if they exist
    if (nFiles > 1) {
        for (int i = 1; i < myRunnableDataSet.size(); i++) {
              ((DataSet) myRunnableDataSet.elementAt(i)).setCommentChar(commentChar);
              dmgr.addDataSet((RunnableDataSet) myRunnableDataSet.elementAt(i));
        }
    }


    } // end init

    public boolean handleEvent(Event e) {
        if (e.id == Event.WINDOW_DESTROY) {
            System.exit(0);
        } else if (e.id == Event.SCROLL_ABSOLUTE ||
                 e.id == Event.SCROLL_PAGE_UP ||
                 e.id == Event.SCROLL_PAGE_DOWN ||
                 e.id == Event.SCROLL_LINE_UP ||
                 e.id == Event.SCROLL_LINE_DOWN )  {
            if (e.target == lo || e.target == hi) {
                hiLiteLo = lo.getValue();
                hiLiteHi = hi.getValue();
                if (hiLiteLo >= hiLiteHi) {
                    hiLiteLo = hiLiteHi;
                }
                setAreas();
                showHist();
                return true;
            } else if (e.target == nBar || e.target == pBar) {
                setNP( (int) nBar.getValue(), pBar.getValue()/100.0 );
            } else if (nFiles > 0) {
                if (e.target == bin) {
                    nBins = (int) bin.getValue();
                    makeCounts();
                    if (restrict) restrictCounts();
                    setAreas();
                    showHist();
                }
                if (restrict) {
                    if (e.target == ((RestrictPanel) rApplyHash.get(
                         ((Choice) rChoiceHash.get(currentDataset.name)).getSelectedItem())).less
                        || e.target == ((RestrictPanel) rApplyHash.get(
                             ((Choice) rChoiceHash.get(currentDataset.name)).getSelectedItem())).more)
                    {
                        restrictCounts();
                        showHist();
                    }
                }
            }
        } else if (e.id == Event.ACTION_EVENT) {
            if (nFiles > 0 ) {
                if (e.target == xChoiceHash.get(currentDataset.name)) changedX();
                else if (e.target == urlField) addDataSet(urlField.getText());
                else if (e.target.equals(datasetChoice)) datasetChoiceChanged();
                else if (restrict) {
                    if (e.target == rChoiceHash.get(currentDataset.name)) {
                        restrictVarChanged();
                    } else if (e.target ==
                             ((RestrictPanel) rApplyHash.get(
                             ((Choice) rChoiceHash.get(currentDataset.name)).getSelectedItem())).lessBox
                               || e.target ==
                             ((RestrictPanel) rApplyHash.get(
                             ((Choice) rChoiceHash.get(currentDataset.name)).getSelectedItem())).moreBox)
                    {
                        restrictCounts();
                        showHist();
                    }
                    else if (e.target == showOriginal || e.target == showRestricted) {
                        showHist();
                    }
                    else if (e.target == clearRestrict) {
                        clearedRestrictions();
                    }
                }
            }
            if (e.arg == "Show Normal Curve") {
                showNormal = true;
                showNormalButton.setLabel("Hide Normal Curve");
                normAreaLabel.setText("Normal approx: "
                       + Format.doubleToPct(normHiLitArea(mu,sd),5));
                if (restrict) cNormAreaLabel.setText("Normal approx: "
                       + Format.doubleToPct(normHiLitArea(cMu,cSd),5));
                normAreaLabel.invalidate();
                myPanel[3].invalidate();
                myPanel[5].invalidate();
                validate();
                showHist();
            } else if (e.arg == "Hide Normal Curve") {
                showNormal = false;
                showNormalButton.setLabel("Show Normal Curve");
                normAreaLabel.setText(" ");
                if (restrict) cNormAreaLabel.setText(" ");
                myPanel[3].invalidate();
                myPanel[5].invalidate();
                validate();
                showHist();
            } else if (e.arg == "List Data") {
                listToggled();
            } else if (e.arg == "Univariate Stats") {
                toggleStats();
            }
        }
        return super.handleEvent(e);
    }

    private void restrictVarChanged() {
        restrictLayout.show(restrictPanel,(String)
             ((Choice) rChoiceHash.get(currentDataset.name)).getSelectedItem());
        restrictPanel.invalidate();
        validate();
    }

    private void clearedRestrictions() {
        for (Enumeration e = rApplyHash.keys(); e.hasMoreElements();) {
            String varName = (String) e.nextElement();
            RestrictPanel rp = (RestrictPanel) rApplyHash.get(varName);
            rp.setStates(false, false);
            double[] vmnmx = PbsStat.vMinMax(PbsStat.removeNaN(getData(varName)));
            rp.setValues(vmnmx[0], vmnmx[1]);
        }
        restrictCounts();
        showHist();
    }

    private void changedX() {
        data = getData(((Choice) xChoiceHash.get(currentDataset.name)).getSelectedItem());
        reTitle();
        setAreas();
        showHist();
    }

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


    private void listToggled() {
        if (listFrame.isShowing()) {
            listFrame.hide();
        } else {
            text = ( (Document) docMgr.getDataSet(currentURL.toString()) ).contents;
            list[0] = text.substring(0, text.indexOf("\n"));
            list[1] = text.substring(text.indexOf("\n") + 1, text.length());
            listFrame.setText(list);
            listFrame.show();
            listFrame.toFront();
            listFrame.sizeToText();
        }
    }

    private void datasetChoiceChanged() {
        int which = datasetChoice.getSelectedIndex();
        currentURL = (URL) dataURL.elementAt(which);
        listFrame.setTitle("Listing of dataset: " + datasetChoice.getSelectedItem());
        if (listFrame.isShowing()) {
            text = ( (Document) docMgr.getDataSet(currentURL.toString()) ).contents;
            list[0] = text.substring(0, text.indexOf("\n"));
            list[1] = text.substring(text.indexOf("\n") + 1, text.length());
            listFrame.setText(list);
            listFrame.sizeToText();
        }
        currentDataset = (DataSet) dmgr.getDataSet(currentURL.toString());
        insertVariables(currentDataset);
        xChoiceLayout.show(xChoicePanel, currentDataset.name);
        data = getData(((Choice)
               xChoiceHash.get(currentDataset.name)).getSelectedItem());
        if (restrict) {
            rChoiceLayout.show(rChoicePanel, currentDataset.name);
        }
        if (univariateStats) {
            makeStats();
        }
        reTitle();
        setAreas();
        showHist();
    }


    private void showHist() {
        if (restrict) {
            if (showOriginal.getState()) {
                hist.setColor(Histogram.DEFAULT_COLOR);
                if (showRestricted.getState()) {
                     hist.redraw(binEnd, count, true, cCount, hiLiteLo, hiLiteHi, showNormal,
                           xVal, yVal, cYVal);
                }
                else {
                     hist.redraw(binEnd, count, false, null, hiLiteLo, hiLiteHi, showNormal,
                           xVal, yVal, null);
                }
            }
            else if (showRestricted.getState()) {
                hist.setColor(Histogram.REVERSE_COLOR);
                hist.redraw(binEnd, cCount, false, null, hiLiteLo, hiLiteHi, showNormal,
                           xVal, cYVal, null);
            }
        }
        else hist.redraw(binEnd, count, hiLiteLo, hiLiteHi, showNormal, xVal, yVal);
    }

    public boolean insertVariables(DataSet ds) {
         String varName ="";
         RestrictPanel rp;
         double[] vmnmx;
         Component[] rpcomp;
         if( !xChoiceHash.containsKey(ds.name)) {
              Choice xc = new Choice();
              Choice rc = new Choice();
              for (Enumeration e = ds.Var.keys(); e.hasMoreElements();) {
                  varName = (String) e.nextElement();
                  xc.addItem(varName);
                  rc.addItem(varName);
              }

              xChoiceHash.put(ds.name, xc);
              xChoicePanel.add(ds.name, xc);
              ((Choice) xChoiceHash.get(ds.name)).select(0);
              xChoicePanel.validate();
              if (restrict) {
                  rChoiceHash.put(ds.name, rc);
                  rChoicePanel.add(ds.name, rc);
                  rChoicePanel.validate();
                  ((Choice) rChoiceHash.get(ds.name)).select(0);
              }
              validate();
         } else {
              ((Choice) xChoiceHash.get(ds.name)).select(0);
              if (restrict) {
                ((Choice) rChoiceHash.get(ds.name)).select(0);
              }
         }
         if (restrict) {
              rApplyHash = new Hashtable(ds.Var.size());
              rpcomp = restrictPanel.getComponents();
              restrictPanel.removeAll();
              for (int i = 0; i < rpcomp.length; i++) {
                   restrictLayout.removeLayoutComponent(rpcomp[i]);
              }
              for (Enumeration e = ds.Var.keys(); e.hasMoreElements();) {
                   rp = new RestrictPanel();
                   varName = (String) e.nextElement();
                   vmnmx = PbsStat.vMinMax(PbsStat.removeNaN(getData(varName)));
                   rp.setValues(vmnmx[0], vmnmx[1], vmnmx[0], vmnmx[1], digits);
                   restrictPanel.add(varName, rp);
                   rApplyHash.put(varName, rp);
              }
              restrictLayout.show(restrictPanel,
                              (String) ((Choice) rChoiceHash.get(ds.name)).getSelectedItem());
              restrictPanel.invalidate();
              validate();
         }

         return true;
    } // ends insertVariables

    private double[] getData(String v) {
        double[] data;
        data = (currentDataset.getVariable(v)).data();
        return data;
    }

    private void reTitle() {
        if (nFiles < 1) {
            myPanel[0].remove(titleLabel);
            titleLabel = new Label(
                        ((Choice) xChoiceHash.get(currentDataset.name)).getSelectedItem());
            myPanel[0].add(titleLabel);
        }
        myPanel[0].validate();
        myPanel[0].repaint();
        makeCounts();
        hiLiteLo = Math.max(Math.min(hiLiteLo,binEnd[nBins]),binEnd[0]);
        hiLiteHi = Math.min(Math.max(hiLiteHi,binEnd[0]),binEnd[nBins]);
        lo.setValues(hiLiteLo, binEnd[0], binEnd[nBins], digits, decimals);
        hi.setValues(hiLiteHi, binEnd[0], binEnd[nBins], digits, decimals);
        bin.setValues(nBins, 1, data.length, 4, 0);
        nLabel.setText(" n= " + data.length);
        sdLabel.setText(" SD= " + Format.numToString(sd,digits,decimals,true));
        meanLabel.setText(" Mean= " + Format.numToString(mu,digits,decimals,true));
        if (restrict) {
            clearRestrictions();
            restrictCounts();
        }
        myPanel[6].invalidate();
        myPanel[7].invalidate();
        myPanel[5].invalidate();
        validate();
    }

    private void clearRestrictions() {
        String varName;
        double[] vmnmx;
        double[] tmpData;
        RestrictPanel rp;
        for (Enumeration e = currentDataset.Var.keys(); e.hasMoreElements();) {
            varName = (String) e.nextElement();
            tmpData = getData(varName);
            vmnmx = PbsStat.vMinMax(PbsStat.removeNaN(tmpData));
            rp = (RestrictPanel) rApplyHash.get(varName);
            rp.setStates(false, false);
            rp.setValues(vmnmx[0], vmnmx[1], vmnmx[0], vmnmx[1], digits);
        }
    }


    private void setAreas() {
        areaLabel.setText("Selected area: " +
                           Format.doubleToPct(hiLitArea(count),5));
        if (restrict) cAreaLabel.setText(" Subset data: " +
                           Format.doubleToPct(hiLitArea(cCount),5));
        if (showNormal) {
            normAreaLabel.setText("Normal approx: "
                       + Format.doubleToPct(normHiLitArea(mu,sd),5));
            if (restrict) cNormAreaLabel.setText(" Normal approx: " +
                           Format.doubleToPct(normHiLitArea(cMu, cSd),5));
        } else {
            normAreaLabel.setText(" ");
            if (restrict) {
                cNormAreaLabel.setText(" ");
            }
        }
        myPanel[3].invalidate();
        myPanel[5].invalidate();
        validate();
        myPanel[5].repaint();
    }

    private void applyRestrict() {
        String varName;
        RestrictPanel rp;
        double lb;
        double ub;
        double[] temData = new double[data.length];
        boolean[] tData = new boolean[data.length];
        boolean[] tFlag = new boolean[2];
        for (int i=0; i < tData.length; i++) {
            tData[i] = true;
        }
        for (Enumeration e = currentDataset.Var.keys(); e.hasMoreElements();) {
            varName = (String) e.nextElement();
            rp = (RestrictPanel) rApplyHash.get(varName);
            rData = getData(varName);
            rMnMx = PbsStat.vMinMax(PbsStat.removeNaN(rData));
            lb = rMnMx[0];
            ub = rMnMx[1];
            tFlag = rp.getStates();
            if (tFlag[1]) {
                ub = rp.less.getValue();
            }
            if (tFlag[0]) {
                lb = rp.more.getValue();
            }
            for (int i = 0; i < data.length; i++) {
                if (( tFlag[0] || tFlag[1]) &&
                                (Double.isNaN(rData[i]) || rData[i] < lb || rData[i] > ub)) {
                   tData[i] = false;
                }
            }
        }
        int k = 0;
        for (int i = 0; i < data.length; i++) {
            if (tData[i]) {
                temData[k] = data[i];
                k += 1;
            }
        }
        cData = new double[k];
        System.arraycopy(temData,0,cData,0,k);
        cMu = PbsStat.MeanNaN(cData);
        cSd = PbsStat.SdNaN(cData);
        for (int i=0; i < nx; i++) {
            cYVal[i] = PbsStat.normPdf(cMu, cSd, xVal[i]);
        }
        cNLabel.setText(" Subset: n= " + cData.length);
        cMeanLabel.setText(" Mean= " + Format.numToString(cMu,digits,decimals,true));
        cSdLabel.setText(" SD= " + Format.numToString(cSd,digits,decimals,true));
        myPanel[6].invalidate();
        myPanel[7].invalidate();
        myPanel[5].validate();
        validate();
    } // ends applyRestrict

    private void restrictCounts() {
        applyRestrict();
        cCount = new double[nBins];
        double[] cleanDat = PbsStat.removeNaN(cData);
        for (int i=0; i < nBins; i++) {
            cCount[i] = 0;
        }
        for (int i=0; i < cleanDat.length; i++) {
           for (int k=0; k < nBins - 1; k++) {
              if (cleanDat[i] >= binEnd[k] && cleanDat[i] < binEnd[k+1] ) {
                  cCount[k] += 1;
              }
           }
           if (cleanDat[i] >= binEnd[nBins - 1] ) {
              cCount[nBins - 1] += 1;
           }
        }
        for (int i=0; i < nBins; i++) {
           cCount[i] /= cleanDat.length*(binEnd[i+1]-binEnd[i]);
        }
    }


    private void makeCounts() {
        count = new double[nBins];
        for (int i=0; i < nBins; i++) {
            count[i] = 0;
        }
        mu = PbsStat.MeanNaN(data);
        sd = PbsStat.SdNaN(data);
        dMnMx = PbsStat.vMinMax(PbsStat.removeNaN(data));
        double[] cleanDat = PbsStat.removeNaN(data);
        binEnd = new double[nBins+1];
        for (int i=0; i <= nBins; i++) {
           binEnd[i] = dMnMx[0] + i*(dMnMx[1] - dMnMx[0])/nBins;
        }
        for (int i=0; i < cleanDat.length; i++) {
           for (int k=0; k < nBins - 1; k++) {
              if (cleanDat[i] >= binEnd[k] && cleanDat[i] < binEnd[k+1] ) {
                  count[k] += 1;
              }
           }
           if (cleanDat[i] >= binEnd[nBins - 1] ) {
              count[nBins - 1] += 1;
           }
        }
        for (int i=0; i < nBins; i++) {
           count[i] /= cleanDat.length*(binEnd[i+1]-binEnd[i]);
        }
        makePhi();
   }

   public double estimatedPercentileByString(String pct) {  // estimates the pth percentile from the current histogram
        return(estimatedPercentile(Format.strToDouble(pct, 0)));
   }

   public double estimatedPercentile(double pct) {  // estimates the pth percentile from the current histogram
        double p = pct/100.0;
        double pctile;
        if (p > 1.0) {
            pctile = Double.NaN;
        } else if (p == 1.0) {
            pctile = binEnd[nBins];
        } else {
            double area = 0.0;
            int j = 0;
            while (area < p) {
               j++;
               area = hiLitArea(binEnd[0],binEnd[j]);
            }
            j--;
            area = p - hiLitArea(binEnd[0],binEnd[j]);
            double nextBinArea = hiLitArea(binEnd[j], binEnd[j+1]);
            pctile = binEnd[j] + area/nextBinArea * (binEnd[j+1] - binEnd[j]);
        }
        return(pctile);
   }

   public double hiLitArea(double loEnd, double hiEnd) {
// returns the area of the primary histogram between loEnd and hiEnd
          return(hiLitArea(loEnd, hiEnd, count));
   }

   public double hiLitAreaByString(String loEnd, String hiEnd) {
// returns the area of the primary histogram between loEnd and hiEnd
          return(hiLitArea(Format.strToDouble(loEnd, 0), Format.strToDouble(hiEnd, 0)));
   }

   public double hiLitArea(double loEnd, double hiEnd, double[] c) { // area of count c from loEnd to hiEnd
          double area = 0;
          for (int i=0; i < nBins; i++) {
             if( binEnd[i]  > hiEnd ||  binEnd[i+1] <= loEnd) {}
             else if (binEnd[i] >= loEnd && binEnd[i+1] <= hiEnd) {
                area += c[i]*(binEnd[i+1]-binEnd[i]);
             }
             else if (binEnd[i] >= loEnd && binEnd[i+1] > hiEnd) {
                area += c[i]*(hiEnd - binEnd[i]);
             }
             else if (binEnd[i] <= loEnd && binEnd[i+1] <= hiEnd) {
                area += c[i]*(binEnd[i+1]-loEnd);
             }
             else if (binEnd[i] < loEnd && binEnd[i+1] > hiEnd) {
                area += c[i]*(hiEnd - loEnd);
             }
      }
      return area;
   }

   private double hiLitArea(double[] c) { // returns area corresponding to count vector c from hiLiteLi to hiLiteHi
        return hiLitArea(hiLiteLo, hiLiteHi, c);
   }

   private void makeBin() {
       nBins = n+1;
       count = new double[nBins];
       count[0] = Math.pow((1-p),n);
       binEnd = new double[nBins+1];
       binEnd[0] = -0.5;
       for ( int i=1; i < nBins; i++) {
          if (p < 1) {
              count[i] = count[i-1]*p*(n-i+1)/((1-p)*i);
          }
          else {
              count[i] = 0;
          }
          binEnd[i] = i-0.5;
       }
       binEnd[nBins] = n + 0.5;
       if ( p == 1) {
          count[nBins - 1] = 1;
       }
       sd = Math.sqrt(n*p*(1-p));
       mu = n*p;
   }// ends makeBin


   private double normHiLitArea(double m,double s) {
       return(PbsStat.normCdf((hiLiteHi-m)/s) - PbsStat.normCdf((hiLiteLo-m)/s));
   }// ends normHiLitArea


   private void makePhi() {
       for (int i = 0; i < nx; i++) {
           xVal[i] = binEnd[0] + i*(binEnd[nBins]-binEnd[0])/(nx-1);
           yVal[i] = PbsStat.normPdf(mu, sd, xVal[i]);
       }
   }

   private void makeUrls() { // makes the URLs of the filenames given; adds to datasets.
          URL u;
          URL cb = getCodeBase();
          for (int i = 0; i < fileName.size(); i++) { // form URLs from the names
              u = Format.stringToURL((String) fileName.elementAt(i), cb);
              dataURL.addElement(u);
              myRunnableDataSet.addElement(new RunnableDataSet(u));
              myRunnableDocument.addElement(new RunnableDocument(u));
         }
   }

   public void addDataSet(String dsName) { // public method to add a new dataset via its URL, given as a String
          URL u = Format.stringToURL(dsName, getCodeBase());
          RunnableDataSet rds;
          RunnableDocument rdmt;
          fileName.addElement(dsName);
          nFiles++;
          dataURL.addElement(u);
          rds = new RunnableDataSet(u);
          rdmt = new RunnableDocument(u);
          myRunnableDataSet.addElement(rds);
          myRunnableDocument.addElement(rdmt);
          dmgr.addDataSet(rds);
          docMgr.addDataSet(rdmt);
          String s = dsName.substring(dsName.lastIndexOf("/")+1,dsName.length()); // strip root
          if (s.lastIndexOf(".") > -1) { s = s.substring(0, s.lastIndexOf("."));}
          datasetChoice.addItem(s);
          datasetChoice.select(s);
          datasetChoiceChanged();
          myPanel[0].validate();
          myPanel[0].repaint();
   }

   public void setLimits(double down, double up) {
      lo.setValue(down);
      hi.setValue(up);
      Event e = new Event(lo, Event.SCROLL_ABSOLUTE,"outside");
      this.handleEvent(e);
   }

   public void setLimits(String down, String up) {
      setLimits(Format.strToDouble(down,0),Format.strToDouble(up,0));
   }

   public void setNP(int newN, double newP) {
        n = newN;
        nBins = n+1;
        p = newP;
        makeBin();
        makePhi();
        if (hiLiteHi > binEnd[nBins]) hiLiteHi = binEnd[nBins];
        if (hiLiteLo < binEnd[0]) hiLiteLo = binEnd[0];
        hi.setValues(hiLiteHi, -.5, n+0.5, digits, decimals);
        lo.setValues(hiLiteLo, -.5, n+0.5, digits, decimals);
        setAreas();
        myPanel[3].invalidate();
        myPanel[5].invalidate();
        validate();
        showHist();
   }

   public void setNP(String newN, String newP) {
        setNP( Format.strToInt(newN,5), Format.strToDouble(newP,0.5));
   }

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

}// ends class HistHiLite

