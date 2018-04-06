package io.sciapps;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

interface FoodDoneCallback {
    public void foodDone(float quantityInKilograms);
}

public class FoodService {
    static ExecutorService executor = Executors.newSingleThreadExecutor();

    static void makeFood(String foodName,float quantityInKilograms, FoodDoneCallback done) {
        System.out.printf(
                "%s FoodService::makeFood() now cooking %f kg. , will lose 10%% of the original weight.%n",
                Thread.currentThread().getName(),
                quantityInKilograms
        );

        try {
            Thread.sleep(1000 * (long)quantityInKilograms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        quantityInKilograms *= 0.9; // loosing 10% of weight 'cause of coocking it
        System.out.printf(
                "%s FoodService::makeFood() %s is ready with %s kg. now calling \"foodDone\" callback%n",
                Thread.currentThread().getName(),
                quantityInKilograms,
                foodName
        );

        if (done != null) {
            done.foodDone(quantityInKilograms);
        }
    }
}
