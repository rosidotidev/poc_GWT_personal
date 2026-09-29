package com.example.orders.frontend.client.ui.orders;

import com.example.orders.frontend.client.rpc.OrdersRpcServiceAsync;
import com.example.orders.frontend.client.ui.AppModal;
import com.example.orders.frontend.client.ui.Ui;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

public class OrdersTab extends Composite {

    private final OrderEditorPanel editorPanel;
    private final OrderListPanel listPanel;
    private final AppModal modal = new AppModal();

    public OrdersTab(OrdersRpcServiceAsync ordersService, OrderListPanel.OrdersChangedHandler ordersChangedHandler) {
        listPanel = new OrderListPanel(ordersService, ordersChangedHandler);
        editorPanel = new OrderEditorPanel(ordersService, () -> {
            modal.hide();
            listPanel.showAllAndRefresh();
        }, modal::hide);

        FlowPanel root = Ui.panel("page-view orders-view");
        Button create = new Button("Create order");
        create.setStyleName("button button-dark");
        create.addClickHandler(event -> openCreate());
        root.add(Ui.hero("WORKSPACE", "The order workspace.",
                "Create a new order and manage the queue from one place.", create));
        root.add(listPanel);
        initWidget(root);
    }

    public void refresh() {
        listPanel.refresh();
    }

    public void openCreate() {
        modal.showContent(editorPanel, editorPanel::beginCreate);
    }

    public void showFilter(String filter) {
        listPanel.showFilter(filter);
    }
}
