/**
package PbsGui;
public class IntegerValidator extends Object implements Validator

a validator for integer input

@author P.B. Stark stark@stat.berkeley.edu
@version 1.0

Last modified 8 August 1997

*/

package PbsGui;

public class IntegerValidator extends Object implements Validator
{
    public boolean isValid(String s)
    {
        try
        {
            Integer.parseInt(s);
        }
        catch (NumberFormatException e)
        {
            return(false);
        }
        return(true);
    }

    public double makeValid(String s)
    {
        return((double) Integer.valueOf(s).intValue());
    }

    public double makeValid(Double d)
    {
        return(Math.round(d.doubleValue()));
    }

    public double makeValid(double d)
    {
        return(Math.round(d));
    }
}
