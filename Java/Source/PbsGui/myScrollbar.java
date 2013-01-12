/**
package PbsGui;

public class myScrollbar extends Scrollbar

a smaller scrollbar

@author P.B. Stark stark@stat.berkeley.edu
@copyright 1997, P.B. Stark
@version 1.0

Last modified 15 September 1997

*/

package PbsGui;
import java.awt.*;

public class myScrollbar extends Scrollbar
{
    public final int width = 70;
    public final int height = 15;

    public myScrollbar()
    {
        super();
        this.resize(width,height);
    }

    public myScrollbar(int i)
    {
        super(i);
        if (getOrientation() == Scrollbar.VERTICAL)
        {
            this.resize(height,width);
        }
        else
        {
            this.resize(width,height);
        }
    }

    public myScrollbar(int i, int j, int k, int l, int m)
    {
        super(i, j, k, l, m);
        if (getOrientation() == Scrollbar.VERTICAL)
        {
            this.resize(height,width);
        }
        else
        {
            this.resize(width,height);
        }
    }

    public Dimension minimumSize()
    {
        if (getOrientation() == Scrollbar.VERTICAL)
        {
            return new Dimension(height, width);
        }
        else
        {
            return new Dimension(width, height);
        }
    }

    public Dimension preferredSize()
    {
        if (getOrientation() == Scrollbar.VERTICAL)
        {
            return new Dimension(height, (int) (width*1.1));
        }
        else
        {
            return new Dimension((int) (width*1.1), height);
        }
    }

}
