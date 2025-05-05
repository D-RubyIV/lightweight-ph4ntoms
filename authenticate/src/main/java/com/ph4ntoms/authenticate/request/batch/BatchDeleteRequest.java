package com.ph4ntoms.authenticate.request.batch;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class BatchDeleteRequest {
    private List<UUID> ids;
}
