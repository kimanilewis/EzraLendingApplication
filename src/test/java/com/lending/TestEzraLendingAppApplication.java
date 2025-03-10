package com.lending;

import org.springframework.boot.SpringApplication;

public class TestEzraLendingAppApplication {

  public static void main(String[] args) {
    SpringApplication.from(EzraLendingAppApplication::main).with(TestcontainersConfiguration.class)
        .run(args);
  }

}
