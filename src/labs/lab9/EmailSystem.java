package labs.lab9;	//needed? Single file it seems like

import javax.swing.*;
import javax.swing.border.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;



public class EmailSystem {
	
	//initial user find
	static Set<User> users = new HashSet<>();
	static User currentUser = new User(determineUser());
	static JFrame homeFrame = new JFrame();
	
	public static void main(String[] args)
	{	
		//add robert to system by default
		users.add(new User("Robert Navarro"));
		
		//testing
		System.out.println("Begin program as: " + currentUser);
		
		
		//menu bar (for home window) 
		JMenuBar homeMenu = new JMenuBar(); 
		createFileMenu(homeMenu);
		createUserMenu(homeMenu);
		
		homeFrame.setLayout(new BorderLayout());
		
		//user indicator
		createUserIndicator();
		
		JPanel mainContent = new JPanel();
		mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
		mainContent.add(Box.createVerticalStrut(25)); // 25px spacing top
		createTopPanel(mainContent);
		mainContent.add(Box.createVerticalStrut(25)); // 25px spacing between
		createBottomPanel(mainContent);
		mainContent.add(Box.createVerticalStrut(25)); // 25px spacing under
		homeFrame.add(mainContent, BorderLayout.CENTER);
		
		//homeframe preferences
		homeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		homeFrame.setJMenuBar(homeMenu);
		homeFrame.setSize(1200,800);
		homeFrame.setVisible(true);
	}
	
	public static String determineUser()
	{
		String name = "";
		while(name == null || name.isBlank() ||name.length() == 0)
		{
			name = JOptionPane.showInputDialog("Enter your username: ");
		}
		
		//only adds to set if a unique name
		users.add(new User(name));
		return name;
	}
	
	public static void createUserIndicator() 
	{
		JLabel currentUserDisplay = new JLabel("Current User: " + currentUser);
		homeFrame.add(currentUserDisplay, BorderLayout.NORTH);
	}
	
	public static void createFileMenu(JMenuBar menu)
	{
		JMenu fileMenu = new JMenu("File");
		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(e -> System.exit(0));
		fileMenu.add(exit);
		menu.add(fileMenu);
	}
	
	public static void createUserMenu(JMenuBar menu)
	{
		JMenu userMenu = new JMenu("Users");
		JMenuItem switchUser = new JMenuItem("Switch User");
		
		switchUser.addActionListener(e ->
		{
			currentUser = new User(determineUser());
			System.out.println("Successfully switched to: " + currentUser); //testing
		});
		
		userMenu.add(switchUser);	//sub-option of user
		menu.add(userMenu);
	}

	public static void createTopPanel(JPanel main)
	{
		//1row, 2cols, 10px above/below
		JPanel inboxPanel = new JPanel(new GridLayout(1, 2, 10, 10));
		inboxPanel.setBorder(new TitledBorder(new EtchedBorder(),"Inbox"));
		
		//email list (JTextArea)
		DefaultListModel<Email> listModel = new DefaultListModel<>();
		for (Email e : currentUser.emails) listModel.addElement(e);
		JList<Email> emailList = new JList<>(listModel);
		JScrollPane listScrollable = new JScrollPane(emailList);
		
		
		//preview area (JTextArea in Scrollpane?) 
		JTextArea previewEmail = new JTextArea();
		previewEmail.setEditable(false);
		JScrollPane previewScrollable = new JScrollPane(previewEmail);
		

		//add scrollable panels and inbox container to homeFrame
		inboxPanel.add(listScrollable);
		inboxPanel.add(previewScrollable);
		main.add(inboxPanel);
	}
	
	public static void createBottomPanel(JPanel main)
	{
		JPanel newMsgPanel = new JPanel();
		newMsgPanel.setBorder(new TitledBorder(new EtchedBorder(),"New Message"));
		BoxLayout yAxisBox = new BoxLayout(newMsgPanel, BoxLayout.Y_AXIS);
		newMsgPanel.setLayout(yAxisBox);
		
		JComboBox<String>recipients = new JComboBox<>();
		
		//add elements: List all noncurrent users as available targets for message, can use recipients.getSelectedItem() later for logic
		for(User u : users) if(u.name != currentUser.name) recipients.addItem(u.name); 

		//subject field
		JPanel subject = new JPanel();
		BoxLayout yAxisSubject = new BoxLayout(subject, BoxLayout.Y_AXIS);
		subject.setLayout(yAxisSubject);
		
		final int WIDTH = 10;
		JLabel subjectLabel = new JLabel("Subject: ");
		JTextField subjectField = new JTextField(WIDTH);
		subject.add(subjectLabel);
		subject.add(subjectField);
		subjectField.setMaximumSize(new Dimension(Integer.MAX_VALUE, subjectField.getPreferredSize().height));
		
		//text area for drafting email
		final int ROWS = 15; // Lines of text
		final int COLUMNS = 50; // Characters in each row
		JTextArea messageDraft = new JTextArea(ROWS, COLUMNS);
		JScrollPane messageScrollable = new JScrollPane(messageDraft);
		
		//submission buttons
		JPanel controlButtonsPanel = new JPanel();
		JButton clearButton = new JButton("Clear");
		JButton sendButton = new JButton("Send");
		controlButtonsPanel.add(clearButton);
		controlButtonsPanel.add(sendButton);
		
		
		//add all to panel -> homeFrame
		newMsgPanel.add(recipients);
		appendButtons(newMsgPanel);
		newMsgPanel.add(subjectField);
		newMsgPanel.add(messageScrollable);
		newMsgPanel.add(controlButtonsPanel);
		
		main.add(newMsgPanel);
	}
	
	public static void appendButtons(JPanel panel)
	{
		// create buttons
		JRadioButton high = new JRadioButton("High");
		JRadioButton med = new JRadioButton("Medium");
		JRadioButton low = new JRadioButton("Low");

		// group buttons
		ButtonGroup priorityGroup = new ButtonGroup();
		priorityGroup.add(high);
		priorityGroup.add(med);
		priorityGroup.add(low);

		// group in container to have inline 
		JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		radioPanel.add(high);
		radioPanel.add(med);
		radioPanel.add(low);
		
		panel.add(radioPanel);
		// can use button.isSelected() later for logic 
	}
	

}