package com.xtuml.compiler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Translates Object Action Language (OAL) snippets into Java code.
 * [CRITERIA 8] Mapping OAL Code ke Java
 */
public class ActionTranslator {

    // Component: Instance Selection (Requirement 3)
    private static final Pattern SELECT_PATTERN = Pattern
            .compile("select any (\\w+) from instances of (\\w+)(?: where (.*))?");

    // Component: Creating Instances of a Relationship (Requirement 7)
    private static final Pattern RELATE_PATTERN = Pattern
            .compile("relate (\\w+) to (\\w+) across (\\w+)(?: using (\\w+))?");

    // Component: Deleting Instance of a Relationship (Requirement 8)
    private static final Pattern UNRELATE_PATTERN = Pattern.compile("unrelate (\\w+) from (\\w+) across (\\w+)");

    // Component: Instance Selection by Relationship Navigation (Requirement 9)
    private static final Pattern NAVIGATE_PATTERN = Pattern
            .compile("select one (\\w+) related by (\\w+)->(\\w+)\\[(\\w+)\\]");

    // Component: Timers (Requirement 21)
    private static final Pattern TIMER_PATTERN = Pattern
            .compile("create timer (\\w+) event (\\w+) to (\\w+) delay (\\d+)");

    // Component: Bridges (Requirement 16)
    private static final Pattern BRIDGE_PATTERN = Pattern.compile("Bridge::(\\w+)\\(\"(.*)\"\\)");

    // Component: Date and Time (Requirement 20)
    private static final Pattern DATE_PATTERN = Pattern.compile("create date (\\w+)");

    // Component: Instance Deletion (Requirement 6)
    private static final Pattern DELETE_PATTERN = Pattern.compile("delete (\\w+)");

    // Component: Control Logic (Requirement 1)
    private static final Pattern IF_PATTERN = Pattern.compile("if \\((.*)\\)");

    // Component: Writing Attributes, Assignments, Arithmetic (Requirement 4, 13)
    // Groups: 1=Target(var or obj.attr), 2=Expression
    private static final Pattern ASSIGN_PATTERN = Pattern.compile("([\\w\\.]+) = (.+)");

    // Component: Control Logic (Loops) (Requirement 1)
    private static final Pattern FOR_EACH_PATTERN = Pattern.compile("for each (\\w+) in (\\w+)");
    private static final Pattern WHILE_PATTERN = Pattern.compile("while \\((.*)\\)");

    // Component: Instance Creation (Requirement 2)
    private static final Pattern CREATE_PATTERN = Pattern.compile("create object instance (\\w+) of (\\w+)");

    // Component: Instance Selection (Many) (Requirement 3)
    private static final Pattern SELECT_MANY_PATTERN = Pattern
            .compile("select many (\\w+) from instances of (\\w+)(?: where (.*))?");

    // Component: Generating Events (Requirement 11)
    private static final Pattern GENERATE_PATTERN = Pattern.compile("generate (\\w+) to (\\w+)");
    private static final Pattern GENERATE_CLASS_PATTERN = Pattern.compile("generate (\\w+) to class (\\w+)");
    private static final Pattern GENERATE_CREATOR_PATTERN = Pattern.compile("generate (\\w+) to creator");

    // Component: Control Logic (Requirement 1)
    private static final Pattern ELIF_PATTERN = Pattern.compile("elif \\((.*)\\)");

    // Component: Operations / Functions (Requirement 15, 17)
    private static final Pattern OPERATION_PATTERN = Pattern.compile("([\\w\\.]+)\\((.*)\\)");

