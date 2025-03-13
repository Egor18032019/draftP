package ru.kudo.pallets;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

/**
 * Класс описывает параметры коробки лежащей на палете
 */
@Getter
@Setter
@ToString
public class BoxInPallet extends Box {
    private int xCoord;
    private int zCoord;
    private int yCoord;

    private UUID palletId;

    public BoxInPallet(Box box, UUID palletId, int xCoord, int zCoord, int yCoord) {
        super(box);
        this.xCoord = xCoord;
        this.zCoord = zCoord;
        this.yCoord = yCoord;

        this.palletId = palletId;

    }
}
