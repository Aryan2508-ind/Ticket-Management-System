package com.ticketsystem.service;

import com.ticketsystem.dao.TicketDAO;
import com.ticketsystem.models.Ticket;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class TicketService {
    private TicketDAO ticketDAO;

    public TicketService() throws SQLException {
        this.ticketDAO = new TicketDAO();
    }

    public int createTicket(String title, String description, String priority, 
                           String category, int userId) throws SQLException {
        
        // Validate input
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }
        
        Ticket ticket = new Ticket(title, description, priority, category, userId);
        return ticketDAO.createTicket(ticket);
    }

    public Ticket getTicketDetails(int ticketId) throws SQLException {
        return ticketDAO.getTicketById(ticketId);
    }

    public List<Ticket> getOpenTickets() throws SQLException {
        return ticketDAO.getTicketsByStatus("OPEN");
    }

    public List<Ticket> getUserAssignedTickets(int userId) throws SQLException {
        return ticketDAO.getTicketsAssignedToUser(userId);
    }

    public boolean updateTicketStatus(int ticketId, String newStatus, int userId) throws SQLException {
        // Validate status transition
        Ticket ticket = ticketDAO.getTicketById(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("Ticket not found");
        }
        
        if (!isValidStatusTransition(ticket.getStatus(), newStatus)) {
            throw new IllegalArgumentException("Invalid status transition");
        }
        
        return ticketDAO.updateTicketStatus(ticketId, newStatus, userId);
    }

    public List<Ticket> searchTickets(String keyword, String status, String priority) throws SQLException {
        return ticketDAO.searchTickets(keyword, status, priority);
    }

    public Map<String, Integer> getDashboardStatistics() throws SQLException {
        return ticketDAO.getTicketStatistics();
    }

    private boolean isValidStatusTransition(String currentStatus, String newStatus) {
        // Define valid status transitions
        switch (currentStatus) {
            case "OPEN":
                return "IN_PROGRESS".equals(newStatus) || "CLOSED".equals(newStatus);
            case "IN_PROGRESS":
                return "RESOLVED".equals(newStatus) || "OPEN".equals(newStatus);
            case "RESOLVED":
                return "CLOSED".equals(newStatus) || "OPEN".equals(newStatus);
            case "CLOSED":
                return "OPEN".equals(newStatus);
            default:
                return false;
        }
    }
}
