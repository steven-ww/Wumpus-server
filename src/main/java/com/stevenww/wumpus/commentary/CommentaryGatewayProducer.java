package com.stevenww.wumpus.commentary;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

@ApplicationScoped
public class CommentaryGatewayProducer {
    private static final Logger LOG = Logger.getLogger(CommentaryGatewayProducer.class);

    @Produces
    @ApplicationScoped
    @Named("selectedCommentaryGateway")
    CommentaryGateway selectedCommentaryGateway(
            @ConfigProperty(name = "wumpus.llm.provider", defaultValue = "fallback") String provider,
            FallbackCommentaryGateway fallbackCommentaryGateway,
            Instance<LangChainCommentaryGateway> langChainGatewayInstance
    ) {
        String normalizedProvider = provider == null ? "fallback" : provider.trim().toLowerCase();
        if ("openai".equals(normalizedProvider) && langChainGatewayInstance.isResolvable()) {
            LOG.info("Using OpenAI commentary gateway.");
            return langChainGatewayInstance.get();
        }
        if ("openai".equals(normalizedProvider)) {
            LOG.warn("OpenAI provider requested but unavailable. Falling back to deterministic gateway.");
        } else {
            LOG.infof("Using fallback commentary gateway for provider '%s'.", normalizedProvider);
        }
        return fallbackCommentaryGateway;
    }
}
