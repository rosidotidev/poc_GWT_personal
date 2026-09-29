package com.example.orders.backend.soap;

import com.example.orders.backend.model.Order;
import com.example.orders.backend.model.OrderItem;
import com.example.orders.contract.CreateOrderSoapRequest;
import com.example.orders.contract.OrderItemSoapDto;
import com.example.orders.contract.OrderSoapDto;

import javax.xml.datatype.DatatypeFactory;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.GregorianCalendar;

final class OrderSoapMapper {

    private OrderSoapMapper() {
    }

    static Order toOrder(CreateOrderSoapRequest request) {
        Order order = new Order();
        order.setCustomerName(request.getCustomerName());
        order.setDescription(request.getDescription());
        order.setItems(new ArrayList<>());
        if (request.getItems() != null) {
            for (OrderItemSoapDto item : request.getItems()) {
                OrderItem orderItem = new OrderItem(item.getProductName(), item.getQuantity(), item.getUnitPrice());
                orderItem.setProductId(item.getProductId() == 0 ? null : item.getProductId());
                order.getItems().add(orderItem);
            }
        }
        return order;
    }

    static OrderSoapDto toSoapDto(Order order) {
        OrderSoapDto dto = new OrderSoapDto();
        dto.setId(order.getId());
        dto.setCustomerName(order.getCustomerName());
        dto.setDescription(order.getDescription());
        dto.setStatus(order.getStatus().name());
        dto.setItems(new ArrayList<>());
        for (OrderItem item : order.getItems()) {
            OrderItemSoapDto itemDto = new OrderItemSoapDto();
            itemDto.setProductId(item.getProductId() == null ? 0 : item.getProductId());
            itemDto.setProductName(item.getProductName());
            itemDto.setQuantity(item.getQuantity());
            itemDto.setUnitPrice(item.getUnitPrice());
            dto.getItems().add(itemDto);
        }
        if (order.getCreatedAt() != null) {
            GregorianCalendar calendar = GregorianCalendar.from(order.getCreatedAt().atZone(ZoneOffset.UTC));
            dto.setCreatedAt(DatatypeFactory.newDefaultInstance().newXMLGregorianCalendar(calendar));
        }
        return dto;
    }
}