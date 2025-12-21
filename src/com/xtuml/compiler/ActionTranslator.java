package com.xtuml.compiler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Translates Object Action Language (OAL) snippets into Java code.
 */
public class ActionTranslator {

    // 1. Select from Instances
    private static final Pattern SELECT_PATTERN = Pattern
            .compile("select any (\\w+) from instances of (\\w+)(?: where (.*))?");

    // 2. Relate (Updated for 'using')
    private static final Pattern RELATE_PATTERN = Pattern
            .compile("relate (\\w+) to (\\w+) across (\\w+)(?: using (\\w+))?");

    // 3. Unrelate
    private static final Pattern UNRELATE_PATTERN = Pattern.compile("unrelate (\\w+) from (\\w+) across (\\w+)");

    // 4. Navigate
    private static final Pattern NAVIGATE_PATTERN = Pattern
            .compile("select one (\\w+) related by (\\w+)->(\\w+)\\[(\\w+)\\]");

    // 5. Timer
    private static final Pattern TIMER_PATTERN = Pattern
            .compile("create timer (\\w+) event (\\w+) to (\\w+) delay (\\d+)");

    // 6. Bridge
    private static final Pattern BRIDGE_PATTERN = Pattern.compile("Bridge::(\\w+)\\(\"(.*)\"\\)");

    // 7. Date
    private static final Pattern DATE_PATTERN = Pattern.compile("create date (\\w+)");

    // 8. Delete
    private static final Pattern DELETE_PATTERN = Pattern.compile("delete (\\w+)");

    // 9. If
    private static final Pattern IF_PATTERN = Pattern.compile("if \\((.*)\\)");

    // 10. Assigment / Math
    private static final Pattern ASSIGN_PATTERN = Pattern.compile("(\\w+) = (.+)");

    // 11. Loops
    private static final Pattern FOR_EACH_PATTERN = Pattern.compile("for each (\\w+) in (\\w+)");
    private static final Pattern WHILE_PATTERN = Pattern.compile("while \\((.*)\\)");

    // 12. Create
    private static final Pattern CREATE_PATTERN = Pattern.compile("create object instance (\\w+) of (\\w+)");

    // 13. Select Many
    private static final Pattern SELECT_MANY_PATTERN = Pattern
            .compile("select many (\\w+) from instances of (\\w+)(?: where (.*))?");

    // 14. Generate Event
    private static final Pattern GENERATE_PATTERN = Pattern.compile("generate (\\w+) to (\\w+)");
    private static final Pattern GENERATE_CLASS_PATTERN = Pattern.compile("generate (\\w+) to class (\\w+)");
    private static final Pattern GENERATE_CREATOR_PATTERN = Pattern.compile("generate (\\w+) to creator");

    // 15. Control Flow
    private static final Pattern ELIF_PATTERN = Pattern.compile("elif \\((.*)\\)");

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

        // Try Assignment (Math)
        matcher = ASSIGN_PATTERN.matcher(trimmed);
        if (matcher.matches()) {
            String target = matcher.group(1);
            String expr = matcher.group(2);

            // Check for event param access: rcvd_evt.paramName
            if (expr.contains("rcvd_evt.")) {
                expr = expr.replaceAll("rcvd_evt\\.(\\w+)", "(double)rcvd_evt.getData(\"$1\")");
            } else {
                // Try to resolve variables in the expression (e.g. width -> this.getWidth())
                expr = translateCondition(expr, "this");
            }

            return "double " + target + " = " + expr + ";";
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

        // Try Generate Class (Static)
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

        // Try Generate (Instance)
        matcher = GENERATE_PATTERN.matcher(trimmed);
        if (matcher.matches()) {
            String evtName = matcher.group(1);
            String target = matcher.group(2);

            // Assuming target has public static int EVENT_evtName
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

        // Fallback
        return "// Could not translate: " + trimmed;
    }

    private String translateCondition(String oalCondition, String context) {
        if (oalCondition == null)
            return "true";

        // Pre-process Logic Operators
        // not -> !
        // and -> &&
        // or -> ||
        // is_empty -> isEmpty()
        // not_empty -> !isEmpty()

        String processed = oalCondition
                .replaceAll("\\bnot\\b", "!")
                .replaceAll("\\band\\b", "&&")
                .replaceAll("\\bor\\b", "||")
                .replaceAll("\\.(\\w+)\\.is_empty", ".$1.isEmpty()") // Standardize if needed, usually var.is_empty
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
                if ("true".equals(word) || "false".equals(word) || "null".equals(word)) {
                    m.appendReplacement(sb, word);
                } else {
                    // VARIABLE / ATTRIBUTE logic
                    // If context is "candidate" (for Select filter), use candidate.get...
                    // If context is "this" (for If condition on self), use get...
                    String prefix = "candidate".equals(context) ? "candidate." : "this.";
                    String getter = prefix + "get" + word.substring(0, 1).toUpperCase() + word.substring(1) + "()";
                    m.appendReplacement(sb, getter);
                }
            } else {
                // It's a string literal, ignore
                m.appendReplacement(sb, m.group());
            }
        }
        m.appendTail(sb);
        String res = sb.toString();

        // 2. Handle String literal equality
        // OAL: var == "value" -> Java: "value".equals(var)
        res = res.replaceAll("([\\w\\.\\(\\)]+)\\s*==\\s*\"([^\"]*)\"", "\"$2\".equals($1)");

        return res;
    }
}
