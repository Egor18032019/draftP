package ru.kudo.pallets;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

/**
 * Класс описывает параметры коробки не лежащий на палете
 */
@Getter
@Setter
@ToString
public class Box {
    private UUID article_id;
    private int width_mm, height_mm, length_mm; // габариты коробки (в миллиметрах),
    /**
     * Объем коробки в кубических миллиметрах
     */
    private int area;
    private int weight_kg; // Вес коробки
    private int max_load_kg; // Максимальная нагрузка, которую коробка может выдержать сверху

    public Box(int width_mm, int height_mm, int length_mm, int weight_kg, int max_load_kg, UUID article_id) {
        //длина у коробки всегда больше ширины
        if (width_mm > length_mm) {
            this.length_mm = width_mm;
            this.width_mm = length_mm;
        } else {
            this.width_mm = width_mm;
            this.length_mm = length_mm;
        }

        this.article_id = article_id;
        this.height_mm = height_mm;
        this.area = width_mm * length_mm;
        this.weight_kg = weight_kg;
        this.max_load_kg = max_load_kg;
    }

    public Box(Box box) {
        this.article_id = box.article_id;
        this.width_mm = box.width_mm;
        this.length_mm = box.length_mm;
        this.height_mm = box.height_mm;
        this.area = box.area;
        this.weight_kg = box.weight_kg;
        this.max_load_kg = box.max_load_kg;
    }

    public boolean canSupport(int totalWeightAbove) {
        return totalWeightAbove <= max_load_kg;
    }
}
/*
 article_id: уникальный идентификатор артикула,
- width_mm, length_mm, height_mm: габариты коробки (в миллиметрах),
- weight_kg: вес коробки,
- max_load_kg: максимальная нагрузка, которую коробка может выдержать (при укладке сверху).

 */