package View;

import java.awt.EventQueue;

import javax.swing.JFrame;

import RPC.RPCServerController;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.SwingWorker;
import javax.swing.JScrollPane;

public class View {
	RPCServerController clientController = new RPCServerController();

	private JFrame frame;
	private JTextPane queuesPane;

	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					View window = new View();
//					window.frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}

	/**
	 * Create the application.
	 */
	public View() {
		clientController.initializeRPC();
		// initialize();
	}

	/**
	 * Create the application.
	 * 
	 * @wbp.parser.entryPoint
	 */
	public void renderView() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					initialize();
					frame.setVisible(true);
					
					paralelThread();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 886, 679);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblNewLabel = new JLabel("SERVIDOR");
		lblNewLabel.setBounds(398, 11, 97, 14);
		frame.getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("FILAS");
		lblNewLabel_1.setBounds(408, 72, 46, 14);
		frame.getContentPane().add(lblNewLabel_1);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(245, 145, 358, 396);
		frame.getContentPane().add(scrollPane);
		
		queuesPane = new JTextPane();
		scrollPane.setViewportView(queuesPane);
	}
	
	public void paralelThread() {
		(new SwingWorker<Void, Void>() {
			@Override
			public Void doInBackground() {
				while (true) {
					try {
						Thread.sleep(5000);
						String panelContentQ = clientController.getListedQueuesAndItsAmountOfPendingMessages();
						queuesPane.setText(panelContentQ);
						
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
			}
		}).execute();
	}
}
