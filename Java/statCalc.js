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
                    buttons: [ ["7","num"], ["8","num"],   ["9","num"], ["/","bin"], ["!","una"], ["nCk","bin"],
                               ["4","num"], ["5","num"],   ["6","num"], ["*","bin"], ["U[0,1]","una"], ["N(0,1)","una"],
                               ["1","num"], ["2","num"],   ["3","num"], ["-","bin"], ["Sqrt","una"], ["x^2","una"],
                               ["0","num"], ["+/-","una"], [".","num"], ["+","bin"], ["1/x","una"], ["x^y","bin"],
                               ["=","eq"],  ["CE","una"],  ["C","una"] //, "exp(x)", "log(x)", "log_y(x)"
                              ],
                    digits: 20,
                    buttonsPerRow: 6
    };

// Extend options
    $.extend(this.options, params);

    self.x = 0.0;
    self.inProgress = false;
    self.currentOp = null;

    function initCalc() {
        var me = $('<div />').addClass('calc');
        self.container.append(me);
        // display
        self.theDisplay = $('<input type="text" />').attr('size',self.options['digits']);
        me.append(self.theDisplay);
        // buttons
        self.buttonDiv = $('<div />').addClass('buttonDiv');
        self.buttonTable = $('<table />').addClass('buttonTable');
        self.buttonDiv.append(self.buttonTable);
        $.each(self.options['buttons'], function(i, v) {
                newBut = $('<input type="button" value=' + v[0] + '>')
                              .button()
                              .addClass(v[1])
                              .addClass('calcButton')
                              .click( function() {buttonClick(v[0], v[1])});
                self.buttonTable.append(newBut);
        })
        me.append(self.buttonDiv);
    }
    initCalc();

//  action functions

    function buttonClick(v, opType) {
        var t = self.theDisplay.val().replace(/[a-z, *+\/]+/gi,'').replace(/^0+/,'');
        try {
            switch(opType) {
               case 'num':
                   self.theDisplay.val(t+v);
                   break;

               case 'una':
                   switch(v) {
                      case '+/-':
                         t = (t.indexOf('-') > -1) ? t.substring(1) : '-'+t;
                         self.theDisplay.val(t);
                         break;
                      case '!':
                         self.theDisplay.val(factorial(t).toString());
                         break;
                      case 'Sqrt':
                         self.theDisplay.val(Math.sqrt(t).toString());
                         break;
                      case 'x^2':
                         self.theDisplay.val((t*t).toString());
                         break;
                      case 'exp(x)':
                         self.theDisplay.val(Math.exp(t).toString());
                         break;
                      case 'ln(x)':
                         self.theDisplay.val(Math.log(t).toString());
                         break;
                      case '1/x':
                         self.theDisplay.val((1/t).toString());
                         break;
                      case 'U[0,1]':
                         self.theDisplay.val(rand.next());
                         break;
                      case 'N(0,1)':
                         self.theDisplay.val(rNorm());
                         break;
                      case 'CE':
                         self.theDisplay.val('');
                         break;
                      case 'C':
                         self.x = 0;
                         self.inProgress = false;
                         self.currentOp = null;
                         self.theDisplay.val('0')
                    }
                    break;

               case 'bin':
                   if (self.inProgress) {
                        self.x = doBinaryOp(self.x, self.currentOp, t);
                        self.theDisplay.val(self.x);
                   } else {
                        self.x = t;
                        self.theDisplay.val('');
                        self.inProgress = true;
                   }
                   self.currentOp = v;
                   break;

               case 'eq':
                   if (self.inProgress) {
                        self.x = doBinaryOp(self.x, self.currentOp, t);
                        self.theDisplay.val(self.x);
                        self.inProgress = false;
                        self.currentOp = null;
                   }
                   break;

               default:
                   console.log('unexpected button in statCalc ' + v);
            }
        } catch(e) {
           console.log(e);
           self.theDisplay.val('NaN');
        }
    }

    function doBinaryOp(x, op, y) {
         var res = Math.NaN;
         try {
             switch(op) {
                 case '+':
                     res = x+y;
                     break;
                 case '-':
                     res = x-y;
                     break;
                 case '*':
                     res = x*y;
                     break
                 case 'x^y':
                     res = x^y;
                     break;
                 case 'nCk':
                     res = binomialCoef(x, y);
                     break;
                 default:
                     console.log('unexpected binary function in statCalc ' + op);
              }
        } catch(e) {}
        return(res);
    }




}