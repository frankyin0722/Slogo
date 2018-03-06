package command.Control;

import java.util.ArrayList;
import java.util.List;

import command.Command;
import interpreter.CommandTreeInterpreter;
import parser.CommandNode;

public class AskWith implements Command{
	private CommandTreeInterpreter myInterpreter;
	private List<Integer> myTemporaryActiveTurtleIndices;
	private CommandNode myCondition;
	private List<CommandNode> mySubCommands;
	private List<Integer> myOldActiveTurtleIndices;
	
	public AskWith(CommandNode condition, CommandNode subcommandsParent, CommandTreeInterpreter tree) {
		myInterpreter = tree;
		myCondition = condition.getNodeChildren().get(0);
		myTemporaryActiveTurtleIndices = new ArrayList<>();
		mySubCommands = subcommandsParent.getNodeChildren();
		myOldActiveTurtleIndices = myInterpreter.getCurrentActiveTurtleIndices();
	}
	
	public double execute() {
		for (int i = 1; i <= myInterpreter.getCurrentAvailableTurtles().size(); i++) { // creates an active list of turtles that satisfies the condition 
			myInterpreter.setCurrentActiveTurtleIndex(i);
			myInterpreter.interpretTree(myCondition);
			if (myCondition.getNodeValue()!=0) {
				myTemporaryActiveTurtleIndices.add(myInterpreter.getCurrentActiveTurtleIndex());
			}
		}
		myInterpreter.setCurrentActiveTurtleIndices(myTemporaryActiveTurtleIndices);
		for (int i = 0; i < mySubCommands.size(); i++) {
			myInterpreter.interpretTree(mySubCommands.get(i));
		}
		myInterpreter.setCurrentActiveTurtleIndices(myOldActiveTurtleIndices);
		if (mySubCommands.size() != 0) {
			return (double) mySubCommands.get(mySubCommands.size()-1).getNodeValue();
		}
		else {
			return 0.0;
		}
	}
}
