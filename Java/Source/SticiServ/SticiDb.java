/**
package SticiServ

protected Class SticiDb extends Object implements Runnable

class containing methods to create, modify, and update the
databases for SticiGui

Idea in sketch is this:  first time the user visits a page that
wants to access the database, instantiate this class, and add the
instance to the session object.  The instance starts a thread
maintaining a connection to the database.
Need to handle disposal of the thread when the session gets stale.

Alternative approach: only one instance of the class; all users
talk to it.  Not sure how to do that...

Need to work out the mechanism for assigning a unique key field in each table;
could use the dbm to do it, but it might be better to do it ourselves.

@version 0.1

*/

package SticiServ;

import java.util.*;
import java.sql.*;
 /////  Questions:  should this be abstract or not?
 /////  If not, make it runnable?
 /////  Runnable (thread to establish the connection) or not?


protected Class SticiDb extends Object implements Runnable {
    private final String dbUrl = "jdbc:odbc:SticiGuiDb"; // FIX ME!
    private final String uName = "username";             // FIX ME!
    private final String pwd = "passwd";                 // FIX ME!
    private final String driverName = "odbcDriver";       // FIX ME!
    private Thread thr;
    Connection con = null;


    public synchronized void start() { // start a thread if there is not one
        if (thr == null) {
            thr = new Thread(this);
            thr.start();
        }
    }

    public void run() {  // connect to the database; prepare statements; wait for query
        try {
            Class.forName("driverName");
        }
        catch (Exception e) {
            System.out.println("cannot find driver " + driverName + " in " + this.toString());
            return;
        }
        try {  // can this be hear in the head matter, or should it be part of a start() for
           // the thread?
            Connection con = DriverManager.getConnection(dbUrl,uName,passwd);
        }
        catch (SQLException e) {
            System.out.println("cannot connect to database " + dbUrl.toString() + " in " +
                  this.toString());
            return;
        }

        // set up prepared statements
        /* current plan for database structure:
        Table GRADE_TBL has fields  STUD_KEY, ASSIGN_KEY, SUBMIT_DAT, SCORE
        Table ASSIGN_TBL has fields ASSIGN_KEY, ASSIGN_NAM, COURSE, DUE_DAT, POST_DAT,
                                      SHOW_DAT, MAX_PTS
        Table STUD_TBL has fields   STUD_KEY, SID, LNAME, FNAME, MI, LOGIN, PASSWD, COURSE_KEY,
                                      UNIV, EMAIL, PHONE
        Table COURSE_TBL has fields COURSE_KEY, INSTR_KEY, UNIV, ... ?
        Table INSTR_TBL has fields  INSTR_KEY, PERMIT, LNAME, FNAME, MI, LOGIN, PASSWD, EMAIL,
                                      PHONE, FAX, STREET_ADR, DEPT, UNIV, STATE, COUNTRY, ZIP
                    The PERMIT field identities permissions; INSTR_TBL is used both for
                    instructors and TAs.
        Table ...

        Gotta think through having unique keys for course, instructor

        */
        PreparedStatement setGradeStmt = con.prepareStatement(
             "UPDATE GRADE_TBL SET SCORE = ? WHERE (SID = ? AND ASSIGN_KEY = ?)"
             );
        PreparedStatement getGradeStmt = con.prepareStatement(
             "SELECT ASSIGN_TBL.ASSIGN_NAM, GRADE_TBL.SCORE  FROM , ASSIGN_TBL, GRADE_TBL " +
             " WHERE (GRADE_TBL.SID = ? AND ASSIGN_TBL.ASSIGN_KEY = GRADE_TBL.ASSIGN_KEY) " +
             " SORT BY ASSIGN_TBL.ASSIGN_NAM "
             );
        PreparedStatement getAssignStmt = con.prepareStatement(
             "SELECT ASSIGN_NAM, DUE_DAT, MAX_PTS FROM ASSIGN_TBL WHERE COURSE = ?"
             );
        PreparedStatement getGradesStmt = con.prepareStatement(
             "SELECT GRADE_TBL.SID, ASSIGN_TBL.ASSIGN_NAM, GRADE_TBL.SCORE FROM " +
             "GRADE_TBL, ASSIGN_TBL WHERE (ASSIGN_TBL.COURSE_KEY = ? AND " +
             "ASSIGN_TBL.ASSIGN_KEY = GRADE_TBL.ASSIGN_KEY) " +
             "SORT BY SID, ASSIGN_NAM" // needs fixing
             );
        PreparedStatement addStudStmt = con.prepareStatement( );
             // add a student to the class
        PreparedStatement dropStudStmt = con.prepareStatement();
             // drop a student from the class
        PreparedStatement addStudsStmt = con.prepareStatement();
             // add a list of students to the class
        PreparedStatement getRosterStmt = con.prepareStatement(
             "SELECT SID, LNAME, FNAME, MI, EMAIL FROM STUD_TBL WHERE COURSE = ? " +
             "SORT BY LNAME, FNAME, SID"
             );
        // etc.
        // make statements for average grades on each assignment and number of submissions,
        // etc.
    }

    protected HtmlResponse makeInstr(String PERMIT, String LNAME, String FNAME, String MI,
                                     String LOGIN, String PASSWD, String EMAIL,
                                     String PHONE, String FAX, String STREET_ADR,
                                     String DEPT, String UNIV, String STATE, String COUNTRY,
                                     String ZIP) {
        // validate the fields.  If OK, create a new entry in the instructor table and
        //                       return the entry as html
        //                       If not OK, complain eloquently

    }

    protected HtmlResponse makeStud( ) {
        // create a new student record
    }

    protected HtmlResponse makeRoster(int courseKey, int[] sids, String[] fnames,
                                      String[] lnames, String[] mis, String[] logins,
                                      String[] passwds) {
        // create a bunch of student entries with initialized logins and passwords

    }

    protected HtmlResponse updateStudent(int

    protected HtmlResponse setGrade(int SID, int assignKey, double grade) {
        // set the grade of one student
         setGradeStmt.setDouble(1, grade);
         setGradeStmt.setInt(2, SID);
         setGradeStmt.setInt(3, assignKey);
         int rows = setGradeStmt.executeUpdate();
         if (rows == 0) {
            // do something
         }
         else {
            // make the int look like a result set; convert to an HtmlResponse
         }
    }

    protected HtmlResponse setGrades(int[] SID, int[] assignKey, double[] grade) {
    }

    protected HtmlResponse getGrade(int SID, int courseKey) {
    }

    protected HtmlResponse getGrades(String instructor, String course) {
        //

    }

    protected HtmlResponse getRoster(String instructor, String course) {
        // get all the SIDs, student names, and email addresses for the students
        // in a course.
    }

}




