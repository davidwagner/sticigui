// script logic.js :  build logical statements
// copyright 2000-2008. P.B. Stark, stark@stat.berkeley.edu
// Version 0.9
// All rights reserved.

// !!!!Beginning of the code!!!!

var logicModTime = '1/21/2009 14:20';

var parenSize = 0;
var binaryHtm = [
                 '',
                 '|',
                 '&amp;',
                 '&rarr;',
                 '&harr;'
                ];

var unaryHtm =  [
                 '',
                 '!'
                ];

var propHtm =   [
                 '<em>p</em>',
                 '<em>q</em>',
                 '<em>r</em>'
                ];

var twoByTwoTruthTables = [['F', 1],
                 ['<em>p</em>&nbsp;&amp;&nbsp;<em>q</em>', 2],  
                 ['!<em>p</em>&nbsp;&amp;&nbsp;<em>q</em>', 3],
                 ['<em>p</em>&nbsp;&amp;&nbsp;!<em>q</em>', 5],
                 ['!<em>p</em>&nbsp;&amp;&nbsp;!<em>q</em>', 7],
                 ['<em>q</em>', 2*3],
                 ['<em>p</em>', 2*5],
                 ['(<em>p</em>&nbsp;&amp;&nbsp;<em>q</em>)&nbsp;|&nbsp;' + 
                  '(!<em>p</em>&nbsp;&amp;&nbsp;!<em>q</em>)', 2*7],
                 ['(<em>p</em>&nbsp;&amp;&nbsp;!<em>q</em>)&nbsp;|&nbsp;' +
                  '(!<em>p</em>&nbsp;&amp;&nbsp;<em>q</em>)', 3*5],
                 ['!<em>p</em>', 3*7],
                 ['<em>p</em>&nbsp;|&nbsp;<em>q</em>', 2*3*5],
                 ['!<em>q</em>', 5*7],
                 ['!<em>p</em>&nbsp;|&nbsp;<em>q</em>', 2*3*7],
                 ['<em>p</em>&nbsp;|&nbsp;!<em>q</em>', 2*5*7],
                 ['!<em>p</em>&nbsp;|&nbsp;!<em>q</em>', 3*5*7],
                 ['T', 2*3*5*7]
                ];

var truthValues = [true, false];

function binaryCombo(p1, p2, dropProb) {
    if ( (typeof(dropProb) == 'undefined') || (dropProb == null) ) {
        dropProb = 0.4;
    }
    var which = listOfRandInts(1,1,binaryHtm.length-1)[0];
    var pNew = p1[0];
    var pNewJs = p1[1];
    var openPar = ' <font size="+' + parenSize + '">(</font>';
    var closePar = '<font size="+' + parenSize + '">)</font>';
    if (rand.next() > dropProb) {
        var op = openPar;
        var cp = closePar;
        if (p1[0].length <= propHtm[1].length) {
            op = '';
            cp = '';
        }
        pNew = op + p1[0] + cp + ' ' + binaryHtm[which] + ' ';
        if ( p2[0].length <= propHtm[1].length) {
            op = '';
            cp = '';
        } else {
            op = openPar;
            cp = closePar;
        }
        pNew += op + p2[0] + cp;
        if (which == 1) {                                       // OR
            pNewJs = '(' + p1[1] + ')||(' + p2[1] + ')';
        } else if (which == 2) {                                // AND
            pNewJs = '(' + p1[1] + ')&&(' + p2[1] + ')';
        } else if (which == 3) {                                // IMPLIES
            pNewJs = '(!(' + p1[1] + ')||(' + p2[1] + '))';
        } else if (which == 4) {                                // IFF
            pNewJs = '(((' + p1[1] + ')&&(' + p2[1] + '))||(!(' + p1[1] + ')&&!(' + p2[1] +
                      ')))';
        } else {
            alert('Error #1 in logic.binaryCombo(): Unsupported operator ' + binaryHtm[which] +
                  '!\n  Please report this to your instructor.');
        }
    } else {
        var which = rand.next();
        if (which > 0.5 ) {
            pNew = p2[0];
            pNewJs = p2[1];
        }
    }
    return([pNew,pNewJs]);
}

function unary(p1, dropProb) {
    if ( (typeof(dropProb) == 'undefined') || dropProb == null ) {
            dropProb = 0.5;
    }
    var which = listOfRandInts(1, 1, unaryHtm.length-1)[0];
    var pNew = p1[0];
    var pNewJs = p1[1];
    var openPar = '<font size="+' + parenSize + '">(</font>';
    var closePar = '<font size="+' + parenSize + '">)</font>';
    if (rand.next() > dropProb) {
        if (p1[0].length <= propHtm[1].length) {
            openPar = '';
            closePar = '';
        }
        pNew = unaryHtm[which] + openPar + p1[0] + closePar;
        pNewJs = '(' + p1[1] + ')';
        if (which == 0) {
        } else if (which == 1) {
            pNewJs = ' !' + pNewJs;
        } else {
            alert('Error #1 in logic.unary(): Unsupported operator ' + unaryHtm[which] +
                  '!\n Please report this to your instructor.');
        }
    }
    return([pNew,pNewJs]);
}

