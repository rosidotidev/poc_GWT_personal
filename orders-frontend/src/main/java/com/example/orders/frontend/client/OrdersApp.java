package com.example.orders.frontend.client;

import com.example.orders.frontend.client.rpc.OrdersRpcService;
import com.example.orders.frontend.client.rpc.OrdersRpcServiceAsync;
import com.example.orders.frontend.client.ui.AppShell;
import com.example.orders.frontend.client.ui.OverviewTab;
import com.example.orders.frontend.client.ui.orders.OrdersTab;
import com.example.orders.frontend.client.ui.products.ProductsTab;
import com.example.orders.frontend.client.ui.suppliers.SuppliersTab;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class OrdersApp implements EntryPoint {

    private final OrdersRpcServiceAsync ordersService = GWT.create(OrdersRpcService.class);
    private AppShell shell;
    private OverviewTab overviewTab;
    private OrdersTab ordersTab;
    private ProductsTab productsTab;
    private SuppliersTab suppliersTab;

    @Override
    public void onModuleLoad() {
        overviewTab = new OverviewTab(this::openOrderComposer, this::refreshOrders, this::showOrdersWithFilter);
        ordersTab = new OrdersTab(ordersService, overviewTab::setOrders);
        productsTab = new ProductsTab(ordersService);
        suppliersTab = new SuppliersTab(ordersService, productsTab::setSuppliers);
        shell = new AppShell(token -> History.newItem(token));

        RootPanel.get().clear();
        RootPanel.get().add(shell);
        History.addValueChangeHandler(event -> showPage(event.getValue()));
        showPage(History.getToken());
        if (History.getToken() == null || History.getToken().isEmpty()) {
            History.newItem("overview", false);
        }

        ordersTab.refresh();
        productsTab.refresh();
        suppliersTab.refresh();
    }

    private void showPage(String token) {
        String page = normalizePage(token);
        shell.show(page, pageWidget(page));
    }

    private String normalizePage(String token) {
        if ("orders".equals(token) || "products".equals(token) || "suppliers".equals(token)) {
            return token;
        }
        return "overview";
    }

    private Widget pageWidget(String page) {
        if ("orders".equals(page)) {
            return ordersTab;
        }
        if ("products".equals(page)) {
            return productsTab;
        }
        if ("suppliers".equals(page)) {
            return suppliersTab;
        }
        return overviewTab;
    }

    private void openOrderComposer() {
        History.newItem("orders");
        ordersTab.openCreate();
    }

    private void refreshOrders() {
        ordersTab.refresh();
    }

    private void showOrdersWithFilter(String status) {
        ordersTab.showFilter(status);
        History.newItem("orders");
    }
}
