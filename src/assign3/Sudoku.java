package assign3;

import java.util.*;

import javax.management.RuntimeErrorException;

/*
 * Encapsulates a Sudoku grid to be solved.
 * CS108 Stanford.
 */
public class Sudoku {
	// Provided grid data for main/testing
	// The instance variable strategy is up to you.
	
	// Provided easy 1 6 grid
	// (can paste this text into the GUI too)
	public static final int[][] easyGrid = Sudoku.stringsToGrid(
	"1 6 4 0 0 0 0 0 2",
	"2 0 0 4 0 3 9 1 0",
	"0 0 5 0 8 0 4 0 7",
	"0 9 0 0 0 6 5 0 0",
	"5 0 0 1 0 2 0 0 8",
	"0 0 8 9 0 0 0 3 0",
	"8 0 9 0 4 0 2 0 0",
	"0 7 3 5 0 9 0 0 1",
	"4 0 0 0 0 0 6 7 9");
	
	
	// Provided medium 5 3 grid
	public static final int[][] mediumGrid = Sudoku.stringsToGrid(
	 "530070000",
	 "600195000",
	 "098000060",
	 "800060003",
	 "400803001",
	 "700020006",
	 "060000280",
	 "000419005",
	 "000080079");
	
	// Provided hard 3 7 grid
	// 1 solution this way, 6 solutions if the 7 is changed to 0
	public static final int[][] hardGrid = Sudoku.stringsToGrid(
	"3 7 0 0 0 0 0 8 0",
	"0 0 1 0 9 3 0 0 0",
	"0 4 0 7 8 0 0 0 3",
	"0 9 3 8 0 0 0 1 2",
	"0 0 0 0 4 0 0 0 0",
	"5 2 0 0 0 6 7 9 0",
	"6 0 0 0 2 1 0 4 0",
	"0 0 0 5 3 0 9 0 0",
	"0 3 0 0 0 0 0 5 1");
	
	
	public static final int SIZE = 4;  // size of the whole 9x9 puzzle
	public static final int PART = 2;  // size of each 3x3 part
	public static final int MAX_SOLUTIONS = 100;
	
	private int[][] grid = new int[SIZE][SIZE];
	private int[][] solvedGrid = new int[SIZE][SIZE];
//	private Set<Integer> rows = new HashSet<Integer>();
//	private Set<Integer> columns = new HashSet<Integer>();
//	private Set<Integer> quadrants = new HashSet<Integer>();
	private long timeElapsed = 0;
	
	
	public class Spot {
		
		private int x;
		private int y;
		private int priority;
		
		public Spot(int x, int y, int priority) {
			this.x = x;
			this.y = y;
			this.priority = priority;
		}
		
		public int getX() {
			return x;
		}
		
		public int getY() {
			return y;
		}
		
		public int getValue() {
			return grid[x][y];
		}
		
		public int getPriority() {
			return priority;
		}
		
		public void set(int val) {
			grid[x][y] = val;
		}
	}
	
	// Provided various static utility methods to
	// convert data formats to int[][] grid.
	
	/**
	 * Returns a 2-d grid parsed from strings, one string per row.
	 * The "..." is a Java 5 feature that essentially
	 * makes "rows" a String[] array.
	 * (provided utility)
	 * @param rows array of row strings
	 * @return grid
	 */
	public static int[][] stringsToGrid(String... rows) {
		int[][] result = new int[rows.length][];
		for (int row = 0; row<rows.length; row++) {
			result[row] = stringToInts(rows[row]);
		}
		return result;
	}
	
	
	/**
	 * Given a single string containing 81 numbers, returns a 9x9 grid.
	 * Skips all the non-numbers in the text.
	 * (provided utility)
	 * @param text string of 81 numbers
	 * @return grid
	 */
	public static int[][] textToGrid(String text) {
		int[] nums = stringToInts(text);
		if (nums.length != SIZE*SIZE) {
			throw new RuntimeException("Needed 81 numbers, but got:" + nums.length);
		}
		
		int[][] result = new int[SIZE][SIZE];
		int count = 0;
		for (int row = 0; row<SIZE; row++) {
			for (int col=0; col<SIZE; col++) {
				result[row][col] = nums[count];
				count++;
			}
		}
		return result;
	}
	
	
	/**
	 * Given a string containing digits, like "1 23 4",
	 * returns an int[] of those digits {1 2 3 4}.
	 * (provided utility)
	 * @param string string containing ints
	 * @return array of ints
	 */
	public static int[] stringToInts(String string) {
		int[] a = new int[string.length()];
		int found = 0;
		for (int i=0; i<string.length(); i++) {
			if (Character.isDigit(string.charAt(i))) {
				a[found] = Integer.parseInt(string.substring(i, i+1));
				found++;
			}
		}
		int[] result = new int[found];
		System.arraycopy(a, 0, result, 0, found);
		return result;
	}

