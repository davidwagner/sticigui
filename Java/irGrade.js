// script irGrade:  interactive, real-time grading; html formatting; statistical functions,
//                  linear algebra
// copyright 1997-2013. P.B. Stark, statistics.berkeley.edu/~stark
// Version 2.3
// All rights reserved.

// !!!!Beginning of the code!!!!

var irGradeModTime = '2013/1/19/1217'; // modification date and time
var today = (new Date()).toLocaleString();
var copyYr = '1997&ndash;2013. ';  // copyright years
var sticiRelPath = '.';            // relative path to the root of SticiGui
var courseBase = './Courses/';     // base for looking for course-specific files
var cssBase = '/Graphics/sticiGuiDefault.css';  // css file
var graderActionURL = 'http://statistics.berkeley.edu/cgi-bin/grader5'; // URL of grading scripts
var chapterTitles = [
                         ['Preface','preface'],
                         ['Introduction','howto'],
                         ['Reasoning and Fallacies','reasoning'],
                         ['Statistics','histograms'],
                         ['Measures of Location and Spread','location'],
                         ['Multivariate Data and Scatterplots','scatterplots'],
                         ['Association','association'],
                         ['Correlation and Association','correlation'],
                         ['Computing the Correlation Coefficient','computeR'],
                         ['Regression','regression'],
                         ['Regression Diagnostics','regressionDiagnostics'],
                         ['Errors in Regression','regressionErrors'],
                         ['Counting','counting'],
                         ['The Meaning of Probability: Theories of probability','probabilityPhilosophy'],
                         ['Set Theory: The Language of Probability','sets'],
                         ['Categorical Logic','categoricalLogic'],
                         ['Propositional Logic','logic'],
                         ['Probability: Axioms and Fundaments','probabilityAxioms'],
                         ['The &quot;Let\'s Make a Deal&quot; (Monty Hall) Problem','montyHall'],
                         ['Probability Meets Data','montyHallTest'],
                         ['Random Variables and Discrete Distributions','randomVariables'],
                         ['The Long Run and the Expected Value','expectation'],
                         ['Standard Error','standardError'],
                         ['The Normal Curve, the Central Limit Theorem, and Markov\'s' +
                          ' and Chebychev\'s Inequalities for Random Variables','clt'],
                         ['Sampling','sampling'],
                         ['Estimating Parameters from Simple Random Samples','estimation'],
                         ['Confidence Intervals','confidenceIntervals'],
                         ['Hypothesis Testing: Does Chance explain the Results?','testing'],
                         ['Does Treatment Have an Effect?','experiments'],
                         ['Testing Equality of Two Percentages','percentageTests'],
                         ['Approximate Hypothesis Tests: the <em>z</em> Test and the <em>t</em> Test',
                               'zTest'],
                         ['The Multinomial Distribution and the Chi-Squared Test for Goodness of Fit',
                               'chiSquare'],
                         ['A Case Study in Natural Resource Legislation','abalone']
                    ];
var assignmentTitles = [
                         ['Have you read the syllabus and instructions?', // topic
                          'Have you read the syllabus and instructions?', // title
                          'syllabus',  // assignment link
                          'howto' // chapter link(s)
                         ],
                         ['Mathematical prerequisites',
                          'Mathematical prerequisites',
                          'prerequisites',
                          'preface'
                         ],
                         ['Reasoning and Fallacies',
                          'Reasoning and Fallacies',
                          'reasoning',
                          'reasoning'
                         ],
                         ['Data taxonomy, histograms and percentiles',
                          'Data taxonomy, histograms and percentiles',
                          'histograms',
                          'histograms'
                         ],
                         ['Measures of location and spread',
                          'Measures of location and spread: mean, median, mode, IRQ, ' +
                               'range, SD, Markov\'s and Chebychev\'s Inequalities',
                          'location',
                          'location'
                         ],
                         ['Multivariate Data, Scatterplots, and Association',
                          'Scatterplots and association: reading scatterplots, linearity ' +
                                'and nonlinearity, homoscedasticity and heteroscedasticity, outliers',
                          'scatterplots',
                          'scatterplots,association'
                         ],
                         ['Correlation and Association',
                          'Correlation: eyeball estimates of <em>r</em>, heuristics, facts, standard ' +
                          'units, computing the correlation coefficient',
                          'correlation',
                         'correlation,computeR'
                         ],
                         ['Regression',
                          'Regression: the equation of the regression line, interpolation, extrapolation',
                          'regression',
                          'regression'
                         ],
                         ['Regression Diagnostics, Errors in Regression',
                          'Regression diagnostics and regression errors: residual plots, ' +
			            'regression fallacy, rms error of regression',
                          'regressionErrors',
                          'regressionDiagnostics,regressionErrors'
                         ],
                         ['Counting',
                          'Counting: fundamental rule of counting, combinations, permutations',
                          'counting',
                          'counting'
                         ],
                         ['The Meaning of Probability: Theories of Probability',
                          'The Meaning of Probability: Theories of Probability',
                          'probabilityPhilosophy',
                          'probabilityPhilosophy'
                         ],
                         ['Set Theory',
                          'Set Theory',
                          'sets',
                          'sets'
                         ],
                         ['Categorical Logic',
                          'Categorical Logic',
                          'categoricalLogic',
                          'categoricalLogic'
                         ],
                         ['Propositional Logic',
                          'Propositional Logic',
                          'logic',
                          'logic'
                         ],
                         ['Axioms of Probability',
                          'Axioms of Probability',
                          'probabilityAxioms',
                          'probabilityAxioms'
                         ],
                         ['Conditional probability and independence',
                          'Conditional probability, multiplication rule, independence, and Bayes\' rule',
                          'conditioning',
                          'montyHall'
                         ],
                         ['The sample sum and the Binomial distribution',
                          'The sample sum and the Binomial distribution',
                          'binomial',
                          'montyHallTest'
                         ],
                         ['Random Variables and Discrete Distributions',
                          'Random variables and discrete distributions',
                          'randomVariables',
                          'randomVariables'
                         ],
                         ['Expected Value',
                          'Expected Value',
                          'expectation',
                          'expectation'
                         ],
                         ['Standard Error',
                          'Standard Error',
                          'standardError',
                          'standardError'
                         ],
                         ['The Normal Curve, the Central Limit Theorem, and Markov\'s ' +
                            'and Chebychev\'s Inequalities for Random Variables',
                          'The Normal Curve, the Central Limit Theorem, and Markov\'s ' +
                            'and Chebychev\'s Inequalities for Random Variables',
                          'clt',
                           'clt'
                         ],
                         ['Sampling',
                          'Sample surveys and sampling designs',
                          'sampling',
                          'sampling'
                         ],
                         ['Estimating Parameters from Simple Random Samples',
                          'Estimating a parameter from a random sample; bias, SE, and MSE of estimators',
                          'estimation',
                          'estimation'
                         ],
                         ['Confidence Intervals',
                          'Confidence intervals for the population mean: the meaning of ' +
                               'confidence level and coverage probability',
                          'confidenceIntervals',
                          'confidenceIntervals'
                         ],
                         ['Hypothesis Testing',
                          'Hypothesis Testing: Does Chance explain the Results?',
                          'testing',
                          'testing'
                         ],
                         ['Does Treatment Have an Effect?',
                          'Does Treatment have an Effect?',
                          'experiments',
                          'experiments'
                         ],
                         ['Testing Whether Two Percentages are Equal',
                          'Testing whether two percentages are equal',
                          'percentageTests',
                          'percentageTests'
                         ],
                         ['Approximate Hypothesis Tests: the <em>z</em> Test and the <em>t</em> Test',
                          'Approximate Hypothesis Tests: the <em>z</em> Test and the <em>t</em> Test',
                          'zTest',
                          'zTest'
                         ],
                         ['The Multinomial Distribution and the Chi-Squared Test for Goodness of Fit',
                          'The Multinomial distribution and the Chi-Squared Test for Goodness of Fit',
                          'chiSquare',
                          'chiSquare'
                         ]
                        ];

var chapterNumbers = new Object;
var assignmentNumbers = new Object;
for (var j = 0; j < chapterTitles.length ; j++) {
    chapterNumbers[chapterTitles[j][1]] = j;
}
for (var j=0; j < assignmentTitles.length; j++) {
    assignmentNumbers[assignmentTitles[j][2]] = j;
}

var cookieExpireDays = 7;          // days for the cookies to endure
var theChapter = null;             // current chapter
var theChapterTitle;               // title of the current chapter, if specified
var theCourse;                     // course-specific data
var openAssignNow;                 // server time when assignment page was opened
var enrollList;                    // hashed enrollment list
var newStyleAnswer = true;         // flag for pop-up versus inline
var fCtr = 0;                      // counter for footnotes
var figCtr = 1;                    // counter for figures
var pCtr = 1;                      // counter for problems
var qCtr = 1;                      // counter for questions
var tCtr = 1;                      // counter for tables
var xCtr = 1;                      // counter for examples
// var footnotes = new Array();       // array of footnotes
// var footnoteLabels = new Array();  // array of footnote labels
var key = new Array();             // key for self-graded exercises
var boxList = new Array();         // list of images for self-graded exercises
var setNum;                        // current problem set number
var isLab = false;                 // is this a problem set?
var mySID;
var acccessURL;
var dueURL;
var timeURL;
var slack = 11*60000;
var serverDate;                    // time according to the server
var pbsURL = 'http://statistics.berkeley.edu/~stark';
                                   // P.B. Stark's URL
var pbsRef = '<a href="' + pbsURL + '" target="_top">P.B. Stark</a>';
                                   // link to author
var fudgeFactor = 0.01;            // relative tolerance for imprecise numerical answers
var absFudge = 1.e-20;             // absolute tolerance for identically zero answers
var startXHT = '<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" ' +
                                     '"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">';
var metaTagXHT = '<meta http-equiv="expires" content="0" />' +
               '<meta http-equiv="Content-Type" content="text/xhtml; charset=utf-8" />' +
               '<meta http-equiv="Content-Language" content="en-us" />' +
               '<meta name="author" content="P.B. Stark" />' +
               '<meta name="copyright" content="Copyright &copy;"' + copyYr +
                         ' by P.B. Stark, ' + pbsURL + ' All rights reserved." />' +
               '<meta http-equiv="Content-Script-Type" content="text/javascript" />' +
               '<meta http-equiv="Content-Style-Type" content="text/css" />' +
               '<meta name="keywords" content="statistics,probability,textbook,interactive,regression,' +
                                              'hypothesis,random,syllogism,logic,reasoning,fallacies">';
var cssLinkXHT = '<link rel="stylesheet" type="text/css" href="..' + cssBase.toString() + '" />';
var assignmentPrefix = 'PS-';
var inlinePrefix='Q#:a#:';
var bigPi = '3141592653';
var rmin = 2.3e-308;            // for numerical analysis
var eps = 2.3e-16;              // ditto
var maxIterations = 100;        // default iteration limit for iterative algorithms
var maxSubmits = 5;             // max submissions of each homework
var showWrongAfterSubmits = 4;  // show which answers are wrong after this many submissions
var showQMarks = true;          // show labeling of each question answer area
var continueLab;
var htmStuff;
var dfStat;
var randSeed;                   // seed of random number generator
var CA = false;
var sectionContext;             // chapter-specific initialization script
var qImgSrc = '../Graphics/answer_unknown.gif';
var rightImgSrc = '../Graphics/answer_good.gif';
var wrongImgSrc = '../Graphics/answer_bad.gif';
var alphabet = ['a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p',
        'q','r','s','t','u','v','w','x','y','z'
        ];
var ALPHABET = ['A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P',
        'Q','R','S','T','U','V','W','X','Y','Z'
        ];
var Alphabet = ALPHABET;
var cardinals = ['zero','one','two','three','four','five','six','seven','eight','nine','ten',
            'eleven','twelve','thirteen','fourteen','fifteen','sixteen','seventeen','eighteen',
            'nineteen','twenty','twenty-one','twenty-two','twenty-three','twenty-four',
            'twenty-five','twenty-six','twenty-seven','twenty-eight','twenty-nine','thirty',
            'thirty-one','thirty-two','thirty-three','thirty-four','thirty-five','thirty-six',
            'thirty-seven','thirty-eight','thirty-nine','fourty','fourty-one','fourty-two',
            'fourty-three','fourty-four','fourty-five','fourty-six','fourty-seven','fourty-eight',
            'fourty-nine','fifty','fifty-one','fifty-two','fifty-three','fifty-four','fifty-five',
            'fifty-six','fifty-seven','fifty-eight','fifty-nine','sixty','sixty-one','sixty-two',
            'sixty-three','sixty-four','sixty-five','sixty-six','sixty-seven','sixty-eight',
            'sixty-nine','seventy','seventy-one','seventy-two','seventy-three','seventy-four',
            'seventy-five','seventy-six','seventy-seven','seventy-eight','seventy-nine',
            'eighty','eighty-one','eighty-two','eighty-three','eighty-four','eighty-five',
            'eighty-six','eighty-seven','eighty-eight','eighty-nine','ninety',
            'ninety-one','ninety-two','ninety-three','ninety-four','ninety-five',
            'ninety-six','ninety-seven','ninety-eight','ninety-nine','one hundred'
            ];
var Cardinals = ['Zero','One','Two','Three','Four','Five','Six','Seven','Eight','Nine','Ten',
            'Eleven','Twelve','Thirteen','Fourteen','Fifteen','Sixteen','Seventeen','Eighteen',
            'Nineteen','Twenty','Twenty-one','Twenty-two','Twenty-three','Twenty-four',
            'Twenty-five','Twenty-six','Twenty-seven','Twenty-eight','Twenty-nine','Thirty',
            'Thirty-one','Thirty-two','Thirty-three','Thirty-four','Thirty-five','Thirty-six',
            'Thirty-seven','Thirty-eight','Thirty-nine','Fourty','Fourty-one','Fourty-two',
            'Fourty-three','Fourty-four','Fourty-five','Fourty-six','Fourty-seven','Fourty-eight',
            'Fourty-nine','Fifty','Fifty-one','Fifty-two','Fifty-three','Fifty-four','Fifty-five',
            'Fifty-six','Fifty-seven','Fifty-eight','Fifty-nine','Sixty','Sixty-one','Sixty-two',
            'Sixty-three','Sixty-four','Sixty-five','Sixty-six','Sixty-seven','Sixty-eight',
            'Sixty-nine','Seventy','Seventy-one','Seventy-two','Seventy-three','Seventy-four',
            'Seventy-five','Seventy-six','Seventy-seven','Seventy-eight','Seventy-nine',
            'Eighty','Eighty-one','Eighty-two','Eighty-three','Eighty-four','Eighty-five',
            'Eighty-six','Eighty-seven','Eighty-eight','Eighty-nine','Ninety',
            'Ninety-one','Ninety-two','Ninety-three','Ninety-four','Ninety-five',
            'Ninety-six','Ninety-seven','Ninety-eight','Ninety-nine','One hundred'
            ];
var ordinals = ['zeroth','first','second','third','fourth','fifth','sixth','seventh','eighth',
            'ninth','tenth','eleventh','twelfth','thirteenth','fourteenth','fifteenth',
            'sixteenth','seventeenth','eighteenth','ninteenth','twentieth','twenty-first',
            'twenty-second','twenty-third','twenty-fourth','twenty-fifth','twenty-sixth',
            'twenty-seventh','twenty-eighth','twenty-ninth','thirtieth','thirty-first',
            'thirty-second','thirty-third','thirty-fourth','thirty-fifth','thirty-sixth',
            'thirty-seventh','thirty-eighth','thirty-ninth','fourtieth','fourty-first',
            'fourty-second','fourty-third','fourty-fourth','fourty-fifth','fourty-sixth',
            'fourty-seventh','fourty-eighth','fourty-ninth','fiftieth','fifty-first',
            'fifty-second','fifty-third','fifty-fourth','fifty-fifth','fifty-sixth',
            'fifty-seventh','fifty-eighth','fifty-ninth','sixtieth','sixty-first',
            'sixty-second','sixty-third','sixty-fourth','sixty-fifth','sixty-sixth',
            'sixty-seventh','sixty-eighth','sixty-ninth','seventieth','seventy-first',
            'seventy-second','seventy-third','seventy-fourth','seventy-fifth','seventy-sixth',
            'seventy-seventh','seventy-eighth','seventy-ninth','eightieth','eighty-first',
            'eighty-second','eighty-third','eighty-fourth','eighty-fifth',
            'eighty-sixth','eighty-seventh','eighty-eighth','eighty-ninth','ninetieth',
            'ninety-first','ninety-second','ninety-third','ninety-fourth','ninety-fifth',
            'ninety-sixth','ninety-seventh','ninety-eighth','ninety-ninth','hundredth'
            ];
var iteratives = ['no times','once','twice','thrice'];
for (var i=4; i < cardinals.length; i++) {
    iteratives[i] = cardinals[i] + ' times';
}
var primes = [ 2,      3,      5,      7,     11,     13,     17,     19,     23,     29, 
              31,     37,     41,     43,     47,     53,     59,     61,     67,     71, 
              73,     79,     83,     89,     97,    101,    103,    107,    109,    113,
             127,    131,    137,    139,    149,    151,    157,    163,    167,    173,
             179,    181,    191,    193,    197,    199,    211,    223,    227,    229,
             233,    239,    241,    251,    257,    263,    269,    271,    277,    281,
             283,    293,    307,    311,    313,    317,    331,    337,    347,    349,
             353,    359,    367,    373,    379,    383,    389,    397,    401,    409];
var nPrimes = [0, 0, 1, 2, 2, 3, 3, 4, 4, 4,           // 0-9
               4, 5, 5, 6, 6, 6, 6, 7, 7, 8,           // 10-19
               8, 8, 8, 9, 9, 9, 9, 9, 9, 10,          // 20-29
               10, 11, 11, 11, 11, 11, 11, 12, 12, 12, // 30-39
               12, 13, 13, 14, 14, 14, 14, 15, 15, 15, // 40-49
               15, 15, 15, 16];                        // 50-53
var allTheChars = '~\`!1@2#3$4%5^6&7*8(9)0_-+=QqWwEeRrTtYyUuIiOoPp{[}]|\\AaSsDdFfGgHhJjKkLl:;' +
                  '\"\'ZzXxCcVvBbNnMm<,>.?/';
var faces = ['Ace','two','three','four','five','six','seven','eight','nine','ten','Jack',
         'Queen','King'];
var suits = ['spades','hearts','diamonds','clubs'];
var colors = ['red','orange','yellow','green','blue','indigo','violet',
              'black','white','gray','silver','gold','brown','aqua','teal',
              'fuschia','magenta','cyan','sage','turquoise','chartreuse',
              'mauve','periwinkle','umber','brick','marigold','seafoam','coral',
              'purple','grape','cherry','beige','copper','sienna','baby blue'
             ];

// ========================================================================
//  FUNCTION LIBRARY
// ========================================================================

// ===============  STRING HANDLERS and HTML GENERATORS ===================

function mailLink(user, host, domain, altText) {
        var addr = user +  '&#64;' + host + '.' + domain;
        if (typeof(altText) == 'undefined' || altText == null || altText.length == 0) {
                altText = addr;
        }
        document.writeln('<a href="mai' + 'lto:' + addr + '">' + altText + '</a>');
}

function mailForm(user, host, domain, subTxt, onSubTxt) {
        var addr = user +  '&#64;' + host + '.' + domain;
        if (typeof(subject) == 'undefined' || altText == null || altText.length == 0) {
                altText = addr;
        }
        document.writeln('<form method="post" action="mai' + 'lto:' + addr + '?subject="' +
                subTxt + '" onsubmit="' + onSubTxt + '">');
}

