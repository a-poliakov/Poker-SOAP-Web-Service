package web.client.service;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.transform.JDOMResult;
import org.jdom2.transform.JDOMSource;
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

    public PokerHandType evaluateHand(Card[] cards)
            throws IOException {
        // Конструирование XML-сообщения
        Element requestElement = new Element("EvaluateHandRequest");
        Namespace ns = Namespace.getNamespace("http://www.springinaction.com/poker/schemas");
        requestElement.setNamespace(ns);
        Document doc = new Document(requestElement);
        for(int i=0; i<cards.length; i++) {
            Element cardElement = new Element("card");
            Element suitElement = new Element("suit");
            suitElement.setText(cards[i].getSuit().toString());
            Element faceElement = new Element("face");
            faceElement.setText(cards[i].getFace().toString());
            cardElement.addContent(suitElement);
            cardElement.addContent(faceElement);
            doc.getRootElement().addContent(cardElement);
        }
        // Отправка сообщения с использованием шаблона
        JDOMSource requestSource = new JDOMSource(doc);
        JDOMResult result = new JDOMResult();
        webServiceTemplate.sendAndReceive(requestSource, result);
        // Парсинг XML-ответа
        Document resultDocument = result.getDocument();
        Element responseElement = resultDocument.getRootElement();
        Element handNameElement = responseElement.getChild("handName", ns);
        return PokerHandType.valueOf(handNameElement.getText());
    }
}
