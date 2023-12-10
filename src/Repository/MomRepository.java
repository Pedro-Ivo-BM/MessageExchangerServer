package Repository;


import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import javax.jms.*;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;

public class MomRepository {
	ConnectionFactory connectionFactory;
	Connection connection;
	Session session;

	///////////////////////// CONFIGS INICIAIS //////////////////////////
	private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;

	public void initializeMom() throws JMSException {
		connectionFactory = new ActiveMQConnectionFactory(url);
		connection = connectionFactory.createConnection();
		connection.start();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	}

	public void finishMom() throws JMSException {
		session.close();
		connection.close();
	}

	///////////////////////////////// QUEUES
	///////////////////////////////// ////////////////////////////////////////////
	public Destination createQueue(String queueName) throws JMSException {
		Destination destination = session.createQueue(queueName);
		MessageProducer producer = session.createProducer(destination);
		producer.close();
		return destination;
	}

	public void deleteQueue(String queueName) throws JMSException {
		Destination destination = session.createQueue(queueName);
		((ActiveMQConnection) connection).destroyDestination((ActiveMQDestination) destination);

	}

	public Set<ActiveMQQueue> getExistingQueues() throws JMSException {
		Set<ActiveMQQueue> queues = ((ActiveMQConnection) connection).getDestinationSource().getQueues();
		return queues;
	}

	public List<String> getExistingQueuesNames() throws JMSException {
		Set<ActiveMQQueue> queues = ((ActiveMQConnection) connection).getDestinationSource().getQueues();
		List<String> queueNames = new ArrayList<>();
		queues.forEach(fila -> {
			String nome = fila.getPhysicalName();
			queueNames.add(nome);

		});
		return queueNames;
	}

	public ActiveMQQueue getEspecificQueueFromName(String queueName) throws JMSException {
		Set<ActiveMQQueue> queues = ((ActiveMQConnection) connection).getDestinationSource().getQueues();
		for (ActiveMQQueue value : queues) {
			if (value.getPhysicalName().equals(queueName)) {
				// System.out.println("Valor encontrado: " + value);
				return value;

			}
		}
		return null;
	}

	public Boolean hasPendingMessagesFromQueue(String queueName) throws JMSException {
		ActiveMQQueue queue = (ActiveMQQueue) session.createQueue(queueName);
		QueueBrowser browser = session.createBrowser((ActiveMQQueue) queue);
		Enumeration<?> enumeration = browser.getEnumeration();
		return enumeration.hasMoreElements();

	}

	public int getAmountOfPendingMessagesFromQueue(String queueName) throws JMSException {
		ActiveMQQueue queue = (ActiveMQQueue) session.createQueue(queueName);
		QueueBrowser browser = session.createBrowser((ActiveMQQueue) queue);
		Enumeration<?> enumeration = browser.getEnumeration();
		int count = 0;
		while (enumeration.hasMoreElements()) {
			enumeration.nextElement();
			count++;
		}
		return count;
	}

	public List<String> getAllPendingMessagesFromQueue(String queueName, MessageConsumer consumer) throws JMSException {
		ActiveMQQueue queue = (ActiveMQQueue) session.createQueue(queueName);
		QueueBrowser browser = session.createBrowser((ActiveMQQueue) queue);
		Enumeration<?> enumeration = browser.getEnumeration();
		List<String> messages = new ArrayList<>();

		while (enumeration.hasMoreElements()) {
			Message message = (Message) consumer.receive();

			if (message instanceof TextMessage) {
				TextMessage textMessage = (TextMessage) message;
				messages.add(textMessage.getText());
				// message.acknowledge();
			}
			enumeration.nextElement();
		}
		return messages;
	}

	/////////////////////////////// TOPICS
	/////////////////////////////// ////////////////////////////////////////////////////////////
	public Destination createTopic(String topicName) throws JMSException {
		Destination destination = session.createTopic(topicName);
		MessageProducer producer = session.createProducer(destination);
		producer.close();
		return destination;
	}

	public void deleteTopic(String topicName) throws JMSException {
		Destination destination = session.createTopic(topicName);
		((ActiveMQConnection) connection).destroyDestination((ActiveMQDestination) destination);

	}

	public Set<ActiveMQTopic> getExistingTopics() throws JMSException {
		Set<ActiveMQTopic> topics = ((ActiveMQConnection) connection).getDestinationSource().getTopics();
		return topics;
	}

	public List<String> getExistingTopicNames() throws JMSException {
		Set<ActiveMQTopic> topics = ((ActiveMQConnection) connection).getDestinationSource().getTopics();
		List<String> topicsNames = new ArrayList<>();
		topics.forEach(fila -> {
			String nome = fila.getPhysicalName();
			topicsNames.add(nome);

		});
		return topicsNames;
	}

	public ActiveMQTopic getEspecificTopicFromName(String topicName) throws JMSException {
		Set<ActiveMQTopic> topics = ((ActiveMQConnection) connection).getDestinationSource().getTopics();
		for (ActiveMQTopic value : topics) {
			if (value.getPhysicalName().equals(topicName)) {
				// System.out.println("Valor encontrado: " + value);
				return value;

			}
		}
		return null;
	}

	/////////////////////////////// RESTO ///////////////////////////
	public MessageProducer initializeMessageProducer(String queueName) throws JMSException {
		Destination destination = session.createQueue(queueName);
		MessageProducer producer = session.createProducer(destination);
		return producer;
	}

	public MessageProducer initializeMessagePublisher(String topicName) throws JMSException {
		Destination destination = session.createTopic(topicName);
		MessageProducer producer = session.createProducer(destination);
		return producer;
	}

	public void closeProducerOrPublisher(MessageProducer producerOrPublisher) throws JMSException {
		producerOrPublisher.close();
	}

	public MessageConsumer initializeClient(String queueName) throws JMSException {
		Destination destination = session.createQueue(queueName);
		MessageConsumer client = session.createConsumer(destination);
		return client;
	}
	
	public void closeClient(MessageConsumer client) throws JMSException {
		client.close();
	}
	
	public TextMessage createTextMessage(String message) throws JMSException {
		TextMessage textMessage = session.createTextMessage(message);
		return textMessage;
	}

}


