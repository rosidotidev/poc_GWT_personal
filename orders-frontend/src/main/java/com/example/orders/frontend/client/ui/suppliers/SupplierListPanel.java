package com.example.orders.frontend.client.ui.suppliers;

import com.example.orders.frontend.client.rpc.SupplierRpcDto;
import com.example.orders.frontend.client.ui.Ui;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

import java.util.List;

public class SupplierListPanel extends Composite {

    @FunctionalInterface
    public interface EditHandler {
        void edit(SupplierRpcDto supplier);
    }

    @FunctionalInterface
    public interface ActiveHandler {
        void setActive(SupplierRpcDto supplier, boolean active);
    }

    private final EditHandler editHandler;
    private final ActiveHandler activeHandler;
    private final Label count = new Label("Loading suppliers");
    private final Label status = new Label();
    private final FlowPanel list = new FlowPanel();

    public SupplierListPanel(EditHandler editHandler, ActiveHandler activeHandler) {
        this.editHandler = editHandler;
        this.activeHandler = activeHandler;
        initWidget(build());
    }

    public void render(List<SupplierRpcDto> suppliers) {
        list.clear();
        count.setText(suppliers.size() + (suppliers.size() == 1 ? " supplier" : " suppliers"));
        if (suppliers.isEmpty()) {
            FlowPanel empty = Ui.panel("empty-state product-empty");
            empty.add(Ui.label("Your supplier network starts here.", "empty-title"));
            empty.add(Ui.label("Add a supplier and keep its contacts ready for the next order.", "empty-copy"));
            list.add(empty);
            return;
        }
        for (SupplierRpcDto supplier : suppliers) {
            list.add(card(supplier));
        }
    }

    public void showError(Throwable caught) {
        count.setText("Directory unavailable");
        list.clear();
        FlowPanel error = Ui.panel("empty-state error-state");
        error.add(Ui.label("We could not reach the supplier directory.", "empty-title"));
        error.add(Ui.label(Ui.errorMessage(caught), "empty-copy"));
        list.add(error);
    }

    public void showStatus(String message, boolean isError) {
        status.setText(message);
        status.setStyleName("supplier-action-status" + (isError ? " supplier-action-error" : ""));
        status.setVisible(true);
    }

    private FlowPanel build() {
        FlowPanel directory = Ui.panel("panel catalog-panel");
        FlowPanel heading = Ui.panel("board-heading");
        FlowPanel title = Ui.panel("panel-heading-copy");
        title.add(Ui.label("YOUR NETWORK", "eyebrow"));
        title.add(Ui.label("Suppliers", "panel-title"));
        heading.add(title);
        count.setStyleName("result-count");
        heading.add(count);
        directory.add(heading);
        status.setStyleName("supplier-action-status");
        status.setVisible(false);
        directory.add(status);
        list.setStyleName("catalog-list");
        directory.add(list);
        return directory;
    }

    private FlowPanel card(SupplierRpcDto supplier) {
        FlowPanel card = Ui.panel("catalog-product" + (supplier.isActive() ? "" : " product-inactive"));
        FlowPanel details = Ui.panel("catalog-product-main");
        FlowPanel identity = Ui.panel("catalog-identity");
        identity.add(Ui.label(supplier.getCode(), "catalog-sku"));
        identity.add(Ui.label(supplier.isActive() ? "Active" : "Paused",
                "catalog-state" + (supplier.isActive() ? " catalog-state-active" : "")));
        details.add(identity);
        details.add(Ui.label(supplier.getName(), "catalog-product-name"));
        details.add(Ui.label(supplier.getContactName() + "  /  " + supplier.getEmail() + "  /  " + supplier.getPhone(),
                "catalog-product-description"));
        card.add(details);

        FlowPanel controls = Ui.panel("catalog-product-controls");
        FlowPanel actions = Ui.panel("catalog-actions");
        Button edit = new Button("Edit");
        edit.setStyleName("catalog-action");
        edit.addClickHandler(event -> editHandler.edit(supplier));
        Button toggle = new Button(supplier.isActive() ? "Pause" : "Activate");
        toggle.setStyleName("catalog-action");
        toggle.addClickHandler(event -> activeHandler.setActive(supplier, !supplier.isActive()));
        actions.add(edit);
        actions.add(toggle);
        controls.add(actions);
        card.add(controls);
        return card;
    }
}
