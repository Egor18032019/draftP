package ru.kudo.pallets;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class FCNRAlgorithm {
    static Pallet pallet = new Pallet(9, 9, 9, 50, 50);

    public static void main(String[] args) {
        List<Box> boxes = new ArrayList<>();
        boxes.add(new Box(2, 3, 9, 5, 10, new UUID(1, 22))); // Вес 5, максимальная нагрузка 10
        boxes.add(new Box(1, 2, 8, 2, 5, new UUID(2, 22)));   // Вес 2, максимальная нагрузка 5
        boxes.add(new Box(3, 3, 7, 9, 8, new UUID(3, 22)));   // Вес 4, максимальная нагрузка 8
        boxes.add(new Box(4, 4, 6, 10, 15, new UUID(4, 22))); // Вес 10, максимальная нагрузка 15
        boxes.add(new Box(2, 4, 6, 1, 15, new UUID(4, 22))); // Вес 10, максимальная нагрузка 15


        stackingAlgorithm(boxes, pallet);
    }

    public static void stackingAlgorithm(List<Box> order, Pallet pallet) {

        // сортируем по весу и длине ? или только по весу ?? или + нагрузке ?
        order.sort(Comparator.comparingInt(b -> -b.getWeight_kg()));
//        order.sort(Comparator.comparingInt(Box::getLength_mm).thenComparingInt(Box::getWeight_kg));


        // Создаем новую копию исходного списка
        List<Box> sortedList = new ArrayList<>(order);

        while (!sortedList.isEmpty()) {
            for (Box box : order) {
                if(box.getLength_mm()==6&&box.getWidth_mm()==2){
                    System.out.println("!!!");
                }
                // Пробуем разместить коробку на паллете
                if (canFitOnPalletOnThisLevel(box)) {
                    pallet.addBox(box);
                    sortedList.remove(box);
                }
            }
            // делаем новый уровень коробок
            // как сделать выход и не создавать новые пустые уровни??
            if (!pallet.getLevels().get(pallet.getCurrentLevel()).getBoxes().isEmpty()) {
                pallet.addLevel();
                order = new ArrayList<>(sortedList);
            } else {
                // переходим к оставшимся площадям после установки всех коробок на уровне ранее
                System.out.println(order);

            }

        }


        System.out.println("Remaining height on the pallet: " + pallet.getCurrentHeightPallet());
        System.out.println(pallet.getWeight_limit_kg() + " - оставшийся вес палеты.");
    }


    public static boolean canFitOnPalletOnThisLevel(Box box) {
        boolean result = pallet.canAddBox(box);

        return result;
    }

}



        /*
       Производится предварительная сортировка по объему, чтобы размещать более крупные коробки первыми.
       Самая объемная коробка устанавливается в левый ближний угол,
       Потом идет сортировка коробок по весу и максимальной нагрузке. = arr1
       И последовательно заполняем первый уровень паллеты с arr1.
       Далее повторяем.
       То есть сортируем по объему и пытаемся разместить самую объемную коробку в левый ближний угол.
            (проверка по максимальной высоте и нагрузке)
       И если это не получается, то перебираем по весу и максимальной нагрузке и высоте ? (рекурсия?)
и так далее.
         */