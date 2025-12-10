package com.xtuml.compiler;

import com.xtuml.compiler.model.Association;
import com.xtuml.compiler.model.AssociationEnd;
import com.xtuml.compiler.model.Attribute;
import com.xtuml.compiler.model.MetaModel;
import com.xtuml.compiler.model.ModelClass;
import com.xtuml.compiler.model.State;
import com.xtuml.compiler.model.XtUmlModelElement;
import java.util.HashMap;
import java.util.Map;

/**
 * Automatically generates a RunSystem.java file to simulate the current model.
 * Upgraded to include Auto-Linking and Auto-Event firing.
 */
public class SimulationGenerator {

    public String generateRunSystem(MetaModel model) {
        StringBuilder sb = new StringBuilder();
        Map<String, String> createdVars = new HashMap<>(); // ClassName -> VarName

        // Imports
        sb.append("import com.xtuml.generated.*;\n");
        sb.append("import com.xtuml.runtime.*;\n");
        sb.append("import java.util.UUID;\n");
        sb.append("import java.time.LocalDateTime;\n\n");

        // Class Definition
        sb.append("public class RunSystem {\n\n");
        sb.append("    public static void main(String[] args) {\n");
        sb.append("        System.out.println(\"==========================================\");\n");
        sb.append("        System.out.println(\"   xtUML Generated System - Automatic Simulation    \");\n");
        sb.append("        System.out.println(\"==========================================\");\n\n");

        // Setup
        sb.append("        ObjectBroker broker = ObjectBroker.getInstance();\n");
        sb.append("        EventDispatcher dispatcher = EventDispatcher.getInstance();\n\n");

        // ---------------------------------------------------------
        // Phase 1: Create Instances
        // ---------------------------------------------------------
        sb.append("        System.out.println(\"\\n[1] Phase 1: Initializing Model Instances...\");\n");

        if (model.getModel() != null) {
            for (XtUmlModelElement element : model.getModel()) {
                if (element instanceof ModelClass) {
                    ModelClass clazz = (ModelClass) element;
                    String varName = clazz.getClassName().toLowerCase() + "1";
                    createdVars.put(clazz.getClassName(), varName); // Register for linking

                    sb.append("        // Create ").append(clazz.getClassName()).append("\n");
                    sb.append("        ").append(clazz.getClassName()).append(" ").append(varName)
                            .append(" = new ").append(clazz.getClassName()).append("();\n");

                    // Set Attributes
                    if (clazz.getAttributes() != null) {
                        for (Attribute attr : clazz.getAttributes()) {
                            String type = attr.getDataType();
                            String attrName = attr.getAttributeName();
                            String setter = "set" + attrName.substring(0, 1).toUpperCase() + attrName.substring(1);

                            if (attrName.equals("id") || attrName.contains("_id"))
                                continue;

                            if ("String".equalsIgnoreCase(type)) {
                                String val = "\"Test " + attrName + "\"";
                                if ("availability".equalsIgnoreCase(attrName))
                                    val = "\"available\"";
                                sb.append("        ").append(varName).append(".").append(setter).append("(").append(val)
                                        .append(");\n");
                                sb.append("        System.out.println(\"   > Set ").append(attrName).append(": \" + ")
                                        .append(varName).append(".get")
                                        .append(attrName.substring(0, 1).toUpperCase() + attrName.substring(1))
                                        .append("());\n");
                            } else if ("Integer".equalsIgnoreCase(type)) {
                                sb.append("        ").append(varName).append(".").append(setter).append("(10);\n");
                                sb.append("        System.out.println(\"   > Set ").append(attrName).append(": \" + ")
                                        .append(varName).append(".get")
                                        .append(attrName.substring(0, 1).toUpperCase() + attrName.substring(1))
                                        .append("());\n");
                            } else if ("Real".equalsIgnoreCase(type)) {
                                sb.append("        ").append(varName).append(".").append(setter).append("(99.99);\n");
                                sb.append("        System.out.println(\"   > Set ").append(attrName).append(": \" + ")
                                        .append(varName).append(".get")
                                        .append(attrName.substring(0, 1).toUpperCase() + attrName.substring(1))
                                        .append("());\n");
                            }
                        }
                    }

                    sb.append("        System.out.println(\" - Created: \" + ").append(varName)
                            .append(".getClass().getSimpleName() + \" (\" + ")
                            .append(varName).append(".getInstanceId() + \")\");\n");

                    // Check initial state
                    if (clazz.getStates() != null && !clazz.getStates().isEmpty()) {
                        sb.append("        System.out.println(\"   Initial State: \" + getField(").append(varName)
                                .append(", \"currentState\"));\n");
                    }
                    sb.append("\n");
                }
            }
        }

        // ---------------------------------------------------------
        // Phase 2: Establish Relations (Auto-Linker)
        // ---------------------------------------------------------
        sb.append("        System.out.println(\"\\n[2] Phase 2: Establishing Relationships...\");\n");
        if (model.getModel() != null) {
            for (XtUmlModelElement element : model.getModel()) {
                if (element instanceof Association) {
                    Association assoc = (Association) element;
                    if (assoc.getClasses() != null && assoc.getClasses().size() == 2) {
                        AssociationEnd end1 = assoc.getClasses().get(0);
                        AssociationEnd end2 = assoc.getClasses().get(1);

                        String var1 = createdVars.get(end1.getClassName());
                        String var2 = createdVars.get(end2.getClassName());

                        if (var1 != null && var2 != null) {
                            sb.append("        // Link ").append(end1.getClassName()).append(" <-> ")
                                    .append(end2.getClassName()).append("\n");
                            sb.append("        RelationshipManager.getInstance().relate(").append(var1).append(", ")
                                    .append(var2).append(", \"").append(assoc.getName()).append("\", null);\n");
                            sb.append("        System.out.println(\" - Linked: ").append(end1.getClassName())
                                    .append(" and ").append(end2.getClassName()).append(" across ")
                                    .append(assoc.getName()).append("\");\n\n");
                        }
                    }
                }
            }
        }

        // ---------------------------------------------------------
        // Phase 3: Fire Events (Auto-Event)
        // ---------------------------------------------------------
        sb.append("        System.out.println(\"\\n[3] Phase 3: Firing Test Events...\");\n");
        if (model.getModel() != null) {
            for (XtUmlModelElement element : model.getModel()) {
                if (element instanceof ModelClass) {
                    ModelClass clazz = (ModelClass) element;
                    String varName = createdVars.get(clazz.getClassName());

                    if (clazz.getStates() != null && !clazz.getStates().isEmpty()) {
                        State initialState = clazz.getStates().get(0);
                        if (initialState.getStateEvents() != null && !initialState.getStateEvents().isEmpty()) {
                            String evt = initialState.getStateEvents().get(0); // Pick first available event

                            sb.append("        // Fire event ").append(evt).append(" to ").append(clazz.getClassName())
                                    .append("\n");
                            sb.append("        System.out.println(\" -> Dispatching Event: ").append(evt)
                                    .append("...\");\n");
                            sb.append("        dispatcher.enqueue(new XtUmlEvent(").append(varName)
                                    .append(".getInstanceId(), ")
                                    .append(clazz.getClassName()).append(".EVENT_").append(evt.toUpperCase())
                                    .append(", null));\n");
                        }
                    }
                }
            }
            sb.append("\n");
            sb.append("        System.out.println(\" -> Processing Event Queue...\");\n");
            sb.append("        dispatcher.processQueue();\n");
        }

        sb.append("\n        System.out.println(\"\\n[4] Simulation Complete.\");\n");
        sb.append("    }\n");

        // Helper
        sb.append("    private static Object getField(Object obj, String fieldName) {\n");
        sb.append("        try {\n");
        sb.append("            java.lang.reflect.Field f = obj.getClass().getDeclaredField(fieldName);\n");
        sb.append("            f.setAccessible(true);\n");
        sb.append("            return f.get(obj);\n");
        sb.append("        } catch (Exception e) {\n");
        sb.append("            return \"Unknown\";\n");
        sb.append("        }\n");
        sb.append("    }\n");

        sb.append("}\n");
        return sb.toString();
    }
}
