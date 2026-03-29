package com.employee.payrollsystem.servlet;

import com.employee.payrollsystem.servlet.DBConnection;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.time.LocalDate;

public class GenerateSlipServlet extends HttpServlet {

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

        String empId = request.getParameter("empId"); // matches form input name

        try (PrintWriter out = response.getWriter();
             Connection con = DBConnection.getConnection()) {

            // Get employee info only if created by this admin
            PreparedStatement ps1 = con.prepareStatement(
                "SELECT * FROM employees WHERE empId=? AND created_by=?"
            );
            ps1.setString(1, empId);
            ps1.setString(2, username);
            ResultSet rsEmp = ps1.executeQuery();

           if (!rsEmp.next()) { out.println("<p style='color:red;'>Employee not found! <a href='home.html'>Home</a></p>"); return; }


            String name = rsEmp.getString("name");
            String dept = rsEmp.getString("department");
            double baseSalary = rsEmp.getDouble("salary");

            // Allowances
            PreparedStatement ps2 = con.prepareStatement(
                "SELECT SUM(totalAllowance) AS totalAllowance FROM allowances WHERE empId=?"
            );
            ps2.setString(1, empId);
            ResultSet rsAllow = ps2.executeQuery();
            double totalAllowance = rsAllow.next() ? rsAllow.getDouble("totalAllowance") : 0;

            // Deductions
            PreparedStatement ps3 = con.prepareStatement(
                "SELECT SUM(amount) AS totalDeduction FROM deductions WHERE empId=?"
            );
            ps3.setString(1, empId);
            ResultSet rsDed = ps3.executeQuery();
            double totalDeduction = rsDed.next() ? rsDed.getDouble("totalDeduction") : 0;

            double netSalary = baseSalary + totalAllowance - totalDeduction;

            out.println("<div style='"
                + "background: #f9f9f9;"
                + "color: #333;"
                + "padding: 25px;"
                + "max-width: 500px;"
                + "margin: auto;"
                + "border-radius: 12px;"
                + "box-shadow: 0 4px 10px rgba(0,0,0,0.1);"
                + "font-family: Arial, sans-serif;'>");

            out.println("<h3 style='color: #007BFF;'>Salary Slip for Employee ID: " + empId + "</h3>");
            out.println("<p><strong>Name:</strong> " + name + "</p>");
            out.println("<p><strong>Department:</strong> " + dept + "</p>");
            out.println("<p><strong>Base Salary:</strong> ₹" + baseSalary + "</p>");
            out.println("<p><strong>Total Allowance:</strong> ₹" + totalAllowance + "</p>");
            out.println("<p><strong>Total Deduction:</strong> ₹" + totalDeduction + "</p>");
            out.println("<p><strong>Net Salary:</strong> ₹" + netSalary + "</p>");
            out.println("<p><strong>Date:</strong> " + LocalDate.now() + "</p>");
            out.println("</div>");

            out.println("<br><a href='home.html' style='"
                + "display: inline-block;"
                + "margin-top: 20px;"
                + "padding: 10px 20px;"
                + "background-color: #007BFF;"
                + "color: white;"
                + "text-decoration: none;"
                + "border-radius: 5px;"
                + "font-weight: bold;'>Back to Home</a>");

        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("<p style='color:red;'>Error: " + e.getMessage() + "</p>");
        }
    }
}
