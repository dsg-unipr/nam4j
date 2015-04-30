package it.unipr.ce.dsg.examples.migration;

import it.unipr.ce.dsg.nam4j.impl.service.Service;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * <p>
 * This class represents an example of a {@link Service} with a thread providing
 * methods to get started, suspended, resumed and stopped. Such a
 * {@link Service} represents an iterative Sudoku game solver.
 * </p>
 * 
 * <p>
 * Copyright (c) 2011, Distributed Systems Group, University of Parma, Italy.
 * Permission is granted to copy, distribute and/or modify this document under
 * the terms of the GNU Free Documentation License, Version 1.3 or any later
 * version published by the Free Software Foundation; with no Invariant
 * Sections, no Front-Cover Texts, and no Back-Cover Texts. A copy of the
 * license is included in the section entitled "GNU Free Documentation License".
 * </p>
 * 
 * @author Alessandro Grazioli (grazioli@ce.unipr.it)
 * 
 */

public class SudokuService extends Service implements Serializable {
	
	private static final long serialVersionUID = -3578516506759956119L;
	private ServiceRunnableImplementation serviceRunnableImplementation = null;

	public SudokuService() {
		super();
		this.setId("SudokuService");
		this.setName("SudokuService");
		serviceRunnableImplementation = new ServiceRunnableImplementation();
	}
	
	@Override
	public ServiceRunnableImplementation getServiceRunnable() {
		return serviceRunnableImplementation;
	}
	
	public class ServiceRunnableImplementation extends ServiceRunnable {
		
		private static final long serialVersionUID = 2117356186065287266L;
		int[][] sudoku = {
				{6, 0, 0, 0, 0, 0, 0, 3, 0},
				{2, 0, 0, 1, 9, 0, 0, 0, 0},
				{0, 7, 0, 5, 0, 0, 0, 0, 0},
				{3, 0, 0, 0, 5, 0, 1, 8, 0},
				{0, 0, 2, 0, 0, 0, 5, 0, 0},
				{0, 4, 1, 0, 8, 0, 0, 0, 2},
				{0, 0, 0, 0, 0, 8, 0, 9, 0},
				{0, 0, 0, 0, 3, 2, 0, 0, 4},
				{0, 8, 0, 0, 0, 0, 0, 0, 3}
			};
		
		int[][] solution = new int[9][9];
		int emptyCellsListRowIndex = 0;
		
		ArrayList<int[]> emptyCells = new ArrayList<>();
		
		/** Class constructor. */
		public ServiceRunnableImplementation() {
			
			for (int r = 0; r < sudoku.length; r++)
				for (int c = 0; c < sudoku[0].length; c++)
					solution[r][c] = sudoku[r][c];
			
			for (int r = 0; r < solution.length; r++)
				for (int c = 0; c < solution[r].length; c++)
					if (solution[r][c] == 0) {
						emptyCells.add(new int[]{r, c});
					}
			
			System.out.println("Trying to solve the following sudoku:");
			printSudoku(sudoku);
		}
		
		private void printSudoku(int[][] sudoku) {
			System.out.println("+-------+-------+-------+");
			for(int row = 0; row < 9; row++) {
				System.out.print("| ");
				for(int col = 0; col < 9; col++) {
					System.out.print(sudoku[row][col] + " ");
					if((col + 1) % 3 == 0)
						System.out.print("| ");
					if((col + 1) == 9)
						System.out.print("\n");
				}
				if((row + 1) % 3 == 0)
					System.out.println("+-------+-------+-------+");
			}
		}

		private boolean noDuplicatesInList(int[] list) {
			for (int j = 0; j < list.length; j++)
				for (int k = j + 1; k < list.length; k++)
					if (list[j] == list[k] && list[j] != 0)
						return false;
			return true;
		}

		private boolean checkValidityOnRow(int row) {
			int[] digitsList = new int[solution.length];
			for (int c = 0; c < digitsList.length; c++)
				digitsList[c] = solution[row][c];
			if (noDuplicatesInList(digitsList))
				return true;
			return false;
		}

		private boolean checkValidityOnCol(int col) {
			int[] digitsList = new int[solution.length];
			for (int i = 0; i < digitsList.length; i++)
				digitsList[i] = solution[i][col];
			if (noDuplicatesInList(digitsList))
				return true;
			return false;
		}

		private boolean checkValidityOnRegion(int row, int col) {
			int[] digitsList = new int[solution.length];
			row = (row / 3) * 3;
			col = (col / 3) * 3;
			for (int r = 0, i = 0; r < 3; r++)
				for (int c = 0; c < 3; c++)
					digitsList[i++] = solution[row + r][col + c];
			if (noDuplicatesInList(digitsList))
				return true;
			return false;
		}

		private boolean isValid(int row, int col) {
			boolean checkRow = checkValidityOnRow(row);
			boolean checkCol = checkValidityOnCol(col);
			boolean checkReg = checkValidityOnRegion(row, col);
			if (checkRow && checkCol && checkReg)
				return true;
			return false;
		}
		
		@Override
		public void saveState() {}
		
		@Override
		public void restoreState() {
			System.out.println("Restarting execution from the following partial solution:");
			printSudoku(solution);
		}
		
		public void run() {
			
		    while (emptyCellsListRowIndex < emptyCells.size()) {
		        if (solution[emptyCells.get(emptyCellsListRowIndex)[0]][emptyCells.get(emptyCellsListRowIndex)[1]]++ != 9) {
		            if (isValid(emptyCells.get(emptyCellsListRowIndex)[0], emptyCells.get(emptyCellsListRowIndex)[1])) {
		                emptyCellsListRowIndex++;
		            } 
		        } else {
		        	solution[emptyCells.get(emptyCellsListRowIndex)[0]][emptyCells.get(emptyCellsListRowIndex)[1]] = 0;
		            emptyCellsListRowIndex--;
		            if (emptyCellsListRowIndex < 0) {
		            	System.out.println("Sudoku cannot be solved");
		            	break;
		            }
		        }
		        if(emptyCellsListRowIndex == emptyCells.size()) {
		        	System.out.println("Sudoku solved");
		    	    printSudoku(solution);
		        }
		        
		        synchronized (this) {
					while (isSuspended()) {
						try {
							wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
		    }
		}
	}
	
}
