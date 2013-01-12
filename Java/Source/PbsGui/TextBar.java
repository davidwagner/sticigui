/**
package PbsGui
public class TextBar extends Panel implements ComboInput

an input widget that combines a validated text area, a label, and a small
scrollbar

@author P.B. Stark stark@stat.berkeley.edu
@version 1.0
@copyright 1997, 1998, 1999, 2000. P.B. Stark. All rights reserved.

Last modified 29 September 2000

*/

package PbsGui;
import java.awt.*;

public class TextBar extends Panel implements ComboInput {
    private int scale = 250; // convert to ScrollBar units
    public static final int TEXT_FIRST = 0;
    public static final int BAR_FIRST = 1;
    public static final int TEXT_TOP = 2;
    public static final int BAR_TOP = 3;
    public static final int NO_BAR = 4;

    private TBar tBar;
    private ValidatingTextField vTextField;
    private Validator validator;
    private Label label;
    private double minValue;
    private double maxValue;
    private double value;
    private int digits;               // number of digits in TextField
    private int decimals;             // number of places beyond the decimal in TextField
    private boolean eNotation;        // use exponential notation in the text field?

    public TextBar(double value, double minValue, double maxValue, int digits, int decimals,
            boolean eNotation, Validator validator, int orient, String labelString)
    {
        this.minValue = minValue;
        this.maxValue = maxValue;
        value = validator.makeValid(value);
        this.value = value;
        this.digits = digits;
        this.decimals = decimals;
        this.eNotation = eNotation;
        this.validator = validator;
        label = new Label(labelString);

        setLayout(new BorderLayout());
        tBar = new TBar(this, value, 0, scale);
        vTextField = new ValidatingTextField(
                         (ComboInput) this, value, digits, validator);
        Panel bPanel = new Panel();
        if (labelString != null)  {
            Panel cPanel = new Panel();
            cPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            cPanel.add(label);
            cPanel.add(vTextField);
            Panel dPanel = new Panel();
            dPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            dPanel.add(tBar);
            if (orient == TEXT_FIRST) {
                bPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
                bPanel.add(cPanel);
                bPanel.add(dPanel);
            } else if (orient == BAR_TOP) {
                bPanel.setLayout(new GridLayout(2,1));
                bPanel.add(dPanel);
                bPanel.add(cPanel);
            } else if (orient == TEXT_TOP) {
                bPanel.setLayout(new GridLayout(2,1));
                bPanel.add(cPanel);
                bPanel.add(dPanel);
            } else if (orient == NO_BAR) {
                bPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
                bPanel.add(cPanel);
            }

        } else {
            bPanel.setLayout(new FlowLayout());
            if (orient == BAR_FIRST) {
                bPanel.add(tBar);
                bPanel.add(vTextField);
            } else if (orient == TEXT_FIRST) {
                bPanel.add(vTextField);
                bPanel.add(tBar);
            } else if (orient == NO_BAR) {
                bPanel.add(vTextField);
            }
        }
        add("Center",bPanel);

        setTBar();
        setVTextField();
    }

    public TextBar(double value, double minValue, double maxValue, int digits, int decimals,
            Validator validator, int orient, String labelString) {
        this(value, minValue, maxValue, digits, decimals, false, validator, orient, labelString);
    }

    public TextBar(double value, double minValue, double maxValue, int digits,
            int decimals, Validator validator) {
        this(value, minValue, maxValue, digits, decimals, false,
             validator, TEXT_FIRST, null);
    }

    public TextBar(double value, double minValue, double maxValue, int digits,
            Validator validator, int orient, String labelString) {
        this(value, minValue, maxValue, digits, Math.max(0,digits-3), false, validator,
             orient, labelString);
    }


    public TextBar(double value, double minValue, double maxValue, int digits,
            Validator validator) {
        this(value, minValue, maxValue, digits, Math.max(0,digits-3), false,
             validator, TEXT_FIRST, null);
    }

    public TextBar(double value, double minValue, double maxValue, int digits,
            int decimals, Validator validator, int orient) {
        this(value, minValue, maxValue, digits, decimals, false,
             validator, orient, null);
    }

    public TextBar(double value, double minValue, double maxValue, int digits,
            Validator validator, int orient) {
        this(value, minValue, maxValue, digits, Math.max(0,digits-3), false,
             validator, orient, null);
    }

    public double getMinValue() {
        return(minValue);
    }

    public double getMaxValue() {
        return(maxValue);
    }

    public int getScale() {
        return(scale);
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public double getValue() {
        return(value);
    }

    public void setValue(double newVal) {
        value = validator.makeValid(newVal);
        if (value < minValue) { value = minValue;}
        if (value > maxValue) { value = maxValue;}
        setTBar();
        setVTextField();
    }

    public void setValues(double value, double minValue, double maxValue,
                          int digits, int decimals) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.digits = digits;
        this.decimals = decimals;
        this.value = value;
        this.value = validator.makeValid(this.value);
        if (this.value < minValue) { this.value = minValue;}
        if (this.value > maxValue) { this.value = maxValue;}
        setVTextField();
        setTBar();
    }

    public void setValues(double value, double minValue, double maxValue,
                          int digits) {
        setValues(value, minValue, maxValue, digits, Math.max(0,digits-3));
    }


    public void updateValue(Object source, Event evt, String newVal) {
        value = Format.strToDouble(newVal);
        value = validator.makeValid(value);
        if (value < minValue) { value = minValue;}
        if (value > maxValue) { value = maxValue;}
        setVTextField(); // note: this can change value---rounds in the process!
        setTBar();
        if (source == vTextField) {evt.id = Event.SCROLL_ABSOLUTE;}
        Event e = cloneEvent(evt, evt.id,
                             new Double(newVal));
        deliverEvent(e);
    }

    private Event cloneEvent(Event e, int id, Object o) {
        Event ec = new Event(this, id, o);
        ec.x = e.x;
        ec.y = e.y;
        ec.when = e.when;
        return(ec);
    }

    private void setTBar() {
        tBar.setValue((int)(scale*(value - minValue)/(maxValue - minValue)));
    }

    private void setVTextField() { // round to the precision that will be displayed
        vTextField.setText(Format.numToString(value, digits, decimals, true, eNotation));
        value = Format.strToDouble(vTextField.getText());
        vTextField.lastValue = vTextField.getText();
    }

    public void setBackground(Color color) {
        super.setBackground(color);
        setBkgndKids(this, color);
    }

    private void setBkgndKids(Container parent, Color color) {
        Component kids[] = parent.getComponents();
        for (int i = 0; i < kids.length; i++) {
            kids[i].setBackground(color);
            if (kids[i] instanceof Container) {
                setBkgndKids((Container)kids[i], color);
            }
        }
    }

    public void setForeground(Color color) {
        super.setForeground(color);
        setFrgndKids(this, color);
    }

    private void setFrgndKids(Container parent, Color color) {
        Component kids[] = parent.getComponents();
        for (int i = 0; i < kids.length; i++) {
            kids[i].setForeground(color);
            if (kids[i] instanceof Container) {
                setFrgndKids((Container)kids[i], color);
            }
        }
    }

    public void setFont(Font font) {
        vTextField.setFont(font);
    }
}
