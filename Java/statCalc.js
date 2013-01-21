/*
Calculator with statistical functions

copyright (c) 2013 by P.B. Stark
License: AGPL

Dependencies: irGrade,js, jQuery

*/

function statCalc(container_id, params) {
    var self = this;
    this.container = $('#' + container_id);

    if (!params instanceof Object) {
       console.error('statCalc parameters should be an object');
       return;
    }

// default options
    this.options = {
                    buttons: 'all',
                    decimals: 12,
                    places: 16,
                    buttonsPerRow: 6
    };

// Override options
    jQuery.extend(this.options, params);

    self.x = 0.0;
    self.y = 0.0;
    self.z = 0.0;
    var buttonText = [ "7", "8", "9", "/", "!", "nCm",
                    "4", "5", "6", "*", "U[0,1]", "N(0,1)",
                    "1", "2", "3", "-", "Sqrt", "x^2",
                    "0", "+/-", ".", "+", "1/x", "x^y",
                    "=", "CE", "C", "exp(x)", "log(x)", "log_y(x)"
                    ];
    var buttonType = [ "num", "num", "num", "bin", "una", "bin",
                    "num", "num", "num", "bin", "una", "una",
                    "num", "num", "num", "bin", "una", "una",
                    "num", "chs", "num", "bin", "una", "bin",
                    "eq",  "CE",  "una", "una", "una", "bin"
                    ];
    self.numberVec = new Array(self.decimals);
    self.decimal = false;
    self.update = true;
    self.inputInProgress = true;
    self.fn = "blah";

    function initCalc() {
        var o = $('<div />').addClass('calc');
        self.container.append(o);
        // display
        self.theDisplay = $('<input type="text" />');
        o.append(self.theDisplay);
        // buttons
        self.buttonDiv = $('<div />').addClass('buttonDiv');
        self.buttonTable = $('<table />').addClass('buttonTable');
        self.buttonDiv.append(self.buttonTable);
        $.each(buttonText, function(index, v) {
                self.buttonTable.append('<input type="button" value=' + v + '>')
                              .button()
                              .addClass(buttonType[index])
                              .addClass('calcButton')
                              .click( function() {buttonClick(v, buttonType[index])});
        })
        o.append(self.buttonDiv);
    }
    initCalc();

//  action functions

    function buttonClick(v, opType) {
        try {
            switch(opType) {
               case 'num':
                   var t = self.theDisplay.val().replace(/[a-z, *+\/]+/gi,'');
                   t = (t=='0') ? t+v : v;
                   self.theDisplay.val(t);
                   break;
               case 'una':
                   switch(v) {
                      case '+/-':
                         var t = self.theDisplay.val().replace(/[a-z, *+\/]+/gi,'');
                         t = (t.indexOf('-') > -1) ? t.substring(1) : t;
                         self.theDisplay.val(t);
                         break;
                      case '!':
                         var t = self.theDisplay.val().replace(/[a-z, *+\/]+/gi,'');
                         var ans = factorial(t);
                         self.theDisplay.val(t.toString());
                         break;
                      case 'sqrt':
                         var t = self.theDisplay.val().replace(/[a-z, *\-+\/]+/gi,'');
                         var ans = Math.sqrt(t);
                         self.theDisplay.val(t.toString());
                         break;
                      case 'x^2':
                         var t = self.theDisplay.val().replace(/[a-z, *\-+\/]+/gi,'');
                         var ans = x*x;
                         self.theDisplay.val(t.toString());
                         break;
                      case 'exp(x)':
                         var t = self.theDisplay.val().replace(/[a-z, *\-+\/]+/gi,'');
                         var ans = Math.exp(x);
                         self.theDisplay.val(t.toString());
                         break;
                      case '1/x':
                         var t = self.theDisplay.val().replace(/[a-z, *\-+\/]+/gi,'');
                         var ans = 1/x;
                         self.theDisplay.val(t.toString());
                         break;
                      case 'U[0,1]':
                         self.theDisplay.val(rand.next());
                         break;
                      case 'CE':
                         self.theDisplay.val('');
                         break;
                      case 'C':
                         self.x = 0;
                         self.y = 0;
                         self.theDisplay.val('0')
                    }
                    break;
                default:
                    console.log('unexpected button in statCalc');
            }
        } catch(e) {
           console.log(e);
           self.theDisplay.val('NaN');
        }
    }


    $(document).ready(function() {
         $('.calcButton.num').click(function() {

               });
    });

}

