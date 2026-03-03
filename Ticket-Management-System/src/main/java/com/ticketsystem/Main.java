package com.ticketsystem;

import com.ticketsystem.service.TicketService;
import com.ticketsystem.models.Ticket;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static TicketService ticketService;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            ticketService = new TicketService();
            System.out.println("=== Ticket Management System ===");
            
            while (true) {
                showMenu();
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                
                switch (choice) {
                    case 1:
                        createNewTicket();
                        break;
                    case 2:
                        viewOpenTickets();
                        break;
                    case 3:
                        updateTicketStatus();
                        break;
                    case 4:
                        searchTickets();
                        break;
                    case 5:
                        viewStatistics();
                        break;
                    case 0:
                        System.out.println("Exiting...");
                        System.exit(0);
                    default:
                        System.out.println("Invalid choice!");
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void showMenu() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("1. Create New Ticket");
        System.out.println("2. View Open Tickets");
        System.out.println("3. Update Ticket Status");
        System.out.println("4. Search Tickets");
        System.out.println("5. View Statistics");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
    }

    private static void createNewTicket() throws SQLException {
        System.out.println("\n--- Create New Ticket ---");
        
        System.out.print("Title: ");
        String title = scanner.nextLine();
        
        System.out.print("Description: ");
        String description = scanner.nextLine();
        
        System.out.print("Priority (LOW/MEDIUM/HIGH/CRITICAL): ");
        String priority = scanner.nextLine().toUpperCase();
        
        System.out.print("Category: ");
        String category = scanner.nextLine();
        
        int ticketId = ticketService.createTicket(title, description, priority, category, 1);
        System.out.println("✓ Ticket created successfully with ID: " + ticketId);
    }

    private static void viewOpenTickets() throws SQLException {
        System.out.println("\n--- Open Tickets ---");
        List<Ticket> tickets = ticketService.getOpenTickets();
        
        if (tickets.isEmpty()) {
            System.out.println("No open tickets found.");
        } else {
            for (Ticket ticket : tickets) {
                System.out.printf("ID: %d | %s | Priority: %s | %s\n",
                    ticket.getTicketId(),
                    ticket.getTitle(),
                    ticket.getPriority(),
                    ticket.getCreatedAt()
                );
            }
        }
    }

    private static void updateTicketStatus() throws SQLException {
        System.out.print("Enter Ticket ID: ");
        int ticketId = scanner.nextInt();
        scanner.nextLine();
        
        System.out.print("New Status (OPEN/IN_PROGRESS/RESOLVED/CLOSED): ");
        String newStatus = scanner.nextLine().toUpperCase();
        
        if (ticketService.updateTicketStatus(ticketId, newStatus, 1)) {
            System.out.println("✓ Ticket status updated successfully!");
        }
    }

    private static void searchTickets() throws SQLException {
        System.out.println("\n--- Search Tickets ---");
        
        System.out.print("Enter keyword (or press Enter to skip): ");
        String keyword = scanner.nextLine();
        
        System.out.print("Filter by Status (or press Enter to skip): ");
        String status = scanner.nextLine().toUpperCase();
        
        System.out.print("Filter by Priority (or press Enter to skip): ");
        String priority = scanner.nextLine().toUpperCase();
        
        List<Ticket> results = ticketService.searchTickets(
            keyword.isEmpty() ? null : keyword,
            status.isEmpty() ? null : status,
            priority.isEmpty() ? null : priority
        );
        
        System.out.println("\nSearch Results: " + results.size() + " tickets found");
        for (Ticket ticket : results) {
            System.out.printf("ID: %d | %s | Status: %s | Priority: %s\n",
                ticket.getTicketId(),
                ticket.getTitle(),
                ticket.getStatus(),
                ticket.getPriority()
            );
        }
    }

    private static void viewStatistics() throws SQLException {
        System.out.println("\n--- Ticket Statistics ---");
        var stats = ticketService.getDashboardStatistics();
        
        System.out.println("Tickets by Status:");
        stats.forEach((status, count) -> 
            System.out.println("  " + status + ": " + count)
        );
    }
}
