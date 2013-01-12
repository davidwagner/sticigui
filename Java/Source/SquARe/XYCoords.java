/**
public class XYCoords extends MouseMotionAdapter

A MouseMotionListener that displays the current cursor position
on named labels

@author P.B. Stark stark@stat.berkeley.edu
@date 5 August 1997
@version 0.1
@copyright 1997. P.B. Stark

*/

import java.awt.*;
import java.awt.event.*;

public class XYCoords extends MouseMotionAdapter
{
    protected XYComponent xycomponent;
    protected Font labelFont = new Font("Helvetica",Font.PLAIN,12);
    protected int digits = 5;                   // number of digits to
                                                // show on labels
    protected String xPretext = "x: ";          // text before number
    protected String yPretext = "y: ";          // ditto
    protected Label xLabel = new Label("              ");// label for x axis
    protected Label yLabel = new Label("              ");// label for y axis

    protected Container labelContainer;

    public XYCoords(XYComponent xycomponent)
    {
        super();
        setXYComponent(xycomponent);
    }

    public int getDigits()
    {
        return(digits);
    }

    public void setDigits(int digits)
    {
        this.digits = digits;
    }

    public String getXPretext()
    {
        return(xPretext);
    }

    public void setXPretext(String s)
    {
        xPretext = s;
    }

    public String getYPretext()
    {
        return(yPretext);
    }

    public void setYPretext(String s)
    {
        yPretext = s;
    }

    public void setXYComponent(XYComponent xycomponent)
    {
        this.xycomponent = xycomponent;
        ((Component) xycomponent).addMouseMotionListener(this);
    }


    public XYComponent getXYComponent()
    {
        return(xycomponent);
    }

    public void setFont(Font f)
    {
        labelFont = f;
        xLabel.setFont(labelFont);
        yLabel.setFont(labelFont);
    }

    public Font getFont()
    {
        return(labelFont);
    }

    public void setXLabel(Label lab)
    {
        xLabel = lab;
    }

    public Label getXLabel()
    {
        return(xLabel);
    }

    public void setYLabel(Label lab)
    {
        yLabel = lab;
    }

    public Label getYLabel()
    {
        return(yLabel);
    }

    public void mouseMoved(MouseEvent e)
    {
        double x = xycomponent.pixToX(e.getX());
        double y = xycomponent.pixToY(e.getY());
        xLabel.setText(xPretext + numToString(x,digits));
        yLabel.setText(yPretext + numToString(y,digits));
        xLabel.validate();
        yLabel.validate();
    }

    public String numToString(double v, int places)
    {
        String sValue = (new Double(v)).toString();
        int vLength = sValue.length();
        int p = places;
        if (sValue.substring(0,1).equals("-")) {p = places+1;}
        return(sValue.substring(0,Math.min(vLength,p)));
    }

}