function randProp(depth,props) {
    if (typeof(props) == 'undefined' || props == null || props < 1) {
        props = 2;
    }
    var n = Math.pow(2,depth);
    var which = listOfRandInts(n,0,props-1);
    var pRaw = new Array(n);
    var pJs = new Array(n);
    var dum;
    for (var i = 0; i < n; i++) {
        pRaw[i] = propHtm[which[i]];
        pJs[i] = removeMarkup(pRaw[i]);
    }
    parenSize = 0;
    while (n >= 1 ) {
        n /= 2;
        for (var j=0; j < n; j++) {
            dum = binaryCombo(unary([pRaw[2*j],pJs[2*j]]), unary([pRaw[2*j+1],pJs[2*j+1]]) );
            pRaw[j] = dum[0];
            pJs[j] = dum[1];
        }
        parenSize++;
    }
    return([pRaw[0], pJs[0]]);
}


function buildLogicFn(str, name) {
    return('function ' + name + '(p, q) {return(' + str + ');}');
}

function wordsToLogic(str) { // turns "word" logic into math logic
    s = removeMarkup(removeNonLogicals(str));
    s = s.replace(/\[/gm,'(');
    s = s.replace(/\]/gm,')');
    s = s.replace(/\{/gm,'(');
    s = s.replace(/\}/gm,')');
//    s = s.replace(/AND/gim,'\&\&');
//    s = s.replace(/OR/gim,'\|\|');
    s = s.replace(/-/gim,'\!');
    s = s.replace(/~/gim,'\!');
    s = s.replace(/f/gim,' false ');
    s = s.replace(/t/gim,' true ');
    return(s);
}

function logicToWords(str) { // turns JavaScript logic into words
    s = str.replace(/\&\&/gim,' &amp; ');
    s = s.replace(/\|\|/gim,' | ');
    s = s.replace(/\!/gim,' !');
    return(s);
}

function wordsToLogicFunction(str, name, argStr) { // builds a logical function of argStr from str
    return('function ' + name + '(' + argStr + ') { return(' + wordsToLogic(str) + ');}');
}

function reduceLogic(str) { // simplifies logical expressions a bit [FIX ME!]
    s = removeAllBlanks(str);
    s = s.replace( /\(\(([^)]+)\)\)/ig, '($1)');
    s = s.replace( /\(([^)\&\|]+)\)/ig, '$1');
    s = s.replace( /!\(([^)\&\|]+)\)/ig, '!$1');
    s = s.replace( /!\(!\(([^)]+)\)\)/ig, '($1)');
    s = s.replace( /!!/ig, '');
    s = s.replace( /\(([^)\&\|]+)\)/ig, '$1');
    s = s.replace( /!\(!\(([^)]+)\)\)/ig, '($1)');
    s = s.replace( /\(\(([^)]+)\)\)/ig, '($1)');
    s = s.replace( /\(([a-z\&!\|]+)\)\&\&\(\1\)/ig, '$1');
    s = s.replace( /\(([a-z\&!\|]+)\)\|\|\(\1\)/ig, '$1');
    s = s.replace( /!\(([a-z\&!\|]+)\)\&\&\(\1\)/ig, 'false');
    s = s.replace( /\(([a-z\&!\|]+)\)\&\&!\(\1\)/ig, 'false');
    s = s.replace( /!\(([a-z\&!\|]+)\)\|\|\(\1\)/ig, 'true');
    s = s.replace( /\(([a-z\&!\|]+)\)\|\|!\(\1\)/ig, 'true');
    s = s.replace( /!\(false\)/ig, '(true)');
    s = s.replace( /!false/ig, 'true');
    s = s.replace( /!\(true\)/ig, '(false)');
    s = s.replace( /!true/ig, 'false');
    return(s);
}

function whichTwoByTwoTruthTable(s) {
    var ansStr = '';
    var whichTab = 1;
    try{
        eval('function ans(p,q) { return(' + strArr[1] + ');}');
    } catch(e) {
        alert('Error #1 in logic.whichTwoByTwoTruthTable(): expression does not parse.');
    }
    for (var i=0; i<truthValues.length; i++) { 
        for (j=0; j<truthValues.length; j++) {
           if (ans(truthValues[i], truthValues[j])) {
               whichTab = whichTab*primes[i+2*j];
            }
        }
     }
    for (var i=0; i<twoByTwoTruthTables.length; i++) {
        if (whichTab == twoByTwoTruthTables[i][1]) {
            ansStr = twoByTwoTruthTables[i][0];
        }
    }
    return(ansStr);
}





