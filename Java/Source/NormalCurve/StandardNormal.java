import java.awt.*;
import java.applet.*;
import PbsStat.*;
import PbsGui.*;

public class StandardNormal extends Applet {

GraphCanvas c;
TextBar mean;
TextBar SD;
Button button1;
private final double meanMin = -3;
private final double meanMax = 3;
private final double xMin = -4.2;
private final double xMax = 4.2;
private final double SDmin = 0.5;
private final double SDmax = 4;
private final int digits = 4;
private final int decimals = 2;
private double[] xVals;
private double[] yVals;
int MAXPOINTS;


    public void init() {
        super.init();
        setBackground(Color.white);
        setLayout(new BorderLayout());
    // the North panel gets the clear button
        Panel p1 = new Panel();
        p1.setLayout(new FlowLayout(FlowLayout.LEFT));
        button1=new Button("clear lines");
        button1.reshape(95,165,90,30);
        p1.add(button1);

    // the Center panel gets the canvas
        c = new GraphCanvas();
        c.setLimits(xMin, xMax);
    // the South panel gets the scroll controls
        Panel p3 = new Panel();
        p3.setLayout(new FlowLayout(FlowLayout.CENTER));
        DoubleValidator dv = new DoubleValidator();
        p3.add(mean = new TextBar(0, meanMin, meanMax, digits, decimals,
                                 (Validator) dv, TextBar.TEXT_TOP, "Mean"));
        p3.add(SD = new TextBar(1, SDmin, SDmax, digits, decimals,
                                 (Validator) dv, TextBar.TEXT_TOP, "SD"));
        add("North", p1);
        add("Center", c);
        add("South",p3);
        makeCurve();
        c.redraw(xVals, yVals);
    }

    public void clickedButton1() {
        c.setCurrentPoint(null);
        c.setCurrentLine(0);
        c.setAnchor(null);
        c.redraw(xVals, yVals);

    }

    public boolean handleEvent(Event e) {
        if (e.id == Event.WINDOW_DESTROY) System.exit(0);
        else if (e.id == Event.SCROLL_ABSOLUTE
                 || e.id == Event.SCROLL_LINE_DOWN
                 || e.id == Event.SCROLL_LINE_UP
                 || e.id == Event.SCROLL_PAGE_DOWN
                 || e.id == Event.SCROLL_PAGE_UP) {
            makeCurve();
            c.redraw(xVals, yVals);
            return(true);
        }
        else if (e.id == Event.ACTION_EVENT && e.target == button1) {
            clickedButton1();
            return(true);
        }
        return super.handleEvent(e);
    }

    private void makeCurve() {
         xVals = new double[c.MAXPOINTS];
         yVals = new double[c.MAXPOINTS];
         for (int j=0; j < xVals.length; j++) {
			 xVals[j]=xMin+j*(xMax-xMin)/(c.MAXPOINTS-1);
             yVals[j]=PbsStat.normPdf(mean.getValue(),SD.getValue(), xVals[j]);
         }
    }
}

class GraphCanvas extends Canvas {

     public final int MAXLINES = 30;
     public final int MAXPOINTS = 300;
     double  xMin = -1;
     double xMax = 1;
     final int vOffset = 50;
     final double yMax = 1.3;
     final double dx = (xMax-xMin)/(MAXPOINTS - 1);

     int currLine = 0;
     Dimension dim = new Dimension();
     Point starts[]= new Point[MAXLINES];
     Point ends[] = new Point[MAXLINES];
     Point anchor;
     Point currPoint;
     Polygon theCurve= new Polygon();
     Point xAxis[] = new Point[2];
     Point yAxis[] = new Point[2];
     double[] xvals;
     double[] yvals;


   GraphCanvas() {
	   resize(400,400);
   }

   public void redraw(double[] xv, double[] yv) {
	  xvals = xv;
	  yvals = yv;
      repaint();
   }

