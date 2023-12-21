package eshop;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class CartPage
 */
@WebServlet("/CartPage")
public class CartPage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Connection con;
	private static Statement stmt;
	private static ResultSet rs;
	private HttpSession session;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CartPage() {
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
		if(operation.equals("remove")) {
			removeFromCart(out,request,session,response);
			response.sendRedirect(request.getContextPath()+"/CartPage");
		}
		if(operation.equals("order")) {
			order(out,request,session,response);
			response.sendRedirect(request.getContextPath()+"/CartPage");
		}
		if(operation.equals("logout")) {
			session.invalidate();
			response.sendRedirect(request.getContextPath()+"/RegisterPage");
			return;
		}
	}
	public void order(PrintWriter out, HttpServletRequest request, HttpSession session, HttpServletResponse response) {
		DateFormat dateFormat = new SimpleDateFormat("yyyyddHHss");
		Calendar cal = Calendar.getInstance();
		String order_id = session.getAttribute("id")+""+dateFormat.format(cal.getTime());
		boolean allowed = true;
		ArrayList<String> queries = new ArrayList<>();
		String am = "SELECT COUNT(*) as am FROM cart\r\n"
				+ "JOIN product ON product.id = game_id\r\n"
				+ "WHERE user_id = "+session.getAttribute("id")+" ;";
    	try {
    		stmt = con.createStatement();
			rs = stmt.executeQuery(am);
			rs.next();
			if(rs.getInt("am")>0) {
				String check = "SELECT cart.*,product.amount as orig_am FROM cart\r\n"
						+ "JOIN product ON product.id = game_id\r\n"
						+ "WHERE user_id = "+session.getAttribute("id")+" ;";
				String createOrder = "INSERT INTO `orders` (`id`, `order_id`, `user_id`, `price`, `date`, `status`) VALUES (NULL, '"+order_id+"', '"+session.getAttribute("id")+"', '"+request.getParameter("total")+"', current_timestamp(), 'PENDING');";
				try {
					synchronized (this) {
					stmt = con.createStatement();
			    	rs = stmt.executeQuery(check);
			    	while(rs.next()) {
			    		if(rs.getInt("amount")>rs.getInt("orig_am")) allowed = false;
			    	}
					if(allowed) {
						stmt = con.createStatement();
				    	stmt.executeUpdate(createOrder);
				    	rs = stmt.executeQuery(check);
				    	while(rs.next()) {
				    		queries.add("INSERT INTO `order_items` (`id`, `order_id`, `product_id`, `amount`, `price`) VALUES (NULL, '"+order_id+"', '"+rs.getString("game_id")+"', '"+rs.getString("amount")+"', '"+rs.getString("price")+"')");
				    		queries.add("UPDATE product SET amount = " + (rs.getInt("orig_am") - rs.getInt("amount")) + " WHERE id = " + rs.getString("game_id") + ";");
				    		queries.add("DELETE FROM cart WHERE user_id = '"+session.getAttribute("id")+"' AND game_id = '"+rs.getString("game_id")+"';");
				    	}
				    	stmt.close();
				    	for(int i = 0; i< queries.size();i++) {
				    		stmt = con.createStatement();
					    	stmt.executeUpdate(queries.get(i));
				    	}
					}else {
						out.println("<script>alert(\"" + "Out of stock!" + "\");</script>");
					}
					}

				} catch (SQLException e) {
			        e.printStackTrace();
			    }
			}else {
				out.println("<script>alert(\"" + "Empty cart!!" + "\");</script>");
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
    	
		
	}
	public void removeFromCart(PrintWriter out, HttpServletRequest request, HttpSession session, HttpServletResponse response) {
		String cart_id = request.getParameter("cart_id");
		try {
			 stmt = con.createStatement();
			 String sql = "SELECT * FROM cart WHERE id = '" + cart_id + "';";
		     rs = stmt.executeQuery(sql);
		     rs.next();
		     int amount = rs.getInt("amount");
		     String game_id = rs.getString("game_id");
		     sql = "SELECT * FROM product WHERE id = "+game_id+";";
		     rs = stmt.executeQuery(sql);
		     rs.next();
             sql = "DELETE FROM cart WHERE id = "+cart_id+";";
             stmt.executeUpdate(sql);
		} catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	public void printBody(PrintWriter out) {
	    out.println("<h1>Your cart</h1>");
	    out.println("<ol class=\"list-group list-group-numbered\">");
	    int total = 0;
	    boolean available = true;
	    try {
	        stmt = con.createStatement();
	        String sql = "SELECT cart.*, product.name AS game_name,product.amount AS game_amount " +
	                     "FROM cart " +
	                     "JOIN product ON cart.game_id = product.id " +
	                     "WHERE user_id = " + session.getAttribute("id") + ";";
	        rs = stmt.executeQuery(sql);
	        while (rs.next()) {
	            out.println("  <form action=\"CartPage\" method=\"post\">");
	            out.println("    <input type=\"hidden\" name=\"operation\" value=\"remove\">");
	            out.println("    <input type=\"hidden\" name=\"cart_id\" value="+rs.getString("cart.id")+">");
	            out.println("    <li class=\"list-group-item d-flex justify-content-between align-items-start\">");
	            out.println("      <div class=\"ms-2 me-auto\">");
	            out.println("        <div class=\"fw-bold\">" + rs.getString("game_name") + "</div>");
	            out.println("        Price: " + rs.getString("price"));
	            out.println("      </div>");
	            if(rs.getInt("amount")>rs.getInt("game_amount")) {
	            	out.println("      <span class=\"badge bg-danger rounded-pill\">Amount: " + rs.getString("amount") + "</span>");
	            	available = false;
	            }else {
		            out.println("      <span class=\"badge bg-primary rounded-pill\">Amount: " + rs.getString("amount") + "</span>");
		            available = true;
	            }
				out.println("    <input type='submit' class=\"btn btn-danger btn-lg\" value='Remove'>");
	            out.println("    </li>");
	            out.println("  </form>");
	            total+=Integer.parseInt(rs.getString("price"));
	        }
	    } catch (SQLException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace(); 
	    }
	    out.println("</ol>");
	    out.println("<h3>Total amount is: "+total+" </h3>");
		if(available) {
			out.println("<form action=\"CartPage\" method=\"post\">");
			out.println("<input type='hidden' name='operation' value='order'>");
			out.println("<input type='hidden' name='total' value='"+total+"'>");
			out.println("<input type='submit' class=\"btn btn-outline-success btn-lg\" value='Purchase'>");
			out.println("</form>");
		}else {
			out.println("<div class=\"p-3 text-light bg-danger border border-primary-subtle rounded-3\">\r\n"
					+ "  Out of stock!\r\n"
					+ "</div>");
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
	    out.println("<link rel=\"stylesheet\" href=\"<%=request.getContextPath()%>/util/css/style.css\">");
	    out.println("    <link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css' rel='stylesheet' integrity='sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN' crossorigin='anonymous'>");
	    out.println("  </head>");
	    out.println("  <body>");
	    out.println("<nav class=\"navbar navbar-expand-lg bg-body-tertiary sticky-top\">");
	    out.println("  <div class=\"container-fluid\">");
	    out.println("    <a class=\"navbar-brand\" href=\"MainPage\">Steam</a>");
	    out.println("    <button class=\"navbar-toggler\" type=\"button\" data-bs-toggle=\"collapse\" data-bs-target=\"#navbarNav\" aria-controls=\"navbarNav\" aria-expanded=\"false\" aria-label=\"Toggle navigation\">");
	    out.println("      <span class=\"navbar-toggler-icon\"></span>");
	    out.println("    </button>");
	    out.println("    <div class=\"collapse navbar-collapse\" id=\"navbarNav\">");
	    out.println("      <ul class=\"navbar-nav\">");
	    out.println("        <li class=\"nav-item\">");
	    out.println("          <a class=\"nav-link active\" aria-current=\"page\" href=\"MainPage\">Home</a>");
	    out.println("        </li>");
	    out.println("        <li class=\"nav-item\">");
	    out.println("          <a class=\"nav-link\" href=\"AccountPage\">Account</a>");
	    out.println("        </li>");
	    out.println("<form action=\"CartPage\" method=\"post\">");
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
