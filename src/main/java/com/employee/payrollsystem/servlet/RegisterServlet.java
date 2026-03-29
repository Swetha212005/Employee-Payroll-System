package com.employee.payrollsystem.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;

public class RegisterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/payrolldb", "root", "25Mca0007^");

            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO login (username, email, password) VALUES (?, ?, ?)"
            );
            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, password);

            int i = ps.executeUpdate();

            out.println("<!DOCTYPE html>");
            out.println("<html><head><title>Registration Status</title>");
            out.println("<style>");
            out.println("body {"
                    + "font-family: 'Poppins', sans-serif;"
                    + "background: linear-gradient(135deg, #d4fc79, #96e6a1);"
                    + "display: flex;"
                    + "justify-content: center;"
                    + "align-items: center;"
                    + "height: 100vh;"
                    + "margin: 0;"
                    + "color: #333;"
                    + "}");
            out.println(".card {"
                    + "background-color: #ffffff;"
                    + "padding: 40px 60px;"
                    + "border-radius: 15px;"
                    + "box-shadow: 0 8px 25px rgba(0,0,0,0.2);"
                    + "text-align: center;"
                    + "}");
            out.println("a {"
                    + "display: inline-block;"
                    + "margin-top: 20px;"
                    + "text-decoration: none;"
                    + "background-color: #4facfe;"
                    + "color: white;"
                    + "padding: 10px 20px;"
                    + "border-radius: 6px;"
                    + "transition: 0.3s;"
                    + "font-weight: bold;"
                    + "}");
            out.println("a:hover {background-color: #00c6ff;}");
            out.println("</style></head><body>");
            out.println("<div class='card'>");

            if (i > 0) {
                out.println("<h2 style='color:green;'>Registration Successful!</h2>");
                out.println("<p><a href='login.html'>Go to Login</a></p>");
            } else {
                out.println("<h2 style='color:red;'>Registration Failed!</h2>");
                out.println("<p><a href='register.html'>Try Again</a></p>");
            }

            out.println("</div></body></html>");

            ps.close();
            conn.close();

        } catch (Exception e) {
            out.println("<!DOCTYPE html><html><body style='font-family:Poppins;text-align:center;background:#f8f9fa;'>");
            out.println("<h2 style='color:red;'>Error: " + e.getMessage() + "</h2>");
            out.println("<p><a href='register.html'>Back to Register</a></p>");
            out.println("</body></html>");
        }
    }
}