	public String toString() {
		String result = "";
		for (int row = 0; row < SIZE; row++) {
			for (int col = 0; col < SIZE; col++) {
				result = result + grid[row][col] + " ";
			}
			result = result.trim() + "\n";
		}
		return result;
	}

	// Provided -- the deliverable main().
	// You can edit to do easier cases, but turn in
	// solving hardGrid.
	public static void main(String[] args) {
		Sudoku sudoku;
		sudoku = new Sudoku(hardGrid);
		
		System.out.println(sudoku); // print the raw problem
		int count = sudoku.solve();
		System.out.println("solutions:" + count);
		System.out.println("elapsed:" + sudoku.getElapsed() + "ms");
		System.out.println(sudoku.getSolutionText());
	}
	
	
	

	/**
	 * Sets up based on the given ints.
	 */
	public Sudoku(int[][] ints) {
		grid = ints;
	}
	
	/**
	 * Sets up based on the given ints.
	 */
	public Sudoku(String intsString) {
		this(textToGrid(intsString));
	}
	
	/**
	 * Solves the puzzle, invoking the underlying recursive search.
	 * @return number of solutions
	 * @throws RuntimeException if grid is mal-formed
	 */
	public int solve() {
		long startTime = System.currentTimeMillis();
		ArrayList<HashSet<Integer>> rows = new ArrayList<HashSet<Integer>>();
		ArrayList<HashSet<Integer>> columns = new ArrayList<HashSet<Integer>>();
		ArrayList<HashSet<Integer>> quadrants = new ArrayList<HashSet<Integer>>();
        
		initializeSets(rows,columns,quadrants);
		PriorityQueue<Spot> queue = fillQueue(rows,columns,quadrants);
		int result = findSolution(queue,rows,columns,quadrants,0);

		timeElapsed = System.currentTimeMillis() - startTime;
		return result;
	}


	public String getSolutionText() {
		String result = "";
		for (int i = 0; i < SIZE; i++) {
			result = result + Arrays.toString(solvedGrid[i]) + "\n";
		}
		result = result.replace("[", "");
		result = result.replace("]", "");
		result = result.replace(",", "");
		return result;
	}
	
	public long getElapsed() {
		return timeElapsed;
	}
	
	/**
	 * Fills in sets that correspond to which numbers can be found in which rows, 
	 * columns, and quadrants of the sudoku grid initially.
	 * @param rows a list of sets corresponding to the rows of the grid
	 * @param columns a list of sets corresponding to the columns of the grid
	 * @param quadrants a list of sets corresponding to the quadrants of the grid.
	 */
	private void initializeSets(ArrayList<HashSet<Integer>> rows,
								ArrayList<HashSet<Integer>> columns,
								ArrayList<HashSet<Integer>> quadrants) {
		addEmptySets(rows,columns,quadrants);
		for (int row = 0; row < SIZE; row++) {
			for (int col = 0; col < SIZE; col ++) {
				if (grid[row][col] != 0) {
					rows.get(row).add(grid[row][col]);
					columns.get(col).add(grid[row][col]);
					addToQuadrantsSet(quadrants,row,col,grid[row][col]);
				}
			}
		}		
	}

