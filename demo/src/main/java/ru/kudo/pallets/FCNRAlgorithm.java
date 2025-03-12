package ru.kudo.pallets;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class FCNRAlgorithm {

    public static void main(String[] args) {
        List<Box> boxes = new ArrayList<>();
        boxes.add(new Box(2, 3, 9, 5, 10, new UUID(1, 22))); // Вес 5, максимальная нагрузка 10
        boxes.add(new Box(1, 2, 8, 2, 5, new UUID(2, 22)));   // Вес 2, максимальная нагрузка 5
        boxes.add(new Box(3, 3, 7, 9, 8, new UUID(3, 22)));   // Вес 4, максимальная нагрузка 8
        boxes.add(new Box(4, 4, 6, 10, 15, new UUID(4, 22))); // Вес 10, максимальная нагрузка 15
        boxes.add(new Box(2, 4, 6, 1, 15, new UUID(4, 22))); // Вес 10, максимальная нагрузка 15
        boxes.add(new Box(5, 4, 5, 1, 15, new UUID(4, 22))); // Вес 10, максимальная нагрузка 15
        boxes.add(new Box(5, 4, 5, 1, 15, new UUID(4, 22))); // Вес 10, максимальная нагрузка 15
        boxes.add(new Box(5, 4, 5, 1, 15, new UUID(4, 22))); // Вес 10, максимальная нагрузка 15


        stackingAlgorithm(boxes);
    }

    public static void stackingAlgorithm(List<Box> order) {
        List<Pallet> neededPallets = new ArrayList<>();
        Pallet pallet = new Pallet(9, 9, 9, 50, 180);

        // сортируем по весу и длине ? или только по весу ?? или + нагрузке ?
//        order.sort(Comparator.comparingInt(b -> -b.getWeight_kg()));
        order.sort(Comparator.comparingInt(Box::getWeight_kg).reversed().thenComparingInt(Box::getHeight_mm));


        // Создаем новую копию исходного списка
        List<Box> sortedList = new ArrayList<>(order);

        while (!sortedList.isEmpty()) {
            for (Box box : order) {

                // Пробуем разместить коробку на паллете
                if (canFitOnPalletOnThisLevel(box, pallet)) {
                    pallet.addBox(box);
                    sortedList.remove(box);
                }
            }
            // делаем новый уровень коробок
            // как сделать выход и не создавать новые пустые уровни??
            if (pallet.getLevels().get(pallet.getCurrentLevel()).getBoxes().isEmpty()) {
                System.out.println("Необходим новый паллет.");
                System.out.println("Осталось " + sortedList.size() + " коробок.");
                pallet.getLevels().remove(pallet.getCurrentLevel());
                neededPallets.add(pallet);
                pallet = new Pallet(9, 9, 9, 50, 55);

            }
            pallet.addLevel();
            order = new ArrayList<>(sortedList);


        }
        if (pallet.getLevels().get(pallet.getCurrentLevel()).getBoxes().isEmpty()) {
            pallet.getLevels().remove(pallet.getCurrentLevel());
        }
        neededPallets.add(pallet);
        System.out.println("Все коробки размещены на паллетах.");
        System.out.println("Количество паллет: " + neededPallets.size());
        System.out.println(neededPallets);
    }


    public static boolean canFitOnPalletOnThisLevel(Box box, Pallet pallet) {
        boolean result = pallet.canAddBox(box);

        return result;
    }

}


