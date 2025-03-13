package ru.kudo.pallets.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kudo.pallets.*;
import ru.kudo.pallets.model.*;
import ru.kudo.pallets.utils.EndPoint;

import java.util.*;

@RestController
@RequestMapping(value = EndPoint.ORDER)
public class OrderController {

    @GetMapping
    public SolutionResponse getBoxesOnPallet(@RequestBody Order order) {
        System.out.println("Получаем заказ и размещаем коробки на паллетах.");
        List<Pallet> pallets = FCNRAlgorithm.stackingAlgorithm(order);

        SolutionResponse solutionResponse = new SolutionResponse();

        Solution solution = new Solution();
        solution.setId(UUID.fromString("c3f7a536-5155-46d2-8584-e33c37f8464a"));
        solution.setDescription("Super-Algorithm-V1");
        solution.setCalculated(new Date());

        Container container = new Container();
        container.setId(pallets.get(0).getPalletId());
        container.setXCoord(0);
        container.setYCoord(0);
        container.setZCoord(0);
        container.setHeight(pallets.get(0).getHeight());
        container.setWidth(pallets.get(0).getWidth());
        container.setLength(pallets.get(0).getLength());
        container.setUnit("mm");

        List<Good> goods = new ArrayList<>();
        for (int i = 0; i < pallets.get(0).getLevels().size(); i++) {

            LevelArr level = pallets.get(0).getLevels().get(i);
            // Создаем список товаров (Goods) лежащих на палете для фронта.
            for (int j = 0; j < level.getBoxes().size(); j++) {

                BoxInPallet box = level.getBoxes().get(j);
                Good good = new Good(
                        box.getArticle_id(), 0, box.getHeight_mm(), box.getWidth_mm(), box.getLength_mm(), false,
                        box.getXCoord(), box.getYCoord(), box.getZCoord(), 4000, 4000, 0, "Palette 1",
                        pallets.get(0).getPalletId(), order.getOrderGuid(), 1, true, true, false,
                        order.getOrderGuid()
                );
                goods.add(good);
            }

        }
        container.setGoods(goods);

        // Устанавливаем контейнер в Solution
        solution.setContainer(container);

        CalculationSource calculationSource = new CalculationSource();
        calculationSource.setTitle("Java");
        calculationSource.setStaticAlgorithm("4");
        solution.setCalculationSource(calculationSource);

        solutionResponse.setSolution(solution);
        return solutionResponse;
    }

}
