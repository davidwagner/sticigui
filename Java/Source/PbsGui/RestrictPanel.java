package PbsGui;

import java.awt.*;
import java.applet.*;
import java.util.*;

public class RestrictPanel extends Panel
{
    public TextBar less;
    public TextBar more;
    public Checkbox lessBox;
    public Checkbox moreBox;
    private int digits = 5;
    private int decimals = 2;

    public RestrictPanel()
    {
        less = new TextBar(1, 0, 1, digits, decimals, (Validator) new DoubleValidator(),
            TextBar.NO_BAR);
        lessBox = new Checkbox("and <=");
        more = new TextBar(0, 0, 1, digits, decimals, (Validator) new DoubleValidator(),
            TextBar.NO_BAR);
        moreBox = new Checkbox(">=");
        setLayout(new FlowLayout(FlowLayout.CENTER));
        add(moreBox);
        add(more);
        add(lessBox);
        add(less);
    }

    public RestrictPanel(TextBar less, TextBar more, Checkbox lessBox, Checkbox moreBox)
    {
        this.less = less;
        this.more = more;
        this.lessBox = lessBox;
        this.moreBox = moreBox;
        setLayout(new FlowLayout(FlowLayout.CENTER));
        add(moreBox);
        add(more);
        add(lessBox);
        add(less);
    }

    public void setStates(boolean m, boolean l)
    {
        lessBox.setState(l);
        moreBox.setState(m);
    }

    public boolean[] getStates()
    {
        boolean[] states = new boolean[2];
        states[0] = moreBox.getState();
        states[1] = lessBox.getState();
        return(states);
    }

    public double[] getValues()
    {
        double[] values = new double[2];
        values[0] = more.getValue();
        values[1] = less.getValue();
        return(values);
    }

    public void setValues(double m, double l)
    {
        less.setValue(l);
        more.setValue(m);
    }

    public void setValues(double m, double l, double le, double ue, int digits)
    {
        setValues(m, l, le, ue, digits, Math.max(digits-3,0));
    }

    public void setValues(double m, double l, double le, double ue, int digits, int decimals)
    {
        less.setValues(l, le, ue, digits, decimals);
        more.setValues(m, le, ue, digits, decimals);
    }

}
