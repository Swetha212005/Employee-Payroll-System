package com.employee.payrollsystem.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;

public class SearchEmployeeServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession(false);
        String username = (session != null) ? (String) session.getAttribute("username") : null;

        if (username == null) {
            response.sendRedirect("login.html");
            return;
        }

        String empId = request.getParameter("empId");

        try (Connection con = DBConnection.getConnection();
             var out = response.getWriter()) {

            String sql = "SELECT * FROM employees WHERE empId = ? AND created_by = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, empId);
            ps.setString(2, username);
            ResultSet rs = ps.executeQuery();

            out.println("<!DOCTYPE html>");
            out.println("<html><head><title>Employee Details</title>");
            out.println("<style>");
            out.println("@import url('https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap');");
            out.println("body { font-family: 'Poppins', sans-serif; background: linear-gradient(135deg, #e0f7e9, #d4e9f8, #f7f7f7); color: #222; text-align: center; padding-top: 70px; margin: 0; }");
            out.println(".card { background: #ffffff; display: inline-block; padding: 35px 60px; border-radius: 15px; box-shadow: 0 6px 18px rgba(0, 0, 0, 0.15); color: #333; }");
            out.println("h2 { color: #007BFF; margin-bottom: 20px; }");
            out.println("p { font-size: 16px; color: #555; margin: 8px 0; }");
            out.println("a { color: white; text-decoration: none; background: linear-gradient(90deg, #00c6ff, #0072ff); padding: 10px 25px; border-radius: 6px; font-weight: 600; transition: 0.3s; display: inline-block; margin-top: 20px; }");
            out.println("a:hover { background: linear-gradient(90deg, #0099cc, #005bb5); transform: scale(1.05); }");
            out.println("</style></head><body>");

            if (!rs.next()) {
                out.println("<div class='card'>");
                out.println("<h2>No employees found for this admin!</h2>");
                out.println("<a href='home.html'>Back to Home</a>");
                out.println("</div>");
            } else {
                out.println("<div class='card'>");
                out.println("<h2>Employee Details</h2>");
                out.println("<p><strong>ID:</strong> " + rs.getString("empId") + "</p>");
                out.println("<p><strong>Name:</strong> " + rs.getString("name") + "</p>");
                out.println("<p><strong>DOB:</strong> " + rs.getDate("dob") + "</p>");
                out.println("<p><strong>Department:</strong> " + rs.getString("department") + "</p>");
                out.println("<p><strong>Salary:</strong> ₹" + rs.getDouble("salary") + "</p>");
                out.println("<p><strong>Date Hired:</strong> " + rs.getDate("dateHired") + "</p>");
                out.println("<br><a href='home.html'>Back to Home</a>");
                out.println("</div>");
            }

            rs.close();
            ps.close();

            out.println("</body></html>");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
