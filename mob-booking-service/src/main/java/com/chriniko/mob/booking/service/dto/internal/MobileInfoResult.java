package com.chriniko.mob.booking.service.dto.internal;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;


@Data
@NoArgsConstructor
public class MobileInfoResult {

    private String id;
    private String model;
    private String modelId;
    private List<String> technology;
    private Map<String, List<String>> bandsByTech;

}