function trimBlanks(s){
    if (s == null || s.length == 0 ) { return(s); }
    while (s.charAt(s.length-1) == ' ' ) {      // trim trailing blanks
        s = s.substring(0, s.length-1);
    }
    while (s.charAt(0) == ' ') {                // trim leading blanks
        s = s.substring(1, s.length);
    }
    return(s);
}

function allBlanks(s) {
    if (s == null || trimBlanks(s).length == 0) {
        return(true);
    } else {
        return(false);
    }
}

function removeAllBlanks(s){
    return(s.replace(/ +/gm,''));
}

function removeMarkup(s) { // removes html markup
    return(s.replace(/<[^>]*>/gm,''));
}

function replaceMarkupByChar(s,sub) { // replaces html markup with sub
    if (typeof(sub) == 'undefined' || sub == null || sub.length == 0) {
       sub = ' ';
    }
    return(s.replace(/<[^>]*>/gm, sub));
}

function removeSpecials(s) { // removes special characters markup EXCEPT brackets
    return(s.replace(/[0123456789:;~`'"<>,.?\/+_@#$%^&*|!=-]+/gm,''));
}

function removeNonLogicals(s) { // removes special characters markup EXCEPT brackets and logical symbols
    return(s.replace(/[0123456789:;`'"<>,.?\/+_@#$%^*=-]+/gm,''));
}

function trimToLowerCase(s) {
// trim trailing blanks, convert to lower case
    if (s == null || s.length == 0 ) {
        return(s);
    }
    return(trimBlanks(s.toLowerCase()));
}

function allLetters(s){
// return true if all characters in string s are letters (or trailing blanks)
    var trimS = trimToLowerCase(s);
    var alpha=' -abcdefghijklmnopqrstuvwxyz';
    var truth = true;
    for (var i = 0; i < trimS.length; i++) {
        if(alpha.indexOf(trimS.charAt(i)) < 0) {
            truth = false;
        }
    }
    return(truth);
}

function removeCommas(s) { // removes commas from a string
    return(s.replace(/,/gm,''));
}

function removeString(str,s) { // removes instances of the string str from s.
    return(s.replace(eval('/'+str+'/gm'),''));
}

function removeStrings(strArr,s) {
    for (var j=0; j < strArr.length; j++) {
        s = removeString(strArr[j], s);
    }
    return(s);
}

function parsePercent(s) {
// parse a number that contains a % sign to turn it into a decimal fraction
    var value;
    if (s.indexOf('%') == -1) {
        value = parseFloat(trimBlanks(removeCommas(s)))
    } else {
        while (s.indexOf('%') != -1) {
            s = s.substring(0,s.indexOf('%')) +
                s.substring(s.indexOf('%')+1,s.length)
        }
        value = parseFloat(trimBlanks(removeCommas(s)))/100;
    }
    return(value);
}

function evalNum(s) { // try to evaluate a string as a numeric value
    var value;
    var dmy = s + ' ';
    dmy = dmy.replace(/\%/g,'\/100');
    dmy = dmy.replace(/,/g,'');
    if ( typeof(s) == 'undefined' || s == null || !s.match(/[^ ]/) ) {
        value = 'NaN';
    } else if ( dmy.match(/[^1234567890+*\/.ed() -]/i) ) {
        value = 'NaN';
    } else if ( !dmy.match(/[1234567890]/) ) {
        value = 'NaN';
    } else {
        try {
            eval('value = ' + dmy + ';');
        } catch(e) {
            value = 'NaN';
        }
    }
    return(value);
}

function parseMultiple(option) {
  // pre-processes multiple selections so that checkAnswer can be used to grade them
    var response = '';
    for (var i=0; i < option.length; i++) {
        if (option[i].selected) {
            response += trimToLowerCase(option[i].value) + ',' ;
        }
    }
    if (response.charAt(response.length - 1) == ',') {  // trim trailing comma
        response = response.substring(0, response.length - 1);
    }
    return(response);
}

function getFormElementIndex(q) {
 // finds the form element with id= q and returns its index.
    for (var inx =0; inx < document.forms[0].elements.length; inx++) {
        if (document.forms[0].elements[inx].id == q) {
            return(inx);
        }
    }
    alert('Error #1 in irGrade.getFormElementIndex(): Form element ' + q + ' is missing!');
    return('document.forms[0].length');
}

function findNum(s) {
// if s is an integer or its string representation, returns s.
// if not, tries to remove characters to
// leave an int, and returns that int.
    var i = parseInt(s);
    if ( !isNaN(i)) {
        return(i);
    } else {
        var q = '';
        for (var j=0; j < s.length; j++) {
            var dum = q;
            if (!isNaN(parseInt(dum + s.charAt(j)))) {
                q += s.charAt(j);
            }
        }
        return(q);
    }
}

function vFindNum(s) { // finds numbers in a string array
    var a = new Array(s.length);
    for (var i=0; i < s.length; i++) {
        a[i] = findNum(s[i]);
    }
    return(a);
}

// ============================================================================
// ========================= COOKIE MANIPULATION ==============================

function expireTimeString(ed) {
    var now = new Date();
    var expire = new Date();
    var cookDays;
    if (typeof(ed) == 'undefined' || ed == null) {
        cookDays = cookieExpireDays;
    } else {
        cookDays = ed;
    }
    expire.setTime(now.getTime() + cookDays*60*60*1000*24);
    return(expire.toGMTString());
}

function getCookieVal(cook,key){ // gets the value of key within the cookie cook
    var searchStr = cook + '&';
    var val = null;
    var pat = key + '=';
    var inx = searchStr.indexOf(pat);
    if (inx > -1){
        searchStr = searchStr.substring(inx + pat.length, searchStr.length);
        val = unescape(searchStr.substring(0, searchStr.indexOf('&')));
    }
    return(val);
}

function deleteAllCookies() {
     var et = expireTimeString(-1);
     var clist = document.cookie.split(';');
     for (var j=0; j < clist.length; j++) {
         var cook = clist[j].split('=');
         document.cookie = cook[0] + '= ;EXPIRES=' + et;
     }
     return(true);
}

function getCookieArray(cook,stem,len) { // gets an array from the cookie
    var ansArr = new Array(len);
    for (var i=0; i < len; i++) {
        ansArr[i] = getCookieVal(cook, stem + i.toString());
    }
    return(ansArr);
}

function setCookieArray(arr,stem) { // make a cookie-like string from the array arr
    var ansStr = '';
    for (var i=0; i < arr.length; i++) {
        ansStr += stem + i.toString() + '=' + arr[i] + '&';
    }
    if (ansStr.substr(ansStr.length-1,1) == '&') {
        ansStr = ansStr.substring(0, ansStr.length - 1);
    }
    return(ansStr);
}

// ============================================================================
// =================== PROBLEM AND GRADING, SPECIAL MARKUP ====================

function numToMultiple(opt,ans) { // finds the multiple choice closest to ans
    var dif = Math.abs(parsePercent(opt[0]) - ans);
    var aVal = 'a';
    for (var i=1; i < opt.length; i++) {
        var d2 = Math.abs(parsePercent(opt[i]) - ans);
        if (d2 < dif) {
            dif = d2;
            aVal = alphabet[i];
        }
    }
    return(aVal);
}

function hiddenInput(string, val){  // hidden input with id=name="string" and value="val"
    return('<input type="hidden" id="' + string + '" name="' + string + '" value="' + val + '" />');
}


function citeChapter(s) {
    var j = chapterNumbers[s];
    return('<span class="chapterCite">Chapter ' + j.toString() + ', ' + chapterTitles[j][0] + '</span>');
}

function citeLinkChapter(s, anchor, relpath) {
    if (typeof(anchor) == 'undefined' || anchor == null || anchor.length == 0) {
         anchor = '';
    } else {
         anchor = '#' + anchor;
    }
    if (typeof(relpath) == 'undefined' || relpath == null || relpath.length == 0) {
         relpath = './';
    }
    return('<a class="chapterLink" target="_self" href="' + relpath + s + anchor + '.htm">' + citeChapter(s) + '</a>');
}

function writeCaption(ref, q, capt, align) { // start a numbered caption.
    if (typeof(align) == 'undefined' || align == null) {
            align='center';
    }
    var linkStr = capt.replace(/[^a-z0-9]/gi,'_');
    var qStr = '<div class="caption" id="' + linkStr +'"><p align="' + align + '">' + ref + '-' + q + ': ' + capt + '</p></div>';
    document.writeln(qStr);
    if (!document.getElementById(linkStr).parentNode.id) {
          document.getElementById(linkStr).parentNode.id = linkStr + '_parent';
    }
}

function writePlainCaption(capt, align) { // start a plain caption.
    if (typeof(align) == 'undefined' || align == null) {
            align='center';
    }
    var linkStr = capt.replace(/[^a-z0-9]/gi,'_');
    var qStr = '<div class="caption" id="' + linkStr +'"><p align="' + align + '">' + ref + '-' + q + ': ' + capt +
                '</p></div>';
    document.writeln(qStr);
    if (!document.getElementById(linkStr).parentNode.id) {
          document.getElementById(linkStr).parentNode.id = linkStr + '_parent';
    }
}

function writeTableCaption(capt, align) { //
    writeCaption('Table&nbsp;' + chapterNumbers[cNum].toString(), tCtr++, capt, align);
}

function writeFigureCaption(capt, align) {
    writeCaption('Figure&nbsp;' + chapterNumbers[cNum].toString(), figCtr++, capt, align);
}

function writeExampleCaption(capt, align) {
    if (typeof(align) == 'undefined' || align == null) {
            align = 'left';
    }
    writeCaption('Example&nbsp;' + chapterNumbers[cNum].toString(), xCtr++, capt, align);
}

function citeTable(num, print) {
    if (typeof(num) == 'undefined' || num == null) {
            num = tCtr;
    }
    if (typeof(print) == 'undefined' || print == null) {
            print = true;
    }
    var cStr = '<span class="chapterCite">Table&nbsp;' + chapterNumbers[cNum].toString() + '-' + num.toString() +
               '</span> ';
    if (print) {
            document.writeln(cStr);
            return(true);
    } else {
            return(cStr);
    }
}

function citeFig(num, print) {
    if (typeof(num) == 'undefined' || num == null) {
            num = figCtr;
    }
    if (typeof(print) == 'undefined' || print == null) {
            print = true;
    }
    var cStr = '<span class="chapterCite">Figure&nbsp;' + chapterNumbers[cNum].toString() + '-' +
               num.toString() + '</span> ';
    if (print) {
            document.writeln(cStr);
            return(true);
    } else {
            return(cStr);
    }
}

function citeExample(num, print) {
    if (typeof(num) == 'undefined' || num == null) {
            num = xCtr;
    }
    if (typeof(print) == 'undefined' || print == null) {
            print = true;
    }
    var cStr = '<span class="chapterCite">Example&nbsp;' + chapterNumbers[cNum].toString() + '-' +
               num.toString() + '</span> ';
    if (print) {
            document.writeln(cStr);
            return(true);
    } else {
            return(cStr);
    }
}

function startProblem(q) {  // writes html to start a problem, numbered q
    chStr = '';
    if (typeof(chapterNumbers[cNum]) != 'undefined' && chapterNumbers[cNum] != null) {
        chStr += (chapterNumbers[cNum]).toString() + '-';
    }
    var ref;
    if (HI) {
        ref = "Problem&nbsp;" + chStr + q.toString();
    } else {
        ref = "Exercise&nbsp;" + chStr + q.toString();
    }
    var linkStr = 'Q-' + q.toString();
    var s = '<a id="' + linkStr + '"/><p>&nbsp;</p><p><strong>' + ref + '.</strong>';
    return(s);
}


function startSolution(q) {  // html to start a solution, numbered q
    return('<strong>Solution.</strong> ');
}

function writeSelectExercise(mult, q, opt, ans) {
    document.writeln(selectExerciseString(mult, q, opt, ans));
    return(true);
}

function selectExerciseString(mult, q, opt, ans) {
    var id = 'Q' + q.toString();
    var s = selectExercise(mult, id, opt, CA);
    if (showQMarks) {
            document.writeln('(Q' + q + ')');
    }
    if (CA) {
        if (mult) {
            s += '<input type="button" id="B' + q +
                '" value="Check Answer" onclick="checkAnswer(\'' + id +
                '\',parseMultiple(' + id +'.options))" />';
        }
        if (newStyleAnswer != null && newStyleAnswer) {
            boxList[q - 1] = document.images.length;
            s += qCheckString(q);
        }
    }
    key[q - 1] = crypt(ans, randSeed.toString());
    return(s);
}

function qCheckString(q) {
    var s = '<a href="javascript:void();" target="_self"' +
            ' onClick="giveAnswer(\'Q' + q.toString() + '\');return(false);">' +
            ' <img src="' + qImgSrc + '" ' +
            ' border="1" align="top" title="see the answer" /></a>' +
            ' <span class="ansSpan" id="ansSpan' + q.toString() + '"></span>';
   return(s);
}

function textExercise(size,q,ca) {
  // text input area of "size" size, id q, and appropriate onChange()
    var s = '<input type="text" size="' + size + '" id="' + q.toString() + '" name="' + q.toString() + '" ';
    if (ca == null || ca ) {
        s += 'onChange="checkAnswer(id,value);"';
    }
    s += ' />';
    return(s);
}

function textProblem(size,q) {  // makes text input area of "size" size, id "q"
    var s = '<input type="text" size="' + size + '" id="' + q + '" name="' + q + '" />';
    return(s);
}

function writeTextExercise(size, q, ans) {  // does all the printing for a textfield exercise
    if (showQMarks) {
            document.writeln('(Q' + q + ')');
    }
    document.writeln(textExercise(size, 'Q' + q, CA));
    if (CA) {
        if (newStyleAnswer != null && newStyleAnswer) {
            boxList[q - 1] = document.images.length;
            document.writeln(qCheckString(q));
        }
    }
    key[q - 1] = crypt(ans, randSeed.toString());
    return(true);
}

function writeRadioExercise(q, opt, ans) {  // write a radio exercise
    document.writeln(radioExercise('Q'+q, opt, CA));
    if (showQMarks) {
            document.writeln('(Q' + q + ')');
    }
    if (CA) {
        if  (newStyleAnswer != null && newStyleAnswer) {
            boxList[q - 1] = document.images.length;
            document.writeln(qCheckString(q));
        }
    }
    key[q-1] = crypt(ans, randSeed.toString());
    return(true);
}

function radioExercise(q, opt, ca){  // makes a collection of radio inputs.
    var s = '';
    var oplen = opt.length;
    for (var i = 0; i < oplen; i++) {
        s  += '<input type="radio" id="' + q + '" name="' + q + '" value="' + alphabet[i] + '" ';
        if (ca == null || ca) {
            s += 'onClick="checkAnswer(id,value);"';
        }
        s += ' />\n' + alphabet[i] + ') ' + opt[i] + '<br />\n';
    }
    return(s);
}

function selectExercise(mult, q, opt, ca) {
   // makes a select with multiple=mult, id q.
   // if mult, makes the size large enough to show all options.
   // otherwise size=1;  opt is a 1 by array.
    var s;
    var size;
    var oplen = opt.length;
    if (mult) { // leave room for all the answers to be visible.
        size= oplen+1;
    } else {
        size = 1;
    }
    var s = '<select id="' + q + '" name="' + q + '" size="' + size + '" ';
    if (mult) {
        s += 'multiple="' + mult + '" ';
    }
    if ((ca == null || ca) && !mult) {
        s += 'onChange ="checkAnswer(id,options[selectedIndex].value);"';
    }
    s += '>\n <option>?</option>\n';
    if (oplen <= 26) {
        for (var i=0; i < oplen; i++) {
            s += '<option value="' + alphabet[i] + '">' + ALPHABET[i] +
                ': ' + opt[i] + '</option>\n';
        }
    } else {
        for (var i=0; i < oplen; i++) {
            s += '<option value="' + (i+1).toString() + '">' +
                (i+1).toString() + ': ' + opt[i] + '</option>\n';
        }
    }
    s += '</select>';
    return(s);
}

function functionalGradeString(fn, ans) {
    return('@ansText = \'' + ans + '\';\n' +
           'function checkProblem(r) {\n' +
                     fn + '\n}');
}

function scoreProblem(truth,response){
    var ansVec = parseKey(truth);
    var qTypeCode = ansVec[0];  // type of question
    var answer = ansVec[1];     // the correct answer or function that evaluates correctness
    response = trimToLowerCase(response);  // student response
    var rsp;
    var correctness;
    if (response == null) {
        correctness = false;
    } else if (qTypeCode == 'FN') {
        eval(answer);
        correctness = eval('checkProblem(response)');
    } else if (qTypeCode == 'WC') {
        rsp = trimBlanks(response);
        if (rsp.length > 0) {
           correctness = true;
        }
    } else if (qTypeCode == 'LA') {
        // try to parse as number; if fail, take literal.
        rsp = evalNum(response);
        if (!isNaN(rsp)) {
            response = rsp;
        }
        if (answer.toString() == response.toString()) {
            correctness = true;
        } else {
            correctness = false;
        }
    } else if (qTypeCode == 'RG') {
        var r = evalNum(response);
        if ((answer[0] <= r) && (r <= answer[1])) {
            correctness = true;
        } else {
           correctness = false;
        }
    } else if (qTypeCode == 'MA') {
        correctness = false;
        resArray = response.split(',');
        resArray.sort()
        var matches = new Array();
        for (var k=0; k < resArray.length; k++) {
            matches[k] = 0;
            for (var i=0; i < answer.length; i++) {
                if (resArray[k] == answer[i]) {
                    matches[k] = 1;
                }
            }
        }
        if (vMinMax(matches)[0] == 1) {
            correctness = true;
        }
    } else if (qTypeCode == 'MR') {
        correctness = false;
        resArray = response.split(',');
        resArray.sort()
        if (resArray.length == answer.length) {
            correctness = true;
            for (var i=0; i < answer.length; i++ ) {
                if (answer[i] != trimToLowerCase(resArray[i])) {
                    correctness = false;
                }
            }
        }
    }
    return(correctness);
}

function parseKey(s) {
// parses the answer keys for interactive grading.  See header.
// Returns the question type code, the answer, and a text representation of the answer.
// The answer can be a function that evaluates the correctness of the response, or
// one of several types of answers: literals, numerical ranges, multiple-selects, wildcards, ...
    s = trimBlanks(s) // remove trailing blanks
    var answer;
    var ansText;
    if (s.indexOf('@') == 0) { // answer is a function
        answer = s.substring(1,s.length);
        qTypeCode = 'FN';                  // answer is type function (FN)
        try {
            eval(answer);
        } catch(err) {
            alert('Error #1 in irGrade.parseKey(): functional key does not parse!');
        }
        if (ansText == null || removeAllBlanks(ansText).length == 0 ) {
               ansText = 'Sorry, there is no text representation of the answer to this question.';
        }
    } else if (s.indexOf(':') != -1) { // solution is a range
        answer = s.toLowerCase().split(':')
        if (answer.length != 2) { alert('Error #2 in irGrade.parseKey(): bad range syntax!') }
        qTypeCode = 'RG';                   // answer is of type range (RG)
        for (var i=0; i < answer.length; i++) {
            answer[i] = parsePercent(answer[i]);
            if (isNaN(answer[i])){
                alert('Error #3 in irGrade.parseKey(): unparsable number in range!');
            }
        }
        ansText = answer[0] + ' to ' + answer[1];
    } else if (s.indexOf('&') != -1 ){ // multiple required answers; assume all
                                       // are letters
        answer = s.toLowerCase().split('&');
        qTypeCode = 'MR';                   // answer is of type multiple required (MR)
        for (var i=0; i < answer.length; i++ ) {
            answer[i] = trimBlanks(answer[i].toLowerCase());
            answer.sort();
        }
        ansText = answer[0];
        for (var i=1; i < answer.length; i++) {
            ansText += ' and ' + answer[i];
        }
    } else if (s.indexOf('|') != -1 ){ // multiple answers accepted; assume all
                                       // are letters
        answer = s.toLowerCase().split('|');
        qTypeCode = 'MA';                   // answer is of type multiple accepted (MA)
        for (var i=0; i < answer.length; i++ ) {
            answer[i] = trimBlanks(answer[i].toLowerCase());
        }
        ansText = answer[0];
        for (var i=1; i < answer.length; i++) {
            ansText += ' or ' + answer[i];
        }
    } else if (s == '*') {
        qTypeCode = 'WC';                   // wildcard
        answer = '*';
        ansText = 'any non-blank answer';
    } else {                           // answer is literal
        qTypeCode = 'LA';                   // literal answer (LA)
        answer = parsePercent(s);
        if (isNaN(answer)) {
            answer = trimBlanks(s.toLowerCase());
        }
        ansText = answer;
    }
    return([qTypeCode,answer,ansText]);
}

function setCourseSpecs() {
    course = theCourse[1];
    courseName = theCourse[2];
    teacher = theCourse[3];
    teacherName = theCourse[4];
    gPath = theCourse[5];
    maxSubmits = theCourse[6];
    showWrongAfterSubmits = theCourse[7];
    assignURL = theCourse[8];
    accessURL = theCourse[9];
    timeURL = theCourse[10];
    dFile = cRoot + course + dFileBase;
    sFile = cRoot + course + sFileBase;
    $(document).ready(function() {
            $("#courseSelector").text(courseName + ': ' + teacherName)
                                .css('color', 'blue');
    });
    return(true);
}

function getGrades(theForm) {
    if (validateLablet(theForm)) {
        mySID = theForm.sid.value;
        $('#scores').text('<p class="center">Retrieving scores for SID ' +
                                                       mySID + '<blink>&hellip</blink></p>');
        scoresURL = scoreBase + 'class=' + course + '&teacher=' + teacher + '&gpath=' + gPath + '&sids';
        getURL = $.ajax({
                          type: 'GET',
                          url:   scoresURL
                        })
                        .done(function() {
                            var rt = data.split('\n');
                            var myScores = new Array();
                            var k=0;
                            for (var j=0; j < rt.length; j++) {
                                if (!rt[j].match('#') && (rt[j].match(mySID.toString()) || rt[j].match('Set'))) {
                                    myScores[k++] = rt[j];
                                }
                            }
                            var ihtm = '<p class="center">Scores for SID ' + mySID + '</p><table class="dataTable">';
                            for (var j=0; j < myScores.length; j++) {
                                ihtm += '<tr>';
                                var dum = myScores[j].replace(/ +/gm,' ').split(' ');
                                for (var k=0; k < dum.length; k++) {
                                    ihtm += '<td>' + dum[k] + '</td>';
                                }
                                ihtm += '</tr>';
                            }
                            ihtm += '</table>';
                            $('#scores').text(ithm)
                                        .css('visibility', 'visible');
                       })
                       .fail(function() {
                            alert('failed to retrieve scores');
                            $('#scores').text('<p>Unable to retrieve scores for SID ' +
                                              mySID.toString() + ' at this time.</p>')
                                        .css('visibility', 'visible');
                       });
            }
}


function spawnProblem(theForm,setName,relPath) {
    if (typeof(relPath) == 'undefined' || relPath == null || relPath.length == 0) {
        relPath = '..';
    }
    if (validateLablet(theForm)) {
        var ck = document.cookie;
        var fname = formStemName + assignmentNumbers[setName].toString();
        var assigned = assign[setName] && (assign[setName][1] == 'ready');
        if (!assigned) {
                    alert('Error #1 in irGrade.spawnProblem(): This has not been assigned yet.\n Try again later.');
                    return(false);
        } else {
            var sstr =  crypt('sid' + theForm.sid.value, theForm.sid.value) + '=';
            if (ck.indexOf(sstr) < 0){
                var rs = (theForm.sid.value).toString();
                if (rs.length < 10){
                     rs += rs;
                }
                randSeed = parseInt(rs.substr(0,Math.min(10,rs.length)));
                setSubmitCookie('sid', theForm, true);
                ck = document.cookie;
                if (ck.indexOf(sstr) < 0) {
                    alert('Error #2 in irGrade.spawnProblem()!\n' +
                          'Make sure your browser is configured to accept cookies.\n' +
                          'Clear existing cookies and try again.');
                    return(false);
                }
            }
            var ss = ck.substring(ck.indexOf(sstr) + sstr.length, ck.length);
            if (ss.indexOf(';') > -1) {
                ss = ss.substring(0,ss.indexOf(';'));
            }
            var cl = crypt(ss, theForm.sid.value);
            var instr = relPath + '/Problems/' + setName + 'i.htm';
            var appl  = relPath + '/Problems/' + setName + 'j.htm';
            lablet = open('','lablet','toolbar=no,location=no,directories=no,status=no,'+
                'scrollbars=yes,resizable=yes,height=600,width=800');
            lablet.document.open();
            lablet.continueLab = cl;
            var pastDue = (new Date(assign[setName][0]) < new Date());
            var sAns = assign[setName][2] == 'show_answers' || ((assign[setName][2] == 'automatic') && pastDue);
            var allowSubmit = !pastDue && (assign[setName][1] == 'ready');
            lablet.sAns = sAns;
            lablet.allowSubmit = allowSubmit;
            lablet.dFile = dFile;
            lablet.course = course;
            lablet.teacher = teacher;
            lablet.maxSubmits = maxSubmits;
            lablet.showWrongAfterSubmits = showWrongAfterSubmits;
            lablet.theChapter = setName;
            var qStr = startXHT + '<head>' + metaTagXHT + styleSheetRef(relPath) +
                                   '<title>SticiGui Assignment ' + i.toString() + '</title>' +
                                   '<script language="JavaScript1.4" type="text/javascript" src="../../Java/irGrade.js"></script>' +
                                   '</head>';
            lablet.document.writeln('<frameset rows="*,300">');
            lablet.document.writeln('<frame id="instrWin" src="' + instr + '"' +
                ' frameborder="1" framespacing="0" border="1" />');
            lablet.document.writeln('<frame id="appletWin" src="' + appl + '"' +
                ' frameborder="1" framespacing="0" border="1" />');
            lablet.document.writeln('</frameset></html>');
            lablet.document.close();
            return(true);
    }
  } else {
     return(false);
  }
}

// log headers entering and exiting

$(function() {
    var logThis = function(e) {
        pushSectionViewed(this, e.type);
    }

   $("H1").bind('enterviewport', logThis);
//   $("H1").bind('leaveviewport', logThis);
   $("H1").bullseye();
   $("H2").bind('enterviewport', logThis);
//   $("H2").bind('leaveviewport', logThis);
   $("H2").bullseye();
   $("H3").bind('enterviewport', logThis);
//   $("H3").bind('leaveviewport', logThis);
   $("H3").bullseye();

//  login
   $(document).ready(function() {
        resolveUserEid('handleUserLoginCheck');
   });
   $('#loginLink').hover(function() {
        $('#loginBox').slideDown(1000);
        return(true);
    });
    $('#loginLink').mouseout(function() {
        setTimeout("$('#loginBox').slideUp(10000)",2500);
        return(true);
    });
});

function handleUserLoginCheck(_userEid) {
    if (_userEid) {
        $('#loginBox').hide();
    } else {
        $('#loginBox').show();
        setTimeout("$('#loginBox').slideUp(10000)",2500);
    }
    return(true);
}

// ########################  START Onsophic ##################################

//ssanders: BEGIN: Added
var jsonRequestQueue = [];
var jsonRequestTime = new Date().getTime();
var jsonRequestBusy = false;
var jsonRequestUrl = 'http://www.willthatbeonthefinal.com:8088/direct/';

function resolveUserEid(_callbackName) {
/* REMOVED PBS 1/12/2013
    var userUrl = jsonRequestUrl + 'learnrepoActivityEvent/resolveUserEid.json';
    var userRequestId = '_userRequest';
    var userRequestElement = document.getElementById(userRequestId);
    if (userRequestElement) {
        userRequestElement.parentNode.removeChild(userRequestElement);
    }
    userRequestElement = document.createElement('SCRIPT');
    userRequestElement.type = 'text/javascript';
    userRequestElement.id = userRequestId;
    //ssanders: Defeat caching of JavaScript
    userRequestElement.src = userUrl + '?jsonRequestCallback=' + _callbackName + '&jsonRequestTime=' + jsonRequestTime++;
    document.body.appendChild(userRequestElement);
*/
}

function performJsonRequest(_url) {
/* REMOVED PBS 11/2/2012
    jsonRequestBusy = true;
    var jsonRequestId = '_jsonRequest';
    var jsonRequestElement = document.getElementById(jsonRequestId);
    if (jsonRequestElement) {
        jsonRequestElement.parentNode.removeChild(jsonRequestElement);
    }
    jsonRequestElement = document.createElement('SCRIPT');
    jsonRequestElement.type = 'text/javascript';
    jsonRequestElement.id = jsonRequestId;
    //ssanders: Ensure callback and defeat caching of JavaScript
    jsonRequestElement.src = _url + '&jsonRequestCallback=callbackJsonRequest&jsonRequestTime=' + jsonRequestTime++;
    document.body.appendChild(jsonRequestElement);
*/
}

function callbackJsonRequest(_result) {
    if (console && console.log) {
        console.log('callbackJsonRequest(' + _result + ')');
    }
    if (jsonRequestQueue.length > 0) {
        performJsonRequest(jsonRequestQueue.shift());
    } else {
        jsonRequestBusy = false;
    }
}

function enqueueJsonRequest(_url) {
    if (console && console.log) {
        console.log('enqueueJsonRequest(' + _url + '): jsonRequestBusy=' + jsonRequestBusy);
    }
/*  REMOVED PBS 11/2/2012
    if (jsonRequestBusy) {
        jsonRequestQueue.push(_url);
    } else {
        performJsonRequest(_url);
    }
*/
}

//  set maximum wait for json callback
  var jsonIterationCount;
  var jsonMaxIterationCount = 200;

function waitForJsonRequest(_callback) {
/* REMOVED PBS 11/2/2012
    if ((jsonRequestBusy || jsonRequestQueue.length > 0) &&
        jsonIterationCount <= jsonMaxIterationCount) {
             jsonIterationCount++;
             setTimeout(function() { waitForJsonRequest(_callback); }, 100);
    } else  {
*/
             jsonIterationCount = 0;
             if (_callback) {
                  _callback();
             }
//    }
}

function resolveAddOrUpdateUrl(_eventType) {
    var addOrUpdateUrl = jsonRequestUrl + 'learnrepoActivityEvent/addOrUpdate.json';
    addOrUpdateUrl += '?eventType=' + _eventType;
    var targetUrl = document.location.href;
    //ssanders: Remove user/password and fragment
    targetUrl = targetUrl.replace(/:\/\/[^@]+@/, '://').replace(/#.*/, '');
    addOrUpdateUrl += '&targetUrl=' + encodeURIComponent(targetUrl);
    return addOrUpdateUrl;
}

function resolveEventParameters(_index, _anchorValue, _scorePercent, _timeMinutes) {
    var eventParameters = '';
    if (typeof _scorePercent != 'undefined' && _scorePercent != null) {
        eventParameters += '&scorePercent' + _index + '=' + _scorePercent;
    }
    if (typeof _timeMinutes != 'undefined' && _timeMinutes != null) {
        eventParameters += '&timeMinutes' + _index + '=' + _timeMinutes;
    }
    if (typeof _anchorValue != 'undefined' && _anchorValue != null) {
        eventParameters += '&anchorValue' + _index + '=' + encodeURIComponent(_anchorValue);
    }
    return eventParameters;
}

function pushActivityEvent(_eventType, _anchorValue, _scorePercent, _timeMinutes) {
    var pushUrl = resolveAddOrUpdateUrl(_eventType);
    pushUrl += resolveEventParameters('', _anchorValue, _scorePercent, _timeMinutes);
    if (console && console.log) {
        console.log('pushActivityEvent(' + _eventType + ', ' + _anchorValue + ', ' +
            _scorePercent + ', ' + _timeMinutes + ')');
    }
    enqueueJsonRequest(pushUrl);
}

function pushActivityEventSet(_eventType, _anchorValuesAndScorePercents) {
    var pushUrlPrefix = resolveAddOrUpdateUrl(_eventType);
    var pushUrl = '';
    var cnt = 1;
    var scorePercent;
    for (var anchorValue in _anchorValuesAndScorePercents) {
      scorePercent = _anchorValuesAndScorePercents[anchorValue];
      pushUrl += resolveEventParameters(cnt, anchorValue, scorePercent);
      if (console && console.log) {
          console.log('pushActivityEventSet(' + cnt + ', ' + _eventType + ', ' +
              anchorValue + ', ' + scorePercent + ')');
      }
      cnt++;
      //ssanders: Ensure shorter than max URL length
      if (pushUrl.length > 2048 - pushUrlPrefix.length - 128) {
        enqueueJsonRequest(pushUrlPrefix + pushUrl);
        pushUrl = '';
        cnt = 1;
      }
    }
    if (pushUrl.length > 0) {
      enqueueJsonRequest(pushUrlPrefix + pushUrl);
    }
}

function pushSectionViewed(_heading, _eventType) {
    var parentSibling = _heading.firstChild
    var anchorValue;
    while (parentSibling) {
        if (parentSibling.id) {
           anchorValue = "//" + parentSibling.nodeName +
                  "[@id='" + parentSibling.id + "']";
           break;
        }
        parentSibling = parentSibling.nextSibling ? parentSibling.nextSibling :  null;
    }
    if (anchorValue) {
       pushActivityEvent('WORKED', anchorValue);
    }
}


function pushQuestionsWorked(_questionsAndAnswers) {
	var anchorValuesAndScorePercents = [];
	var truth;
	for (var questionNumber in _questionsAndAnswers) {
		truth = _questionsAndAnswers[questionNumber];
	    var anchorValue = 'Q' + questionNumber;
    	    var questionInput = $(anchorValue);
	    if (!questionInput) {
    	    for (var cnt = 0; cnt < frames.length; cnt++) {
        	    try {
            	        questionInput = frames[cnt].document.getElementById(anchorValue);
                	if (questionInput) {
	                    break;
    	                }
        	     } catch (e) {
            	      //ssanders: Frame is not accessible, because it's from another domain
	             }
    	    }
	    }
    	if (questionInput) {
	        //ssanders: Try to build XPath based on Exercise and Question Ids
    	        var parentSibling = questionInput.parentNode;
        	while (parentSibling) {
            	    if (parentSibling.id) {
	                anchorValue = "//" + parentSibling.nodeName +
    	                      "[@id='" + parentSibling.id + "']/following::" +
        	              questionInput.nodeName + "[@id='" + questionInput.id + "']";
            	        break;
	             }
    	             parentSibling = parentSibling.previousSibling ? parentSibling.previousSibling :  parentSibling.parentNode;
        	}
	 }
    	 anchorValuesAndScorePercents[anchorValue] = truth ? 100 : 0;
    }
    pushActivityEventSet('WORKED', anchorValuesAndScorePercents);
}

function pushSolutionOpened(_questionNumber) {
    var anchorValue = 'solDivLink' + _questionNumber;
    var solutionLink = $(anchorValue);
    if (!solutionLink) {
        for (var cnt = 0; cnt < frames.length; cnt++) {
            try {
                solutionLink = frames[cnt].document.getElementById(anchorValue);
                if (solutionLink) {
                    break;
                }
            } catch (e) {
                //ssanders: Frame is not accessible, because it's from another domain
            }
        }
    }
    if (solutionLink) {
        //ssanders: Try to build XPath based on Exercise and Question Ids
        var parentSibling = solutionLink.previousSibling;
        while (parentSibling) {
            if (parentSibling.id) {
              anchorValue = "//" + parentSibling.nodeName +
                  "[@id='" + parentSibling.id + "']/following::" +
                  solutionLink.nodeName + "[@id='" + solutionLink.id + "']";
              break;
            }
            parentSibling = parentSibling.previousSibling ? parentSibling.previousSibling :  parentSibling.parentNode;
        }
    }
    pushActivityEvent('OPENED', anchorValue);
}

function pushFootnoteOpened(_footnoteNumber) {
    var anchorValue = "//SPAN[@id='fnSpanLink" + _footnoteNumber + "']";
    pushActivityEvent('OPENED', anchorValue);
}


var assignmentOpenedTime;
function pushAssignmentOpened() {
    pushActivityEvent('OPENED');
    assignmentOpenedTime = new Date();
}

function pushAssignmentClosed(_scorePercent, _timeMinutes) {
    if (!_timeMinutes && assignmentOpenedTime) {
        _timeMinutes = (new Date() - assignmentOpenedTime) / 1000 / 60;
    }
    pushActivityEvent('CLOSED', null, _scorePercent, _timeMinutes);
}
//ssanders: END: Added

// ########################  END Onsophic ##################################


function checkAnswer(number, response) {
  // check response against key[number-1].  If number is not an integer,
  // calls findNum(number) to remove characters to try to leave an integer.
    var theQuestion = findNum(number);
    var truth = scoreProblem(crypt(key[theQuestion-1],randSeed.toString()),response);
    if (truth) {
        document.images[boxList[theQuestion-1]].src = rightImgSrc;
        document.images[boxList[theQuestion-1]].alt = "Correct!";
    } else {
        document.images[boxList[theQuestion-1]].src = wrongImgSrc;
        document.images[boxList[theQuestion-1]].alt = "Sorry, wrong answer!";
    }
    var questionsAndAnswers = [];
    questionsAndAnswers[theQuestion] = truth;
    pushQuestionsWorked(questionsAndAnswers); //* Onsophic
    return(truth);
}

function isAnswered(qVal) { // checks whether the student answered a question
    var n = findNum(qVal) - 1;
    var iA = false;
    var inx;
    var el = document.forms[0].elements[qVal];
    var qT = el.type;
    if (qT == 'select-one') {
        inx = el.selectedIndex;
        if (inx > 0) {
            iA = true;
        }
    } else if (qT == 'select-multiple') {
        resp = parseMultiple(el.options);
        if (resp != null && resp != '' && resp != '?') {
            iA = true;
        }
    } else if (qT == 'text') {
        resp = el.value;
       if (resp != null && resp != '' && removeAllBlanks(resp) != null &&
                 removeAllBlanks(resp).length > 0) {
            iA = true;
        }
    } else if (qT == 'radio') { // incomprehensible bugs with this
        for (var i=0; i < el.length; i++) {
            if (el[i].checked) {
                iA = true;
            }
        }
    } else if ( typeof(qT) == 'undefined' || qT == null ) { // assume it is a radio
        for (var i=0; i < el.length; i++) {
            if (el[i].checked) {
                iA = true;
            }
        }
    } else {
        alert('Error #1 in irGrade.js.isAnswered(): input type ' + qT +
           ' is not supported!');
    }
    return(iA);
}


function giveAnswer(number) {
 // display the answer to question[number] in a visibility-controlled div, provided the student has
 // tried to answer the question; else display a warning
    ansText = parseKey(crypt(key[findNum(number)-1], randSeed.toString()))[2];
    var q = findNum(number);
    var ansSpan = $('#ansSpan' + q.toString());
    if ($(ansSpan).css('display') == 'block') {  // hide the answer
           $(ansSpan).css('display','none')
                     .html('');
    } else {                                         // show the answer, or a warning if response is blank
       var qStr = '<p>[<a onClick = "$(\'#ansSpan' + q.toString() + '\').css(\'display\',\'none\');">-</a>]';
       if (isAnswered(number)) {
           qStr += '<span class="correctSpan">Answer: ' + ansText + '</span>.';
       } else {
           qStr += '<span class="warnSpan">You must answer before you may see the solution.</span>';
       }
       qStr += '</p>';
       $(ansSpan).html(qStr)
                 .css('display','block');
    }
    return(true);
}

function validEmail(e) { // checks whether e appears to be a valid email address
    var okEmailChars="._-@:%0123456789abcdefghijklmnopqrstuvwxyz";
    var truth = true;
    if (e == null || e.length == 0) {
        truth=false;
    } else {
        et = trimToLowerCase(e);
        if (et.indexOf('@') == -1 ||
            ( (et.lastIndexOf('.') != et.length - 4 ) &&
              (et.lastIndexOf('.') != et.length - 3 ) ) )
            truth = false;
        else if (et.indexOf('@') != et.lastIndexOf('@')) {
            truth = false;
        } else {
            for (var i=0; i < et.length; i++) {
                if(okEmailChars.indexOf(et.charAt(i)) < 0) {truth = false;}
            }
        }
    }
    return(truth);
}

function validSID(s){ // check whether SID is valid
/* REMOVED PBS 11/7/2012
    var digits="0123456789";
    var truth = false;
    if (s.length == 8 && (s.charAt(0) == "1" || s.charAt(0) == "2") || s.length == 9) {
        truth = true;
        if (s.match(/[^1234567890]/)) {
             truth = false;
        }
    }
    return(truth);
*/
    return(true);
}

function validateLabletSubmit(theForm){
// check that various form entries are filled in correctly, submit or cancel
    if (validateLablet(theForm)){
        return(labletSubmit(theForm));
    } else {
        return(false);
    }
}

function labletSubmit(theForm) {
     var OK = false;
     confirmStr = 'Your assignment is ready to submit. \nPress "OK" to submit it, or "Cancel" to return to the assignment.';
     if (confirm(confirmStr)) {
         setExtraInputs(theForm);
         pushAssignmentClosed(theForm.elements['score'].value);
         jsonIterationCount = 0;
         waitForJsonRequest(function() {    //ssanders: Have to wait, because the submit() replaces the DOM/JS
             setSubmitCookie(setNum.toString(),theForm,false);
             document.forms[1].action = graderActionURL;
             var s = collectResponses(theForm,true,true);
             document.forms[1].elements['contents'].value = crypt(s,bigPi);
             document.forms[1].submit();
         });
         OK = true;
     } else {
        alert('Your assignment has NOT been submitted.');
        OK = false;
     }
     return(OK);
}

function validateLablet(theForm) {
    if (theForm.lastName.value == null || theForm.lastName.value.length == 0 ||
             allBlanks(theForm.lastName.value) ) {
        alert('Last Name is missing');
        theForm.lastName.focus();
        return(false);
    } else if (!allLetters(theForm.lastName.value) ) {
        alert('Illegal character(s) in Last Name');
        theForm.lastName.focus();
        return(false);
    } else if (theForm.firstName.value == null ||
          theForm.firstName.value.length == 0 || allBlanks(theForm.firstName.value)) {
        alert('First Name is missing');
        theForm.firstName.focus();
        return(false);
    } else if (!allLetters(theForm.firstName.value) ) {
        alert('Illegal character(s) in First Name');
        theForm.firstName.focus();
        return(false);
    } else if ( !validEmail(theForm.email.value)) {
        alert('Email address is missing or invalid');
        theForm.email.focus();
        return(false);
    }
    var OK = false;
    if (enrollList.indexOf(CryptoJS.SHA256(trimToLowerCase(theForm.sid.value) + ',' +
                trimToLowerCase(theForm.email.value)).toString()) > -1) {
          OK = true;
          theForm.lastName.value = trimBlanks(theForm.lastName.value);
          theForm.firstName.value = trimBlanks(theForm.firstName.value);
          theForm.email.value = trimToLowerCase(theForm.email.value);
          theForm.sid.value = trimBlanks(theForm.sid.value);
    } else {
          alert('You do not seem to be enrolled; please check that you entered your ID and email ' +
                'address correctly and that this page is the correct assignment page for the class ' +
                'you are enrolled in.');
    }
    return(OK);
}

function saveResponses(setName,theForm,saveAns) {
    if(setSubmitCookie(setName,theForm,false)) {
        confirm('Your answers have been saved as a cookie on your computer.\n' +
               'Cookies are NOT RELIABLE storage--do not count on them!\n' +
               'You should write your answers down, too.\n' +
               'The cookie will be erased automatically in ' +
               cookieExpireDays.toString() + ' days or less.');
        return(true);
    } else {
        alert('Error #1 in irGrade.saveResponses:\nYour answers might NOT have been saved,\n' +
                'or previous answers might have been deleted, because the cookie became too large.');
        return(false);
    }
}

function setSubmitCookie(fff,theForm,idInfo){
    var ok = true;
    var s = collectResponses(theForm,false,idInfo);
    document.cookie = crypt(fff + theForm.sid.value, theForm.sid.value) +
                       '=' + crypt(s, theForm.sid.value) + ';EXPIRES=' + expireTimeString();
    if (document.cookie.length > 2048) {
         deleteAllCookies();
         document.cookie = crypt(fff + theForm.sid.value, theForm.sid.value) +
                            '=' + crypt(s, theForm.sid.value) + ';EXPIRES=' + expireTimeString();
         ok = false;
    }
    return(ok);
}

function recoverResponses() {
    if (continueLab == null) {
        return(false);
    } else {
        var theSid = getCookieVal(continueLab,"sid");
        var thePw = theSid;
        var tv = false;
        var theForm = document.forms[0];
        var ascStr = crypt(setNum.toString() + theSid, thePw)+ '=';
        var searchStr = document.cookie;
        var startInx = searchStr.indexOf(ascStr);
        if (startInx < 0 ) {
            return(false);
        }
        searchStr = searchStr.substring(startInx+ascStr.length,searchStr.length);
        var endInx = searchStr.indexOf(';');
        if (endInx > -1 ) {
            searchStr = searchStr.substring(0,endInx);
        }
        searchStr = crypt(searchStr,thePw) + "&";
        var qName;
        var aText;
        var inx;
        var ampInx;
        var elem;
        for (var i=0; i < theForm.elements.length; i++) {
            qName = theForm.elements[i].id + '=';
            if (qName.indexOf('Q') == 0) {
                elem = theForm.elements[i];
                if (elem.type == 'select-multiple') {
                    while (searchStr.indexOf(qName) > -1) {
                           inx = searchStr.indexOf(qName);
                           searchStr = searchStr.substring(inx+qName.length,searchStr.length);
                           ampInx = searchStr.indexOf('&');
                           qText = unescape(searchStr.substring(0,ampInx));
                           // search for option to select
                           var ag = false;
                           for (var j=0; j < elem.length; j++) {
                                if (elem[j].value == qText) {
                                    elem.options[j].selected = true;
                                    ag = true;
                                }
                           }
                    }
                } else if (elem.type == 'select-one') {
                       inx = searchStr.indexOf(qName);
                       if (inx > -1){
                           searchStr = searchStr.substring(inx+qName.length,searchStr.length);
                           ampInx = searchStr.indexOf('&');
                           qText = unescape(searchStr.substring(0,ampInx));
                           // search for option to select
                           for (var j=0; j < elem.length; j++) {
                             if (elem[j].value == qText) {
                                elem.options[j].selected = true;
                             }
                           }
                    }
                 } else if (elem.type == 'text') {
                       inx = searchStr.indexOf(qName);
                       if (inx > -1) {
                           searchStr = searchStr.substring(
                                inx+qName.length,searchStr.length);
                           ampInx = searchStr.indexOf('&');
                           qText = unescape(searchStr.substring(0,ampInx));
                           elem.value = qText;
                       }
                } else if (elem.type == null || elem.type == 'radio' ||
                           elem.type == 'undefined') {
                    inx = searchStr.indexOf(qName);
                       if (inx > -1){
                           searchStr = searchStr.substring(inx+qName.length,searchStr.length);
                           ampInx = searchStr.indexOf('&');
                           qText = unescape(searchStr.substring(0,ampInx));
                           if (elem.value == qText) {
                                elem.checked = true;
                           }
                       }
                } else {
                    alert('Error #1 in irGrade.recoverResponses(): unsupported problem type ' +
                        elem.type + '!');
                    return(false);
                }
            }
        }
    }
    return(true);
}

function collectResponses(theForm,saveAs,saveId) {
    var typ;
    var nam;
    var s = '';
    s += 'randSeed=' + escape(randSeed) + '&';
    for (var i=0; i < theForm.elements.length; i++) {
        typ = theForm.elements[i].type;
        nam = theForm.elements[i].id;
        if (typ == "button" || typ == "submit" || typ == "reset") {
        } else if (!saveId && (nam == "lastName" || nam == "firstName" ||
                             nam == "sid" || nam == "sid2" || nam == "email" ||
                             nam == "passwd" || nam == "passwd2")) {
        } else if (typ == "select-one") {
            s += escape(nam) + '=' +
                 escape(theForm.elements[i].options[
                   theForm.elements[i].options.selectedIndex].value)+ '&';
        } else if (typ == "select-multiple") {
            for (var j=0; j < theForm.elements[i].options.length; j++) {
                if (theForm.elements[i].options[j].selected) {
                    s += escape(nam) + '=' +
                        escape(theForm.elements[i].options[j].value) + '&';
                }
            }
        } else if (typ == "radio") {
            if (theForm.elements[i].checked) {
                s += escape(nam) + '=' +
                    escape(theForm.elements[i].value) + '&';
            }
        } else {
            s += escape(nam) + '=' + escape(trimBlanks(theForm.elements[i].value)) + '&';
        }
    }
    if (saveAs) {
        for (var i=0; i < key.length; i++) {
            s += escape('a' + (i+1).toString()) + '=' + escape(crypt(key[i],randSeed.toString()).toString()) + '&';
        }
    }
    if (s[s.length-1] == "&") {
        s = s.substring(0,s.length - 1);
    }
    return(s);
}

function labInstrSetUp(seed,sn) {
    isLab = false;
    dfStat = 'Tools for SticiGui Assignment ' + sn.toString();
    sectionContext = '';
    window.id= 'setj';
    setNum = sn;
    cNum = assignmentPrefix + setNum;
    HI = true;
    writeProblemSetHead(setNum);
    return(true);
}

function labSetUp(seed, sn) {
    isLab = true;
    dfStat = 'SticiGui Assignment ' + sn.toString();
    sectionContext = '';
    window.id= 'seti';
    setNum = sn;
    cNum = assignmentPrefix + setNum;
    HI = true;
    if (typeof(parent.sAns) == 'undefined') {
        CA = 'invalid';
    } else {
        CA = parent.sAns;
    }
    if (seed != "SeEd") {
        rand = new rng(parseInt(seed));
    } else {
        continueLab = parent.continueLab;
        if (continueLab == null || CA == 'invalid') {
            alert('Error #1 in irGrade.labSetUp()!\n' +
                'Assignment not initialized correctly.\n' +
                'You must use the Assignment Form to go to this page.\n' +
                'If you did, make sure your browser is configured to accept cookies, ' +
                ' and try again.\n ');
            document.close();
            window.close();
            parent.close();
            return(false);
        } else {
            var searchStr = continueLab + '&';
            var pat = "randSeed=";
            var inx = searchStr.indexOf(pat);
            if (inx < 0) {
                alert('Error #2 in irGrade.labSetUp()!\n' +
                    'Assignment not recovered correctly.\n' +
                    'Questions may have changed!');
                rand = new rng();
            } else {
                searchStr = searchStr.substring(
                    inx + pat.length, searchStr.length);
                randSeed = unescape(searchStr.substring(0,searchStr.indexOf('&')));
                rand = new rng(randSeed);
            }
        }
    }
    randSeed = rand.getSeed();
    writeProblemSetHead(setNum);
    return(true);
}

function setRequiredInputs(theForm) {
    theForm.elements['lastName'].value =  getCookieVal(continueLab,"lastName");
    theForm.elements['firstName'].value = getCookieVal(continueLab,"firstName");
    theForm.elements['email'].value = getCookieVal(continueLab,"email");
    theForm.elements['sid'].value = getCookieVal(continueLab,"sid");
    theForm.elements['sid2'].value = getCookieVal(continueLab,"sid");
    theForm.elements['passwd'].value = getCookieVal(continueLab,"sid");
    theForm.elements['passwd2'].value = getCookieVal(continueLab,"sid");
    return(true);
}

function setExtraInputs(theForm) {
    theForm.elements['inlinekey'].value = inlinePrefix + (qCtr-1).toString();
    theForm.elements['dFile'].value = parent.dFile;
    theForm.elements['teacher'].value = parent.teacher;
    theForm.elements['class'].value = parent.course;
    theForm.elements['maxSubmits'].value = parent.maxSubmits;
    theForm.elements['showWrongAfterSubmits'].value = parent.showWrongAfterSubmits;
    theForm.elements['extrainfo'].value = escape('seed=' + randSeed.toString() +
                             '&irGradeVersion=' + irGradeModTime.toString() +
                             '&submitTime=' + (new Date()).toString() +
                             '&assignmentname=' + assignmentTitles[assignmentNumbers[theChapter]][2]
                          );
    var nRight = 0;
    var qVal;
    var resp;
    var qType;
    var questionsAndAnswers = [];
    for (var i=1; i < qCtr; i++) {
        qVal = 'Q' + i.toString();
        qType = theForm.elements[qVal].type;
        if (qType == 'select-one') {
            resp = theForm.elements[qVal].options[
               theForm.elements[qVal].options.selectedIndex].value;
        } else if (qType == 'select-multiple') {
            resp = parseMultiple(theForm.elements[qVal].options);
        } else if (qType == 'text') {
            resp = theForm.elements[qVal].value;
        } else if (qType == 'radio') {
            if (theForm.elements[qVal].checked) {
                resp = theForm.elements[qVal].value;
            }
        } else if (qType == null || qType == 'undefined') { // radio too!
            if (theForm.elements[qVal].checked) {
                resp = theForm.elements[qVal].value;
            }
        } else {
            alert('Error #1 in irGrade.setExtraInputs(): Input type ' + qType +
               ' in question ' + qVal + ' is not supported!');
        }
        var sp = scoreProblem(crypt(key[i-1], randSeed.toString()), resp);
        if (sp) {
              nRight++;
        }
        questionsAndAnswers[i] = sp; // Onsophic
    }
    pushQuestionsWorked(questionsAndAnswers); // Onsophic
    theForm.elements['score'].value = roundToDig(100*nRight/(qCtr - 1),2).toString();
    return(true);
}


// ============================================================================
// ============================ SPECIAL HTML GENERATORS =======================

function styleSheetRef(relPath) {
    if (typeof(relPath) == 'undefined' || relPath == null || relPath.length == 0) {
    	relPath = '..';
    }
    return('<link rel="stylesheet" type="text/css" href="' + relPath + cssBase.toString() + '" />');
}

function writeChapterHead(seed, chTit, titStr, showSticiGui, relPath) {
    theChapter = null;
    showQMarks = false;
    if (typeof(showSticiGui) == 'undefined' || showSticiGui == null) {
            showSticiGui = true;
    }
    var noTitStr = false;
    if (typeof(titStr) == 'undefined' || titStr == null || titStr.length == 0) {
            noTitStr = true;
    }
    if (typeof(relPath) == 'undefined' || relPath == null || relPath.length == 0) {
    	relPath = '..';
    }
    if (typeof(chTit) == 'undefined' || chTit == null || chTit.length == 0) {
    	theChapter = null;
    } else {
	    theChapter = chapterNumbers[chTit];
    }
    if (theChapter == null) {
        if (noTitStr) {
                alert('Error in irGrade.writeChapterHead(): chapter ' + chTit +
                      ' is not in the index!');
        }
    }
    if (seed != "SeEd") {
        rand = new rng(parseInt(seed));
    } else {
        rand = new rng();
    }
    randSeed = rand.getSeed();
    window.id = 'bookWin';
    if (noTitStr) {
        theChapterTitle = chapterTitles[theChapter][0];
    } else {
        theChapterTitle = titStr;
    }
    if (showSticiGui) {
            dfStat = 'SticiGui ' + removeMarkup(theChapterTitle);
    } else {
            dfStat = theChapterTitle;
    }
    var qStr =  metaTagXHT + styleSheetRef(relPath) +
                   '<title>' + dfStat + '</title>' +
                   '<base target="glossWin">';
    document.writeln(qStr);
    CA = (1==1);
    HI = false;
    randSeed = rand.getSeed();
    sectionContext = '';
    return(true);
}

function writeChapterNav(relPath) {
    if (typeof(relPath) == 'undefined' || relPath == null || relPath.length == 0) {
    	relPath = '..';
    }
    var qStr =
              /* REMOVED PBS 11/2/2012
                '<div class="loginStub" id="loginStub"><a id="loginLink" href="http://www.willthatbeonthefinal.com:8088/portal" target="_self">' +
                '&laquo;login</a></div>' +
                '<div class="loginBox" id="loginBox">If you are enrolled in a course using these materials, please ' +
                '<a href="http://www.willthatbeonthefinal.com:8088/portal" target="_self">log into the class portal</a>.</div>' +
              */
                '<div id="divNav"><ul id="navMenu" class="compact"><li><a href="#firstContent" ' +
                'title="skip navigation" target="_self">Menu&nbsp;&raquo;</a>' +
		'<ul>' +
                '<li><a href="' + relPath + '/index.htm" title="SticiGui Homepage" target="_top">Home</a></li>' +
                '<li><a href="' + relPath + '/Text/index.htm" title="SticiGui Text Table of Contents" ' +
                   'target="_top">Text Table of Contents</a></li>' +
                '<li><a href="http://www.youtube.com/course?list=PL10921DED3A8BFF53" ' +
                   'target="_new" title="Online Lectures">Online Lectures</a></li>' +
                '<li><a href="' + relPath + '/Problems/index.htm" title="Online Assignments" ' +
                   'target="_top">Assignments</a></li>' +
                '<li><a href="javascript:void(0)" title="calculator applet" onClick="calcWin = ' +
                   'window.open(\''
                             + relPath + '/../Java/Html/StatCalc.htm\',\'calcWin\',' +
                   '\'toolbar=no,location=no,directories=no,status=no,' +
                   'scrollbars=yes,resizable=yes,width=320,height=200,top=0,left=0\');"> ' +
                   'Calculator</a></li>' +
                '<li><a href="' + relPath + '/../Java/Html/index.htm" title="Java Tools and Demos" ' +
                   'target="_self">Tools &amp; Demos&nbsp;&raquo;</a>' +
                     '<ul>' +
    			  '<li><a href="' + relPath + '/../Java/Html/BinHist.htm" target="lablet">Binomial Histogram</a></li>' +
                          '<li><a href="' + relPath + '/../Java/Html/StatCalc.htm" target="lablet">Calculator</a></li>' +
                          '<li><a href="' + relPath + '/../Java/Html/chiHiLite.htm" target="lablet">Chi-square distribution</a></li>' +
                          '<li><a href="' + relPath + '/../Java/Html/HistControl.htm" target="lablet">Controlling for variables</a></li>' +
                          '<li><a href="' + relPath + '/../Java/Html/Ci.htm" target="lablet">Confidence Intervals</a></li>' +
                          '<li><a href="' + relPath + '/../Java/Html/Correlation.htm" target="lablet">Correlation and Regression</a></li>' +
                          '<li><a href="' + relPath + '/../Java/Html/HistHiLite.htm" target="lablet">Histogram</a></li>' +
                          '<li><a href="' + relPath + '/../Java/Html/lln.htm" target="lablet">Law of Large Numbers</a></li>' +
                          '<li><a href="' + relPath + '/../Java/Html/NormApprox.htm" target="lablet">Normal Approximation to Data</a></li>' +
                          '<li><a href="' + relPath + '/../Java/Html/StandardNormal.htm" target="lablet">Normal Curve</a></li>' +
                          '<li><a href="' + relPath + '/../Java/Html/NormHiLite.htm" target="lablet">Normal Probabilities</a></li>' +
                          '<li><a href="' + relPath + '/../Java/Html/ProbCalc.htm" target="lablet">Probability Calculator</a></li>' +
                          '<li><a href="' + relPath + '/../Java/Html/SampleDist.htm" target="lablet">Sampling Distributions</a></li>' +
                          '<li><a href="' + relPath + '/../Java/Html/ScatterPlot.htm" target="lablet">Scatterplots</a></li>' +
                          '<li><a href="' + relPath + '/../Java/Html/tHiLite.htm" target="lablet">Student\'s t Distribution</a></li>' +
                          '<li><a href="' + relPath + '/../Java/Html/Venn.htm" target="lablet">Venn Diagram (2 subsets)</a></li>' +
                          '<li><a href="' + relPath + '/../Java/Html/Venn3.htm" target="lablet">Venn Diagram (3 subsets)</a></li>' +
                       '</ul>' +
                '</li>' +
                '<li><a href="' + relPath + '/Review/index.htm" title="Exam Review Materials" ' +
                   'target="_self">Review</a></li>' +
                '<li><a href="' + relPath + '/Text/gloss.htm" title="SticiGui Statistics Glossary" ' +
                   'target="_new">Glossary</a></li>' +
                '<li><a href="' + relPath + '/Text/references.htm" title="SticiGui Bibliography" ' +
                   'target="_self">Bibliography</a></li>' +
                '<li><a href="' + relPath + '/minimum.htm" title="SticiGui Minimum System Requirements" ' +
                   'target="_self">System Requirements</a></li>' +
                '<li><a href="' + relPath + '/../index.html" title="Author\'s Homepage" ' +
                   'target="_new">Author\'s Homepage</a></li>' +
                '</ul></li></ul></div>';
    document.writeln(qStr);
    if (window.attachEvent) {
         window.attachEvent("onload", sfHover);
    }
}

function sfHover() {
	var sfEls = document.getElementById("navMenu").getElementsByTagName("LI");
	for (var i=0; i<sfEls.length; i++) {
		sfEls[i].onmouseover=function() {
			this.className+= " sfhover";
		}
		sfEls[i].onmouseout=function() {
			this.className=this.className.replace(new RegExp(" sfhover\\b"), "");
		}
	}
        return(true);
}


function writeChapterTitle(s) {
    qStr = '<h1><a id="firstContent"></a>';
    if (typeof(s) != 'undefined' && s != null && s != '') {
         qStr += s;
    } else if (typeof(theChapter) != 'undefined' && theChapter != null) {
         qStr += 'Chapter ' + theChapter.toString();
    } else {
         qStr += 'SticiGui';
    }
    qStr +=  '</h1>';
    document.writeln(qStr);
    return(true);
}

function examSetUp(seed, sName, sn) {
    isLab = false;
    showQMarks = false;
   sectionContext = '';
    window.id= 'seti';
    examName = sName;
    examNum = sn;
    cNum = sn;
    HI = true;
    if (seed != "SeEd") {
        rand = new rng(parseInt(seed));
    } else {
        rand = new rng();
    }
    randSeed = rand.getSeed();
    writeExamHeader(examName, examNum.toString() + '.' + randSeed.toString() );
    dfStat = 'SticiGui ' + examName + ' ' + examNum.toString();
    return(true);
}

function writeExamHeader(exNam, exVer, relPath) {
    if (typeof(relPath) == 'undefined' || relPath == null || relPath.length == 0) {
    	relPath = '..';
    }
    var qStr =  metaTagXHT + styleSheetRef(relPath) +
                   '<title>' + dfStat + '</title>' +
                   '<base target="glossWin">' +
                   '<form id="labletForm" method="POST" accept-charset="UTF-8"><h1><a id="firstContent"></a>' +
                   '<a href="../index.htm" target="_new">SticiGui</a> ' + exNam +
                   '</h1><h2> Version ' + exVer.toString() + '</h2>';
    document.writeln(qStr);
    return(true);
}

$(document).ready(function() {
    eval(sectionContext);
    if (  (typeof(document.forms) != 'undefined') && (document.forms != null) &&
            (document.forms.length > 0 ) && !isLab )  {
        document.forms[0].reset();
    }
    if (isLab) {
        recoverResponses();
    }
    $(".solution").hide();
    $(".solLink").click(function() {
          $(this).next().toggle()
          if ($(this).text() == '[+Solution]') {
              $(this).text('[-Solution]');
          } else {
              $(this).text('[+Solution]');
          }
    });
    $(".footnote").css('display','block')
                  .hide();
    $(".footnoteLink").click(function() {
          $(this).parent().next().toggle()
          if ($(this).text() == '[+]') {
              $(this).text('[-]');
          } else {
              $(this).text('[+]');
          }
    });
});

function writeProblemSetFooter() {
    var qStr = '<div align="center"><center>';
    if (parent.allowSubmit) {
        qStr += '<input type="button" id="subBut" value="Submit for Grading" ' +
                   ' onClick="labletSubmit(this.form);" />' +
                '<input type="button" id="saveBut" value="Save Answers" ' +
                   ' onClick="saveResponses(setNum.toString(),this.form,false)" />';
    } else {
        qStr += '<input type="reset" id="reset" value="Clear Form" />';
    }
    qStr += '</center></div><p>&nbsp;</p></form>' +
            '<form method="POST" accept-charset="UTF-8">' + hiddenInput('contents',''); + '</form>';
    document.writeln(qStr);
    writeMiscFooter(false);
    return(true);
}

function writeSolution(p,text,solFunc) {
    var qStr = '<div class="solutionLink"><p><a class="solLink">[+Solution]</a> ' +
               '<span class="solution">';
    if (typeof(text) != 'undefined') {
       qStr += text;
    }
    qStr += '</span></p></div>';
    document.writeln(qStr);
    return(true);
}


function writeFootnote(p,label,text, print) {
    if (typeof(print) == 'undefined' || print == null) {
       print = true;
    }
    var chStr = '<strong>';
    if (parseInt(label) == findNum(label)) {
       if (typeof(chapterNumbers[cNum]) != 'undefined' && chapterNumbers[cNum] != null) {
        chStr += 'Note ' + (chapterNumbers[cNum]).toString() + '-';
       }
    }
    footnote = chStr + label + ':</strong> ' + text ;
    var qStr = '<sup><a class="footnoteLink">[+]</a></sup>' +
               '<span class="footnote">' + footnote + '</span> ';
    if (print) {
       document.writeln(qStr);
       return(true);
    } else {
       return(qStr);
    }
}

function writeChapterFooter(finalCommand, relPath) {
    if (typeof(relPath) == 'undefined' || relPath == null || relPath.length == 0) {
    	relPath = '..';
    }
    if (typeof(finalCommand) == 'undefined' || finalCommand == null || finalCommand.length == 0) {
        finalCommand = 'sticiBottom("' + relPath.toString() + '")';
    }
    eval(finalCommand);
    writeMiscFooter(false);
    return(true);
}

function writeMiscFooter(sr) {
    var stra = '<p class="center"><a href="';
    var strb = '/index.htm" target="_top">SticiGui Home</a></p>';
    if (typeof(sr) == 'boolean') {
        if (sr) {
           document.writeln(stra + '..' + strb);
        }
    } else {
        document.writeln(stra + sr + strb);
    }
    document.write('<p><font color="#FF0000"><small>&copy;' + copyYr + pbsRef +
        '. All rights reserved.</small></font><br /><small>Last generated ' + today +
        '. ');
    if ( !(typeof(pageModDate) == 'undefined') && !(pageModDate == null) ) {
        document.write('Content last modified ' + pageModDate + '. ');
    }
    document.writeln('</small></p>');
    return(true);
}

function sticiBottom(relPath) {
    if (typeof(relPath) == 'undefined' || relPath == null || relPath.length == 0) {
    	relPath = '..';
    }
    document.writeln('<hr /><div id="chapterMenu"><p class="center">Jump to chapter:</p><p class="center">|');
    for (var i=0; i< chapterTitles.length; i++) {
        var theChapterLinkName = chapterTitles[i][0];
        if (i > 1) {
            theChapterLinkName = i.toString();
        }
        document.writeln('<a href="' + relPath + '/Text/' + chapterTitles[i][1] + '.htm" ' +
                            'target="_self"><span id="chLink' + i.toString() + '">' +
                            theChapterLinkName + '</span></a> |');
    }
    document.writeln('</p></div>');
    if (theChapter != null) {
        var thisChapterLink = $('#chLink' + theChapter.toString());
        if (typeof(thisChapterLink) != null && thisChapterLink != null) {
            thisChapterLink.style.color = '#ffffff';
            thisChapterLink.style.backgroundColor = '#000000';
        }
    }
}

function writeProblemSetHead(sn) {
    continueLab = parent.continueLab;
    showQMarks = true;
    if (continueLab == null) {
        alert('Error #1 in irGrade.writeProblemSetHead()!\n' +
            'The assignment is not correctly initialized.\n' +
            'Make sure your browser is set up to accept cookies and try again.');
        document.close();
        window.close();
        return(false);
    } else {
        theChapter = sn;
        parent.theChapter = sn;
    	var qStr = metaTagXHT + styleSheetRef('..') +
                  '<title>SticiGui Assignment ' + theChapter.toString() +
                  '</title><base target="_self">'
    	document.writeln(qStr);
    	return(true);
    }
}

function writeProblemSetBody() {
    theChapter = parent.theChapter;
    try {
         var qStr = '<form id="labletForm" method="POST" accept-charset="UTF-8">' +
               hiddenInput('formname', 'SticiGuiSet' + assignmentNumbers[theChapter] ) +
               hiddenInput('lastName','') +
               hiddenInput('firstName','') +
               hiddenInput('email','') +
               hiddenInput('sid','') +
               hiddenInput('sid2','') +
               hiddenInput('passwd','') +
               hiddenInput('passwd2','') +
               hiddenInput('inlinekey','') +
               hiddenInput('dFile','') +
               hiddenInput('teacher','') +
               hiddenInput('class','') +
               hiddenInput('score','') +
               hiddenInput('maxSubmits','') +
               hiddenInput('showWrongAfterSubmits','') +
               hiddenInput('extrainfo','');
   } catch(e) {
         alert('Exception in writeProblemSetBody ' + e + ' theChapter ' + theChapter);
   }
    document.writeln(qStr);
    setRequiredInputs(document.forms[0]);
    qStr = '<h1><a id="firstContent"></a><a href="../index.htm" target="_new">SticiGui</a>: ' +
           assignmentTitles[assignmentNumbers[theChapter]][1] + '</h1>';
    document.writeln(qStr);
    //ssanders: BEGIN: Added
    pushAssignmentOpened();
    //ssanders: END: Added
    return(true);
}


function makeOptions(ans, pert, dig, extra) { // make a set of numerical options as answers,
                                              // converted to strings with dig digits of
                                              // precision. each answer is perturbed by a
                                              // signed multiple of pert.
                                              // extra is appended to each answer
    if (typeof(dig) == 'undefined' || dig == null) {
        dig = 0;
    }
    if (typeof(extra) == 'undefined' || extra == null) {
        extra = '';
    }
    var rs = listOfRandSigns(5);
    var rawOpt = new Array(5);
    for (var i=0; i < 5; i++) {
        rawOpt[i] = commify(roundToDig(ans + i*rs[i]*pert,dig)) + extra;
    }
    var optPerm = randPermutation(rawOpt,'inverse');
    optPerm[1] = alphabet[optPerm[1][0]];
    return(optPerm);
}

function makeRangeOptions(t, lo, hi, loLim, hiLim, dig, extra, iterLim) {
             // make multiple choice options
             // t is truth, lo is starting lower limit, hi is starting upper limit,
             // loLim is ultimate lower limit, hiLim is ultimate upper limit,
             // dig is digits of precision, extra is appended to each answer
             // steps up and down by 10% until t is within 0.35 of the space between
             // answers of one of the answers
    var pertFac = 0.07;      // amount by which to move endpoints each iteration
    var closeFac = 0.35;     // how much closer should answer be to one endpoint?
    var av;
    var altern = false;      // alternate moving upper and lower endpoints
    if (typeof(extra) == 'undefined' || extra == null) {
        extra = '';
    }
    if (typeof(dig) == 'undefined' || dig == null) {
        dig = 2;
    }
    var ok = false;
    var lim = 0;
    var maxIt;
    if (typeof(iterLim) == 'undefined' || iterLim == null) {
        maxIt = maxIterations;
    } else {
        maxIt = iterLim;
    }
    while(!ok && lim <= maxIt) {
       var ans = linspace(lo,hi,5);
       var d = closeFac*(ans[1]-ans[0]);    // want to be significantly closer to one end
       if (t <= ans[0] ) {
           av = alphabet[0];
           ok = true;
       } else if ( t >= ans[4]) {
           av = alphabet[4];
           ok = true;
       } else {
           for (i=0; i < ans.length; i++) {
              if (Math.abs(t - ans[i]) <= d) {
                 ok = true;
                 av = alphabet[i];
              }
            }
            if (altern) {
                lo = loLim + (1.0 - pertFac)*(lo-loLim);
                altern = !altern;
            } else {
                hi = hi + pertFac*(hiLim-hi);
                altern = !altern;
            }
        }
        if (lim++ == maxIt) {
            av = alphabet[0];
            var ref = Math.abs(t-ans[0]);
            for (var i=1; i < ans.length; i++ ) {
                var nref = Math.abs(t-ans[i]);
                if (nref < ref) {
                    ref = nref;
                    av = alphabet[i];
                }
            }
            ok = true;
            alert('Error #1 in irGrade.makeRangeOptions: maximum iterations exceeded in ' +
                  'problem ' + (pCtr-1).toString() + '.\nPlease report this to your ' +
                  'instructor.');
        }
    }
    var opt = new Array(ans.length);
    for (i=0; i < ans.length; i++) {
       opt[i] = commify((roundToDig(ans[i],dig)))+ extra;
    }
    var out = new Array(2);
    out[0] = opt;
    out[1] = av;
    return(out);
}

function makeProbOptions(t, lo, hi, dig, iter) {
    if (typeof(dig) == 'undefined' || dig == null) {
        dig = 0;
    }
    return(makeRangeOptions(100*t,100*lo,100*hi,0,100,dig,'%', iter));
}

function breakTF(groups, topt, fopt) { // randomly partition a collection of True statements
                                       // and a collection of False statements
    if (groups > Math.min(topt.length, fopt.length)) {
        alert('Error #1 in irGrade.breakTF(): too many groups!');
        return(false);
    } else {
        var per = Math.floor((topt.length+fopt.length)/groups);
        var groupsT = constrainedRandomPartition(topt, groups, per-1);
        var fSoFar = 0;
        var broken = new Array(groups);
        var nArray = new Array(groups);
        for (var g = 0; g < groups; g++) {
            broken[g] = new Array();
            nArray[g] = groupsT[g].length;
            for (var i=0; i < groupsT[g].length; i++) {
                broken[g][i] = groupsT[g][i];
            }
            for (var i = 0; i < per-groupsT[g].length; i++) {
                if (i+fSoFar < fopt.length) {
                    broken[g][i+groupsT[g].length] = fopt[i+fSoFar];
                }
            }
            fSoFar = fSoFar + per - groupsT[g].length;
         }
         return([broken,nArray]);
    }
}


function truthTable(title, valArr) { // make a 2 by 2 truth table
    if (valArr.length < 4) {
        alert('Error #1 in irGrade.truthTable: number of truth values is ' + valArr.length);
        return(null);
    } else {
        return(truthTableHeader(title) +
            '<tr><td headers="col0" align="center">T</td><td headers="col1" align="center">T</td>' +
            '<td headers="col2" align="center">' + valArr[0] + '</td></tr>' +
            '<tr><td headers="col0" align="center">T</td><td headers="col1" align="center">F</td>' +
            '<td headers="col2" align="center">' + valArr[2] + '</td></tr>' +
            '<tr><td headers="col0" align="center">F</td><td headers="col1" align="center">T</td>' +
            '<td headers="col2" align="center">' + valArr[1] + '</td></tr>' +
            '<tr><td headers="col0" align="center">F</td><td headers="col1" align="center">F</td>' +
            '<td headers="col2" align="center">' + valArr[3] + '</td></tr>' +
            '</table></center></div>'
        );
     }
}


function writeTruthTable(title, valArr) {
    document.writeln(truthTable(title, valArr));
    return(true);
}

function truthTableHeader(title) {
    return( '<a id="' + title.replace(/[ &!;<>|\\\/)(]/gi,'_') + '"></a><div class="plainTable"><center><table class="truthTable">' +
            '<caption>Truth Table</caption>' +
            '<tr><th id="col0" align="center" bgcolor="lightblue"><span class="math">p</span></th>' +
            '<th id="col1" align="center" bgcolor="lightblue"><span class="math">q</span></th>' +
            '<th id="col2" align="center" bgcolor="lightblue"><span class="math">' + title +
            '</span></th></tr>'
          );
}

function writeTruthTableProblem(title, ansArr) { // 2 by 2 truth table problem
    var opt = ['T','F'];
    document.writeln(truthTableHeader(title));
    var solArr = new Array(ansArr.length);
    if ( (typeof(ansArr)).toLowerCase() == 'function') {
        trueArr = [true, false];
        for (var i=0; i < 2; i++ ) {
            for (var j=0; j < 2; j++) {
                var inx = j + 2*i;
                if (ansArr(trueArr[j], trueArr[i])) {
                    solArr[inx] = 'a';
                } else {
                    solArr[inx] = 'b';
                }
            }
        }
    } else {
        for (var i=0; i < ansArr.length; i++) {
            if (ansArr[i]) {
                solArr[i] = 'a';
            } else {
                solArr[i] = 'b';
            }
        }
    }
    document.writeln('<tr><td headers="col0" align="center">T</td>' +
                     '<td headers="col1" align="center">T</td><td headers="col2" align="center">');
    writeSelectExercise(false, qCtr++, opt, solArr[0]);
    document.writeln('</td></tr><tr><td headers="col0" align="center">T</td>' +
                     '<td headers="col1" align="center">F</td><td headers="col2" align="center">');
    writeSelectExercise(false, qCtr++, opt, solArr[2]);
    document.writeln('</td></tr><tr><td headers="col0" align="center">F</td>' +
                     '<td headers="col1" align="center">T</td><td headers="col2" align="center">');
    writeSelectExercise(false, qCtr++, opt, solArr[1]);
    document.writeln('</td></tr><tr><td headers="col0" align="center">F</td>' +
                     '<td headers="col1" align="center">F</td><td headers="col2" align="center">');
    writeSelectExercise(false, qCtr++, opt, solArr[3]);
    document.writeln('</td></tr></table></center></div>');
}


// ===============================================
// ===formatting functions and html generators====
// ===============================================


function commify(num) { // punctuate number strings greater than 1,000 in magnitude
    var str;
    var strA = (removeAllBlanks(num.toString())).toLowerCase();
    if ( (strA.indexOf('e') > -1) || (strA.indexOf('d') > -1) ) {
        str = strA;  // don't mess with exponential notation
    } else {
        str = strA;
        var curLoc = str.length;
        if ( str.indexOf('.') > -1 ) {
            curLoc = str.indexOf('.');
        }
        var negSign = str.indexOf('-');
        for (var loc = curLoc-4; loc > negSign; loc -= 3) {
            str = str.substr(0,loc+1) + ',' + str.substr(loc+1, str.length);
        }
    }
    return(str);
}

function commifyList(list) { // commify an array
    var listStr = new Array(list.length);
    for (var j=0; j < list.length; j++) {
        listStr[j] = commify(list[j]);
    }
    return(listStr);
}



function writeBlankLines(k) {  // blank space
    if ( (typeof(k) == 'undefined') || (k == null) || (k < 0) ) {
        k = 1;
    }
    for (var i=0; i < k; i++) {
        document.writeln('<p>&nbsp;</p>');
    }
}

function roundToDig(num, dig) { // rounds a number or list to dig digits after the decimal place
    var powOfTen = Math.pow(10,dig);
    if ((typeof(num)).toLowerCase() == 'number') {
        var fmt = Math.round(num*powOfTen)/powOfTen;
        return(fmt);
    } else if ((typeof(num)).toLowerCase() == 'object' ||
               (typeof(num)).toLowerCase() == 'array') {
        var fmt = new Array(num.length);
        for (var i = 0; i < num.length; i++) {
            fmt[i] = Math.round(num[i]*powOfTen)/powOfTen;
        }
        return(fmt);
    } else {
        alert('Error #1 in irGrade.roundToDig(): argument (' + num.toString() + ') is not a number or an array');
        return(Math.NaN);
    }
}

function doubleToStr(num,dig) {
  // returns a string representation of num, rounded to dig digits after the decimal
    return(removeAllBlanks(roundToDig(num,dig).toString()));
}

function doubleToRange(num,fudge) {
  // returns a string range of num +/- fudge, separated by a colon
    var dig = -Math.floor(Math.log(Math.abs(fudge))/Math.log(10)) + 1;
    var s = roundToDig(num,dig);
    var range = doubleToStr(s - Math.abs(fudge),dig) + ':' +
                          doubleToStr(s+ Math.abs(fudge),dig);
    range = range.replace(/ /g,'');
    return(range);
}

function numToRange(num,fudge) {
  // returns a string range of num +/- fudge, separated by a colon
    if ( (typeof(fudge) == 'undefined') || (fudge == null) ) {
        fudge = fudgeFactor*num;
    }
    if (fudge == 0) {
        fudge = absFudge;
    }
    return(doubleToRange(num,Math.abs(fudge)));
}

function numToOrdinal(num) { // turns integer into string, appends appropriate suffix
    var st = (roundToDig(num,0)).toString();
    var suffArray = ['th','st','nd','rd','th','th','th','th','th','th'];
    var finalDig = parseInt(st.substr(st.length-1,st.length));
    var str = st;
    if (num == 11 || num == 12 || num == 13 ) {
       st = st + 'th';
    } else {
       st = st + suffArray[finalDig];
    }
    return(st);
}

function listToTable(header,list,orientation,centering,print,ft) {
  // formats an array of arrays as an html table
    if (typeof(centering) == 'undefined' || centering == null) {
        centering = 'right';
    }
    if (typeof(print) == 'undefined') {
        print = true;
    }
    if (typeof(ft) == 'undefined' || ft == null) {
        ft = '';
        eft = '';
    } else {
        ft = '<font size="' + ft + '">';
        eft = '</font>';
    }
    var rows = list.length;
    var cols;
    if (typeof(list[0]) != 'object') {
        cols=null;
    } else {
        cols = list[0].length;
    }
    var str = '<div class="plainTable"><center><table class="dataTable">';
    if (cols == null || cols == 1) {
        str += '<tr>';
        str += '<th align="' + centering + '" id="col1"> ' + ft + header + eft + '</th>\n';
        if (orientation == 'standard') {
            for (var j=0; j < rows; j++) {
                str +='<td align="' + centering + '" headers="col1">' +
                      ft + list[j] + eft + '</td>\n';
            }
            str += '</tr>';
        } else if (orientation == 'transpose') {
            str += '</tr>';
            for (var j=0; j < rows; j++) {
                str += '<tr>';
                str += '<td align="' + centering + '" headers="col1">' +
                       ft + list[j] + eft + '</td>\n';
                str += '</tr>';
            }
        } else {
            alert('Error #1 in irGrade.listToTable: unsupported orientation ' + orientation);
        }
    } else {
        if (orientation == 'standard') {
            for (var j = 0; j < rows; j++) {
                str += '<tr>';
                str += '<th align="' + centering + '" id="row' + j.toString() + '">' +
                       ft + header[j] + eft + '</th>\n';
                for (var i=0; i < cols; i++) {
                    str += '<td align="' + centering + '" headers="row' + j.toString() + '">' +
                           ft + list[j][i] + eft + '</td>';
                }
                str +='</tr>';
            }
         } else if (orientation == 'transpose') {
            str += '<tr>';
            for (var i=0; i < header.length; i++) {
                str += '<th align="' + centering + '" id="col' + i.toString() + '">' +
                        ft + header[i] + eft + '</th>\n';
            }
            str += '</tr>';
            for (var j = 0; j < cols; j++) {
                str += '<tr>';
                for (var i=0; i < rows; i++) {
                    str += '<td align="' + centering + '" headers="col' + j.toString() + '">' +
                           ft + list[i][j] + eft + '</td>\n';
                }
                str +='</tr>';
            }
         } else {
            alert('Error #2 in irGrade.listToTable: unsupported orientation ' + orientation);
         }
    }
    str += '</table></center></div>';
    if (print) {
        document.writeln(str);
        return(true);
    } else {
        return(str);
    }
 }

function arrayToRow(v,alignment) {
 // makes a row of a table from the elements of the array v, with specified alignment
    document.writeln('<tr>');
    for (var i=0; i < v.length; i++) {
        document.write('<td align="right">');
        document.write(v[i].toString());
        document.writeln('</td>');
    }
    document.writeln('</tr>');
    return(true);
}

// ============================================================================
// ========================= STATISTICAL SUBROUTINES ==========================

function mean(list) { // computes the mean of the list
    return(vSum(list)/list.length);
}

function vMult(a, list) { // multiply a vector times a scalar
    var list2 = new Array(list.length);
    for (var i=0; i < list.length; i++) {
        list2[i] = a*list[i];
    }
    return(list2);
}

function vScalarSum(list, scalar) { // adds the scalar to every component of the list
    var vs = new Array(list.length);
    for (var i =0; i < list.length; i++) {
        vs[i] = list[i] + scalar;
    }
    return(vs);
}

function vVectorSum(list1, list2) { // vector addition
    if (list1.length != list2.length) {
        alert('Error #1 in irGrade.vVectorSum: vector lengths are not equal');
        return(Math.NaN);
    } else {
        var vs = new Array(length(list1));
        for (var i =0; i < list1.length; i++) {
            vs[i] = list1[i] + list2[i];
        }
        return(vs);
    }
}

function vPointwiseMult(list1, list2) { // componentwise multiplication of two vectors
    var list3 = Math.NaN;
    if (list1.length != list2.length) {
        alert('Error #1 in irGrade.vPointwiseMult: vector lengths do not match!');
    } else {
        list3 = new Array(list1.length);
        for (var i=0; i < list1.length; i++) {
            list3[i] = list1[i]*list2[i];
        }
    }
    return(list3);
}

function vFloor(list) { // takes floor of all components
    var list2 = new Array(list.length);
    for (var i = 0; i < list.length; i++) {
        list2[i] = Math.floor(list[i]);
    }
    return(list2);
}

function vCeil(list) { // takes ceil of all components
    var list2 = new Array(list.length);
    for (var i = 0; i < list.length; i++) {
        list2[i] = Math.ceil(list[i]);
    }
    return(list2);
}

function vRoundToInts(list) { // round all components to the nearest int
    var list2 = new Array(list.length);
    var tmp;
    for (var i = 0; i < list.length; i++) {
        list2[i] = Math.floor(list[i]);
        if (list[i] - list2[i] >= 0.5) {
            list2[i]++;
        }
    }
    return(list2);
}

function vSum(list) { // computes the sum of the elements of list
    var tot = 0.0;
    for (var i = 0; i < list.length; i++) {
        tot += list[i];
    }
    return(tot);
}

function vProd(list) { // computes the product of the elements of list
    var p = 1.0;
    for (var i = 0; i < list.length; i++) {
        p *= list[i];
    }
    return(p);
}

function vCum(list) { // vector of cumulative sum
    var list2 = list;
    for (var i = 1; i < list.length; i++ ) {
        list2[i] += list2[i-1];
    }
    return(list2);
}

function vDiff(list) { // vector of differences; 1st element unchanged
    var list2 = new Array(list.length);
    for (var i = list.length-1; i > 0; i-- ) {
        list2[i] = list[i] - list[i-1];
    }
    list2[0] = list[0];
    return(list2);
}


function vInterval(list) { // vector of differences between successive elements; 0 subtracted from first element
    var list2 = new Array();
    list2[0] = list[0];
    for (var i = 1; i < list.length; i++) {
        list2[i] = list[i] - list[i-1];
    }
    return(list2);
}

function vZero(n) { // returns a vector of zeros of length n
    var list = new Array(n);
    for (var i=0; i < n; i++) {
        list[i] = 0.0;
    }
    return(list);
}

function vOne(n) { // returns a vector of ones of length n
    var list = new Array(n);
    for (var i=0; i < n; i++) {
        list[i] = 1.0;
    }
    return(list);
}

function twoNorm(list) { // two norm of a vector
    var tn = 0.0;
    for (var i=0; i < list.length; i++) {
        tn += list[i]*list[i];
    }
    return(Math.sqrt(tn));
}

function convolve(a,b) { // convolve two lists
    var c = new Array(a.length + b.length - 1);
    var left; var right;
    for (var i=0; i < a.length + b.length - 1; i++) {
        c[i] = 0;
        right = Math.min(i+1, a.length);
        left = Math.max(0, i - b.length + 1);
        for (var j=left; j < right; j++) {
            c[i] += a[j]*b[b.length - i - 1 + j];
        }
    }
    return(c);
}

function nFoldConvolve(a,n) {
    var b = a;
    for (var i=0; i < n; i++ ) {
        b = convolve(b,a);
    }
    return(b);
}

function numberLessThan(a,b) { // numerical ordering for javascript sort function
    var diff = parseFloat(a)-parseFloat(b);
    if (diff < 0) {
        return(-1);
    } else if (diff == 0) {
        return(0);
    } else {
        return(1);
    }
}

function numberGreaterThan(a,b) { // numerical ordering for javascript sort function
    var diff = parseFloat(a)-parseFloat(b);
    if (diff < 0) {
        return(1);
    } else if (diff == 0) {
        return(0);
    } else {
        return(-1);
    }
}

function sd(list) { // computes the SD of the list
    ave = mean(list);
    ssq = 0;
    for (var i = 0; i < list.length; i++) {
        ssq += (list[i] - ave)*(list[i] - ave);
    }
    ssq = Math.sqrt(ssq/list.length);
    return(ssq);
}

function sampleSd(list) { // computes the sample SD of the list
    ave = mean(list);
    ssq = 0;
    for (var i = 0; i < list.length; i++) {
        ssq += (list[i] - ave)*(list[i] - ave);
    }
    ssq = Math.sqrt(ssq/(list.length - 1.0));
    return(ssq);
}

function corr(list1, list2) {
// computes the correlation coefficient of list1 and list2
    if (list1.length != list2.length) {
        alert('Error #1 in irGrade.corr(): lists have different lengths!');
        return(Math.NaN);
    } else {
        var ave1 = mean(list1);
        var ave2 = mean(list2);
        var sd1 = sd(list1);
        var sd2 = sd(list2);
        var cc = 0.0;
        for (var i=0; i < list1.length; i++) {
            cc += (list1[i] - ave1)*(list2[i] - ave2);
        }
        cc /= sd1*sd2*list1.length;
        return(cc);
    }
}

function percentile(list,p) { // finds the pth percentile of list
    var n = list.length;
    var sList = new Array(n);
    for (var i=0; i < n; i++) sList[i] = list[i].valueOf();
    sList.sort(numberLessThan);
    var ppt = Math.max(Math.ceil(p*n/100),1);
    return(sList[ppt-1]);
}

function histMakeCounts(binEnd, data) {  // makes vector of histogram heights
        var nBins = binEnd.length - 1;
        var counts = new Array(nBins);
        for (var i=0; i < nBins; i++) {
            counts[i] = 0;
        }
        for (var i=0; i < data.length; i++) {
           for (var k=0; k < nBins - 1; k++) {
              if (data[i] >= binEnd[k] && data[i] < binEnd[k+1] ) {
                  counts[k] += 1;
              }
           }
           if (data[i] >= binEnd[nBins - 1] ) {
              counts[nBins - 1] += 1;
           }
        }
        for (var i=0; i < nBins; i++) {
           counts[i] /= data.length*(binEnd[i+1]-binEnd[i]);
        }
        return(counts);
}

function histMakeBins(nBins, data) { // makes equispaced histogram bins that span the range of data
        binEnd = new Array(nBins+1);
        dMnMx = vMinMax(data);
        for (var i=0; i < nBins+1; i++) {
           binEnd[i] = dMnMx[0] + i*(dMnMx[1] - dMnMx[0])/nBins;
        }
        return(binEnd);
}

function histEstimatedPercentile(pct, binEnd, counts) {  // estimates the pth percentile from a histogram
        var p = pct/100.0;
        var pctile;
        if (p > 1.0) {
            pctile = Math.NaN;
        } else if (p == 1.0) {
            pctile = binEnd[nBins];
        } else {
            var area = 0.0;
            var j = 0;
            while (area < p) {
               j++;
               area = histHiLitArea(binEnd[0], binEnd[j], binEnd, counts);
            }
            j--;
            area = p - histHiLitArea(binEnd[0], binEnd[j], binEnd, counts);
            var nextBinArea = histHiLitArea(binEnd[j], binEnd[j+1], binEnd, counts);
            pctile = binEnd[j] + (area/nextBinArea) * (binEnd[j+1] - binEnd[j]);
        }
        return(pctile);
}


function histHiLitArea(loEnd, hiEnd, binEnd, counts) { // area of counts from loEnd to hiEnd
          var nBins = binEnd.length - 1;
          var area = 0;
          if (loEnd < hiEnd) {
             for (var i=0; i < nBins; i++) {
                if( binEnd[i]  > hiEnd ||  binEnd[i+1] <= loEnd) {
                } else if (binEnd[i] >= loEnd && binEnd[i+1] <= hiEnd) {
                   area += counts[i]*(binEnd[i+1]-binEnd[i]);
                } else if (binEnd[i] >= loEnd && binEnd[i+1] > hiEnd) {
                   area += counts[i]*(hiEnd - binEnd[i]);
                } else if (binEnd[i] <= loEnd && binEnd[i+1] <= hiEnd) {
                   area += counts[i]*(binEnd[i+1]-loEnd);
                } else if (binEnd[i] < loEnd && binEnd[i+1] > hiEnd) {
                   area += counts[i]*(hiEnd - loEnd);
                }
            }
         }
      return(area);
}

function listOfRandSigns(n) { // random +-1 vector
    var list = new Array(n);
    for (var i=0; i < n; i++) {
        var rn = rand.next();
        if (rn < 0.5) {
            list[i] = -1;
        } else {
            list[i] = 1;
        }
    }
    return(list);
}

function listOfRandUniforms(n, lo, hi) { // n random variables uniform on (lo, hi)
    if ( (typeof(lo) == 'undefined') || (lo == null) ) {
        lo = 0.0;
    }
    if ( (typeof(hi) == 'undefined') || (hi == null) ) {
            hi = 1.0;
    }
    var list = new Array(n);
    for (var i=0; i < n; i++) {
        list[i] = lo + (hi-lo)*rand.next();
    }
    return(list);
}

function listOfRandInts(n, lo, hi) { // n random integers between lo and hi
    var list = new Array(n);
    for (var i=0; i < n; i++) {
        list[i] = Math.floor((hi+1 - lo)*rand.next()) + lo;
    }
    return(list);
}

function listOfDistinctRandInts(n, lo, hi) { // n dintinct random integers between lo and hi
    var list = new Array(n);
    var trial;
    var i=0;
    var unique;
    while (i < n) {
        trial = Math.floor((hi+1 - lo)*rand.next()) + lo;
        unique = true;
        for (var j = 0; j < i; j++) {
            if (trial == list[j]) unique = false;
        }
        if (unique) {
            list[i] = trial;
            i++;
        }
    }
    return(list);
}

function randomSample(list, ssize, replace) {
  // sample from list, size ssize w/ or w/o replacement.
  // default is without replacement
    var sample = new Array();
    var indices = new Array();
    if (replace != null && replace ) {
        indices = listOfRandInts(ssize,0,list.length-1);
    } else {
        indices = listOfDistinctRandInts(ssize,0,list.length - 1);
    }
    for (var i=0; i < ssize; i++) {
        sample[i] = list[indices[i]];
    }
    return(sample) ;
}

function randomPartition(list, n) {
  // randomly partition list into n nonempty groups
    var bars = listOfDistinctRandInts(n-1, 1, list.length-1).sort(numberLessThan);
    bars[bars.length] = list.length;
    var parts = new Array(n);
    for (var i = 0; i < parts.length; i++) {
        parts[i] = new Array();
    }
    for (var i=0; i < bars[0]; i++) {
        parts[0][i] = list[i];
    }
    for (var j=1; j < n; j++) {
        for (var i=0; i < bars[j]-bars[j-1]; i++) {
           parts[j][i] = list[bars[j-1]+i];
        }
    }
    return(parts);
}

function constrainedRandomPartition(list, n, mx) {
  // randomly partition list into n nonempty groups no bigger than mx
    if (n*mx < list.length) {
        alert('Error in irGrade.constrainedRandomPartition: mx too small!');
        return(false);
    } else {
        var bars = listOfDistinctRandInts(n-1, 1, list.length-1).sort(numberLessThan);
        bars[bars.length] = list.length;
        vm = vMinMax(vDiff(bars))[1];
        while (vm > mx) {
            bars = listOfDistinctRandInts(n-1, 1, list.length-1).sort(numberLessThan);
            bars[bars.length] = list.length;
            vm = vMinMax(vDiff(bars))[1];
        }
        var parts = new Array(n);
        for (var i = 0; i < parts.length; i++) {
            parts[i] = new Array();
        }
        for (var i=0; i < bars[0]; i++) {
            parts[0][i] = list[i];
        }
        for (var j=1; j < n; j++) {
            for (var i=0; i < bars[j]-bars[j-1]; i++) {
               parts[j][i] = list[bars[j-1]+i];
            }
        }
        return(parts);
    }
}

function multinomialSample(pVec, n) { // multinomial sample of size n with probabilities pVec
    pVec = vMult(1.0/vSum(pVec), pVec); // renormalize in case
    var pCum = vCum(pVec);
    var counts = vZero(pVec.length);
    var rv;
    var inx;
    for (var i=0; i < n; i++) {
        rv = rand.next();
        inx = 0;
        while ( (rv > pCum[inx]) && (inx < n) ) {
            inx++;
        }
        counts[inx]++;
    }
    return(counts);
}

function normPoints(n, mu, s, dig) {   // n normals with expected value mu, sd s, rounded to dig
    var round = true;
    if ( (typeof(dig) == 'undefined') || (dig == null) ) {
        var round = false;
    }
    var xVal = new Array(n);
    if (round) {
        for (var i=0; i < n; i++) {
            xVal[i] = roundToDig(mu + s*rNorm(),dig);
        }
    } else {
        for (var i=0; i < n; i++) {
            xVal[i] = mu + s*rNorm(),dig;
        }
    }
    return(xVal);
}

function cNormPoints(n, r) {
 // generate pseudorandom normal bivariate w/ specified realized correlation coefficient
    var xVal = new Array(n);
    var yVal = new Array(n);
    for (var i=0; i<n ; i++ ) {
        xVal[i]= rNorm();
        yVal[i] = rNorm();
    }
    var rAtt = corr(xVal, yVal);
    var s = sgn(rAtt)*sgn(r);
    var xBarAtt = mean(xVal);
    var yBarAtt = mean(yVal);
    var xSdAtt = sd(xVal);
    var ySdAtt = sd(yVal);
    var pred = new Array(n);
    var resid = new Array(n);
    for (var i=0; i < n; i++) {
        xVal[i] = (xVal[i] - xBarAtt)/xSdAtt;
        pred[i] = s*rAtt*xVal[i]*ySdAtt+ yBarAtt;
        resid[i] = s*yVal[i] - pred[i];
    }
    var resNrm = rms(resid);
    for (var i = 0; i < n; i++) {
        yVal[i] = Math.sqrt(1.0-r*r)*resid[i]/resNrm + r*xVal[i];
    }
    var ymnmx = vMinMax(yVal);
    var xmnmx = vMinMax(xVal);
    var xscl = 9.5/(xmnmx[1] - xmnmx[0]);
    var yscl = 9.5/(ymnmx[1] - ymnmx[0]);
    for (var i=0; i < n; i++) {
        xVal[i] = (xVal[i] - xmnmx[0]) * xscl  + 1.0;
        yVal[i] = (yVal[i] - ymnmx[0]) * yscl + 1.0;
    }
    var lists = new Array(xVal,yVal);
    return(lists);
}// ends cNormPoints

function listOfRandReals(n,lo,hi) { // n-vector of uniforms on [lo, hi]
    var list = new Array(n);
    for (var i=0; i < n; i++) {
        list[i] = (hi - lo)*rand.next() + lo;
    }
    return(list);
}

function rNorm() {  // standard normal pseudorandom variable
    var y = normInv(rand.next());
    return(y);
} // ends rNorm()

function normCdf(y) { // normal distribution cumulative distribution function
   return(0.5*erfc(-y*0.7071067811865475));
}

function erfc(x) { // error function
     var xbreak = 0.46875;     // for normal cdf
// coefficients for |x| <= 0.46875
    var a = [3.16112374387056560e00, 1.13864154151050156e02,
             3.77485237685302021e02, 3.20937758913846947e03,
             1.85777706184603153e-1];
    var b = [2.36012909523441209e01, 2.44024637934444173e02,
            1.28261652607737228e03, 2.84423683343917062e03];
// coefficients for 0.46875 <= |x| <= 4.0
    var c = [5.64188496988670089e-1, 8.88314979438837594e00,
             6.61191906371416295e01, 2.98635138197400131e02,
             8.81952221241769090e02, 1.71204761263407058e03,
             2.05107837782607147e03, 1.23033935479799725e03,
             2.15311535474403846e-8];
    var d = [1.57449261107098347e01, 1.17693950891312499e02,
             5.37181101862009858e02, 1.62138957456669019e03,
             3.29079923573345963e03, 4.36261909014324716e03,
             3.43936767414372164e03, 1.23033935480374942e03];
// coefficients for |x| > 4.0
    var p = [3.05326634961232344e-1, 3.60344899949804439e-1,
             1.25781726111229246e-1, 1.60837851487422766e-2,
             6.58749161529837803e-4, 1.63153871373020978e-2];
    var q = [2.56852019228982242e00, 1.87295284992346047e00,
             5.27905102951428412e-1, 6.05183413124413191e-2,
             2.33520497626869185e-3];
    var y, z, xnum, xden, result, del;

/*
Translation of a FORTRAN program by W. J. Cody,
Argonne National Laboratory, NETLIB/SPECFUN, March 19, 1990.
The main computation evaluates near-minimax approximations
from "Rational Chebyshev approximations for the error function"
by W. J. Cody, Math. Comp., 1969, PP. 631-638.
*/

//  evaluate  erf  for  |x| <= 0.46875

    if(Math.abs(x) <= xbreak) {
        y = Math.abs(x);
        z = y * y;
        xnum = a[4]*z;
        xden = z;
        for (var i = 0; i< 3; i++) {
            xnum = (xnum + a[i]) * z;
            xden = (xden + b[i]) * z;
        }
        result = 1.0 - x* (xnum + a[3])/ (xden + b[3]);
    } else if (Math.abs(x) <= 4.0) {
        y = Math.abs(x);
        xnum = c[8]*y;
        xden = y;
        for (var i = 0; i < 7; i++) {
            xnum = (xnum + c[i])* y;
            xden = (xden + d[i])* y;
        }
        result = (xnum + c[7])/(xden + d[7]);
        if (y > 0.0) {
            z = Math.floor(y*16)/16.0;
        } else {
            z = Math.ceil(y*16)/16.0;
        }
        del = (y-z)*(y+z);
        result = Math.exp(-z*z) * Math.exp(-del)* result;
    } else {
        y = Math.abs(x);
        z = 1.0 / (y*y);
        xnum = p[5]*z;
        xden = z;
        for (var i = 0; i < 4; i++) {
            xnum = (xnum + p[i])* z;
            xden = (xden + q[i])* z;
        }
        result = z * (xnum + p[4]) / (xden + q[4]);
        result = (1.0/Math.sqrt(Math.PI) -  result)/y;
        if (y > 0.0) {
            z = Math.floor(y*16)/16.0;
        } else {
            z = Math.ceil(y*16)/16.0;
        }
        del = (y-z)*(y+z);
        result = Math.exp(-z*z) * Math.exp(-del) * result;
    }
    if (x < -xbreak) {
        result = 2.0 - result;
    }
    return(result);
}

function normInv(p) {
    if ( p == 0.0 ) {
        return(Math.NEGATIVE_INFINITY);
    } else if ( p >= 1.0 ) {
        return(Math.POSITIVE_INFINITY);
    } else {
        return(Math.sqrt(2.0) * erfInv(2*p - 1));
    }
}

function erfInv(y) {
    var a = [ 0.886226899, -1.645349621, 0.914624893, -0.140543331];
    var b = [-2.118377725, 1.442710462, -0.329097515, 0.012229801];
    var c = [-1.970840454, -1.624906493, 3.429567803, 1.641345311];
    var d = [ 3.543889200, 1.637067800];
    var y0 = 0.7;
    var x = 0;
    var z = 0;
    if (Math.abs(y) <= y0) {
        z = y*y;
        x = y * (((a[3]*z+a[2])*z+a[1])*z+a[0])/
         ((((b[3]*z+b[2])*z+b[1])*z+b[0])*z+1.0);
    } else if (y > y0 && y < 1.0) {
        z = Math.sqrt(-Math.log((1-y)/2));
        x = (((c[3]*z+c[2])*z+c[1])*z+c[0]) / ((d[1]*z+d[0])*z+1);
    } else if (y < -y0 && y > -1) {
        z = Math.sqrt(-Math.log((1+y)/2));
        x = -(((c[3]*z+c[2])*z+c[1])*z+c[0])/ ((d[1]*z+d[0])*z+1);
    }
    x = x - (1.0 - erfc(x) - y) / (2/Math.sqrt(Math.PI) * Math.exp(-x*x));
    x = x - (1.0 - erfc(x) - y) / (2/Math.sqrt(Math.PI) * Math.exp(-x*x));

    return(x);
} // ends erfInv

function betaCdf( x,  a,  b) {
   if (a <= 0 || b <= 0) {
      return(Math.NaN);
   } else if (x >= 1) {
      return(1.0);
   } else if ( x > 0.0) {
      return(Math.min(incBeta(x ,a ,b),1.0));
   } else {
      return(0.0);
   }
}

function betaPdf( x,  a,  b) {
    if (a <= 0 || b <= 0 || x < 0 || x > 1) {
        return(Math.NaN);
    } else if ((x == 0 && a < 1) || (x == 2 && b < 1)) {
        return(Math.POSITIVE_INFINITY);
    } else if (!(a <= 0 || b <= 0 || x <= 0 || x >= 1)) {
        return(Math.exp((a - 1)*Math.log(x) + (b-1)*Math.log(1 - x) - lnBeta(a,b)));
    } else {
        return(0.0);
    }
}

function lnBeta( x, y) {
    return(lnGamma(x) + lnGamma(y) - lnGamma(x+y));
}

function betaInv( p,  a,  b) {
    if (p < 0 || p > 1 || a <= 0 || b <= 0) {
        return(Math.NaN);
    } else if ( p == 0 ) {
        return(Math.NEGATIVE_INFINITY);
    } else if ( p == 1) {
        return(Math.POSITIVE_INFINITY);
    } else {
        var maxIt = 100;
        var it = 0;
        var tol = Math.sqrt(eps);
        var work = 1.0;
        var next;
        var x;
        if (a == 0.0 ) {
            x = Math.sqrt(eps);
        } else if ( b == 0.0) {
            x = 1 - Math.sqrt(eps);
        } else {
            x = a/(a+b);
        }
        while (Math.abs(work) > tol*Math.abs(x) && Math.abs(work) > tol && it < maxIt) {
           it++;
           work = (betaCdf(x,a,b) - p)/betaPdf(x,a,b);
           next =  x - work;
           while (next < 0 || next > 1) {
               work = work/2;
               next = x - work;
           }
           x = next;
         }
         return(x);
     }
}

function lnGamma(x) {
/*  natural ln(gamma(x)) without computing gamma(x)
    P.B. Stark

      JavaScript subroutine is based on a MATLAB program by C. Moler,
      in turn based on a FORTRAN program by W. J. Cody,
      Argonne National Laboratory, NETLIB/SPECFUN, June 16, 1988.

      References:

      1) W. J. Cody and K. E. Hillstrom, 'Chebyshev Approximations for
         the Natural Logarithm of the Gamma Function,' Math. Comp. 21,
         1967, pp. 198-203.

      2) K. E. Hillstrom, ANL/AMD Program ANLC366S, DGAMMA/DLGAMA, May,
         1969.

      3) Hart, Et. Al., Computer Approximations, Wiley and sons, New
         York, 1968.
*/

     var d1 = -5.772156649015328605195174e-1;
     var p1 = [4.945235359296727046734888e0, 2.018112620856775083915565e2,
           2.290838373831346393026739e3, 1.131967205903380828685045e4,
           2.855724635671635335736389e4, 3.848496228443793359990269e4,
           2.637748787624195437963534e4, 7.225813979700288197698961e3];
     var q1 = [6.748212550303777196073036e1, 1.113332393857199323513008e3,
           7.738757056935398733233834e3, 2.763987074403340708898585e4,
           5.499310206226157329794414e4, 6.161122180066002127833352e4,
           3.635127591501940507276287e4, 8.785536302431013170870835e3];
     var d2 = 4.227843350984671393993777e-1;
     var p2 = [4.974607845568932035012064e0, 5.424138599891070494101986e2,
           1.550693864978364947665077e4, 1.847932904445632425417223e5,
           1.088204769468828767498470e6, 3.338152967987029735917223e6,
           5.106661678927352456275255e6, 3.074109054850539556250927e6];
     var q2 = [1.830328399370592604055942e2, 7.765049321445005871323047e3,
           1.331903827966074194402448e5, 1.136705821321969608938755e6,
           5.267964117437946917577538e6, 1.346701454311101692290052e7,
           1.782736530353274213975932e7, 9.533095591844353613395747e6];
     var d4 = 1.791759469228055000094023e0;
     var p4 = [1.474502166059939948905062e4, 2.426813369486704502836312e6,
           1.214755574045093227939592e8, 2.663432449630976949898078e9,
           2.940378956634553899906876e10, 1.702665737765398868392998e11,
           4.926125793377430887588120e11, 5.606251856223951465078242e11];
     var q4 = [2.690530175870899333379843e3, 6.393885654300092398984238e5,
           4.135599930241388052042842e7, 1.120872109616147941376570e9,
           1.488613728678813811542398e10, 1.016803586272438228077304e11,
           3.417476345507377132798597e11, 4.463158187419713286462081e11];
     var c = [-1.910444077728e-03, 8.4171387781295e-04,
          -5.952379913043012e-04, 7.93650793500350248e-04,
          -2.777777777777681622553e-03, 8.333333333333333331554247e-02,
           5.7083835261e-03];

     var lng = Math.NaN;
     var mach = 1.e-12;
     var den = 1.0;
     var num = 0;
     var xm1, xm2, xm4;

   if (x < 0) {
       return(lng);
   } else if (x <= mach) {
       return(-Math.log(x));
   } else if (x <= 0.5) {
      for (var i = 0; i < 8; i++) {
            num = num * x + p1[i];
            den = den * x + q1[i];
      }
      lng = -Math.log(x) + (x * (d1 + x * (num/den)));
   } else if (x <= 0.6796875) {
      xm1 = x - 1.0;
      for (var i = 0; i < 8; i++) {
         num = num * xm1 + p2[i];
         den = den * xm1 + q2[i];
      }
      lng = -Math.log(x) + xm1 * (d2 + xm1*(num/den));
   } else if (x <= 1.5) {
      xm1 = x - 1.0;
      for (var i = 0; i < 8; i++) {
         num = num*xm1 + p1[i];
         den = den*xm1 + q1[i];
      }
      lng = xm1 * (d1 + xm1*(num/den));
   } else if (x <= 4.0) {
      xm2 = x - 2.0;
      for (var i = 0; i<8; i++) {
         num = num*xm2 + p2[i];
         den = den*xm2 + q2[i];
      }
      lng = xm2 * (d2 + xm2 * (num/den));
   } else if (x <= 12) {
      xm4 = x - 4.0;
      den = -1.0;
      for (var i = 0; i < 8; i++)  {
         num = num * xm4 + p4[i];
         den = den * xm4 + q4[i];
      }
      lng = d4 + xm4 * (num/den);
   } else {
      var r = c[6];
      var xsq = x * x;
      for (var i = 0; i < 6; i++) {
         r = r / xsq + c[i];
      }
      r = r / x;
      var lnx = Math.log(x);
      var spi = 0.9189385332046727417803297;
      lng = r + spi - 0.5*lnx + x*(lnx-1);
    }
    return(lng);
} // ends lnGamma


function normPdf( mu,  sigma, x) {
     return(Math.exp(-(x-mu)*(x-mu)/(2*sigma*sigma))/
            (Math.sqrt(2*Math.PI)*sigma));
} // ends normPdf


function tCdf(df, x) { // cdf of Student's t distribution with df degrees of freedom
    var ans;
    if (df < 1) {
        ans = Math.NaN;
    } else if (x == 0.0) {
        ans = 0.5;
    } else if (df == 1) {
        ans = .5 + Math.atan(x)/Math.PI;
    } else if (x > 0) {
        ans = 1 - (incBeta(df/(df+x*x), df/2.0, 0.5))/2;
    } else if (x < 0) {
        ans = incBeta(df/(df+x*x), df/2.0, 0.5)/2;
    }
    return(ans);
}

function tInv(p, df ) { // inverse Student-t distribution with
                                              // df degrees of freedom
    var z;
    if (df < 0 || p < 0) {
        return(Math.NaN);
    } else if (p == 0) {
        return(Math.NEGATIVE_INFINITY);
    } else if (p == 1) {
        return(Math.POSITIVE_INFINITY);
    } else if (df == 1) {
        return(Math.tan(Math.PI*(p-0.5)));
    } else if ( p >= 0.5) {
        z = betaInv(2.0*(1-p),df/2.0,0.5);
        return(Math.sqrt(df/z - df));
    } else {
        z = betaInv(2.0*p,df/2.0,0.5);
        return(-Math.sqrt(df/z - df));
    }
}

function incBeta(x, a, b) { // incomplete beta function
       // I_x(z,w) = 1/beta(z,w) * integral from 0 to x of t^(z-1) * (1-t)^(w-1) dt
       // Ref: Abramowitz & Stegun, Handbook of Mathemtical Functions, sec. 26.5.
    var res;
    if (x < 0 || x > 1) {
        res = Math.NaN;
    } else {
        res = 0;
        var bt = Math.exp(lnGamma(a+b) - lnGamma(a) - lnGamma(b) +
                    a*Math.log(x) + b*Math.log(1-x));
        if (x < (a+1)/(a+b+2)) {
            res = bt * betaGuts(x, a, b) / a;
        } else {
            res = 1 - bt*betaGuts(1-x, b, a) / b;
        }
    }
    return(res);
}

function betaGuts( x, a, b) { // guts of the incomplete beta function
    var ap1 = a + 1;
    var am1 = a - 1;
    var apb = a + b;
    var am = 1;
    var bm = am;
    var y = am;
    var bz = 1 - apb*x/ap1;
    var d = 0;
    var app = d;
    var ap = d;
    var bpp = d;
    var bp = d;
    var yold = d;
    var m = 1;
    var t;
    while (y-yold > 4*eps*Math.abs(y)) {
       t = 2 * m;
       d = m * (b - m) * x / ((am1 + t) * (a + t));
       ap = y + d * am;
       bp = bz + d * bm;
       d = -(a + m) * (apb + m) * x / ((a + t) * (ap1 + t));
       app = ap + d * y;
       bpp = bp + d * bz;
       yold = y;
       am = ap / bpp;
       bm = bp / bpp;
       y = app / bpp;
       if (m == 1) bz = 1;
       m++;
    }
    return(y);
}

function chi2Cdf( x, df ) {
    return(gammaCdf(x,df/2,2));
}

function chi2Inv( p, df ) { // kluge for chi-square quantile function.
    var guess = Math.NaN;
    if (p == 0.0) {
        guess = 0.0;
    } else if ( p == 1.0 ) {
        guess = Math.POSITIVE_INFINITY;
    } else if ( p < 0.0 ) {
        guess = Math.NaN;
    } else {
        var tolAbs = 1.0e-8;
        var tolRel = 1.0e-3;
        guess = Math.max(0.0, df + Math.sqrt(2*df)*normInv(p)); // guess from normal approx
        var currP = chi2Cdf( guess, df);
        var loP = currP;
        var hiP = currP;
        var guessLo = guess;
        var guessHi = guess;
        while (loP > p) { // step down
            guessLo = 0.8*guessLo;
            loP = chi2Cdf( guessLo, df);
        }
        while (hiP < p) { // step up
            guessHi = 1.2*guessHi;
            hiP = chi2Cdf( guessHi, df);
        }
        guess = (guessLo + guessHi)/2.0;
        currP = chi2Cdf( guess, df);
        while ( (Math.abs(currP - p) > tolAbs) || (Math.abs(currP - p)/p > tolRel) ) { // bisect
            if ( currP < p ) {
                guessLo = guess;
            } else {
                guessHi = guess;
            }
            guess = (guessLo + guessHi)/2.0;
            currP = chi2Cdf(guess, df);
        }
    }
    return(guess);
}

function gammaCdf( x,  a,  b) { // gamma distribution CDF.
    var p = Math.NaN;
    if (a <= 0 || b <= 0) {
    } else if (x <= 0) {
        p = 0.0;
    } else {
        p = Math.min(incGamma(x/b, a), 1.0);
    }
    return(p);
}

function incGamma( x,  a) {
    var inc = 0;
    var gam = lnGamma(a+rmin);
    if (x == 0) {
        inc = 0;
    } else if (a == 0) {
        inc = 1;
    } else if (x < a+1) {
        var ap = a;
        var sum = 1.0/ap;
        var del = sum;
        while (Math.abs(del) >= 10*eps*Math.abs(sum)) {
            del *= x/(++ap);
            sum += del;
        }
        inc = sum * Math.exp(-x + a*Math.log(x) - gam);
    } else if (x >= a+1) {
       var a0 = 1;
       var a1 = x;
       var b0 = 0;
       var b1 = 1;
       var fac = 1;
       var n = 1;
       var g = 1;
       var gold = 0;
       var ana;
       var anf;
       while (Math.abs(g-gold) >= 10*eps*Math.abs(g)) {
            gold = g;
            ana = n - a;
            a0 = (a1 + a0 *ana) * fac;
            b0 = (b1 + b0 *ana) * fac;
            anf = n*fac;
            a1 = x * a0 + anf * a1;
            b1 = x * b0 + anf * b1;
            fac = 1.0 / a1;
            g = b1 * fac;
            n++;
       }
       inc = 1 - Math.exp(-x + a*Math.log(x) - gam) * g;
    }
    return(inc);
}

function poissonPmf( lambda, k) {  // Poisson probability mass function
    var p = 0.0;
    if (k >= 0) {
        p = Math.exp(-lambda)*Math.pow(lambda,k)/factorial(k);
    }
    return(p);
}

function poissonCdf( lambda, k) {  // Poisson CDF
    var p = 0;
    var b = 0;
    var m = 0;
    while (m <= k) {
        b += Math.pow(lambda, m++)/factorial(k);
    }
    p += Math.exp(-lambda)*b;
    return(p);
}

function poissonTail(lambda, k) {  // upper tail probability of the Poisson
    return(1.0-poissonCdf(lambda, k-1));
}

function expCdf(lambda, x) {   // exponential CDF
        return(1-Math.exp(-x/lambda));
    }

function expPdf(lambda, x) {  // exponential density
        return((1.0/lambda)*Math.exp(-x/lambda));
}


function factorial(n) { // computes n!
    var fac=1;
    for (var i=n; i > 1; i--) {fac *= i;}
    return(fac);
}

function binomialCoef(n,k) { // computes n choose k
    if (n < k || n < 0) {
        return(0.0);
    } else if ( k == 0 || n == 0 || n == k) {
        return(1.0);
    } else {
        var minnk = Math.min(k, n-k);
        var coef = 1;
        for (var j = 0; j < minnk; j++) {
            coef *= (n-j)/(minnk-j);
        }
        return(coef);
    }
}

function binomialPmf(n, p, k) {  // binomial pmf at k.
    var pmf = binomialCoef(n,k)*Math.pow(p,k)*Math.pow((1-p),(n-k));
    return(pmf);
}

function binomialCdf(n, p, k) {  // binomial CDF:  Pr(X <= k), X~B(n,p)
    if (k < 0) {
        return(0.0);
    } else if (k >= n) {
        return(1.0);
    } else {
        var cdf = 0.0;
        for (var i = 0; i <= k; i++) {
            cdf += binomialPmf(n, p, i);
        }
        return(cdf);
    }
}

function binomialTail(n,p,k) { // binomial tail probability Pr(X >= k), X~B(n,p)
    if (k < 0) {
        return(1.0);
    } else if (k >= n) {
        return(0.0);
    } else {
        var tailP = 0.0;
        for (var i = k; i <= n; i++) {
            tailP += binomialPmf(n, p, i);
        }
        return(tailP);
    }
}

function binomialInv(n, p, pt) { // binomial percentile function
    var t = 0;
    if (pt < 0 || pt > 1) {
        t = NaN;
    } else if (pt == 0.0) {
        t = 0;
    } else if (pt == 1.0) {
        t = n;
    } else {
        var t = 0;
        var pc = 0.0;
        while ( pc < pt ) {
            pc += binomialPmf(n, p, t++);
        }
        t -= 1;
    }
    return(t);
}

function multinomialCoef(list, n) { // multinomial coefficient.
// WARNING:  not very stable algorithm; avoid for large n.
    var val = 0;
    var lmn = vMinMax(list);
    if (typeof(n) == 'undefined' || n == null) {
        n = vSum(list);
    }
    if (lmn[0] < 0.0) {
        alert('Error #1 in irGrade.multinomialCoef: a number of outcomes is negative!');
    } else if (n == vSum(list)) {
        val = factorial(n);
        for (var i=0; i < list.length; i++) {
            val /= factorial(list[i]);
        }
    }
    return(val);
}

function multinomialPmf(olist, plist, n) { // multinomial pmf; not stable algorithm
    var val = 0.0;
    var pmn = vMinMax(plist);
    var omn = vMinMax(olist);
    if (typeof(n) == 'undefined' || n == null) {
        n = vSum(olist);
    }
    if (olist.length != plist.length) {
        alert('Error #1 in irGrade.multinomialPmf: length of outcome and probability vectors ' +
               'do not match!');
    } else if (pmn[0] < 0.0) {
        alert('Error #2 in irGrade.multinomialPmf: a probability is negative!');
    } else if (omn[0] < 0.0) {
        alert('Error #3 in irGrade.multinomialPmf: a number of outcomes is negative!');
    } else if (n == vSum(olist)) {
        var pl = vMult(1.0/vSum(plist), plist);  // just in case
        val = factorial(n);
        for (var i=0; i< olist.length; i++) {
            val *= Math.pow(pl[i], olist[i])/factorial(olist[i]);
        }
    }
    return(val);
}


function geoPmf( p,  k) {
  // chance it takes k trials to the first success in iid Bernoulli(p) trials
  // EX = 1/p; SD(X) = sqrt(1-p)/p
    if (k < 1 || p == 0.0) {
        return(0.0);
    } else {
        return(Math.pow((1-p),k-1)*p);
    }
}

function geoCdf( p, k) {
  // chance it takes k or fewer trials to the first success in iid Bernoulli(p) trials
    if (k < 1 || p == 0.0) {
        return(0.0);
    } else {
        return(1-Math.pow( 1-p, k));
    }
}

function geoTail( p,  k) {
  // chance of k or more trials to the first success in iid Bernoulli(p) trials
    return(1 - geoCdf(p, k-1));
}

function geoInv(p, pt) { // geometric percentile function
    var t = 0;
    if (pt < 0 || pt > 1) {
        t = Math.NaN;
    } else if (pt == 0.0) {
        t = 0;
    } else if (pt == 1.0) {
        t = Math.POSITIVE_INFINITY;
    } else {
        var t = 0;
        var pc = 0.0;
        while ( pc < pt ) {
            pc += geoPmf(p, t++);
        }
    }
    return(t);
}

function hyperGeoPmf( N,  M,  n,  m) {
  // chance of drawing m of M objects in a sample of size n from
  // N objects in all.  p = (M C m)*(N-M C n-m)/(N C n)
  // EX = n*M/N; SD(X)= sqrt((N-n)/(N-1))*sqrt(np(1-p));
    var p;
    if ( n < m || N < M || M < m  || m < 0 || N < 0) {
        return(0.0);
    } else {
        p = binomialCoef(M,m)*binomialCoef(N-M,n-m)/binomialCoef(N,n);
        return(p);
    }
}

function hyperGeoCdf( N,  M,  n,  m) {
  // chance of drawing m or fewer of M objects in a sample of size n from
  // N objects in all
    var p=0.0;
    var mMax = Math.min(m,M);
    mMax = Math.min(mMax,n);
    for (var i = 0; i <= mMax; i++) {
        p += hyperGeoPmf(N, M, n, i);
    }
    return(p);
}

function hyperGeoTail( N,  M,  n,  m) {
  // chance of drawing m or more of M objects in a sample of size n from
  // N objects in all
    var p=0.0;
    for (var i = m; i <= Math.min(M,n); i++) {
        p += hyperGeoPmf(N, M, n, i);
    }
    return(p);
}

function negBinomialPmf( p,  s,  t) {
  // chance that the sth success in iid Bernoulli trials is on the tth trial
  // EX = s/p; SD(X) = sqrt(s(1-p))/p
    if (s > t || s < 0) {
        return(0.0);
    }
    var prob = p*binomialPmf(t-1,p,s-1);
    return(prob);
}

function negBinomialCdf( p,  s,  t) {
  // chance the sth success in iid Bernoulli trials is on or before the tth trial
    var prob = 0.0;
    for (var i = s; i <= t; i++) {
        prob += negBinomialPmf(p, s, i);
    }
    return(prob);
}

function pDieRolls(rolls,spots) { // chance that the sum of 'rolls' rolls of a die = 'spots'
    if (rolls > 4) {
        alert('Error #1 in irGrade.pDiceRolls: too many rolls ' + rolls + '. ');
        return(Math.NaN);
    } else {  // BRUTE FORCE!
        var found = 0;
        if (spots < rolls || spots > 6*rolls) {return(0.0);}
        var possible = Math.pow(6,rolls);
        if (rolls == 1) {
            return(1/possible);
        } else if (rolls == 2) {
            for (var i=1; i <=6; i++ ) {
                for (var j=1; j <= 6; j++ ) {
                    if (i+j == spots ) {found++;}
                }
            }
        } else if (rolls == 3 ) {
            for (var i=1; i <=6; i++ ) {
                for (var j=1; j<=6; j++ ) {
                    for (var k=1; k<=6; k++ ) {
                        if (i+j+k == spots ) {found++;}
                    }
                }
            }
        } else if (rolls == 4 ) {
            for (var i=1; i <=6; i++ ) {
                for (var j=1; j<=6; j++ ) {
                    for (var k=1; k<=6; k++ ) {
                        for (var m=1; m <=6; m++ ) {
                            if (i+j+k+m == spots ) {found++;}
                        }
                    }
                }
            }
        }
        return(found/possible);
    }
    return(false);
}

function permutations(n,k) { // number of permutations of k of n things
    if (n < k || n < 0) {
        return(0);
    } else if ( k==0 || n == 0) {
        return(1);
    } else {
        var coef=1;
        for (var j=0; j < k; j++) coef *= (n-j);
    }
    return(coef);
}


function sgn(x) {  // signum function
    if (x >= 0) {
        return(1);
    } else if (x < 0) {
        return (-1);
    }
}

function linspace(lo,hi,n) { // n linearly spaced points between lo and hi
    var spaced = new Array(n);
    var dx =(hi-lo)/(n-1);
    for (var i=0; i < n; i++) {
        spaced[i] = lo + i*dx;
    }
    return(spaced);
}

function rms(list) { // rms
    var r = 0;
    for (var i=0; i < list.length; i++) r += list[i]*list[i];
    r /= list.length;
    return(Math.sqrt(r));
}

function vMinMax(list){ // returns min and max of list
    var mn = list[0];
    var mx = list[0];
    for (var i=1; i < list.length; i++) {
        if (mn > list[i]) mn = list[i];
        if (mx < list[i]) mx = list[i];
    }
    return(new Array(mn,mx));
}

function vMinMaxIndices(list){ // returns min, max, index of min, index of max
    var mn = list[0];
    var indMn = 0;
    var mx = list[0];
    var indMx = 0;
    for (var i=1; i < list.length; i++) {
        if (mn > list[i]) {
            mn = list[i];
            indMn = i;
        }
        if (mx < list[i]) {
            mx = list[i];
            indMx = i;
        }
    }
    return(new Array(mn,mx,indMn,indMx));
}

function vMinMaxAbs(list) {
// returns min and max of absolute values of a list's elements
    var mn = Math.abs(list[0]);
    var mx = Math.abs(list[0]);
    var val;
    for (var i=1; i < list.length; i++) {
        val = Math.abs(list[i]);
            if (mn > val) mn = val;
            if (mx < val) mx = val;
    }
    return(new Array(mn,mx));
}

function randBoolean(p){ // random boolean value, prob p that it is true
    if (typeof(p) == 'undefined' || p == null) {
        p = 0.5;
    }
    if (rand.next() <= p) {
        return(false);
    } else {
        return(true);
    }
}

function sortUnique(list,order) { // sort a list, remove duplicate entries
    var temp = list;
    if (typeof(order) != 'undefined' && order != null) {
        temp.sort(order);
    } else {
        temp.sort();
    }
    var temp2 = new Array();
    temp2[0] = temp[0];
    var ix = 0;
    for (var i=1; i < temp.length; i++) {
        if (temp[i] != temp2[ix] ) {
            temp2[++ix] = temp[i];
        }
    }
    return(temp2);
}

function uniqueCount(list) { // unique elements and their counts
    var temp = new Object;
    temp[list[0]] = list[0];
    for (var j=1; j < list.length; j++) {
        if (typeof(temp[list[j]]) == 'undefined' || temp[list[j]] == null) {
             temp[list[j]] = 1;
        } else {
             temp[list[j]]++;
        }
    }
    uc = new Array(2);
    uc[0] = new Array();
    uc[1] = new Array();
    var k = 0;
    for (var j in temp) {
        uc[0][k] = j;
        uc[1][k++] = temp[j];
    }
    return(uc);
}

function unique(list) {
    return(uniqueCount(list)[0]);
}

function randPermutation(list,index) { // returns a random permutation of list
    var randIndex = listOfDistinctRandInts(list.length,0,list.length-1);
    var thePermutation = new Array(list.length);
    for (var i=0; i < list.length; i++) {
        thePermutation[i] = list[randIndex[i]];
    }
    if (typeof(index) != 'undefined' && index == 'forward') { // original indices
        var p = new Array(2);
        p[0] = thePermutation;
        p[1] = randIndex;
        thePermutation = p;
    } else if (typeof(index) != 'undefined' && index == 'inverse') { // inverse permutation
        var p = new Array(2);
        p[0] = thePermutation;
        p[1] = new Array(list.length);
        for (var i=0; i < list.length; i++) {
            p[1][randIndex[i]] = i;
        }
        thePermutation = p;
    }
    return(thePermutation);
}


function cyclicPermutation(n, k) { // cyclic permutation by k of of the integers 0 to n-1
    if (typeof(k) == 'undefined' || k == null) {
        k = 1;
    }
    var perm = new Array(n);
    for (var i = 0; i < n; i++) {
            perm[i] = (i+k)%n;
    }
    return(perm);
}

function distinctPermutation(n, k) { // returns a permutation of the integers 0 to n-1
                                     // in which no index maps to itself
    if (typeof(k) == 'undefined' || k == null) {
            k = Math.min(3, n-1);
    }
    return(cyclicPermutation(n,k));
}

function distinctRandPermutation(n) { // returns a random permutation of the integers 0 to n-1
                                        // in which no index maps to itself
    function isInPlace(x) {  // is any index in its original place?
        v = false;
        for (var i=0; i < x.length; i++) {
           if (x[i] == i) {
              v = true;
           }
        }
        return(v);
    }
    var x = new Array(n);
    for (var i = 0; i < n; i++) {
        x[i] = i;
    }
    x = randPermutation(x);
    while (isInPlace(x)) {
       x = randPermutation(x);
    }
    return(x);
}




function fakeBivariateData(nPoints, funArray, heteroFac, snr, loEnd, hiEnd) {
   // returns a 2-d array of synthetic data generated from a polynomial,
   // according to the contents of funArray.
   // if funArray[0] == 'polynomial', uses the other elements of funArray as
   // the coefficients of a polynomial.
   // funArray[1] + funArray[2]*X + funArray[3]*X^2 + ...
   // 1/3 of the points have noise level heteroFac times larger than the rest.
   // Normalizes the errors  to signal/noise ratio snr  in 2-norm
    var data = new Array(2);
    data[0] = new Array(nPoints);
    data[1] = new Array(nPoints);
    if (snr == 0) {
            snr = 2;
    }
    var x;
    var fVal;
    var xPow;
    if (funArray[0] == 'polynomial') {
        if (typeof(loEnd) == 'undefined' || loEnd == null) {   // lower limit of X variable
            loEnd = -10;
        }
        if (typeof(hiEnd) == 'undefined' || hiEnd == null) {   // upper limit of X variable
            hiEnd = 10;
        }
        var dX = (hiEnd - loEnd)/(nPoints - 1);
        for (var i=0; i < nPoints; i++) {
            x = loEnd + i*dX;
            data[0][i] = x;
            fVal = 0.0;
            xPow = 1.0;
            for (var j=1;  j < funArray.length; j++) {
                fVal +=  xPow*funArray[j];
                xPow *= x;
            }
            data[1][i] = fVal;
        }
    } else {
        alert('Error #1 in irGrade.fakeBivariateData()!\n' +
            'Unsupported function type: ' + funArray[0].toString());
        return(null);
    }
// now add noise.
    var sigNorm = twoNorm(data[1]);
    var noise = new Array(nPoints);
    for (var i=0; i < nPoints; i++) {
        noise[i] = rNorm();
    }
// pick a random set to perturb for heteroscedastic noise
    var segLen = Math.floor(nPoints/3);
    var startPt = Math.floor(2*nPoints/3*rand.next());
    for (var i=startPt; i < startPt+segLen; i++) {
        noise[i] = noise[i]*heteroFac;
    }
    var noiseNorm = twoNorm(noise);
    for (var i=0; i < nPoints; i++) {
        data[1][i] += noise[i]*sigNorm/noiseNorm/snr;
    }
    return(data)
}

function nextRand() {  // generates next random number in a sequence
    var up   = this.seed / this.Q;
    var lo   = this.seed % this.Q;
    var trial = this.A * lo - this.R * up;
    if (trial > 0) {
        this.seed = trial;
    } else {
        this.seed = trial + this.M;
    }
    return (this.seed * this.oneOverM);
}

function rng(s) {
       if ( typeof(s)=='undefined' || s == null ){
           var d = new Date();
           this.seed = 2345678901
             + (d.getSeconds() * 0xFFFFFF)
             + (d.getMinutes() * 0xFFFF);
       } else {
           this.seed = s;
       }
       this.A = 48271;
       this.M = 2147483647;
       this.Q = this.M / this.A;
       this.R = this.M % this.A;
       this.oneOverM = 1.0 / this.M;
       this.next = nextRand;
       this.getSeed = getRandSeed;
       return(this);
}

function getRandSeed() { // get seed of random number generator
    return(this.seed);
}

function crypt(s,t) {
    var slen = s.length;
    var tlen = t.length;
    var rad = 16;
    var r = 0;
    var i;
    var j = -1;
    var result = '';
    if (s.substr(0,2) == '0x') {
        for (i=2; i < slen; i+=2) {
            if (++j >= tlen) {j = 0;}
            r = parseInt(s.substr(i,2),rad) ^ t.charCodeAt(j);
            result += String.fromCharCode(r);
        }
    } else {
        result +='0x';
        for ( i=0; i < slen; i++) {
           if (++j >= tlen) {j = 0;}
           r = s.charCodeAt(i) ^ t.charCodeAt(j);
           result += (r < rad ? '0' : '') + r.toString(rad);
        }
    }
    return(result);
}


