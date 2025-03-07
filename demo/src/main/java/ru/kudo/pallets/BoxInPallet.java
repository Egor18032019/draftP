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
    int leftX;
    int rightX;
    int topY;
    int bottomY;


    public BoxInPallet(int width_mm, int height_mm, int length_mm, int weight_kg, int max_load_kg, UUID article_id, int leftX, int rightX, int topY, int bottomY) {
        super(width_mm, height_mm, length_mm, weight_kg, max_load_kg, article_id);
        this.leftX = leftX;
        this.rightX = rightX;
        this.topY = topY;
        this.bottomY = bottomY;
    }
}
