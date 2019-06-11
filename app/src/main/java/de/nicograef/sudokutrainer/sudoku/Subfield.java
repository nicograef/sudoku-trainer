package de.nicograef.sudokutrainer.sudoku;

import java.util.LinkedList;

import de.nicograef.sudokutrainer.sudoku.Cell;

public class Subfield {
  
  public LinkedList<Cell> cells;
  
  public Subfield(int fieldSize, Cell[] field, int startIndex) {
    cells = new LinkedList<Cell>();
    
    for (int i = 0; i < Math.sqrt(fieldSize); ++i) {
      for (int j = 0; j < Math.sqrt(fieldSize); ++j) {
        cells.add(field[startIndex + (i * fieldSize) + j]);
      }
    }
  }

}
