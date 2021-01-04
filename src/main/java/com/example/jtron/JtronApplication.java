package com.example.jtron;

import java.awt.EventQueue;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import com.example.jtron.ui.MainWindow;

@SpringBootApplication
public class JtronApplication {

	public static void main(final String[] args) {
		final ConfigurableApplicationContext ctx = new SpringApplicationBuilder(JtronApplication.class).headless(false)
				.web(WebApplicationType.NONE).run(args);

		EventQueue.invokeLater(() -> ctx.getBean(MainWindow.class).setVisible(true));
	}

}
