// s21Header.js
// Copyright P.B. Stark, 2007-2012.  All rights reserved.

var s21HeaderModTime = '2012/4/13/0150';


function s21SetUpHead(titStr, s21RelPath, sticiGuiRelPath) {
    showQMarks = false;
    if (typeof(sticiGuiRelPath) == 'undefined' || sticiGuiRelPath == null || sticiGuiRelPath.length == 0) {
        sticiGuiRelPath = '../../../SticiGui';
    }
    if (typeof(s21RelPath) == 'undefined' || s21RelPath == null || s21RelPath.length == 0) {
        s21RelPath = '.';
    }
    var qStr =  '<META http-equiv="Content-Style-Type" content="text/css">' +
                '<link rel="stylesheet" type="text/css" href="' + sticiGuiRelPath + cssBase.toString() +
                '"/>' + '<title>' + titStr + '</title>' +
                '<base target="_self">';
    document.writeln(qStr);
    return(true);
}

function s21SetUpBody(titStr,s21RelPath,sticiGuiRelPath) {
    if (typeof(sticiGuiRelPath) == 'undefined' || sticiGuiRelPath == null || sticiGuiRelPath.length == 0) {
        sticiGuiRelPath = '../../../SticiGui';
    }
    if (typeof(s21RelPath) == 'undefined' || s21RelPath == null || s21RelPath.length == 0) {
        s21RelPath = '.';
    }
    var qStr = '<body>' +
               '<div class="floatMenuTopSplitLeft" id="navMenu">' + '<a href="#firstContent" title="skip navigation" ' +
                   'target="_self">-</a>&nbsp;' +
               '<ul class="noBullet">' +
               '<li><a href="' + s21RelPath +      '/index.htm" title="Class announcements" ' +
                   'target="_self">Announcements</a></li> ' +
               '<li><a href="' + sticiGuiRelPath + '/index.htm" title="SticiGui text" ' +
                   'target="_new">SticiGui text</a></li>' +
               '<li><a href="' + s21RelPath + '/assign.htm" title="Assignments and scores" ' +
                   'target="_top">Assignments &amp; scores</a></li>' +
               '<li><ul class="noBullet">' +
	         '<li><a href="' + s21RelPath + '/assign.htm#instructions" title="General instructions" ' +
		     'target="_top">General instructions</a></li>' +
	         '<li><a href="' + s21RelPath + '/assign.htm#assignmentSched" title="Assignment schedule" ' +
		     'target="_top">Assignment schedule</a></li>' +
	         '<li><a href="' + s21RelPath + '/assign.htm#troubleshooting" title="Troubleshooting" ' +
		     'target="_top">Troubleshooting</li>' +
	       '</ul></li>' +
               '<li><a href="' + s21RelPath + '/courseInfo.htm" title="Course information" ' +
                   'target="_self">Course info</a></li>' +
               '<li><ul class="noBullet">' +
 	         '<li><a href="' + s21RelPath + '/courseInfo.htm#desc" title="Description" ' +
 	             'target="_self">Description</a></li>' +
                 '<li><a href="' + s21RelPath + '/courseInfo.htm#enrollment" title="Enrollment" ' +
                     'target="_self">Enrollment</a></li>' +
                 '<li><a href="' + s21RelPath + '/courseInfo.htm#texts" title="Text" ' +
                     'target="_self">Text</a></li>' +
                 '<li><a href="' + s21RelPath + '/courseInfo.htm#homework" title="Homework" ' +
                     'target="_self">Homework</a></li>' +
                 '<li><a href="' + s21RelPath + '/courseInfo.htm#midterm" title="Midterm" ' +
                     'target="_self">Midterm</a></li>' +
                 '<li><a href="' + s21RelPath + '/courseInfo.htm#final" title="Final" ' +
                     'target="_self">Final</a></li>' +
                 '<li><a href="' + s21RelPath + '/courseInfo.htm#grading" title="Grading policy" ' +
                     'target="_self">Grading</a></li>' +
                 '<li><a href="' + s21RelPath + '/courseInfo.htm#honesty" title="honesty" ' +
                     'target="_self">Honesty</a></li>' +
                 '<li><a href="' + s21RelPath + '/courseInfo.htm#officeHours" title="Office hours" ' +
                     'target="_self">Office hours</a></li>' +
                 '<li><a href="' + s21RelPath + '/courseInfo.htm#ta" title="Graduate Student Instructors" ' +
                     'target="_self">GSIs</a></li>' +
                 '<li><a href="' + s21RelPath + '/courseInfo.htm#courseManager" title="Course Manager" ' +
                     'target="_self">Course manager</a></li>' +
                 '<li><a href="' + s21RelPath + '/courseInfo.htm#techSupport" title="Tech support" ' +
                     'target="_self">Tech support</a></li>' +
                 '<li><a href="' + s21RelPath + '/courseInfo.htm#discussionBoard" title="Discussion board" ' +
                     'target="_self">Discussion</a></li>' +
                 '<li><a href="' + s21RelPath + '/courseInfo.htm#emailPolicy" title="Email policy" ' +
                     'target="_self">Email policy</a></li>' +
                 '<li><a href="' + s21RelPath + '/courseInfo.htm#accommodations" ' +
                     'title="Disability accommodations" ' +
                     'target="_self">Disability accommodations</a></li>' +
                 '<li><a href="' + s21RelPath + '/courseInfo.htm#advice" title="advice" ' +
                     'target="_self">Advice</a></li>' +
                 '<li><a href="' + s21RelPath + '/courseInfo.htm#bugs" title="Bug and typo reports" ' +
                     'target="_self">Bugs and typos</a></li>' +
                 '<li><a href="' + s21RelPath + '/courseInfo.htm#slc" title="Tutors, etc." ' +
                     'target="_self">Tutors, etc.</a></li>' +
               '</ul></li>' +
               '<li><a href="http://statistics.berkeley.edu/~stark" title="P.B. Stark\'s homepage" ' +
                   'target="_new">P.B. Stark</a></li>' +
               '</ul>' +
               '</div>';
    document.writeln(qStr);
    qStr = '<div class="coursePage" id="contentDiv"><a id="firstContent"></a><h1 class="compact">';
    qStr += titStr + '</h1>';
    document.writeln(qStr);
    return(true);
}