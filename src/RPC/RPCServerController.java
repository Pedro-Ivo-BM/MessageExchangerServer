package RPC;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;

import Repository.MomRepository;

public class RPCServerController implements RPCServerInterface {
	private Registry registry = null;
	private Remote remoteObject = null;
	MomRepository momRepository = new MomRepository();

	public RPCServerController() {
	}

	public void initializeRPC() {
		try {
			momRepository.initializeMom();
			
			registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
			remoteObject = UnicastRemoteObject.exportObject(this, 0);
			registry.bind("ServerMOM", remoteObject);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Erro initializeRPC");
			e.printStackTrace();
		}
	}

	public String getListedQueuesAndItsAmountOfPendingMessages() {

		try {
			List<String> namesList = momRepository.getExistingQueuesNames();

			StringBuilder queueNames = new StringBuilder();

			namesList.forEach(queue -> {
				int amountOfPendingMessages = 0;
				try {
					amountOfPendingMessages = momRepository.getAmountOfPendingMessagesFromQueue(queue);
				} catch (JMSException e) {
					System.out.println("erro ao pegar quantidade de valores pendentes da fila" + queue);
					amountOfPendingMessages = 0;
					e.printStackTrace();

				}
				queueNames
						.append("NOME FILA: " + queue + ". \nMENSAGENS PENDENTES: " + amountOfPendingMessages + "\n\n");

			});

			return queueNames.toString();

		} catch (JMSException e) {
			System.out.println("erro ao listar filas ");
			e.printStackTrace();
			return "Erro ao listar filas";
		}
	}

	@Override
	public void createQueueForNewUser(String userName) throws RemoteException {
		try {
			momRepository.createQueue(userName);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void saveOfflineUserPendingMessage(String userName, String message) throws RemoteException {
		try {
			MessageProducer producer = momRepository.initializeMessageProducer(userName);
			TextMessage textMessage = momRepository.createTextMessage(message);
			producer.send(textMessage);
			momRepository.closeProducerOrPublisher(producer);
		} catch (JMSException e) {
			System.out.println("erro ao mandar mensagem para fila " + userName);
			e.printStackTrace();
		}

	}

	@Override
	public List<String> getUserPendingMessages(String userName) throws RemoteException {
		List<String> messages = new ArrayList<>();
		try {
			MessageConsumer consumer = momRepository.initializeClient(userName);
			messages = momRepository.getAllPendingMessagesFromQueue(userName, consumer);

			momRepository.closeClient(consumer);

			return messages;

		} catch (JMSException e) {
			List<String> messagesError = new ArrayList<>();
			e.printStackTrace();
			return messagesError;
		}
	}

}
