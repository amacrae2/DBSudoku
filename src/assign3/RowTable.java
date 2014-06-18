package assign3;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.table.JTableHeader;


public class RowTable extends JFrame {
	
	private static final int TEXTBOX_LENGTH = 10;
	private static final String WINDOW_NAME = "Metropolis Viewer";
	private static final String METRO_LABEL = "Metropolis:";
	private static final String CONT_LABEL = "Continent:";
	private static final String POP_LABEL = "Population:";
	private static final String ADD_BUTTON_NAME = "Add";
	private static final String SEARCH_BUTTON_NAME = "Search";
	private static final String LABEL_TEXT = "Search Options";
	private static final String POP_OPTION1 = "Population Larger Than";
	private static final String POP_OPTION2 = "Population Less Than Or Equal To";
	private static final String EXACT_MATCH = "Exact Match";
	private static final String CLOSE_MATCH = "Partial Match";
	private static final String[] POPULATION_SEACH_OPTIONS = new String[] {POP_OPTION1, POP_OPTION2};
	private static final String[] MATCH_SEACH_OPTIONS = new String[] {EXACT_MATCH, CLOSE_MATCH};
	
	private JTextField metropolisTextBox;
	private JTextField continentTextBox;
	private JTextField populationTextBox;
	private JTable table;
	private DBTableModel model;
	private JButton add;
	private JButton search;
	private JComboBox populationComboBox;
	private JComboBox matchComboBox;
	
	public RowTable() {
		super(WINDOW_NAME);
		setLocationByPlatform(true);
	    setLayout(new BorderLayout(4,4));
		addTopComponentsToGUI();
		
		// center part
		model = new DBTableModel(new ArrayList<Row>());
		table = new JTable(model);
		table.setBorder(new MatteBorder(1, 0, 1, 0, Color.RED));
		table.setSize(250,150);
		add(new JScrollPane(table), BorderLayout.CENTER);
		
		// right part
		JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
		add = new JButton(ADD_BUTTON_NAME);
		add.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Row row = new Row(metropolisTextBox.getText(), 
								  continentTextBox.getText(), 
								  Integer.valueOf(populationTextBox.getText()));
				MySQLUtil.writeToDB(row);
				List<Row> rows = new ArrayList<Row>();
				rows.add(row);
				model = new DBTableModel(rows);
				table.setModel(model);
			}
		});
		right.add(add);
		search = new JButton(SEARCH_BUTTON_NAME);
		search.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean largerThan = false;
				boolean exactMatch = false;
				if (populationComboBox.getSelectedItem() == POP_OPTION1) {
					largerThan = true;
				}
				if (matchComboBox.getSelectedItem() == EXACT_MATCH) {
					exactMatch = true;
				}
				List<Row> rows = MySQLUtil.getRows(metropolisTextBox.getText(), 
						  continentTextBox.getText(), 
						  populationTextBox.getText(), largerThan, exactMatch);
				model = new DBTableModel(rows);
				table.setModel(model);
			}
		});
		right.add(search);
		JLabel searchOptions = new JLabel(LABEL_TEXT);
		right.add(searchOptions);
		populationComboBox = new JComboBox(POPULATION_SEACH_OPTIONS);
		matchComboBox = new JComboBox(MATCH_SEACH_OPTIONS);
		right.add(populationComboBox);
		right.add(matchComboBox);
		add(right, BorderLayout.EAST);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

	private void addTopComponentsToGUI() {
		JPanel top = new JPanel();
		JLabel metropolisLabel = new JLabel(METRO_LABEL);
		metropolisTextBox = new JTextField(TEXTBOX_LENGTH);
		JLabel continentLabel = new JLabel(CONT_LABEL);
		continentTextBox = new JTextField(TEXTBOX_LENGTH);
		JLabel populationLabel = new JLabel(POP_LABEL);
		populationTextBox = new JTextField(TEXTBOX_LENGTH);
		top.add(metropolisLabel);
		top.add(metropolisTextBox);
		top.add(continentLabel);
		top.add(continentTextBox);
		top.add(populationLabel);
		top.add(populationTextBox);
	    add(top, BorderLayout.NORTH);		
	}


	public static void main(String[] args) {
		// GUI Look And Feel
		// Do this incantation at the start of main() to tell Swing
		// to use the GUI LookAndFeel of the native platform. It's ok
		// to ignore the exception.
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) { }
		
		RowTable table = new RowTable();
	}
	
}
