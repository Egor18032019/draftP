package ru.kudo.pallets;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс, представляющий уровень на котором можно хранить коробки.
 */
@Getter
@Setter
@ToString
class Level {
    private List<Box> boxes;
    private final int palletWidth;
    private final int palletLength;
    private int currentY;

    private int currentAvailableArea;
    private List<int[]> freeSpaceAfterInstallingBox = new ArrayList<>();

    public Level(int palletWidth, int palletLength ) {
        this.palletWidth = palletWidth;
        this.palletLength = palletLength;

        this.currentAvailableArea = palletWidth * palletLength;

        this.boxes = new ArrayList<>();
        this.currentY = palletLength;
    }



}
