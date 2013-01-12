/**
public interface XYComponent


An interface to guarantee a way to convert pixel
coordinates to reals, and vice versa.
This will usually be implemented by a Component,
in particular, a Canvas (such as XYCanvas)

@author P.B. Stark stark@stat.berkeley.edu
@version 0.1

Last modified 8 August 1997.

*/
public interface XYComponent
{
    public double pixToX(int p);

    public double pixToY(int p);

    public int xToPix(double x);

    public int yToPix(double y);

}