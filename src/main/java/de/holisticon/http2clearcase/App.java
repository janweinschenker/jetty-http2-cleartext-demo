package de.holisticon.http2clearcase;

import org.eclipse.jetty.http2.server.HTTP2CServerConnectionFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.ServerConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;

/**
 * Hello world!
 */
@SpringBootApplication
public class App {

  private static final Logger LOG = LoggerFactory.getLogger(App.class);

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
        jetty.addServerCustomizers(server -> {
          for (Connector connector : server.getConnectors()) {
            if (connector instanceof ServerConnector) {
              // HTTP/2 cleartext support.
              HttpConfiguration config = new HttpConfiguration();
              HTTP2CServerConnectionFactory http2c = new HTTP2CServerConnectionFactory(config);
              ((ServerConnector) connector).addConnectionFactory(http2c);
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
