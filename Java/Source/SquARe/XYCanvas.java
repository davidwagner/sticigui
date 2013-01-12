/**
public Class XYCanvas extends Canvas

An abstract class that adds the ability to convert pixels to
real coordinates, and back.

@author P.B. Stark stark@stat.berkeley.edu
@date 5 August 1997
@version 0.1


*/
import java.awt.*;

public class XYCanvas extends Canvas implements XYComponent
{
    protected int xOffset = 60;                 // x insets in pixels, *2
    protected int yOffset = 40;
    protected double xLo;                       // lower x axis limit
    protected double xHi;                       // upper x axis limit
    protected double yLo;                       // lower y axis limit
    protected double yHi;                       // upper y axis limit
    protected double xScale;                    // convert to/from pix
    protected double yScale;                    // convert to/from pix
    private int ht;                             // height of the canvas
    private int wd;                             // width of the canvas
    protected int dotSize = 7;                  // size of plotting symbol

    XYCanvas()
    {
        super();
        setScale(size());
    }

    public int xToPix(double x)
    {
        return( (int) ((x - xLo)*xScale ) + xOffset/2);
    } // ends xToPix

    public int yToPix(double y)
    {
        return(ht - yOffset - (int) ((y-yLo)*yScale ));
    } // ends yToPix

    public double pixToX(int p)
    {// converts the pixel value to the corresponding value of x
        return(xLo + (p - xOffset/2)/xScale);
    }

    public double pixToY(int p)
    {
        return(yLo + (ht - yOffset - p )/yScale);
    }

    public void setXOffset(int xOffset)
    {
        this.xOffset = xOffset;
        setScale(size());
    }

    public int getXOffset()
    {
        return(xOffset);
    }

    public void setYOffset(int yOffset)
    {
        this.yOffset = yOffset;
        setScale(size());
    }

    public int getYOffset()
    {
        return(yOffset);
    }

    public void setDotSize(int dotSize)
    {
        this.dotSize = dotSize;
        setScale(size());
    }

    public int getDotSize()
    {
        return(dotSize);
    }

    protected void setScale(Dimension dim)
    {
        ht = dim.height;
        wd = dim.width;
        xScale = ((double) (wd - xOffset - dotSize ))/(xHi - xLo);
        yScale = ((double) (ht - 2*yOffset - dotSize))/(yHi - yLo);
    }

    public double[] getScale()
    {
        double[] sc = new double[2];
        sc[0] = xScale;
        sc[1] = yScale;
        return(sc);
    }

    public void setAxes(double xl, double xh, double yl,
                        double yh)
    {
        xLo = xl;
        xHi = xh;
        yLo = yl;
        yHi = yh;
        setScale(size());
    }

    public double[] getAxes()
    {
        double[] axes = new double[4];
        axes[0] = xLo;
        axes[1] = xHi;
        axes[2] = yLo;
        axes[3] = yHi;
        return(axes);
    }



}