	/**
	 * initializes the lists of sets to be updated with the number of empty
	 * sets that will be filled with numbers later.
	 * @param rows a list of sets corresponding to the rows of the grid
	 * @param columns a list of sets corresponding to the columns of the grid
	 * @param quadrants a list of sets corresponding to the quadrants of the grid.
	 */
	private void addEmptySets(ArrayList<HashSet<Integer>> rows,
							  ArrayList<HashSet<Integer>> columns,
							  ArrayList<HashSet<Integer>> quadrants) {
		for (int i = 0; i < SIZE; i++) {
			rows.add(new HashSet<Integer>());
			columns.add(new HashSet<Integer>());
			quadrants.add(new HashSet<Integer>());
		}
	}

	/**
	 * Updates the set corresponding to the quadrant that falls within the bounds of the
	 * current row and column number
	 * @param quadrants a list of sets corresponding to the quadrants of the grid.
	 * @param row the current row number in consideration 
	 * @param col the current column number in consideration
	 */
	private void addToQuadrantsSet(ArrayList<HashSet<Integer>> quadrants,
								   int row, int col, int num) {
		int quadNum = getQuadNum(row,col);
		quadrants.get(quadNum-1).add(num);		
	}

	/**
	 * finds the quadrant number associated with a given row and column
	 * @param row the row number
	 * @param col the column number
	 * @return the quadrant number of specified row and col.
	 */
	private int getQuadNum(int row, int col) {
		int quadNum = 0;
		int rowNum = getIndexNum(row);
		int colNum = getIndexNum(col);
		quadNum = (PART*(rowNum-1)) + colNum; // quadNum is indexed 1:3 across the top three rows, 4:6 across the middle row, ect...		return 0;
		return quadNum;
	}


	/**
	 * Finds the section 1-3 that the index i falls into based on the size of one Part of the grid
	 * @param i the row or column number to be looked at
	 * @return The section 1-3 that the index i falls into.
	 */
	private int getIndexNum(int i) {
		if (i < PART) {
			return 1;
		} else if (i < 2*PART) {
			return 2;
		} else if (i < 3*PART) {
			return 3;
		} else {
			throw new RuntimeException("Needed row and column numbers less than 9, but got: " + i);
		}
	}

	/**
	 * Fills in a priority queue with spots = to 0 on the grid in the order of which they should be
	 * filled in recursively based on the starting number of possible numbers that could
	 * legally fill that spot.
	 * @param rows a list of sets corresponding to the rows of the grid
	 * @param columns a list of sets corresponding to the columns of the grid
	 * @param quadrants a list of sets corresponding to the quadrants of the grid.
	 * @return a priority queue containing the spots = to 0 to be filled in in the order to be filled in.
	 */
	private PriorityQueue<Spot> fillQueue(ArrayList<HashSet<Integer>> rows,
										  ArrayList<HashSet<Integer>> columns,
										  ArrayList<HashSet<Integer>> quadrants) {
        Comparator<Spot> comparator = new SpotComparator();
        PriorityQueue<Spot> queue = new PriorityQueue<Spot>(SIZE*SIZE, comparator);
		for (int row = 0; row < SIZE; row++) {
			for (int col = 0; col < SIZE; col ++) {
				if (grid[row][col] == 0) {
					int priority = findSpotPriority(rows,columns,quadrants,row,col);
					Spot spot = new Spot(row,col,priority);
					queue.add(spot);
				}
			}
		}	
		return queue;
	}

