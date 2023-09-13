import java.util.Arrays;
import java.util.Random;



public class Sorting {

  public static void main(String[] args) {
    printComparison();
  }


  //improved quicksort with countingsort
  public static void improvedQuicksort(int[] array, int left, int right) {

    if (array[right + 1] - array[left - 1] < right - left ) {
      countingSortWithInterval(array, array[right + 1], left, right);
    } else {

      if (right - left > 2) {
        int divisor = split(array, left, right);
        improvedQuicksort(array, left, divisor - 1);
        improvedQuicksort(array, divisor + 1, right);

      } else {
        median3sort(array, left, right);
      }
    }

  }



  private static long timerNormal(int[] a) {

    int sumBefore = getSum(a);

    long start = System.currentTimeMillis();
    quicksort(a, 0, a.length - 1);

    long end = System.currentTimeMillis();
    int sumAfter = getSum(a);

    if (!checkSorted(a) || sumBefore != sumAfter){
      throw new RuntimeException("Error while sorting while using normal quicksort");
    }

    return end - start;

  }

  private static long timerImproved(int[] a) {

    int sumBefore = getSum(a);

    long start = System.currentTimeMillis();

    setLargestAndSmallest(a);

    improvedQuicksort(a, 1, a.length - 2);

    long end = System.currentTimeMillis();
    int sumAfter = getSum(a);


    if (!checkSorted(a) || sumBefore != sumAfter){
      throw new RuntimeException("Error while sorting while using normal quicksort");
    }

    return end - start;

  }

  //Sets the highest value to the last element, and the lowest to the first element
  private static void setLargestAndSmallest(int[] a) {
    int max = 0;
    int maxI = 0;

    for (int i = 0; i < a.length; i++) {
      if (max < a[i]) {
        max = a[i];
        maxI = i;
      }

    }
    swap(a, maxI, a.length - 1);

    int min = a[0];
    int minI = 0;
    for (int i = 0; i < a.length; i++) {
      if (a[i] < min) {
        min = a[i];
        minI = i;
      }
    }

    swap(a, 0, minI);

  }

  //Normal quicksort
  public static void quicksort(int[] array, int left, int right) {
    if (right - left > 2) {
      int divisor = split(array, left, right);
      quicksort(array, left, divisor - 1);
      quicksort(array, divisor + 1, right);

    } else {
      median3sort(array, left, right);
    }
  }



  //uses countingsort to sort the array from and between the given left and right indexes.
  public static void countingSortWithInterval(int[] inn, int k, int left, int right) {

    int i;
    int[] ht = new int[k + 1];
    int[] ut = new int[inn.length];

    for (i = left; i < right + 1; ++i) {
      ++ht[inn[i]];
    }

    for (i = 1; i <= k; ++i) {
      ht[i] += ht[i - 1];
    }

    for (i = right; i >= left; --i) {
      ut[ht[inn[i]] - 1] = inn[i];
      --ht[inn[i]];
    }

    if (right + 1 - left >= 0) {
      System.arraycopy(ut, 0, inn, left, right + 1 - left);
    }
  }

  //retrieved from book
  private static int median3sort(int[] array, int left, int right) {
    int mid = (left + right) / 2;
    if (array[left] > array[mid]) {
      swap(array, left, mid);
    }
    if (array[mid] > array[right]) {
      swap(array, mid, right);
      if (array[left] > array[mid]) {
        swap(array, left, mid);
      }
    }
    return mid;
  }

  //Swaps two integers in an array, given by index a and b
  private static void swap(int[] array, int a, int b) {
    int temp = array[a];
    array[a] = array[b];
    array[b] = temp;
  }

  //retrieved from book
  private static int split(int[] array, int left, int right) {
    int iv, ih;
    int m = median3sort(array, left, right);
    int dv = array[m];
    swap(array, m, right - 1);
    for (iv = left, ih = right - 1; ; ) {
      while (array[++iv] < dv)
        ;
      while (array[--ih] > dv)
        ;
      if (iv >= ih) {
        break;
      }
      swap(array, iv, ih);
    }
    swap(array, iv, right - 1);
    return iv;
  }

  //Creates an int array of n length, where each value ranges from the given lower to
  //upper value
  private static int[] createTestArray(int n, int lower, int upper) {
    int[] data = new int[n];

    Random random = new Random();

    for (int i = 0; i < data.length; i++) {
      data[i] = random.nextInt(lower, upper + 1);
    }

    return data;
  }

  //Checks that an array is sorted
  private static boolean checkSorted(int[] sortedArray) {
    for (int i = 0; i < sortedArray.length - 1; i++) {
      if (sortedArray[i] > sortedArray[i + 1]) {
        return false;
      }
    }
    return true;
  }

  //Gives a sum of all values present in a given array
  private static int getSum(int[] array) {
    return Arrays.stream(array).sum();
  }


