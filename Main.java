/**
 * The Main class serves as the entry point for the application. It creates an instance of ConsoleController
 * and invokes its welcomeMessage method. This setup is typically used to initialize and start the application,
 * demonstrating a basic use case of handling user interactions through a console interface.
 */
public class Main {
    public static void main(String[] args) {
        ConsoleController controller = new ConsoleController();
        controller.welcomeMessage();
    }
}
