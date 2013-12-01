import java.util.Scanner;

/**
 * 
 */

/**
 * @author yeshu
 * @date 2013年12月1日
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// 启动服务
		new Thread(new Runnable() {

			@Override
			public void run() {
				ServerManager.getInstance().startServer();
			}
		}).start();

		// 启动线程循环接收命令
		new Thread(new Runnable() {

			@Override
			public void run() {
				boolean flag = true;
				Scanner sc = new Scanner(System.in);
				while (flag) {
					System.out.println("------------>please input command:forward, backward, stop, turnleft, turnright");
					String command = null;
					switch (sc.nextLine()) {
					case "forward":
						command = Command.FORWARD;
						break;
					case "backward":
						command = Command.BACKWARD;
						break;
					case "stop":
						command = Command.STOP;
						break;
					case "turnleft":
						command = Command.TURNLEFT;
						break;
					case "turnright":
						command = Command.TURNRIGHT;
						break;
					default:
						System.out.println("undefined command!");
						break;
					}
					
					if(null != command){
						ServerManager.getInstance().sendCommandToArduino(command);
					}
				}
				
				//关闭sc
				sc.close();
			}
		}).start();

	}

}
