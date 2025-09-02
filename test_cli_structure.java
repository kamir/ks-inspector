import java.lang.reflect.Method;
import java.util.Arrays;
import picocli.CommandLine.Command;
import java.lang.annotation.Annotation;

/**
 * Simple test to demonstrate KS-Inspector CLI structure
 * This works around dependency issues to show what commands are available
 */
public class test_cli_structure {
    
    public static void main(String[] args) {
        try {
            // Load the CLI class
            Class<?> cliClass = Class.forName("io.confluent.mdgraph.cli.CLI");
            
            System.out.println("=".repeat(60));
            System.out.println("KS-INSPECTOR CLI STRUCTURE ANALYSIS");
            System.out.println("=".repeat(60));
            
            // Check if it has the @Command annotation
            Command classCommand = cliClass.getAnnotation(Command.class);
            if (classCommand != null) {
                System.out.println("Main CLI Command: " + classCommand.name());
                System.out.println("Description: " + classCommand.description());
                System.out.println("Version: " + classCommand.version());
            }
            
            System.out.println("\nAvailable Subcommands:");
            System.out.println("-".repeat(40));
            
            // Get all methods and check for @Command annotations
            Method[] methods = cliClass.getDeclaredMethods();
            int commandCount = 0;
            
            for (Method method : methods) {
                Command methodCommand = method.getAnnotation(Command.class);
                if (methodCommand != null) {
                    commandCount++;
                    System.out.printf("%d. %-20s - %s%n", 
                        commandCount, 
                        methodCommand.name(), 
                        methodCommand.description());
                }
            }
            
            System.out.println("\nTotal Commands Found: " + commandCount);
            
            System.out.println("\n" + "=".repeat(60));
            System.out.println("CLI STRUCTURE VERIFICATION COMPLETE");
            System.out.println("=".repeat(60));
            
        } catch (ClassNotFoundException e) {
            System.out.println("CLI class not found - compilation may be needed first");
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error analyzing CLI structure: " + e.getMessage());
            e.printStackTrace();
        }
    }
}