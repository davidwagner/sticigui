// script course:  sets course-specific javascript variables for SticiGui
// copyright 1999-2012. P.B. Stark, statistics.berkeley.edu/~stark
// Version 1.1
// All rights reserved.
//

var courseModTime = "2012/6/17/0020";   // last modification


// Modification instructions:  set due date file, instructor's name, semester,
//     course designation, root for admin, path for grades

var teacher;                       // instructor's name
var teacherName;                   // English name
var course;                        // course designation
var cRoot = "Java/Courses/";       // relative root for course management
var semester = "Summer 2012";      // semester
var courseName;                    // English name of course
var scoreBase = "../../../cgi-bin/scores.dat?";
var queryBase = "http://statistics.berkeley.edu/cgi-bin/querygrade5?";
var courses = [
               ["0","s2-x","Statistics 2.1x, Spring 2013, UC Berkeley","stark","Profs P.B. Stark and A. Adhikari","",5,4],
               ["1","s21-s13","Statistics W21, Spring 2013, UC Berkeley","stark","Prof. S. Murali",
                      "Teach/S21/Su13/Grades", 5, 4],
               ["2","guest-","guest login (no grading)","stark","Prof. P.B. Stark","","",1],
               ["3","","none","","","",1]
              ];
var dFileBase = "sets.due";        // due date file stem
var sFileBase = "sid.txt";         // user file stem
var formStemName = "SticiGuiSet";  // stem for assignment forms
var queryList = "";                // list of assignments to look for
var lastAssignment = 27;           // last assigned problem set
for (var i= -1; i<=lastAssignment; i++) {
    queryList += formStemName + i.toString() + ",";
}
queryList = queryList.substring(0,queryList.length-1);     // trim final comma
// ends course.js
