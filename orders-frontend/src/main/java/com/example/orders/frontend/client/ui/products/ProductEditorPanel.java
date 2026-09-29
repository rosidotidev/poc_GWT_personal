package com.example.orders.frontend.client.ui.products;

import com.example.orders.frontend.client.rpc.ProductRpcDto;
import com.example.orders.frontend.client.rpc.OrdersRpcServiceAsync;
import com.example.orders.frontend.client.rpc.SupplierRpcDto;
import com.example.orders.frontend.client.ui.Ui;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

import java.util.ArrayList;
import java.util.List;

public class ProductEditorPanel extends Composite {

    @FunctionalInterface
    public interface SaveHandler {
        void save(ProductRpcDto product);
    }

    private final OrdersRpcServiceAsync ordersService;
    private final SaveHandler saveHandler;
    private final Runnable cancelHandler;
    private final TextBox skuBox = new TextBox();
    private final TextBox nameBox = new TextBox();
    private final TextBox descriptionBox = new TextBox();
    private final TextBox priceBox = new TextBox();
    private final TextBox supplierSearchBox = new TextBox();
    private final FlowPanel supplierSearchResults = new FlowPanel();
    private final FlowPanel selectedSuppliers = new FlowPanel();
    private final Button saveButton = new Button("Add product");
    private final Button cancelButton = new Button("Cancel edit");
    private final Label notice = new Label();
    private final Label title = new Label("Add a product");
    private final Label tag = new Label("NEW ITEM");
    private List<SupplierRpcDto> suppliers = new ArrayList<>();
    private List<Long> selectedSupplierIds = new ArrayList<>();
    private long editingId;
    private int searchSequence;

    public ProductEditorPanel(OrdersRpcServiceAsync ordersService, SaveHandler saveHandler, Runnable cancelHandler) {
        this.ordersService = ordersService;
        this.saveHandler = saveHandler;
        this.cancelHandler = cancelHandler;
        initWidget(build());
    }

    public void beginCreate() {
        reset();
    }

    public void setSuppliers(List<SupplierRpcDto> suppliers) {
        this.suppliers = new ArrayList<>(suppliers);
        renderPicker();
    }

    public void edit(ProductRpcDto product) {
        editingId = product.getId();
        skuBox.setText(product.getSku());
        nameBox.setText(product.getName());
        descriptionBox.setText(product.getDescription());
        priceBox.setText(Ui.formatMoney(product.getUnitPrice()));
        selectedSupplierIds = new ArrayList<>(product.getSupplierIds());
        supplierSearchBox.setText("");
        renderPicker();
        title.setText("Edit product");
        tag.setText("EDITING");
        saveButton.setText("Save changes");
        cancelButton.setVisible(true);
        Window.scrollTo(0, 0);
        skuBox.setFocus(true);
    }

    public void focus() {
        skuBox.setFocus(true);
    }

    public void saved(ProductRpcDto product) {
        saveButton.setEnabled(true);
        showNotice(product.getName() + " saved to the catalog.", false);
        reset();
    }

    public void saveFailed(Throwable caught) {
        saveButton.setEnabled(true);
        saveButton.setText(editingId == 0 ? "Add product" : "Save changes");
        showNotice("Could not save product: " + Ui.errorMessage(caught), true);
    }

    public void showStatus(String message, boolean error) {
        showNotice(message, error);
    }

    private FlowPanel build() {
        FlowPanel editor = Ui.panel("panel product-editor");
        FlowPanel heading = Ui.panel("panel-heading");
        FlowPanel copy = Ui.panel("panel-heading-copy");
        copy.add(Ui.label("THE CATALOG", "eyebrow"));
        title.setStyleName("panel-title");
        copy.add(title);
        heading.add(copy);
        tag.setStyleName("panel-tag");
        heading.add(tag);
        editor.add(heading);
        editor.add(Ui.label("Give it a memorable name, a short note and a price.", "panel-description"));
        editor.add(Ui.field("SKU / short code", skuBox, "e.g. MUG-014", "field-full"));
        editor.add(Ui.field("Product name", nameBox, "What do you call it?", "field-full"));
        editor.add(Ui.field("Description", descriptionBox, "A little detail goes a long way", "field-full"));
        editor.add(Ui.field("Unit price", priceBox, "0.00", "field-full"));

        FlowPanel supplierField = Ui.panel("form-field field-full");
        supplierField.add(Ui.label("Suppliers", "field-label"));
        FlowPanel searchControl = Ui.panel("supplier-search-control");
        supplierSearchBox.setStyleName("text-input supplier-search-input");
        supplierSearchBox.getElement().setAttribute("placeholder", "Search supplier or type ** for the first 20...");
        supplierSearchBox.getElement().setAttribute("aria-label", "Search product suppliers");
        supplierSearchBox.getElement().setAttribute("autocomplete", "off");
        supplierSearchBox.addKeyUpHandler(event -> renderSearchResults());
        searchControl.add(supplierSearchBox);
        supplierSearchResults.setStyleName("supplier-search-results");
        supplierSearchResults.getElement().setAttribute("aria-live", "polite");
        searchControl.add(supplierSearchResults);
        supplierField.add(searchControl);
        selectedSuppliers.setStyleName("selected-suppliers");
        supplierField.add(selectedSuppliers);
        editor.add(supplierField);

        saveButton.setStyleName("button button-accent create-button");
        saveButton.addClickHandler(event -> submit());
        editor.add(saveButton);
        cancelButton.setStyleName("button button-quiet cancel-edit-button");
        cancelButton.setVisible(false);
        cancelButton.addClickHandler(event -> {
            reset();
            cancelHandler.run();
        });
        editor.add(cancelButton);
        notice.setStyleName("notice");
        notice.getElement().setAttribute("aria-live", "polite");
        editor.add(notice);
        return editor;
    }

