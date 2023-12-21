package eshop;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class dbConnection
 */
@WebServlet("/dbConnection")
public class dbConnection extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static String URL = "jdbc:mysql://localhost/";
    private static String userName = "root";
    private static String pwd = "";
    private static String databaza = "savchukeshop";
    private Guard g;

    public dbConnection() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Connection c = getConnection(request);
    }

    protected Connection getConnection(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession();
            Connection c = (Connection) session.getAttribute("connection");

            if (c == null || c.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                c = DriverManager.getConnection(URL + databaza, userName, pwd);
                session.setAttribute("connection", c);
                g = new Guard(c);
                System.out.println("Connection created");
            } else {
                System.out.println("Connection already exists");
            }
            return c;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
