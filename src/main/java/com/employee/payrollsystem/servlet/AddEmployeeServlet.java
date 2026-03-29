package com.employee.payrollsystem.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;

public class AddEmployeeServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String empId = request.getParameter("empId");
        String name = request.getParameter("name");
        String dob = request.getParameter("dob");
        String department = request.getParameter("department");
        String salaryStr = request.getParameter("salary");
        String dateHired = request.getParameter("dateHired");

        HttpSession session = request.getSession(false);
        String createdBy = (session != null) ? (String) session.getAttribute("username") : null;

        if (empId.isEmpty() || name.isEmpty() || dob.isEmpty() || department.isEmpty()
                || salaryStr.isEmpty() || dateHired.isEmpty()) {
            out.println("<script type='text/javascript'>");
            out.println("alert('All fields are required!');");
            out.println("window.history.back();"); // go back to the form
            out.println("</script>");
            return;
        }

        double salary = Double.parseDouble(salaryStr);

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO employees(empId, name, dob, department, salary, dateHired, created_by) VALUES(?,?,?,?,?,?,?)"
            );
            ps.setString(1, empId);
            ps.setString(2, name);
            ps.setDate(3, java.sql.Date.valueOf(dob));
            ps.setString(4, department);
            ps.setDouble(5, salary);
            ps.setDate(6, java.sql.Date.valueOf(dateHired));
            ps.setString(7, createdBy);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                out.println("<script type='text/javascript'>");
                out.println("alert('Employee added successfully!');");
                out.println("window.location.href='add_employee.html';"); // redirect to same form
                out.println("</script>");
            } else {
                out.println("<script type='text/javascript'>");
                out.println("alert('Failed to add employee. Please try again.');");
                out.println("window.history.back();");
                out.println("</script>");
            }

        } catch (SQLException e) {
            out.println("<script type='text/javascript'>");
            out.println("alert('Database Error: " + e.getMessage().replace("'", "") + "');");
            out.println("window.history.back();");
            out.println("</script>");
        }
    }
}
