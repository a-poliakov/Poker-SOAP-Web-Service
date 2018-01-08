package web.client.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.client.core.WebServiceTemplate;
import web.client.model.Card;
import web.client.model.PokerHandType;
import web.client.model.marshalling.EvaluateHandRequest;
import web.client.model.marshalling.EvaluateHandResponse;

import java.io.IOException;

public class MarshallingPokerClient implements PokerClient {
    private WebServiceTemplate webServiceTemplate;

    @Autowired
    public void setWebServiceTemplate(WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }

    @Override
    public PokerHandType evaluateHand(Card[] cards) throws IOException {
            EvaluateHandRequest request = new EvaluateHandRequest();// Создание
            request.setHand(cards); // запроса
            EvaluateHandResponse response = (EvaluateHandResponse) // Отправка
                    webServiceTemplate.marshalSendAndReceive(request); // запроса
            return response.getPokerHand(); // Возвращает объект ответа
    }
}
