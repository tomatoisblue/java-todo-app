import java.lang.Comparable;

class ToDo implements Comparable<ToDo> {
  String title;
  int importance;


  public ToDo(String title, int importance) {
    this.title = title;
    this.importance = importance;
  }

  String showStatus() {
      return String.format("%s  重要度:%d", this.title, this.importance);
  }

  void changeImportance(int importance) {
    this.importance = importance;
    System.out.println("重要度を" + importance + "に。変更しました。");
  }

  String toCSV() {
    return String.format("%s,%d", this.title, this.importance);
  }

  @Override
  public int compareTo(ToDo other) {
    return Integer.compare(this.importance, other.importance);
  }

}