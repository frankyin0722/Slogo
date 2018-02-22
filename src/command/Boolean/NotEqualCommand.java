package command.Boolean;

public class NotEqualCommand {
	private double input1;
	private double input2;
	
	public NotEqualCommand (double test1, double test2){
		input1 = test1;
		input2 = test2;
	}
	
	public double execute(){
		return (input1 != input2) ? 1:0;
	}
	
}