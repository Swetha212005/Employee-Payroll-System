package com.employee.payrollsystem.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

public class AllowanceServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        // Get admin username from session
        HttpSession session = request.getSession(false);
        String username = (session != null) ? (String) session.getAttribute("username") : null;
        if (username == null) {
            response.sendRedirect("login.html");
            return;
        }

        String empId = request.getParameter("empId");
        String empName = request.getParameter("empName");
        String department = request.getParameter("department");
        int overtimeHours = Integer.parseInt(request.getParameter("overtimeHours"));
        double rate = Double.parseDouble(request.getParameter("ratePerHour"));
        double medical = Double.parseDouble(request.getParameter("medical"));
        double bonus = Double.parseDouble(request.getParameter("bonus"));
        double other = Double.parseDouble(request.getParameter("other"));
        double totalSalary = Double.parseDouble(request.getParameter("totalSalary"));

        try (Connection con = DBConnection.getConnection()) {

            // ✅ 1. Check if the employee exists and belongs to this admin
            PreparedStatement checkEmp = con.prepareStatement(
                    "SELECT * FROM employees WHERE empId=? AND department=? AND created_by=? AND name=?"
            );
            checkEmp.setString(1, empId.trim());
            checkEmp.setString(2, department.trim());
            checkEmp.setString(3, username);
            checkEmp.setString(4, empName);
            ResultSet rs = checkEmp.executeQuery();

            if (!rs.next()) {
                // Employee not found for this admin
                out.println("<script type='text/javascript'>");
                out.println("alert('❌ No employees found for this admin or Employee ID invalid!');");
                out.println("window.location.href='allowance.html';");
                out.println("</script>");
                return;
            }

            // ✅ 2. Calculate allowances
            double overtimePay = overtimeHours * rate;
            double totalAllowance = overtimePay + medical + bonus + other;

            // ✅ 3. Insert allowance record
            String sql = "INSERT INTO allowances(empId, empName, department, overtimeHours, ratePerHour, overtimePay, medical, bonus, other, totalAllowance, totalSalary) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, empId);
            ps.setString(2, empName);
            ps.setString(3, department);
            ps.setInt(4, overtimeHours);
            ps.setDouble(5, rate);
            ps.setDouble(6, overtimePay);
            ps.setDouble(7, medical);
            ps.setDouble(8, bonus);
            ps.setDouble(9, other);
            ps.setDouble(10, totalAllowance);
            ps.setDouble(11, totalSalary);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                out.println("<script type='text/javascript'>");
                out.println("alert('✅ Allowance and Salary Added Successfully!');");
                out.println("window.location.href='home.html';");
                out.println("</script>");
            } else {
                out.println("<script type='text/javascript'>");
                out.println("alert('Error adding data. Try again!');");
                out.println("window.location.href='allowance.html';");
                out.println("</script>");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            out.println("<script type='text/javascript'>");
            out.println("alert('Database Error: " + e.getMessage().replace("'", "") + "');");
            out.println("window.location.href='allowance.html';");
            out.println("</script>");
        }
    }
}
