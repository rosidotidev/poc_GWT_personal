package com.example.orders.frontend.server.rpc;

import com.example.orders.contract.CreateOrderSoapRequest;
import com.example.orders.contract.InvalidOrderStateFault;
import com.example.orders.contract.OrderItemSoapDto;
import com.example.orders.contract.OrderNotFoundFault;
import com.example.orders.contract.OrderSoapDto;
import com.example.orders.contract.OrdersSoapService;
import com.example.orders.contract.ProductNotFoundFault;
import com.example.orders.contract.ProductSoapDto;
import com.example.orders.contract.SupplierNotFoundFault;
import com.example.orders.contract.SupplierCodeConflictFault;
import com.example.orders.contract.SupplierSoapDto;
import com.example.orders.frontend.client.rpc.CreateOrderRpcRequest;
import com.example.orders.frontend.client.rpc.OrderItemRpcDto;
import com.example.orders.frontend.client.rpc.OrderRpcDto;
import com.example.orders.frontend.client.rpc.OrderRpcException;
import com.example.orders.frontend.client.rpc.OrdersRpcService;
import com.example.orders.frontend.client.rpc.ProductRpcDto;
import com.example.orders.frontend.client.rpc.SupplierRpcDto;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class OrdersRpcServiceImpl extends com.google.gwt.user.server.rpc.jakarta.RemoteServiceServlet
        implements OrdersRpcService {

    private OrdersSoapService soapService;

    public OrdersRpcServiceImpl() {
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        WebApplicationContext context = WebApplicationContextUtils
                .getRequiredWebApplicationContext(config.getServletContext());
        soapService = context.getBean(OrdersSoapService.class);
    }

    OrdersRpcServiceImpl(OrdersSoapService soapService) {
        this.soapService = soapService;
    }

    @Override
    public List<OrderRpcDto> listOrders() throws OrderRpcException {
        try {
            List<OrderSoapDto> soapOrders = soapService.listOrders();
            if (soapOrders == null) {
                return Collections.emptyList();
            }
            return soapOrders.stream()
                    .map(this::toRpcDto)
                    .collect(Collectors.toList());
        } catch (RuntimeException e) {
            throw rpcFailure(e);
        }
    }

    @Override
    public OrderRpcDto getOrder(long id) throws OrderRpcException {
        try {
            return toRpcDto(soapService.getOrder(id));
        } catch (OrderNotFoundFault e) {
            throw new OrderRpcException(e.getMessage());
        } catch (RuntimeException e) {
            throw rpcFailure(e);
        }
    }

    @Override
    public OrderRpcDto createOrder(CreateOrderRpcRequest request) throws OrderRpcException {
        try {
            return toRpcDto(soapService.createOrder(toSoapRequest(request)));
        } catch (RuntimeException e) {
            throw rpcFailure(e);
        }
    }

    @Override
    public OrderRpcDto approveOrder(long id) throws OrderRpcException {
        try {
            return toRpcDto(soapService.approveOrder(id));
        } catch (OrderNotFoundFault | InvalidOrderStateFault e) {
            throw new OrderRpcException(e.getMessage());
        } catch (RuntimeException e) {
            throw rpcFailure(e);
        }
    }

    @Override
    public OrderRpcDto sendOrder(long id) throws OrderRpcException {
        try {
            return toRpcDto(soapService.sendOrder(id));
        } catch (OrderNotFoundFault | InvalidOrderStateFault e) {
            throw new OrderRpcException(e.getMessage());
        } catch (RuntimeException e) {
            throw rpcFailure(e);
        }
    }

    @Override
    public List<ProductRpcDto> listProducts() throws OrderRpcException {
        try {
            List<ProductSoapDto> products = soapService.listProducts();
            if (products == null) {
                return Collections.emptyList();
            }
            return products.stream().map(this::toRpcProduct).collect(Collectors.toList());
        } catch (RuntimeException e) {
            throw rpcFailure(e);
        }
    }

    @Override
    public List<ProductRpcDto> searchActiveProductsByName(String name) throws OrderRpcException {
        try {
            List<ProductSoapDto> products = soapService.searchActiveProductsByName(name);
            if (products == null) {
                return Collections.emptyList();
            }
            return products.stream().map(this::toRpcProduct).collect(Collectors.toList());
        } catch (RuntimeException e) {
            throw rpcFailure(e);
        }
    }

    @Override
    public ProductRpcDto saveProduct(ProductRpcDto product) throws OrderRpcException {
        try {
            ProductSoapDto soapProduct = toSoapProduct(product);
            ProductSoapDto saved = product.getId() == 0
                    ? soapService.createProduct(soapProduct)
                    : soapService.updateProduct(soapProduct);
            return toRpcProduct(saved);
        } catch (ProductNotFoundFault e) {
            throw new OrderRpcException(e.getMessage());
        } catch (RuntimeException e) {
            throw rpcFailure(e);
        }
    }

    @Override
    public ProductRpcDto setProductActive(long id, boolean active) throws OrderRpcException {
        try {
            return toRpcProduct(soapService.setProductActive(id, active));
        } catch (ProductNotFoundFault e) {
            throw new OrderRpcException(e.getMessage());
        } catch (RuntimeException e) {
            throw rpcFailure(e);
        }
    }

    @Override
    public List<SupplierRpcDto> listSuppliers() throws OrderRpcException {
        try {
            List<SupplierSoapDto> suppliers = soapService.listSuppliers();
            if (suppliers == null) {
                return Collections.emptyList();
            }
            return suppliers.stream().map(this::toRpcSupplier).collect(Collectors.toList());
        } catch (RuntimeException e) {
            throw rpcFailure(e);
        }
    }

    @Override
    public List<SupplierRpcDto> searchActiveSuppliers(String query) throws OrderRpcException {
        try {
            List<SupplierSoapDto> suppliers = soapService.searchActiveSuppliers(query);
            if (suppliers == null) {
                return Collections.emptyList();
            }
            return suppliers.stream().map(this::toRpcSupplier).collect(Collectors.toList());
        } catch (RuntimeException e) {
            throw rpcFailure(e);
        }
    }

    @Override
    public SupplierRpcDto setSupplierActive(long id, boolean active) throws OrderRpcException {
        try {
            return toRpcSupplier(soapService.setSupplierActive(id, active));
        } catch (SupplierNotFoundFault e) {
            throw new OrderRpcException(e.getMessage());
        } catch (RuntimeException e) {
            throw rpcFailure(e);
        }
    }

    private CreateOrderSoapRequest toSoapRequest(CreateOrderRpcRequest request) {
        CreateOrderSoapRequest soapRequest = new CreateOrderSoapRequest();
        soapRequest.setCustomerName(request.getCustomerName());
        soapRequest.setDescription(request.getDescription());
        List<OrderItemSoapDto> items = new ArrayList<>();
        for (OrderItemRpcDto item : request.getItems()) {
            OrderItemSoapDto soapItem = new OrderItemSoapDto();
            soapItem.setProductId(item.getProductId());
            soapItem.setQuantity(item.getQuantity());
            items.add(soapItem);
        }
        soapRequest.setItems(items);
        return soapRequest;
    }

    private OrderRpcDto toRpcDto(OrderSoapDto soapOrder) {
        OrderRpcDto order = new OrderRpcDto();
        order.setId(soapOrder.getId());
        order.setCustomerName(soapOrder.getCustomerName());
        order.setDescription(soapOrder.getDescription());
        order.setStatus(soapOrder.getStatus());
        order.setCreatedAt(soapOrder.getCreatedAt() == null ? null : soapOrder.getCreatedAt().toXMLFormat());

        List<OrderItemRpcDto> items = new ArrayList<>();
        for (OrderItemSoapDto soapItem : soapOrder.getItems()) {
            OrderItemRpcDto item = new OrderItemRpcDto();
            item.setProductId(soapItem.getProductId());
            item.setProductName(soapItem.getProductName());
            item.setQuantity(soapItem.getQuantity());
            item.setUnitPrice(soapItem.getUnitPrice().doubleValue());
            items.add(item);
        }
        order.setItems(items);
        return order;
    }

    private ProductSoapDto toSoapProduct(ProductRpcDto product) {
        ProductSoapDto soapProduct = new ProductSoapDto();
        soapProduct.setId(product.getId());
        soapProduct.setSku(product.getSku());
        soapProduct.setName(product.getName());
        soapProduct.setDescription(product.getDescription());
        soapProduct.setUnitPrice(java.math.BigDecimal.valueOf(product.getUnitPrice()));
        soapProduct.setActive(product.isActive());
        soapProduct.setSupplierIds(product.getSupplierIds());
        return soapProduct;
    }

    private ProductRpcDto toRpcProduct(ProductSoapDto soapProduct) {
        ProductRpcDto product = new ProductRpcDto();
        product.setId(soapProduct.getId());
        product.setSku(soapProduct.getSku());
        product.setName(soapProduct.getName());
        product.setDescription(soapProduct.getDescription());
        product.setUnitPrice(soapProduct.getUnitPrice().doubleValue());
        product.setActive(soapProduct.isActive());
        product.setSupplierIds(soapProduct.getSupplierIds());
        return product;
    }

    private SupplierSoapDto toSoapSupplier(SupplierRpcDto supplier) {
        SupplierSoapDto soapSupplier = new SupplierSoapDto();
        soapSupplier.setId(supplier.getId());
        soapSupplier.setCode(supplier.getCode());
        soapSupplier.setName(supplier.getName());
        soapSupplier.setContactName(supplier.getContactName());
        soapSupplier.setEmail(supplier.getEmail());
        soapSupplier.setPhone(supplier.getPhone());
        soapSupplier.setActive(supplier.isActive());
        return soapSupplier;
    }

    private SupplierRpcDto toRpcSupplier(SupplierSoapDto soapSupplier) {
        SupplierRpcDto supplier = new SupplierRpcDto();
        supplier.setId(soapSupplier.getId());
        supplier.setCode(soapSupplier.getCode());
        supplier.setName(soapSupplier.getName());
        supplier.setContactName(soapSupplier.getContactName());
        supplier.setEmail(soapSupplier.getEmail());
        supplier.setPhone(soapSupplier.getPhone());
        supplier.setActive(soapSupplier.isActive());
        return supplier;
    }

    private OrderRpcException rpcFailure(RuntimeException e) {
        String message = e.getMessage();
        return new OrderRpcException(message == null ? "Order service is unavailable" : message);
    }
}