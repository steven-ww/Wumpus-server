package com.stevenww.wumpus.commentary;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

@ApplicationScoped
public class BedrockConfigurationLogger {
    private static final Logger LOG = Logger.getLogger(BedrockConfigurationLogger.class);

    private final String bedrockRegion;
    private final String bedrockModelId;

    public BedrockConfigurationLogger(
            @ConfigProperty(
                    name = "quarkus.langchain4j.bedrock.wumpus-commentary.aws.region"
            ) String bedrockRegion,
            @ConfigProperty(
                    name = "quarkus.langchain4j.bedrock.wumpus-commentary.chat-model.model-id"
            ) String bedrockModelId
    ) {
        this.bedrockRegion = bedrockRegion;
        this.bedrockModelId = bedrockModelId;
    }

    void onStart(@Observes StartupEvent ignored) {
        LOG.infof(
                "Bedrock runtime configuration loaded: region=%s, modelId=%s",
                bedrockRegion,
                bedrockModelId
        );
    }
}
