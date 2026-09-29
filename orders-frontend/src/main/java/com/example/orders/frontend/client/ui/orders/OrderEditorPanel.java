package com.example.orders.frontend.client.ui.orders;

import com.example.orders.frontend.client.rpc.CreateOrderRpcRequest;
import com.example.orders.frontend.client.rpc.OrderItemRpcDto;
import com.example.orders.frontend.client.rpc.OrderRpcDto;
import com.example.orders.frontend.client.rpc.OrdersRpcServiceAsync;
import com.example.orders.frontend.client.rpc.ProductRpcDto;
import com.example.orders.frontend.client.ui.Ui;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

import java.util.ArrayList;
import java.util.List;

public class OrderEditorPanel extends Composite {

    private final OrdersRpcServiceAsync ordersService;
    private final Runnable orderCreated;
    private final Runnable cancelHandler;
    private final TextBox customerNameBox = new TextBox();
    private final TextBox descriptionBox = new TextBox();
    private final TextBox quantityBox = new TextBox();
    private final TextBox productSearchBox = new TextBox();
    private final FlowPanel productSearchResults = new FlowPanel();
    private final Label selectedProductMeta = new Label("Type at least 3 letters, or ** for the first 20 products");
    private final Label noticeLabel = new Label();
    private final Button createButton = new Button("Create order");
    private ProductRpcDto selectedProduct;
    private int searchSequence;

    public OrderEditorPanel(OrdersRpcServiceAsync ordersService, Runnable orderCreated, Runnable cancelHandler) {
        this.ordersService = ordersService;
        this.orderCreated = orderCreated;
        this.cancelHandler = cancelHandler;
        initWidget(build());
    }

    public void beginCreate() {
        clearForm();
        customerNameBox.setFocus(true);
    }

    private FlowPanel build() {
        FlowPanel panel = Ui.panel("panel composer-panel");
        FlowPanel heading = Ui.panel("panel-heading");
        FlowPanel copy = Ui.panel("panel-heading-copy");
        copy.add(Ui.label("01  /  INTAKE", "eyebrow"));
        copy.add(Ui.label("Create an order", "panel-title"));
        heading.add(copy);
        heading.add(Ui.label("NEW", "panel-tag"));
        panel.add(heading);
        panel.add(Ui.label("Start with the customer, then add the first line item.", "panel-description"));
        panel.add(Ui.field("Customer name", customerNameBox, "e.g. Northstar Studio", "field-full"));
        panel.add(Ui.field("Order description", descriptionBox, "What is this order for?", "field-full"));

        FlowPanel productField = Ui.panel("form-field field-full product-search-field");
        productField.add(Ui.label("Catalog product", "field-label"));
        productSearchBox.setStyleName("text-input product-search-input");
        productSearchBox.getElement().setAttribute("placeholder", "Search product name or type **...");
        productSearchBox.getElement().setAttribute("aria-label", "Search catalog product");
        productSearchBox.getElement().setAttribute("autocomplete", "off");
        productSearchBox.addKeyUpHandler(event -> searchProducts());
        FlowPanel searchControl = Ui.panel("product-search-control");
        searchControl.add(productSearchBox);
        productSearchResults.setStyleName("product-search-results");
        productSearchResults.getElement().setAttribute("aria-live", "polite");
        searchControl.add(productSearchResults);
        productField.add(searchControl);
        selectedProductMeta.setStyleName("selected-product-meta");
        productField.add(selectedProductMeta);
        panel.add(productField);

        FlowPanel quantityRow = Ui.panel("quantity-row");
        quantityRow.add(Ui.field("Quantity", quantityBox, "1", "field-quantity"));
        panel.add(quantityRow);
        createButton.setStyleName("button button-accent create-button");
        createButton.addClickHandler(event -> createOrder());
        panel.add(createButton);
        Button cancel = new Button("Cancel");
        cancel.setStyleName("button button-quiet cancel-edit-button");
        cancel.addClickHandler(event -> {
            clearForm();
            cancelHandler.run();
        });
        panel.add(cancel);
        noticeLabel.setStyleName("notice");
        noticeLabel.getElement().setAttribute("aria-live", "polite");
        panel.add(noticeLabel);
        return panel;
    }

