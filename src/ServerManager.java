import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 
 */

/**
 * @author yeshu
 * @date 2013年12月2日
 */
public class ServerManager {
	private static ServerManager mInstance = new ServerManager();
	private Socket arduinoSocket = null;
	private boolean isStart = false;
	
	private ServerManager(){
		
	}
	
	public static ServerManager getInstance(){
		return mInstance;
	}
	
	public void startServer() {
		isStart = true;
		
		try {
			ServerSocket server = new ServerSocket(60000);
			while (isStart) {
				//循环等待接收连接
				final Socket clientSocket = server.accept();
				System.out.println("client is connected!"
						+ clientSocket.getInetAddress());
				
				//处理已连接的客户端
				onClientConnected(clientSocket);
			}
			
			//服务器停止了，关闭套接字
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}  
	}
	
	public void stopServer(){
		this.isStart = false;
	}
	
	/**
	 * 发送命令
	 * @param command 
	 */
	public void sendCommandToArduino(String command){
		if(null == arduinoSocket || null == command){
			System.out.println("arduinoScoket or command is null!");
			return;
		}
		System.out.println("send command is :" + command);
		sendData(arduinoSocket, command.getBytes());
	}
	
	private void onClientConnected(Socket socket){
		if(null == socket){
			return;
		}
		
		if(isArduino(socket)){
			setAruduinoSocket(socket);
			startReceiveArduinoData(socket);
		}else{
			startReceiveControlClientData(socket);
		}
		
		
	}
	
	/**
	 * 开线程循环等待接收数据
	 * @param socket
	 */
	private void startReceiveArduinoData(final Socket socket){
		if(null == socket){
			return;
		}
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				//循环接收数据
				while (isStart) {
					BufferedReader is;
					try {
						is = new BufferedReader(new InputStreamReader(
								socket.getInputStream()));
						//接收来自arduion的数据
						System.out.println("arduion :" + is.readLine());
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}
			}
		}).start();

	}
	
	/**
	 * 
	 * @param socket
	 */
	private void startReceiveControlClientData(final Socket socket){
		if(null == socket){
			return;
		}
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				//循环接收数据
				BufferedReader is;
				try {
					is = new BufferedReader(new InputStreamReader(
							socket.getInputStream()));
					
					while (isStart) {
						String command = is.readLine();
						//接收到命令后，将该命令发送给arduino
						System.out.println("------------------>receive data :" + command);
					}
				} catch (IOException e1) {
					System.out.println("client is disconnect!");
					e1.printStackTrace();
				}

			}
		}).start();

	}
	
	

	/**
	 * 设置Arduino板子的socket
	 * @param socket
	 */
	private void setAruduinoSocket(Socket socket){
		this.arduinoSocket = socket;
	}
	
	/**
	 * 判断是不是arduino，就用最简单的判断ip的方法
	 * @param socket
	 */
	private boolean isArduino(Socket socket){
		if(null == socket){
			return false;
		}
		
		return Constant.ARDUINO_IP.equals(socket.getInetAddress().getHostName());
	}
	

	/**
	 * 发送数据
	 * @param socket
	 * @param data
	 */
	private void sendData(Socket socket, byte[] data) {
		DataOutputStream dos = null;
		try {
			dos = new DataOutputStream(socket.getOutputStream());
			dos.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	
}
