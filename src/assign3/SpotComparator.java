package assign3;

import java.util.*;

import assign3.Sudoku.Spot;

public class SpotComparator implements Comparator<Spot> {

	@Override
	public int compare(Spot s1, Spot s2) {
		if (s1.getPriority() < s2.getPriority()) {
			return -1;
		}
		if (s1.getPriority() > s2.getPriority()) {
			return 1;
		}
		return 0;
	}

}
