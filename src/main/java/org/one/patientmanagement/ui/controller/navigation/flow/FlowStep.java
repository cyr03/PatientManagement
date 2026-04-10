package org.one.patientmanagement.ui.controller.navigation.flow;

/**
 *
 * @author KAROL JOHN
 * @param <R> route
 * @param <S> arbitrary state model
 */
public interface FlowStep<R extends Enum<R>, S> {

    R getRoute();

    FlowStep<R, S> next(FlowState<S> state);

    default boolean canProceed(FlowState<S> state) {
        return true;
    }
}