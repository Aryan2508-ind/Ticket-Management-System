package com.ticketsystem.dao;

import com.ticketsystem.models.Ticket;
import com.ticketsystem.utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class TicketDAO {
    private Connection connection;

    public TicketDAO() throws SQLException {
        connection = DatabaseConnection.getInstance().getConnection();
    }

    // Create new ticket
    public int createTicket(Ticket ticket) throws SQLException {
        String sql = "INSERT INTO tickets (title, description, status, priority, category, created_by, assigned_to) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, ticket.getTitle());
            pstmt.setString(2, ticket.getDescription());
            pstmt.setString(3, ticket.getStatus());
            pstmt.setString(4, ticket.getPriority());
            pstmt.setString(5, ticket.getCategory());
            pstmt.setInt(6, ticket.getCreatedBy());
            
            if (ticket.getAssignedTo() != null) {
                pstmt.setInt(7, ticket.getAssignedTo());
            } else {
                pstmt.setNull(7, Types.INTEGER);
            }
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        }
        return -1;
    }

    // Get ticket by ID
    public Ticket getTicketById(int ticketId) throws SQLException {
        String sql = "SELECT * FROM tickets WHERE ticket_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, ticketId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTicket(rs);
                }
            }
        }
        return null;
    }

    // Get tickets by status (using index)
    public List<Ticket> getTicketsByStatus(String status) throws SQLException {
        String sql = "SELECT * FROM tickets WHERE status = ? ORDER BY priority DESC, created_at DESC";
        List<Ticket> tickets = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, status);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tickets.add(mapResultSetToTicket(rs));
                }
            }
        }
        return tickets;
    }

    // Get tickets assigned to user (using index)
    public List<Ticket> getTicketsAssignedToUser(int userId) throws SQLException {
        String sql = "SELECT * FROM tickets WHERE assigned_to = ? AND status IN ('OPEN', 'IN_PROGRESS') " +
                     "ORDER BY priority DESC, created_at ASC";
        List<Ticket> tickets = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tickets.add(mapResultSetToTicket(rs));
                }
            }
        }
        return tickets;
    }

    // Update ticket status
    public boolean updateTicketStatus(int ticketId, String newStatus, int userId) throws SQLException {
        connection.setAutoCommit(false);
        
        try {
            // Get current status
            String getCurrentSql = "SELECT status FROM tickets WHERE ticket_id = ?";
            String oldStatus = null;
            
            try (PreparedStatement pstmt = connection.prepareStatement(getCurrentSql)) {
                pstmt.setInt(1, ticketId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        oldStatus = rs.getString("status");
                    }
                }
            }
            
            // Update ticket
            String updateSql = "UPDATE tickets SET status = ?, resolved_at = ? WHERE ticket_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(updateSql)) {
                pstmt.setString(1, newStatus);
                
                if ("RESOLVED".equals(newStatus) || "CLOSED".equals(newStatus)) {
                    pstmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                } else {
                    pstmt.setNull(2, Types.TIMESTAMP);
                }
                
                pstmt.setInt(3, ticketId);
                pstmt.executeUpdate();
            }
            
            // Log to history
            logTicketHistory(ticketId, userId, "status", oldStatus, newStatus);
            
            connection.commit();
            return true;
            
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    // Search tickets with optimized query
    public List<Ticket> searchTickets(String keyword, String status, String priority) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT * FROM tickets WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (keyword != null && !keyword.isEmpty()) {
            sql.append(" AND (title LIKE ? OR description LIKE ?)");
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");
        }
        
        if (status != null && !status.isEmpty()) {
            sql.append(" AND status = ?");
            params.add(status);
        }
        
        if (priority != null && !priority.isEmpty()) {
            sql.append(" AND priority = ?");
            params.add(priority);
        }
        
        sql.append(" ORDER BY priority DESC, created_at DESC LIMIT 100");
        
        List<Ticket> tickets = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tickets.add(mapResultSetToTicket(rs));
                }
            }
        }
        return tickets;
    }

    // Get ticket statistics
    public Map<String, Integer> getTicketStatistics() throws SQLException {
        String sql = "SELECT status, COUNT(*) as count FROM tickets GROUP BY status";
        Map<String, Integer> stats = new HashMap<>();
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                stats.put(rs.getString("status"), rs.getInt("count"));
            }
        }
        return stats;
    }

    // Helper method to log history
    private void logTicketHistory(int ticketId, int userId, String fieldName, 
                                  String oldValue, String newValue) throws SQLException {
        String sql = "INSERT INTO ticket_history (ticket_id, changed_by, field_name, old_value, new_value) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, ticketId);
            pstmt.setInt(2, userId);
            pstmt.setString(3, fieldName);
            pstmt.setString(4, oldValue);
            pstmt.setString(5, newValue);
            pstmt.executeUpdate();
        }
    }

    // Helper method to map ResultSet to Ticket
    private Ticket mapResultSetToTicket(ResultSet rs) throws SQLException {
        Ticket ticket = new Ticket();
        ticket.setTicketId(rs.getInt("ticket_id"));
        ticket.setTitle(rs.getString("title"));
        ticket.setDescription(rs.getString("description"));
        ticket.setStatus(rs.getString("status"));
        ticket.setPriority(rs.getString("priority"));
        ticket.setCategory(rs.getString("category"));
        ticket.setCreatedBy(rs.getInt("created_by"));
        
        int assignedTo = rs.getInt("assigned_to");
        if (!rs.wasNull()) {
            ticket.setAssignedTo(assignedTo);
        }
        
        ticket.setCreatedAt(rs.getTimestamp("created_at"));
        ticket.setUpdatedAt(rs.getTimestamp("updated_at"));
        ticket.setResolvedAt(rs.getTimestamp("resolved_at"));
        
        return ticket;
    }
}
