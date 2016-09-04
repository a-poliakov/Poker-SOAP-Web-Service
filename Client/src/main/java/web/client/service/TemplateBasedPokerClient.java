package web.client.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.client.core.WebServiceTemplate;
import web.client.model.Card;
import web.client.model.PokerHandType;

import java.io.IOException;

public class TemplateBasedPokerClient implements PokerClient {
    private WebServiceTemplate webServiceTemplate;

    @Autowired
    public void setWebServiceTemplate(WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }

    @Override
    public PokerHandType evaluateHand(Card[] cards) throws IOException {
        return null;
    }
}
