package web.client.service;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import web.client.model.Card;
import web.client.model.PokerHandType;
import web.client.model.marshalling.EvaluateHandRequest;
import web.client.model.marshalling.EvaluateHandResponse;

import java.io.IOException;

public class PokerServiceGateway extends WebServiceGatewaySupport implements PokerClient{
    public PokerHandType evaluateHand(Card[] cards) throws IOException {
        EvaluateHandRequest request = new EvaluateHandRequest();
        request.setHand(cards);
        // Использовать предоставленный объект WebServiceTemplate
        EvaluateHandResponse response = (EvaluateHandResponse)
                getWebServiceTemplate().marshalSendAndReceive(request);
        return response.getPokerHand();
    }
}
