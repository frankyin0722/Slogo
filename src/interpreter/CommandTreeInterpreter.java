package interpreter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alerts.Alerts;
import alerts.CommandException;
import alerts.Resources;
import command.Command;
import command.CommandManager;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import observables.Listener;
import parser.CommandNode;
import turtle.Turtle;
import turtle.TurtleController;
import variables.Variable;
import variables.VariableManager;
/**
 * interpret the command tree generated by the parser and dynamically execute commands as the interpretation process goes on 
 */
public class CommandTreeInterpreter {
	private CommandManager myCommandManager;
	private VariableManager myVariables;
	private TurtleController myTurtleController;
	private HashMap<String, CommandNode> userDefinedCommands;
	private HashMap<String, List<CommandNode>> userDefinedCommandParameters;
	private ArrayList<String> history;
	private HashMap<String, String> activeUDC;
	private List<Listener> theseListeners;
	private List<Listener> activeUDCListener;
	/**
	 * initializes the command manager, the variable manager, the user defined command map, the user define command parameter map, the listeners that notify the front end whenever display needs to be updated  
	 * @param turtlecontroller: this holds the information of all the turtles that would be created in SLogo 
	 */
	public CommandTreeInterpreter(TurtleController turtlecontroller) {
		myCommandManager = new CommandManager();
		myVariables = new VariableManager();
		myTurtleController = turtlecontroller;
		userDefinedCommands = new HashMap<String, CommandNode>();
		userDefinedCommandParameters = new HashMap<String, List<CommandNode>>();
		history = new ArrayList<String>();
		theseListeners = new ArrayList<Listener>();
		activeUDC = new HashMap<>();
		activeUDCListener = new ArrayList<Listener>();
	}
	
	/**
	 * recursively interprets the command tree depending on the specific type of command that the method is currently looking at, and updates the value of the current node
	 * @param myRoot: the command tree generated by the parser to execute commands 
	 */
	public void interpretTree(CommandNode myRoot) {
		List<Object> Parameters = new ArrayList<>();
		for (int i = 0; i < myRoot.getNodeChildren().size(); i ++) {
			switch (myRoot.getCommandType()) {
			case "Control":
				interpretControl(i, myRoot, Parameters);
				break;
			case "Turtle":
				interpretTurtle(i, myRoot, Parameters);
				break;
			default:
				interpretTree(myRoot.getNodeChildren().get(i));
				Parameters.add(myRoot.getNodeChildren().get(i).getNodeValue());
				break;
			}
		}
		updateNodeValue(myRoot, Parameters);
	}
	
	private void interpretControl(int i, CommandNode myRoot, List<Object> Parameters) {
		if (i == 0 && !myRoot.getCommandName().equals("MakeUserInstruction") && !myRoot.getCommandName().equals("AskWith") && !myRoot.getCommandName().equals("Define")) {
			interpretTree(myRoot.getNodeChildren().get(i));
		}
		Parameters.add(myRoot.getNodeChildren().get(i));
	}
	
	private void interpretTurtle(int i, CommandNode myRoot, List<Object> Parameters) {
		
		for (int t = 0; t < myTurtleController.getActiveTurtleIndices().size(); t++) {
			myTurtleController.setCurrentTurtleIndex(myTurtleController.getActiveTurtleIndices().get(t)); // 0 index refers to 1 turtle 
			interpretTree(myRoot.getNodeChildren().get(i));
			Parameters.add(myRoot.getNodeChildren().get(i).getNodeValue());
		}
		if (myTurtleController.getActiveTurtleIndices().size()==0) {
			myTurtleController.setCurrentTurtleIndex(0);
		}
		else {
			myTurtleController.setCurrentTurtleIndex(myTurtleController.getActiveTurtleIndices().get(0)); // reset the current turtle to be the first in active turtle list 
		}
	}
	
	private void updateNodeValue(CommandNode node, List<Object> Parameters) {
		switch (node.getCommandType()) {
			case "UserDefined":
				updateUserDefined(node, Parameters);
				break;
			case "Turtle":
				updateTurtle(node, Parameters);
				break;
			case "Control":
				Parameters.add(this);
				createCommand(node, Parameters);
				break;
			case "Constant":
				break;
			case "Variable":
				updateVariable(node, Parameters);
				break;
			case "Bracket":
				updateBracket(node, Parameters);
				break;
			case "GroupBracket":
				updateGroupBracket(node, Parameters);
				break;
			case "Display":
				Parameters.add(this.getTurtleController());
				createCommand(node, Parameters);
			default: 
				createCommand(node, Parameters);
				break;
		}
	}
	
