package com.example.orders.backend.model;

/** Order lifecycle: CREATED -&gt; APPROVED -&gt; SENT, no other transitions allowed. */
public enum OrderStatus {
    CREATED,
    APPROVED,
    SENT
}
