package com.drmodi.food.ordering.system.order.service.domain.valueobject;

import com.drmodi.food.ordering.system.domain.valueobject.BaseId;

import java.util.UUID;

public class TrackingId extends BaseId<UUID> {
    public TrackingId(UUID value) {
        super(value);
    }
}
