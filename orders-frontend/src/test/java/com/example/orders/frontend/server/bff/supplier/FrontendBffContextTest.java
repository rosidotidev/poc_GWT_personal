package com.example.orders.frontend.server.bff.supplier;

import com.example.orders.contract.OrdersSoapService;
import com.example.orders.contract.SupplierSoapDto;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FrontendBffContextTest {

    @Test
    void childMvcContextUsesSoapProxyFromRootContext() {
        OrdersSoapService soapService = mock(OrdersSoapService.class);
        SupplierSoapDto supplier = new SupplierSoapDto();
        supplier.setId(12);
        supplier.setCode("ROOT-12");
        supplier.setName("Root context supplier");
        when(soapService.listSuppliers()).thenReturn(List.of(supplier));

        try (GenericApplicationContext root = new GenericApplicationContext()) {
            root.registerBean(OrdersSoapService.class, () -> soapService);
            root.refresh();

            try (AnnotationConfigWebApplicationContext child = new AnnotationConfigWebApplicationContext()) {
                child.setServletContext(new MockServletContext());
                child.setParent(root);
                child.register(FrontendBffConfig.class);
                child.refresh();

                SupplierRepresentation result = child.getBean(SupplierBffService.class).get(12);
                assertEquals("ROOT-12", result.code());
                verify(soapService).listSuppliers();
            }
        }
    }
}