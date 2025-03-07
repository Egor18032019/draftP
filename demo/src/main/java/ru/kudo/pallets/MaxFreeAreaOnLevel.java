package ru.kudo.pallets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MaxFreeAreaOnLevel {
    int lengthUninterrupted;
    int widthUninterrupted;

    int leftX;
    int rightX;
    int topY;
    int bottomY;
}
