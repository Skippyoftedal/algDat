import java.util.Random;

public class Multiplication {

  public static void main(String[] args) {

    runTaskTestData();
    testAlgorithm(new int[]{1000, 2000, 5000, 10000}, 100000, 1);
    testAlgorithm(new int[]{1000, 2000, 5000, 10000}, 100000000, 2);
  }


  //method 1
  public static double multiply(double x, int n) {
    if (n == 0) {
      return 0;
    }
    if (n == 1) {
      return x;

    } else {
      return multiply(x, n - 1) + x;
    }
  }

  //method 2
  public static double multiply2(double x, int n) {
    if (n == 1) {
      return x;
    } else if ((n & 1) == 1) {
      return x + multiply2(x + x, (n - 1) / 2);
    } else {
      return multiply2(x + x, n / 2);
    }

  }


  //runs test data from the given task
  public static void runTaskTestData() {
    double expected1 = 32.5;
    double expected2 = 141.4;

    double method1test1 = multiply(2.5, 13);
    double method1test2 = multiply(10.1, 14);

    double method2test1 = multiply2(2.5, 13);
    double method2test2 = multiply2(10.1, 14);
    System.out.println("Test data from the given task:");
    System.out.print("------------------------------------------------------------\n");
    System.out.printf("| %-10s | %-20s | %-20s |%n", "Expected", "Method 1", "Method 2");
    System.out.printf("| %-10s | %-20s | %-20s |%n", expected1, method1test1, method2test1);
    System.out.printf("| %-10s | %-20s | %-20s |%n", expected2, method1test2, method2test2);
    System.out.print("------------------------------------------------------------\n");

  }


  //Tests method 2 using an array of teststages (n-values) and a given amount of
  //iterations (times that the test is run)
  private static void testAlgorithm(int[] testStages, int iterations, int method) {
    if (method > 2 || method < 1){
      throw new IllegalArgumentException("Method has to be either 1 or 2");
    }


    long[] timeElapsed = new long[testStages.length];

    for (int i = 0; i < testStages.length; i++) {
      if (method == 1) {
        timeElapsed[i] = runMethod1(testStages[i], iterations);
      } else {
        timeElapsed[i] = runMethod2(testStages[i], iterations);
      }
    }

    System.out.printf("Method %d:\n", method);
    System.out.print(
        "-----------------------------------------------------------------------------------\n");
    System.out.printf("| %-10s | %-20s | %-20s | %-20s |%n", "n", "amount of iterations",
        "Time (ms)", "Relative difference");

    for (int i = 0; i < timeElapsed.length; i++) {
      System.out.printf("| %-10s | %-20s | %-20s | %-20s |%n", testStages[i],
          iterations + " (10^" + Math.log10(iterations) + ")", timeElapsed[i],
          (double) timeElapsed[i] / timeElapsed[0]);

    }
    System.out.print(
        "-----------------------------------------------------------------------------------\n");

  }

  private static long runMethod1(int n, int dataPoints) {

    double[] data = testData(dataPoints);
    long startTime = System.currentTimeMillis();

    for (double datum : data) {
      multiply(datum, n);
    }

    long endTime = System.currentTimeMillis();
    return endTime - startTime;
  }

  private static long runMethod2(int n, int iterations) {

    double[] data = testData(iterations);
    long startTime = System.currentTimeMillis();

    for (double iteration : data) {
      multiply2(iteration, n);
    }

    long endTime = System.currentTimeMillis();
    return endTime - startTime;
  }

  private static double[] testData(int amount) {
    double[] data = new double[amount];

    Random random = new Random();

    for (int i = 0; i < data.length; i++) {
      data[i] = random.nextDouble();
    }

    return data;
  }


}
