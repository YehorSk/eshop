package eshop;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class AdminPage
 */
@WebServlet("/AdminPage")
public class AdminPage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Connection con;
	private static Statement stmt;
	private static ResultSet rs;
	private HttpSession session;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AdminPage() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        session = request.getSession(true);
        dbConnection db = new dbConnection();
        con = db.getConnection(request);

        if (session.getAttribute("id") != null) {
            if (Integer.parseInt((String) session.getAttribute("admin")) == 1) {
                printHead(response.getWriter());
                printBody(response.getWriter());
                printBottom(response.getWriter());
            } else {
                response.sendRedirect(request.getContextPath() + "/MainPage");
                return;
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/RegisterPage");
            return;
        }
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
		PrintWriter out = response.getWriter();
		HttpSession session = request.getSession(true);
		String operation = request.getParameter("operation");
		if(operation.equals("logout")) {
			session.invalidate();
			response.sendRedirect(request.getContextPath()+"/RegisterPage");
			return;
		}
		if(operation.equals("updateOrder")) {
			updateOrder(out,request,session,response);
			response.sendRedirect(request.getContextPath()+"/AdminPage");
			return;
		}
		if(operation.equals("deleteOrder")) {
			deleteOrder(out,request,session,response);
			response.sendRedirect(request.getContextPath()+"/AdminPage");
			return;
		}
		if(operation.equals("updateUser")) {
			updateUser(out,request,session,response);
			response.sendRedirect(request.getContextPath()+"/AdminPage");
			return;
		}
    }
    public void updateUser(PrintWriter out, HttpServletRequest request, HttpSession session, HttpServletResponse response) {
    	String sql = "UPDATE users\r\n"
    			+ "SET admin = '"+request.getParameter("admin_status")+"'\r\n"
    			+ "WHERE id = '"+request.getParameter("user_id")+"';";
    	try {
    		stmt = con.createStatement();
    		stmt.executeUpdate(sql);
    	} catch (SQLException e) {
	        e.printStackTrace();
	    }
    }

    public void updateOrder(PrintWriter out, HttpServletRequest request, HttpSession session, HttpServletResponse response) {
    	String sql = "UPDATE orders\r\n"
    			+ "SET status = '"+request.getParameter("status")+"'\r\n"
    			+ "WHERE id = '"+request.getParameter("order_id")+"';";
    	try {
    		stmt = con.createStatement();
    		stmt.executeUpdate(sql);
    	} catch (SQLException e) {
	        e.printStackTrace();
	    }
    }
    public void deleteOrder(PrintWriter out, HttpServletRequest request, HttpSession session, HttpServletResponse response) {
        String sqlDeleteOrderItems = "DELETE FROM `order_items` WHERE `order_id` = '" + request.getParameter("order_id_main") + "';";
        String sqlSelectOrderItems = "SELECT * FROM order_items WHERE order_id = '" + request.getParameter("order_id_main") + "';";
        String sqlDeleteOrders = "DELETE FROM `orders` WHERE `id` = '" + request.getParameter("order_id") + "';";

        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(sqlSelectOrderItems);
            ArrayList<String> queries = new ArrayList<>();
            while (rs.next()) {
            	queries.add("UPDATE product SET amount = amount + " + rs.getInt("amount") + " WHERE id = " + rs.getInt("product_id") + ";");
            }
            for(int i = 0;i<queries.size();i++) {
                stmt.executeUpdate(queries.get(i));
            }
            stmt.close();
            stmt = con.createStatement();
            stmt.executeUpdate(sqlDeleteOrderItems);
            stmt = con.createStatement();
            stmt.executeUpdate(sqlDeleteOrders);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

	public void printBody(PrintWriter out) {
		out.println("<h1>Orders</h1>");
		printOrders(out);
		out.println("<h1>Users</h1>");
		printUsers(out);
	}
	public void printOrders(PrintWriter out) {
		out.println("<table class=\"table\">");
        out.println("  <thead>");
        out.println("    <tr>");
        out.println("      <th scope=\"col\">order_id</th>");
        out.println("      <th scope=\"col\">user_id</th>");
        out.println("      <th scope=\"col\">price</th>");
        out.println("      <th scope=\"col\">date</th>");
        out.println("      <th scope=\"col\">status</th>");
        out.println("      <th scope=\"col\">New status</th>");
        out.println("      <th scope=\"col\">Update</th>");
        out.println("      <th scope=\"col\">Delete</th>");
        out.println("    </tr>");
        out.println("  </thead>");
        out.println("  <tbody>");

        try {
	        String sql = "SELECT * FROM orders";
	        stmt = con.createStatement();
	        rs = stmt.executeQuery(sql);
	        while (rs.next()) {
	            out.println("    <tr>");
	            out.println("      <th scope=\"row\">"+rs.getString("order_id")+"</th>");
	            out.println("      <td>"+rs.getString("user_id")+"</td>");
	            out.println("      <td>"+rs.getString("price")+"</td>");
	            out.println("      <td>"+rs.getString("date")+"</td>");
	            out.println("      <td>"+rs.getString("status")+"</td>");
	            out.println("<form action=\"AdminPage\" method=\"post\">");
	            out.println("<input type=\"hidden\" name=\"operation\" value=\"updateOrder\">");
	            out.println("<input type=\"hidden\" name=\"order_id\" value="+rs.getString("id")+">");
	            out.println("<td>");
	            out.println("<select class=\"form-select\" name=\"status\">");
	            out.println("  <option value=\"PROCESSED\">PROCESSED</option>");
	            out.println("  <option value=\"SHIPPED\">SHIPPED</option>");
	            out.println("  <option value=\"PAID\">PAID</option>");
	            out.println("</select>");
	            out.println("</td>");
	            out.println("<td>");
				out.println("<input type='submit' class=\"btn btn-success btn-lg\" value='Update'>");
	            out.println("</form>");
	            out.println("</td>");
	            out.println("<td>");
	            if(!rs.getString("status").equals("PAID")) {
		            out.println("<form action=\"AdminPage\" method=\"post\">");
		            out.println("<input type=\"hidden\" name=\"operation\" value=\"deleteOrder\">");
		            out.println("<input type=\"hidden\" name=\"order_id\" value="+rs.getString("id")+">");
		            out.println("<input type=\"hidden\" name=\"order_id_main\" value="+rs.getString("order_id")+">");
		            out.println("<input type='submit' class=\"btn btn-danger btn-lg\" value='Delete'>");
		            out.println("</form>");
	            }else {
		            out.println("<input type='submit' class=\"btn btn-danger btn-lg\" value='Delete' disabled=\"disabled\">");
	            }

	            out.println("</td>");
	            out.println("</tr>");
	        }
	        out.println("  </tbody>");
	        out.println("</table>");

	    } catch (SQLException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	}
	public void printUsers(PrintWriter out) {
		out.println("<table class=\"table\">");
        out.println("  <thead>");
        out.println("    <tr>");
        out.println("      <th scope=\"col\">user_id</th>");
        out.println("      <th scope=\"col\">name</th>");
        out.println("      <th scope=\"col\">surname</th>");
        out.println("      <th scope=\"col\">email</th>");
        out.println("      <th scope=\"col\">role</th>");
        out.println("      <th scope=\"col\">Change role</th>");
        out.println("      <th scope=\"col\">Update</th>");
        out.println("    </tr>");
        out.println("  </thead>");
        out.println("  <tbody>");

        try {
	        String sql = "SELECT * FROM users";
	        stmt = con.createStatement();
	        rs = stmt.executeQuery(sql);
	        while (rs.next()) {
	            out.println("    <tr>");
	            out.println("      <th scope=\"row\">"+rs.getString("id")+"</th>");
	            out.println("      <td>"+rs.getString("name")+"</td>");
	            out.println("      <td>"+rs.getString("surname")+"</td>");
	            out.println("      <td>"+rs.getString("email")+"</td>");
	            if(rs.getInt("admin")==0) {
	            	out.println("<td>User</td>");
	            }else {
	            	out.println("<td>Admin</td>");
	            }
	            
	            out.println("<form action=\"AdminPage\" method=\"post\">");
	            out.println("<input type=\"hidden\" name=\"operation\" value=\"updateUser\">");
	            out.println("<input type=\"hidden\" name=\"user_id\" value="+rs.getString("id")+">");
	            out.println("<td>");
	            out.println("<select class=\"form-select\" name=\"admin_status\">");
	            out.println("  <option value=\"1\">ADMIN</option>");
	            out.println("  <option value=\"0\">USER</option>");
	            out.println("</select>");
	            out.println("</td>");
	            out.println("<td>");
				out.println("<input type='submit' class=\"btn btn-success btn-lg\" value='Update'>");
	            out.println("</form>");
	            out.println("</td>");
	            out.println("</tr>");
	        }
	        out.println("  </tbody>");
	        out.println("</table>");

	    } catch (SQLException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	}
	public void printHead(PrintWriter out) {
	    out.println("<!DOCTYPE html>");
	    out.println("<html lang=\"en\" data-bs-theme=\"dark\">");
	    out.println("  <head>");
	    out.println("    <meta charset=\"utf-8\">");
	    out.println("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
	    out.println("    <title>Steam</title>");
	    out.println("<script src=\"https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js\" integrity=\"sha384-C6RzsynM9kWDrMNeT87bh95OGNyZPhcTNXj1NW7RuBCsyN/o0jlpcV8Qyq46cDfL\" crossorigin=\"anonymous\"></script>\r\n"
	    		+ "");
	    out.println("    <link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css' rel='stylesheet' integrity='sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN' crossorigin='anonymous'>");
	    out.println("  </head>");
	    out.println("  <body>");
	    out.println("<nav class=\"navbar navbar-expand-lg bg-body-tertiary sticky-top\">");
	    out.println("  <div class=\"container-fluid\">");
	    out.println("    <a class=\"navbar-brand\" href=\"#\">Steam</a>");
	    out.println("    <button class=\"navbar-toggler\" type=\"button\" data-bs-toggle=\"collapse\" data-bs-target=\"#navbarNav\" aria-controls=\"navbarNav\" aria-expanded=\"false\" aria-label=\"Toggle navigation\">");
	    out.println("      <span class=\"navbar-toggler-icon\"></span>");
	    out.println("    </button>");
	    out.println("    <div class=\"collapse navbar-collapse\" id=\"navbarNav\">");
	    out.println("      <ul class=\"navbar-nav\">");
	    out.println("        <li class=\"nav-item\">");
	    out.println("          <a class=\"nav-link active\" aria-current=\"page\" href=\"MainPage\">Home</a>");
	    out.println("        </li>");
	    out.println("<form action=\"AdminPage\" method=\"post\">");
	    out.println("    <input type=\"hidden\" name=\"operation\" value=\"logout\">");
	    out.println("    <li class=\"nav-item\">");
	    out.println("        <button type=\"submit\" class=\"nav-link\" style=\"border: none; background: none; cursor: pointer;\">Logout</button>");
	    out.println("    </li>");
	    out.println("</form>");
	    out.println("      </ul>");
	    
	    out.println("    </div>");
	    out.println("  </div>");
	    out.println("</nav>");
	    out.println("<div class=\"container\">");
	}
	
	public void printBottom(PrintWriter out) {
		 out.println("  </div>");
		out.println("    <script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js' integrity=\"sha384-C6RzsynM9kWDrMNeT87bh95OGNyZPhcTNXj1NW7RuBCsyN/o0jlpcV8Qyq46cDfL\" crossorigin=\"anonymous\"></script>\r\n"
				+ "  </body>\r\n"
				+ "</html>");
	}

}
