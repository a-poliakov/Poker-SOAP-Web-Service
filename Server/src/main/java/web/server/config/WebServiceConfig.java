package web.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.oxm.ValidationFailureException;
import org.springframework.oxm.castor.CastorMarshaller;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.server.endpoint.mapping.PayloadRootQNameEndpointMapping;
import org.springframework.ws.soap.server.SoapMessageDispatcher;
import org.springframework.ws.soap.server.endpoint.SoapFaultDefinition;
import org.springframework.ws.soap.server.endpoint.SoapFaultMappingExceptionResolver;
import org.springframework.ws.transport.http.WebServiceMessageReceiverHandlerAdapter;
import org.springframework.ws.transport.http.WsdlDefinitionHandlerAdapter;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;
import web.server.model.PokerHandEvaluatorImpl;
import web.server.service.EvaluateHandMarshallingEndpoint;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {
    @Autowired
    Environment environment;

    /**
     * Настройка маршалера
     * @return CastorMarshaller сконфигурированный маршалер
     */
    @Bean
    public CastorMarshaller marshaller(){
        CastorMarshaller marshaller = new CastorMarshaller();
        marshaller.setMappingLocation(new ClassPathResource("mapping.xml"));
        return marshaller;
    }

    /**
     * Настройка отображения сообщений в конечные точки
     * @return PayloadRootQNameEndpointMapping настроенный mapping
     */
    @Bean
    public PayloadRootQNameEndpointMapping payloadMapping(){
        PayloadRootQNameEndpointMapping mapping = new PayloadRootQNameEndpointMapping();
        Map<String, Object> endpointMap = new HashMap<>();
        endpointMap.put("{http://www.khasang.ru/poker/schemas}EvaluateHandRequest", evaluateHandEndpoint());
        mapping.setEndpointMap(endpointMap);
        return mapping;
    }

    /* Настройка конечной точки службы */

//    /**
//     * Внедрению конечной точки, использующей JDom модель разбора XML
//     * @return EvaluateHandJDomEndpoint настроенная конечная точка
//     */
//    @Bean
//    public EvaluateHandJDomEndpoint evaluateHandEndpoint(){
//        EvaluateHandJDomEndpoint evaluateHandJDomEndpoint = new EvaluateHandJDomEndpoint();
//        evaluateHandJDomEndpoint.setPokerHandEvaluator(pokerHandEvaluator());
//        return evaluateHandJDomEndpoint;
//    }

    /**
     * Внедрению конечной точки, использующей маршалинг
     * @return EvaluateHandMarshallingEndpoint настроенная конечная точка
     */
    @Bean
    public EvaluateHandMarshallingEndpoint evaluateHandEndpoint(){
        EvaluateHandMarshallingEndpoint evaluateHandMarshallingEndpoint = new EvaluateHandMarshallingEndpoint();
        evaluateHandMarshallingEndpoint.setPokerHandEvaluator(pokerHandEvaluator());
        evaluateHandMarshallingEndpoint.setMarshaller(marshaller());
        evaluateHandMarshallingEndpoint.setUnmarshaller(marshaller());
        return evaluateHandMarshallingEndpoint;
    }

    /**
     * Конфигурация оценщика подборки карт
     * @return PokerHandEvaluatorImpl настроенный оценщик подборки карт
     */
    @Bean
    public PokerHandEvaluatorImpl pokerHandEvaluator(){
        return new PokerHandEvaluatorImpl();
    }

    /**
     * Конфигурация преобразователя Java Ошибок в XML ошибки SOAP и обратно
     * TODO: 04.09.2016 add my own ServiceFaultExceptions and implementation of SoapFaultMappingExceptionResolver
     * @return SoapFaultMappingExceptionResolver обработчик ошибок веб сервиса
     */
    @Bean
    public SoapFaultMappingExceptionResolver exceptionResolver(){
        SoapFaultMappingExceptionResolver exceptionResolver = new SoapFaultMappingExceptionResolver();
        SoapFaultDefinition faultDefinition = new SoapFaultDefinition();
        faultDefinition.setFaultCode(SoapFaultDefinition.SERVER);
        exceptionResolver.setDefaultFault(faultDefinition);
        Properties errorMappings = new Properties();
        errorMappings.setProperty(UnmarshallingFailureException.class.getName(), SoapFaultDefinition.SENDER.toString() + ", Invalid message received");
        errorMappings.setProperty(ValidationFailureException.class.getName(), SoapFaultDefinition.SENDER.toString() + ", Invalid message received");
        exceptionResolver.setExceptionMappings(errorMappings);
        exceptionResolver.setOrder(1);
        return exceptionResolver;
    }

    // TODO: 04.09.2016  Настроить сервлет MessageDispatcherServlet для SOAP так, чтобы одновременно был доступен и DispatcherServlet для Spring MVC

    @Bean
    public WebServiceMessageReceiverHandlerAdapter webServiceMessageReceiverHandlerAdapter(){
        return new WebServiceMessageReceiverHandlerAdapter();
    }

    @Bean
    public WsdlDefinitionHandlerAdapter wsdlDefinitionHandlerAdapter(){
        return new WsdlDefinitionHandlerAdapter();
    }

    @Bean
    public SoapMessageDispatcher messageDispatcher(){
        return new SoapMessageDispatcher();
    }

    @Bean
    public SimpleUrlHandlerMapping simpleUrlHandlerMapping(){
        SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
        handlerMapping.setDefaultHandler(messageDispatcher());
        Properties properties = new Properties();
        properties.put("*.wsdl", defaultWsdl11Definition(pokerSchema()));
        handlerMapping.setMappings(properties);
        return handlerMapping;
    }

//    @Bean
//    public SimpleWsdl11Definition myServiceDefinition(){
//        SimpleWsdl11Definition definition = new SimpleWsdl11Definition();
//        definition.setWsdl(new ClassPathResource("poker.wsdl"));
//        return definition;
//    }

    @Bean(name = "poker")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema countriesSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("Poker");
        wsdl11Definition.setLocationUri("/services");
        wsdl11Definition.setTargetNamespace("http://localhost:8080/poker");
        wsdl11Definition.setSchema(pokerSchema());
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema pokerSchema() {
        return new SimpleXsdSchema(new ClassPathResource("PokerTypes.xsd"));
    }

    @Bean
    public SimpleControllerHandlerAdapter simpleControllerHandlerAdapter(){
        return new SimpleControllerHandlerAdapter();
    }
}
