/** Basic plan as of 26 April 1999

This represents a change and (I hope) a simplification from the last outline.
In particular, one servlet has been eliminated (grade access); its function is
combined with the homework access in a way that seems natural to me.

Servlets for the following functions:

1) Homework grading and logging; essentially cgi form handling
2) Homework access/grade access
    two parts:
    a) present a form showing assignments with links, and grades for submitted assignments
	b) output ascii file to an applet to display grades for the entire course
3) Serving instructor functions:
    a) creating assignments, modifying due dates and grades
	b) uploading class lists
	c) creating term grades
	d) modifying student records (resetting passwords, etc.)
4) Student registration/record updates
5) Login


More detail:

LOGIN SERVLET.
On arriving at a servlet, the servlet should check for an existing session object.
If none exists, redirect to the login servlet. The action of the login servlet
should take as an argument the referring URL, so that after logging in,
the user can be taken back immediately to where he/she was before the login was needed.
The login servlet should check for the existence of the user name in the database.
    If the user exists, check for password; accept or reject.
	   If accepted,
	      1) check for another existing session object for this user.  If one exists, kill
		     it.  Establish a session object for the user; store the course info and
			 permission info in it.
		  2) check whether this is first login (i.e., whether the required
	         identifying information exists in the database).  If this is a first
		     login, require a password change and require the requisite identifying
		     information to be supplied.
		  The successful login page should offer a link back to where the person was
		  (if this is the result of a redirect), and should offer a link to update
		  personal records, check grades, check assignment due dates, etc.
	   If rejected, offer two more chances to input the password correctly; offer
	      student-supplied mnemonic, if available.
		  If there are three failures in less than 10 minutes, require a 5 minute wait
		  before trying again.  This can be implemented using fields in the user database.
	If the user does not exist, establish a session object and return message that
	   user does not exist;  offer new login or option to create guest account
	   (with no priviledges). Allow a maximum of three login attempts per session object.


GRADING SERVLET.
Accepts a hashed string input.
De-hash the input; look for username, assignment id, and score.  Update the appropriate
database with the score; return html showing success or failure.  Failure should
give a useful error message.

HOMEWORK/GRADE ACCESS SERVLET.
Requires previous login.
Use userid from session object to look up assignments for the class, due dates, and
existing scores.
Output two frames as follows:

Top frame: table

      [assignment name]  [due date]  [link to assignment]   [submitted time and score if already due/available]


Bottom frame: applet
  histogram applet for all available scores for the class, with data read from the grade
  access servlet

Following a link in the upper frame to an assignment should set a cookie with the
identifying information needed to submit homework.  Using a cookie allows the session
to terminate without losing the identifying information, so a student can work on
a problem set offline.  The mechanism for setting a cookie with this sort of data
is already implemented in the JavaScript routines in irgrade.js; the change is to
read these data from the session object and use it to initialize local variables in
the page.

REGISTRATION/UPDATE SERVLET.
Need to get
    lastname, firstname, mi
	email address
	course abbreviation (e.g., Stat2)
	university
	phone number
	student ID number
Assign unique identifier; maybe let the student pick it (check DB for uniqueness)
Allow student to update email address, phone number, password, mnemonic for password

INSTRUCTOR SERVLET.
Not done yet.

*/


