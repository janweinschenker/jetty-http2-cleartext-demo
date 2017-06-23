package de.holisticon.http2clearcase;

import org.eclipse.jetty.http2.server.HTTP2CServerConnectionFactory;
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

import java.util.Arrays;

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

      /**
       * Get the existing {@link ServerConnector} from Jetty and add an h2c connector.
       * @param jetty
       */
      private void customizeJetty(JettyEmbeddedServletContainerFactory jetty) {
        jetty.addServerCustomizers(server -> {
          ServerConnector sc = Arrays
              .stream(server.getConnectors()) // get all connectors (there is effectively only one)
              .filter(connector -> connector instanceof ServerConnector) // Filter
              .map(connector ->
                       (ServerConnector) connector) // cast to ServerConnector
              .findFirst()
              .get();
          // Add h2c ConnectionFactory
          sc.addConnectionFactory(new HTTP2CServerConnectionFactory(new HttpConfiguration()));
        });
      }
    };
  }

  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
  }
}
