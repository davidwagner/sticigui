// s21su11Header.js
// Copyright P.B. Stark, 2007-2011.  All rights reserved.

var s21HeaderModTime = '2012/04/13/0150';


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
                '"/>' + '<title>' + replaceMarkupByChar(titStr,'&mdash;') + '</title>' +
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
    var qStr = '<div id="navDiv">' +
               '<ul id="navMenu">' +
                   '<li><a href="#firstContent" title="skip navigation" target="_self">Menu&nbsp;&raquo;</a>' +
                       '<ul>' +
                          '<li><a href="' + s21RelPath +      '/index.htm" title="Class Announcements" ' +
                               'target="_self">Announcements</a></li> ' +
                          '<li><a href="' + sticiGuiRelPath + '/index.htm" title="SticiGui Text" ' +
                               'target="_new">SticiGui Text</a></li>' +
                          '<li><a href="' + sticiGuiRelPath + '/Problems/index.htm" title="Assignments and Scores" ' +
                               'target="_top">Assignments &amp; Scores&nbsp;&raquo;</a>' +
                               '<ul>' +
	                            '<li><a href="' + sticiGuiRelPath + '/Problems/index.htm#instructions" title="General Instructions" ' +
		                         'target="_top">General Instructions</a></li>' +
	                            '<li><a href="' + sticiGuiRelPath + '/Problems/index.htm#assignmentSched" title="Assignment Schedule" ' +
	                                 'target="_top">Assignment Schedule</a></li>' +
	                            '<li><a href="' + sticiGuiRelPath + '/Problems/index.htm#troubleshooting" title="Troubleshooting" ' +
		                         'target="_top">Troubleshooting</a></li>' +
	                      '</ul>' +
                          '</li>' +
                          '<li><a href="' + s21RelPath + '/index.htm" title="Course Information" ' +
                               'target="_self">Course Info&nbsp;&raquo;</a>' +
                               '<ul>' +
 	                            '<li><a href="' + s21RelPath + '/index.htm#desc" title="Description" ' +
 	                                 'target="_self">Description</a></li>' +
                                    '<li><a href="' + s21RelPath + '/index.htm#enrollment" title="Enrollment" ' +
                                         'target="_self">Enrollment</a></li>' +
                                    '<li><a href="' + s21RelPath + '/index.htm#texts" title="Text" ' +
                                         'target="_self">Text</a></li>' +
                                    '<li><a href="' + s21RelPath + '/index.htm#homework" title="Homework" ' +
                                         'target="_self">Homework</a></li>' +
                                    '<li><a href="' + s21RelPath + '/index.htm#midterm" title="Midterm" ' +
                                         'target="_self">Midterm</a></li>' +
                                    '<li><a href="' + s21RelPath + '/index.htm#final" title="Final" ' +
                                         'target="_self">Final</a></li>' +
                                    '<li><a href="' + s21RelPath + '/index.htm#grading" title="Grading Policy" ' +
                                         'target="_self">Grading</a></li>' +
                                    '<li><a href="' + s21RelPath + '/index.htm#honesty" title="honesty" ' +
                                         'target="_self">Honesty</a></li>' +              
                                    '<li><a href="' + s21RelPath + '/index.htm#ta" title="Graduate Student Instructors" ' +
                                         'target="_self">GSIs</a></li>' +
                                    '<li><a href="' + s21RelPath + '/index.htm#officeHours" title="Office Hours" ' +
                                         'target="_self">Office Hours</a></li>' +
                                    '<li><a href="' + s21RelPath + '/index.htm#studyHall" title="Study Hall" ' +
                                         'target="_self">Online Study Hall</a></li>' +
                                    '<li><a href="' + s21RelPath + '/index.htm#courseManager" title="Course Manager" ' +
                                         'target="_self">Course Manager</a></li>' +
                                   '<li><a href="' + s21RelPath + '/index.htm#discussionBoard" title="Discussion board" ' +
                                          'target="_self">Discussion</a></li>' +
                                   '<li><a href="' + s21RelPath + '/index.htm#emailPolicy" title="Email Policy" ' +
                                          'target="_self">Email Policy</a></li>' +
                                   '<li><a href="' + s21RelPath + '/index.htm#accommodations" ' +
                                          'title="Disability Accommodations" ' +
                                          'target="_self">Disability Accommodations</a></li>' +
                                   '<li><a href="' + s21RelPath + '/index.htm#advice" title="advice" ' +
                                          'target="_self">Advice</a></li>' +
                                   '<li><a href="' + s21RelPath + '/index.htm#bugs" title="Bug and Typo Reports" ' +
                                          'target="_self">Bugs and Typos</a></li>' +
                                   '<li><a href="' + s21RelPath + '/index.htm#slc" title="Tutors, etc." ' +
                                          'target="_self">Tutors, etc.</a></li>' +
                             '</ul>' +
                       '</li>' +
                       '<li><a href="http://statistics.berkeley.edu/~stark" title="P.B. Stark\'s homepage" ' +
                               'target="_new">P.B. Stark</a></li>' +
                   '</ul>' +
                '</li>' +
             '</ul>' +
             '</div>';
    if (window.attachEvent) {
         window.attachEvent("onload", sfHover);
    }
    document.writeln(qStr);
    qStr = '<div class="coursePage" id="contentDiv"><h1 class="compact" id="firstContent">';
    qStr += titStr + '</h1>';
    document.writeln(qStr);
    return(true);
}