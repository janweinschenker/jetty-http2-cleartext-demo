package de.holisticon.http2clearcase;

import org.eclipse.jetty.http2.server.HTTP2CServerConnectionFactory;
import org.eclipse.jetty.server.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.jetty.JettyServerCustomizer;
import org.springframework.context.annotation.Bean;

/**
 * Hello world!
 */
@SpringBootApplication
public class App {

  private static final Logger LOG = LoggerFactory.getLogger(App.class);

  //@Bean
  public JettyEmbeddedServletContainerFactory jettyEmbeddedServletContainerFactory() {
    final JettyEmbeddedServletContainerFactory factory = new JettyEmbeddedServletContainerFactory();


    factory.addServerCustomizers((JettyServerCustomizer) server -> {
      // Common HTTP configuration.
      HttpConfiguration config = new HttpConfiguration();

      // HTTP/1.1 support.
      HttpConnectionFactory connectionFactory = new HttpConnectionFactory(config);

      // HTTP/2 cleartext support.
      HTTP2CServerConnectionFactory http2c = new HTTP2CServerConnectionFactory(config);

      ServerConnector connector = new ServerConnector(server, connectionFactory, http2c);
      server.addConnector(connector);
      System.out.println("Hallo");

    });
    return factory;
  }

  @Bean
  public EmbeddedServletContainerCustomizer customizer() {
    return new EmbeddedServletContainerCustomizer() {

      @Override
      public void customize(ConfigurableEmbeddedServletContainer container) {
        if (container instanceof JettyEmbeddedServletContainerFactory) {
          customizeJetty((JettyEmbeddedServletContainerFactory) container);
        }
      }

      private void customizeJetty(JettyEmbeddedServletContainerFactory jetty) {
        jetty.addServerCustomizers(new JettyServerCustomizer() {

          @Override
          public void customize(Server server) {

            for (Connector connector : server.getConnectors()) {
              if (connector instanceof ServerConnector) {
                HttpConnectionFactory connectionFactory = ((ServerConnector) connector)
                    .getConnectionFactory(HttpConnectionFactory.class);
                // HTTP/2 cleartext support.
                HttpConfiguration config = new HttpConfiguration();
                HTTP2CServerConnectionFactory http2c = new HTTP2CServerConnectionFactory(config);
                ((ServerConnector) connector).addConnectionFactory(http2c);
              }
            }
          }
        });
      }
    };

  }

  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
  }
}
