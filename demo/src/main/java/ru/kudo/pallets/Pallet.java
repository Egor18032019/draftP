package ru.kudo.pallets;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
class Pallet {
    int width, length, height;
    int weight_limit_kg = Integer.MAX_VALUE;

    private List<LevelArr> levels = new ArrayList<>();
    private List<Integer> currentLevelHeight = new ArrayList<>();


    private int currentLevel;
    private int maximumTotalHeight;
    private int currentHeightPallet = 0;


    public Pallet(int width, int length, int height) {
        this.width = width;
        this.length = length;
        this.height = height;
    }

    //    Ограничения по максимальному весу паллеты (если есть).
    public Pallet(int width, int length, int height, int weight_limit_kg, int maximumTotalHeight) {
        this.width = width;
        this.length = length;
        this.height = height;
        this.weight_limit_kg = weight_limit_kg;
        this.maximumTotalHeight = maximumTotalHeight;

    }


    /**
     * проверка на максимальный вес
     *
     * @param box коробка, которую хотим положить на этот уровень
     * @return true, если можем положить эту коробку на этот уровень
     */
    public boolean checkingLoad(Box box) {
        if (weight_limit_kg - box.getWeight_kg() < 0) return false;
        if (levels.size() == 1) return true; // если первый уровень
        boolean load = true;
        int approximateIndex = levels.get(currentLevel).getBoxes().size();
// todo убрать текущий уровень
        for (int i = 0; i < levels.size() - 2; i++) {
            LevelArr level = levels.get(i);
            // получаем лвл под текущей коробкой и проверяем нагрузку
            List<BoxInPallet> boxes = level.getBoxes();
            if (boxes.isEmpty()) continue;
            Box prevBox = boxes.get(approximateIndex);
            if (prevBox.getMax_load_kg() - box.getWeight_kg() >= 0) {
                continue;
            } else {
                load = false;
            }
        }
        return load;
    }

    public void currentLoad(int load) {
        if (levels.size() == 1) return; // если первый уровень
        int approximateIndex = levels.get(currentLevel).getBoxes().size();
        // todo убрать текущий уровень
        for (int i = 0; i < levels.size() - 2; i++) {
            LevelArr level = levels.get(i);
            List<BoxInPallet> boxes = level.getBoxes();
            if (boxes.isEmpty()) continue;
            Box prevBox = boxes.get(approximateIndex);
            prevBox.setMax_load_kg(prevBox.getMax_load_kg() - load);
        }
    }

    /**
     * Проверка на оставшуюся площадь
     *
     * @param box коробка, которую хотим положить на этот уровень
     * @return true, если можем положить эту коробку на этот уровень
     */
    public boolean canPlaceBoxForArea(Box box) {
        // погрешность допустима ? или прям заморочиться с х у ?
        int currentAvailableArea = levels.get(currentLevel).getCurrentAvailableArea();
        if (currentAvailableArea - box.getArea() >= 0) {

            System.out.println("Осталось площадей - " + currentAvailableArea);
            if (currentAvailableArea >= box.getArea()) {
                // ищем максимальное количество 0 по х и y
                // если х больше y то ищем по х, если нет по y
                MaxFreeAreaOnLevel freeAreaOnLevel = levels.get(currentLevel).getMeUninterruptedFreeArea();
                System.out.println("Непрерывная максимальная площадь имеет размер " + freeAreaOnLevel.lengthUninterrupted + " мм и " + freeAreaOnLevel.widthUninterrupted + " мм");
                if (freeAreaOnLevel.lengthUninterrupted >= box.getLength_mm() && freeAreaOnLevel.widthUninterrupted >= box.getWidth_mm()) {
                    return true;
                }
            }


        }
        return false;
    }

    /**
     * Проверка на максимальную высоту
     *
     * @param box коробка, которую хотим положить на этот уровень
     * @return true, если можем положить эту коробку на этот уровень
     */
    public boolean canPlaceBoxForMaximumTotalHeight(Box box) {
        int remainderMaxHeight = currentHeightPallet + box.getHeight_mm();
        return maximumTotalHeight - remainderMaxHeight >= 0;
    }


    public boolean canAddBox(Box box) {
        if (levels.isEmpty()) {
            LevelArr level = new LevelArr(width, length);
            levels.add(level);
            currentLevelHeight.add(0);
            currentLevel = 0;

        }
        return canPlaceBoxForArea(box) && checkingLoad(box) && canPlaceBoxForMaximumTotalHeight(box);
    }

    public void addBox(Box box) {
        weight_limit_kg -= box.getWeight_kg();
        levels.get(currentLevel).addBox(box);
        currentLoad(box.getWeight_kg());
        int prevMaxHeightBoxFromLevel = currentLevelHeight.get(currentLevel);
        int currentMaxHeightBoxFromLevel = box.getHeight_mm();
        if (currentMaxHeightBoxFromLevel > prevMaxHeightBoxFromLevel) {
            currentLevelHeight.set(currentLevel, currentMaxHeightBoxFromLevel);
            currentHeightPallet += (currentMaxHeightBoxFromLevel - prevMaxHeightBoxFromLevel);
        }
    }

    public void addLevel() {
        currentLevel++;
        LevelArr level = new LevelArr(width, length);
        levels.add(level);
        currentLevelHeight.add(0);

    }
}


/*
Параметры паллет:
- Размер основания (например, 800 мм × 1200 мм).
- Высота «поддона» (20 см) + максимальная общая высота (например, 1,8 м или 2,2 м от пола).
- Ограничения по максимальному весу паллеты (если есть).

 */