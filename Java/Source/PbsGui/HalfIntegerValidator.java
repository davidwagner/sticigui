/**
package PbsGui;

public class HalfIntegerValidator extends Object implements Validator

a validator for half-integer input

@author P.B. Stark stark@stat.berkeley.edu
@version 1.0

Last modified 8 August 1997

*/

package PbsGui;

public class HalfIntegerValidator extends Object implements Validator
{
    public boolean isValid(String s)
    {
        try
        {
            Double.valueOf(s);
        }
        catch (NumberFormatException e)
        {
            return(false);
        }
        return(true);
    }

    public double makeValid(String s)
    {
        return(Math.round(Double.valueOf(s).doubleValue() + 0.5) - 0.5);
    }

    public double makeValid(Double d)
    {
        return(Math.round(d.doubleValue()+0.5) - 0.5);
    }

    public double makeValid(double d)
    {
        return(Math.round(d+0.5) - 0.5);
    }
}
