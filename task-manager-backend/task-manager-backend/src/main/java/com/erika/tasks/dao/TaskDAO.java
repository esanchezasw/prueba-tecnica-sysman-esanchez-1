package com.erika.tasks.dao;

import com.erika.tasks.model.Task;
import com.erika.tasks.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {

    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();

        String sql = "SELECT TASK_ID, TITLE, DESCRIPTION, COMPLETED, CREATED_AT, UPDATED_AT FROM TASKS";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Task task = new Task();
                task.setTaskId(rs.getLong("TASK_ID"));  // Cambiado a getLong
                task.setTitle(rs.getString("TITLE"));
                task.setDescription(rs.getString("DESCRIPTION"));
                task.setCompleted(rs.getInt("COMPLETED") == 1); // 1 = true, 0 = false
                task.setCreated_at(rs.getTimestamp("CREATED_AT"));
                task.setUpdated_at(rs.getTimestamp("UPDATED_AT"));
                tasks.add(task);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tasks;
    }

    public boolean createTask(Task task) {
        String sql = "INSERT INTO tasks (title, description, completed) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, task.getTitle());
            stmt.setString(2, task.getDescription());
            stmt.setBoolean(3, task.isCompleted());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateTask(Task task) {
        String sql = "UPDATE tasks SET title = ?, description = ?, completed = ?, updated_at = CURRENT_TIMESTAMP WHERE task_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, task.getTitle());
            stmt.setString(2, task.getDescription());
            stmt.setBoolean(3, task.isCompleted());
            stmt.setLong(4, task.getTaskId());  // Cambiado a setLong y getTaskId()

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteTask(Long taskId) {
        String sql = "DELETE FROM tasks WHERE task_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, taskId);  // Cambiado a setLong

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
