/*
 * Christopher DeRoche
 * Date Created: 5/25/2019
 * https://github.com/compact-disc
 * 
 * Class to start the program
 */

package twitter;

public class Run {
	
	@SuppressWarnings("unused")
	private BotDriver derocheBot;
	
	public Run() {
		
		this.derocheBot = new BotDriver();
		
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		
		System.out.println("Starting DeRoche Bot...");
		Run startBot = new Run();
		
	}
	
}
