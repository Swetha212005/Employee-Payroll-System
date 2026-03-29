package com.employee.payrollsystem.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;


public class LoginServlet extends HttpServlet {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/payrolldb";
    private static final String DB_USER = "root";          
    private static final String DB_PASSWORD = "25Mca0007^"; 

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // ✅ Change the table name to match RegisterServlet
            String sql = "SELECT * FROM login WHERE username=? AND password=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, password);

            ResultSet rs = pst.executeQuery();

          if(rs.next()) {
    // Login successful
    HttpSession session = request.getSession();
    session.setAttribute("username", username);
    response.sendRedirect("home.html");
} else {
    // Login failed – display styled message
    response.setContentType("text/html");
    response.getWriter().println("<!DOCTYPE html>");
    response.getWriter().println("<html><head><title>Login Failed</title></head><body>");
    response.getWriter().println("<h3 style='color:red;text-align:center;'>Invalid username or password!</h3>");
    response.getWriter().println("<p style='text-align:center;'><a href='login.html'>Try Again</a></p>");
    response.getWriter().println("</body></html>");
}

            rs.close();
            pst.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("<h3 style='color:red;text-align:center;'>Database error: " + e.getMessage() + "</h3>");
        }
    }
}
