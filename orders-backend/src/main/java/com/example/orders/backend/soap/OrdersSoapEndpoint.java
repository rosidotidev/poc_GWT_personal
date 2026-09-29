package com.example.orders.backend.soap;

import com.example.orders.backend.exception.OrderNotFoundException;
import com.example.orders.backend.exception.ProductNotFoundException;
import com.example.orders.backend.exception.SupplierNotFoundException;
import com.example.orders.backend.exception.SupplierCodeConflictException;
import com.example.orders.backend.model.Order;
import com.example.orders.backend.model.OrderItem;
import com.example.orders.backend.model.Product;
import com.example.orders.backend.service.OrderService;
import com.example.orders.backend.service.ProductService;
import com.example.orders.backend.service.SupplierService;
import com.example.orders.contract.CreateOrderSoapRequest;
import com.example.orders.contract.InvalidOrderStateFault;
import com.example.orders.contract.InvalidOrderStateFaultBean;
import com.example.orders.contract.OrderNotFoundFault;
import com.example.orders.contract.OrderNotFoundFaultBean;
import com.example.orders.contract.ProductNotFoundFault;
import com.example.orders.contract.ProductNotFoundFaultBean;
import com.example.orders.contract.OrderSoapDto;
import com.example.orders.contract.OrdersSoapService;
import com.example.orders.contract.ProductSoapDto;
import com.example.orders.contract.SupplierNotFoundFault;
import com.example.orders.contract.SupplierNotFoundFaultBean;
import com.example.orders.contract.SupplierCodeConflictFault;
import com.example.orders.contract.SupplierCodeConflictFaultBean;
import com.example.orders.contract.SupplierSoapDto;
import jakarta.jws.WebService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service("ordersSoapService")
@WebService(serviceName = "OrdersSoapService", portName = "OrdersSoapPort",
        targetNamespace = OrdersSoapService.NAMESPACE,
        endpointInterface = "com.example.orders.contract.OrdersSoapService")
public class OrdersSoapEndpoint implements OrdersSoapService {

    private final OrderService orderService;
    private final ProductService productService;
    private final SupplierService supplierService;

    public OrdersSoapEndpoint(OrderService orderService, ProductService productService, SupplierService supplierService) {
        this.orderService = orderService;
        this.productService = productService;
        this.supplierService = supplierService;
    }

