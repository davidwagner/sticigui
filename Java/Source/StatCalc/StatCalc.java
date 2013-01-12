/**
StatCalc.java
Statistical Calculator Applet
@author P.B. Stark
@version 0.9
@copyright 1997-2001 P.B. Stark. All rights reserved.
http://statistics.berkeley.edu/~stark/
Berkeley, CA, USA
Last modified 13 March 2001.
*/


import java.applet.*;
import java.awt.*;
import PbsStat.*;
import PbsGui.*;
import java.util.*;

public class StatCalc extends Applet
{

//
// Declare the class variables
//

double x = 0.0;
double y = 0.0;
double z = 0.0;
String[] buttonText = { "7", "8", "9", "/", "!", "nCm",
                        "4", "5", "6", "*", "U[0,1]", "N(0,1)",
                        "1", "2", "3", "-", "Sqrt", "x^2",
                        "0", "+/-", ".", "+", "1/x", "x^y",
                        "=", "CE", "C", "exp(x)", "log(x)", "log_y(x)",
                      };
String[] buttonType = { "num", "num", "num", "bin", "una", "bin",
                        "num", "num", "num", "bin", "una", "una",
                        "num", "num", "num", "bin", "una", "una",
                        "num", "chs", "num", "bin", "una", "bin",
                        "eq",  "CE",  "una", "una", "una", "bin",
                      };
Vector numberVec = new Vector(11);
Vector unaryVec = new Vector(10);
Vector binaryVec = new Vector(7);
Vector chsVec = new Vector(1);
Button[] myButton = new Button[buttonText.length];
Panel[] myPanel = new Panel[4];
boolean decimal = false;
boolean update = true;
boolean inputInProgress = true;
String fn = "blah";
int decimals = 12;
int places = 16;
ValidatedTextField display = new ValidatedTextField("0", places,
        (Validator) new DoubleValidator());

public void init()
{
    super.init();
    setBackground(Color.white);
    for (int i=0; i < buttonText.length; i++) {
        myButton[i] = new Button(buttonText[i]);
    }
    for (int i=0; i < myPanel.length; i++) {
        myPanel[i] = new Panel();
    }
	myPanel[0].setLayout(new GridLayout(5,6));
	for (int i=0; i < buttonText.length; i++) {
	    myPanel[0].add(myButton[i]);
	}
	myPanel[1].setLayout(new BorderLayout());
	myPanel[1].add("Center", display);
	myPanel[2].setLayout(new BorderLayout());
	myPanel[2].add("Center",myPanel[0]);

	setLayout(new BorderLayout());
	add("North", myPanel[1]);
	this.add("Center", myPanel[2]);

	for (int i=0; i < buttonType.length; i++) {
	    if (buttonType[i].equals("num")) {
	        numberVec.addElement(buttonText[i]);
	    } else if (buttonType[i].equals("una")) {
	        unaryVec.addElement(buttonText[i]);
	    } else if (buttonType[i].equals("bin")) {
	        binaryVec.addElement(buttonText[i]);
	    } else if (buttonType[i].equals("chs")) {
	        chsVec.addElement(buttonText[i]);
	    }
	}

	show();
}

public boolean handleEvent(Event e)
{
    if (e.id == Event.ACTION_EVENT)
    {
        if (numberVec.contains((String) e.arg)) number((String) e.arg);
        else if (unaryVec.contains((String) e.arg)) unary((String) e.arg);
        else if (binaryVec.contains((String) e.arg)) binary((String) e.arg);
        else if (e.arg.equals("=")) eq();
        else if (e.arg.equals("CE")) ce();
        else if (e.arg.equals("+/-")) chs();
	}
	else if (e.id == Event.SCROLL_ABSOLUTE) inputInProgress = true;
    return(super.handleEvent(e));
}

public void ce()
{
    decimal = false;
    update = true;
    display.setValue("0");
}

public void eq()
{  // the equals key has been clicked
    if (inputInProgress) y = Format.strToDouble(display.getValue(), 0.0);
    if (Double.isNaN(x) || Double.isNaN(y)) z = Double.NaN;
    else
    {
    	if (fn.equals("+")) z = x + y;
    	else if (fn.equals("-")) z = x - y;
    	else if (fn.equals("*")) z = x * y;
    	else if (fn.equals("/")) z = x / y;
    	else if (fn.equals("x^y")) z = Math.pow(x, y);
    	else if (fn.equals("nCm"))
    	     z = PbsStat.binomialCoef((int) Math.round(x), (int) Math.round(y));
    	else if (fn.equals("log_y(x)"))
    	{
    	    if (x > 0 && y > 0) z = Math.log(x)/Math.log(y);
    	    else z = Double.NaN;
    	}
    	else if (fn.equals("blah")) z = y;
    }
	display.setValue(Format.numToString(z, places, decimals, true, true));
	x = z;
	y = z;
	update = true;
	decimal = false;
	fn = "blah";
}


public void number(String arg)
{   // a number has been clicked with the mouse
     inputInProgress = true;
     if (update)
     {
        if (arg.equals("."))
        {
            decimal = true;
            display.setValue("0.");
        }
        else display.setValue(arg);
        update = false;
     }
     else
     {
        if (arg.equals("."))
        {
            if (decimal) {}
            else display.setValue(display.getValue() + ".");
            decimal = true;
        }
        else display.setValue(display.getValue() + arg);
     }

}   // ends numberClicked()

public void chs()
{ // change the sign
    update = false;
    if (!inputInProgress) x = -x;
    if (display.getValue() == null)
    {
        display.setValue("-");
        inputInProgress = true;
    }
    else
    {
        if (display.getValue().indexOf('-') < 0)
        {
            display.setValue("-" + display.getValue());
        }
        else
        {
            display.setValue(display.getValue().substring(1,display.getValue().length()));
        }
    }
}


public void unary( String fun )
{
	if (inputInProgress)
	{
	    x = Format.strToDouble(display.getValue());
	    inputInProgress = false;
	}
    if (fun.equals("C"))
	{
	    x = 0.0;
	    y = 0.0;
		fn = "blah";
	}
	else if (Double.isNaN(x)) y = Double.NaN;
    else if (fun.equals("x^2")) y =  x * x ;
	else if (fun.equals("Sqrt")) y = Math.sqrt(x);
	else if (fun.equals("1/x")) y = 1./x;
	else if (fun.equals("!")) y = PbsStat.factorial((int) Math.floor(x));
	else if (fun.equals("N(0,1)")) y = PbsStat.rNorm();
	else if (fun.equals("U[0,1]")) y = Math.random();
	else if (fun.equals("log(x)"))
	{
	    if (x > 0)  y = Math.log(x);
	    else y = Double.NaN;
	}
	else if (fun.equals("exp(x)")) y = Math.exp(x);
	display.setValue(Format.numToString(y, places, decimals, true, true));
	x = y;
	update = true;
	decimal = false;
}

public void binary(String arg)
{
	eq();
	fn = arg;
	if (inputInProgress)
	{
	    x = Format.strToDouble(display.getValue(), 0);
	    inputInProgress = false;
	}
	update = true;
}


} // ends class StatCalc

