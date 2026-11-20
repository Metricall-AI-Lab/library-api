package com.example.library;

import java.net.URI;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import java.util.Map;

/**
 * Accepts DATABASE_URL in the common form postgres://user:password@host:port/db and turns it
 * into the three Spring datasource properties, so the same image runs under docker compose,
 * under Forkstage, or on any host that sets DATABASE_URL.
 */
public class DataSourceUrl implements EnvironmentPostProcessor {
  @Override
  public void postProcessEnvironment(ConfigurableEnvironment env, SpringApplication app) {
    String url = env.getProperty("DATABASE_URL");
    if (url == null || url.isBlank() || !url.startsWith("postgres")) return;
    URI u = URI.create(url);
    String[] auth = u.getUserInfo() == null ? new String[] {"", ""} : u.getUserInfo().split(":", 2);
    int port = u.getPort() == -1 ? 5432 : u.getPort();
    String jdbc = "jdbc:postgresql://" + u.getHost() + ":" + port + u.getPath();
    env.getPropertySources().addFirst(new MapPropertySource("databaseUrl", Map.of(
        "spring.datasource.url", jdbc,
        "spring.datasource.username", auth[0],
        "spring.datasource.password", auth.length > 1 ? auth[1] : "")));
  }
}
