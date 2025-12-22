package com.xtuml.generated;

import com.xtuml.runtime.BaseModel;
import com.xtuml.runtime.EventDispatcher;
import com.xtuml.runtime.ObjectBroker;
import com.xtuml.runtime.XtUmlEvent;
import com.xtuml.runtime.RelationshipManager;
import java.util.Scanner;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

public class InteractiveApp {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== xtUML Interactive Console ===");
        while (true) {
            System.out.println("\n--- Rental Shop Cashier ---");
            System.out.println("1. Sewa Baru (New Rental)");
            System.out.println("2. Pengembalian (Return Unit)");
            System.out.println("3. Cek Stok (Check Availability)");
            System.out.println("4. Admin Mode (Generic View)");
            System.out.println("5. Exit");
            System.out.print("Select an option: ");

            String input = scanner.nextLine();
            if (input.equals("5")) {
                System.out.println("Exiting...");
                break;
            }

            if (input.equals("1")) {
                manageNewRental();
            } else if (input.equals("2")) {
                manageReturnUnit();
            } else if (input.equals("3")) {
                checkAvailability();
            } else if (input.equals("4")) {
                manageAdminMode();
            } else {
                System.out.println("Invalid option.");
            }
            
            EventDispatcher.getInstance().processQueue();
        }
    }

    private static void manageNewRental() {
        System.out.println("\n--- New Rental Transaction ---");
        System.out.print("Enter Customer Name: ");
        String name = scanner.nextLine();
        Customer cust = new Customer();
        cust.setCustomer_id(UUID.randomUUID());
        cust.setName(name);
        System.out.println("Customer '" + name + "' created.");

        System.out.println("Available Consoles:");
        List<PSConsole> allConsoles = ObjectBroker.getInstance().selectAll(PSConsole.class);
        List<PSConsole> availableConsoles = new ArrayList<>();
        for (PSConsole c : allConsoles) {
            if ("available".equalsIgnoreCase(c.getAvailability())) {
                availableConsoles.add(c);
            }
        }
        if (availableConsoles.isEmpty()) {
            System.out.println("No consoles available!");
            return;
        }
        for (int i = 0; i < availableConsoles.size(); i++) {
            System.out.println((i + 1) + ". " + availableConsoles.get(i).getModel());
        }
        System.out.print("Select Console: ");
        int consoleIdx = -1;
        try {
            consoleIdx = Integer.parseInt(scanner.nextLine()) - 1;
        } catch(Exception e) {}
        if (consoleIdx < 0 || consoleIdx >= availableConsoles.size()) {
            System.out.println("Invalid selection.");
            return;
        }
        PSConsole console = availableConsoles.get(consoleIdx);

        System.out.print("Enter Duration (hours): ");
        int hours = 0;
        try {
            hours = Integer.parseInt(scanner.nextLine());
        } catch(Exception e) {}

        Rental r = new Rental();
        r.setRental_id(UUID.randomUUID());
        r.setStart_time(java.time.LocalDateTime.now());
        r.setEnd_time(java.time.LocalDateTime.now().plusHours(hours));
        r.setStatus("pending");
        RelationshipManager.getInstance().relate(r, cust, "R1", null);
        RelationshipManager.getInstance().relate(r, console, "R2", null);
        XtUmlEvent event = new XtUmlEvent(console.getInstanceId(), PSConsole.EVENT_SETRENTED, null);
        EventDispatcher.getInstance().enqueue(event);
        System.out.println("Success! " + cust.getName() + " is renting " + console.getModel() + " for " + hours + " hours.");
    }

    private static void manageReturnUnit() {
        System.out.println("\n--- Return Unit ---");
        List<Rental> rentals = ObjectBroker.getInstance().selectAll(Rental.class);
        List<Rental> activeRentals = new ArrayList<>();
        for (Rental r : rentals) {
            if (!"completed".equalsIgnoreCase(r.getStatus()) && !"canceled".equalsIgnoreCase(r.getStatus())) {
                activeRentals.add(r);
            }
        }
        if (activeRentals.isEmpty()) {
            System.out.println("No active rentals.");
            return;
        }
        for (int i = 0; i < activeRentals.size(); i++) {
            Rental r = activeRentals.get(i);
            // Try to find related customer/console name for display if possible, but for now just ID/Status
            System.out.println((i + 1) + ". Rental [" + r.getStatus() + "] - " + r.getInstanceId().toString().substring(0,8));
        }
        System.out.print("Select Rental to Return: ");
        int idx = -1;
        try {
            idx = Integer.parseInt(scanner.nextLine()) - 1;
        } catch(Exception e) {}
        if (idx < 0 || idx >= activeRentals.size()) {
            System.out.println("Invalid selection.");
            return;
        }
        Rental target = activeRentals.get(idx);
        XtUmlEvent event = new XtUmlEvent(target.getInstanceId(), Rental.EVENT_UNIT_RETURNED, null);
        EventDispatcher.getInstance().enqueue(event);
        System.out.println("Unit return processed (Event fired).");
    }

    private static void checkAvailability() {
        System.out.println("\n--- Console Availability ---");
        List<PSConsole> all = ObjectBroker.getInstance().selectAll(PSConsole.class);
        if (all.isEmpty()) System.out.println("No consoles in system.");
        for (PSConsole c : all) {
            System.out.println("- " + c.getModel() + ": " + c.getAvailability());
        }
    }

    private static void manageAdminMode() {
        while (true) {
            System.out.println("\n--- Admin Mode (Generic) ---");
            System.out.println("1. Customer");
            System.out.println("2. PSConsole");
            System.out.println("3. Rate");
            System.out.println("4. Rental");
            System.out.println("5. Back");
            System.out.print("Select option: ");
            String inp = scanner.nextLine();
            if (inp.equals("5")) return;
            if (inp.equals("1")) manageCustomer();
            else if (inp.equals("2")) managePSConsole();
            else if (inp.equals("3")) manageRate();
            else if (inp.equals("4")) manageRental();
            else System.out.println("Invalid.");
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
        String createdLabel = "";
        instance.setCustomer_id(UUID.randomUUID());
        System.out.print("Enter name (string): ");
        try {
            String val = scanner.nextLine();
            instance.setName(val);
            createdLabel = val;
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
        System.out.println("Success! Customer '" + createdLabel + "' has been registered.");
    }

    private static void selectCustomer() {
        List<Customer> instances = ObjectBroker.getInstance().selectAll(Customer.class);
        if (instances.isEmpty()) {
            System.out.println("No instances found.");
            return;
        }

        for (int i = 0; i < instances.size(); i++) {
            System.out.println((i + 1) + ". " + instances.get(i).getName() + " (ID: " + instances.get(i).getInstanceId().toString().substring(0,8) + "...)");
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
            System.out.println("\n--- Action for Customer '" + instance.getName() + "' ---");
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
        String createdLabel = "";
        instance.setConsole_id(UUID.randomUUID());
        System.out.print("Enter model (string): ");
        try {
            String val = scanner.nextLine();
            instance.setModel(val);
            createdLabel = val;
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
        instance.setRate_id(UUID.randomUUID());
        System.out.println("Success! PSConsole '" + createdLabel + "' has been registered.");
    }

    private static void selectPSConsole() {
        List<PSConsole> instances = ObjectBroker.getInstance().selectAll(PSConsole.class);
        if (instances.isEmpty()) {
            System.out.println("No instances found.");
            return;
        }

        for (int i = 0; i < instances.size(); i++) {
            System.out.println((i + 1) + ". " + instances.get(i).getModel() + " (ID: " + instances.get(i).getInstanceId().toString().substring(0,8) + "...)");
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
            System.out.println("\n--- Action for PSConsole '" + instance.getModel() + "' ---");
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
        instance.setRate_id(UUID.randomUUID());
        System.out.print("Enter price_per_hour (decimal): ");
        try {
            String val = scanner.nextLine();
            instance.setPrice_per_hour(Double.parseDouble(val));
        } catch (Exception e) {
            System.out.println("Error setting price_per_hour: " + e.getMessage());
        }
        System.out.println("Success! Rate instance created.");
    }

    private static void selectRate() {
        List<Rate> instances = ObjectBroker.getInstance().selectAll(Rate.class);
        if (instances.isEmpty()) {
            System.out.println("No instances found.");
            return;
        }

        for (int i = 0; i < instances.size(); i++) {
            System.out.println((i + 1) + ". " + instances.get(i).getInstanceId() + " (ID: " + instances.get(i).getInstanceId().toString().substring(0,8) + "...)");
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
            System.out.println("\n--- Action for Rate '" + instance.getInstanceId() + "' ---");
            System.out.println("[Operations]");
            System.out.println("1. calculate_tax");
            System.out.println("2. Back");
            System.out.print("Choose action: ");
            try {
                int action = Integer.parseInt(scanner.nextLine());
                if (action == 2) return;
                else if (action >= 1 && action <= 1) {
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
        instance.setRental_id(UUID.randomUUID());
        instance.setCustomer_id(UUID.randomUUID());
        instance.setConsole_id(UUID.randomUUID());
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
        System.out.println("Success! Rental instance created.");
    }

    private static void selectRental() {
        List<Rental> instances = ObjectBroker.getInstance().selectAll(Rental.class);
        if (instances.isEmpty()) {
            System.out.println("No instances found.");
            return;
        }

        for (int i = 0; i < instances.size(); i++) {
            System.out.println((i + 1) + ". " + instances.get(i).getInstanceId() + " (ID: " + instances.get(i).getInstanceId().toString().substring(0,8) + "...)");
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
            System.out.println("\n--- Action for Rental '" + instance.getInstanceId() + "' ---");
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
