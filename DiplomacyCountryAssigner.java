
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JMenuBar;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import java.awt.Desktop;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.Font;
import java.net.URI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Creates a GUI used to assign random countries in the Diplomacy Board Game. 
 * Country fields corresponding to name fields are populated when the name field 
 * is populated and the enter key is pressed.
 * @author Adam Darr
 */
public class DiplomacyCountryAssigner {
	
	private static List<JTextField> nameFields = new ArrayList<JTextField>();
	private static List<JTextField> countryFields = new ArrayList<JTextField>();
	private static List<String> countries = Arrays.asList("Russia", "Germany", "England", "France", 
														  "Austria-Hungary", "Turkey", "Italy");
	
	private static JCheckBoxMenuItem namesRequired = new JCheckBoxMenuItem("Names Required", true);
	private static JSlider shuffleSlider = new JSlider(0, 1000, 0);
	
    private static class Dispatcher implements KeyEventDispatcher {
        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER && e.getID() == KeyEvent.KEY_PRESSED) {	// assign random countries
            	Collections.shuffle(countries);
            	
            	int numberOfShuffles = shuffleSlider.getValue() + 1;
            	
            	for(int j = 0; j < numberOfShuffles; j++) {
	        		for(int i = 0; i < countries.size(); i++) {
	        			if(nameFields.get(i).getText().length() > 0 || namesRequired.getState() != true) {
	        				countryFields.get(i).setText(countries.get(i));
	        			} else {
	        				countryFields.get(i).setText("");
	        			}
	        		}
        		}
            } else if(e.getKeyCode() == KeyEvent.VK_TAB) {	// get next text field on tab
            	KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent();
            }
            return false;
        }
    }
    
    private static void createGUI() {
        String[] labels = {"1 ", "2 ", "3 ", "4 ", "5 ", "6 ", "7 "};
        int numPairs = labels.length;
        
        Font headerFont = new Font("Helvetica", Font.BOLD, 14);
        Font fieldFont = new Font("Helvetica", Font.PLAIN, 14);
 
        JPanel p = new JPanel(new SpringLayout());
        
        // Add headers
        JLabel numHeader = new JLabel("#");
        numHeader.setFont(headerFont);
        p.add(numHeader);
        
        JLabel nameHeader = new JLabel("Name");
        nameHeader.setFont(headerFont);
        p.add(nameHeader);
        
        JLabel countryHeader = new JLabel("Country");
        countryHeader.setFont(headerFont);
        p.add(countryHeader);
        
        // Add text fields
        for (int i = 0; i < numPairs; i++) {        
        	JTextField countryField = new JTextField(10);
            countryField.setEditable(false);
            countryField.setFont(fieldFont);
            countryFields.add(countryField);
            
            JTextField nameField = new JTextField(10);
            nameField.setFont(fieldFont);
            nameFields.add(nameField);
            
            JLabel numLabel = new JLabel(labels[i], JLabel.TRAILING);
            numLabel.setFont(new Font("Helvetica", Font.BOLD, 12));
            
        	createRow(p, numLabel, nameField, countryField);
        }
 
        // Create grid layout
        SpringUtilities.makeCompactGrid(p,
                                        numPairs + 1, 3, //rows, cols
                                        6, 6,        //initX, initY
                                        6, 6);       //xPad, yPad
        
        // Create and setup frame to put panels in
        JFrame frame = new JFrame("Diplomacy Assignments");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        
        // Setup the content pane
        p.setOpaque(true);  
        frame.setContentPane(p);
        
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");

        JMenuItem reset = new JMenuItem(new AbstractAction("Reset") {
            public void actionPerformed(ActionEvent ae) {
            	int size = nameFields.size();
            	for(int i = 0; i < size; i++) {
        				countryFields.get(i).setText("");
        				nameFields.get(i).setText("");
        		}
            }
        });
        
        UIManager.put("OptionPane.messageFont", new FontUIResource(new Font("Helvetica", Font.PLAIN, 12)));
        
        JMenuItem help = new JMenuItem(new AbstractAction("Help") {
            public void actionPerformed(ActionEvent ae) {
            	JOptionPane.showMessageDialog(null,
            		    "Simply enter the names of the players in your\n" +
            		    "game and press enter to get country assignments.",
            		    "Help",
            		    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        JMenuItem rules = new JMenuItem(new AbstractAction("Rules") {
            public void actionPerformed(ActionEvent ae) {
            	if(Desktop.isDesktopSupported()) {
            		try {
            			Desktop.getDesktop().browse(new URI("http://www.backstabbr.com/rules"));
            		} catch (Exception e) {
            			JOptionPane.showMessageDialog(null,
                    		    "Sorry, an error occured while opening the rules.",
                    		    "Error",
                    		    JOptionPane.ERROR_MESSAGE);
            		}
            	}
            }
        });
    	
        JMenuItem shuffles = new JMenuItem(new AbstractAction("Number of Shuffles") {
        	
            public void actionPerformed(ActionEvent ae) {    	
            	String spaces = "              ";	// couldn't find a better way to center label and keep box layout
            	JLabel sliderLabel = new JLabel(spaces + "Number of Country Shuffles Per Key Press\n");
            	
            	shuffleSlider.setMinorTickSpacing(50);
            	shuffleSlider.setMajorTickSpacing(250);
            	shuffleSlider.setPaintTicks(true);
            	shuffleSlider.setPaintLabels(true);
            	
            	JPanel sliderPanel = new JPanel();
            	sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));
            	sliderPanel.add(sliderLabel);
            	sliderPanel.add(shuffleSlider);
            	
            	JOptionPane.showMessageDialog(null,
            		    sliderPanel,
            		    "Number of Shuffles",
            		    JOptionPane.QUESTION_MESSAGE);
            }
        });
        
        JMenu options = new JMenu("Options");
        options.add(namesRequired);
        options.add(shuffles);
        
        menu.add(options);
        menu.add(rules);
        menu.add(reset);
        menu.add(help);
        menuBar.add(menu);

        frame.setJMenuBar(menuBar);
        
        // Add key listener
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new Dispatcher());
       
        // Display window
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
    }
    
    public static void createRow(JPanel p, JLabel label, JTextField nameField, JTextField countryField) {
        p.add(label);
        p.add(nameField);
        p.add(countryField);
    }
 
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createGUI();
            }
        });
    }
}