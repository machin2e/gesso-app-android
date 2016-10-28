package camp.computer.clay.engine.component;

import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.model.action.Action;
import camp.computer.clay.model.action.ActionListener;

public class ActionListenerComponent extends Component {

    private ActionListener actionListener = null;

    public ActionListenerComponent() {
        super();
    }

    public void setOnActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void processAction(Action action) {
        actionListener.onAction(action);
    }
}
