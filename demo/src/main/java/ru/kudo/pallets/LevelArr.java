package ru.kudo.pallets;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.*;

/**
 * Класс, представляющий уровень на котором можно хранить коробки.
 */
@Getter
@Setter
@ToString
public class LevelArr {
    private int[][] occupiedArrMap;
    private int[][] maxLoadArrMap;

    private List<BoxInPallet> boxes;

    private final int palletWidth;
    private final int palletLength;

    public LevelArr(int palletWidth, int palletLength, int weight_limit_kg) {
        this.occupiedArrMap = new int[palletWidth][palletLength];
        this.maxLoadArrMap = new int[palletWidth][palletLength];
        for (int i = 0; i < palletWidth; i++) {
            for (int j = 0; j < palletLength; j++) {
                maxLoadArrMap[i][j] = weight_limit_kg;
            }
        }
        this.boxes = new ArrayList<>();
        this.palletWidth = palletWidth;
        this.palletLength = palletLength;
    }

    /*
   [
   [0,0,0,0,0,0,0,0,0],
   [0,0,0,0,0,0,0,0,0],
   [0,0,0,0,0,0,0,0,0],
   [0,0,0,0,0,0,0,0,0],
   [0,0,0,0,0,0,0,0,0],
   ]
     */
    public BoxInPallet addBox(Box box) {
        System.out.println(box.toString());

        MaxFreeAreaOnLevel maxFreeAreaOnLevel = getMeUninterruptedFreeArea();

        int currentY = maxFreeAreaOnLevel.getTopY();
        int currentX = maxFreeAreaOnLevel.getLeftX();
        if (box.getWidth_mm() >= maxFreeAreaOnLevel.getWidthUninterrupted() && box.getLength_mm() >= maxFreeAreaOnLevel.getLengthUninterrupted()) {
            for (int j = currentY; j < currentY + box.getWidth_mm(); j++) {
                for (int i = currentX; i < currentX + box.getLength_mm(); i++) {
                    occupiedArrMap[i][j] = 1;
                    maxLoadArrMap[i][j] = box.getMax_load_kg();
                }
            }
            BoxInPallet boxInPallet = new BoxInPallet(box, "first", currentX, currentY, 0);
            boxes.add(boxInPallet);
            System.out.println("Добавлена коробка: " + box + " на уровне: " + this);
            return boxInPallet;
        } else {
            for (int i = currentY; i < currentY + box.getWidth_mm(); i++) {
                for (int j = currentX; j < currentX + box.getLength_mm(); j++) {
                    occupiedArrMap[i][j] = 1;
                    maxLoadArrMap[i][j] = box.getMax_load_kg();
                }
            }
            BoxInPallet boxInPallet = new BoxInPallet(box, "first", currentY, currentX, 0);
            boxes.add(boxInPallet);
            System.out.println("Добавлена коробка: " + box + " на уровне: " + this);
            return boxInPallet;
        }
    }

    public int getCurrentAvailableArea() {
        // посчитать свободное место на уровне
        int availableArea = 0;
        for (int i = 0; i < occupiedArrMap.length; i++) {
            for (int j = 0; j < occupiedArrMap[i].length; j++) {
                if (occupiedArrMap[i][j] == 0) {
                    availableArea++;
                }
            }
        }
        return availableArea;
    }

    //todo необходим рефакторинг(сократить кол-во вызовов + сам алгоритм)
    public MaxFreeAreaOnLevel getMeUninterruptedFreeArea() {
        int maxY = 0;
        int maxX = 0;

        UniqueIndexedList<Integer> y = new UniqueIndexedList<>();
        UniqueIndexedList<Integer> x = new UniqueIndexedList<>();

        int uninterruptedX = 0;
        int uninterruptedY = 0;


// ищем самую длинную строку в матрице
        for (int i = 0; i < occupiedArrMap.length; i++) {
            int currentMax = 0;

            for (int j = 0; j < occupiedArrMap[i].length; j++) {
                if (occupiedArrMap[i][j] == 0) {
                    currentMax++;
//                    levelArr[i][j]=2;
                } else {

                    if (currentMax > maxX) {
                        maxX = currentMax;
                    }
                    currentMax = 0;
                }
            }
            if (currentMax > maxX) {
                maxX = currentMax;
            }
        }
        //ищем самую длинную колонку в матрице
        for (int j = 0; j < occupiedArrMap.length; j++) {
            int currentMax = 0;
            for (int i = 0; i < occupiedArrMap[j].length; i++) {
                if (occupiedArrMap[i][j] == 0) {
                    currentMax++;
//                    levelArr[i][j]=2;
                } else {
                    if (currentMax > maxY) {
                        maxY = currentMax;
                    }
                    currentMax = 0;
                }
            }
            if (currentMax > maxY) {
                maxY = currentMax;
            }
        }

        MaxFreeAreaOnLevel maxFreeAreaOnLevel;

        if (maxX >= maxY) {
//ищем координаты начала самой длинной строки
            int tempX = 0;
            for (int i = 0; i < occupiedArrMap.length; i++) {
                int currentMaxX = 0;
                boolean flag = true;
                int beginningLongestX = 0;
                for (int j = 0; j < occupiedArrMap[i].length; j++) {
                    if (occupiedArrMap[i][j] == 0) {
                        currentMaxX++;
                        if (flag) {
                            beginningLongestX = j;
                            flag = false;
                        }
                        if (currentMaxX == maxX) {
                            x.add(beginningLongestX);
                            y.add(i);
                            tempX++;
                        }
                    } else {
                        break;
                    }
                }
                if (tempX > uninterruptedX) {
                    uninterruptedX = tempX;
                } else {
                    tempX = 0;
                }
            }
            int leftX = x.get(0);
            int topY = y.get(0);
            int rightX = x.get(x.size() - 1);
            int bottomY = y.get(y.size() - 1);
            maxFreeAreaOnLevel = new MaxFreeAreaOnLevel(maxX, uninterruptedX, leftX, rightX, topY, bottomY);

        } else {
            System.out.println("!! Напиши функцию для поиска по Y");
            int tempY = 0;
            int j = 0;
            for (; j < occupiedArrMap.length; j++) {
                int currentMax = 0;
                boolean flag = true;
                int beginningLongestY = 0;
                for (int i = 0; i < occupiedArrMap[j].length; i++) {
                    if (occupiedArrMap[i][j] == 0) {
                        currentMax++;
//                        levelArr[i][j] =2;
                        if (flag) {
                            beginningLongestY = i;
                            flag = false;
                        }
                        if (currentMax == maxY) {
                            y.add(beginningLongestY);
                            y.add(i);
                            x.add(j);

                            tempY++;
                        }
                    } else {

                        break;
                    }
                }
                if (tempY > uninterruptedY) {
                    uninterruptedY = tempY;
                } else {
                    tempY = 0;
                }
            }
            int leftX = x.get(0);
            int rightX = x.get(x.size() - 1);
            int topY = y.get(0);
            int bottomY = y.get(y.size() - 1);

            maxFreeAreaOnLevel = new MaxFreeAreaOnLevel(maxY, uninterruptedY, leftX, rightX, topY, bottomY);


        }


        return maxFreeAreaOnLevel;
    }


}