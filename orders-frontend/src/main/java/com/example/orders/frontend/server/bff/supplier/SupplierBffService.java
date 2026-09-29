package com.example.orders.frontend.server.bff.supplier;

import com.example.orders.contract.OrdersSoapService;
import com.example.orders.contract.SupplierCodeConflictFault;
import com.example.orders.contract.SupplierNotFoundFault;
import com.example.orders.contract.SupplierSoapDto;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class SupplierBffService {

    private final OrdersSoapService soapService;
    private final SupplierRequestValidator validator;

    public SupplierBffService(OrdersSoapService soapService, SupplierRequestValidator validator) {
        this.soapService = soapService;
        this.validator = validator;
    }

    public SupplierRepresentation get(long id) {
        requirePositiveId(id);
        try {
            List<SupplierSoapDto> suppliers = soapService.listSuppliers();
            return (suppliers == null ? Collections.<SupplierSoapDto>emptyList() : suppliers).stream()
                    .filter(supplier -> supplier.getId() == id)
                    .findFirst()
                    .map(this::toRepresentation)
                    .orElseThrow(() -> new SupplierBffExceptions.NotFound(id));
        } catch (SupplierBffExceptions.NotFound | SupplierBffExceptions.BadRequest e) {
            throw e;
        } catch (RuntimeException e) {
            throw new SupplierBffExceptions.DownstreamUnavailable(e);
        }
    }

    public SupplierRepresentation create(SupplierWriteRequest request) {
        validator.validate(request);
        try {
            return toRepresentation(soapService.createSupplier(toSoapDto(0, request)));
        } catch (SupplierCodeConflictFault e) {
            throw new SupplierBffExceptions.Conflict(request.code().trim().toUpperCase());
        } catch (RuntimeException e) {
            throw new SupplierBffExceptions.DownstreamUnavailable(e);
        }
    }

    public SupplierRepresentation update(long id, SupplierWriteRequest request) {
        requirePositiveId(id);
        validator.validate(request);
        try {
            return toRepresentation(soapService.updateSupplier(toSoapDto(id, request)));
        } catch (SupplierNotFoundFault e) {
            throw new SupplierBffExceptions.NotFound(id);
        } catch (SupplierCodeConflictFault e) {
            throw new SupplierBffExceptions.Conflict(request.code().trim().toUpperCase());
        } catch (RuntimeException e) {
            throw new SupplierBffExceptions.DownstreamUnavailable(e);
        }
    }

    private void requirePositiveId(long id) {
        if (id <= 0) throw new SupplierBffExceptions.BadRequest("Supplier ID must be positive");
    }

    private SupplierSoapDto toSoapDto(long id, SupplierWriteRequest request) {
        SupplierSoapDto supplier = new SupplierSoapDto();
        supplier.setId(id);
        supplier.setCode(request.code());
        supplier.setName(request.name());
        supplier.setContactName(request.contactName());
        supplier.setEmail(request.email());
        supplier.setPhone(request.phone());
        return supplier;
    }

    private SupplierRepresentation toRepresentation(SupplierSoapDto supplier) {
        return new SupplierRepresentation(supplier.getId(), supplier.getCode(), supplier.getName(),
                supplier.getContactName(), supplier.getEmail(), supplier.getPhone(), supplier.isActive());
    }
}