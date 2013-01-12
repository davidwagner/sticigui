/**
package SticiServ

public class SticiGrader extends HttpServlet

log hw submissions and update grade database

@author P.B. Stark http://statistics.berkeley.edu/~stark
@version 0.1

**/
package SticiServ;

import java.util.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;


public class SticiGrader extends HttpServlet {
    private Hashtable permissions = null;  // permissions
    private HttpSession session;

    public void doGet(HttpServletRequest req, HttpServletResponse res)
                                  throws ServletException, IOException {
         // decrypt submission:
         //      get id information from cookie
         // if decryption fails, return error message
         // if decrypts OK:
         //      log submission
         //      use SticiDb to:
         //           check due date
         //           verify that student is enrolled
         //           check for previous submission and whether that's OK
         //           update grade database; make sure student exists, etc.
         //      return html message regarding success, past-due, etc.
         // end

         // Might be better for this servlet NOT to use SticiDb, because it only
         // establishes one connection at a time to the databases---short life span.
         // Alternatively, might be better for this servlet to persist, handling
         // submissions as they come in... Synchronization?
    }
}
