package web.client.model.marshalling;

import web.server.model.Card;

public class EvaluateHandRequest {
    private Card[] hand;

    public EvaluateHandRequest() {}

    public Card[] getHand() {
        return hand;
    }

    public void setHand(Card[] cards) {
        this.hand = cards;
    }
}
