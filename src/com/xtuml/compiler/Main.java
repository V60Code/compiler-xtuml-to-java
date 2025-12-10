package com.xtuml.compiler;

import com.xtuml.compiler.model.Association;
import com.xtuml.compiler.model.MetaModel;
import com.xtuml.compiler.model.ModelClass;
import com.xtuml.compiler.model.XtUmlModelElement;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting xtUML Compiler Phase 2 Verification...");

        String filePath = "xtuml.json"; // Assumes running from project root
        ModelLoader loader = new ModelLoader();

        try {
            MetaModel model = loader.load(filePath);
            System.out.println("Successfully loaded model: " + model.getSubName());

            int classCount = 0;
            int assocCount = 0;

            for (XtUmlModelElement element : model.getModel()) {
                if (element instanceof ModelClass) {
                    classCount++;
                    ModelClass clazz = (ModelClass) element;
                    System.out.println(" - Found Class: " + clazz.getClassName() + " (ID: " + clazz.getClassId() + ")");
                    if (clazz.getAttributes() != null) {
                        System.out.println("   Attributes: " + clazz.getAttributes().size());
                    }
                    if (clazz.getStates() != null) {
                        System.out.println("   States: " + clazz.getStates().size());
                    }
                } else if (element instanceof Association) {
                    assocCount++;
                    Association assoc = (Association) element;
                    System.out.println(" - Found Association: " + assoc.getName());
                }
            }

            System.out.println("Summary: Loaded " + classCount + " Classes and " + assocCount + " Associations.");

            // Phase 3 Verification
            System.out.println("\n--- Phase 3: Generated Code Verification ---");
            JavaSourceGenerator generator = new JavaSourceGenerator();
            java.util.Map<String, String> sources = generator.generateSources(model);

            if (!sources.isEmpty()) {
                // Print the first one found
                String firstKey = sources.keySet().iterator().next();
                System.out.println("Generated Source for: " + firstKey);
                System.out.println("--------------------------------------------------");
                System.out.println(sources.get(firstKey));
                System.out.println("--------------------------------------------------");
            } else {
                System.out.println("No classes found to generate.");
            }

            // Phase 4 Verification: OAL Translation
            System.out.println("\n--- Phase 4: OAL Translation Verification ---");
            ActionTranslator translator = new ActionTranslator();
            String sampleOal = "select any user from instances of User where name == \"Bob\"";
            String sampleRelate = "relate user to order across R1";

            System.out.println("OAL Input: " + sampleOal);
            System.out.println("Java Output:\n" + translator.translateAction(sampleOal));

            System.out.println("\nOAL Input: " + sampleRelate);
            System.out.println("Java Output:\n" + translator.translateAction(sampleRelate));

            // Phase 5 Verification: Event & State Machine Runtime
            System.out.println("\n--- Phase 5: Event Runtime Verification ---");
            System.out.println("Simulating a generated class 'MockConsole'...");

            // Mock class mimicking generated code
            class MockConsole extends com.xtuml.runtime.BaseModel {
                enum State {
                    AVAILABLE, RENTED
                }

                State currentState = State.AVAILABLE;

                public MockConsole() {
                    com.xtuml.runtime.ObjectBroker.getInstance().add(this);
                }

                @Override
                public void processEvent(com.xtuml.runtime.XtUmlEvent e) {
                    // Event ID 1 = setRented
                    if (currentState == State.AVAILABLE && e.getEventId() == 1) {
                        System.out.println(" [MockConsole] Event received. Transitioning AVAILABLE -> RENTED");
                        currentState = State.RENTED;
                        action_Rented();
                    }
                }

                private void action_Rented() {
                    System.out.println(" [MockConsole] Executing Action: Console is now rented.");
                }
            }

            MockConsole console = new MockConsole();
            System.out.println("Created MockConsole. Current State: " + console.currentState);

            // Fire event
            System.out.println("Dispatching Event(ID=1) to Console...");
            com.xtuml.runtime.XtUmlEvent event = new com.xtuml.runtime.XtUmlEvent(console.getInstanceId(), 1, null);
            com.xtuml.runtime.EventDispatcher.getInstance().enqueue(event);

            com.xtuml.runtime.EventDispatcher.getInstance().processQueue();

            // Phase 6: Code Generation to Disk
            System.out.println("\n--- Phase 6: Write to Disk ---");
            String outputDir = "src-gen/com/xtuml/generated";
            java.io.File dir = new java.io.File(outputDir);
            if (!dir.exists()) {
                boolean success = dir.mkdirs();
                if (success) {
                    System.out.println("Created directory: " + outputDir);
                } else {
                    System.err.println("Failed to create directory: " + outputDir);
                }
            }

            // Re-generate from full model
            java.util.Map<String, String> classes = generator.generateSources(model);

            for (java.util.Map.Entry<String, String> entry : classes.entrySet()) {
                String className = entry.getKey();
                String code = entry.getValue();

                java.io.File file = new java.io.File(dir, className + ".java");
                try (java.io.FileWriter fw = new java.io.FileWriter(file)) {
                    fw.write(code);
                    System.out.println("Written: " + file.getAbsolutePath());
                } catch (java.io.IOException ex) {
                    System.err.println("Error writing " + className + ": " + ex.getMessage());
                }
            }

            System.out.println("Generation Complete. Files are in " + outputDir);

            // Phase 7: Generate Simulation Runner
            System.out.println("\n--- Phase 7: Generate Automatic Simulation ---");
            SimulationGenerator simGenerator = new SimulationGenerator();
            String runSystemCode = simGenerator.generateRunSystem(model);

            java.io.File runFile = new java.io.File("src-gen/RunSystem.java");
            try (java.io.FileWriter fw = new java.io.FileWriter(runFile)) {
                fw.write(runSystemCode);
                System.out.println("Written Auto-Runner: " + runFile.getAbsolutePath());
            } catch (java.io.IOException ex) {
                System.err.println("Error writing RunSystem: " + ex.getMessage());
            }

        } catch (Exception e) {
            System.err.println("Failed to load or generate model: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
