package de.nicograef.sudokutrainer.sudoku;

import java.util.LinkedList;

public class Field {

    public int size;
    public boolean solved;
    public Cell[] cells;

    private LinkedList<Row> rows;
    private LinkedList<Column> columns;
    private LinkedList<Subfield> subfields;

    public Field(int size, int[] values) {
        this.size = size;
        solved = false;
        cells = new Cell[size * size];

        rows = new LinkedList<Row>();
        columns = new LinkedList<Column>();
        subfields = new LinkedList<Subfield>();

        // create cells
        for (int i = 0; i < values.length; ++i) {
            cells[i] = new Cell(size, i, values[i], values[i] != 0);
        }

        // compute the starting indices for all the rows and columns
        for (int i = 0; i < size * size; ++i) {
            if (i % size == 0) {
                rows.add(new Row(size, cells, i));
            }
            if (i < size) {
                columns.add(new Column(size, cells, i));
            }
        }

        // compute the starting indices for all the subfields
        for (int i = 0; i < Math.sqrt(size); ++i) {
            for (int j = 0; j < Math.sqrt(size); ++j) {
                subfields.add(new Subfield(size, cells, (int) (i * size * Math.sqrt(size)) + (int) (j * Math.sqrt(size))));
            }
        }
    }

    public void computeOptions() {
        for (int i = 0; i < size * size; ++i) {
            if (cells[i].solved) {
                continue;
            }

            for (int j = 0; j < size; ++j) {

                // check the rows
                if (rows.get(j).cells.contains(cells[i])) {
                    for (int n = 0; n < size; ++n) {
                        cells[i].removeOption(rows.get(j).cells.get(n).value, rows.get(j).cells.get(n).solved);
                    }
                }

                // check the columns
                if (columns.get(j).cells.contains(cells[i])) {
                    for (int n = 0; n < size; ++n) {
                        cells[i].removeOption(columns.get(j).cells.get(n).value, columns.get(j).cells.get(n).solved);
                    }
                }

                // check the subfields
                if (subfields.get(j).cells.contains(cells[i])) {
                    for (int n = 0; n < size; ++n) {
                        cells[i].removeOption(subfields.get(j).cells.get(n).value, subfields.get(j).cells.get(n).solved);
                    }
                }
            }

        }
    }

    public void solveOneStep() {
        for (int i = 0; i < size * size; ++i) {
            if (cells[i].solved) {
                continue;
            }

            for (int j = 0; j < size; ++j) {

                // check the rows
                if (rows.get(j).cells.contains(cells[i])) {
                    for (int n = 0; n < size; ++n) {
                        cells[i].removeOption(rows.get(j).cells.get(n).value, rows.get(j).cells.get(n).solved);
                    }
                }

                if (checkForSolvedCells()) { return; }

                // check the columns
                if (columns.get(j).cells.contains(cells[i])) {
                    for (int n = 0; n < size; ++n) {
                        cells[i].removeOption(columns.get(j).cells.get(n).value, columns.get(j).cells.get(n).solved);
                    }
                }

                if (checkForSolvedCells()) { return; }

                // check the subfields
                if (subfields.get(j).cells.contains(cells[i])) {
                    for (int n = 0; n < size; ++n) {
                        cells[i].removeOption(subfields.get(j).cells.get(n).value, subfields.get(j).cells.get(n).solved);
                    }
                }

                if (checkForSolvedCells()) { return; }
            }

        }
    }

    public boolean checkForSolvedCells() {
        boolean nothingSolved = true;
        boolean allSolved = true;

        for (int i = 0; i < cells.length; ++i) {
            if (cells[i].solvedLastIteration()) {
                cells[i].value = cells[i].options.poll();
                cells[i].solved = true;
                nothingSolved = false;
            }

            if (!cells[i].solved) allSolved = false;
        }

        if (allSolved) solved = true;
        return !nothingSolved;
    }

    public int[] getSolution() {
        int[] solution = new int[size * size];

        for (int i = 0; i < solution.length; ++i) {
            solution[i] = cells[i].value;
        }

        return solution;
    }

    public void printAllOptions() {
        System.out.println("----- ALL OPTIONS -----");
        for (Cell c : cells) {
            System.out.println("Cell " + c.index + " has options: " + c.options);
        }
    }

}
