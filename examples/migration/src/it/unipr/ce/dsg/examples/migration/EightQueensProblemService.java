package it.unipr.ce.dsg.examples.migration;

import it.unipr.ce.dsg.nam4j.impl.service.Service;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * <p>
 * This class represents an example of a {@link Service} with a thread providing
 * methods to get started, suspended, resumed and stopped. Such a
 * {@link Service} represents a N-queens-problem solver.
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

public class EightQueensProblemService extends Service implements Serializable {
	
	private static final long serialVersionUID = -5870168538989313837L;
	private ServiceRunnableImplementation serviceRunnableImplementation = null;

	public EightQueensProblemService() {
		super();
		this.setId("NQueensProblemService");
		this.setName("NQueensProblemService");
		serviceRunnableImplementation = new ServiceRunnableImplementation();
	}
	
	@Override
	public ServiceRunnableImplementation getServiceRunnable() {
		return serviceRunnableImplementation;
	}
	
	public class ServiceRunnableImplementation extends ServiceRunnable {

		private static final long serialVersionUID = -5783852062897237050L;

		byte[][] board = new byte[8][8];
		
		private ArrayList<byte[][]> solutions = new ArrayList<>();
		
		// Iteration indexes are defined out of the blocks so they can be
		// serialized and restored
		int i1 = 0, j1 = 0, k1 = 0, l1 = 0, m1 = 0, n1 = 0, o1 = 0, p1 = 0;
		
		/** Class constructor. */
		public ServiceRunnableImplementation() {}
		
		@Override
		public void saveState() {}
		
		@Override
		public void restoreState() {}
		
		private void clearBoard(byte[][] board) {
			for (int i = 0; i < 8; i ++) {
				for (int j = 0; j < 8; j++) {
					board[i][j] = 0;
				}
			}
		}
		
		private boolean isSolution(byte[][] board) {
			int rowSum = 0;
			int colSum = 0;

			for (int i = 0; i < 8; i++) {
				for (int j = 0;  j < 8; j++) {
					rowSum += board[i][j];
					colSum += board[j][i];

					if (i == 0 || j == 0)
						if ( !checkDiagonal1(board, i, j) ) return false;

					if (i == 0 || j == 8-1)
						if ( !checkDiagonal2(board, i, j) ) return false;

				}
				if (rowSum > 1 || colSum > 1) return false;
				rowSum = 0;
				colSum = 0;
			}

			return true;
		}
		
		private boolean checkDiagonal1(byte[][] board, int row, int col) {
			int sum = 0;
			int i = row;
			int j = col;
			while (i < 8 && j < 8) {
				sum += board[i++][j++];
			}
			return sum <= 1;
		}
		
		private boolean checkDiagonal2(byte[][] board, int row, int col) {
			int sum = 0;
			int i = row;
			int j = col;
			while (i < 8 && j >=0) {
				sum += board[i++][j--];
			}
			return sum <= 1;
		}
		
		private void printBoard(byte[][] board) {
			for (int i = 0; i < 8; i++) {
				StringBuilder row = new StringBuilder();
				for (int j = 0;  j < 8; j++) {
					row.append(board[i][j]);
					if (j < 7)
						row.append(" ");
				}
				System.out.println(row.toString());
			}
			System.out.println("\n");
		}
		
		private void setAndCheckBoard(byte[][] board, int... cols) {
			clearBoard(board);
			
			for (int i = 0; i < 8; i++)
				board[i][cols[i]] = 1;
			
			if (isSolution(board)) {
				
				// The array added to solutions is a copy of the actual
				// solution, otherwise the latter would be passed by reference
				// and all elements in solutions would be equal to the last
				// found solution
				byte[][] solution = new byte[8][8];
				for (int i = 0; i < solution.length; i++)
					for (int j = 0; j < solution[i].length; j++)
						solution[i][j] = board[i][j];
				
				solutions.add(solution);
				System.out.println("Found " + solutions.size() + " solutions");
			}
		}
		
		public void run() {
			
			// Counters updates happen after each nested loop since, when the
			// execution state gets migrated, resuming execution restarts run
			// method and all while conditions are tested. Doing so, counters
			// have the serialized values. If for loops were used, counters'
			// initialization would happen again and the execution would start
			// from the beginning thus finding already found solutions.
			while(i1 < 7) {
				while(j1 < 8) {
					while(k1 < 8) {
						while(l1 < 8) {
							while(m1 < 8) {
								while(n1 < 8) {
									while(o1 < 8) {
										while(p1 < 8) {
											setAndCheckBoard(board, new int[]{i1, j1, k1, l1, m1, n1, o1, p1});
											p1++;
											
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
										o1++;
										p1 = 0;
									}
									n1++;
									o1 = 0;
									p1 = 0;
								}
								m1++;
								n1 = 0;
								o1 = 0;
								p1 = 0;
							}
							l1++;
							m1 = 0;
							n1 = 0;
							o1 = 0;
							p1 = 0;
						}
						k1++;
						l1 = 0;
						m1 = 0;
						n1 = 0;
						o1 = 0;
						p1 = 0;
					}
					j1++;
					k1 = 0;
					l1 = 0;
					m1 = 0;
					n1 = 0;
					o1 = 0;
					p1 = 0;
				}
				i1++;
				j1 = 0;
				k1 = 0;
				l1 = 0;
				m1 = 0;
				n1 = 0;
				o1 = 0;
				p1 = 0;
			}
			
			System.out.println("Found " + solutions.size() + " solutions");
			for(int solutionIndex = 0; solutionIndex < solutions.size(); solutionIndex++) {
				System.out.println("Solution " + (solutionIndex + 1));
				printBoard(solutions.get(solutionIndex));
			}
		}
	}
	
}
