package com.example.orders.frontend.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

public interface OrdersRpcServiceAsync {

    void listOrders(AsyncCallback<List<OrderRpcDto>> callback);

    void getOrder(long id, AsyncCallback<OrderRpcDto> callback);

    void createOrder(CreateOrderRpcRequest request, AsyncCallback<OrderRpcDto> callback);

    void approveOrder(long id, AsyncCallback<OrderRpcDto> callback);

    void sendOrder(long id, AsyncCallback<OrderRpcDto> callback);

    void listProducts(AsyncCallback<List<ProductRpcDto>> callback);

    void searchActiveProductsByName(String name, AsyncCallback<List<ProductRpcDto>> callback);

    void saveProduct(ProductRpcDto product, AsyncCallback<ProductRpcDto> callback);

    void setProductActive(long id, boolean active, AsyncCallback<ProductRpcDto> callback);

    void listSuppliers(AsyncCallback<List<SupplierRpcDto>> callback);

    void searchActiveSuppliers(String query, AsyncCallback<List<SupplierRpcDto>> callback);

    void setSupplierActive(long id, boolean active, AsyncCallback<SupplierRpcDto> callback);
}