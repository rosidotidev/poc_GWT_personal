package com.example.orders.frontend.client.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import java.util.List;

@RemoteServiceRelativePath("rpc/orders")
public interface OrdersRpcService extends RemoteService {

    List<OrderRpcDto> listOrders() throws OrderRpcException;

    OrderRpcDto getOrder(long id) throws OrderRpcException;

    OrderRpcDto createOrder(CreateOrderRpcRequest request) throws OrderRpcException;

    OrderRpcDto approveOrder(long id) throws OrderRpcException;

    OrderRpcDto sendOrder(long id) throws OrderRpcException;

    List<ProductRpcDto> listProducts() throws OrderRpcException;

    List<ProductRpcDto> searchActiveProductsByName(String name) throws OrderRpcException;

    ProductRpcDto saveProduct(ProductRpcDto product) throws OrderRpcException;

    ProductRpcDto setProductActive(long id, boolean active) throws OrderRpcException;

    List<SupplierRpcDto> listSuppliers() throws OrderRpcException;

    List<SupplierRpcDto> searchActiveSuppliers(String query) throws OrderRpcException;

    SupplierRpcDto setSupplierActive(long id, boolean active) throws OrderRpcException;
}