    public String translateAction(String oalLine) {
        if (oalLine == null || oalLine.trim().isEmpty()) {
            return "";
        }

        String trimmed = oalLine.trim();
        Matcher matcher;

        // Try Select
        matcher = SELECT_PATTERN.matcher(trimmed);
        if (matcher.matches()) {
            String varName = matcher.group(1);
            String type = matcher.group(2);
            String condition = matcher.group(3);

            String javaCondition = (condition == null || condition.isEmpty()) ? "true"
                    : translateCondition(condition, "candidate");

            return String.format(
                    "%s %s = ObjectBroker.getInstance().selectAll(%s.class).stream()\n" +
                            "    .filter(candidate -> %s)\n" +
                            "    .findFirst().orElse(null);",
                    type, varName, type, javaCondition);
        }

        // Try Relate
        matcher = RELATE_PATTERN.matcher(trimmed);
        if (matcher.matches()) {
            String src = matcher.group(1);
            String target = matcher.group(2);
            String rel = matcher.group(3);
            String usingInst = matcher.group(4); // May be null

            String srcJava = "self".equals(src) ? "this" : src;
            String targetJava = "self".equals(target) ? "this" : target;
            String usingJava = (usingInst == null) ? "null" : usingInst;

            return String.format("RelationshipManager.getInstance().relate(%s, %s, \"%s\", %s);", srcJava, targetJava,
                    rel, usingJava);
        }

        // Try Unrelate
        matcher = UNRELATE_PATTERN.matcher(trimmed);
        if (matcher.matches()) {
            String src = matcher.group(1);
            String target = matcher.group(2);
            String rel = matcher.group(3);

            String srcJava = "self".equals(src) ? "this" : src;
            String targetJava = "self".equals(target) ? "this" : target;

            return String.format("RelationshipManager.getInstance().unrelate(%s, %s, \"%s\", null);", srcJava,
                    targetJava, rel);
        }

        // Try Navigate
        matcher = NAVIGATE_PATTERN.matcher(trimmed);
        if (matcher.matches()) {
            String targetVar = matcher.group(1);
            String srcVar = matcher.group(2);
            String targetType = matcher.group(3);
            String rel = matcher.group(4);

            String srcJava = "self".equals(srcVar) ? "this" : srcVar;

            return String.format(
                    "%s %s = (%s) RelationshipManager.getInstance().getRelated(%s, \"%s\", null).stream()\n" +
                            "    .findFirst().orElse(null);",
                    targetType, targetVar, targetType, srcJava, rel);
        }

        // Try Timer
        matcher = TIMER_PATTERN.matcher(trimmed);
        if (matcher.matches()) {
            String evtName = matcher.group(2);
            String target = matcher.group(3);
            String delay = matcher.group(4);

            String targetJava = "self".equals(target) ? "this" : target;

            // Updated: Removed targetJava.USAGE, keeping it simpler
            return String.format(
                    "com.xtuml.runtime.XtUmlEvent evt = new com.xtuml.runtime.XtUmlEvent(%s.getInstanceId(), EVENT_%s, null);\n"
                            +
                            "com.xtuml.runtime.TimerService.getInstance().startTimer(%s, evt);",
                    targetJava, evtName.toUpperCase(), delay);
        }

        // Try Bridge
        matcher = BRIDGE_PATTERN.matcher(trimmed);
        if (matcher.matches()) {
            String bridgeFunc = matcher.group(1);
            String msg = matcher.group(2);
            if ("log".equals(bridgeFunc)) {
                return String.format("System.out.println(\"%s\");", msg);
            }
        }

        // Try Date
        matcher = DATE_PATTERN.matcher(trimmed);
        if (matcher.matches()) {
            String varName = matcher.group(1);
            return "java.time.LocalDateTime " + varName + " = java.time.LocalDateTime.now();";
        }

        // Try Delete
        matcher = DELETE_PATTERN.matcher(trimmed);
        if (matcher.matches()) {
            String varName = matcher.group(1);
            if ("self".equals(varName)) {
                return "this.delete();";
            } else {
                return varName + ".delete();";
            }
        }

        // Try If
        matcher = IF_PATTERN.matcher(trimmed);
        if (matcher.matches()) {
            String cond = matcher.group(1);
            return "if (" + translateCondition(cond, "this") + ") {";
        }

        if ("end if".equals(trimmed)) {
            return "}";
        }

        // Try Assignment (Smart: Setter or Variable)
        matcher = ASSIGN_PATTERN.matcher(trimmed);
        if (matcher.matches()) {
            String target = matcher.group(1);
            String expr = matcher.group(2);
            
            // Translate the expression
            if (expr.contains("rcvd_evt.")) {
                // Improved: Remove hardcoded (double) cast
                expr = expr.replaceAll("rcvd_evt\\.(\\w+)", "rcvd_evt.getData(\"$1\")");
            } else {
                expr = translateCondition(expr, "this");
            }

            // Case 1: Attribute Setter -> obj.attr = val
            if (target.contains(".")) {
                 String[] parts = target.split("\\.");
                 if (parts.length == 2) {
                     String obj = parts[0];
                     String attr = parts[1];
                     String setter = "set" + attr.substring(0, 1).toUpperCase() + attr.substring(1);
                     return obj + "." + setter + "(" + expr + ");";
                 }
            }

            // Case 2: Local Variable Assignment
            // Smart type inference
            if (expr.startsWith("\"")) {
                 return "String " + target + " = " + expr + ";";
            } else if (expr.matches("-?\\d+(\\.\\d+)?")) {
                 return "double " + target + " = " + expr + ";";
            } else if (expr.equals("true") || expr.equals("false")) {
                 return "boolean " + target + " = " + expr + ";";
            } else {
                 // Dynamic type inference using var.
                 if ("null".equals(expr)) {
                     return "Object " + target + " = null;"; 
                 }
                 return "var " + target + " = " + expr + ";";
            }
        }

        // Try Operations / Function Calls
        // Executed AFTER assignment to avoid capturing "x = y"
        matcher = OPERATION_PATTERN.matcher(trimmed);
        if (matcher.matches()) {
            String func = matcher.group(1);
            String args = matcher.group(2);
            
            // Parse arguments
            String translatedArgs = "";
            if (!args.isEmpty()) {
                String[] argList = args.split(",");
                StringBuilder sb = new StringBuilder();
                for (String arg : argList) {
                    if (sb.length() > 0) sb.append(", ");
                    sb.append(translateCondition(arg.trim(), "this"));
                }
                translatedArgs = sb.toString();
            }
            return func + "(" + translatedArgs + ");";
        }

        // Try Loops
        matcher = FOR_EACH_PATTERN.matcher(trimmed);
        if (matcher.matches()) {
            String var = matcher.group(1);
            String col = matcher.group(2);
            // using 'var' for type inference (Java 10+)
            return "for (var " + var + " : " + col + ") {";
        }

        matcher = WHILE_PATTERN.matcher(trimmed);
        if (matcher.matches()) {
            String cond = matcher.group(1);
            return "while (" + translateCondition(cond, "this") + ") {";
        }

        // Try Create
        matcher = CREATE_PATTERN.matcher(trimmed);
        if (matcher.matches()) {
            String var = matcher.group(1);
            String type = matcher.group(2);
            return type + " " + var + " = new " + type + "();";
        }

        // Try Select Many
        matcher = SELECT_MANY_PATTERN.matcher(trimmed);
        if (matcher.matches()) {
            String varName = matcher.group(1);
            String type = matcher.group(2);
            String condition = matcher.group(3);

            String javaCondition = (condition == null || condition.isEmpty()) ? "true"
                    : translateCondition(condition, "candidate");

            return String.format(
                    "java.util.List<%s> %s = ObjectBroker.getInstance().selectAll(%s.class).stream()\n" +
                            "    .filter(candidate -> %s)\n" +
                            "    .collect(java.util.stream.Collectors.toList());",
                    type, varName, type, javaCondition);
        }

        // Try Generate Class
        matcher = GENERATE_CLASS_PATTERN.matcher(trimmed);
        if (matcher.matches()) {
            String evtName = matcher.group(1);
            String keyLetter = matcher.group(2);
            return String.format("System.out.println(\"[Static Event] Generating %s to class %s\");", evtName,
                    keyLetter);
        }

        // Try Generate Creator
        matcher = GENERATE_CREATOR_PATTERN.matcher(trimmed);
        if (matcher.matches()) {
            String evtName = matcher.group(1);
            return String.format("System.out.println(\"[Creator Event] Generating %s to creator\");", evtName);
        }

        // Try Generate Instance
        matcher = GENERATE_PATTERN.matcher(trimmed);
        if (matcher.matches()) {
            String evtName = matcher.group(1);
            String target = matcher.group(2);

            return String.format(
                    "com.xtuml.runtime.XtUmlEvent gevt = new com.xtuml.runtime.XtUmlEvent(%s.getInstanceId(), %s.EVENT_%s, null);\n"
                            +
                            "com.xtuml.runtime.EventDispatcher.getInstance().enqueue(gevt);",
                    target, target, evtName.toUpperCase());
        }

        // Control Flow
        matcher = ELIF_PATTERN.matcher(trimmed);
        if (matcher.matches()) {
            return "} else if (" + translateCondition(matcher.group(1), "this") + ") {";
        }

        if ("else".equals(trimmed))
            return "} else {";
        if ("break".equals(trimmed))
            return "break;";
        if ("continue".equals(trimmed))
            return "continue;";
        if ("return".equals(trimmed))
            return "return;";

        if ("end for".equals(trimmed) || "end while".equals(trimmed)) {
            return "}";
        }

        return "// Could not translate: " + trimmed;
    }

