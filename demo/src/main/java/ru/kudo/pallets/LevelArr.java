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
    private  UUID palletId;

    private int[][] occupiedArrMap;
    private int[][] maxLoadArrMap;
    private int[][] heightArrMap;


    private List<BoxInPallet> boxes;

    private final int palletWidth;
    private final int palletLength;

    public LevelArr(UUID palletId,int palletWidth, int palletLength, int weight_limit_kg) {
        this.occupiedArrMap = new int[palletWidth][palletLength];
        this.heightArrMap = new int[palletWidth][palletLength];
        this.maxLoadArrMap = new int[palletWidth][palletLength];
        for (int i = 0; i < palletWidth; i++) {
            for (int j = 0; j < palletLength; j++) {
                maxLoadArrMap[i][j] = weight_limit_kg;
            }
        }
        this.boxes = new ArrayList<>();
        this.palletWidth = palletWidth;
        this.palletId = palletId;
        this.palletLength = palletLength;
    }

    /*
java . есть двумерный массив чисел . Например   [
   [1,2,3,4,5,5,5,5,8],
   [1,2,3,4,5,5,5,8,8],
   [1,2,3,3,0,7,7,7,7],
   [0,2,3,3,0,7,7,7,7],
   [0,2,3,3,0,0,0,0,0],
   [0,2,3,3,0,0,0,0,0],
   ] Надо найти из него непрерывные последовательности цифр по вертикали и горизонтали с длиной больше 3 и шириной больше 2.
     */
    public BoxInPallet addBox(Box box) {
        System.out.println(box.toString());
        int[][] draftArrMap = new int[palletWidth][palletLength];
        Rectangl maxFreeAreaOnLevel = findRectangles(occupiedArrMap, box.getLength_mm(), box.getWidth_mm(), heightArrMap);
        int widthUninterrupted = maxFreeAreaOnLevel.getRightX() - maxFreeAreaOnLevel.getLeftX();
        int lengthUninterrupted = maxFreeAreaOnLevel.getBottomY() - maxFreeAreaOnLevel.getTopY();

        System.out.println(maxFreeAreaOnLevel);

        if (lengthUninterrupted > widthUninterrupted) {
            int currentY = maxFreeAreaOnLevel.getTopY();
            int currentX = maxFreeAreaOnLevel.getLeftX();
            for (int i = currentY; i < currentY + box.getLength_mm(); i++) {
                for (int j = currentX; j < currentX + box.getWidth_mm(); j++) {
                    //                    occupiedArrMap[y][x] = 1;
                    draftArrMap[i][j] = 1;
                    occupiedArrMap[i][j] = 1;
                    heightArrMap[i][j] = box.getHeight_mm();
                    maxLoadArrMap[i][j] = box.getMax_load_kg();//todo сделать нулевой уровень паллеты ?
                }
            }

            BoxInPallet boxInPallet = new BoxInPallet(box, palletId, currentX, currentY, heightArrMap[currentY][currentX]);
            boxes.add(boxInPallet);
            System.out.println("Добавлена коробка: " + box + " на уровне: " + this);
            return boxInPallet;
        } else {
            int currentY = maxFreeAreaOnLevel.getTopY();
            int currentX = maxFreeAreaOnLevel.getLeftX();
            for (int i = currentY; i < currentY + box.getWidth_mm(); i++) {
                for (int j = currentX; j < currentX + box.getLength_mm(); j++) {
//                    occupiedArrMap[y][x] = 1;
                    draftArrMap[i][j] = 1;
                    occupiedArrMap[i][j] = 1;
                    heightArrMap[i][j] = box.getHeight_mm();
                    maxLoadArrMap[i][j] = box.getMax_load_kg();
                }
            }
            //повернули коробку
            box.rotate90();
            BoxInPallet boxInPallet = new BoxInPallet(box, palletId, currentY, currentX, heightArrMap[currentY][currentX]);
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
    public Rectangl findRectangles(int[][] array, int length_mm, int width_mm, int[][] arrayHeight) {
        // поиск прямоугольников в матрице с учетом одинаковой высоты и ширины

        List<Rectangl> rectangles = new ArrayList<>();
        int rows = array.length;
        int cols = array[0].length;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int currentHeight = array[i][j];
                boolean isFreeArea = array[i][j] != 0;
//                occupiedArrMap[y][x] = 1;
                if (isFreeArea) {
                    //todo как то по другому бы
                    continue;
                }
                int maxWidth = findMaxWidth(array, i, j, currentHeight);
                int maxLength = findMaxHeight(array, i, j, currentHeight);

                // Проверяем, что область удовлетворяет условиям
                if (maxWidth >= length_mm && maxLength >= width_mm) {
                    Rectangl rectangle = new Rectangl(true, currentHeight, j, j + maxWidth - 1, i, i + maxLength - 1);
                    rectangles.add(rectangle);
                } else if (maxLength >= length_mm && maxWidth >= width_mm) {
                    Rectangl rectangle = new Rectangl(true, currentHeight, j, j + maxWidth - 1, i, i + maxLength - 1);
                    rectangles.add(rectangle);
                }
            }
        }
        Optional<Rectangl> minHeightRectangle = rectangles.stream()
                .min((r1, r2) -> Integer.compare(r1.height, r2.height));

        return minHeightRectangle.orElseGet(() -> new Rectangl(false, 0, 0, 0, 0, 0));
    }

    // Находит максимальную ширину прямоугольной области
    private static int findMaxWidth(int[][] array, int row, int col, int value) {
        int width = 0;
        while (col + width < array[0].length && array[row][col + width] == value) {
            width++;
        }
        return width;
    }

    // Находит максимальную высоту прямоугольной области
    private static int findMaxHeight(int[][] array, int row, int col, int value) {
        int height = 0;
        while (row + height < array.length && array[row + height][col] == value) {
            height++;
        }
        return height;
    }
}

