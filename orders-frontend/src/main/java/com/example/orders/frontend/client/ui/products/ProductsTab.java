package com.example.orders.frontend.client.ui.products;

import com.example.orders.frontend.client.rpc.ProductRpcDto;
import com.example.orders.frontend.client.rpc.OrdersRpcServiceAsync;
import com.example.orders.frontend.client.rpc.SupplierRpcDto;
import com.example.orders.frontend.client.ui.AppModal;
import com.example.orders.frontend.client.ui.Ui;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

import java.util.ArrayList;
import java.util.List;

public class ProductsTab extends Composite {

    private final OrdersRpcServiceAsync ordersService;
    private final ProductEditorPanel editor;
    private final ProductListPanel productList;
    private final AppModal modal = new AppModal();
    private List<ProductRpcDto> products = new ArrayList<>();
    private List<SupplierRpcDto> suppliers = new ArrayList<>();

    public ProductsTab(OrdersRpcServiceAsync ordersService) {
        this.ordersService = ordersService;
        editor = new ProductEditorPanel(ordersService, this::save, modal::hide);
        productList = new ProductListPanel(this::openEdit, this::setActive);

        FlowPanel root = Ui.panel("page-view products-view");
        Button add = new Button("Add a product");
        add.setStyleName("button button-dark");
        add.addClickHandler(event -> openCreate());
        root.add(Ui.hero("WORKSPACE", "A catalog with character.",
                "Keep your favorite things, prices and availability in one tidy place.", add));
        root.add(productList);
        initWidget(root);
    }

    public void refresh() {
        ordersService.listProducts(new AsyncCallback<List<ProductRpcDto>>() {
            @Override
            public void onSuccess(List<ProductRpcDto> result) {
                products = result == null ? new ArrayList<>() : result;
                productList.render(products, suppliers);
            }

            @Override
            public void onFailure(Throwable caught) {
                productList.showError(caught);
            }
        });
    }

    public void setSuppliers(List<SupplierRpcDto> suppliers) {
        this.suppliers = new ArrayList<>(suppliers);
        editor.setSuppliers(this.suppliers);
        productList.render(products, this.suppliers);
    }

    private void save(ProductRpcDto product) {
        ordersService.saveProduct(product, new AsyncCallback<ProductRpcDto>() {
            @Override
            public void onSuccess(ProductRpcDto saved) {
                editor.saved(saved);
                modal.hide();
                refresh();
            }

            @Override
            public void onFailure(Throwable caught) {
                editor.saveFailed(caught);
            }
        });
    }

    private void setActive(ProductRpcDto product, boolean active) {
        ordersService.setProductActive(product.getId(), active, new AsyncCallback<ProductRpcDto>() {
            @Override
            public void onSuccess(ProductRpcDto result) {
                editor.showStatus(result.getName() + (active ? " is available for new orders." : " has been paused."), false);
                refresh();
            }

            @Override
            public void onFailure(Throwable caught) {
                editor.showStatus("Could not update product: " + Ui.errorMessage(caught), true);
            }
        });
    }

    private void openCreate() {
        modal.showContent(editor, () -> {
            editor.beginCreate();
            editor.focus();
        });
    }

    private void openEdit(ProductRpcDto product) {
        modal.showContent(editor, () -> editor.edit(product));
    }
}
