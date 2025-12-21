package com.xtuml.compiler;

import com.xtuml.compiler.model.Attribute;
import com.xtuml.compiler.model.MetaModel;
import com.xtuml.compiler.model.ModelClass;
import com.xtuml.compiler.model.Operation;
import com.xtuml.compiler.model.State;
import com.xtuml.compiler.model.XtUmlModelElement;

import java.util.ArrayList;
import java.util.List;

public class ConsoleAppGenerator {

    public String generateInteractiveApp(MetaModel model) {
        StringBuilder sb = new StringBuilder();

        // Package and Imports
        sb.append("package com.xtuml.generated;\n\n");
        sb.append("import com.xtuml.runtime.BaseModel;\n");
        sb.append("import com.xtuml.runtime.EventDispatcher;\n");
        sb.append("import com.xtuml.runtime.ObjectBroker;\n");
        sb.append("import com.xtuml.runtime.XtUmlEvent;\n");
        sb.append("import java.util.Scanner;\n");
        sb.append("import java.util.UUID;\n");
        sb.append("import java.util.List;\n\n");

        sb.append("public class InteractiveApp {\n\n");
        sb.append("    private static final Scanner scanner = new Scanner(System.in);\n\n");

        // Main Method
        sb.append("    public static void main(String[] args) {\n");
        sb.append("        System.out.println(\"=== xtUML Interactive Console ===\");\n");
        sb.append("        // Run automatically generated system runner if exists\n");
        sb.append("        // RunSystem.main(args); // Optional: if we want to pre-populate data\n\n");

        sb.append("        while (true) {\n");
        sb.append("            System.out.println(\"\\n--- Main Menu ---\");\n");

        List<ModelClass> classes = new ArrayList<>();
        if (model.getModel() != null) {
            for (XtUmlModelElement element : model.getModel()) {
                if (element instanceof ModelClass) {
                    classes.add((ModelClass) element);
                }
            }
        }

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
        sb.append("    }\n\n");

        // Generate Management Methods for each Class
        for (ModelClass clazz : classes) {
            generateClassManager(sb, clazz);
        }

        sb.append("}\n");
        return sb.toString();
    }

    private void generateClassManager(StringBuilder sb, ModelClass clazz) {
        String className = clazz.getClassName();
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

        if (clazz.getAttributes() != null) {
            for (Attribute attr : clazz.getAttributes()) {
                // Skip ID attributes if they are auto-generated or handled internally,
                // but usually user might want to set names/values.
                // Assuming ID is string/UUID/int.
                // For simplicity, we ask for all descriptive attributes.
                if ("naming_attribute".equals(attr.getAttributeType())
                        || "descriptive_attribute".equals(attr.getAttributeType())) {
                    String attrName = attr.getAttributeName();
                    String setterName = "set" + attrName.substring(0, 1).toUpperCase() + attrName.substring(1);
                    String type = attr.getDataType();

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
                    } else if ("id".equalsIgnoreCase(type)) {
                        // IDs might be UUIDs in the generated code, need to handle that.
                        // The existing JavaSourceGenerator maps 'id' to UUID.
                        // But usually we can't just type a UUID.
                        // For this simple console, let's assume we can generate one or parse string?
                        // Ideally, if it's a naming attribute, maybe we parse it?
                        // BUT `JavaSourceGenerator` maps `id` to `UUID`.
                        // Let's generate a random UUID if empty or try to parse?
                        // Actually, let's look at `JavaSourceGenerator`:
                        // case "id": return "UUID";
                        // So the setter expects a UUID.
                        sb.append("            // ID handling: input string is converted to UUID bytes or hash?\n");
                        sb.append("            // For simplicity, we'll try to parse or generate random if empty\n");
                        sb.append("            if (val.trim().isEmpty()) {\n");
                        sb.append("                instance.").append(setterName).append("(UUID.randomUUID());\n");
                        sb.append("            } else {\n");
                        sb.append("                try {\n");
                        sb.append("                    instance.").append(setterName)
                                .append("(UUID.fromString(val));\n");
                        sb.append("                } catch (IllegalArgumentException e) {\n");
                        sb.append("                    // Fallback for non-standard UUID strings\n");
                        sb.append("                    instance.").append(setterName)
                                .append("(UUID.nameUUIDFromBytes(val.getBytes()));\n");
                        sb.append("                }\n");
                        sb.append("            }\n");

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
                    }
                    sb.append("        } catch (Exception e) {\n");
                    sb.append("            System.out.println(\"Error setting ").append(attrName)
                            .append(": \" + e.getMessage());\n");
                    sb.append("        }\n");
                }
            }
        }
        sb.append("        System.out.println(\"Created instance with ID: \" + instance.getInstanceId());\n");
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
        sb.append(
                "            System.out.println((i + 1) + \". \" + instances.get(i).getInstanceId() + \" [\" + instances.get(i).getClass().getSimpleName() + \"]\");\n");
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
        sb.append("            System.out.println(\"\\n--- Interact with ").append(className)
                .append(" : \" + instance.getInstanceId() + \" ---\");\n");

        // List Events
        int menuIdx = 1;
        List<String> eventList = new ArrayList<>();
        if (clazz.getStates() != null) {
            sb.append("            System.out.println(\"[Events]\");\n");
            // Collect unique events
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
        int currentLimit = 1;
        if (!eventList.isEmpty()) {
            sb.append("                if (action >= 1 && action <= ").append(eventList.size()).append(") {\n");
            sb.append("                    // Fire Event\n");
            sb.append("                    int eventId = 0;\n");

            // Map index to event ID
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
            currentLimit += eventList.size();
        }

        // Handle Operation selection
        if (!opList.isEmpty()) {
            // Logic to offset indices
            // e.g., if 3 events, operations start at 4
            int startOp = eventList.size() + 1;
            int endOp = eventList.size() + opList.size();

            sb.append("                else if (action >= ").append(startOp).append(" && action <= ").append(endOp)
                    .append(") {\n");
            sb.append("                    // Execute Operation\n");
            sb.append("                    switch(action) {\n");
            for (int i = 0; i < opList.size(); i++) {
                Operation op = opList.get(i);
                int choice = startOp + i;
                sb.append("                        case ").append(choice).append(": \n");
                // Check return type
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
}