    private void createOrder() {
        String customer = customerNameBox.getText().trim();
        String description = descriptionBox.getText().trim();
        if (customer.isEmpty() || selectedProduct == null) {
            showNotice("Add a customer and choose a catalog product to continue.", true);
            return;
        }
        int quantity;
        try {
            quantity = Integer.parseInt(quantityBox.getText().trim());
            if (quantity < 1) {
                showNotice("Quantity must be at least 1.", true);
                return;
            }
        } catch (NumberFormatException e) {
            showNotice("Enter a whole-number quantity.", true);
            return;
        }

        OrderItemRpcDto item = new OrderItemRpcDto();
        item.setProductId(selectedProduct.getId());
        item.setProductName(selectedProduct.getName());
        item.setQuantity(quantity);
        item.setUnitPrice(selectedProduct.getUnitPrice());
        CreateOrderRpcRequest request = new CreateOrderRpcRequest();
        request.setCustomerName(customer);
        request.setDescription(description);
        request.getItems().add(item);

        createButton.setEnabled(false);
        createButton.setText("Creating...");
        ordersService.createOrder(request, new AsyncCallback<OrderRpcDto>() {
            @Override
            public void onSuccess(OrderRpcDto result) {
                createButton.setEnabled(true);
                createButton.setText("Create order");
                showNotice("Order #" + result.getId() + " is now in the queue.", false);
                clearForm();
                orderCreated.run();
            }

            @Override
            public void onFailure(Throwable caught) {
                createButton.setEnabled(true);
                createButton.setText("Create order");
                showNotice("Could not create order: " + Ui.errorMessage(caught), true);
            }
        });
    }

    private void searchProducts() {
        selectedProduct = null;
        selectedProductMeta.setText("Choose a result from the list");
        productSearchResults.clear();
        String query = productSearchBox.getText().trim();
        int requestSequence = ++searchSequence;
        if (!"**".equals(query) && query.length() < 3) {
            selectedProductMeta.setText("Type at least 3 letters, or ** for the first 20 products");
            return;
        }
        productSearchResults.add(Ui.label("Searching...", "product-search-status"));
        ordersService.searchActiveProductsByName(query, new AsyncCallback<List<ProductRpcDto>>() {
            @Override
            public void onSuccess(List<ProductRpcDto> result) {
                if (requestSequence == searchSequence) {
                    renderResults(result == null ? new ArrayList<>() : result);
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                if (requestSequence == searchSequence) {
                    productSearchResults.clear();
                    productSearchResults.add(Ui.label("Catalog search unavailable", "product-search-status product-search-error"));
                }
            }
        });
    }

    private void renderResults(List<ProductRpcDto> products) {
        productSearchResults.clear();
        if (products.isEmpty()) {
            productSearchResults.add(Ui.label("No matching active products", "product-search-status"));
            return;
        }
        for (ProductRpcDto product : products) {
            Button result = new Button();
            result.setStyleName("product-search-result");
            result.getElement().setInnerHTML("<span class=\"product-search-result-name\">" + Ui.escapeHtml(product.getName())
                    + "</span><span class=\"product-search-result-detail\">" + Ui.escapeHtml(product.getSku())
                    + " &nbsp; / &nbsp; $" + Ui.formatMoney(product.getUnitPrice()) + "</span>");
            result.addClickHandler(event -> selectProduct(product));
            productSearchResults.add(result);
        }
    }

    private void selectProduct(ProductRpcDto product) {
        selectedProduct = product;
        productSearchBox.setText(product.getName());
        productSearchResults.clear();
        selectedProductMeta.setText(product.getDescription() + "  /  Unit price $" + Ui.formatMoney(product.getUnitPrice()));
    }

    private void clearForm() {
        customerNameBox.setText("");
        descriptionBox.setText("");
        quantityBox.setText("");
        productSearchBox.setText("");
        selectedProduct = null;
        productSearchResults.clear();
        selectedProductMeta.setText("Type at least 3 letters, or ** for the first 20 products");
    }

    private void showNotice(String message, boolean error) {
        noticeLabel.setText(message);
        noticeLabel.setStyleName("notice" + (error ? " notice-error" : " notice-success"));
    }
}
