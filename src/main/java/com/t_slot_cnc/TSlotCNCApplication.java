package com.t_slot_cnc;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import com.t_slot_cnc.controller.MainController;
import com.t_slot_cnc.ui.LeftPanel;
import com.t_slot_cnc.ui.MachineSettingsPanel;
import com.t_slot_cnc.ui.MiddlePanel;
import com.t_slot_cnc.ui.RightPanel;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

/**
 * @author Alex Vazquez <vazqueza2000@gmail.com>
 */
@SpringBootApplication
public class TSlotCNCApplication extends Application {

	private ConfigurableApplicationContext springContext;

	@Override
	public void init() {
		springContext = new SpringApplicationBuilder(TSlotCNCApplication.class)
				.web(WebApplicationType.NONE)
				.headless(false)
				.run();
	}

	@Override
	public void start(Stage primaryStage) {
		MainController controller = springContext.getBean(MainController.class);

		RightPanel rightPanel = new RightPanel(controller::saveGCode);
		MiddlePanel middlePanel = new MiddlePanel();
		LeftPanel leftPanel = new LeftPanel(controller, middlePanel, rightPanel);

		SplitPane centerSplit = new SplitPane(middlePanel, rightPanel);
		centerSplit.setDividerPositions(0.5);

		SplitPane mainSplit = new SplitPane(leftPanel, centerSplit);
		mainSplit.setDividerPositions(0.2);

		MachineSettingsPanel machineSettingsPanel = new MachineSettingsPanel(
				controller.getMachineSettings(), controller::saveMachineSettings);

		Tab generateTab = new Tab("Generate Parts", mainSplit);
		generateTab.setClosable(false);
		Tab settingsTab = new Tab("Machine Settings", machineSettingsPanel);
		settingsTab.setClosable(false);

		TabPane tabPane = new TabPane(generateTab, settingsTab);

		primaryStage.setTitle("T-Slot CNC Application");
		primaryStage.setScene(new Scene(tabPane, 1200, 600));
		primaryStage.show();
	}

	@Override
	public void stop() {
		springContext.close();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