	private void updateUserDefined(CommandNode node, List<Object> Parameters) {
		List<CommandNode> para = userDefinedCommandParameters.get(node.getCommandName());
		for (int i = 0; i < para.size(); i++) {
			if (myVariables.checkVariable(para.get(i).getCommandName())) {
				myVariables.setVariable((double) Parameters.get(i), para.get(i).getCommandName());
			}
			else {
				myVariables.addVariable(new Variable((double) Parameters.get(i)), para.get(i).getCommandName());
			}
		}
		List<Object> paramForUserDefinedCommand = new ArrayList<>();
		String myDefinedCommandName = node.getCommandName();
		paramForUserDefinedCommand.add(myDefinedCommandName);
		paramForUserDefinedCommand.add(this);
		createCommand(node,paramForUserDefinedCommand);
	}
	
	private void updateTurtle(CommandNode node, List<Object> Parameters) {
		int individualParameterSize = node.getNodeChildren().size();
		int totalParamPairNum = 0;
		if (individualParameterSize!=0) {
			totalParamPairNum = Parameters.size()/individualParameterSize;
		}
		for (int i = 0; i < myTurtleController.getActiveTurtleIndices().size(); i++) {
			List<Object> individualParameter = new ArrayList<>();
			for (int j = 0; j < individualParameterSize; j++) {
				individualParameter.add(Parameters.get(j*totalParamPairNum+i));
			}
			individualParameter.add(myTurtleController.getTurtle(myTurtleController.getActiveTurtleIndices().get(i)-1));
			createCommand(node,individualParameter);
		}
	}
	
	private void updateVariable(CommandNode node, List<Object> Parameters) {
		if (!myVariables.checkVariable(node.getCommandName())) {
			Variable newvar = new Variable((double) 0);
			myVariables.addVariable(newvar, node.getCommandName());
		}
		node.setNodeValue((double) myVariables.getVariable(node.getCommandName()).getValue());
	}
	
	private void updateBracket(CommandNode node, List<Object> Parameters) {
		if (node.getNodeChildren().size()!=0) {
			//
			node.setNodeValue(node.getNodeChildren().get(node.getNodeChildren().size()-1).getNodeValue());
		}
		else {
			node.setNodeValue(0.0);
		}
	}
	
	private void updateGroupBracket(CommandNode node, List<Object> Parameters) {
		if (node.getNodeChildren().size()!=0) {
			double totalValue = 0;
			for (CommandNode sub : node.getNodeChildren()) {
				totalValue = totalValue + sub.getNodeValue();
			}
			node.setNodeValue(totalValue);
		}
		else {
			node.setNodeValue(0.0);
		}
	}
	
	private Command createCommandInstance(Class<?> commandClass, List<Object> parameters) {
		Constructor<?> commandConstructor = commandClass.getDeclaredConstructors()[0];
		Command thisCommand = null;
		try {
			thisCommand = (Command) commandConstructor.newInstance(parameters.toArray());
			return thisCommand;
		} catch (IllegalArgumentException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
			Alerts.createAlert(new CommandException(Resources.getString("CommandHeaderError")), "CommandMessageError1");
			return thisCommand; 
		}
	}
	
	private void invokeExecuteMethod(CommandNode node, Class<?> commandClass, Command thisCommand) {
		Method thisExecution = null;
		try {
			thisExecution = commandClass.getDeclaredMethod("execute", null);
	        try {
				double result = (double) thisExecution.invoke(thisCommand, null);
				node.setNodeValue(result);
			}
			catch (IllegalAccessException | InvocationTargetException e) {
				Alerts.createAlert(new CommandException(Resources.getString("CommandHeaderError")), "CommandMessageError8");
				return;
			}

		} catch (IllegalArgumentException | NoSuchMethodException | SecurityException e) {
			Alerts.createAlert(new CommandException(Resources.getString("CommandHeaderError")), "CommandMessageError7");
			return;
		} 
	}
	
