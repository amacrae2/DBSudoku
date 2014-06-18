package assign3;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;
import javax.swing.text.Document;

import java.awt.*;
import java.awt.event.*;

 public class SudokuFrame extends JFrame {

	JTextArea source;
	JTextArea solution;	
	JButton check;
	JCheckBox checkBox;
	
	public SudokuFrame() {
		super("Sudoku Solver");
		
		setLocationByPlatform(true);
	    setLayout(new BorderLayout(4,4));
	    initializeTextBoxes();
	    Document sourceDocument = source.getDocument();
	    sourceDocument.addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent event) {
				tryInputText();				
			}
			
			@Override
			public void insertUpdate(DocumentEvent event) {
				tryInputText();
			}

			@Override
			public void changedUpdate(DocumentEvent event) {
				tryInputText();				
			}
		});
	    solution.setBorder(new TitledBorder("Solution"));
	    JPanel bottomBox = new JPanel(new FlowLayout(FlowLayout.LEFT));
	    check = new JButton("Check");
	    checkBox = new JCheckBox();
	    checkBox.setSelected(true);
	    bottomBox.add(check);
	    check.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent event) {
				tryInputText();
			}
		});
	    bottomBox.add(checkBox);
	    bottomBox.add(new JLabel("Auto Check"));
	    add(bottomBox, BorderLayout.SOUTH);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}
	
	
	private void initializeTextBoxes() {
	    source = new JTextArea(15,20);
	    solution = new JTextArea(15,20);	
	    add(source, BorderLayout.CENTER);
	    add(solution, BorderLayout.EAST);
	    source.setBorder(new TitledBorder("Puzzle"));		
	}
	
	private void tryInputText() {
		try {
			if (checkBox.isSelected()) {
				String input = source.getText();
				Sudoku sudoku = new Sudoku(input);
				int numSolutions = sudoku.solve();
				String solutionText = sudoku.getSolutionText();
				long runTime = sudoku.getElapsed();
				solution.setText(solutionText+"\nsolutions:"+numSolutions+"\nelapsed"+runTime+"ms");
			}
		} catch (RuntimeException e) {
			solution.setText("Parsing problem");
		}				
	}


	public static void main(String[] args) {
		// GUI Look And Feel
		// Do this incantation at the start of main() to tell Swing
		// to use the GUI LookAndFeel of the native platform. It's ok
		// to ignore the exception.
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) { }
		
		SudokuFrame frame = new SudokuFrame();
	}

}
