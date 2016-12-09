package camp.computer.clay.structure.assembly;

public class Instruction {

    public enum Type {
        PORT,
        PATH
    }

    String text;
    String speakText;

    boolean isComplete;
}
