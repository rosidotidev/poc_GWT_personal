package com.example.orders.frontend.client.ui.suppliers;

import com.example.orders.frontend.client.rpc.OrdersRpcServiceAsync;
import com.example.orders.frontend.client.rpc.SupplierRpcDto;
import com.example.orders.frontend.client.ui.AppModal;
import com.example.orders.frontend.client.ui.Ui;
import com.example.orders.frontend.client.ui.angular.SupplierAngularEditorPanel;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

import java.util.ArrayList;
import java.util.List;

public class SuppliersTab extends Composite {

    @FunctionalInterface
    public interface SuppliersChangedHandler {
        void onSuppliersChanged(List<SupplierRpcDto> suppliers);
    }

    private final OrdersRpcServiceAsync ordersService;
    private final SuppliersChangedHandler changedHandler;
    private final SupplierListPanel supplierList;
    private final AppModal modal = new AppModal();
    private List<SupplierRpcDto> suppliers = new ArrayList<>();

    public SuppliersTab(OrdersRpcServiceAsync ordersService, SuppliersChangedHandler changedHandler) {
        this.ordersService = ordersService;
        this.changedHandler = changedHandler;
        supplierList = new SupplierListPanel(this::openEdit, this::setActive);

        FlowPanel root = Ui.panel("page-view suppliers-view");
        Button add = new Button("Add a supplier");
        add.setStyleName("button button-dark");
        add.addClickHandler(event -> openCreate());
        root.add(Ui.hero("WORKSPACE", "Partners behind the catalog.",
                "Keep supplier contacts current and pause partners without losing their history.", add));
        root.add(supplierList);
        initWidget(root);
    }

    public void refresh() {
        ordersService.listSuppliers(new AsyncCallback<List<SupplierRpcDto>>() {
            @Override
            public void onSuccess(List<SupplierRpcDto> result) {
                suppliers = result == null ? new ArrayList<>() : result;
                supplierList.render(suppliers);
                changedHandler.onSuppliersChanged(new ArrayList<>(suppliers));
            }

            @Override
            public void onFailure(Throwable caught) {
                supplierList.showError(caught);
            }
        });
    }

    private void setActive(SupplierRpcDto supplier, boolean active) {
        ordersService.setSupplierActive(supplier.getId(), active, new AsyncCallback<SupplierRpcDto>() {
            @Override
            public void onSuccess(SupplierRpcDto result) {
                supplierList.showStatus(result.getName() + (active ? " is active again." : " has been paused."), false);
                refresh();
            }

            @Override
            public void onFailure(Throwable caught) {
                supplierList.showStatus("Could not update supplier: " + Ui.errorMessage(caught), true);
            }
        });
    }

    private void openCreate() {
        openEditor(false, 0);
    }

    private void openEdit(SupplierRpcDto supplier) {
        openEditor(true, supplier.getId());
    }

    private void openEditor(boolean editMode, long supplierId) {
        SupplierAngularEditorPanel editor = new SupplierAngularEditorPanel(editMode, supplierId, () -> {
            modal.hide();
            refresh();
        }, modal::hide);
        modal.showContent(editor, () -> { }, editor::destroy);
    }
}
