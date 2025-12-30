package com.xtuml.compiler;

import com.xtuml.compiler.model.Attribute;
import com.xtuml.compiler.model.MetaModel;
import com.xtuml.compiler.model.ModelClass;
import com.xtuml.compiler.model.Operation;
import com.xtuml.compiler.model.State;
import com.xtuml.compiler.model.XtUmlModelElement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ConsoleAppGenerator {

    public String generateInteractiveApp(MetaModel model) {
        StringBuilder sb = new StringBuilder();

        List<ModelClass> classes = new ArrayList<>();
        if (model.getModel() != null) {
            for (XtUmlModelElement element : model.getModel()) {
                if (element instanceof ModelClass) {
                    classes.add((ModelClass) element);
                }
            }
        }

        boolean isRentalApp = isRentalApp(classes);

        // Package and Imports
        sb.append("package com.xtuml.generated;\n\n");
        sb.append("import com.xtuml.runtime.BaseModel;\n");
        sb.append("import com.xtuml.runtime.EventDispatcher;\n");
        sb.append("import com.xtuml.runtime.ObjectBroker;\n");
        sb.append("import com.xtuml.runtime.XtUmlEvent;\n");
        sb.append("import com.xtuml.runtime.RelationshipManager;\n"); // Added for linking
        sb.append("import java.util.Scanner;\n");
        sb.append("import java.util.UUID;\n");
        sb.append("import java.util.List;\n");
        sb.append("import java.util.ArrayList;\n\n");

        sb.append("public class InteractiveApp {\n\n");
        sb.append("    private static final Scanner scanner = new Scanner(System.in);\n\n");

        // Main Method
        sb.append("    public static void main(String[] args) {\n");
        sb.append("        System.out.println(\"=== xtUML Interactive Console ===\");\n");

        if (isRentalApp) {
            generateRentalMainMenu(sb);
        } else {
            generateGenericMainMenu(sb, classes);
        }

        sb.append("    }\n\n");

        // Rental Specific Methods
        if (isRentalApp) {
            generateRentalMethods(sb);
        }

        // Generic Management Methods (Keep them for Admin Mode or generic apps)
        for (ModelClass clazz : classes) {
            generateClassManager(sb, clazz);
        }

        sb.append("}\n");
        return sb.toString();
    }

    private boolean isRentalApp(List<ModelClass> classes) {
        boolean hasCustomer = false;
        boolean hasConsole = false;
        boolean hasRental = false;
        for (ModelClass c : classes) {
            if (c.getClassName().equals("Customer"))
                hasCustomer = true;
            if (c.getClassName().equals("PSConsole"))
                hasConsole = true;
            if (c.getClassName().equals("Rental"))
                hasRental = true;
        }
        return hasCustomer && hasConsole && hasRental;
    }

    private void generateRentalMainMenu(StringBuilder sb) {
        sb.append("        while (true) {\n");
        sb.append("            System.out.println(\"\\n--- Rental Shop Cashier ---\");\n");
        sb.append("            System.out.println(\"1. Sewa Baru (New Rental)\");\n");
        sb.append("            System.out.println(\"2. Pengembalian (Return Unit)\");\n");
        sb.append("            System.out.println(\"3. Cek Stok (Check Availability)\");\n");
        sb.append("            System.out.println(\"4. Admin Mode (Generic View)\");\n");
        sb.append("            System.out.println(\"5. Exit\");\n");
        sb.append("            System.out.print(\"Select an option: \");\n\n");

        sb.append("            String input = scanner.nextLine();\n");
        sb.append("            if (input.equals(\"5\")) {\n");
        sb.append("                System.out.println(\"Exiting...\");\n");
        sb.append("                break;\n");
        sb.append("            }\n\n");

        sb.append("            if (input.equals(\"1\")) {\n");
        sb.append("                manageNewRental();\n");
        sb.append("            } else if (input.equals(\"2\")) {\n");
        sb.append("                manageReturnUnit();\n");
        sb.append("            } else if (input.equals(\"3\")) {\n");
        sb.append("                checkAvailability();\n");
        sb.append("            } else if (input.equals(\"4\")) {\n");
        sb.append("                manageAdminMode();\n");
        sb.append("            } else {\n");
        sb.append("                System.out.println(\"Invalid option.\");\n");
        sb.append("            }\n");
        sb.append("            \n");
        sb.append("            EventDispatcher.getInstance().processQueue();\n");
        sb.append("        }\n");
    }

    private void generateRentalMethods(StringBuilder sb) {
        // 1. Manage New Rental
        sb.append("    private static void manageNewRental() {\n");
        sb.append("        System.out.println(\"\\n--- New Rental Transaction ---\");\n");

        // Step A: Customer Name
        sb.append("        System.out.print(\"Enter Customer Name: \");\n");
        sb.append("        String name = scanner.nextLine();\n");
        sb.append("        Customer cust = new Customer();\n");
        sb.append("        cust.setCustomer_id(UUID.randomUUID());\n");
        sb.append("        cust.setName(name);\n");
        sb.append("        System.out.println(\"Customer '\" + name + \"' created.\");\n\n");

        // Step B: Choose Console
        sb.append("        System.out.println(\"Available Consoles:\");\n");
        sb.append("        List<PSConsole> allConsoles = ObjectBroker.getInstance().selectAll(PSConsole.class);\n");
        sb.append("        List<PSConsole> availableConsoles = new ArrayList<>();\n");
        sb.append("        for (PSConsole c : allConsoles) {\n");
        sb.append("            if (\"available\".equalsIgnoreCase(c.getAvailability())) {\n");
        sb.append("                availableConsoles.add(c);\n");
        sb.append("            }\n");
        sb.append("        }\n");

        sb.append("        if (availableConsoles.isEmpty()) {\n");
        sb.append("            System.out.println(\"No consoles available!\");\n");
        sb.append("            return;\n");
        sb.append("        }\n");

        sb.append("        for (int i = 0; i < availableConsoles.size(); i++) {\n");
        sb.append("            System.out.println((i + 1) + \". \" + availableConsoles.get(i).getModel());\n");
        sb.append("        }\n");
        sb.append("        System.out.print(\"Select Console: \");\n");
        sb.append("        int consoleIdx = -1;\n");
        sb.append("        try {\n");
        sb.append("            consoleIdx = Integer.parseInt(scanner.nextLine()) - 1;\n");
        sb.append("        } catch(Exception e) {}\n");

        sb.append("        if (consoleIdx < 0 || consoleIdx >= availableConsoles.size()) {\n");
        sb.append("            System.out.println(\"Invalid selection.\");\n");
        sb.append("            return;\n");
        sb.append("        }\n");
        sb.append("        PSConsole console = availableConsoles.get(consoleIdx);\n\n");

        // Step C: Duration
        sb.append("        System.out.print(\"Enter Duration (hours): \");\n");
        sb.append("        int hours = 0;\n");
        sb.append("        try {\n");
        sb.append("            hours = Integer.parseInt(scanner.nextLine());\n");
        sb.append("        } catch(Exception e) {}\n\n");

        // Step D: Execute
        sb.append("        Rental r = new Rental();\n");
        sb.append("        r.setRental_id(UUID.randomUUID());\n");
        sb.append("        r.setStart_time(java.time.LocalDateTime.now());\n");
        sb.append("        r.setEnd_time(java.time.LocalDateTime.now().plusHours(hours));\n");
        sb.append("        r.setStatus(\"pending\");\n");

        // Relate
        sb.append("        RelationshipManager.getInstance().relate(r, cust, \"R1\", null);\n");
        sb.append("        RelationshipManager.getInstance().relate(r, console, \"R2\", null);\n");

        // Fire Event
        sb.append(
                "        XtUmlEvent event = new XtUmlEvent(console.getInstanceId(), PSConsole.EVENT_SETRENTED, null);\n");
        sb.append("        EventDispatcher.getInstance().enqueue(event);\n");

        sb.append(
                "        System.out.println(\"Success! \" + cust.getName() + \" is renting \" + console.getModel() + \" for \" + hours + \" hours.\");\n");
        sb.append("    }\n\n");

        // 2. Manage Return
        sb.append("    private static void manageReturnUnit() {\n");
        sb.append("        System.out.println(\"\\n--- Return Unit ---\");\n");
        sb.append("        List<Rental> rentals = ObjectBroker.getInstance().selectAll(Rental.class);\n");
        sb.append("        List<Rental> activeRentals = new ArrayList<>();\n");
        sb.append("        for (Rental r : rentals) {\n");
        sb.append(
                "            if (!\"completed\".equalsIgnoreCase(r.getStatus()) && !\"canceled\".equalsIgnoreCase(r.getStatus())) {\n");
        sb.append("                activeRentals.add(r);\n");
        sb.append("            }\n");
        sb.append("        }\n");

        sb.append("        if (activeRentals.isEmpty()) {\n");
        sb.append("            System.out.println(\"No active rentals.\");\n");
        sb.append("            return;\n");
        sb.append("        }\n");

        sb.append("        for (int i = 0; i < activeRentals.size(); i++) {\n");
        sb.append("            Rental r = activeRentals.get(i);\n");
        sb.append(
                "            // Try to find related customer/console name for display if possible, but for now just ID/Status\n");
        sb.append(
                "            System.out.println((i + 1) + \". Rental [\" + r.getStatus() + \"] - \" + r.getInstanceId().toString().substring(0,8));\n");
        sb.append("        }\n");
        sb.append("        System.out.print(\"Select Rental to Return: \");\n");
        sb.append("        int idx = -1;\n");
        sb.append("        try {\n");
        sb.append("            idx = Integer.parseInt(scanner.nextLine()) - 1;\n");
        sb.append("        } catch(Exception e) {}\n");

        sb.append("        if (idx < 0 || idx >= activeRentals.size()) {\n");
        sb.append("            System.out.println(\"Invalid selection.\");\n");
        sb.append("            return;\n");
        sb.append("        }\n");
        sb.append("        Rental target = activeRentals.get(idx);\n");

        // Fire Event unit_returned
        sb.append(
                "        XtUmlEvent event = new XtUmlEvent(target.getInstanceId(), Rental.EVENT_UNIT_RETURNED, null);\n");
        sb.append("        EventDispatcher.getInstance().enqueue(event);\n");
        sb.append("        System.out.println(\"Unit return processed (Event fired).\");\n");
        sb.append("    }\n\n");

        // 3. Check Availability
        sb.append("    private static void checkAvailability() {\n");
        sb.append("        System.out.println(\"\\n--- Console Availability ---\");\n");
        sb.append("        List<PSConsole> all = ObjectBroker.getInstance().selectAll(PSConsole.class);\n");
        sb.append("        if (all.isEmpty()) System.out.println(\"No consoles in system.\");\n");
        sb.append("        for (PSConsole c : all) {\n");
        sb.append("            System.out.println(\"- \" + c.getModel() + \": \" + c.getAvailability());\n");
        sb.append("        }\n");
        sb.append("    }\n\n");

        // 4. Admin Mode Bridge (Generic)
        sb.append("    private static void manageAdminMode() {\n");
        sb.append("        while (true) {\n");
        sb.append("            System.out.println(\"\\n--- Admin Mode (Generic) ---\");\n");
        // We can just call generateGenericMainMenu inside here? No, duplicate code.
        // Let's copy the generic menu logic here briefly or extract it?
        // Let's just create a quick generic menu here\n");
        sb.append("            System.out.println(\"1. Customer\");\n");
        sb.append("            System.out.println(\"2. PSConsole\");\n");
        sb.append("            System.out.println(\"3. Rate\");\n");
        sb.append("            System.out.println(\"4. Rental\");\n");
        sb.append("            System.out.println(\"5. Back\");\n");
        sb.append("            System.out.print(\"Select option: \");\n");
        sb.append("            String inp = scanner.nextLine();\n");
        sb.append("            if (inp.equals(\"5\")) return;\n");
        sb.append("            if (inp.equals(\"1\")) manageCustomer();\n");
        sb.append("            else if (inp.equals(\"2\")) managePSConsole();\n");
        sb.append("            else if (inp.equals(\"3\")) manageRate();\n");
        sb.append("            else if (inp.equals(\"4\")) manageRental();\n");
        sb.append("            else System.out.println(\"Invalid.\");\n");
        sb.append("        }\n");
        sb.append("    }\n\n");
    }

    private void generateGenericMainMenu(StringBuilder sb, List<ModelClass> classes) {
        sb.append("        while (true) {\n");
        sb.append("            System.out.println(\"\\n--- Main Menu ---\");\n");

        for (int i = 0; i < classes.size(); i++) {
            sb.append("            System.out.println(\"").append(i + 1).append(". ")
                    .append(classes.get(i).getClassName()).append("\");\n");
        }
        sb.append("            System.out.println(\"").append(classes.size() + 1).append(". Exit\");\n");
        sb.append("            System.out.print(\"Select an option: \");\n\n");

        sb.append("            String input = scanner.nextLine();\n");
        sb.append("            try {\n");
        sb.append("                int choice = Integer.parseInt(input);\n");
        sb.append("                if (choice == ").append(classes.size() + 1).append(") {\n");
        sb.append("                    System.out.println(\"Exiting...\");\n");
        sb.append("                    break;\n");
        sb.append("                }\n\n");

        // Switch for Class Selection
        sb.append("                switch (choice) {\n");
        for (int i = 0; i < classes.size(); i++) {
            ModelClass clazz = classes.get(i);
            sb.append("                    case ").append(i + 1).append(":\n");
            sb.append("                        manage").append(clazz.getClassName()).append("();\n");
            sb.append("                        break;\n");
        }
        sb.append("                    default:\n");
        sb.append("                        System.out.println(\"Invalid option.\");\n");
        sb.append("                }\n");

        sb.append("            } catch (NumberFormatException e) {\n");
        sb.append("                System.out.println(\"Invalid input. Please enter a number.\");\n");
        sb.append("            }\n");
        sb.append("            \n");
        sb.append("            // Process any pending events after user interaction\n");
        sb.append("            EventDispatcher.getInstance().processQueue();\n");
        sb.append("        }\n");
    }

    private void generateClassManager(StringBuilder sb, ModelClass clazz) {
        String className = clazz.getClassName();

        // Identify a "readable" attribute for this class
        Attribute readableAttr = findReadableAttribute(clazz);
        String readableGetter = (readableAttr != null)
                ? "get" + capitalize(readableAttr.getAttributeName()) + "()" // e.g., getName()
                : "getInstanceId()";

        sb.append("    private static void manage").append(className).append("() {\n");
        sb.append("        while (true) {\n");
        sb.append("            System.out.println(\"\\n--- Manager: ").append(className).append(" ---\");\n");
        sb.append("            System.out.println(\"1. Create New Instance\");\n");
        sb.append("            System.out.println(\"2. Select Existing Instance\");\n");
        sb.append("            System.out.println(\"3. Back\");\n");
        sb.append("            System.out.print(\"Choose: \");\n\n");

        sb.append("            String input = scanner.nextLine();\n");
        sb.append("            if (input.equals(\"3\")) return;\n\n");

        sb.append("            if (input.equals(\"1\")) {\n");
        sb.append("                create").append(className).append("();\n");
        sb.append("            } else if (input.equals(\"2\")) {\n");
        sb.append("                select").append(className).append("();\n");
        sb.append("            } else {\n");
        sb.append("                System.out.println(\"Invalid option.\");\n");
        sb.append("            }\n");
        sb.append("        }\n");
        sb.append("    }\n\n");

        // Creator Method
        sb.append("    private static void create").append(className).append("() {\n");
        sb.append("        System.out.println(\"Creating ").append(className).append("...\");\n");
        sb.append("        ").append(className).append(" instance = new ").append(className).append("();\n");

        // Helper variable to store the name/label for final message
        if (readableAttr != null) {
            sb.append("        String createdLabel = \"\";\n");
        }

        if (clazz.getAttributes() != null) {
            for (Attribute attr : clazz.getAttributes()) {
                String attrName = attr.getAttributeName();
                String setterName = "set" + capitalize(attrName);
                String type = attr.getDataType();
                boolean isId = isIdAttribute(attrName);

                if (isId) {
                    // Auto-generate ID silently
                    sb.append("        instance.").append(setterName).append("(UUID.randomUUID());\n");
                } else if ("naming_attribute".equals(attr.getAttributeType())
                        || "descriptive_attribute".equals(attr.getAttributeType())) {

                    sb.append("        System.out.print(\"Enter ").append(attrName).append(" (").append(type)
                            .append("): \");\n");
                    sb.append("        try {\n");
                    sb.append("            String val = scanner.nextLine();\n");

                    if ("integer".equalsIgnoreCase(type)) {
                        sb.append("            instance.").append(setterName).append("(Integer.parseInt(val));\n");
                    } else if ("real".equalsIgnoreCase(type) || "decimal".equalsIgnoreCase(type)) {
                        sb.append("            instance.").append(setterName).append("(Double.parseDouble(val));\n");
                    } else if ("boolean".equalsIgnoreCase(type)) {
                        sb.append("            instance.").append(setterName).append("(Boolean.parseBoolean(val));\n");
                    } else if ("datetime".equalsIgnoreCase(type)) {
                        sb.append("            try {\n");
                        sb.append("                instance.").append(setterName)
                                .append("(java.time.LocalDateTime.parse(val));\n");
                        sb.append("            } catch (java.time.format.DateTimeParseException e) {\n");
                        sb.append(
                                "                System.out.println(\"Invalid date format. Use ISO-8601 (e.g. 2023-10-05T10:00:00)\");\n");
                        sb.append("            }\n");
                    } else {
                        // String or others
                        sb.append("            instance.").append(setterName).append("(val);\n");
                        // If this is the readable attribute, capture it
                        if (attr == readableAttr) {
                            sb.append("            createdLabel = val;\n");
                        }
                    }
                    sb.append("        } catch (Exception e) {\n");
                    sb.append("            System.out.println(\"Error setting ").append(attrName)
                            .append(": \" + e.getMessage());\n");
                    sb.append("        }\n");
                }
            }
        }

        // Friendly success message
        if (readableAttr != null) {
            sb.append("        System.out.println(\"Success! ").append(className)
                    .append(" '\" + createdLabel + \"' has been registered.\");\n");
        } else {
            sb.append("        System.out.println(\"Success! ").append(className).append(" instance created.\");\n");
        }
        sb.append("    }\n\n");

        // Selector Method
        sb.append("    private static void select").append(className).append("() {\n");
        sb.append("        List<").append(className).append("> instances = ObjectBroker.getInstance().selectAll(")
                .append(className).append(".class);\n");
        sb.append("        if (instances.isEmpty()) {\n");
        sb.append("            System.out.println(\"No instances found.\");\n");
        sb.append("            return;\n");
        sb.append("        }\n\n");

        sb.append("        for (int i = 0; i < instances.size(); i++) {\n");
        // Smart Selection Logic: Print readable name
        sb.append(
                "            System.out.println((i + 1) + \". \" + instances.get(i).").append(readableGetter)
                .append(" + \" (ID: \" + instances.get(i).getInstanceId().toString().substring(0,8) + \"...)\");\n");

        sb.append("        }\n");
        sb.append("        System.out.print(\"Select instance: \");\n");

        sb.append("        try {\n");
        sb.append("            int idx = Integer.parseInt(scanner.nextLine()) - 1;\n");
        sb.append("            if (idx >= 0 && idx < instances.size()) {\n");
        sb.append("                interactWith").append(className).append("(instances.get(idx));\n");
        sb.append("            } else {\n");
        sb.append("                System.out.println(\"Invalid selection.\");\n");
        sb.append("            }\n");
        sb.append("        } catch (NumberFormatException e) {\n");
        sb.append("            System.out.println(\"Invalid input.\");\n");
        sb.append("        }\n");
        sb.append("    }\n\n");

        // Interaction Method (Events & Operations)
        sb.append("    private static void interactWith").append(className).append("(").append(className)
                .append(" instance) {\n");
        sb.append("        while (true) {\n");
        // Use readable name in header
        sb.append("            System.out.println(\"\\n--- Action for ").append(className)
                .append(" '\" + instance.").append(readableGetter).append(" + \"' ---\");\n");

        // List Events
        int menuIdx = 1;
        List<String> eventList = new ArrayList<>();
        if (clazz.getStates() != null) {
            sb.append("            System.out.println(\"[Events]\");\n");
            List<String> uniqueEvents = new ArrayList<>();
            for (State s : clazz.getStates()) {
                if (s.getStateEvents() != null) {
                    for (String evt : s.getStateEvents()) {
                        if (!uniqueEvents.contains(evt)) {
                            uniqueEvents.add(evt);
                            sb.append("            System.out.println(\"").append(menuIdx).append(". ").append(evt)
                                    .append("\");\n");
                            eventList.add(evt);
                            menuIdx++;
                        }
                    }
                }
            }
        }

        // List Operations
        List<Operation> opList = new ArrayList<>();
        if (clazz.getOperations() != null) {
            sb.append("            System.out.println(\"[Operations]\");\n");
            for (Operation op : clazz.getOperations()) {
                sb.append("            System.out.println(\"").append(menuIdx).append(". ").append(op.getName())
                        .append("\");\n");
                opList.add(op);
                menuIdx++;
            }
        }

        sb.append("            System.out.println(\"").append(menuIdx).append(". Back\");\n");
        sb.append("            System.out.print(\"Choose action: \");\n");

        sb.append("            try {\n");
        sb.append("                int action = Integer.parseInt(scanner.nextLine());\n");
        sb.append("                if (action == ").append(menuIdx).append(") return;\n");

        // Handle Event selection
        if (!eventList.isEmpty()) {
            sb.append("                if (action >= 1 && action <= ").append(eventList.size()).append(") {\n");
            sb.append("                    int eventId = 0;\n");
            sb.append("                    switch(action) {\n");
            for (int i = 0; i < eventList.size(); i++) {
                String evtName = eventList.get(i);
                sb.append("                        case ").append(i + 1).append(": eventId = ").append(className)
                        .append(".EVENT_").append(evtName.toUpperCase()).append("; break;\n");
            }
            sb.append("                    }\n");
            sb.append("                    if (eventId != 0) {\n");
            sb.append(
                    "                         XtUmlEvent event = new XtUmlEvent(instance.getInstanceId(), eventId, null);\n");
            sb.append("                         EventDispatcher.getInstance().enqueue(event);\n");
            sb.append("                         System.out.println(\"Event fired.\");\n");
            sb.append("                    }\n");
            sb.append("                }\n");
        }

        // Handle Operation selection
        if (!opList.isEmpty()) {
            int startOp = eventList.size() + 1;
            int endOp = eventList.size() + opList.size();
            sb.append("                else if (action >= ").append(startOp).append(" && action <= ").append(endOp)
                    .append(") {\n");
            sb.append("                    switch(action) {\n");
            for (int i = 0; i < opList.size(); i++) {
                Operation op = opList.get(i);
                int choice = startOp + i;
                sb.append("                        case ").append(choice).append(": \n");
                boolean isVoid = op.getReturnType() == null || op.getReturnType().equalsIgnoreCase("void");
                if (!isVoid) {
                    sb.append("                            System.out.println(\"Result: \" + ");
                } else {
                    sb.append("                            ");
                }
                sb.append("instance.").append(op.getName()).append("()");
                if (!isVoid)
                    sb.append(")");
                sb.append(";\n");
                sb.append("                            break;\n");
            }
            sb.append("                    }\n");
            sb.append("                }\n");
        }

        sb.append("            } catch (NumberFormatException e) {\n");
        sb.append("                System.out.println(\"Invalid input.\");\n");
        sb.append("            }\n");

        sb.append("        }\n");
        sb.append("    }\n\n");
    }

    // --- Helpers ---

    private String capitalize(String str) {
        if (str == null || str.isEmpty())
            return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private boolean isIdAttribute(String name) {
        if (name == null)
            return false;
        String n = name.toLowerCase();
        return n.equals("id") || n.endsWith("_id") || n.equals("key_letter");
    }

    private Attribute findReadableAttribute(ModelClass clazz) {
        List<Attribute> attrs = clazz.getAttributes();
        if (attrs == null)
            return null;

        // Priority 1: Exact matches
        for (Attribute a : attrs) {
            String n = a.getAttributeName().toLowerCase();
            if (n.equals("name") || n.equals("model") || n.equals("title") || n.equals("description")) {
                return a;
            }
        }

        // Priority 2: First String that is NOT an ID
        for (Attribute a : attrs) {
            if ("string".equalsIgnoreCase(a.getDataType()) && !isIdAttribute(a.getAttributeName())) {
                return a;
            }
        }

        return null;
    }
}
