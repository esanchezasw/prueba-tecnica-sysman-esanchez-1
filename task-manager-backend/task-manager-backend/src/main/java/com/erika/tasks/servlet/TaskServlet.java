package com.erika.tasks.servlet;

import com.erika.tasks.dao.TaskDAO;
import com.erika.tasks.model.Task;
import com.google.gson.Gson;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/tasks/*")
public class TaskServlet extends HttpServlet {

    private final TaskDAO taskDAO = new TaskDAO();
    private final Gson gson = new Gson();

    // Agrega cabeceras CORS
    private void setCorsHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:56575"); // Cambia según tu puerto del frontend
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Allow-Credentials", "true");
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        setCorsHeaders(response);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        setCorsHeaders(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        List<Task> tasks = taskDAO.getAllTasks();
        String json = gson.toJson(tasks);

        try (PrintWriter out = response.getWriter()) {
            out.print(json);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        setCorsHeaders(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            Task task = parseRequestBody(request);

            if (task == null || task.getTitle() == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().print("{\"error\":\"Datos inválidos\"}");
                return;
            }

            boolean created = taskDAO.createTask(task);
            response.setStatus(created ? HttpServletResponse.SC_CREATED : HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("{\"success\":" + created + "}");

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("{\"error\":\"Error interno: " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        setCorsHeaders(response);
        Task task = parseRequestBody(request);
        boolean updated = taskDAO.updateTask(task);

        response.setContentType("application/json");
        response.setStatus(updated ? HttpServletResponse.SC_OK : HttpServletResponse.SC_BAD_REQUEST);

        try (PrintWriter out = response.getWriter()) {
            out.print("{\"success\":" + updated + "}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        setCorsHeaders(response);
        String pathInfo = request.getPathInfo(); // e.g. /5
        Long taskId = pathInfo != null && pathInfo.length() > 1
                ? Long.valueOf(pathInfo.substring(1))
                : -1;

        boolean deleted = taskId != -1L && taskDAO.deleteTask(taskId);

        response.setContentType("application/json");
        response.setStatus(deleted ? HttpServletResponse.SC_OK : HttpServletResponse.SC_BAD_REQUEST);

        try (PrintWriter out = response.getWriter()) {
            out.print("{\"success\":" + deleted + "}");
        }
    }

    private Task parseRequestBody(HttpServletRequest request) throws IOException {
        BufferedReader reader = request.getReader();
        StringBuilder jsonBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonBody.append(line);
        }

        System.out.println("JSON recibido: " + jsonBody.toString());  // Log para debug

        return gson.fromJson(jsonBody.toString(), Task.class);
    }
}
