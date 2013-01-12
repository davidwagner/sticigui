// script pbsTutor:  interactive, real-time guided
// copyright 1999, P.B. Stark, stark AT stat.berkeley.edu  All rights reserved.
// Version 0.1
// All rights reserved.
// Last modified 28 October 1999.

// !!!!Beginning of the code!!!!
var HI = false;
var CA = true;
var tutorModTime="1999/10/28/0945";                     // time code last modified
//
//  get cookie info
ck = document.cookie;
cq = getCookieVal(ck,"cq");
if (cq == null || cq == -1) {cq = 0;}
pq = getCookieVal(ck,"pq");
if (pq == null) {pq = -1;}
qCtr = findNum(cq)+1;
pCtr = findNum(cq)+1;
//
// subroutines
//
function nextQuestion(q) {
    if (chain[cq][1] > -1) {
        setQuestion(chain[cq][1]);
    } else {
        setQuestion(0);
    }
    return(true);
}

function nextTopic(q) {                                // start next topic by fiat.
    if (chain[cq][2] > -1) {
         setQuestion(chain[cq][2]);
    } else {
        setQuestion(0);
    }
    return(true);
}

function setQuestion(q,corr) {
    if (corr == null || typeof(corr) == "undefined") {
    } else if (corr == 0) {
        qAttempts[chain[cq][3]]++;
    } else {
        gotRight[chain[cq][3]]++;
        qAttempts[chain[cq][3]]++;
    }
    var cookStr = 'cq=' + q + '&pq=' + cq + '&tl=' + (topic.length).toString() + '&';
    cookStr += setCookieArray(qAttempts,"ans") + '&' + setCookieArray(gotRight,"ok") +
        ';expires=' + expireTimeString();
    document.cookie =  cookStr;
    document.location = document.location;
    return(true);
}

function writeTutorExercise(qc,typ,op,a) {
    if (typ == "multiple-one") {
        writeSelectExercise(false,qc,op,a);
    } else if (typ == "multiple-many") {
        writeSelectExercise(true,qc,op,a);
    } else if (typ == "text") {
        writeTextExercise(8,qc,a);
    } else {
        alert("Error #1 in pbsTutor.writeTutorExercise: unsupported problem type " +
            typ + "! ");
        return(false);
    }
    return(true);
}


function writeTutorSetFooter() {
    qAttempts = new Array();
    gotRight = new Array();
    for (var i=0; i < topic.length; i++) {
        qAttempts[i] = getCookieVal(ck,"ans"+i);
        gotRight[i] = getCookieVal(ck,"ok"+i);
        if (qAttempts[i] == null || qAttempts[i] == -1) {
            qAttempts[i] = 0;
        }
        if (gotRight[i] == null || gotRight[i] == -1) {
            gotRight[i] = 0;
        }
    }
    document.writeln('<p></p><hr align="center"><p align="center"><center>');
    document.writeln('<input type="button" name="firstQBut" value="|<<" ' +
                          'onclick="setQuestion(0);">');
    if (pq > -1 ) {
        document.writeln('<input type="button" name="prevQBut" value="<" ' +
                          'onclick="setQuestion(pq);">');
    }
    if (chain[cq][1] >= 0) {
        document.write('<input type="button" name="nextQBut" value=">" ' +
          'onclick="if(checkAnswer(document.forms[0].elements[0].name,');
        if (qType[cq] == "multiple-one") {
            document.writeln('document.forms[0].elements[0].options[document.forms[0].elements[0].options.selectedIndex].value)');
        } else if (qType[cq] == "text") {
            document.write('document.forms[0].elements[0].value)');
        } else {
            alert("Error #1 in pbsTutor.writeTutorSetFooter: unsupported problem type " +
                qType[cq] + "! ");
        }
        document.writeln('){setQuestion(chain[cq][2],1);}else{setQuestion(chain[cq][1],0);}">');
    }
    if (chain[cq][2] > -1 ) {
            document.writeln('<input type="button" name="nextTBut" value="Next Topic" ' +
                              'onclick="nextTopic(cq);">');
    }
    document.writeln('</p><p align=\"center\"><input type="button" name="summBut" ' +
            'value="How am I doing?" onclick="scoreSummary()">' +
            '<input type="button" name="clearBut" value="Clear my history" ' +
            'onclick="zeroCookie()">');
    document.writeln('</center></p><p align="center">');
    document.writeln('<a href="../index.htm" target="_top" ' +
            'onmouseout="window.status=defaultStatus;return(true);" ' +
            'onmouseover="window.status=\'SticiGui Homepage\';return(true);">' +
            'SticiGui<sup>&copy;</sup> Homepage</a> </h3>');
    document.writeln('<p><font color="#FF0000" size="2">&copy;' + copyYr + pbsRef +
        '. All rights reserved.</font><br /><font size="2">Last generated ' + today +
        '.</font></p></form>');
    return(true);
}

function writeTutorSetHeader() {
    document.writeln('<form><h1 align=\"center\">' + setTitle + '</h1><h2 align=\"center\">' +
        topic[chain[cq][3]] + '</h2>');
    return(true);
}

function scoreSummary() {
    var ck = document.cookie;
    var qAttempts = getCookieArray(ck,"ans",topic.length);
    var gotRight = getCookieArray(ck,"ok",topic.length);
    var qTot = vSum(vFindNum(qAttempts));
    var rTot = vSum(vFindNum(gotRight));
    var pct = roundToDig(100*rTot/qTot,2);
    var alertStr = '<html><head><title>Score Summary</title></head><body><form>' +
                   '<h3 align="center">Score Summary</h3><table border=\"2\"><tr>' +
                   '<td align="center" colspan="4">Overall Results</td></tr><tr>' +
                   '<td></td><td>tries</td><td>correct</td><td>%correct</td></tr>' +
                   '<tr><td>Total:</td><td>' + qTot.toString() + '</td><td>' +
                   rTot.toString() + '</td><td>' + pct.toString() + '%</td></tr>' +
                   '<tr><td align=\"center\" colspan=\"4\">Results by Topic</td></tr>';
    for (var i=0; i < topic.length; i++ ) {
        alertStr += '<tr><td>' + topic[i] + '.</td><td>' + qAttempts[i] + '</td><td>' +
             gotRight[i] + '</td><td>' +
             roundToDig(100*findNum(gotRight[i])/findNum(qAttempts[i]),2).toString() +
             '%</td></tr>';
    }
    alertStr += '</table></center></div><p align="center">' +
                '<input type="button" value="Continue" onclick="window.close();">' +
                '</p></form></body></html>';
    scoreWin = window.open('','scoreWin',
            'toolbar=no,location=no,directories=no,status=no,scrollbars=yes,'+
            'resizable=yes,width=350,height=350,top=5,left=5');
    scoreWin.document.writeln(alertStr);
    scoreWin.document.close();
    scoreWin.focus();
}

function zeroCookie() {
    for (var i=0; i < chain.length; i++) {
        qAttempts[chain[i][3]] = 0;
        gotRight[chain[i][3]] = 0;
    }
    var cookStr = 'cq=' + cq + '&pq=' + pq + '&tl=' + (topic.length).toString() + '&';
    cookStr += setCookieArray(qAttempts,"ans") + '&' + setCookieArray(gotRight,"ok") +
        ';expires=' + expireTimeString();
    alert(cookStr);
    document.cookie =  cookStr;
    return(true);
}

