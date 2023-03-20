package com.bobocode.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

@WebServlet("/date")
public class DateServlet extends HttpServlet {

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        final LocalDateTime currentTime = LocalDateTime.now();

        final PrintWriter writer = resp.getWriter();
        writer.println(currentTime);
        writer.flush();

        final String clientAddress = req.getRemoteAddr();
        System.out.println("clientAddress = " + clientAddress);
    }
}
