package com.example.jtron.client;

import com.example.jtron.client.ui.MainWindow;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.awt.*;

@SpringBootApplication
public class JTronClientApplication {
    public static void main(final String[] args) {
        final ConfigurableApplicationContext ctx = new SpringApplicationBuilder(JTronClientApplication.class)
                .headless(false)
                .web(WebApplicationType.NONE)
                .run(args);

        EventQueue.invokeLater(() -> ctx.getBean(MainWindow.class).setVisible(true));
    }
}
