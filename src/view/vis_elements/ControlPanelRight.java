package view.vis_elements;

import java.util.ResourceBundle;
import interpreter.CommandTreeInterpreter;
import javafx.scene.layout.VBox;
import turtle.TurtleController;
import view.canvas.DrawingWindow;
import view.menus.CustomVarsMenu;
import view.menus.HelpMenu;
import view.menus.LanguageMenu;
import view.menus.TurtleSelectionMenu;

public class ControlPanelRight extends VBox {
	private LanguageMenu myLanguageMenu;
	private ResourceBundle myResources;
	private CommandTreeInterpreter interpreter;
	
	public ControlPanelRight(CommandTreeInterpreter i, ResourceBundle resources, TurtleController tc) {
		interpreter = i;
		initializeMenus(tc);
	}

	private void initializeMenus(TurtleController tc) {
//		myLanguageMenu = new LanguageMenu();
//		this.getChildren().addAll(new VariableMenu(), myLanguageMenu, new CustomVarsMenu());
		this.getChildren().addAll(
//				new VariableMenu(),
				new CustomVarsMenu(interpreter),
				myLanguageMenu = new LanguageMenu(),
				new HelpMenu()
//				new TurtleSelectionMenu(tc)
				);
	}
	
	public ResourceBundle getLanguage() {
		myResources = myLanguageMenu.getLanguage();
		return myResources;
	}
}
