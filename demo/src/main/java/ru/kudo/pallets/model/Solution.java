package ru.kudo.pallets.model;

import lombok.Data;

import java.util.Date;
import java.util.UUID;
@Data
public class Solution {
    private UUID id;
    private String description;
    private Container container;
    private Date calculated;
    private CalculationSource calculationSource;
}
