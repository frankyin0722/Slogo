package visual_elements;

import java.util.ResourceBundle;

import interpreter.CommandTreeInterpreter;
import javafx.scene.layout.VBox;
import turtle.Turtle;
import visual_elements.menu_managers.HistoryMenu;
import visual_elements.menu_managers.PenColorMenu;
import visual_elements.menu_managers.TurtleMenu;

public class ControlPanelLeft extends VBox {

	private Turtle myTurtle;
	private CommandTreeInterpreter interpreter;
	
	public ControlPanelLeft(CommandTreeInterpreter i, Turtle turtle, ResourceBundle resources) {
		myTurtle = turtle;
		interpreter = i;
		initializeMenus(resources);
	}

	private void initializeMenus(ResourceBundle resources) {
		this.getChildren().addAll(
				new HistoryMenu(interpreter, resources), 
				new TurtleMenu(resources, myTurtle), 
				new PenColorMenu(resources, myTurtle.getPen()));
	}
}