   public void paint(Graphics g) {
// first redo the curve
       makeTheCurve();
       PbsPlot.drawOpenPolygon(g,theCurve);
// plot the axes
       int wd=size().width;
       int ht=size().height;
       g.drawLine(0,ht-vOffset/2,wd, ht-vOffset/2);  // x axis
       g.drawLine(wd/2,0,wd/2,ht-vOffset/2);// y axis

       Font f= new Font("TimesRoman", Font.PLAIN, 14);
       g.setFont(f);
       FontMetrics fm = getFontMetrics(f);
       int sh= fm.getHeight();
/* add some tic marks and labels to the axes.
the height of y=k in plot units is
        (ht-vOffset/2) - (k/yMax)*(ht-vOffset)
the horizontal position of x=k in plot units is
        (wd/2)+k/(xMax-xMin)*wd
thus:
*/
       int nYTics = (int) Math.floor(yMax);
       for (int i=1; i<= nYTics ; i++) {
           int yt= (int) ((ht-vOffset/2)-((i)/yMax)*(ht-vOffset));
           g.drawLine(wd/2-5, yt, wd/2+5, yt);
           String s = "" + i;
           int sw = fm.stringWidth(s);
           g.drawString(s, wd/2-7-sw,yt+sh/2);
       }
       int nXTics = (int) Math.floor(xMax-xMin);
       for (int i= (int) (-nXTics/2); i <= (int) (nXTics/2); i++) {
           int xt=(int) (wd/2+i/(xMax-xMin)*wd);
           g.drawLine(xt,ht-vOffset/2-5,xt,ht-vOffset/2+5);
           String s = "" + i;
           int sw = fm.stringWidth(s);
           g.drawString(s,xt-sw/2,ht-vOffset/2+sh);
       }
// draw existing lines
       for (int i=1; i < currLine; i++) {
            g.setColor(Color.blue);
            g.drawLine(starts[i].x,starts[i].y,
                ends[i].x,ends[i].y);
       }
       g.setColor(Color.red);
       if (currPoint != null)
            g.drawLine(anchor.x, anchor.y,
                currPoint.x, currPoint.y);
     }

     void makeTheCurve() {
// rescale to pixel coordinates for plotting
         double maxY=0;
         for (int j=0; j< yvals.length; j++) maxY=Math.max(maxY,yvals[j]);
         int wd = size().width;
         int ht= size().height;
         double xScale = wd/(xMax-xMin-dx);
//       double yScale = (ht-vOffset)/maxY;
// use a fixed scale based on yMax, not maxY, for now:
         double yScale = (ht - vOffset)/yMax;  //yMax defaults to 1.
// clear the current curve
         theCurve = new Polygon();
         for (int j=0; j < xvals.length; j++)
             theCurve.addPoint((int) ((xvals[j]-xMin)*xScale),
                (int) (ht-yvals[j]*yScale)-vOffset/2);
         return;
     }

     public boolean mouseDown(Event e, int x, int y) {
         anchor = new Point(x,y);
         return true;
     }

     public boolean mouseUp(Event e, int x, int y) {
         if (currLine < MAXLINES)
            addLine(x,y);
         else System.out.println("Too many lines.");
         return true;
     }

     public boolean mouseDrag(Event e, int x, int y) {
         currPoint = new Point(x,y);
         repaint();
         return true;
     }

     private void addLine(int x, int y) {
         starts[currLine]= anchor;
         ends[currLine]= new Point(x,y);
         currLine++;
         currPoint= null;
         repaint();
     }

     public void setCurrentLine(int line) {
         currLine = line;
     }

     public void setCurrentPoint(Point p) {
         currPoint= p;
     }

     public void setAnchor(Point p) {
         anchor = p;
     }

     public void setxMax(double xm) {
        xMax = xm;
     }

     public void setxMin(double xm) {
        xMin = xm;
     }

     public void setLimits(double xmn, double xmx) {
        xMin = xmn;
        xMax = xmx;
     }

}


