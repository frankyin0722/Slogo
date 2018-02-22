package parser;

public interface TreeGenerator {
	
	public void increaseIndex();
	
	public int getIndex();
	
	public void increaseListStartIndex();
	
	public void increaseListEndIndex();
	
	public void recurse(CommandNode commandNode);
}