	private void createCommand(CommandNode node, List<Object> parameters) {
		Class<?> commandClass = null;
		if (!node.getCommandType().equals("UserDefined")) {
			commandClass = myCommandManager.createCommand(node.getCommandType(), node.getCommandName());
		}
		else {
			commandClass = myCommandManager.createCommand(node.getCommandType(), node.getCommandType());
		}
		Command thisCommand = createCommandInstance(commandClass, parameters);

        invokeExecuteMethod(node, commandClass, thisCommand);
	}
	
	/**
	 * passes the list of current active turtles to commands for iteration 
	 * @return: list of active turtles 
	 */
	public List<Turtle> getCurrentActiveTurtles() {
		return myTurtleController.getActiveTurtles();
	}
	
	/**
	 * passes the list of current active turtle indices to commands for iteration 
	 * @return: list of active turtle indices
	 */
	public List<Integer> getCurrentActiveTurtleIndices() {
		return myTurtleController.getActiveTurtleIndices();
	}
	
	/**
	 * passes the turtle controller to commands for turtle information gathering 
	 * @return: the turtle controller 
	 */
	public TurtleController getTurtleController() {
		return myTurtleController;
	}
	
	/**
	 * passes the list of current available turtles to commands for iteration 
	 * @return: list of current available turtles 
	 */
	public List<Turtle> getCurrentAvailableTurtles() {
		return myTurtleController.getAllTurtles();
	}
	
	/**
	 * passes the variable manager to commands to get the value of variables used in commands 
	 * @return: the variable manager 
	 */
	public VariableManager getVariables() {
		return myVariables;
	}
	
	/**
	 * passes the map from user-defined command names to user-defined commands for user-defined command executions 
	 * @return: the mapping from user-defined command names to user-defined commands 
	 */
	public Map<String, CommandNode> getUserCommands(){
        return userDefinedCommands;
    }
	
	/**
	 * passes the map from user-defined command names to user-defined command parameters for user-defined command executions 
	 * @return: the mapping from user-defined command names to user-defined command parameters 
	 */
	public Map<String, List<CommandNode>> getUserCommandParameters(){
        return userDefinedCommandParameters;
    }
	
	/**
	 * adds the user-defined command name to history, gets called by front end 
	 */
	public void addToHistory(String command) {
		history.add(command);
		notifyListeners();
	}
	
	/**
	 * passes the list of history for commands, gets called by front end 
	 * @return: the list of history for executed commands 
	 */
	public List<String> getHistory(){
		return history;
	}
	/**
	 * Method to call update on each listener
	 */
	private void notifyListeners() {
		for(Listener l:theseListeners) {
			l.update();
		}
	}
	
	/**
	 * adds one listener instance to the list 
	 * @param l: one listener instance 
	 */
	public void addListener(Listener l) {
		theseListeners.add(l);
	}
	
	/**
	 * prints the currently existing user-defined commands 
	 */
	public void printExistingCommands() {
		for (String commandname : userDefinedCommands.keySet()) {
			CommandNode methodRoot = userDefinedCommands.get(commandname);
		}
	}
	
	/**
	 * iterates through the map and adds the user-defined commands to activeUDC
	 * @param map
	 */
	public void iterateUDC(Map<String, CommandNode> map) {
		for (String key: map.keySet()) {
			CommandNode command = map.get(key);
			addToActiveUDC(key, "");
		}
	}
	
	/**
	 * passes the index of the current active turtle to turtle-specific commands for individual turtle information  
	 * @return: an integer value for the index of the current active turtle
	 */
	public int getCurrentActiveTurtleIndex() {
		return myTurtleController.getCurrentTurtleIndex();
	}
	
	/**
	 * gets the command name of this node 
	 * @param: node
	 * @return: string of the command name  
	 */
	public String iterateNode(CommandNode node) {
		return node.getCommandName();
	}
	/**
	 * Adds to userDefinedCommands by
	 * @param commandName
	 * @param commandAction, as defined by name of first node
	 * Then notifies all UDC listeners
	 */
	public void addToActiveUDC(String commandName, String commandAction) {
			activeUDC.put(commandName, commandAction);
			notifyUDCListeners();
	}
	/**
	 * Method adds
	 * @param l, listener
	 * to list of active UDC Listeners
	 */
	public void addUDCListener(Listener l) {
		activeUDCListener.add(l);
	}
	/**
	 * Method to notify all listeners for new UDC
	 */
	public void notifyUDCListeners() {
		for (Listener l: activeUDCListener) {
			l.update();
		}
	}

}
