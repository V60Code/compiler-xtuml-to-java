import com.xtuml.generated.*;
import com.xtuml.runtime.*;
import java.util.UUID;
import java.time.LocalDateTime;

public class RunSystem {

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("   xtUML Generated System - Automatic Simulation    ");
        System.out.println("==========================================");

        ObjectBroker broker = ObjectBroker.getInstance();
        EventDispatcher dispatcher = EventDispatcher.getInstance();

        System.out.println("\n[1] Phase 1: Initializing Model Instances...");
        // Create Customer
        Customer customer1 = new Customer();
        customer1.setName("Test name");
        System.out.println("   > Set name: " + customer1.getName());
        customer1.setContact("Test contact");
        System.out.println("   > Set contact: " + customer1.getContact());
        customer1.setEmail("Test email");
        System.out.println("   > Set email: " + customer1.getEmail());
        System.out.println(" - Created: " + customer1.getClass().getSimpleName() + " (" + customer1.getInstanceId() + ")");

        // Create PSConsole
        PSConsole psconsole1 = new PSConsole();
        psconsole1.setModel("Test model");
        System.out.println("   > Set model: " + psconsole1.getModel());
        System.out.println(" - Created: " + psconsole1.getClass().getSimpleName() + " (" + psconsole1.getInstanceId() + ")");
        System.out.println("   Initial State: " + getField(psconsole1, "currentState"));

        // Create Rate
        Rate rate1 = new Rate();
        System.out.println(" - Created: " + rate1.getClass().getSimpleName() + " (" + rate1.getInstanceId() + ")");

        // Create Rental
        Rental rental1 = new Rental();
        System.out.println(" - Created: " + rental1.getClass().getSimpleName() + " (" + rental1.getInstanceId() + ")");
        System.out.println("   Initial State: " + getField(rental1, "currentState"));

        System.out.println("\n[2] Phase 2: Establishing Relationships...");
        // Link Customer <-> Rental
        RelationshipManager.getInstance().relate(customer1, rental1, "R1", null);
        System.out.println(" - Linked: Customer and Rental across R1");

        // Link Rental <-> PSConsole
        RelationshipManager.getInstance().relate(rental1, psconsole1, "R2", null);
        System.out.println(" - Linked: Rental and PSConsole across R2");

        // Link PSConsole <-> Rate
        RelationshipManager.getInstance().relate(psconsole1, rate1, "R3", null);
        System.out.println(" - Linked: PSConsole and Rate across R3");

        System.out.println("\n[3] Phase 3: Firing Test Events...");
        // Fire event setAvailable to PSConsole
        System.out.println(" -> Dispatching Event: setAvailable...");
        dispatcher.enqueue(new XtUmlEvent(psconsole1.getInstanceId(), PSConsole.EVENT_SETAVAILABLE, null));
        // Fire event pay_confirmed to Rental
        System.out.println(" -> Dispatching Event: pay_confirmed...");
        dispatcher.enqueue(new XtUmlEvent(rental1.getInstanceId(), Rental.EVENT_PAY_CONFIRMED, null));

        System.out.println(" -> Processing Event Queue...");
        dispatcher.processQueue();

        System.out.println("\n[4] Simulation Complete.");
    }
    private static Object getField(Object obj, String fieldName) {
        try {
            java.lang.reflect.Field f = obj.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            return f.get(obj);
        } catch (Exception e) {
            return "Unknown";
        }
    }
}
