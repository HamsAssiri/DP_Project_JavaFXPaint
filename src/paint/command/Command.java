package paint.command;

public interface Command {
    void execute();  
    void undo();     
    String getName(); // Add this for command identification
}
