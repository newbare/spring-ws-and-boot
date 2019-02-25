package br.com.minhaempresa;

import br.com.minhaempresa.messageria.RecuperarDocumentoMessageConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.jndi.JndiTemplate;

import javax.jms.ConnectionFactory;
import javax.naming.Context;
import javax.naming.NamingException;
import java.util.Properties;

@Configuration
@EnableJms
public class MessageriaConfig {

    public static final String FILA_RECUPERAR_PECA = "java:jms/queue/test";
    private static final Logger Logger = LoggerFactory.getLogger(MessageriaConfig.class);

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory
                = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(jmsConnectionFactory());
        return factory;
    }

    @Bean
    public JndiTemplate jndiTemplate() {
        JndiTemplate jndiTemplate = new JndiTemplate();
        Properties properties = new Properties();
        properties.put(Context.INITIAL_CONTEXT_FACTORY,"org.jboss.naming.remote.client.InitialContextFactory");
        properties.put(Context.PROVIDER_URL,"remote://localhost:4447");
        properties.put("remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED", "false");
        properties.put("remote.connection.default.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS", "false");
        properties.put(Context.SECURITY_PRINCIPAL,"teste");
        properties.put(Context.SECURITY_CREDENTIALS,"jbo$$122333");
        jndiTemplate.setEnvironment(properties);
        return jndiTemplate;
    }

    @Bean
    public ConnectionFactory jmsConnectionFactory() {
        JndiObjectFactoryBean objectFactoryBean = new JndiObjectFactoryBean();
        objectFactoryBean.setJndiTemplate(jndiTemplate());
        objectFactoryBean.setJndiName("java:jms/RemoteConnectionFactory");
        try {
            objectFactoryBean.afterPropertiesSet();
        } catch (NamingException e) {
            e.printStackTrace();
        }
        return (ConnectionFactory) objectFactoryBean.getObject();
    }

    @Bean
    public MessageConverter documentoMessageConverter() {
        Logger.info("Criando JMS Message Converter...");
        RecuperarDocumentoMessageConverter converter = new RecuperarDocumentoMessageConverter();
        return converter;
    }

}
