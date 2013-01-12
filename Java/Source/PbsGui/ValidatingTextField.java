/**
public class Validating TextField extends TextField

a TextField that validates input

@author P.B. Stark stark@stat.berkeley.edu
@version 1.0
@copyright 1997, 1998, P.B. Stark

Last modified 28 July 1998

*/

package PbsGui;

import java.awt.*;

public class ValidatingTextField extends TextField
{
    private ComboInput comboInput;
    private Validator validator;
    protected String lastValue;

    ValidatingTextField(ComboInput comboInput, String initVal,
               int places, Validator validator)
    {
        super(initVal, places+2);
        this.comboInput = comboInput;
        this.validator = validator;
    }

    ValidatingTextField(ComboInput comboInput, double initVal,
               int places, Validator validator)
    {
        this(comboInput, Double.toString(initVal), places,
               validator);
    }

    ValidatingTextField(ComboInput comboInput, int initVal,
               int places, Validator validator)
    {
        this(comboInput, Integer.toString(initVal), places,
               validator);
    }

    public boolean handleEvent(Event e)
    {
        if (e.id != Event.ACTION_EVENT && e.id != Event.LOST_FOCUS)
        {
            return(super.handleEvent(e));
        }
        else
        {
            String text = getText().trim();
            if (text.equals(lastValue))
            {
                return(super.handleEvent(e));
            }
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
                return(action(e,e.arg));
            }
        }
    }

    public boolean action(Event e, Object ob)
    {
        comboInput.updateValue(this, e, getText().trim());
        return(true);
    }


} // ends ValidatingTextField