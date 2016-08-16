package org.cns.client;

import org.cns.api.client.ClientState;
import org.cns.model.ChatHistory;

/**
 * Простая реализация клиентского состояния.
 * 
 * @author johnson
 *
 */
public class ClientStateImpl implements ClientState {

	private ChatHistory history;

	public ClientStateImpl() {
		this.history = new ChatHistory();
	}

	@Override
	public ChatHistory getChatHistory() {
		return this.history;
	}

}
