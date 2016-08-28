package org.cns.client;

import org.cns.api.client.ClientProtocol;
import org.cns.api.client.ServerAdapter;

/**
 * Реализация сервиса чата.
 * 
 * @author johnson
 *
 */
public class ChatService {

	private ClientProtocol handler;

	public ChatService() {

	}

	public void init(ServerAdapter sa) {
		this.handler = new ClientProtocolHandler();
		this.handler.initialize(sa);

		InputProcessor inputProcessor = new InputProcessor(this.handler);
		inputProcessor.start();

		OutputProcessor outputProcessor = new OutputProcessor(this.handler);
		outputProcessor.start();
	}

	public ClientProtocol getHandler() {
		return handler;
	}

}
