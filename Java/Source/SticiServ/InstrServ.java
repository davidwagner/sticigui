/**
package SticiServ

public class InstrServ extends HttpServlet

servlet to interact with course instructor.
serves pages to
           create student database
           query student database
           modify fields in student database

           create grade database
           add fields to grade database
           modify fields in grade database, including by formulae
           compute summary statistics of grades

           create assignment database
           add fields to assignment database
           modify fields in assignment database

@author P.B. Stark http://statistics.berkeley.edu/~stark
@version 0.1

**/
package SticiServ;

import java.util.*;
import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;


public class InstrServ extends HttpServlet {
    private Connection con = null;         // the instructor's connection to the database
    private Hashtable permissions = null;  // permissions
    private HttpSession session;

    public void doGet(HttpServletRequest req, HttpServletResponse res)
                                  throws ServletException, IOException {
         // check for session; if does not exist, go to login -- USE SAME CLASS/METHOD AS
         //   FOR STUDENTS
         // retrieve permissions
         // retrieve courses associated with instructor
         // check request type; switch by type
         // open db connection to appropriate databases

    }

    private HtmlResultSet getGrade(int course, int sid) {
        // get the grades for one student, return an HtlmResultSet

    }

    private HtmlResultSet getGrades(int course) {
        // get the grades for all students in a course

    }

    private HtmlResultSet setGrade(int course, int sid, String assignment, double grade) {
        // set the grade for one student for a given assignment
        // return a message for success or failure
        // test for existence of the assignment
    }

    private HtmlResultSet addAssignment(int course, String assignment, Date dueByDate,
            Date postDate, double maxGrade) {
        // create an assignment field, with name = assignment, (due date) = dueByDate,
        // (date to post grade and solutions) = postDate, and
        // (maximum score) = maxGrade


    }

    private HtmlResultSet deleteAssignment(int course, String assignment) {
        // delete the field for this assignment

    }

    private HtmlResultSet addComputedField(int course, String label, String formula) {
        // add a computed field to the grade database
    }

    private HtmlResultSet recomputeField(int course, String label) {
        // recompute a field after data are changed
        // gotta store the formula somewhere ... HOW DO RDBSs DO THIS?
    }




}
