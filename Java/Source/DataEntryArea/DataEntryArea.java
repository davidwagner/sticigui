/*
Class DataEntryArea
Combines a Validated TextField with a TextArea to input and display data

 */

import java.awt.*;
import java.applet.*;

public class DataEntryArea extends Panel implements ComboInput
{
    private TextArea textArea;
    private ValidatedTextField vTextField;
    private Label label;
    private Validator validator;

    private ValidatingTextField vTextField;
    private Validator validator;
    private Label label;
    private double value;
    private double[] values;
    private int places;               // number of digits in TextField

    TextBar(double value, double minValue, double maxValue, int places,
            Validator validator, int orient, String labelString)
    {
        this.minValue = minValue;
        this.maxValue = maxValue;
        value = validator.makeValid(value);
        this.value = value;
        this.places = places;
        this.validator = validator;
        label = new Label(labelString);

        setLayout(new BorderLayout());
        tBar = new TBar(this, value, 0, scale);
        vTextField = new ValidatingTextField(
                         (ComboInput) this, value, places,
                                                validator);
        Panel bPanel = new Panel();
        if (labelString != null)
        {
            bPanel.setLayout(new GridLayout(2,1));
            Panel cPanel = new Panel();
            cPanel.add(label);
            cPanel.add(vTextField);
            Panel dPanel = new Panel();
            dPanel.add(tBar);
            if (orient == BAR_TOP)
            {
                bPanel.add(dPanel);
                bPanel.add(cPanel);
            }
            else
            {
                bPanel.add(cPanel);
                bPanel.add(dPanel);
            }
        }
        else
        {
            bPanel.setLayout(new FlowLayout());
            if (orient == BAR_FIRST)
            {
                bPanel.add(tBar);
                bPanel.add(vTextField);
            }
            else
            {
                bPanel.add(vTextField);
                bPanel.add(tBar);
            }
        }
        Panel xPanel = new Panel();
        xPanel.setLayout(new BorderLayout());
        xPanel.add("South", bPanel);
        add("Center",xPanel);

        setTBar();
        setVTextField();
    }

    TextBar(double value, double minValue, double maxValue, int places,
            Validator validator)
    {
        this(value, minValue, maxValue, places, validator, TEXT_FIRST, null);
    }

    TextBar(double value, double minValue, double maxValue, int places,
            Validator validator, int orient)
    {
        this(value, minValue, maxValue, places, validator, orient, null);
    }

    public double getValue()
    {
        return(value);
    }

    public double[] getValues()
    {
        return(values);
    }

    public void setValue(double newVal)
    {
        value = validator.makeValid(newVal);
        if (value < minValue) { value = minValue;}
        if (value > maxValue) { value = maxValue;}
        setTBar();
        setVTextField();
    }

    public void setValues(double value, double minValue, double maxValue,
                          int places)
    {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.places = places;
        this.value = value;
        this.value = validator.makeValid(this.value);
        if (this.value < minValue) { this.value = minValue;}
        if (this.value > maxValue) { this.value = maxValue;}
        setVTextField();
        setTBar();
    }

    public void updateValue(Object source, Event evt, String newVal)
    {
        value = Double.valueOf(newVal).doubleValue();
        value = validator.makeValid(value);
        if (value < minValue) { value = minValue;}
        if (value > maxValue) { value = maxValue;}
        setTBar();
        setVTextField();
        if (source == vTextField) {evt.id = Event.SCROLL_ABSOLUTE;}
        Event e = cloneEvent(evt, evt.id,
                             new Double(newVal));
        deliverEvent(e);
    }

    private Event cloneEvent(Event e, int id, Object o)
    {
        Event ec = new Event(this, id, o);
        ec.x = e.x;
        ec.y = e.y;
        ec.when = e.when;
        return(ec);
    }

    private void setTBar()
    {
        tBar.setValue((int)(scale*(value - minValue)/(maxValue - minValue)));
    }

    private void setVTextField()
    {
        String sValue = (new Double(value)).toString();
        int vLength = sValue.length();
        vTextField.setText(sValue.substring(0,Math.min(vLength,places)));
    }

    public void setBackground(Color color)
    {
        super.setBackground(color);
        setBkgndKids(this, color);
    }

    private void setBkgndKids(Container parent, Color color)
    {
        Component kids[] = parent.getComponents();
        for (int i = 0; i < kids.length; i++)
        {
            kids[i].setBackground(color);
            if (kids[i] instanceof Container)
            {
                setBkgndKids((Container)kids[i], color);
            }
        }
    }

    public void setForeground(Color color)
    {
        super.setForeground(color);
        setFrgndKids(this, color);
    }

    private void setFrgndKids(Container parent, Color color)
    {
        Component kids[] = parent.getComponents();
        for (int i = 0; i < kids.length; i++)
        {
            kids[i].setForeground(color);
            if (kids[i] instanceof Container)
            {
                setFrgndKids((Container)kids[i], color);
            }
        }
    }

    public void setFont(Font font)
    {
        vTextField.setFont(font);
        textArea.setFont(font);
    }
}
