package com.example.orders.frontend.client.ui.orders;

import com.example.orders.frontend.client.rpc.OrderItemRpcDto;
import com.example.orders.frontend.client.rpc.OrderRpcDto;
import com.example.orders.frontend.client.rpc.OrdersRpcServiceAsync;
import com.example.orders.frontend.client.ui.Ui;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

import java.util.ArrayList;
import java.util.List;

public class OrderListPanel extends Composite {

    @FunctionalInterface
    public interface OrdersChangedHandler {
        void onOrdersChanged(List<OrderRpcDto> orders);
    }

    private static final String FILTER_ALL = "ALL";

    private final OrdersRpcServiceAsync ordersService;
    private final OrdersChangedHandler ordersChangedHandler;
    private final Label resultCount = new Label("Loading orders");
    private final TextBox searchBox = new TextBox();
    private final Label noticeLabel = new Label();
    private final FlowPanel orderList = new FlowPanel();
    private final FlowPanel filters = new FlowPanel();
    private List<OrderRpcDto> orders = new ArrayList<>();
    private String selectedFilter = FILTER_ALL;

    public OrderListPanel(OrdersRpcServiceAsync ordersService, OrdersChangedHandler ordersChangedHandler) {
        this.ordersService = ordersService;
        this.ordersChangedHandler = ordersChangedHandler;
        initWidget(build());
    }

    public void refresh() {
        resultCount.setText("Syncing queue...");
        ordersService.listOrders(new AsyncCallback<List<OrderRpcDto>>() {
            @Override
            public void onSuccess(List<OrderRpcDto> result) {
                orders = result == null ? new ArrayList<>() : result;
                ordersChangedHandler.onOrdersChanged(new ArrayList<>(orders));
                render();
            }

            @Override
            public void onFailure(Throwable caught) {
                resultCount.setText("Queue unavailable");
                orderList.clear();
                FlowPanel error = Ui.panel("empty-state error-state");
                error.add(Ui.label("We could not reach the order service.", "empty-title"));
                error.add(Ui.label(Ui.errorMessage(caught), "empty-copy"));
                orderList.add(error);
            }
        });
    }

    public void showFilter(String filter) {
        selectedFilter = filter;
        buildFilters();
        render();
    }

    public void showAllAndRefresh() {
        selectedFilter = FILTER_ALL;
        buildFilters();
        refresh();
    }

    private FlowPanel build() {
        FlowPanel board = Ui.panel("panel board-panel");
        FlowPanel heading = Ui.panel("board-heading");
        FlowPanel title = Ui.panel("panel-heading-copy");
        title.add(Ui.label("02  /  WORK QUEUE", "eyebrow"));
        title.add(Ui.label("Orders", "panel-title"));
        heading.add(title);
        resultCount.setStyleName("result-count");
        heading.add(resultCount);
        board.add(heading);

        FlowPanel toolbar = Ui.panel("board-toolbar");
        searchBox.setStyleName("text-input search-input");
        searchBox.getElement().setAttribute("placeholder", "Search customer or order");
        searchBox.getElement().setAttribute("aria-label", "Search orders");
        searchBox.addKeyUpHandler(event -> render());
        toolbar.add(searchBox);
        buildFilters();
        toolbar.add(filters);
        board.add(toolbar);
        orderList.setStyleName("order-list");
        board.add(orderList);
        noticeLabel.setStyleName("notice");
        noticeLabel.getElement().setAttribute("aria-live", "polite");
        board.add(noticeLabel);
        return board;
    }

    private void buildFilters() {
        filters.clear();
        filters.setStyleName("filter-group");
        addFilter("All", FILTER_ALL);
        addFilter("Created", "CREATED");
        addFilter("Approved", "APPROVED");
        addFilter("Sent", "SENT");
    }

    private void addFilter(String title, String value) {
        Button filter = new Button(title);
        filter.setStyleName("filter-button" + (selectedFilter.equals(value) ? " selected" : ""));
        filter.addClickHandler(event -> showFilter(value));
        filters.add(filter);
    }

    private void render() {
        orderList.clear();
        List<OrderRpcDto> visible = new ArrayList<>();
        String query = searchBox.getText().trim().toLowerCase();
        for (OrderRpcDto order : orders) {
            if (!FILTER_ALL.equals(selectedFilter) && !selectedFilter.equals(order.getStatus())) {
                continue;
            }
            String searchable = (order.getCustomerName() + " " + order.getDescription() + " " + order.getId()).toLowerCase();
            if (query.isEmpty() || searchable.contains(query)) {
                visible.add(order);
            }
        }
        resultCount.setText(visible.size() + (visible.size() == 1 ? " order" : " orders"));
        if (visible.isEmpty()) {
            FlowPanel empty = Ui.panel("empty-state");
            empty.add(Ui.label(orders.isEmpty() ? "The queue is clear." : "No matching orders.", "empty-title"));
            empty.add(Ui.label(orders.isEmpty() ? "Create an order to see it move through the workflow."
                    : "Try another status or search term.", "empty-copy"));
            orderList.add(empty);
            return;
        }
        for (OrderRpcDto order : visible) {
            orderList.add(orderCard(order));
        }
    }