    private String translateCondition(String oalCondition, String context) {
        if (oalCondition == null)
            return "true";

        // Pre-process Logic Operators
        String processed = oalCondition
                .replaceAll("\\bnot\\b", "!")
                .replaceAll("\\band\\b", "&&")
                .replaceAll("\\bor\\b", "||")
                .replaceAll("\\.(\\w+)\\.is_empty", ".$1.isEmpty()") 
                .replaceAll("\\b(\\w+)\\.is_empty\\b", "$1.isEmpty()")
                .replaceAll("\\b(\\w+)\\.not_empty\\b", "!$1.isEmpty()");

        StringBuffer sb = new StringBuffer();
        // Match string literals OR identifiers
        Pattern p = Pattern.compile("\"[^\"]*\"|\\b([a-zA-Z_]\\w*)\\b");
        Matcher m = p.matcher(processed);

        while (m.find()) {
            if (m.group(1) != null) {
                // It's an identifier
                String word = m.group(1);
                if ("true".equals(word) || "false".equals(word) || "null".equals(word) || "this".equals(word) || "rcvd_evt".equals(word)) {
                    m.appendReplacement(sb, word);
                } else {
                    if (word.startsWith("get") || word.startsWith("set")) {
                        m.appendReplacement(sb, word);
                    } else {
                        String prefix = "candidate".equals(context) ? "candidate." : "this.";
                        String getter = prefix + "get" + word.substring(0, 1).toUpperCase() + word.substring(1) + "()";
                        m.appendReplacement(sb, getter);
                    }
                }
            } else {
                m.appendReplacement(sb, m.group());
            }
        }
        m.appendTail(sb);
        String res = sb.toString();

        // Handle equality with strings
        res = res.replaceAll("([\\w\\.\\(\\)]+)\\s*==\\s*\"([^\"]*)\"", "\"$2\".equals($1)");
        res = res.replaceAll("([\\w\\.\\(\\)]+)\\s*!=\\s*\"([^\"]*)\"", "!\"$2\".equals($1)");

        return res;
    }
}
