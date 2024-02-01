import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Stack;

public class BracketSolver {

  public static void main(String[] args) {
    Scanner scanner;
    try {
      scanner = new Scanner(new FileReader(args[0]));

    } catch (FileNotFoundException e) {
      System.out.println("Something went wrong while reading the given file");
      return;
    }

    while (scanner.hasNextLine()) {
      System.out.printf(" : %s\n", checkValidBrackets(scanner.next()));
    }
    scanner.close();

  }

  public static boolean checkValidBrackets(String s) {
    String result = s.replaceAll("[^\\[\\]{}()]", "");
    System.out.print(result);
    char[] chars = result.toCharArray();

    Stack<Character> stack = new Stack<>();
    HashSet<Character> leftBrackets = new HashSet<>();
    leftBrackets.add('{');
    leftBrackets.add('(');
    leftBrackets.add('[');

    for (char character : chars) {

      if (leftBrackets.contains(character)) {
        stack.add(character);

      } else {

        if (stack.isEmpty()) {
          return false;
        }

        switch (character) {
          case '}' -> {
            if (stack.pop() != '{') {
              return false;
            }
          }
          case ')' -> {
            if (stack.pop() != '(') {
              return false;
            }
          }
          case ']' -> {
            if (stack.pop() != '[') {
              return false;
            }
          }
        }
      }
    }
    return stack.isEmpty();
  }


}