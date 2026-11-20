package com.example.library;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

class DataSourceUrlTest {
  @Test
  void turnsDatabaseUrlIntoSpringProperties() {
    MockEnvironment env = new MockEnvironment();
    env.setProperty("DATABASE_URL", "postgres://forkstage:forkstage@db:5432/app");
    new DataSourceUrl().postProcessEnvironment(env, null);
    assertEquals("jdbc:postgresql://db:5432/app", env.getProperty("spring.datasource.url"));
    assertEquals("forkstage", env.getProperty("spring.datasource.username"));
    assertEquals("forkstage", env.getProperty("spring.datasource.password"));
  }
}
