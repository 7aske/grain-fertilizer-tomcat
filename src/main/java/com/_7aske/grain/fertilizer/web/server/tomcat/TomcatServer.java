package com._7aske.grain.fertilizer.web.server.tomcat;

import com._7aske.grain.core.configuration.Configuration;
import com._7aske.grain.core.context.ApplicationContext;
import com._7aske.grain.exception.AppInitializationException;
import com._7aske.grain.web.server.Server;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

/**
 * Tomcat implementation of a Grain {@link Server}.
 */
public class TomcatServer extends Server {

    public TomcatServer(Configuration configuration, ApplicationContext context) {
        super(configuration, context);
    }

    public void run() {
        TomcatConfigurer servletConfigurer = new TomcatConfigurer(context, configuration);
        servletConfigurer.configure();

        Tomcat tomcat = servletConfigurer.getServer();

        try {
            tomcat.start();
            // Starts the default connector
            tomcat.getConnector();
        } catch (LifecycleException e) {
            throw new AppInitializationException("Failed to initialize GrainApp", e);
        }
        tomcat.getServer().await();
    }
}
