package com._7aske.grain.fertilizer.web.server.tomcat;

import com._7aske.grain.core.configuration.Configuration;
import com._7aske.grain.core.configuration.ConfigurationKey;
import com._7aske.grain.core.context.ApplicationContext;
import com._7aske.grain.exception.GrainInitializationException;
import com._7aske.grain.logging.Logger;
import com._7aske.grain.logging.LoggerFactory;
import com._7aske.grain.security.authentication.provider.HttpRequestAuthenticationProviderStrategy;
import com._7aske.grain.web.controller.exceptionhandler.ExceptionControllerHandler;
import com._7aske.grain.web.requesthandler.handler.runner.HandlerRunner;
import jakarta.servlet.MultipartConfigElement;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com._7aske.grain.web.http.session.SessionConstants.SESSION_COOKIE_NAME;

/**
 * Main Embedded Tomcat configuration. Creates a {@link DispatcherServlet} that
 * acts as an entry point for all requests and adds it to the default {@link Context}.
 */
public class TomcatConfigurer {
    private final Configuration configuration;
    private final ApplicationContext context;
    private final Logger logger = LoggerFactory.getLogger(TomcatConfigurer.class);
    private final Tomcat tomcat;

    public TomcatConfigurer(ApplicationContext context, Configuration configuration) {
        this.context = context;
        this.configuration = configuration;
        this.tomcat = new Tomcat();
    }

    public void configure() {
        HandlerRunner handlerRunner = context.getGrain(HandlerRunner.class);
        HttpRequestAuthenticationProviderStrategy provider = context.getGrain(HttpRequestAuthenticationProviderStrategy.class);
        ExceptionControllerHandler errorHandler = context.getGrain(ExceptionControllerHandler.class);

        Path root = createTempDir("grain-tomcat");
        tomcat.setBaseDir(root.toString());
        tomcat.setPort(getPort());

        String contextPath = "";
        String docBase = root.toFile().getAbsolutePath();

        Context defaultContext = tomcat.addContext(contextPath, docBase);
        defaultContext.setSessionCookieName(SESSION_COOKIE_NAME);

        // Configure default servlet
        DispatcherServlet servlet = new DispatcherServlet(handlerRunner, errorHandler, provider);
        Tomcat.addServlet(defaultContext, servlet.getServletName(), servlet)
                .setMultipartConfigElement(new MultipartConfigElement(root.toAbsolutePath().toString()));
        logger.info("Mapping {} {} to {}", servlet.getMethods(), servlet.getPath(), servlet.getServletName());
        defaultContext.addServletMappingDecoded(servlet.getPath(), DispatcherServlet.SERVLET_NAME);
    }

    protected Path createTempDir(String prefix) {
        try {
            File tempDir = File.createTempFile(prefix + ".", "." + getPort());
            tempDir.delete();
            tempDir.mkdir();
            tempDir.deleteOnExit();
            return tempDir.toPath();
        } catch (IOException ex) {
            throw new GrainInitializationException(
                    "Unable to create tempDir. java.io.tmpdir is set to "
                    + System.getProperty("java.io.tmpdir"),
                    ex);
        }
    }

    public int getPort() {
        return configuration.getInt(ConfigurationKey.SERVER_PORT);
    }

    public Tomcat getServer() {
        return tomcat;
    }
}
