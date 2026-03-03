package com.ticketsystem.models;

import java.sql.Timestamp;

public class Ticket {
    private int ticketId;
    private String title;
    private String description;
    private String status;
    private String priority;
    private String category;
    private int createdBy;
    private Integer assignedTo;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp resolvedAt;

    // Constructors
    public Ticket() {}

    public Ticket(String title, String description, String priority, 
                  String category, int createdBy) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.category = category;
        this.createdBy = createdBy;
        this.status = "OPEN";
    }

    // Getters and Setters
    public int getTicketId() { return ticketId; }
    public void setTicketId(int ticketId) { this.ticketId = ticketId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }

    public Integer getAssignedTo() { return assignedTo; }
    public void setAssignedTo(Integer assignedTo) { this.assignedTo = assignedTo; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public Timestamp getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(Timestamp resolvedAt) { this.resolvedAt = resolvedAt; }
}
