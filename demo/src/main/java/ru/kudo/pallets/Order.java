package ru.kudo.pallets;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Класс описывает параметры заказа.
 */
@Getter
@Setter
@ToString
public class Order {
    private UUID orderGuid;
    private List<Box> boxes;
    private LocalDate orderDate;

    public Order() {
        this.orderGuid = UUID.randomUUID();
        this.boxes = new ArrayList<>();
        this.orderDate = LocalDate.now();
    }

    public Order(List<Box> boxes) {
        this.orderGuid = UUID.randomUUID();
        this.boxes = boxes;
        this.orderDate = LocalDate.now();
    }

    public Order(UUID orderGuid, List<Box> boxes, LocalDate orderDate) {
        this.orderGuid = orderGuid;
        this.boxes = boxes;
        this.orderDate = orderDate;
    }
}
