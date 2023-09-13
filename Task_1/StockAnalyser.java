import java.util.ArrayList;
import java.util.Random;

public class StockAnalyser {

  public record Result(int currentMax, int buyDay, int sellDay) {
  }

  public static void main(String[] args) {

    System.out.println(
        "Arraylength: 1000, Amount of arrays: 1000, Completion time:" + timeTester(1000, 1000)
            + "ms");
    System.out.println(
        "Arraylength: 2000, Amount of arrays: 1000, Completion time:" + timeTester(2000, 1000)
            + "ms");

    System.out.println(
        "Arraylength: 10000, Amount of arrays: 1000, Completion time:" + timeTester(10000, 1000)
            + "ms");

    Result result = optimalStockProfit(new int[]{-1, 3, -9, 2, 2, -1, 2, -1, -5});

    System.out.printf("Example from the book:\nProfit: %d, Day of purchase: %d, Date of sale: %d", result.currentMax,
        result.buyDay, result.sellDay);


  }

  //Algorithm for finding the optimal
  public static Result optimalStockProfit(int[] nums) {
    int[] pricePerDay = new int[nums.length];

    //Find the price of the stock for each day
    int currentPrice = 0;
    for (int i = 0; i < pricePerDay.length; i++) {
      currentPrice += nums[i];
      pricePerDay[i] = currentPrice;
    }

    //Find the maximum possible profit to be made from buying and selling once
    int buyDay = 0;
    int sellDay = 0;
    int currentMax = 0;
    for (int i = 0; i < pricePerDay.length - 1; i++) {

      for (int j = i + 1; j < pricePerDay.length; j++) {
        if (currentMax < pricePerDay[j] - pricePerDay[i]) {
          currentMax = pricePerDay[j] - pricePerDay[i];
          buyDay = i;
          sellDay = j;
        }
      }
    }
    return new Result(currentMax, buyDay, sellDay);
  }

  //Tests how long time a test elapses from a given array length and amount of arrays
  public static long timeTester(int arrayLength, int amountOfArrays) {
    ArrayList<int[]> data = testData(arrayLength, amountOfArrays);

    long startTime = System.currentTimeMillis();
    for (int[] test : data) {
      optimalStockProfit(test);
    }

    long endTime = System.currentTimeMillis();
    return endTime - startTime;
  }

  //Creates test data from the given arrayLength and amountOfData
  public static ArrayList<int[]> testData(int arrayLength, int amountOfData) {
    Random random = new Random(10);

    ArrayList<int[]> randomArray = new ArrayList<>();

    for (int i = 0; i < amountOfData; i++) {
      int[] array = new int[arrayLength];
      for (int j = 0; j < arrayLength; j++) {
        //Random number from -100 to 100
        array[j] = random.nextInt(200) - 100;
      }
      randomArray.add(array);
    }

    return randomArray;
  }

}
