package eshop;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class AccountPage
 */
@WebServlet("/AccountPage")
public class AccountPage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Connection con;
	private static Statement stmt;
	private static ResultSet rs;
	private static Statement stmt_products;
	private static ResultSet rs_products;
	private HttpSession session;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AccountPage() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter(); 
		session = request.getSession(true);
		System.out.println(session.getAttribute("id"));
		dbConnection db = new dbConnection();
		con = db.getConnection(request);

		if(session.getAttribute("id")!=null) {
			printHead(out);
			printBody(out);
			printBottom(out);
		}else {
			response.sendRedirect(request.getContextPath()+"/RegisterPage");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
		PrintWriter out = response.getWriter();
		HttpSession session = request.getSession(true);
		String operation = request.getParameter("operation");
		if(operation.equals("update")) {
			updateData(out,request,session,response);
			response.sendRedirect(request.getContextPath()+"/AccountPage");
		}
		if(operation.equals("deleteOrder")) {
			deleteOrder(out,request,session,response);
			response.sendRedirect(request.getContextPath()+"/AdminPage");
			return;
		}
		if(operation.equals("logout")) {
			session.invalidate();
			response.sendRedirect(request.getContextPath()+"/RegisterPage");
			return;
		}
	}
	
    public void deleteOrder(PrintWriter out, HttpServletRequest request, HttpSession session, HttpServletResponse response) {
    	String sql = "DELETE FROM `order_items`\r\n"
    			+ "WHERE `order_id` = '"+request.getParameter("order_id_main")+"';";
    	String second_sql = "DELETE FROM `orders`\r\n"
    			+ "WHERE `id` = '"+request.getParameter("order_id")+"';";
    	try {
    		stmt = con.createStatement();
    		stmt.executeUpdate(sql);
    		stmt.executeUpdate(second_sql);
    	} catch (SQLException e) {
	        e.printStackTrace();
	    }
    }
    
	public void updateData(PrintWriter out,HttpServletRequest request,HttpSession session,HttpServletResponse response) {
		try {
			stmt = con.createStatement();
	        String sql = "SELECT * FROM users WHERE id = '" + session.getAttribute("id") + "';";
	        rs = stmt.executeQuery(sql);
	        rs.next();
	        if(!request.getParameter("email").equals(rs.getString("email"))) {
	        	sql = "SELECT COUNT(*) as amount FROM users WHERE email = '" + request.getParameter("email") + "';";
		        rs = stmt.executeQuery(sql);
		        rs.next();
	        	if(Integer.parseInt(rs.getString("amount"))!=0) {
		    		System.out.println("User with this email already exists!!");
		        }else {
		        	sql = "UPDATE users\r\n"
		        			+ "SET\r\n"
		        			+ "  email = '"+request.getParameter("email")+"',\r\n"
		        			+ "  name = '"+request.getParameter("name")+"',\r\n"
		        			+ "  surname = '"+request.getParameter("surname")+"',\r\n"
		        			+ "  address = '"+request.getParameter("address")+"'\r\n"
		        			+ "WHERE\r\n"
		        			+ "  id = '"+session.getAttribute("id")+"';\r\n"
		        			+ "";
			        stmt.executeUpdate(sql);
			        rs.next();
		        }
	        }else {
	        	sql = "UPDATE users\r\n"
	        			+ "SET\r\n"
	        			+ "  email = '"+request.getParameter("email")+"',\r\n"
	        			+ "  name = '"+request.getParameter("name")+"',\r\n"
	        			+ "  surname = '"+request.getParameter("surname")+"',\r\n"
	        			+ "  address = '"+request.getParameter("address")+"'\r\n"
	        			+ "WHERE\r\n"
	        			+ "  id = '"+session.getAttribute("id")+"';\r\n"
	        			+ "";
		        stmt.executeUpdate(sql);
	        }
		}catch (SQLException e) {
	        // Handle SQLException
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
	    out.println("        <li class=\"nav-item\">");
	    out.println("          <a class=\"nav-link\" href=\"CartPage\">Cart</a>");
	    out.println("        </li>");
	    out.println("<form action=\"AccountPage\" method=\"post\">");
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
	
	public void printBody(PrintWriter out) {

		try {		
			String sql = "SELECT * FROM users WHERE id ='"+session.getAttribute("id")+"';";
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			rs.next();
			out.println("<h1>Change user data: </h1>");
			out.println("<form action=\"AccountPage\" method=\"post\">");
			out.println("<input type='hidden' name='operation' value='update'>");
			out.println("    <div class=\"mb-3\">");
			out.println("        <label for=\"exampleInputEmail1\" class=\"form-label\">Email address</label>");
			out.println("        <input type=\"email\" name=\"email\" class=\"form-control\" id=\"exampleInputEmail1\" value=\"" + rs.getString("email") + "\">");
			out.println("    </div>");
			out.println("    <div class=\"mb-3\">");
			out.println("        <label for=\"exampleInputName\" class=\"form-label\">Name</label>");
			out.println("        <input type=\"text\" name=\"name\" class=\"form-control\" id='exampleInputName' value=\"" + rs.getString("name") + "\">");
			out.println("    </div>");
			out.println("    <div class=\"mb-3\">");
			out.println("        <label for=\"exampleInputSurname\" class=\"form-label\">Surname</label>");
			out.println("        <input type=\"text\" name=\"surname\" class=\"form-control\" id='exampleInputSurname' value=\"" + rs.getString("surname") + "\">");
			out.println("    </div>");
			out.println("    <div class=\"mb-3\">");
			out.println("        <label for=\"exampleInputAddress\" class=\"form-label\">Address</label>");
			out.println("        <input type=\"text\" name=\"address\" class=\"form-control\" id='exampleInputAddress' value=\"" + rs.getString("address") + "\">");
			out.println("    </div>");
			out.println("    <input type='submit' class=\"btn btn-primary\" value='Save'>");
			out.println("</form>");
			printOrders(out);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		

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
	    out.println("      <th scope=\"col\">details</th>");
	    out.println("    </tr>");
	    out.println("  </thead>");
	    out.println("  <tbody>");

	    try {
	        String sql = "SELECT * FROM orders WHERE user_id = " + session.getAttribute("id");
	        stmt = con.createStatement();
	        rs = stmt.executeQuery(sql);

	        while (rs.next()) {
	            out.println("    <tr>");
	            out.println("      <th scope=\"row\">" + rs.getString("order_id") + "</th>");
	            out.println("      <td>" + rs.getString("user_id") + "</td>");
	            out.println("      <td>" + rs.getString("price") + "</td>");
	            out.println("      <td>" + rs.getString("date") + "</td>");
	            out.println("      <td>" + rs.getString("status") + "</td>");
	            out.println("<td>");
	            String modalId = "exampleModal" + rs.getString("order_id");

	            out.println("<button type=\"button\" class=\"btn btn-primary\" data-bs-toggle=\"modal\" data-bs-target=\"#" + modalId + "\">");
	            out.println("Show details");
	            out.println("</button>");

	            out.println("<div class=\"modal fade\" id=\"" + modalId + "\" tabindex=\"-1\" aria-labelledby=\"exampleModalLabel\" aria-hidden=\"true\">");
	            out.println("  <div class=\"modal-dialog\">");
	            out.println("    <div class=\"modal-content\">");
	            out.println("      <div class=\"modal-header\">");
	            out.println("        <h1 class=\"modal-title fs-5\" id=\"exampleModalLabel\">Order: "+rs.getString("order_id")+"</h1>");
	            out.println("        <button type=\"button\" class=\"btn-close\" data-bs-dismiss=\"modal\" aria-label=\"Close\"></button>");
	            out.println("      </div>");
	            out.println("      <div class=\"modal-body\">");
	            String product_sql = "SELECT product.name, order_items.amount, order_items.price FROM order_items " +
	                                 "JOIN product ON product.id = order_items.product_id " +
	                                 "WHERE order_id = '" + rs.getString("order_id") + "';";
	            stmt_products = con.createStatement();
	            rs_products = stmt_products.executeQuery(product_sql);
	    	    out.println("<table class=\"table\">");
	    	    out.println("  <thead>");
	    	    out.println("    <tr>");
	    	    out.println("      <th scope=\"col\">name</th>");
	    	    out.println("      <th scope=\"col\">amount</th>");
	    	    out.println("      <th scope=\"col\">price</th>");
	    	    out.println("    </tr>");
	    	    out.println("  </thead>");
	    	    out.println("  <tbody>");
	            while (rs_products.next()) {
	            	
	            	out.println("<tr>");
	                out.println("  <td>" + rs_products.getString("name") + "</td>");
	                out.println("  <td>" + rs_products.getString("amount") + "</td>");
	                out.println("  <td>" + rs_products.getString("price") + "</td>");
	                out.println("</tr>");
	            }
		        out.println("  </tbody>");
		        out.println("</table>");
	            out.println("      </div>");
	            out.println("      <div class=\"modal-footer\">");
	            out.println("        <button type=\"button\" class=\"btn btn-secondary\" data-bs-dismiss=\"modal\">Close</button>");
	            out.println("      </div>");
	            out.println("    </div>");
	            out.println("  </div>");
	            out.println("</div>");
	            out.println("</td>");
	            out.println("</tr>");
	        }

	        out.println("  </tbody>");
	        out.println("</table>");

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	public void printBottom(PrintWriter out) {
		 out.println("  </div>");
		out.println("    <script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js' integrity=\"sha384-C6RzsynM9kWDrMNeT87bh95OGNyZPhcTNXj1NW7RuBCsyN/o0jlpcV8Qyq46cDfL\" crossorigin=\"anonymous\"></script>\r\n"
				+ "  </body>\r\n"
				+ "</html>");
	}
}
