package labs.lab9;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class EmailSystem {
	
	//fields that need to change during runtime cannot be local
	static Set<User> users = new HashSet<>();
	static User currentUser = new User(determineUser());

	static JFrame homeFrame = new JFrame();
	static ButtonGroup priorityGroup = new ButtonGroup();
	static JLabel currentUserDisplay = new JLabel();
	static JComboBox<String> currentRecipients = new JComboBox<>();

	//inbox
	static DefaultListModel<Email> inboxModel = new DefaultListModel<>();
	static JList<Email> currentEmailList = new JList<>(inboxModel);

	//preview
	static JTextArea previewEmail = new JTextArea();
	
	public static void main(String[] args) {
		new EmailSystem();
	}

	public EmailSystem()
	{
		// default program start
		users.add(new User("Robert Navarro"));
		System.out.println("Begin program as: " + currentUser);

		// menu bar (for home window)
		JMenuBar homeMenu = new JMenuBar();
		createFileMenu(homeMenu);
		createUserMenu(homeMenu);

		homeFrame.setLayout(new BorderLayout());

		// user indicator
		updateUserIndicator();

		//main area of program formatting
		JPanel mainContent = new JPanel();
		mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
		mainContent.add(Box.createVerticalStrut(25)); // 25px spacing top
		createTopPanel(mainContent);
		mainContent.add(Box.createVerticalStrut(25)); // 25px spacing between

		createBottomPanel(mainContent);
		previewEmail.setEditable(false);
		mainContent.add(Box.createVerticalStrut(25)); // 25px spacing under
		homeFrame.add(mainContent, BorderLayout.CENTER);

		// homeframe preferences
		homeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		homeFrame.setJMenuBar(homeMenu);
		homeFrame.setSize(1200, 800);
		homeFrame.setVisible(true);
	}
	
	public static String determineUser()
	{
		String name = "";
		while(name == null || name.isBlank() ||name.length() == 0)
		{
			name = JOptionPane.showInputDialog("Enter your username: ");
			if(name == null)
			{
				System.out.print("User cancelled login, exiting.");
				System.exit(0);
			}
		}
		//only adds to set if a unique name
		users.add(new User(name));
		return name;
	}
	
	public static void updateUserIndicator() 
	{
		currentUserDisplay.setText("Current User: " + currentUser);
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
			String name = determineUser();
			
			for (User u : users) {
				if (u.getName().equals(name)) {
					currentUser = u;
					break;
				}
			}

			// update indicator, recipients, inbox
			updateUserIndicator();	
			updateRecipients();
			refreshInbox();
			System.out.println("Successfully switched to: " + currentUser);
		});
		
		userMenu.add(switchUser);
		menu.add(userMenu);
	}

	public static void createTopPanel(JPanel main)
	{
		JPanel inboxPanel = new JPanel(new GridLayout(1, 2, 10, 10));
		inboxPanel.setBorder(new TitledBorder(new EtchedBorder(),"Inbox"));

		//inbox operations
		refreshInbox();
		JScrollPane listScrollable = new JScrollPane(currentEmailList);
		
		//preview area operations
		currentEmailList.addListSelectionListener(e -> {
			Email selected = currentEmailList.getSelectedValue();

			if(selected != null) previewEmail.setText(selected.getContents());
			else previewEmail.setText("");
		});
		JScrollPane previewScrollable = new JScrollPane(previewEmail);
		
		//add scrollable panels and inbox container to homeFrame
		inboxPanel.add(listScrollable);
		inboxPanel.add(previewScrollable);
		main.add(inboxPanel);
	}

	public static void refreshInbox()
	{
		inboxModel.clear();
		for (Email e : currentUser.emails)
			inboxModel.addElement(e);
	}
	
	public static void createBottomPanel(JPanel main)
	{
		//panel+formatting for bottom
		JPanel newMsgPanel = new JPanel();
		newMsgPanel.setBorder(new TitledBorder(new EtchedBorder(),"New Message"));
		BoxLayout yAxisBox = new BoxLayout(newMsgPanel, BoxLayout.Y_AXIS);
		newMsgPanel.setLayout(yAxisBox);
		
		//add elements: List all noncurrent users as available targets for message, can use currentRecipients.getSelectedItem() later for logic
		updateRecipients();

		//subject field
		JPanel subject = new JPanel();
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
		messageDraft.setEditable(true);
		
		//submission buttons
		JPanel controlButtonsPanel = new JPanel();
		JButton clearButton = new JButton("Clear");
		JButton sendButton = new JButton("Send");
		controlButtonsPanel.add(clearButton);
		controlButtonsPanel.add(sendButton);
		
		
		//add all to panel -> homeFrame
		newMsgPanel.add(currentRecipients);
		newMsgPanel.add(createPrioButtons());	//add panel of buttons
		newMsgPanel.add(subjectField);
		newMsgPanel.add(messageScrollable);
		newMsgPanel.add(controlButtonsPanel);
		
		//logic
		clearButton.addActionListener(e ->
		{
			messageDraft.setText("");
			subjectField.setText("");
			priorityGroup.clearSelection();
		});
		
		sendButton.addActionListener(e ->
		{
			//don't need to check validity
			String recipientName = (String) currentRecipients.getSelectedItem();
			Email currentEmail = new Email(currentUser.getName(), recipientName, determineSelectedRadio(), subjectField.getText(), messageDraft.getText());

			//find recipient based on name
			User targetUser = null;
			for (User u : users) {
				if (u.getName().equals(recipientName)) {
					targetUser = u;
					break;
				}
			}

			if(targetUser != null) targetUser.addEmail(currentEmail);

			//clear relevant fields
			messageDraft.setText("");
			subjectField.setText("");
			
			//show informational dialogue of confirmation
			JOptionPane.showMessageDialog(homeFrame, "Email sent!", "Success",JOptionPane.INFORMATION_MESSAGE);
		});
		
		main.add(newMsgPanel);
	}

	public static void updateRecipients()
	{
		currentRecipients.removeAllItems();
		for (User u : users)
			if (!u.getName().equals(currentUser.getName()))
				currentRecipients.addItem(u.getName());
	}
	
	public static JPanel createPrioButtons()
	{
		// create buttons
		JRadioButton high = new JRadioButton("High");
		high.setActionCommand("High");
		JRadioButton med = new JRadioButton("Medium");
		med.setActionCommand("Medium");
		JRadioButton low = new JRadioButton("Low");
		low.setActionCommand("Low");

		// group buttons
		priorityGroup.add(high);
		priorityGroup.add(med);
		priorityGroup.add(low);

		// group in container to have inline 
		JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		radioPanel.add(high);
		radioPanel.add(med);
		radioPanel.add(low);
		
		return radioPanel; 
		// can use button.isSelected() later for logic 
	}

	public static String determineSelectedRadio()
	{
		String priority = "";
		if (priorityGroup.getSelection().getActionCommand() != null) {
			priority = priorityGroup.getSelection().getActionCommand();
		}
		return priority;
	}
}