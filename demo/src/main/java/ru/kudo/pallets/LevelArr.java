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
    private int[][] heightArrMap;


    private List<BoxInPallet> boxes;

    private final int palletWidth;
    private final int palletLength;

    public LevelArr(int palletWidth, int palletLength, int weight_limit_kg) {
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

            BoxInPallet boxInPallet = new BoxInPallet(box, "first", currentX, currentY, heightArrMap[currentY][currentX]);
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
            BoxInPallet boxInPallet = new BoxInPallet(box, "first", currentY, currentX, heightArrMap[currentY][currentX]);
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

    @Deprecated
    public MaxFreeAreaOnLevel getMeUninterruptedFreeArea(Box box) {
        int maxY = 0;
        int maxX = 0;

        UniqueIndexedList<Integer> y = new UniqueIndexedList<>();
        UniqueIndexedList<Integer> x = new UniqueIndexedList<>();

        int startLongestRowX = 0;
        int endLongestRowX = 0;
        int startLongestRowY = 0;
        int endLongestRowY = 0;


        int startLongestColumnX1 = 0;
        int endLongestColumnX2 = 0;

        int startLongestColumnY1 = 0;
        int endLongestColumnY2 = 0;

// ищем самую длинную строку в матрице + ее координаты
        for (int i = 0; i < occupiedArrMap.length; i++) {
            int currentMax = 0;
            boolean flag = true;
            int tempLongestX = 0;
            int tempLongestY = 0;
            for (int j = 0; j < occupiedArrMap[i].length; j++) {
                if (occupiedArrMap[i][j] == 0) {
                    currentMax++;
//                    levelArr[i][j]=2;
                    if (flag) {
                        tempLongestX = j;
                        tempLongestY = i;
                        flag = false;
                    }

                } else {

                    if (currentMax > maxX) {
                        maxX = currentMax;
                        startLongestRowX = tempLongestX;
                        startLongestRowY = tempLongestY;
                    }
                    if (currentMax == maxX && currentMax != 0) {
                        endLongestRowX = j;
                        endLongestRowY = i;
                    }
                    currentMax = 0;
                }
            }

            if (currentMax > maxX) {
                maxX = currentMax;
                startLongestRowX = tempLongestX;
                startLongestRowY = tempLongestY;

                endLongestRowY = occupiedArrMap[i].length - 1;
            }
            if (currentMax == maxX) {
                endLongestRowX = i;
            }

        }
        //ищем самую длинную колонку в матрице + ее координаты
        for (int j = 0; j < occupiedArrMap.length; j++) {
            int currentMax = 0;
            int beginningLongestY = 0;
            int beginningLongestX = 0;
            boolean flag = true;
            for (int i = 0; i < occupiedArrMap[j].length; i++) {
                if (occupiedArrMap[i][j] == 0) {
                    currentMax++;
//                    levelArr[y][x]=2;

                    if (flag) {
                        beginningLongestY = i;
                        beginningLongestX = j;
                        flag = false;
                    }
                } else {
                    if (currentMax > maxY) {
                        maxY = currentMax;
                        startLongestColumnX1 = beginningLongestX;
                        startLongestColumnY1 = beginningLongestY;
                    }
                    if (currentMax == maxY && currentMax != 0) { //todo лишнее условие
                        endLongestColumnX2 = j;
                        endLongestColumnY2 = i;
                    }
                    currentMax = 0;
                }
            }
            if (currentMax > maxY) {
                maxY = currentMax;
                startLongestColumnX1 = beginningLongestX;
                startLongestColumnY1 = beginningLongestY;
                endLongestColumnX2 = occupiedArrMap[j].length - 1;
            }
            if (currentMax == maxY) {
                endLongestColumnY2 = j;
            }
        }
        if (maxX < box.getLength_mm() || maxY < box.getWidth_mm()) {
            if (maxX < box.getWidth_mm() || maxY < box.getLength_mm()) {
                System.out.println("Не могу поместить коробку: " + box + " на уровне: " + this);
                //todo исправить
                return null;
            }
        }
        MaxFreeAreaOnLevel maxFreeAreaOnLevel;
// аксиома ? самая длинная всегда будем проходить через все самые большие участки ?
        int[][] draftArrMap = new int[palletWidth][palletLength];
        if (maxX >= maxY && maxX >= box.getLength_mm()) {
            int needY = box.getWidth_mm();
//ищем координаты начала самой длинной строки
            // пройтись по maxX и проверить что по needY все свободно
            for (int j = startLongestRowX; j <= endLongestRowX; j++) {
                int currentMaxX = 0;
                boolean flag = true;
                int beginningLongestX = 0;
                for (int i = startLongestRowY; i <= endLongestRowY; i++) {
                    //occupiedArrMap[y][x]

                    if (occupiedArrMap[i][j] == 0) {
                        draftArrMap[i][j] = 2;
                        currentMaxX++;
                        if (flag) {
                            beginningLongestX = i;
                            flag = false;
                        }
                        if (currentMaxX == needY) {
                            y.add(beginningLongestX);
                            x.add(j);
                            y.add(i);

                        }
                    } else {
                        break;
                    }
                }

            }

            int leftX = x.get(0);
            int topY = y.get(0);
            int rightX = x.get(x.size() - 1);
            int bottomY = y.get(y.size() - 1);
            maxFreeAreaOnLevel = new MaxFreeAreaOnLevel(x.size() > 0, maxX, maxY, leftX, rightX, topY, bottomY);

        } else {
            System.out.println("!! Напиши функцию для поиска по Y");

            int needX = box.getWidth_mm();


            for (int i = startLongestColumnY1; i <= endLongestColumnY2; i++) {
                int currentMax = 0;
                boolean flag = true;
                int beginningLongestY = 0;
                for (int j = startLongestColumnX1; j <= endLongestColumnX2; j++) {
                    //occupiedArrMap[y][x]
                    if (occupiedArrMap[i][j] == 0) {
                        currentMax++;
                        draftArrMap[i][j] = 2;
                        if (flag) {
                            beginningLongestY = j;
                            flag = false;
                        }
                        if (currentMax == needX) {
                            x.add(beginningLongestY);
                            y.add(i);
                            x.add(j);


                        }
                    } else {

                        break;
                    }
                }

            }
            int leftX = x.get(0);
            int rightX = x.get(x.size() - 1);
            int topY = y.get(0);
            int bottomY = y.get(y.size() - 1);

            maxFreeAreaOnLevel = new MaxFreeAreaOnLevel(x.size() > 0, maxY, maxX, leftX, rightX, topY, bottomY);


        }


        return maxFreeAreaOnLevel;
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

