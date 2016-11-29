package camp.computer.clay.platform.graphics.controls;

public class Parameter<T> {

    private T value;

    public Parameter(T value) {
        this.value = value;
    }
    
    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}


//// <TODO>
//public enum ViewType {
//    BUTTON,
//    TEXT // i.e., for labels, read-only text, editable text
//}
//
//public class EntryView {
//    ViewType type;
//    Parameter parameters;
//}
//
//    private View createEntryView(String label) {
//        return null;
//    }
//// </TODO>