  private static void printComparison(){


    //Small interval of possible values
    int[] nValues = new int[]{100000, 1000000, 10000000};
    System.out.println("Test where the possible integers are small (0-100)");
    System.out.println("____________________________________________________________");
    System.out.printf("| %-10s | %-20s | %-20s |%n", "Quicksort", "Quicksort improved", "n");
    for (int i = 0; i < nValues.length; i++) {
      int[] a = createTestArray(nValues[i], 0, 100000);
      int[] b = new int[a.length];
      System.arraycopy(a, 0, b, 0, a.length);

      long timeA = timerNormal(a);
      long timeB = timerImproved(b);
      System.out.printf("| %-10s | %-20s | %-20s |%n", timeA, timeB, nValues[i]);

    }
    System.out.println("-------------------------------------------------------------");

    //test high interval of possible values

    System.out.println("\nTest where the possible integers are larger (0-Integer.Max)");
    System.out.println("____________________________________________________________");
    System.out.printf("| %-10s | %-20s | %-20s |%n", "Quicksort", "Quicksort improved", "n");
    for (int i = 0; i < nValues.length; i++) {
      int[] a = createTestArray(nValues[i], 0, Integer.MAX_VALUE - 1);
      int[] b = new int[a.length];
      System.arraycopy(a, 0, b, 0, a.length);

      long timeA = timerNormal(b);
      long timeB = timerImproved(a);
      System.out.printf("| %-10s | %-20s | %-20s |%n", timeA, timeB, nValues[i]);

    }
    System.out.println("-------------------------------------------------------------");


    //Test that there are no n^2 problems
    System.out.println("\nTest to check for no n^2 problems for improved quicksort");
    System.out.println("________________________________________________");
    System.out.printf("| %-20s | %-20s |%n", "Improved quicksort", "relative difference");
    int[] a = createTestArray(1000000, 0, 1000000);
    long first = timerImproved(a);
    System.out.printf("| %-20s | %-20s |%n", first, 1);
    for (int i = 0; i < 10; i++) {
      long timeElapsed = timerImproved(a);
      System.out.printf("| %-20s | %-20s |%n", timeElapsed, (double)timeElapsed/first);
    }
    System.out.println("-------------------------------------------------------------");


    //check for many duplicates, where half the numbers are 42
    System.out.println("\nTest where every other number is 42, n= 1.000.000, max value = 10.000.000");
    System.out.println("________________________________________________");
    System.out.printf("| %-20s | %-20s | %n", "Quicksort", "Improved quicksort");

    Random random = new Random();
    for (int i = 0; i < 10; i++) {
      int[] duplicateArray = new int[1000000];
      int[] array2 = new int[1000000];
      for (int j = 0; j < 1000000; j++) {
          if (j % 2 == 0){
            duplicateArray[j] = 42;
          } else{
            duplicateArray[j] = random.nextInt(0, 10000000);
          }
      }
      System.arraycopy(duplicateArray, 0, array2, 0, duplicateArray.length);
      System.out.printf("| %-20s | %-20s | %n", timerNormal(array2), timerImproved(duplicateArray));

    }
    System.out.println("--------------------------------------------------");


    //sorting the array: 1, 3, 6, 9, 12, 15,

    System.out.println("Test following: 1,3,6,9,12... , n= 1.000.000");
    System.out.println("________________________________________________");
    System.out.printf("| %-20s | %-20s | %n", "Normal", "Improved quicksort");
    for (int i = 0; i < 10; i++) {
      int[] array = new int[1000000];
      int[] array2 = new int[array.length];
      System.arraycopy(array, 0, array2, 0, array.length);
      for (int j = 1; j < 1000000; j++) {
        array[j - 1] = j*3;
      }
      System.out.printf("| %-20s | %-20s | %n", timerNormal(array2), timerImproved(array));

    }
    System.out.println("--------------------------------------------------");









  }





  //Checks different cases of sorting to check if the algorithm works
  private static void testSorted() {
    boolean errorInImproved = false;
    boolean errorInNormal = false;
    for (int i = 10; i < 1000000; i*=2) {
      int[] a = createTestArray(i, 0, 1000);
      int before = getSum(a);
      setLargestAndSmallest(a);
      improvedQuicksort(a, 1, a.length - 2);
      int after = getSum(a);

      if (!checkSorted(a) || before != after){
        errorInImproved = true;
        break;
      }

      int[] b = createTestArray(i, 0, 1000);
      before = getSum(b);

      quicksort(a, 0, a.length - 1);

      after = getSum(b);

      if (!checkSorted(a) || before != after){
        errorInNormal = true;
        break;
      }
    }
    if (errorInImproved){
      System.out.println("Error in improved algorithm");
    } else if (errorInNormal){
      System.out.println("Error in normal quicksort algorithm");
    }


  }

}
