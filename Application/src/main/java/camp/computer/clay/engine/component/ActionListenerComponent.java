package camp.computer.clay.engine.component;

import camp.computer.clay.model.action.Action;
import camp.computer.clay.model.action.ActionListener;

public class ActionListenerComponent extends Component {

    private ActionListener actionListener = null;

    public ActionListenerComponent() {
    }

    public void setOnActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void processAction(Action action) {
        actionListener.onAction(action);
    }
}