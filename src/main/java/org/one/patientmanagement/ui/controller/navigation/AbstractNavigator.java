package org.one.patientmanagement.ui.controller.navigation;

import java.awt.CardLayout;
import javax.swing.JPanel;

public abstract class AbstractNavigator<R extends Enum> {

    private final CardLayout layout;
    private final JPanel container;

    protected AbstractNavigator(CardLayout layout, JPanel container) {
        this.layout = layout;
        this.container = container;
    }

    /**
     * Navigate to a route
     */
    public void goTo(R route) {
        String key = getRouteKey(route);
        beforeNavigate(route);
        layout.show(container, key);
        afterNavigate(route);
    }

    /**
     * Convert route → CardLayout key
     */
    protected String getRouteKey(R route) {
        return route.name();
    }

    /**
     * Hook: before navigation
     */
    protected void beforeNavigate(R route) {}

    /**
     * Hook: after navigation
     */
    protected void afterNavigate(R route) {}

    /**
     * Register a view
     */
    public void register(R route, JPanel panel) {
        container.add(panel, getRouteKey(route));
    }

    /**
     * Optional accessors (protected for subclasses)
     */
    protected JPanel getContainer() {
        return container;
    }

    protected CardLayout getLayout() {
        return layout;
    }
}
