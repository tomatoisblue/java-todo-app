import java.util.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ToDoApp {
  static void sortList(ArrayList<ToDo> list) {
    Collections.sort(list, Collections.reverseOrder());
  }

  static void displayList(ArrayList<ToDo> list) {
    sortList(list);
    System.out.println("\n");
    System.out.println("####################");
    System.out.println(" ###現在のリスト###");
    System.out.println("####################");
    System.out.println("\n");
    for(int i=0; i<list.size(); i++) {
      System.out.printf("%d : %s%n", i, list.get(i).showStatus());
    }
    System.out.println("####################");
    System.out.println("\n");
  }

  static void addItem(ArrayList<ToDo> list, Scanner sc) {
    ToDo todo;

    String title;
    while(true) {
      try {
        System.out.println("新規作成をします。内容を入力してください。");
        title = sc.nextLine();
        if(!title.isEmpty()) throw new EmptyContentException("空のタスクは作成できません。");
        break;
      } catch (EmptyContentException e) {
        sc.nextLine();
        System.out.printf("エラー: %s\n", e.getMessage());
      } catch (Exception e) {
        sc.nextLine();
      }
    }

    title = sc.nextLine();
    title = title.trim();

    int importance;

    while(true){
      System.out.println("重要度を1-10で入力してください");
      try {
        importance = sc.nextInt();
        if (importance > 0 && importance <= 10) {
          todo = new ToDo(title, importance);
          list.add(todo);
          break;
        }
      } catch (Exception e) {
        sc.nextLine();
      }
    }
  }

  static void updateItem(ArrayList<ToDo> list, Scanner sc) {
    int numberOfToDo = list.size();

    if (numberOfToDo == 0) {
      System.out.println("まだToDoがありません。");
      return ;
    }

    String rangeOfIndex;
    if (numberOfToDo > 1) {
      rangeOfIndex = "(0-" + (numberOfToDo - 1) + ")";
    } else {
      rangeOfIndex = "(0)";
    }


    int importance, index;
    while(true) {
      System.out.println("編集するToDoの番号" + rangeOfIndex + "を入力してください。");
      try {
        index = sc.nextInt();
        if (index < 0 || index > numberOfToDo - 1) throw new OutOfRangeException("番号が" + rangeOfIndex + "の範囲に設定されていません。");
        break;
      } catch (Exception e) {
        sc.nextLine();
      }
    }

    ToDo todo = list.get(index);

    while(true){
      System.out.println("重要度を1-10で入力してください");
      try {
        importance = sc.nextInt();
        if (importance >= 1 && importance <= 10) {
          break;
        }
      } catch (Exception e) {
        sc.nextLine();
      }
    }

    todo.changeImportance(importance);
    System.out.printf("###結果###\nタスク(番号:%d)の重要度を%dに変更しました。\n#########", index, importance);
  }

  static void deleteItem(ArrayList<ToDo> list, Scanner sc) {
    int numberOfToDo = list.size();

    if (numberOfToDo == 0) {
      System.out.println("まだToDoがありません。");
      return ;
    }

    String rangeOfIndex;
    if (numberOfToDo > 1) {
      rangeOfIndex = "(0-" + (numberOfToDo - 1) + ")";
    } else {
      rangeOfIndex = "(0)";
    }

    int index;
    while(true) {
      System.out.println("削除するToDoの番号" + rangeOfIndex + "を入力してください。");
      try {
        index = sc.nextInt();
        if (index < 0 || index > numberOfToDo - 1) throw new OutOfRangeException("番号が" + rangeOfIndex + "の範囲に設定されていません。");
        break;
      } catch (Exception e) {
        sc.nextLine();
      }
    }

    list.remove(index);
    System.out.printf("###結果###\n１件(番号:%d)削除しました。\n#########", index);
  }

  // Load a list of ToDo from CSV file.

  static ArrayList<ToDo> loadFile(File file) throws Exception {
    String fileName = "todo.csv";
    ArrayList<ToDo> list = new ArrayList<>();
    try (FileReader fr = new FileReader(fileName);
         BufferedReader bufferedReader = new BufferedReader(fr)) {
      String line;
      while((line = bufferedReader.readLine()) != null) {
        String[] fields = line.split(",");
        if(fields.length == 2 && !fields[0].isEmpty() && !fields[1].isEmpty()) {
          String title = fields[0];
          try {
            int importance = Integer.parseInt(fields[1]);
            if(importance < 1 || importance > 10) throw new OutOfRangeException("重要度が1～10の範囲に設定されていません。");

            ToDo todo = new ToDo(title, importance);
            list.add(todo);
          } catch(NumberFormatException e) {
            System.err.println("重要度が数字で書かれていません。");
          } catch(OutOfRangeException e) {
            System.err.println(e.getMessage());
          }
        }
      }

    } catch (IOException e) {
      System.err.println("An error occured.");
      e.printStackTrace();
    }

    return list;
  }

  // Save a list of Todo to CSV file.

  static void saveFile(File file, ArrayList<ToDo> list) throws FileSaveException {
    // Sort the list of ToDo
    sortList(list);

    try (FileWriter writer = new FileWriter(file)) {
      for(ToDo todo : list) {
        String outputToCSV = String.format("%s,%d\n", todo.title, todo.importance);
        writer.write(outputToCSV);
      }
    } catch (IOException e) {
      throw new FileSaveException("Failed to save : " + file.getAbsolutePath(), e);
    }
  }


  public static void main(String[] args) throws Exception {
    Scanner sc = new Scanner(System.in);
    ArrayList<ToDo> list;

    File file = new File("todo.csv");
    if(file.exists()) {
      list = loadFile(file);
    } else {
      list = new ArrayList<>();
    }

    if(list.size() == 0) {
      System.out.println("ToDoが1件もありません。");
    } else {
      displayList(list);
    }


    int select;
    while(true) {

      System.out.println("##############################");
      System.out.println("操作に対応する番号を入力してください");
      System.out.println("1:新規作成 2:重要度変更 3:削除 4:完了");
      System.out.println("##############################");

      try {
        select = sc.nextInt();
        if (select < 1 || select > 4) throw new OutOfRangeException("1-4の整数を入力してください。");
      } catch (OutOfRangeException e) {
        sc.nextLine();
        System.out.printf("エラー: %s\n", e.getMessage());
        continue;
      } catch (Exception e) {
        sc.nextLine();
        continue;
      }

      switch (select) {
        case 1:
          addItem(list, sc);
          break;
        case 2:
          updateItem(list, sc);
          break;
        case 3:
          deleteItem(list, sc);
          break;
        default:
          System.out.println("アプリケーションを終了します");
          try {
            saveFile(file, list);
            System.out.println("セーブしました。");
          } catch (FileSaveException e) {
            System.out.printf("エラー: %s\n", e.getMessage());
          }
          return;
      }
      displayList(list);
    }
  }
}