    private FlowPanel orderCard(OrderRpcDto order) {
        String status = order.getStatus();
        FlowPanel card = Ui.panel("order-card status-" + status.toLowerCase());
        FlowPanel main = Ui.panel("order-main");
        FlowPanel identity = Ui.panel("order-identity");
        identity.add(Ui.label("ORDER " + order.getId(), "order-reference"));
        identity.add(Ui.label(order.getCustomerName(), "order-customer"));
        main.add(identity);
        main.add(Ui.label(order.getDescription() == null || order.getDescription().trim().isEmpty()
                ? "No description" : order.getDescription(), "order-description"));
        main.add(progress(status));
        card.add(main);

        FlowPanel side = Ui.panel("order-side");
        side.add(statusBadge(status));
        side.add(Ui.label(orderDetails(order), "order-details"));
        if ("CREATED".equals(status)) {
            Button approve = new Button("Approve order");
            approve.setStyleName("button button-small button-dark");
            approve.addClickHandler(event -> approve(order.getId()));
            side.add(approve);
        } else if ("APPROVED".equals(status)) {
            Button send = new Button("Mark as sent");
            send.setStyleName("button button-small button-accent");
            send.addClickHandler(event -> send(order.getId()));
            side.add(send);
        } else {
            side.add(Ui.label("Workflow complete", "completion-note"));
        }
        card.add(side);
        return card;
    }

    private FlowPanel progress(String status) {
        FlowPanel progress = Ui.panel("progress-track");
        addProgressStep(progress, "Created", true);
        addProgressStep(progress, "Approved", "APPROVED".equals(status) || "SENT".equals(status));
        addProgressStep(progress, "Sent", "SENT".equals(status));
        return progress;
    }

    private void addProgressStep(FlowPanel progress, String title, boolean complete) {
        FlowPanel step = Ui.panel("progress-step" + (complete ? " complete" : ""));
        step.add(Ui.label(title, "progress-label"));
        progress.add(step);
    }

    private Label statusBadge(String status) {
        String text = "CREATED".equals(status) ? "Needs approval"
                : "APPROVED".equals(status) ? "Ready to send" : "Sent";
        return Ui.label(text, "status-badge badge-" + status.toLowerCase());
    }

    private String orderDetails(OrderRpcDto order) {
        if (order.getItems() == null || order.getItems().isEmpty()) {
            return "No line items";
        }
        OrderItemRpcDto item = order.getItems().get(0);
        double total = item.getQuantity() * item.getUnitPrice();
        return item.getQuantity() + " x " + item.getProductName() + "  /  $" + Ui.formatMoney(total);
    }

    private void approve(long id) {
        setActionsEnabled(false);
        ordersService.approveOrder(id, actionCallback(id, "approved"));
    }

    private void send(long id) {
        setActionsEnabled(false);
        ordersService.sendOrder(id, actionCallback(id, "sent"));
    }

    private AsyncCallback<OrderRpcDto> actionCallback(long id, String action) {
        return new AsyncCallback<OrderRpcDto>() {
            @Override
            public void onSuccess(OrderRpcDto result) {
                setActionsEnabled(true);
                showNotice("Order #" + id + " " + action + ".", false);
                refresh();
            }

            @Override
            public void onFailure(Throwable caught) {
                setActionsEnabled(true);
                showNotice("Could not update order #" + id + ": " + Ui.errorMessage(caught), true);
            }
        };
    }

    private void setActionsEnabled(boolean enabled) {
        for (int row = 0; row < orderList.getWidgetCount(); row++) {
            FlowPanel card = (FlowPanel) orderList.getWidget(row);
            FlowPanel side = (FlowPanel) card.getWidget(1);
            for (int child = 0; child < side.getWidgetCount(); child++) {
                if (side.getWidget(child) instanceof Button) {
                    ((Button) side.getWidget(child)).setEnabled(enabled);
                }
            }
        }
    }

    private void showNotice(String message, boolean error) {
        noticeLabel.setText(message);
        noticeLabel.setStyleName("notice" + (error ? " notice-error" : " notice-success"));
    }
}
