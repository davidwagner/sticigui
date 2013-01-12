// @copyright 1999 by
// @author P.B. Stark.  All rights reserved
// Last modified 22 April 1999.

package SticiServ;

import java.util.*;
import java.sql.*;

protected class Assignment extends Object implements Runnable {
    // this class encapsulates the behavior of an assignment.
    // it can create an assignment in the database,
    // set parameters of the assignment,
    // set and get grades.
    // This class does no security checks---that should happen before the calls

    // instance variables
    public String name = null;         // assignment name
    public String courseId = null;     // should this be a string or an int?
                                       // need a way to identify instances of the course...
    public Date dueDate = null;        // date assignment is due
    public Date postAnswerDate = null; // date to post solutions
    public Date postGradeDate = null;  // date to post grades
    public double maxScore = null;     // maximum possible points
    protected Thread t;                // my thread

    protected SticiDb db = new SticiDb(????); // really want new? How to initialize?



    // constructors
    public Assignment() {
    }

    public Assignment(String name) {
        this.name = name;
    }

    public Assignment(String name,  String courseId) {
        this.name = name;
        this.courseId = courseId;
    }

    public Assignment(String name, String courseId, Date dueDate) {
        // etc.
    }

    public void start() {
        // what to do?
    }

    public void setDueDate(Date d) {
        this.dueDate = d;
    }

    public Date getDueDate() {
        return(this.dueDate);
    }

    public void setPostAnswerDate(Date d) {
        this.PostAnswerDate = d;
    }

    public Date getPostAnswerDate() {
        return(this.postAnswerDate);
    }

    public void setPostGradeDate(Date d) {
        this.postAnswerDate = d;
    }

    public Date getPostAnswerDate() {
        return(this.postAnswerDate);
    }

    public double getGrade(int sid) {
        // return the grade for this student on this assignment
    }

    public void setGrade(int sid, double grade) {
        // set the grade of this student for this assignment
    }

    public double[] getGrades(int[] sid) {
        // return the grades for an array of sids
    }

    public void setGrades(int[] sids, double[] grades) {
        // set the grades for an array of sids
    }


// more like this

    public void commitChanges() {
        // commit the changes to the database
    }



}



