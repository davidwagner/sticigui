/**
package PbsGui;

public class DoubleValidator extends Object implements Validator

a validator for double input

@author P.B. Stark stark@stat.berkeley.edu
@version 1.0

Last modified 8 August 1997

*/

package PbsGui;

public class DoubleValidator extends Object implements Validator
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
        return(Double.valueOf(s).doubleValue());
    }

    public double makeValid(Double d)
    {
        return(d.doubleValue());
    }

    public double makeValid(double d)
    {
        return(d);
    }
}
