package com.example.orders.frontend.client.ui.products;

import com.example.orders.frontend.client.rpc.ProductRpcDto;
import com.example.orders.frontend.client.rpc.SupplierRpcDto;
import com.example.orders.frontend.client.ui.Ui;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

import java.util.ArrayList;
import java.util.List;

public class ProductListPanel extends Composite {

    @FunctionalInterface
    public interface EditHandler {
        void edit(ProductRpcDto product);
    }

    @FunctionalInterface
    public interface ActiveHandler {
        void setActive(ProductRpcDto product, boolean active);
    }

    private final EditHandler editHandler;
    private final ActiveHandler activeHandler;
    private final Label count = new Label("Loading products");
    private final FlowPanel list = new FlowPanel();

    public ProductListPanel(EditHandler editHandler, ActiveHandler activeHandler) {
        this.editHandler = editHandler;
        this.activeHandler = activeHandler;
        initWidget(build());
    }

    public void render(List<ProductRpcDto> products, List<SupplierRpcDto> suppliers) {
        list.clear();
        count.setText(products.size() + (products.size() == 1 ? " product" : " products"));
        if (products.isEmpty()) {
            FlowPanel empty = Ui.panel("empty-state product-empty");
            empty.add(Ui.label("Your catalog starts here.", "empty-title"));
            empty.add(Ui.label("Add a product once and use it in every new order.", "empty-copy"));
            list.add(empty);
            return;
        }
        for (ProductRpcDto product : products) {
            list.add(card(product, suppliers));
        }
    }

    public void showError(Throwable caught) {
        count.setText("Catalog unavailable");
        list.clear();
        FlowPanel error = Ui.panel("empty-state error-state");
        error.add(Ui.label("We could not reach the catalog.", "empty-title"));
        error.add(Ui.label(Ui.errorMessage(caught), "empty-copy"));
        list.add(error);
    }

    private FlowPanel build() {
        FlowPanel catalog = Ui.panel("panel catalog-panel");
        FlowPanel heading = Ui.panel("board-heading");
        FlowPanel title = Ui.panel("panel-heading-copy");
        title.add(Ui.label("YOUR LINEUP", "eyebrow"));
        title.add(Ui.label("Products", "panel-title"));
        heading.add(title);
        count.setStyleName("result-count");
        heading.add(count);
        catalog.add(heading);
        list.setStyleName("catalog-list");
        catalog.add(list);
        return catalog;
    }

    private FlowPanel card(ProductRpcDto product, List<SupplierRpcDto> suppliers) {
        FlowPanel card = Ui.panel("catalog-product" + (product.isActive() ? "" : " product-inactive"));
        FlowPanel details = Ui.panel("catalog-product-main");
        FlowPanel identity = Ui.panel("catalog-identity");
        identity.add(Ui.label(product.getSku(), "catalog-sku"));
        identity.add(Ui.label(product.isActive() ? "Available" : "Paused",
                "catalog-state" + (product.isActive() ? " catalog-state-active" : "")));
        details.add(identity);
        details.add(Ui.label(product.getName(), "catalog-product-name"));
        details.add(Ui.label(product.getDescription(), "catalog-product-description"));
        details.add(Ui.label(supplierNames(product, suppliers), "catalog-product-suppliers"));
        card.add(details);

        FlowPanel controls = Ui.panel("catalog-product-controls");
        controls.add(Ui.label("$" + Ui.formatMoney(product.getUnitPrice()), "catalog-price"));
        FlowPanel actions = Ui.panel("catalog-actions");
        Button edit = new Button("Edit");
        edit.setStyleName("catalog-action");
        edit.addClickHandler(event -> editHandler.edit(product));
        Button toggle = new Button(product.isActive() ? "Pause" : "Activate");
        toggle.setStyleName("catalog-action");
        toggle.addClickHandler(event -> activeHandler.setActive(product, !product.isActive()));
        actions.add(edit);
        actions.add(toggle);
        controls.add(actions);
        card.add(controls);
        return card;
    }

    private String supplierNames(ProductRpcDto product, List<SupplierRpcDto> suppliers) {
        List<String> names = new ArrayList<>();
        for (Long supplierId : product.getSupplierIds()) {
            for (SupplierRpcDto supplier : suppliers) {
                if (supplier.getId() == supplierId) {
                    names.add(supplier.getName());
                    break;
                }
            }
        }
        return names.isEmpty() ? "No suppliers assigned" : "Suppliers: " + String.join(", ", names);
    }
}
