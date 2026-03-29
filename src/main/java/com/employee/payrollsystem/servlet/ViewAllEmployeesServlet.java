package com.employee.payrollsystem.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;

public class ViewAllEmployeesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        // ✅ Get admin username from session
        HttpSession session = request.getSession(false);
        String loggedInUser = (session != null) ? (String) session.getAttribute("username") : null;

        try (Connection con = DBConnection.getConnection();
             var out = response.getWriter()) {

            String sql = "SELECT * FROM employees WHERE created_by = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, loggedInUser);
            ResultSet rs = ps.executeQuery();

            out.println("<!DOCTYPE html>");
            out.println("<html><head><title>All Employees</title>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; background: linear-gradient(to right, #fdfbfb, #ebedee); color: #333; padding: 40px; text-align: center; }");
            out.println("h2 { color: #007BFF; margin-bottom: 30px; }");
            out.println("table { width: 90%; margin: auto; border-collapse: collapse; background-color: #fff; box-shadow: 0 4px 12px rgba(0,0,0,0.1); }");
            out.println("th, td { padding: 12px 15px; border: 1px solid #ddd; }");
            out.println("th { background-color: #007BFF; color: #fff; }");
            out.println("tr:nth-child(even) { background-color: #f2f2f2; }");
            out.println("tr:hover { background-color: #e9f0f7; }");
            out.println("a { display: inline-block; margin-top: 20px; padding: 10px 20px; background-color: #007BFF; color: white; text-decoration: none; border-radius: 5px; font-weight: bold; }");
            out.println("a:hover { background-color: #0056b3; }");
            out.println("</style></head><body>");

            out.println("<h2>Employees Added by " + loggedInUser + "</h2>");
            out.println("<table><thead><tr>");
            out.println("<th>Employee ID</th><th>Name</th><th>DOB</th><th>Department</th><th>Salary</th><th>Date Hired</th>");
            out.println("</tr></thead><tbody>");

            boolean hasEmployees = false;
            while (rs.next()) {
                hasEmployees = true;
                out.println("<tr>");
                out.println("<td>" + rs.getString("empId") + "</td>");
                out.println("<td>" + rs.getString("name") + "</td>");
                out.println("<td>" + rs.getDate("dob") + "</td>");
                out.println("<td>" + rs.getString("department") + "</td>");
                out.println("<td>₹" + rs.getDouble("salary") + "</td>");
                out.println("<td>" + rs.getDate("dateHired") + "</td>");
                out.println("</tr>");
            }

            if (!hasEmployees) {
                out.println("<tr><td colspan='6' style='color:red;'>No employees found for this admin.</td></tr>");
            }

            out.println("</tbody></table>");
            out.println("<a href='home.html'>Back to Home</a>");
            out.println("</body></html>");

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
