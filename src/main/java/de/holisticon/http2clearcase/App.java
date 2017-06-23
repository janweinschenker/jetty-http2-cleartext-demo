package de.holisticon.http2clearcase;

import org.eclipse.jetty.http2.server.HTTP2CServerConnectionFactory;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.ServerConnector;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class App {

  @Bean
  public EmbeddedServletContainerCustomizer customizer() {
    return container -> {
      if (container instanceof JettyEmbeddedServletContainerFactory) {
        ((JettyEmbeddedServletContainerFactory) container)
            .addServerCustomizers(server -> {
                                    ServerConnector sc = (ServerConnector) server.getConnectors()[0];
                                    sc.addConnectionFactory(new HTTP2CServerConnectionFactory(new HttpConfiguration()));
                                  }
            );
      }
    };
  }

  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
  }
}
