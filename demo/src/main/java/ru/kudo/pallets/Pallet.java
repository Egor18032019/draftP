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
    public boolean checkingLoad(Box box, MaxFreeAreaOnLevel maxFreeAreaOnLevel) {
        if (weight_limit_kg - box.getWeight_kg() < 0) return false;
        if (levels.size() == 1) return true; // если первый уровень
        int max_load_kg = box.getMax_load_kg();
        int currentY = maxFreeAreaOnLevel.getTopY();
        int currentX = maxFreeAreaOnLevel.getLeftX();

        for (int z = 0; z < (currentLevel - 1); z++) {
            int[][] prevMaxLoadArrMap = levels.get(z).getMaxLoadArrMap();
            if (box.getWidth_mm() >= maxFreeAreaOnLevel.getWidthUninterrupted() && box.getLength_mm() >= maxFreeAreaOnLevel.getLengthUninterrupted()) {
                for (int j = currentY; j < currentY + box.getWidth_mm(); j++) {
                    for (int i = currentX; i < currentX + box.getLength_mm(); i++) {
                        if (prevMaxLoadArrMap[j][i] <= max_load_kg) {
                            return false;
                        }
                    }
                }
            } else {
                for (int i = currentY; i < currentY + box.getWidth_mm(); i++) {
                    for (int j = currentX; j < currentX + box.getLength_mm(); j++) {
                        if (prevMaxLoadArrMap[j][i] <= max_load_kg) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    public void currentLoad(BoxInPallet box) {
        if (levels.size() == 1) return; // если первый уровень
        //todo  проходим по нижним уровням и уменьшаем максимальный вес нагрузки
        for (int z = 0; z < currentLevel; z++) {
            int[][] prevMaxLoadArrMap = levels.get(z).getMaxLoadArrMap();
            for (int j = box.yCoord; j < box.yCoord + box.getWidth_mm(); j++) {
                for (int i = box.xCoord; i < box.yCoord + box.getLength_mm(); i++) {
                    prevMaxLoadArrMap[j][i] = prevMaxLoadArrMap[j][i] - box.getWeight_kg();

                }
            }
        }
    }

    /**
     * Проверка на оставшуюся площадь
     *
     * @param box коробка, которую хотим положить на этот уровень
     * @return true, если можем положить эту коробку на этот уровень
     */
    public MaxFreeAreaOnLevel canPlaceBoxForArea(Box box) {
        int currentAvailableArea = levels.get(currentLevel).getCurrentAvailableArea();


        System.out.println("Осталось площадей - " + currentAvailableArea);
        if (currentAvailableArea >= box.getArea()) {
            MaxFreeAreaOnLevel freeAreaOnLevel = levels.get(currentLevel).getMeUninterruptedFreeArea(box);
            if (freeAreaOnLevel == null) {
                return null; //todo исправить !!
            }
            System.out.println("Непрерывная максимальная площадь имеет размер " + freeAreaOnLevel.lengthUninterrupted + " мм и " + freeAreaOnLevel.widthUninterrupted + " мм");
            if (freeAreaOnLevel.getLengthUninterrupted() >= box.getLength_mm() && freeAreaOnLevel.getWidthUninterrupted() >= box.getWidth_mm()) {
                return freeAreaOnLevel;
            }
            if (freeAreaOnLevel.getLengthUninterrupted() >= box.getWidth_mm()
                    && freeAreaOnLevel.getWidthUninterrupted() >= box.getLength_mm()) {
                return freeAreaOnLevel;
            }
        }

        return null;
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

    public boolean canPlaceBoxForContinuousHeight(Box box) {

        return true;
    }

    public boolean canAddBox(Box box) {
        if (levels.isEmpty()) {
            LevelArr level = new LevelArr(width, length, weight_limit_kg);
            levels.add(level);
            currentLevelHeight.add(0);
            currentLevel = 0;

        }
        MaxFreeAreaOnLevel maxFreeAreaOnLevel = canPlaceBoxForArea(box);
        if (maxFreeAreaOnLevel != null) {
            return checkingLoad(box, maxFreeAreaOnLevel) && canPlaceBoxForMaximumTotalHeight(box);
        } else {
            System.out.println("Нет места для этой коробки на этом уровне " + box.getArticle_id());
            return false;
        }

    }

    public void addBox(Box box) {
        weight_limit_kg -= box.getWeight_kg();
        BoxInPallet boxAfterOnPallet = levels.get(currentLevel).addBox(box);
        currentLoad(boxAfterOnPallet);
        int prevMaxHeightBoxFromLevel = currentLevelHeight.get(currentLevel);
        int currentMaxHeightBoxFromLevel = box.getHeight_mm();
        if (currentMaxHeightBoxFromLevel > prevMaxHeightBoxFromLevel) {
            currentLevelHeight.set(currentLevel, currentMaxHeightBoxFromLevel);
            currentHeightPallet += (currentMaxHeightBoxFromLevel - prevMaxHeightBoxFromLevel);
        }
    }

    public void addLevel() {
        currentLevel++;
        LevelArr level = new LevelArr(width, length, weight_limit_kg);
        levels.add(level);
        currentLevelHeight.add(0);
        System.out.println("Добавлен новый уровень " + currentLevel);
    }
}


/*
Параметры паллет:
- Размер основания (например, 800 мм × 1200 мм).
- Высота «поддона» (20 см) + максимальная общая высота (например, 1,8 м или 2,2 м от пола).
- Ограничения по максимальному весу паллеты (если есть).

 */