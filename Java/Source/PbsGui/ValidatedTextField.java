/**
public class ValidatedTextField extends TextField

a TextField that validates input

@author P.B. Stark stark@stat.berkeley.edu
@version 1.0
@copyright 1997, 1998, P.B. Stark

Last modified 23 July 1998.

*/

package PbsGui;

import java.awt.*;

public class ValidatedTextField extends TextField
{
    private Validator validator;
    protected String lastValue;
    private int places;
    private static final int p = 15;

    public ValidatedTextField(String initVal, int places, Validator validator)
    {
        super(initVal, places+2);
        this.validator = validator;
        this.places = places;
    }

    public ValidatedTextField(String initVal, Validator validator)
    {
        this(initVal, p, validator);
    }

    public ValidatedTextField(Validator validator)
    {
        this("0.0", p, validator);
    }


    public ValidatedTextField(double initVal, int places, Validator validator)
    {
        this(Double.toString(initVal), places, validator);
    }

    public ValidatedTextField(int initVal, int places, Validator validator)
    {
        this( Integer.toString(initVal), places, validator);
    }

    public boolean handleEvent(Event e)
    {
        if (e.id != Event.ACTION_EVENT &&
                e.id != Event.LOST_FOCUS)
        {
            return(super.handleEvent(e));
        }
        else
        {
            String text = getText();
            if (text.equals(lastValue)) return(super.handleEvent(e));
            else
            {
                if (validator != null)
                {
                    if (!validator.isValid(text))
                    {
                        super.selectAll();
                        return(true);
                    }
                }
                e.arg = new String(text);
                e.id = Event.SCROLL_ABSOLUTE;
                lastValue = text;
                return(super.handleEvent(e));
            }
        }
    }

    public double getDoubleValue()
    {
        return((new Double(lastValue)).doubleValue());
    }

    public int getIntegerValue()
    {
        return ((new Integer(lastValue)).intValue());
    }

    public void setDoubleValue(double val)
    {
        lastValue = Double.toString(val);
        setText(lastValue);
    }

    public void setIntegerValue(int val)
    {
        lastValue = Integer.toString(val);
        setText(lastValue);
    }

    public String getValue()
    {
        return(lastValue);
    }

    public void setValue(String s)
    {
        lastValue = s;
        setText(s);
    }

    public int getPlaces()
    {
        return(places);
    }



} // ends ValidatedTextField