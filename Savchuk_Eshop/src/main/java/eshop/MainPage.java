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
 * Servlet implementation class MainPage
 */
@WebServlet("/MainPage")
public class MainPage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Connection con;
	private static Statement stmt;
	private static ResultSet rs;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MainPage() {
        super();
        // TODO Auto-generated constructor stub
    }
    public void init(ServletConfig config) throws ServletException {
		
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter(); 
		HttpSession session = request.getSession(true);
		dbConnection db = new dbConnection();
		con = db.getConnection(request);

		if(session.getAttribute("id")!=null) {
			if(Integer.parseInt((String) session.getAttribute("admin"))!=1) {
				printHead(out);
				printBody(out);
				printBottom(out);
			}else if(Integer.parseInt((String) session.getAttribute("admin"))==1){
				response.sendRedirect(request.getContextPath()+"/AdminPage");
			}
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
		if(operation.equals("logout")) {
			session.invalidate();
			response.sendRedirect(request.getContextPath()+"/RegisterPage");
			return;
		}
	}
	
	public void printBody(PrintWriter out) {
	    try {
	        String sql = "SELECT * FROM product";
	        stmt = con.createStatement();
	        rs = stmt.executeQuery(sql);

	        out.println("<div class=\"container text-center\">");
	        out.println("<div class=\"row row-cols-1 row-cols-sm-2 row-cols-md-4\">");

	        while (rs.next()) {
	            out.println("<div class=\"col p-3\">");
	            out.println("<div class=\"card h-100\" >");
	            out.println("<img src='" + rs.getString("image") + "' class=\"card-img-top\" alt='Product Image'>");
	            out.println("<div class=\"card-body\">");
	            out.println("<h5 class=\"card-title\">Name: " + rs.getString("name") + "</h5>");
	            out.println("<p>Amount: " + rs.getInt("amount") + "</p>");
	            out.println("<p>Price: " + rs.getDouble("price") + "</p>");
	            out.println("<form action=\"GamePage\" method=\"GET\">");
	            out.println("    <input type=\"hidden\" name=\"operation\" id=\"operation\" value=\"openGame\">");
	            out.println("    <input type=\"hidden\" name=\"id\" value="+rs.getString("id")+">");
	            out.println("    <button type=\"submit\" class=\"btn btn-primary\">Open</button>");
	            out.println("</form>");
	            out.println("</div>");
	            out.println("</div>");
	            out.println("</div>");
	        }

	        out.println("</div>");
	        out.println("</div>");

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
	    out.println("          <a class=\"nav-link\" href=\"AccountPage\">Account</a>");
	    out.println("        </li>");
	    out.println("        <li class=\"nav-item\">");
	    out.println("          <a class=\"nav-link\" href=\"CartPage\">Cart</a>");
	    out.println("        </li>");
	    out.println("<form action=\"MainPage\" method=\"post\">");
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