    private void submit() {
        String sku = skuBox.getText().trim();
        String name = nameBox.getText().trim();
        if (sku.isEmpty() || name.isEmpty()) {
            showNotice("Add both a product code and a name.", true);
            return;
        }
        double price;
        try {
            price = Double.parseDouble(priceBox.getText().trim());
            if (price < 0) {
                showNotice("Price cannot be negative.", true);
                return;
            }
        } catch (NumberFormatException e) {
            showNotice("Enter a valid unit price.", true);
            return;
        }
        ProductRpcDto product = new ProductRpcDto();
        product.setId(editingId);
        product.setSku(sku);
        product.setName(name);
        product.setDescription(descriptionBox.getText().trim());
        product.setUnitPrice(price);
        product.setActive(true);
        product.setSupplierIds(new ArrayList<>(selectedSupplierIds));
        saveButton.setEnabled(false);
        saveButton.setText("Saving…");
        saveHandler.save(product);
    }

    private void renderPicker() {
        searchSequence++;
        supplierSearchResults.clear();
        selectedSuppliers.clear();
        if (selectedSupplierIds.isEmpty()) {
            selectedSuppliers.add(Ui.label("No suppliers selected", "selected-suppliers-empty"));
            return;
        }
        for (Long supplierId : new ArrayList<>(selectedSupplierIds)) {
            SupplierRpcDto supplier = supplierById(supplierId);
            if (supplier == null) {
                continue;
            }
            FlowPanel row = Ui.panel("selected-supplier");
            FlowPanel identity = Ui.panel("selected-supplier-identity");
            identity.add(Ui.label(supplier.getName(), "selected-supplier-name"));
            identity.add(Ui.label(supplier.getCode() + (supplier.isActive() ? "" : "  /  Paused"), "selected-supplier-code"));
            row.add(identity);
            Button remove = new Button("Remove");
            remove.setStyleName("selected-supplier-remove");
            remove.getElement().setAttribute("aria-label", "Remove " + supplier.getName());
            remove.addClickHandler(event -> {
                selectedSupplierIds.remove(Long.valueOf(supplier.getId()));
                renderPicker();
            });
            row.add(remove);
            selectedSuppliers.add(row);
        }
    }

    private void renderSearchResults() {
        supplierSearchResults.clear();
        String query = supplierSearchBox.getText().trim();
        int requestSequence = ++searchSequence;
        if (!"**".equals(query) && query.length() < 2) {
            return;
        }
        supplierSearchResults.add(Ui.label("Searching...", "supplier-search-status"));
        ordersService.searchActiveSuppliers(query, new AsyncCallback<List<SupplierRpcDto>>() {
            @Override
            public void onSuccess(List<SupplierRpcDto> result) {
                if (requestSequence == searchSequence) {
                    renderSupplierResults(result == null ? new ArrayList<>() : result);
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                if (requestSequence == searchSequence) {
                    supplierSearchResults.clear();
                    supplierSearchResults.add(Ui.label("Supplier search unavailable",
                            "supplier-search-status supplier-search-error"));
                }
            }
        });
    }

    private void renderSupplierResults(List<SupplierRpcDto> results) {
        supplierSearchResults.clear();
        int matches = 0;
        for (SupplierRpcDto supplier : results) {
            if (selectedSupplierIds.contains(supplier.getId())) {
                continue;
            }
            Button result = new Button(supplier.getName() + "  /  " + supplier.getCode());
            result.setStyleName("supplier-search-result");
            result.addClickHandler(event -> addSupplier(supplier));
            supplierSearchResults.add(result);
            matches++;
        }
        if (matches == 0) {
            supplierSearchResults.add(Ui.label("No matching active suppliers", "supplier-search-status"));
        }
    }

    private void addSupplier(SupplierRpcDto supplier) {
        if (!selectedSupplierIds.contains(supplier.getId())) {
            selectedSupplierIds.add(supplier.getId());
        }
        supplierSearchBox.setText("");
        renderPicker();
    }

    private SupplierRpcDto supplierById(long id) {
        for (SupplierRpcDto supplier : suppliers) {
            if (supplier.getId() == id) {
                return supplier;
            }
        }
        return null;
    }

    private void reset() {
        editingId = 0;
        skuBox.setText("");
        nameBox.setText("");
        descriptionBox.setText("");
        priceBox.setText("");
        supplierSearchBox.setText("");
        selectedSupplierIds = new ArrayList<>();
        renderPicker();
        title.setText("Add a product");
        tag.setText("NEW ITEM");
        saveButton.setText("Add product");
        cancelButton.setVisible(false);
    }

    private void showNotice(String message, boolean error) {
        notice.setText(message);
        notice.setStyleName("notice" + (error ? " notice-error" : " notice-success"));
    }
}
