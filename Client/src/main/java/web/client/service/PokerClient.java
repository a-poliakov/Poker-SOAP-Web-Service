package web.client.service;

import web.client.model.Card;
import web.client.model.PokerHandType;

import java.io.IOException;

public interface PokerClient {
    PokerHandType evaluateHand(Card[] cards) throws IOException;
}
