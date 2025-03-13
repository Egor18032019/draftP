package ru.kudo.pallets;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class Pallet {
    private UUID palletId;
    private UUID orderGuid;

    private int width, length, height;
    private int weight_limit_kg = Integer.MAX_VALUE;

    private List<LevelArr> levels = new ArrayList<>();
    private List<Integer> currentLevelHeight = new ArrayList<>();


    private int currentLevel;
    private int maximumTotalHeight;
    private int currentHeightPallet = 0;


    public Pallet(int width, int length, int height) {

        this.palletId = UUID.randomUUID();
        this.width = width;
        this.length = length;
        this.height = height;
    }

    //    Ограничения по максимальному весу паллеты (если есть).
    public Pallet(UUID orderGuid,int width, int length, int height, int weight_limit_kg, int maximumTotalHeight) {
        this.palletId = UUID.randomUUID();
        this.orderGuid = orderGuid;
        this.width = width;
        this.length = length;
        this.height = height;
        this.weight_limit_kg = weight_limit_kg;
        this.maximumTotalHeight = maximumTotalHeight;

    }


    /**
     * Проверка на максимальный нагрузку
     *
     * @param box коробка, которую хотим положить на этот уровень
     * @return true, если можем положить эту коробку на этот уровень
     */
    public boolean checkingLoad(Box box, Rectangl maxFreeAreaOnLevel) {
        if (weight_limit_kg - box.getWeight_kg() < 0) return false;
        if (levels.size() == 1) return true; // если первый уровень
        int weightThisBox = box.getWeight_kg();
        int currentY = maxFreeAreaOnLevel.getTopY();
        int currentX = maxFreeAreaOnLevel.getLeftX();
        int widthUninterrupted = maxFreeAreaOnLevel.getRightX() - maxFreeAreaOnLevel.getLeftX();
        int lengthUninterrupted = maxFreeAreaOnLevel.getBottomY() - maxFreeAreaOnLevel.getTopY();
        for (int z = 0; z < (currentLevel - 1); z++) {
            int[][] prevMaxLoadArrMap = levels.get(z).getMaxLoadArrMap();
            if (box.getWidth_mm() >= widthUninterrupted && box.getLength_mm() >= lengthUninterrupted) {
                for (int j = currentY; j < currentY + box.getWidth_mm(); j++) {
                    for (int i = currentX; i < currentX + box.getLength_mm(); i++) {
                        if (prevMaxLoadArrMap[j][i] - weightThisBox < 0) {
                            return false;
                        }
                    }
                }
            } else {
                for (int i = currentY; i < currentY + box.getWidth_mm(); i++) {
                    for (int j = currentX; j < currentX + box.getLength_mm(); j++) {
                        if (prevMaxLoadArrMap[j][i] - weightThisBox < 0) {
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
        //   проходим по нижним уровням и уменьшаем максимальный вес нагрузки

//todo сделать правильное заполнение
        for (int z = 0; z < currentLevel; z++) {
            int[][] prevMaxLoadArrMap = levels.get(z).getMaxLoadArrMap();
            for (int j = box.getZCoord(); j < box.getZCoord() + box.getWidth_mm(); j++) {
                for (int i = box.getXCoord(); i < box.getXCoord() + box.getLength_mm(); i++) {
                    prevMaxLoadArrMap[j][i] = prevMaxLoadArrMap[j][i] - box.getWeight_kg();
                    // prevMaxLoadArrMap[z][x]
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
    public Rectangl canPlaceBoxForArea(Box box) {
        int currentAvailableArea = levels.get(currentLevel).getCurrentAvailableArea();


        System.out.println("Осталось площадей - " + currentAvailableArea);
        if (currentAvailableArea >= box.getArea()) {

            Rectangl freeAreaOnLevel = levels.get(currentLevel).findRectangles(levels.get(currentLevel).getOccupiedArrMap(), box.getLength_mm(), box.getWidth_mm(), levels.get(currentLevel).getHeightArrMap());
            if (!freeAreaOnLevel.isExists) {
                return null; //todo исправить !!
            } else {
                return freeAreaOnLevel;
            }
        }

        return null;//todo исправить !!
    }

    /**
     * Проверка на максимальную высоту
     *
     * @param box коробка, которую хотим положить на этот уровень
     * @return true, если можем положить эту коробку на этот уровень
     */
    public boolean canPlaceBoxForMaximumTotalHeight(Box box) {
        int remainderMaxHeight = currentHeightPallet + box.getHeight_mm();
        if (maximumTotalHeight - remainderMaxHeight < 0) {
            System.out.println("Высота коробки меньше оставшейся высоты");
        }
        return maximumTotalHeight - remainderMaxHeight > 0;
    }


    public boolean canAddBox(Box box) {
        if (levels.isEmpty()) {
            LevelArr level = new LevelArr(palletId,width, length, weight_limit_kg);
            levels.add(level);
            currentLevelHeight.add(0);
            currentLevel = 0;

        }
        Rectangl maxFreeAreaOnLevel = canPlaceBoxForArea(box);
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
        if (levels.isEmpty()) {
            LevelArr level = new LevelArr(palletId,width, length, weight_limit_kg);
            levels.add(level);
            currentLevelHeight.add(0);
            currentLevel = 0;
        } else {
            currentLevel++;
            LevelArr level = new LevelArr(palletId,width, length, weight_limit_kg);
            levels.add(level);
            currentLevelHeight.add(0);
            System.out.println("Добавлен новый уровень " + currentLevel);
        }
    }
}


/*
Параметры паллет:
- Размер основания (например, 800 мм × 1200 мм).
- Высота «поддона» (20 см) + максимальная общая высота (например, 1,8 м или 2,2 м от пола).
- Ограничения по максимальному весу паллеты (если есть).

 */