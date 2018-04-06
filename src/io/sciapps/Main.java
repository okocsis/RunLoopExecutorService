package io.sciapps;

public class Main {

    static RunLoopExecutorService executor = new RunLoopExecutorService();

    public static void main(String[] args) {

        executor.startRunLoop(() -> {
            System.out.println("Hello I'm the first task in the main run loop");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Cat.executor.submit(() -> Cat.miau(2));
        });
    }

    static void onNeedToFeedMyCat(int howHungry) {

        System.out.printf(Thread.currentThread().getName() + " onNeedToFeedMyCat(howHungry:%d)%n", howHungry);

        float foodNeeded = howHungry;
        String foodName = "caviar";
        FoodService.executor.submit(() -> {

            FoodService.makeFood(foodName, foodNeeded, (quantityInKilograms) -> {
                Main.executor.submit(() -> Main.onFoodReady(foodName, quantityInKilograms));
            });

        });
    }

    static void onFoodReady(String name, float quantityInKilograms) {

        System.out.printf(
                "%s onFoodReady(%s, %s)",
                Thread.currentThread().getName(),
                name,
                quantityInKilograms
        );
        Cat.executor.submit(() -> Cat.onGotFood(name, quantityInKilograms));
    }

    public static void onCatPurring() {
        System.out.printf(Thread.currentThread().getName() + " onCatPurring() :)) we are happy, terminateting...");

        FoodService.executor.shutdown();
        Cat.executor.shutdown();
        Main.executor.shutdown();

    }
}
