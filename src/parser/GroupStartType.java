package parser;

import java.util.List;

public class GroupStartType implements CommandTypes{
	private TreeGenerator myTreeGenerator;
	private List<String> userInput;
	private int BracketNum = 0;
	
	public GroupStartType(List<String> input, TreeGenerator treegenerator) {
		myTreeGenerator = treegenerator;
		userInput = input;
	}
	
	public void recurse(CommandNode node) {
		String currentValue = userInput.get(myTreeGenerator.getIndex()); // which parsed item the recursion is currently looking at 
		CommandNode child = new CommandNode("Bracket", currentValue + ":" + BracketNum++, null, 0);
		node.addChild(child); 
		myTreeGenerator.printNode(child);
		myTreeGenerator.increaseIndex();
		while (!userInput.get(myTreeGenerator.getIndex()).equals(")")) {
			myTreeGenerator.recurse(child);
		}
		myTreeGenerator.increaseIndex();
	}

	@Override
	public String whichType() {
		return "GroupStart";
	}
}