/*



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

this.eq = function ()
{  // the equals key has been clicked
    if (inputInProgress) self.y = 0.0;
    if (Double.isNaN(x) || Double.isNaN(y)) z = Double.NaN;
    else
    {
    	case (fn) if (fn.equals("+")) z = x + y;
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
var strEmpty = "";

// main functions
$(document).ready(function () {

    $("div#keyPad button.keyPad_btnNormal").click(function () {
        var btn = $(this).html();
        $(keyPad_UserInput).val($(keyPad_UserInput).val() + btn);
        $(keyPad_UserInput).focus();

    });

    // CLEAR LAST CHAR IN INPUT TBOX
    $("div#keyPad button.keyPad_btnBack").click(function () {
        var str = $(keyPad_UserInput).val();
        $(keyPad_UserInput).val(str.substring(0, str.length - 1));
        $(keyPad_UserInput).focus();

    });

    // CLEAR ENTIRE INPUT TBOX
    $("div#keyPad button.keyPad_btnClr").click(function () {
        $(keyPad_UserInput).val(strEmpty);
        $(keyPad_UserInput).focus();

    });

    // SPACE BAR BUTTON
    $("div#keyPad button.keyPad_btnSpace").click(function () {
        $(keyPad_UserInput).val($(keyPad_UserInput).val() + " ");
        $(keyPad_UserInput).focus();
    });

    // FROM INPUT BOX TO MEM
    $("div#keyPad button#keyPad_btnToMem").click(function () {
        $(keyPad_Mem).val($(keyPad_UserInput).val());
        $(keyPad_UserInput).val(strEmpty);
        $(keyPad_UserInput).focus();
    });

    // FROM MEM TO INPUT BOX
    $("div#keyPad button#keyPad_btnFromMem").click(function () {
        $(keyPad_UserInput).val($(keyPad_UserInput).val() + $(keyPad_Mem).val());
        $(keyPad_Mem).val(strEmpty);
        $(keyPad_UserInput).focus();
    });

    // CALCULATE 4 ARITHMETIC OPERATIONS
    $("button#keyPad_btnEnter").click(function () {
        var inputBox = $(keyPad_UserInput);
        var arrVal;
        var x1;
        var x2;
        var retVal = "ERROR! CHECK INPUT";

        // VALIDATE INPUT USING SPLIT FUNCTION AND REGULAR EXPRESSION
        arrVal = inputBox.val().split(/[+-\/*]+/);
        if (arrVal.length > 2) { inputBox.val(retVal); return; }

        // parse to get 2 operands
        x1 = parseFloat(arrVal[0]);
        x2 = parseFloat(arrVal[1]);

        // "+"
        if (inputBox.val().indexOf('+') >= 0) { retVal = x1 + x2; }
        // "-"
        else if (inputBox.val().indexOf('-') >= 0) { retVal = x1 - x2; }
        // "*"
        else if (inputBox.val().indexOf('*') >= 0) { retVal = x1 * x2; }
        // "/"
        else if (inputBox.val().indexOf('/') >= 0) { retVal = x1 / x2; }
        else { }

        inputBox.val(retVal);
        inputBox.focus();
    });

    // FUNCTION KEYS' EVENT HANDLER
    $("button.keyPad_btnCommand").click(function () {
        var inputBox = $(keyPad_UserInput);
        var x = parseFloat(inputBox.val());
        var retVal = "ERROR";

        switch (this.id) {
            case 'keyPad_btnInverseSign': retVal = -x; break;       // +/-
            case 'keyPad_btnInverse': retVal = 1 / x; break;        // 1/X
            case 'keyPad_btnSquare': retVal = x * x; break;         // X^2
            case 'keyPad_btnSquareRoot': retVal = Math.sqrt(x); break;  // SQRT(X)
            case 'keyPad_btnCube': retVal = x * x * x; break;       // X^3
            case 'keyPad_btnCubeRoot': var tmp = 1 / 3; retVal = Math.pow(x, tmp); break; // POW (X, 1/3)
            case 'keyPad_btnLog': retVal = Math.log(x); break;      // LOG (N) - NATURAL
            case 'keyPad_btnExp': retVal = Math.exp(x); break;      // E^(X)
            case 'keyPad_btnSin': retVal = Math.sin(x); break;      // SIN(X)
            case 'keyPad_btnCosin': retVal = Math.cos(x); break;    // COS(X)
            case 'keyPad_btnTg': retVal = Math.tan(x); break;       // TAN(X)
            case 'keyPad_btnCtg': retVal = 1 / Math.tan(x); break;  // CTG(X)
            default: break;
        }
        inputBox.val(retVal);
        inputBox.focus();
    });
})

*/
