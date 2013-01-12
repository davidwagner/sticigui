/**
package PbsGui;
public class PositiveIntegerValidator extends Object implements Validator

a validator for nonnegative integer input

@author P.B. Stark stark@stat.berkeley.edu
@version 1.0

Last modified 11 October 1997

*/

package PbsGui;

public class PositiveIntegerValidator extends Object implements Validator
{
    public boolean isValid(String s)
    {
        int i;
        try
        {
            i = Integer.parseInt(s);
        }
        catch (NumberFormatException e)
        {
            return(false);
        }
        if (i >= 0)
        {
            return(true);
        }
        else
        {
            return(false);
        }
    }

    public double makeValid(String s)
    {
        return(Math.max((double) Integer.valueOf(s).intValue(), 0.0));
    }

    public double makeValid(Double d)
    {
        return(Math.max(Math.round(d.doubleValue()),0.0));
    }

    public double makeValid(double d)
    {
        return(Math.max(Math.round(d),0));
    }
}
