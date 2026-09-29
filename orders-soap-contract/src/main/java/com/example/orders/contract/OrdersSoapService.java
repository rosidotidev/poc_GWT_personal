package com.example.orders.contract;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;

import java.util.List;

@WebService(name = "OrdersSoapPort", targetNamespace = OrdersSoapService.NAMESPACE)
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface OrdersSoapService {

    String NAMESPACE = "urn:example:orders:soap";

    @WebMethod(operationName = "listOrders")
    @WebResult(name = "orders")
    List<OrderSoapDto> listOrders();

    @WebMethod(operationName = "getOrder")
    @WebResult(name = "order")
    OrderSoapDto getOrder(@WebParam(name = "id") long id) throws OrderNotFoundFault;

    @WebMethod(operationName = "createOrder")
    @WebResult(name = "order")
    OrderSoapDto createOrder(@WebParam(name = "request") CreateOrderSoapRequest request);

    @WebMethod(operationName = "approveOrder")
    @WebResult(name = "order")
    OrderSoapDto approveOrder(@WebParam(name = "id") long id)
            throws OrderNotFoundFault, InvalidOrderStateFault;

    @WebMethod(operationName = "sendOrder")
    @WebResult(name = "order")
    OrderSoapDto sendOrder(@WebParam(name = "id") long id)
            throws OrderNotFoundFault, InvalidOrderStateFault;

    @WebMethod(operationName = "listProducts")
    @WebResult(name = "products")
    List<ProductSoapDto> listProducts();

    @WebMethod(operationName = "listActiveProducts")
    @WebResult(name = "products")
    List<ProductSoapDto> listActiveProducts();

        @WebMethod(operationName = "searchActiveProductsByName")
        @WebResult(name = "products")
        List<ProductSoapDto> searchActiveProductsByName(@WebParam(name = "name") String name);

    @WebMethod(operationName = "createProduct")
    @WebResult(name = "product")
    ProductSoapDto createProduct(@WebParam(name = "product") ProductSoapDto product);

    @WebMethod(operationName = "updateProduct")
    @WebResult(name = "product")
    ProductSoapDto updateProduct(@WebParam(name = "product") ProductSoapDto product) throws ProductNotFoundFault;

    @WebMethod(operationName = "setProductActive")
    @WebResult(name = "product")
    ProductSoapDto setProductActive(@WebParam(name = "id") long id, @WebParam(name = "active") boolean active)
            throws ProductNotFoundFault;

    @WebMethod(operationName = "listSuppliers")
    @WebResult(name = "suppliers")
    List<SupplierSoapDto> listSuppliers();

        @WebMethod(operationName = "searchActiveSuppliers")
        @WebResult(name = "suppliers")
        List<SupplierSoapDto> searchActiveSuppliers(@WebParam(name = "query") String query);

    @WebMethod(operationName = "createSupplier")
    @WebResult(name = "supplier")
    SupplierSoapDto createSupplier(@WebParam(name = "supplier") SupplierSoapDto supplier)
            throws SupplierCodeConflictFault;

    @WebMethod(operationName = "updateSupplier")
    @WebResult(name = "supplier")
    SupplierSoapDto updateSupplier(@WebParam(name = "supplier") SupplierSoapDto supplier)
            throws SupplierNotFoundFault, SupplierCodeConflictFault;

    @WebMethod(operationName = "setSupplierActive")
    @WebResult(name = "supplier")
    SupplierSoapDto setSupplierActive(@WebParam(name = "id") long id, @WebParam(name = "active") boolean active)
            throws SupplierNotFoundFault;
}