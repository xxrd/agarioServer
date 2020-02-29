import java.util.ArrayList;

public class FoodList {
    private ArrayList<Food> foodList = new ArrayList<Food>();
    private int notEatenFoodCount;

    public FoodList(int toAdd) {
        setNotEatenFoodCount(toAdd);
        while(toAdd-- != 0) {
            addFood();
        }
    }

    public void addFood() {
        int numberOfCandidates = 10;
        float maxDistance = 0;
        Food bestCandidate = new Food();

        if (foodList.size() == 0) {
            Food candidate = new Food();
            foodList.add(candidate);
            return;
        }
        for (int i = 0; i < numberOfCandidates; i++) {
            Food candidate = new Food();
            float minDistance = Util.getDistance(candidate, foodList.get(0));

            for(Food food : foodList) {
                if(food.isEaten()) continue;
                float distance = Util.getDistance(candidate, food);
                if(distance < minDistance) {
                    minDistance = distance;
                }
            }

            if (minDistance > maxDistance) {
                bestCandidate = candidate;
                maxDistance = minDistance;
            }
        }
        foodList.add(bestCandidate);
    }

    public void addFood(int n) {
        int numberOfCandidates = 10;
        float maxDistance = 0;
        Food bestCandidate = new Food();

        if (foodList.size() == 0) {
            Food candidate = new Food();
            foodList.set(n, candidate);
            return;
        }
        for (int i = 0; i < numberOfCandidates; i++) {
            Food candidate = new Food();
            float minDistance = Util.getDistance(candidate, foodList.get(0));

            for(Food food : foodList) {
                if(food.isEaten()) continue;
                float distance = Util.getDistance(candidate, food);
                if(distance < minDistance) {
                    minDistance = distance;
                }
            }

            if (minDistance > maxDistance) {
                bestCandidate = candidate;
                maxDistance = minDistance;
            }
        }
        foodList.set(n, bestCandidate);
    }

    public void topUp(int minSize, int maxSize) {
        if(getNotEatenFoodCount() >= minSize) return;

        for(int i = 0; i < maxSize; i++) {
            if(foodList.get(i).isEaten()) {
                addFood(i);
                changeNotEatenFoodCount(1);
            }
        }
    }

    public ArrayList<Food> getList() {
        return foodList;
    }

    public void changeNotEatenFoodCount(int n) {
        notEatenFoodCount += n;
    }

    public void setNotEatenFoodCount(int n) {
        notEatenFoodCount = n;
    }

    public int getNotEatenFoodCount() {
        return notEatenFoodCount;
    }
}