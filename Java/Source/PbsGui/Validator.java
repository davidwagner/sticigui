/**
package PbsGui;

public  interface Validator

interface for a numerical validator

@author P.B. Stark stark@stat.berkeley.edu
@version 1.0

Last modified 8 August 1997

*/

package PbsGui;

public interface Validator
{
    public boolean isValid(String s);
    public double makeValid(String s);
    public double makeValid(double d);
    public double makeValid(Double d);
}