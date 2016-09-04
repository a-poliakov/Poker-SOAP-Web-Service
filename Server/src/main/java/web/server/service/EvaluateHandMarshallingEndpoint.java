package web.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;
import web.server.model.PokerHand;
import web.server.model.PokerHandEvaluator;
import web.server.model.PokerHandType;
import web.server.model.marshalling.EvaluateHandRequest;
import web.server.model.marshalling.EvaluateHandResponse;

public class EvaluateHandMarshallingEndpoint extends AbstractMarshallingPayloadEndpoint {
    private PokerHandEvaluator pokerHandEvaluator;

    @Autowired
    public void setPokerHandEvaluator(PokerHandEvaluator pokerHandEvaluator) {
        this.pokerHandEvaluator = pokerHandEvaluator;
    }

    @Override
    protected Object invokeInternal(Object object) throws Exception {
        EvaluateHandRequest request =  (EvaluateHandRequest) object;
        PokerHand pokerHand = new PokerHand();
        pokerHand.setCards(request.getHand());
        PokerHandType pokerHandType = pokerHandEvaluator.evaluateHand(pokerHand); // Оценивает комбинацию карт
        return new EvaluateHandResponse(pokerHandType);
    }
}
