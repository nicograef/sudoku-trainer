package de.nicograef.sudokutrainer.sudoku;

import java.util.LinkedList;

public class Cell {
  
  public boolean solved;
  public int value;
  public int index;
  public int fieldsize;
  public LinkedList<Integer> options;
  
  public Cell(int fieldsize, int index, int value, boolean solved) {
    this.solved = solved;
    this.value = value;
    this.index = index;
    this.fieldsize = fieldsize;
    options = new LinkedList<Integer>();
    
    if (!solved) { for (int i = 1; i <= fieldsize; ++i) { options.add(i); } }
  }
  
  public void removeOption(Integer option, boolean condition) {
    if (condition) {
      if (options.contains(option)) { options.remove(option); }
    }
  }
  
  public boolean solvedLastIteration() {
    return (!solved && options.size() == 1);
  }

}