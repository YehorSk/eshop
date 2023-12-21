package eshop;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class GamePage
 */
@WebServlet("/GamePage")
public class GamePage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Connection con;
	private static Statement stmt;
	private static ResultSet rs;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GamePage() {
        super();
        // TODO Auto-generated constructor stub
    }
    public void init(ServletConfig config) throws ServletException {
	}
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		PrintWriter out = response.getWriter(); 
		HttpSession session = request.getSession(true);
		dbConnection db = new dbConnection();
		con = db.getConnection(request);

		if(session.getAttribute("id")!=null) {
			if(request.getParameter("id")==null) {
				response.sendRedirect(request.getContextPath()+"/MainPage");
			}
			printHead(out);
			printBody(out,request.getParameter("id"));
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
		if(operation.equals("addToCart")) {
			addToCart(out,request,session,response);
		}
		if(operation.equals("logout")) {
			session.invalidate();
			response.sendRedirect(request.getContextPath()+"/RegisterPage");
			return;
		}
	}
	public void addToCart(PrintWriter out, HttpServletRequest request, HttpSession session, HttpServletResponse response) {
	    try {
	        stmt = con.createStatement();
	        String productId = request.getParameter("product_id");
	        String quantityParam = request.getParameter("quantity");

	        String getProductSql = "SELECT * FROM product WHERE id = '" + productId + "';";
	        rs = stmt.executeQuery(getProductSql);
	        
	        if (rs.next()) {
	            int availableAmount = Integer.parseInt(rs.getString("amount"));
	            int requestedQuantity = Integer.parseInt(quantityParam);

	            if (requestedQuantity <= availableAmount) {
	                String insertCartSql = "INSERT INTO `cart` (`id`, `user_id`, `game_id`, `amount`, `price`) VALUES (NULL, '"
	                        + session.getAttribute("id") + "', '" + productId + "', '" + quantityParam + "', '"
	                        + requestedQuantity * Integer.parseInt(rs.getString("price")) + "')";
	                stmt.executeUpdate(insertCartSql);

//	                String updateProductSql = "UPDATE product SET amount = " + (availableAmount - requestedQuantity)
//	                        + " WHERE id = " + productId + ";";
//	                stmt.executeUpdate(updateProductSql);
	            } else {
	                out.println("Sorry, the requested quantity exceeds the available quantity.");
	            }
	        } else {
	            out.println("Product not found.");
	        }
	    } catch (SQLException e) {
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
	    out.println("        <li class=\"nav-item\">");
	    out.println("          <a class=\"nav-link\" href=\"CartPage\">Cart</a>");
	    out.println("        </li>");
	    out.println("<form action=\"GamePage\" method=\"post\">");
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
	
	public void printBody(PrintWriter out,String id) {
		try{
			String sql = "SELECT * FROM product WHERE id ='"+id+"';";
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			while(rs.next()) {
				out.println("<div class=\"jumbotron text-white bg-dark\">");
				out.println("  <h1 class=\"display-4\">"+rs.getString("name")+"</h1>");
				out.println("  <img src="+rs.getString("image")+" class=\"img-fluid rounded\" alt=\"Game Image\">");

				out.println("  <p class=\"lead\">"+rs.getString("text")+"</p>");
				out.println("  <hr class=\"my-4\">");

				out.println("  <p class=\"lead\">Price: "+rs.getString("price")+" </p>");
				
				out.println("  <p class=\"lead\">Available Quantity: "+rs.getString("amount")+"</p>");
				out.println("<form action=\"GamePage\" method=\"post\">");
				out.println("<input type='hidden' name='operation' value='addToCart'>");
				out.println("<input type='hidden' name='product_id' value='"+id+"'>");
				out.println("  <div class=\"mb-3\">");
				out.println("    <label for=\"quantity\" class=\"form-label\">Quantity:</label>");
				out.println("    <input type=\"number\" class=\"form-control\" id=\"quantity\" name=\"quantity\" min=\"1\" max="+rs.getString("amount")+">");
				out.println("  </div>");
				out.println("  <p class=\"lead\">");
				if(rs.getInt("amount")<=0) {
					out.println("    <input type='submit' class=\"btn btn-success btn-lg\" value='Add to Cart' disabled=\"disabled\">");
				}else {
					out.println("    <input type='submit' class=\"btn btn-success btn-lg\" value='Add to Cart'>");
				}
				
				out.println("  </p>");
				out.println("</form>");
				out.println("</div>");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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
