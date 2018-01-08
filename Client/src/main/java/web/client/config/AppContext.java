package web.client.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.castor.CastorMarshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.transport.http.HttpUrlConnectionMessageSender;
import web.client.service.TemplateBasedPokerClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class AppContext {
    @Autowired
    Environment environment;

    @Bean
    public WebServiceTemplate webServiceTemplate() throws IOException, URISyntaxException {
        WebServiceTemplate template = new WebServiceTemplate();
        template.setMessageFactory(saajSoapMessageFactory());
        template.setMessageSender(messageSender());
        template.setMarshaller(marshaller());
        template.setUnmarshaller(marshaller());
        return template;
    }

    @Bean
    public SaajSoapMessageFactory saajSoapMessageFactory(){
        return new SaajSoapMessageFactory();
    }

    @Bean
    public HttpUrlConnectionMessageSender messageSender() throws URISyntaxException, IOException {
        HttpUrlConnectionMessageSender messageSender = new HttpUrlConnectionMessageSender();
        messageSender.createConnection(new URI("http://localhost:8080/poker/services"));
        return messageSender;
    }

    @Bean
    public TemplateBasedPokerClient templateBasedPokerClient() throws IOException, URISyntaxException {
        TemplateBasedPokerClient client = new TemplateBasedPokerClient();
        client.setWebServiceTemplate(webServiceTemplate());
        return client;
    }

    @Bean
    public CastorMarshaller marshaller(){
        CastorMarshaller marshaller = new CastorMarshaller();
        marshaller.setMappingLocation(new ClassPathResource("mapping.xml"));
        return marshaller;
    }
}