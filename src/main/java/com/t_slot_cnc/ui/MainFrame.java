package com.t_slot_cnc.ui;

import javax.swing.JFrame;
import javax.swing.JSplitPane;

import com.t_slot_cnc.controller.MainController;

/**
 * @author Alex Vazquez <vazqueza2000@gmail.com>
 */
public class MainFrame extends JFrame {

	/** Serialize */
	private static final long serialVersionUID = -5964334904697966801L;

	public MainFrame(MainController controller) {
		setTitle("T-Slot CNC Application");
		setSize(1000, 500);
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		RightPanel rightPanel = new RightPanel();
		MiddlePanel middlePanel = new MiddlePanel();
		LeftPanel leftPanel = new LeftPanel(controller, middlePanel, rightPanel);

		JSplitPane centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, middlePanel, rightPanel);
		centerSplit.setResizeWeight(0.5);

		JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, centerSplit);
		mainSplit.setResizeWeight(0.2);

		add(mainSplit);
	}

}
