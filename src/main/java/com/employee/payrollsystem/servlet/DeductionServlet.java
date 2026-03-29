package com.employee.payrollsystem.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.time.LocalDate;

public class DeductionServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        String empId = request.getParameter("empId");
        String empName = request.getParameter("empName");
        String department = request.getParameter("department");
        String reason = request.getParameter("reason");
        double amount = Double.parseDouble(request.getParameter("amount"));
        LocalDate date = LocalDate.now();

        HttpSession session = request.getSession(false);
        String username = (session != null) ? (String) session.getAttribute("username") : null;

        if (username == null) {
            response.sendRedirect("login.html"); // Not logged in
            return;
        }

        try (Connection con = DBConnection.getConnection();
             PrintWriter out = response.getWriter()) {

            // Check if employee exists and belongs to this admin
            PreparedStatement checkEmp = con.prepareStatement(
                "SELECT salary FROM employees WHERE empId = ? AND department = ? AND created_by = ?"
            );
            checkEmp.setString(1, empId.trim());
            checkEmp.setString(2, department.trim());
            checkEmp.setString(3, username);
            ResultSet rs = checkEmp.executeQuery();

            if (!rs.next()) {
                // Employee not found for this admin
                out.println("<!DOCTYPE html>");
                out.println("<html><head><title>Employee Not Found</title>");
                out.println("<script>");
                out.println("alert('❌ No employees found for this admin or Employee ID invalid!');");
                out.println("window.location.href='deduction.html';");
                out.println("</script>");
                out.println("</head><body></body></html>");
                System.out.println("DEBUG: Employee not found for ID=" + empId + ", Dept=" + department + ", Admin=" + username);
                return;
            }

            // Employee exists, apply deduction
            double currentSalary = rs.getDouble("salary");
            double newSalary = currentSalary - amount;

            PreparedStatement updateSalary = con.prepareStatement(
                "UPDATE employees SET salary=? WHERE empId=? AND department=? AND created_by=?"
            );
            updateSalary.setDouble(1, newSalary);
            updateSalary.setString(2, empId.trim());
            updateSalary.setString(3, department.trim());
            updateSalary.setString(4, username);
            updateSalary.executeUpdate();

            PreparedStatement insertDeduction = con.prepareStatement(
                "INSERT INTO deductions(empId, empName, department, reason, amount, date) VALUES (?, ?, ?, ?, ?, ?)"
            );
            insertDeduction.setString(1, empId);
            insertDeduction.setString(2, empName);
            insertDeduction.setString(3, department);
            insertDeduction.setString(4, reason);
            insertDeduction.setDouble(5, amount);
            insertDeduction.setDate(6, Date.valueOf(date));
            insertDeduction.executeUpdate();

            // Show success message
            out.println("<!DOCTYPE html>");
            out.println("<html><head><title>Deduction Applied</title>");
            out.println("<style>");
            out.println("@import url('https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap');");
            out.println("body {font-family: 'Poppins', sans-serif; background: linear-gradient(135deg, #e0f7e9, #d4e9f8, #f7f7f7); color:#222; text-align:center; padding-top:70px; margin:0;}");
            out.println(".card {background:#fff; display:inline-block; padding:35px 60px; border-radius:15px; box-shadow:0 8px 25px rgba(0,0,0,0.15); color:#333;}");
            out.println("h2 {color:#28a745; margin-bottom:20px;}");
            out.println("p {font-size:16px; color:#444; margin:8px 0;}"); 
            out.println("a {color:#fff; text-decoration:none; background:linear-gradient(90deg,#00c6ff,#0072ff); padding:10px 25px; border-radius:6px; font-weight:600; transition:0.3s; display:inline-block; margin:10px 8px 0 8px;}");
            out.println("a:hover {background:linear-gradient(90deg,#0099cc,#005bb5); transform:scale(1.05);}");
            out.println("</style></head><body>");
            out.println("<div class='card'>");
            out.println("<h2>✅ Deduction Applied Successfully!</h2>");
            out.println("<p><strong>Employee ID:</strong> " + empId + "</p>");
            out.println("<p><strong>Name:</strong> " + empName + "</p>");
            out.println("<p><strong>Department:</strong> " + department + "</p>");
            out.println("<p><strong>Reason:</strong> " + reason + "</p>");
            out.println("<p><strong>Amount Deducted:</strong> ₹" + amount + "</p>");
            out.println("<p><strong>Updated Salary:</strong> ₹" + newSalary + "</p>");
            out.println("<br><a href='deduction.html'>Add Another</a> <a href='home.html'>Home</a>");
            out.println("</div></body></html>");

        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("<p style='color:red;text-align:center;'>Error: " + e.getMessage() + "</p>");
        }
    }
}
