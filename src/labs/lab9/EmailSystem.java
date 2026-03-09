package labs.lab9;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

public class EmailSystem {
	//fields that need to change during runtime cannot be local
	static Set<User> users = new HashSet<>();
	static User currentUser = new User(determineUser());

	static JFrame homeFrame = new JFrame();

	//user display
	static JPanel currentUserPanel = new JPanel();
	static JLabel currentUserDisplay = new JLabel();
	
	static ButtonGroup priorityGroup = new ButtonGroup();
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

		//formatting, menu bar, user indicator, main for program control
		setHomeFramePreferences();
		setHomeMenuBar();
		updateUserIndicator();
		createMain();
		publishHomeFrame();
	}

	public static void setHomeFramePreferences()
	{
		homeFrame.setLayout(new BorderLayout());
		homeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		homeFrame.setSize(600, 800);
	}

	public static void setHomeMenuBar()
	{
		JMenuBar homeMenu = new JMenuBar();
		createFileMenu(homeMenu);
		createUserMenu(homeMenu);
		homeFrame.setJMenuBar(homeMenu);
	}

	public static void createMain() {
		JPanel mainContent = new JPanel();
		mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
		previewEmail.setEditable(false);

		createInboxPanel(mainContent);

		mainContent.add(Box.createVerticalStrut(25)); // 25px spacing between
		createNewMsgPanel(mainContent);
		mainContent.add(Box.createVerticalStrut(25)); // 25px spacing under

		homeFrame.add(mainContent, BorderLayout.CENTER);
	}

	public static void publishHomeFrame()
	{
		homeFrame.pack();
		homeFrame.setLocationRelativeTo(null);
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
		currentUserPanel.setLayout(new GridBagLayout());
		currentUserPanel.add(currentUserDisplay);
		currentUserPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));	//add margins 
		homeFrame.add(currentUserPanel, BorderLayout.NORTH);
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

	public static void createInboxPanel(JPanel main)
	{
		JPanel inboxPanel = new JPanel(new GridLayout(1, 2, 10, 0));	//horizontal, vertical gap
		inboxPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20)); // add margins
		inboxPanel.setBorder(new TitledBorder(new EtchedBorder(),"Inbox"));

		//inbox operations
		refreshInbox();
		JScrollPane listScrollable = new JScrollPane(currentEmailList);
		inboxPanel.add(listScrollable);
		
		//preview area operations
		currentEmailList.addListSelectionListener(e -> {
			Email selected = currentEmailList.getSelectedValue();

			if(selected != null) previewEmail.setText(selected.getContents());
			else previewEmail.setText("");
		});
		JScrollPane previewScrollable = new JScrollPane(previewEmail);
		inboxPanel.add(previewScrollable);
		
		//publish changes to main
		main.add(inboxPanel);
	}

	public static void refreshInbox()
	{
		inboxModel.clear();

		//invoke a sort on the arraylist via Collections.sort in Email class
		for (Email e : currentUser.sortEmails())
			inboxModel.addElement(e);
	}
	
	public static void createNewMsgPanel(JPanel main)
	{
		JPanel newMsgPanel = new JPanel();

		//panel+formatting for bottom
		Border margin = BorderFactory.createEmptyBorder(10, 10, 10, 10);
		Border title = new TitledBorder(new EtchedBorder(), "New Message");
		newMsgPanel.setBorder(new CompoundBorder(margin, title));

		BoxLayout yAxisBox = new BoxLayout(newMsgPanel, BoxLayout.Y_AXIS);
		newMsgPanel.setLayout(yAxisBox);
		
		//add elements: List all noncurrent users as available targets for message, can use currentRecipients.getSelectedItem() later for logic
		updateRecipients();

		//recipients
		JPanel recipientWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
		recipientWrapper.add(new JLabel("To: "));
		recipientWrapper.add(currentRecipients);
		newMsgPanel.add(recipientWrapper);

		//priority buttons
		JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonWrapper.add(createPrioButtons());
		newMsgPanel.add(buttonWrapper);

		//subject field
		JPanel subjectWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JLabel subjectLabel = new JLabel("Subject: ");
		JTextField subjectField = new JTextField(20);
		subjectWrapper.add(subjectLabel);
		subjectWrapper.add(subjectField);
		newMsgPanel.add(subjectWrapper);
		
		//message area
		JTextArea messageDraft = new JTextArea(15, 50);
		JScrollPane messageScrollable = new JScrollPane(messageDraft);
		newMsgPanel.add(messageScrollable);
		
		//submission buttons
		JPanel controlWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JButton sendButton = new JButton("Send");
		JButton clearButton = new JButton("Clear");
		controlWrapper.add(sendButton);
		controlWrapper.add(clearButton);
		newMsgPanel.add(controlWrapper);
		
		//clearing fields logic
		clearButton.addActionListener(e ->
		{
			messageDraft.setText("");
			subjectField.setText("");
			priorityGroup.clearSelection();
		});
		
		//sending email logic
		sendButton.addActionListener(e ->
		{
			String recipientName = (String) currentRecipients.getSelectedItem();
			String priority = determineSelectedRadio();
			String subject = subjectField.getText();
			String body = messageDraft.getText();

			boolean isValid = recipientName != null &&
					!priority.isEmpty() &&
					!subject.isBlank() &&
					!body.isBlank();

			if(isValid)
			{
				Email currentEmail = new Email(
						currentUser.getName(),
						recipientName,
						determineSelectedRadio(),
						subjectField.getText(),
						messageDraft.getText(),
						LocalDateTime.now());

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
				JOptionPane.showMessageDialog(homeFrame, "Email sent successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
			}
			else 
				JOptionPane.showMessageDialog(homeFrame, "One or more fields empty!", "Failure", JOptionPane.INFORMATION_MESSAGE);
			
		});
		
		// publish changes to main
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
		ButtonModel selectedModel = priorityGroup.getSelection();
		if (selectedModel != null) return selectedModel.getActionCommand();
		return "";
	}
}