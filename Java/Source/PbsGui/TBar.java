/**
package PbsGui

public class TBar extends myScrollbar

a small scrollbar tied to a TextBar comboinput

@author P.B. Stark stark@stat.berkeley.edu
@version 1.0
@copyright 1997, 1998, P.B. Stark

Last modified 28 July 1998

*/

package PbsGui;

import java.awt.*;

public class TBar extends myScrollbar
{
    private TextBar textBar;

    TBar(TextBar textBar, double val, int minVal, int maxVal)
    {
        super(Scrollbar.HORIZONTAL);
        this.setValues(((int) ((val-textBar.getMinValue()) /
              (textBar.getMaxValue() - textBar.getMinValue()) *
               textBar.getScale())), 0, minVal, maxVal+1);
        this.textBar = textBar;
    }

    public boolean handleEvent(Event e)
    {
        if (e.id == Event.SCROLL_ABSOLUTE || e.id == Event.SCROLL_PAGE_DOWN ||
            e.id == Event.SCROLL_PAGE_UP || e.id == Event.SCROLL_LINE_DOWN ||
            e.id == Event.SCROLL_LINE_UP)
        {
            double newVal = ((double) getValue())/((double) textBar.getScale()) *
                        (textBar.getMaxValue() - textBar.getMinValue()) +
                         textBar.getMinValue();
            textBar.updateValue(this, e, (new Double(newVal)).toString());
        }
        return true;
    }
}