package com.employee.payrollsystem.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;

public class UpdateSalaryServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String empId = request.getParameter("empId");
        double increment = Double.parseDouble(request.getParameter("increment"));
        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession(false);
        String created_by = (session != null) ? (String) session.getAttribute("username") : null;

        if (created_by == null) {
            response.sendRedirect("login.html"); // not logged in
            return;
        }

        try (Connection con = DBConnection.getConnection();
             var out = response.getWriter()) {

            // ✅ Only fetch employee if it belongs to this admin
            PreparedStatement ps1 = con.prepareStatement(
                "SELECT salary, name, department FROM employees WHERE empId=? AND created_by=?"
            );
            ps1.setString(1, empId);
            ps1.setString(2, created_by);
            ResultSet rs = ps1.executeQuery();

            out.println("<!DOCTYPE html>");
            out.println("<html><head><title>Salary Update</title>");
            out.println("<style>");
            out.println("body {font-family: Arial, sans-serif; background: linear-gradient(135deg, #fdfbfb, #ebedee); color: #333; text-align: center; padding-top: 50px;}");
            out.println(".card {background: rgba(255,255,255,0.9); display: inline-block; padding: 30px 50px; border-radius: 12px; box-shadow: 0 5px 20px rgba(0,0,0,0.2);}");
            out.println("a {color: white; text-decoration:none; background:#007BFF; padding:10px 20px; border-radius:5px; font-weight:bold; display: inline-block; margin-top: 20px;}");
            out.println("a:hover {background:#0056b3;}");
            out.println("</style></head><body>");

            if (!rs.next()) {
                // ❌ Employee not found for this admin
                out.println("<div class='card'>");
                out.println("<h4> Employee ID not found!</h4>");
                out.println("<a href='update_salary.html'>Back</a> &nbsp; <a href='home.html'>Home</a>");
                out.println("</div>");
                return;
            }

            String name = rs.getString("name");
            String department = rs.getString("department");
            double currentSalary = rs.getDouble("salary");
            double newSalary = currentSalary + increment;

            // Update salary
            PreparedStatement ps2 = con.prepareStatement(
                "UPDATE employees SET salary=? WHERE empId=? AND created_by=?"
            );
            ps2.setDouble(1, newSalary);
            ps2.setString(2, empId);
            ps2.setString(3, created_by);
            ps2.executeUpdate();

            // Show success message
            out.println("<div class='card'>");
            out.println("<h2>Salary Updated Successfully!</h2>");
            out.println("<p><strong>Employee ID:</strong> " + empId + "</p>");
            out.println("<p><strong>Name:</strong> " + name + "</p>");
            out.println("<p><strong>Department:</strong> " + department + "</p>");
            out.println("<p><strong>Increment:</strong> ₹" + increment + "</p>");
            out.println("<p><strong>New Salary:</strong> ₹" + newSalary + "</p>");
            out.println("<br><a href='update_salary.html'>Update Another</a> &nbsp; <a href='home.html'>Home</a>");
            out.println("</div>");

            out.println("</body></html>");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
