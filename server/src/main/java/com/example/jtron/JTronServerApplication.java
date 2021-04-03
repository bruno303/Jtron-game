package com.example.jtron;

import com.example.jtron.server.Server;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class JTronServerApplication {

    public static void main(final String[] args) {
        new SpringApplicationBuilder(JTronServerApplication.class)
                .headless(false)
                .web(WebApplicationType.NONE)
                .run(args);

        new Server(7887);
    }

}
