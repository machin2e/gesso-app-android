package camp.computer.clay.model.assembly;

public class Instruction {

    public enum Type {
        PORT,
        PATH
    }

    String text;
    String speakText;

    boolean isComplete;
}
