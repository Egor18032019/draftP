package ru.kudo.pallets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MaxFreeAreaOnLevel {
    boolean isFound;
    /**
     * Максимальная длина свободного пространства
     */
    int lengthUninterrupted;
    /**
     * Максимальная ширина свободного пространства
     */
    int widthUninterrupted;

    int leftX;
    int rightX;
    int topY;
    int bottomY;
}
