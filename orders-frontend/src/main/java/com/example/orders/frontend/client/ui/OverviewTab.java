package com.example.orders.frontend.client.ui;

import com.example.orders.frontend.client.rpc.OrderRpcDto;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

import java.util.List;

public class OverviewTab extends Composite {

    @FunctionalInterface
    public interface StatusHandler {
        void show(String status);
    }

    private final Label totalValue = new Label("-");
    private final Label activeValue = new Label("-");
    private final Label sentValue = new Label("-");
    private final Label createdLaneValue = new Label("-");
    private final Label approvedLaneValue = new Label("-");
    private final Label sentLaneValue = new Label("-");

    public OverviewTab(Runnable openOrder, Runnable refreshOrders, StatusHandler statusHandler) {
        FlowPanel root = Ui.panel("page-view overview-view");
        Button newOrder = new Button("New order");
        newOrder.setStyleName("button button-dark");
        newOrder.addClickHandler(event -> openOrder.run());
        Button refresh = new Button("Refresh");
        refresh.setStyleName("button button-quiet");
        refresh.addClickHandler(event -> refreshOrders.run());
        root.add(Ui.hero("DAILY OPERATIONS", "Your orders, at a glance.",
                "A clear view of what is moving, what needs attention and what is complete.", newOrder, refresh));
        root.add(buildSummary());

        FlowPanel grid = Ui.panel("overview-grid");
        FlowPanel lanes = Ui.panel("panel lane-panel");
        FlowPanel heading = Ui.panel("panel-heading-copy");
        heading.add(Ui.label("LIVE WORKFLOW", "eyebrow"));
        heading.add(Ui.label("Where work stands", "panel-title"));
        lanes.add(heading);
        lanes.add(lane("01", "Needs approval", createdLaneValue, "lane-created", "CREATED", statusHandler));
        lanes.add(lane("02", "Ready to send", approvedLaneValue, "lane-approved", "APPROVED", statusHandler));
        lanes.add(lane("03", "Completed", sentLaneValue, "lane-sent", "SENT", statusHandler));
        grid.add(lanes);

        FlowPanel start = Ui.panel("panel start-panel");
        start.add(Ui.label("A GOOD NEXT STEP", "eyebrow"));
        start.add(Ui.label("Make a new order", "panel-title"));
        start.add(Ui.label("Capture the customer and first line item. The order will appear in the work queue ready for review.", "panel-description"));
        Button startButton = new Button("Start an order");
        startButton.setStyleName("button button-accent");
        startButton.addClickHandler(event -> openOrder.run());
        start.add(startButton);
        start.add(Ui.label("Created  ->  Approved  ->  Sent", "workflow-note"));
        grid.add(start);
        root.add(grid);
        initWidget(root);
    }

    public void setOrders(List<OrderRpcDto> orders) {
        int created = 0;
        int approved = 0;
        int sent = 0;
        for (OrderRpcDto order : orders) {
            if ("CREATED".equals(order.getStatus())) {
                created++;
            } else if ("APPROVED".equals(order.getStatus())) {
                approved++;
            } else if ("SENT".equals(order.getStatus())) {
                sent++;
            }
        }
        totalValue.setText(String.valueOf(orders.size()));
        activeValue.setText(String.valueOf(created + approved));
        sentValue.setText(String.valueOf(sent));
        createdLaneValue.setText(String.valueOf(created));
        approvedLaneValue.setText(String.valueOf(approved));
        sentLaneValue.setText(String.valueOf(sent));
    }

    private FlowPanel buildSummary() {
        FlowPanel summary = Ui.panel("summary-strip");
        summary.add(summaryItem("ORDERS IN VIEW", totalValue, "summary-total"));
        summary.add(summaryItem("NEEDS A NEXT STEP", activeValue, "summary-active"));
        summary.add(summaryItem("COMPLETED", sentValue, "summary-sent"));
        return summary;
    }

    private FlowPanel summaryItem(String title, Label value, String extraClass) {
        FlowPanel item = Ui.panel("summary-item " + extraClass);
        item.add(Ui.label(title, "summary-label"));
        value.setStyleName("summary-value");
        item.add(value);
        return item;
    }

    private FlowPanel lane(String number, String title, Label count, String style, String status,
                           StatusHandler statusHandler) {
        FlowPanel lane = Ui.panel("lane-row " + style);
        FlowPanel titleGroup = Ui.panel("lane-title-group");
        titleGroup.add(Ui.label(number, "lane-number"));
        titleGroup.add(Ui.label(title, "lane-title"));
        lane.add(titleGroup);
        count.setStyleName("lane-count");
        lane.add(count);
        Button view = new Button("View");
        view.setStyleName("lane-view");
        view.addClickHandler(event -> statusHandler.show(status));
        lane.add(view);
        return lane;
    }
}
