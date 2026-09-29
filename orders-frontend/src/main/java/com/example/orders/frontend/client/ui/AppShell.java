package com.example.orders.frontend.client.ui;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class AppShell extends Composite {

    @FunctionalInterface
    public interface NavigationHandler {
        void navigate(String token);
    }

    private final FlowPanel pageHost = Ui.panel("page-host");
    private final Button overviewNav = new Button();
    private final Button ordersNav = new Button();
    private final Button productsNav = new Button();
    private final Button suppliersNav = new Button();

    public AppShell(NavigationHandler navigationHandler) {
        FlowPanel shell = Ui.panel("app-shell");
        shell.add(buildMasthead());
        shell.add(buildNavigation(navigationHandler));
        shell.add(pageHost);
        initWidget(shell);
    }

    public void show(String token, Widget view) {
        pageHost.clear();
        pageHost.add(view);
        setSelected(overviewNav, "overview".equals(token));
        setSelected(ordersNav, "orders".equals(token));
        setSelected(productsNav, "products".equals(token));
        setSelected(suppliersNav, "suppliers".equals(token));
    }

    private FlowPanel buildMasthead() {
        FlowPanel masthead = Ui.panel("masthead");
        FlowPanel brand = Ui.panel("brand-lockup");
        brand.add(Ui.label("O", "brand-mark"));
        FlowPanel copy = Ui.panel("brand-copy");
        copy.add(Ui.label("ORDER OFFICE", "brand-name"));
        copy.add(Ui.label("Operations workspace", "brand-caption"));
        brand.add(copy);
        FlowPanel meta = Ui.panel("masthead-meta");
        meta.add(Ui.label("LIVE WORKSPACE", "live-indicator"));
        meta.add(Ui.label("Orders / Overview", "breadcrumb"));
        masthead.add(brand);
        masthead.add(meta);
        return masthead;
    }

    private FlowPanel buildNavigation(NavigationHandler navigationHandler) {
        FlowPanel navigation = Ui.panel("primary-navigation");
        configure(overviewNav, "Overview", "overview", navigationHandler);
        configure(ordersNav, "Orders", "orders", navigationHandler);
        configure(productsNav, "Products", "products", navigationHandler);
        configure(suppliersNav, "Suppliers", "suppliers", navigationHandler);
        navigation.add(overviewNav);
        navigation.add(ordersNav);
        navigation.add(productsNav);
        navigation.add(suppliersNav);
        return navigation;
    }

    private void configure(Button button, String title, String token, NavigationHandler navigationHandler) {
        button.setText(title);
        button.setStyleName("nav-link");
        button.addClickHandler(event -> navigationHandler.navigate(token));
    }

    private void setSelected(Button button, boolean selected) {
        button.setStyleName("nav-link" + (selected ? " nav-link-active" : ""));
    }
}
