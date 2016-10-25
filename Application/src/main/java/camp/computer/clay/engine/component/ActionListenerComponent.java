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

    public ActionListener getActionListener() {
        return this.actionListener;
    }

    public void processAction(Action action) {
        // <HACK>
//        // TODO: Remove this after moving ActionListener into Entity.
//        if (getClass() == Host.class || getClass() == Extension.class) {
//            getActionListener().onAction(action);
//        }

        actionListener.onAction(action);

//        if (getClass() == Space.class) {
//            ((Space) this).actionListener.onAction(action);
//        }
        // </HACK>
    }
}