    @Override
    public List<OrderSoapDto> listOrders() {
        return orderService.listOrders().stream()
                .map(OrderSoapMapper::toSoapDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderSoapDto getOrder(long id) throws OrderNotFoundFault {
        try {
            return OrderSoapMapper.toSoapDto(orderService.getOrder(id));
        } catch (OrderNotFoundException e) {
            throw notFound(id, e);
        }
    }

    @Override
    public OrderSoapDto createOrder(CreateOrderSoapRequest request) {
        Order order = OrderSoapMapper.toOrder(request);
        for (OrderItem item : order.getItems()) {
            if (item.getProductId() == null) {
                throw new IllegalArgumentException("Choose a product from the catalog");
            }
            Product product = productService.getProduct(item.getProductId());
            if (!product.isActive()) {
                throw new IllegalArgumentException("Product " + product.getSku() + " is inactive");
            }
            item.setProductName(product.getName());
            item.setUnitPrice(product.getUnitPrice());
        }
        return OrderSoapMapper.toSoapDto(orderService.createOrder(order));
    }

    @Override
    public OrderSoapDto approveOrder(long id) throws OrderNotFoundFault, InvalidOrderStateFault {
        try {
            return OrderSoapMapper.toSoapDto(orderService.approveOrder(id));
        } catch (OrderNotFoundException e) {
            throw notFound(id, e);
        } catch (IllegalStateException e) {
            throw invalidState(id, "approve", e);
        }
    }

    @Override
    public OrderSoapDto sendOrder(long id) throws OrderNotFoundFault, InvalidOrderStateFault {
        try {
            return OrderSoapMapper.toSoapDto(orderService.sendOrder(id));
        } catch (OrderNotFoundException e) {
            throw notFound(id, e);
        } catch (IllegalStateException e) {
            throw invalidState(id, "send", e);
        }
    }

    @Override
    public List<ProductSoapDto> listProducts() {
        return productService.listProducts().stream().map(ProductSoapMapper::toSoapDto).collect(Collectors.toList());
    }

    @Override
    public List<ProductSoapDto> listActiveProducts() {
        return productService.listActiveProducts().stream().map(ProductSoapMapper::toSoapDto).collect(Collectors.toList());
    }

    @Override
    public List<ProductSoapDto> searchActiveProductsByName(String name) {
        return productService.searchActiveProductsByName(name).stream()
                .map(ProductSoapMapper::toSoapDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductSoapDto createProduct(ProductSoapDto product) {
        return ProductSoapMapper.toSoapDto(productService.createProduct(ProductSoapMapper.toProduct(product)));
    }

    @Override
    public ProductSoapDto updateProduct(ProductSoapDto product) throws ProductNotFoundFault {
        try {
            return ProductSoapMapper.toSoapDto(productService.updateProduct(ProductSoapMapper.toProduct(product)));
        } catch (ProductNotFoundException e) {
            throw productNotFound(product.getId(), e);
        }
    }

    @Override
    public ProductSoapDto setProductActive(long id, boolean active) throws ProductNotFoundFault {
        try {
            return ProductSoapMapper.toSoapDto(productService.setProductActive(id, active));
        } catch (ProductNotFoundException e) {
            throw productNotFound(id, e);
        }
    }

    @Override
    public List<SupplierSoapDto> listSuppliers() {
        return supplierService.listSuppliers().stream().map(SupplierSoapMapper::toSoapDto).collect(Collectors.toList());
    }

    @Override
    public List<SupplierSoapDto> searchActiveSuppliers(String query) {
        return supplierService.searchActiveSuppliers(query).stream()
                .map(SupplierSoapMapper::toSoapDto)
                .collect(Collectors.toList());
    }

    @Override
    public SupplierSoapDto createSupplier(SupplierSoapDto supplier) throws SupplierCodeConflictFault {
        try {
            return SupplierSoapMapper.toSoapDto(supplierService.createSupplier(SupplierSoapMapper.toSupplier(supplier)));
        } catch (SupplierCodeConflictException e) {
            throw supplierCodeConflict(e);
        }
    }

    @Override
    public SupplierSoapDto updateSupplier(SupplierSoapDto supplier)
            throws SupplierNotFoundFault, SupplierCodeConflictFault {
        try {
            return SupplierSoapMapper.toSoapDto(supplierService.updateSupplier(SupplierSoapMapper.toSupplier(supplier)));
        } catch (SupplierNotFoundException e) {
            throw supplierNotFound(supplier.getId(), e);
        } catch (SupplierCodeConflictException e) {
            throw supplierCodeConflict(e);
        }
    }

    @Override
    public SupplierSoapDto setSupplierActive(long id, boolean active) throws SupplierNotFoundFault {
        try {
            return SupplierSoapMapper.toSoapDto(supplierService.setSupplierActive(id, active));
        } catch (SupplierNotFoundException e) {
            throw supplierNotFound(id, e);
        }
    }

    private ProductNotFoundFault productNotFound(long id, ProductNotFoundException cause) {
        String message = cause.getMessage();
        return new ProductNotFoundFault(message, new ProductNotFoundFaultBean(id, message), cause);
    }

    private SupplierNotFoundFault supplierNotFound(long id, SupplierNotFoundException cause) {
        String message = cause.getMessage();
        return new SupplierNotFoundFault(message, new SupplierNotFoundFaultBean(id, message), cause);
    }

    private SupplierCodeConflictFault supplierCodeConflict(SupplierCodeConflictException cause) {
        String message = cause.getMessage();
        return new SupplierCodeConflictFault(message,
                new SupplierCodeConflictFaultBean(cause.getCode(), message), cause);
    }

    private OrderNotFoundFault notFound(long id, OrderNotFoundException cause) {
        String message = cause.getMessage();
        return new OrderNotFoundFault(message, new OrderNotFoundFaultBean(id, message), cause);
    }

    private InvalidOrderStateFault invalidState(long id, String action, IllegalStateException cause)
            throws OrderNotFoundFault {
        Order order = getOrderModel(id);
        String message = cause.getMessage();
        InvalidOrderStateFaultBean fault = new InvalidOrderStateFaultBean(
                id, order.getStatus().name(), action, message);
        return new InvalidOrderStateFault(message, fault, cause);
    }

    private Order getOrderModel(long id) throws OrderNotFoundFault {
        try {
            return orderService.getOrder(id);
        } catch (OrderNotFoundException e) {
            throw notFound(id, e);
        }
    }
}