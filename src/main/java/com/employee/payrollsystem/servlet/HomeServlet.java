package com.employee.payrollsystem.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

public class HomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get existing session (do not create new one)
        HttpSession session = request.getSession(false);

        // If no session or user not logged in, redirect to login
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect("login.html");
            return;
        }

        // ✅ User is logged in — forward to styled home page
        RequestDispatcher dispatcher = request.getRequestDispatcher("home.html");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Just call doGet to handle both POST and GET the same way
        doGet(request, response);
    }
}
