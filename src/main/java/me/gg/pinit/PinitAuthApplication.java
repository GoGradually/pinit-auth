package me.gg.pinit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.resilience.annotation.EnableResilientMethods;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableResilientMethods
public class PinitAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(PinitAuthApplication.class, args);
    }

}
