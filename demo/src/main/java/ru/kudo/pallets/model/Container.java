package ru.kudo.pallets.model;

import lombok.Data;

import java.util.List;
import java.util.UUID;
@Data
public class Container {
    private UUID id;
    private int xCoord;
    private int yCoord;
    private int zCoord;
    private int height;
    private int width;
    private int length;
    private String unit;
    private List<Good> goods;
}
