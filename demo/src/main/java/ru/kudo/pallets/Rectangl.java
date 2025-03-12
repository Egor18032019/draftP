package ru.kudo.pallets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class Rectangl {
    boolean isExists;
    int height;
    int leftX;
    int rightX;
    int topY;
    int bottomY;
}

