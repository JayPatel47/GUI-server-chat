import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

/*
 * Clicker: A: I really get it    B: No idea what you are talking about
 * C: kind of following
 */

public class Server{

	int count = 1;	
	ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
	ArrayList<Integer> clientsActive = new ArrayList<Integer>();
	TheServer server;
	private Consumer<Serializable> callback;
	
	Server(Consumer<Serializable> call){
	
		callback = call;
		server = new TheServer();
		server.start();
	}
	
	public class TheServer extends Thread{
		
		public void run() {
		
			try(ServerSocket mysocket = new ServerSocket(5555);){
		    System.out.println("Server is waiting for a client!");
		  
			
		    while(true) {
		
				ClientThread c = new ClientThread(mysocket.accept(), count);
				callback.accept("client has connected to server: " + "client #" + count);
				clients.add(c);
				clientsActive.add(count);
				c.start();

				count++;
				
			    }
			}//end of try
				catch(Exception e) {
					callback.accept("Server socket did not launch");
				}
			}//end of while
		}
	

		class ClientThread extends Thread{
			
		
			Socket connection;
			int count;
			ObjectInputStream in;
			ObjectOutputStream out;
			
			ClientThread(Socket s, int count){
				this.connection = s;
				this.count = count;	
			}
			
			public void updateClients(ClientInfo data, boolean update) {
				
				if (update) {
					data.updateActiveClientList = true;
					synchronized(clientsActive) {
						for(int i : clientsActive) {
							data.clientsActive.add(i);
						}
					}
				}
				
				for(int i = 0; i < clients.size(); i++) {
					ClientThread t = clients.get(i);
					try {
						synchronized(out) {
							if (data.privateChat && data.recievers.contains(t.count)) {
								t.out.writeObject(data);
							} else if (!data.privateChat) {
								t.out.writeObject(data);
							}
						}
					}
					catch(Exception e) {}
				}
			}

			public void run(){
					
				try {
					in = new ObjectInputStream(connection.getInputStream());
					out = new ObjectOutputStream(connection.getOutputStream());
					connection.setTcpNoDelay(true);	
				}
				catch(Exception e) {
					System.out.println("Streams not open");
				}
				
				ClientInfo c = new ClientInfo();
				c.message = "new client on server: client #" + count;
				updateClients(c, true);
					
				 while(true) {
					    try {
					    	ClientInfo data = (ClientInfo)in.readObject(); // from client
					    	callback.accept("client: " + count + " sent: " + data.message);

					    	String message = data.message;
					    	data.message = "client #"+count+" said: " + message;
					    	updateClients(data, false);
					    	
					    	}
					    catch(Exception e) {
					    	callback.accept("OOOOPPs...Something wrong with the socket from client: " + count + "....closing down!");
					    	
					    	for(int i = 0; i < clientsActive.size(); i++) {
								if (clientsActive.get(i) == count) {
									clientsActive.remove(i);
								}
							}
					    	
					    	ClientInfo c2 = new ClientInfo();
					    	c2.message = "Client #"+count+" has left the server!";
					    	updateClients(c2, true);
					    	clients.remove(this);
					    	break;
					    }
				}

			}//end of run

		}//end of client thread
}


	
	

	
