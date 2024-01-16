package com._7aske.grain.fertilizer;

import com._7aske.grain.core.component.Grain;
import com._7aske.grain.core.component.Primary;
import com._7aske.grain.core.configuration.Configuration;
import com._7aske.grain.core.configuration.GrainFertilizer;
import com._7aske.grain.core.context.ApplicationContext;
import com._7aske.grain.fertilizer.web.server.tomcat.TomcatServer;
import com._7aske.grain.logging.Logger;
import com._7aske.grain.logging.LoggerFactory;
import com._7aske.grain.web.server.Server;

/**
 * Autoconfiguration to allow usage of Tomcat Embedded server as the Grain
 * application server.
 */
@GrainFertilizer
public class GrainTomcatFertilizer {
    private final Logger logger = LoggerFactory.getLogger(GrainTomcatFertilizer.class);

    @Grain
    @Primary
    public Server tomcatServer(Configuration configuration, ApplicationContext context) {
        logger.info("Creating TomcatServer");
        return new TomcatServer(configuration, context);
    }
}
