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
 * Servlet implementation class RegisterPage
 */
@WebServlet("/RegisterPage")
public class RegisterPage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Connection con;
	private static Statement stmt;
	private static ResultSet rs;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterPage() {
        super();
        // TODO Auto-generated constructor stub
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

		if(session.getAttribute("Name")==null) {
			printHead(out);
			printBody(out);
			printBottom(out);
		}else {
			response.sendRedirect(request.getContextPath()+"/MainPage");
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
		String action = request.getParameter("operation");
		if(action.equals("register")) {
			register(out,request,session,response);
		}
	}
	
	public void register(PrintWriter out,HttpServletRequest request,HttpSession session,HttpServletResponse response) {
		try {

				stmt = con.createStatement();
				String sql = "SELECT COUNT(*) AS amount FROM users WHERE email='"+request.getParameter("email")+"';";
				rs = stmt.executeQuery(sql);
		        rs.next();
		        if(Integer.parseInt(rs.getString("amount"))==0) {
		        	if(request.getParameter("pwd").equals(request.getParameter("pwd_r"))) {
		        		sql = "INSERT INTO `users` (`id`, `pwd`, `name`, `surname`, `email`, `created_at`, `admin`, `address`) VALUES (NULL, '"+request.getParameter("pwd")+"', '', '', '"+request.getParameter("email")+"', current_timestamp(), '0', '')";
		        		stmt.executeUpdate(sql);
		        		sql = "SELECT * FROM users WHERE email='"+request.getParameter("email")+"';";
		    			rs = stmt.executeQuery(sql);
		    	        rs.next();
		        		session.setAttribute("id", rs.getString("id"));
		        		session.setAttribute("Name", rs.getString("name"));
				        session.setAttribute("admin", rs.getString("admin"));
		        		try {
							response.sendRedirect(request.getContextPath()+"/MainPage");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		        	}else {
		        		out.println("<script>alert(\"" + "Passwords doesn't match!" + "\");</script>");
		        	}
		        }else {
		        	out.println("<script>alert(\"" + "Email is taken!" + "\");</script>");
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
	    out.println("      </ul>");
	    out.println("    </div>");
	    out.println("  </div>");
	    out.println("</nav>");
	    out.println("<div class=\"container\">");
	}
	public void printBody(PrintWriter out) {
		out.println("<div class=\"d-flex align-items-center justify-content-center\" style=\"height: 100vh;\">");
		out.println("  <form action=\"RegisterPage\" method=\"post\">");
		out.println("<input type='hidden' name='operation' value='register'>");
		out.println("    <div class=\"form-group\">");
		out.println("      <label for=\"exampleInputEmail1\">Email address</label>");
		out.println("      <input type=\"email\" class=\"form-control\" name=\"email\" id=\"exampleInputEmail1\" aria-describedby=\"emailHelp\" placeholder=\"Enter email\" required>");
		out.println("    </div>");
		out.println("    <div class=\"form-group\">");
		out.println("      <label for=\"exampleInputPassword1\">Password</label>");
		out.println("      <input type=\"password\" class=\"form-control\" name=\"pwd\" id=\"exampleInputPassword1\" placeholder=\"Password\"required> ");
		out.println("    </div>");
		out.println("    <div class=\"form-group\">");
		out.println("      <label for=\"repeatPasswordInput\">Repeat Password</label>");
		out.println("      <input type=\"password\" class=\"form-control\" name=\"pwd_r\" id=\"repeatPasswordInput\" placeholder=\"Repeat Password\" required>");
		out.println("    </div>");
		out.println("    <div class=\"form-group\">");
		out.println("    </div>");
		out.println("  <input type='submit' class=\"btn btn-primary\" value='Register'>");
		out.println("  <p class=\"mt-3\">Already have an account? <a href=\"LoginPage\">Login here</a></p>");
		out.println("  </form>");
		out.println("</div>");
	}
	public void printBottom(PrintWriter out) {
		 out.println("  </div>");
		out.println("    <script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js' integrity=\"sha384-C6RzsynM9kWDrMNeT87bh95OGNyZPhcTNXj1NW7RuBCsyN/o0jlpcV8Qyq46cDfL\" crossorigin=\"anonymous\"></script>\r\n"
				+ "  </body>\r\n"
				+ "</html>");
	}

}
