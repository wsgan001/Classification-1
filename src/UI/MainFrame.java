package UI;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JButton;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;


public class MainFrame extends JFrame {

	private JPanel contentPane;
	private DefaultListModel leftListModel;
	private DefaultListModel rightListModel;
	private JFileChooser importChooser = new JFileChooser(new File("."));
	private JTextField TrainingFileTextField;
	private JTextField TestingFileTextField;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	
	// data processing variables
	private List<String> allAttribute = new ArrayList<String>();
	private List<String> selectedAttribute = new ArrayList<String>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		setTitle("DMFinal");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 610, 560);
	
		/*最上層File 選單*/	
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		//File
		JMenu mainFrameFileMenu = new JMenu("File");
		menuBar.add(mainFrameFileMenu);
		
		//Exit
		JMenuItem mainFrameExitMenuItem = new JMenuItem("Exit");
		mainFrameExitMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		mainFrameFileMenu.add(mainFrameExitMenuItem);
		
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane leftListScrollPane = new JScrollPane();
		leftListScrollPane.setBounds(50, 55, 200, 250);
		contentPane.add(leftListScrollPane);
		
		leftListModel = new DefaultListModel();
		JList leftList = new JList(leftListModel);
		leftListScrollPane.setViewportView(leftList);
		
		JScrollPane rightListScrollPane = new JScrollPane();
		rightListScrollPane.setBounds(350, 55, 200, 250);
		contentPane.add(rightListScrollPane);
		
		rightListModel = new DefaultListModel();
		JList rightList = new JList(rightListModel);
		rightListScrollPane.setViewportView(rightList);
		
		JButton moveAllButton = new JButton(">>");
		moveAllButton.setFont(new Font("新細明體", Font.BOLD, 12));
		moveAllButton.setBounds(275, 59, 50, 50);
		contentPane.add(moveAllButton);
		
		JButton moveOneButton = new JButton(">");
		moveOneButton.setFont(new Font("新細明體", Font.BOLD, 12));
		moveOneButton.setBounds(275, 127, 50, 50);
		contentPane.add(moveOneButton);
		
		JButton removeOneButton = new JButton("<");
		removeOneButton.setFont(new Font("新細明體", Font.BOLD, 12));
		removeOneButton.setBounds(275, 195, 50, 50);
		contentPane.add(removeOneButton);
		
		JButton removeAllButton = new JButton("<<");
		removeAllButton.setFont(new Font("新細明體", Font.BOLD, 12));
		removeAllButton.setBounds(275, 255, 50, 50);
		contentPane.add(removeAllButton);
		
		JButton btnNewButton_1 = new JButton("Build Decision Tree");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							selectedAttribute = allAttribute; 
							ChooseConFrame frame = new ChooseConFrame(TrainingFileTextField.getText(), TestingFileTextField.getText(), selectedAttribute);
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		btnNewButton_1.setFont(new Font("微軟正黑體", Font.BOLD, 18));
		btnNewButton_1.setBounds(50, 430, 494, 71);
		contentPane.add(btnNewButton_1);
		
		JLabel leftLabel = new JLabel("Selected");
		leftLabel.setHorizontalAlignment(SwingConstants.CENTER);
		leftLabel.setFont(new Font("微軟正黑體", Font.BOLD, 14));
		leftLabel.setBounds(50, 15, 200, 31);
		contentPane.add(leftLabel);
		
		JLabel rightLabel = new JLabel("Not Selected");
		rightLabel.setHorizontalAlignment(SwingConstants.CENTER);
		rightLabel.setFont(new Font("微軟正黑體", Font.BOLD, 14));
		rightLabel.setBounds(350, 15, 200, 31);
		contentPane.add(rightLabel);
		
		JLabel lblTrainingFile = new JLabel("Training File : ");
		lblTrainingFile.setBounds(60, 320, 97, 23);
		contentPane.add(lblTrainingFile);
		
		JLabel lblTestingPath = new JLabel("Testing File :");
		lblTestingPath.setBounds(60, 353, 97, 15);
		contentPane.add(lblTestingPath);
		
		TrainingFileTextField = new JTextField();
		TrainingFileTextField.setEditable(false);
		TrainingFileTextField.setBounds(138, 321, 329, 21);
		contentPane.add(TrainingFileTextField);
		TrainingFileTextField.setColumns(10);
		
		TestingFileTextField = new JTextField();
		TestingFileTextField.setEditable(false);
		TestingFileTextField.setColumns(10);
		TestingFileTextField.setBounds(138, 350, 329, 21);
		contentPane.add(TestingFileTextField);
		
		JRadioButton algorithm1RadioButton = new JRadioButton("ID3");
		algorithm1RadioButton.setSelected(true);
		buttonGroup.add(algorithm1RadioButton);
		algorithm1RadioButton.setBounds(138, 376, 107, 23);
		contentPane.add(algorithm1RadioButton);
		
		JRadioButton algorithm2RadioButton = new JRadioButton("C4.5");
		buttonGroup.add(algorithm2RadioButton);
		algorithm2RadioButton.setBounds(138, 401, 107, 23);
		contentPane.add(algorithm2RadioButton);
		
		JLabel lblAlgorithm = new JLabel("Algorithm :");
		lblAlgorithm.setBounds(60, 394, 97, 15);
		contentPane.add(lblAlgorithm);
		
		JButton selectTrainingFileButton = new JButton("...");
		selectTrainingFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try{
					importChooser.setSelectedFile(new File(""));
					int ops = importChooser.showOpenDialog(null);
					if(ops == JFileChooser.APPROVE_OPTION){
						boolean success = importTrainingFile(importChooser.getSelectedFile().getPath());
						if(success){
							TrainingFileTextField.setText(importChooser.getSelectedFile().getPath());
							importChooser.setCurrentDirectory(importChooser.getSelectedFile());
						}
						else{
							JOptionPane.showMessageDialog(null,"Selected training file is illegal.", "warning", JOptionPane.PLAIN_MESSAGE);
						}
					}
				}catch(Exception exception){
					System.out.println(exception.toString());
				}
			}

			private boolean importTrainingFile(String path) {
				allAttribute.clear();
				leftListModel.clear();
				
				BufferedReader br;
				String line = "";
				String[] tokens;
				try {
					br = new java.io.BufferedReader(new java.io.FileReader(path));
					
					line = br.readLine();
					tokens = line.split("\t");
					for(int i=0;i<tokens.length;i++){
						allAttribute.add(tokens[i]);
						leftListModel.addElement(tokens[i]);
					}

				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
				return true;
			}
		});
		selectTrainingFileButton.setBounds(477, 320, 73, 23);
		contentPane.add(selectTrainingFileButton);
		
		JButton selectTestingFileButton = new JButton("...");
		selectTestingFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try{
					importChooser.setSelectedFile(new File(""));
					int ops = importChooser.showOpenDialog(null);
					if(ops == JFileChooser.APPROVE_OPTION){
						TestingFileTextField.setText(importChooser.getSelectedFile().getPath());
						importChooser.setCurrentDirectory(importChooser.getSelectedFile());
					}
				}catch(Exception exception){
					System.out.println(exception.toString());
				}
			}
		});
		selectTestingFileButton.setBounds(477, 349, 73, 23);
		contentPane.add(selectTestingFileButton);


		
	}
}

