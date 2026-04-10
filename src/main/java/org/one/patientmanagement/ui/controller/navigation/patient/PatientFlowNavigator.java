package org.one.patientmanagement.ui.controller.navigation.patient;

import java.awt.CardLayout;
import java.util.Stack;
import javax.swing.JPanel;
import org.one.patientmanagement.ui.controller.navigation.AbstractNavigator;
import org.one.patientmanagement.ui.controller.navigation.flow.FlowState;
import org.one.patientmanagement.ui.controller.navigation.flow.FlowStep;

/**
 *
 * @param <R> route
 * @param <S> arbitrary state model
 */
public class PatientFlowNavigator<R extends Enum<R>, S> extends AbstractNavigator<R> {

    private final Stack<FlowStep<R,S>> history = new Stack<>();
    private final FlowState<S> state;

    private FlowStep<R,S> currentStep;

    public PatientFlowNavigator(CardLayout layout, JPanel container, FlowState<S> state) {
        super(layout, container);
        this.state = state;
    }

    public void start(FlowStep<R,S> firstStep) {
        history.clear();
        currentStep = firstStep;
        navigate();
    }

    public void next() {
        if (!currentStep.canProceed(state)) return;

        FlowStep<R,S> next = currentStep.next(state);
        if (next == null) return;

        history.push(currentStep);
        currentStep = next;

        navigate();
    }

    public void back() {
        if (history.isEmpty()) return;

        currentStep = history.pop();
        navigate();
    }

    private void navigate() {
        goTo(currentStep.getRoute());
    }

    public FlowStep<R,S> getCurrentStep() {
        return currentStep;
    }
}