	/**
	 * based on the number of possible numbers that could go in a spot legally,
	 * returns that number of legal numbers. The logic is based on the standard logic of sudoku rules.
	 * @param rows a list of sets corresponding to the rows of the grid
	 * @param columns a list of sets corresponding to the columns of the grid
	 * @param quadrants a list of sets corresponding to the quadrants of the grid.
	 * @param row the row number of the spot
	 * @param col the column number of the spot
	 * @return the number of possible numbers that could legally go in that spot
	 */
	private int findSpotPriority(ArrayList<HashSet<Integer>> rows,
								 ArrayList<HashSet<Integer>> columns,
								 ArrayList<HashSet<Integer>> quadrants, int row, int col) {
		int priority = 0;
		int quadNum = getQuadNum(row, col);
		for (int i = 1; i <= SIZE; i++) {
			if (legalMove(rows,columns,quadrants,row,col,quadNum,i)) { // if the move is legal
				priority ++;
			}
		}
		if (priority == 0) {
			throw new RuntimeException("no possible numbers can fit at index : " + row + "," + col);
		}
		return priority;
	}


	private boolean legalMove(ArrayList<HashSet<Integer>> rows,
							  ArrayList<HashSet<Integer>> columns,
							  ArrayList<HashSet<Integer>> quadrants, int row, int col, int quadNum, int num) {
		return (!rows.get(row).contains(num) && !columns.get(col).contains(num) && !quadrants.get(quadNum-1).contains(num));
	}


	private int findSolution(PriorityQueue<Spot> queue,
							  ArrayList<HashSet<Integer>> rows,
							  ArrayList<HashSet<Integer>> columns,
							  ArrayList<HashSet<Integer>> quadrants, int numSolutions) {
		if (queue.isEmpty()) { // we are done and found a solution
			if (numSolutions == 0) { // if first solution found
				fillInSolvedGrid();
			}
			numSolutions ++;
		} else {
			Spot currSpot = queue.poll();
			Set<Integer> possibleNums = getAllPossibleNumbersForSpot(currSpot,rows,columns,quadrants);
			for (Integer num: possibleNums) {
				addPossibleNum(currSpot,rows,columns,quadrants,num);
				numSolutions = findSolution(queue, rows, columns, quadrants, numSolutions);
				removePossibleNum(currSpot,rows,columns,quadrants,num);
			}
			queue.add(currSpot);
		}		
		return numSolutions;
	}
	
	private Set<Integer> getAllPossibleNumbersForSpot(Spot currSpot,
											   ArrayList<HashSet<Integer>> rows,
											   ArrayList<HashSet<Integer>> columns,
											   ArrayList<HashSet<Integer>> quadrants) {
		Set<Integer> posNumsForSpot = new HashSet<Integer>();
		int row = currSpot.getX();
		int col = currSpot.getY();
		int quadNum = getQuadNum(row, col);
		for (int i = 1; i <= SIZE; i++) {
			if (legalMove(rows,columns,quadrants,row,col,quadNum,i)) { // if the move is legal
				posNumsForSpot.add(i);
			}	
		}
		return posNumsForSpot;
	}


	private void addPossibleNum(Spot currSpot,
			   					ArrayList<HashSet<Integer>> rows,
			   					ArrayList<HashSet<Integer>> columns,
			   					ArrayList<HashSet<Integer>> quadrants, int num) {
		int row = currSpot.getX();
		int col = currSpot.getY();
		rows.get(row).add(num);
		columns.get(col).add(num);
		addToQuadrantsSet(quadrants,row,col,num);	
		grid[row][col] = num;
	}


	private void removePossibleNum(Spot currSpot,
								   ArrayList<HashSet<Integer>> rows,
								   ArrayList<HashSet<Integer>> columns,
								   ArrayList<HashSet<Integer>> quadrants, int num) {
		int row = currSpot.getX();
		int col = currSpot.getY();
		rows.get(row).remove(num);
		columns.get(col).remove(num);
		removeFromQuadrantsSet(quadrants,row,col,num);
		grid[row][col] = 0;
	}


	private void removeFromQuadrantsSet(ArrayList<HashSet<Integer>> quadrants,
			int row, int col, int num) {
		int quadNum = getQuadNum(row,col);
		quadrants.get(quadNum-1).remove(num);			
	}


	private void fillInSolvedGrid() {
		for(int i = 0; i < SIZE; i++) {
			  for(int j = 0; j < SIZE; j++) {
			    solvedGrid[i][j] = grid[i][j];
			  }
		}		
	}

}
