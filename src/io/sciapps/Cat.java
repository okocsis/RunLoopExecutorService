package io.sciapps;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Cat {

    static ExecutorService executor = Executors.newSingleThreadExecutor();

    static void miau(int count) {

        for (int i = 0; i < count; i++) {
            System.out.printf("%s Cat::miau(%s)%n",Thread.currentThread().getName(), count);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Main.executor.submit(() -> Main.onNeedToFeedMyCat(count*count));
    }

    static void onGotFood(String with, float quantityInKilograms) {
        System.out.printf("%s Cat::onGotFood(%s, %s)%n", Thread.currentThread().getName(), with, quantityInKilograms);
        Cat.eating(quantityInKilograms);
    }

    private static void eating(float quantityInKilograms) {
        System.out.printf("%s Cat::eating(%s)%n", Thread.currentThread().getName(), quantityInKilograms);
        try {
            Thread.sleep(500 * (long)quantityInKilograms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(
                Thread.currentThread().getName() + " Cat::eating() done, now purring"
        );
        Main.executor.submit(() -> Main.onCatPurring());


    }


}
