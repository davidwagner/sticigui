/**
package SticiServ

public class Grader extends HttpServlet

servlet to log homework submissions.

@author P.B. Stark http://statistics.berkeley.edu/~stark
@version 0.1

**/
package SticiServ;

import java.util.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;


public class Grader extends HttpServlet {
    private Connection con = null;         // the connection to the database
    private Hashtable permissions = null;  // permissions
    private HttpSession session;

    public void doGet(HttpServletRequest req, HttpServletResponse res)
                                  throws ServletException, IOException {
         // check cookie for student identification
         // retrieve permissions
         // retrieve course associated with student
         // open db connection to appropriate databases
         // log submission
         // log grade

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
