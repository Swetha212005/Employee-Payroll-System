package com.employee.payrollsystem.servlet;

import com.employee.payrollsystem.servlet.DBConnection;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;

public class DeleteEmployeeServlet extends HttpServlet {

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

        try (PrintWriter out = response.getWriter(); Connection con = DBConnection.getConnection()) {

            // Check if employee belongs to this admin
            PreparedStatement checkEmp = con.prepareStatement(
                "SELECT * FROM employees WHERE empId=? AND created_by=?"
            );
            checkEmp.setString(1, empId.trim());
            checkEmp.setString(2, username);
            ResultSet rs = checkEmp.executeQuery();

            if (!rs.next()) {
                out.println("<script>");
                out.println("alert('❌ Employee not found or not created by you!');");
                out.println("window.location.href='delete_employee.html';");
                out.println("</script>");
                return;
            }

            // Delete related allowances and deductions
            PreparedStatement ps1 = con.prepareStatement("DELETE FROM allowances WHERE empId=?");
            ps1.setString(1, empId);
            ps1.executeUpdate();

            PreparedStatement ps2 = con.prepareStatement("DELETE FROM deductions WHERE empId=?");
            ps2.setString(1, empId);
            ps2.executeUpdate();

            // Delete employee
            PreparedStatement ps3 = con.prepareStatement("DELETE FROM employees WHERE empId=? AND created_by=?");
            ps3.setString(1, empId);
            ps3.setString(2, username);
            int rows = ps3.executeUpdate();

            // HTML response
            out.println("<!DOCTYPE html>");
            out.println("<html><head><title>Delete Employee</title>");
            out.println("<style>");
            out.println("body {font-family: 'Segoe UI', sans-serif; background: linear-gradient(135deg, #ff4b1f, #ff9068); height:100vh; display:flex; justify-content:center; align-items:center; margin:0;}");
            out.println(".card {background: rgba(255,255,255,0.1); padding:40px; border-radius:15px; text-align:center; box-shadow:0 8px 20px rgba(0,0,0,0.3); color:white;}");
            out.println("a {display:inline-block; margin-top:20px; text-decoration:none; background:#ffd700; color:#000; padding:10px 20px; border-radius:5px; font-weight:bold;}");
            out.println("a:hover {background:#e6c200;}");
            out.println("</style></head><body><div class='card'>");

            out.println("<script>");
            if (rows > 0) {
                out.println("alert('✅ Employee deleted successfully! Employee ID: " + empId + "');");
                out.println("window.location.href='home.html';");
            } else {
                out.println("alert('❌ Employee not found! Check the Employee ID and try again.');");
                out.println("window.location.href='delete_employee.html';");
            }
            out.println("</script>");

            out.println("</div></body></html>");

        } catch (SQLException e) {
            e.printStackTrace();
            try (PrintWriter out = response.getWriter()) {
                out.println("<script>");
                out.println("alert('Database Error: " + e.getMessage() + "');");
                out.println("window.location.href='home.html';");
                out.println("</script>");
            }
        }
    }
}
