package com.xtuml.generated;

import com.xtuml.runtime.BaseModel;
import com.xtuml.runtime.EventDispatcher;
import com.xtuml.runtime.ObjectBroker;
import com.xtuml.runtime.XtUmlEvent;
import java.util.Scanner;
import java.util.UUID;
import java.util.List;

public class InteractiveApp {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== xtUML Interactive Console ===");
        // Run automatically generated system runner if exists
        // RunSystem.main(args); // Optional: if we want to pre-populate data

        while (true) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. Customer");
            System.out.println("2. PSConsole");
            System.out.println("3. Rate");
            System.out.println("4. Rental");
            System.out.println("5. Exit");
            System.out.print("Select an option: ");

            String input = scanner.nextLine();
            try {
                int choice = Integer.parseInt(input);
                if (choice == 5) {
                    System.out.println("Exiting...");
                    break;
                }

                switch (choice) {
                    case 1:
                        manageCustomer();
                        break;
                    case 2:
                        managePSConsole();
                        break;
                    case 3:
                        manageRate();
                        break;
                    case 4:
                        manageRental();
                        break;
                    default:
                        System.out.println("Invalid option.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
            
            // Process any pending events after user interaction
            EventDispatcher.getInstance().processQueue();
        }
    }

    private static void manageCustomer() {
        while (true) {
            System.out.println("\n--- Manager: Customer ---");
            System.out.println("1. Create New Instance");
            System.out.println("2. Select Existing Instance");
            System.out.println("3. Back");
            System.out.print("Choose: ");

            String input = scanner.nextLine();
            if (input.equals("3")) return;

            if (input.equals("1")) {
                createCustomer();
            } else if (input.equals("2")) {
                selectCustomer();
            } else {
                System.out.println("Invalid option.");
            }
        }
    }

    private static void createCustomer() {
        System.out.println("Creating Customer...");
        Customer instance = new Customer();
        System.out.print("Enter customer_id (id): ");
        try {
            String val = scanner.nextLine();
            // ID handling: input string is converted to UUID bytes or hash?
            // For simplicity, we'll try to parse or generate random if empty
            if (val.trim().isEmpty()) {
                instance.setCustomer_id(UUID.randomUUID());
            } else {
                try {
                    instance.setCustomer_id(UUID.fromString(val));
                } catch (IllegalArgumentException e) {
                    // Fallback for non-standard UUID strings
                    instance.setCustomer_id(UUID.nameUUIDFromBytes(val.getBytes()));
                }
            }
        } catch (Exception e) {
            System.out.println("Error setting customer_id: " + e.getMessage());
        }
        System.out.print("Enter name (string): ");
        try {
            String val = scanner.nextLine();
            instance.setName(val);
        } catch (Exception e) {
            System.out.println("Error setting name: " + e.getMessage());
        }
        System.out.print("Enter contact (string): ");
        try {
            String val = scanner.nextLine();
            instance.setContact(val);
        } catch (Exception e) {
            System.out.println("Error setting contact: " + e.getMessage());
        }
        System.out.print("Enter email (string): ");
        try {
            String val = scanner.nextLine();
            instance.setEmail(val);
        } catch (Exception e) {
            System.out.println("Error setting email: " + e.getMessage());
        }
        System.out.println("Created instance with ID: " + instance.getInstanceId());
    }

    private static void selectCustomer() {
        List<Customer> instances = ObjectBroker.getInstance().selectAll(Customer.class);
        if (instances.isEmpty()) {
            System.out.println("No instances found.");
            return;
        }

        for (int i = 0; i < instances.size(); i++) {
            System.out.println((i + 1) + ". " + instances.get(i).getInstanceId() + " [" + instances.get(i).getClass().getSimpleName() + "]");
        }
        System.out.print("Select instance: ");
        try {
            int idx = Integer.parseInt(scanner.nextLine()) - 1;
            if (idx >= 0 && idx < instances.size()) {
                interactWithCustomer(instances.get(idx));
            } else {
                System.out.println("Invalid selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    private static void interactWithCustomer(Customer instance) {
        while (true) {
            System.out.println("\n--- Interact with Customer : " + instance.getInstanceId() + " ---");
            System.out.println("1. Back");
            System.out.print("Choose action: ");
            try {
                int action = Integer.parseInt(scanner.nextLine());
                if (action == 1) return;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
            }
        }
    }

    private static void managePSConsole() {
        while (true) {
            System.out.println("\n--- Manager: PSConsole ---");
            System.out.println("1. Create New Instance");
            System.out.println("2. Select Existing Instance");
            System.out.println("3. Back");
            System.out.print("Choose: ");

            String input = scanner.nextLine();
            if (input.equals("3")) return;

            if (input.equals("1")) {
                createPSConsole();
            } else if (input.equals("2")) {
                selectPSConsole();
            } else {
                System.out.println("Invalid option.");
            }
        }
    }

    private static void createPSConsole() {
        System.out.println("Creating PSConsole...");
        PSConsole instance = new PSConsole();
        System.out.print("Enter console_id (id): ");
        try {
            String val = scanner.nextLine();
            // ID handling: input string is converted to UUID bytes or hash?
            // For simplicity, we'll try to parse or generate random if empty
            if (val.trim().isEmpty()) {
                instance.setConsole_id(UUID.randomUUID());
            } else {
                try {
                    instance.setConsole_id(UUID.fromString(val));
                } catch (IllegalArgumentException e) {
                    // Fallback for non-standard UUID strings
                    instance.setConsole_id(UUID.nameUUIDFromBytes(val.getBytes()));
                }
            }
        } catch (Exception e) {
            System.out.println("Error setting console_id: " + e.getMessage());
        }
        System.out.print("Enter model (string): ");
        try {
            String val = scanner.nextLine();
            instance.setModel(val);
        } catch (Exception e) {
            System.out.println("Error setting model: " + e.getMessage());
        }
        System.out.print("Enter availability (state): ");
        try {
            String val = scanner.nextLine();
            instance.setAvailability(val);
        } catch (Exception e) {
            System.out.println("Error setting availability: " + e.getMessage());
        }
        System.out.println("Created instance with ID: " + instance.getInstanceId());
    }

    private static void selectPSConsole() {
        List<PSConsole> instances = ObjectBroker.getInstance().selectAll(PSConsole.class);
        if (instances.isEmpty()) {
            System.out.println("No instances found.");
            return;
        }

        for (int i = 0; i < instances.size(); i++) {
            System.out.println((i + 1) + ". " + instances.get(i).getInstanceId() + " [" + instances.get(i).getClass().getSimpleName() + "]");
        }
        System.out.print("Select instance: ");
        try {
            int idx = Integer.parseInt(scanner.nextLine()) - 1;
            if (idx >= 0 && idx < instances.size()) {
                interactWithPSConsole(instances.get(idx));
            } else {
                System.out.println("Invalid selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    private static void interactWithPSConsole(PSConsole instance) {
        while (true) {
            System.out.println("\n--- Interact with PSConsole : " + instance.getInstanceId() + " ---");
            System.out.println("[Events]");
            System.out.println("1. setAvailable");
            System.out.println("2. onAvailable");
            System.out.println("3. setRented");
            System.out.println("4. onRented");
            System.out.println("5. setMaintenance");
            System.out.println("6. Back");
            System.out.print("Choose action: ");
            try {
                int action = Integer.parseInt(scanner.nextLine());
                if (action == 6) return;
                if (action >= 1 && action <= 5) {
                    // Fire Event
                    int eventId = 0;
                    switch(action) {
                        case 1: eventId = PSConsole.EVENT_SETAVAILABLE; break;
                        case 2: eventId = PSConsole.EVENT_ONAVAILABLE; break;
                        case 3: eventId = PSConsole.EVENT_SETRENTED; break;
                        case 4: eventId = PSConsole.EVENT_ONRENTED; break;
                        case 5: eventId = PSConsole.EVENT_SETMAINTENANCE; break;
                    }
                    if (eventId != 0) {
                         XtUmlEvent event = new XtUmlEvent(instance.getInstanceId(), eventId, null);
                         EventDispatcher.getInstance().enqueue(event);
                         System.out.println("Event fired.");
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
            }
        }
    }

    private static void manageRate() {
        while (true) {
            System.out.println("\n--- Manager: Rate ---");
            System.out.println("1. Create New Instance");
            System.out.println("2. Select Existing Instance");
            System.out.println("3. Back");
            System.out.print("Choose: ");

            String input = scanner.nextLine();
            if (input.equals("3")) return;

            if (input.equals("1")) {
                createRate();
            } else if (input.equals("2")) {
                selectRate();
            } else {
                System.out.println("Invalid option.");
            }
        }
    }

    private static void createRate() {
        System.out.println("Creating Rate...");
        Rate instance = new Rate();
        System.out.print("Enter rate_id (id): ");
        try {
            String val = scanner.nextLine();
            // ID handling: input string is converted to UUID bytes or hash?
            // For simplicity, we'll try to parse or generate random if empty
            if (val.trim().isEmpty()) {
                instance.setRate_id(UUID.randomUUID());
            } else {
                try {
                    instance.setRate_id(UUID.fromString(val));
                } catch (IllegalArgumentException e) {
                    // Fallback for non-standard UUID strings
                    instance.setRate_id(UUID.nameUUIDFromBytes(val.getBytes()));
                }
            }
        } catch (Exception e) {
            System.out.println("Error setting rate_id: " + e.getMessage());
        }
        System.out.print("Enter price_per_hour (decimal): ");
        try {
            String val = scanner.nextLine();
            instance.setPrice_per_hour(Double.parseDouble(val));
        } catch (Exception e) {
            System.out.println("Error setting price_per_hour: " + e.getMessage());
        }
        System.out.println("Created instance with ID: " + instance.getInstanceId());
    }

    private static void selectRate() {
        List<Rate> instances = ObjectBroker.getInstance().selectAll(Rate.class);
        if (instances.isEmpty()) {
            System.out.println("No instances found.");
            return;
        }

        for (int i = 0; i < instances.size(); i++) {
            System.out.println((i + 1) + ". " + instances.get(i).getInstanceId() + " [" + instances.get(i).getClass().getSimpleName() + "]");
        }
        System.out.print("Select instance: ");
        try {
            int idx = Integer.parseInt(scanner.nextLine()) - 1;
            if (idx >= 0 && idx < instances.size()) {
                interactWithRate(instances.get(idx));
            } else {
                System.out.println("Invalid selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    private static void interactWithRate(Rate instance) {
        while (true) {
            System.out.println("\n--- Interact with Rate : " + instance.getInstanceId() + " ---");
            System.out.println("[Operations]");
            System.out.println("1. calculate_tax");
            System.out.println("2. Back");
            System.out.print("Choose action: ");
            try {
                int action = Integer.parseInt(scanner.nextLine());
                if (action == 2) return;
                else if (action >= 1 && action <= 1) {
                    // Execute Operation
                    switch(action) {
                        case 1: 
                            System.out.println("Result: " + instance.calculate_tax());
                            break;
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
            }
        }
    }

    private static void manageRental() {
        while (true) {
            System.out.println("\n--- Manager: Rental ---");
            System.out.println("1. Create New Instance");
            System.out.println("2. Select Existing Instance");
            System.out.println("3. Back");
            System.out.print("Choose: ");

            String input = scanner.nextLine();
            if (input.equals("3")) return;

            if (input.equals("1")) {
                createRental();
            } else if (input.equals("2")) {
                selectRental();
            } else {
                System.out.println("Invalid option.");
            }
        }
    }

    private static void createRental() {
        System.out.println("Creating Rental...");
        Rental instance = new Rental();
        System.out.print("Enter rental_id (id): ");
        try {
            String val = scanner.nextLine();
            // ID handling: input string is converted to UUID bytes or hash?
            // For simplicity, we'll try to parse or generate random if empty
            if (val.trim().isEmpty()) {
                instance.setRental_id(UUID.randomUUID());
            } else {
                try {
                    instance.setRental_id(UUID.fromString(val));
                } catch (IllegalArgumentException e) {
                    // Fallback for non-standard UUID strings
                    instance.setRental_id(UUID.nameUUIDFromBytes(val.getBytes()));
                }
            }
        } catch (Exception e) {
            System.out.println("Error setting rental_id: " + e.getMessage());
        }
        System.out.print("Enter start_time (datetime): ");
        try {
            String val = scanner.nextLine();
            try {
                instance.setStart_time(java.time.LocalDateTime.parse(val));
            } catch (java.time.format.DateTimeParseException e) {
                System.out.println("Invalid date format. Use ISO-8601 (e.g. 2023-10-05T10:00:00)");
            }
        } catch (Exception e) {
            System.out.println("Error setting start_time: " + e.getMessage());
        }
        System.out.print("Enter end_time (datetime): ");
        try {
            String val = scanner.nextLine();
            try {
                instance.setEnd_time(java.time.LocalDateTime.parse(val));
            } catch (java.time.format.DateTimeParseException e) {
                System.out.println("Invalid date format. Use ISO-8601 (e.g. 2023-10-05T10:00:00)");
            }
        } catch (Exception e) {
            System.out.println("Error setting end_time: " + e.getMessage());
        }
        System.out.print("Enter status (state): ");
        try {
            String val = scanner.nextLine();
            instance.setStatus(val);
        } catch (Exception e) {
            System.out.println("Error setting status: " + e.getMessage());
        }
        System.out.println("Created instance with ID: " + instance.getInstanceId());
    }

    private static void selectRental() {
        List<Rental> instances = ObjectBroker.getInstance().selectAll(Rental.class);
        if (instances.isEmpty()) {
            System.out.println("No instances found.");
            return;
        }

        for (int i = 0; i < instances.size(); i++) {
            System.out.println((i + 1) + ". " + instances.get(i).getInstanceId() + " [" + instances.get(i).getClass().getSimpleName() + "]");
        }
        System.out.print("Select instance: ");
        try {
            int idx = Integer.parseInt(scanner.nextLine()) - 1;
            if (idx >= 0 && idx < instances.size()) {
                interactWithRental(instances.get(idx));
            } else {
                System.out.println("Invalid selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    private static void interactWithRental(Rental instance) {
        while (true) {
            System.out.println("\n--- Interact with Rental : " + instance.getInstanceId() + " ---");
            System.out.println("[Events]");
            System.out.println("1. pay_confirmed");
            System.out.println("2. cancel_request");
            System.out.println("3. end_time_passed");
            System.out.println("4. unit_returned");
            System.out.println("5. setExpired");
            System.out.println("6. setCompleted");
            System.out.println("7. setCanceled");
            System.out.println("8. Back");
            System.out.print("Choose action: ");
            try {
                int action = Integer.parseInt(scanner.nextLine());
                if (action == 8) return;
                if (action >= 1 && action <= 7) {
                    // Fire Event
                    int eventId = 0;
                    switch(action) {
                        case 1: eventId = Rental.EVENT_PAY_CONFIRMED; break;
                        case 2: eventId = Rental.EVENT_CANCEL_REQUEST; break;
                        case 3: eventId = Rental.EVENT_END_TIME_PASSED; break;
                        case 4: eventId = Rental.EVENT_UNIT_RETURNED; break;
                        case 5: eventId = Rental.EVENT_SETEXPIRED; break;
                        case 6: eventId = Rental.EVENT_SETCOMPLETED; break;
                        case 7: eventId = Rental.EVENT_SETCANCELED; break;
                    }
                    if (eventId != 0) {
                         XtUmlEvent event = new XtUmlEvent(instance.getInstanceId(), eventId, null);
                         EventDispatcher.getInstance().enqueue(event);
                         System.out.println("Event fired.");
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
            }
        }
    }

}
