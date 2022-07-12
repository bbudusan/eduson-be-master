package com.servustech.eduson.config;

import java.net.URI;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.session.MapSessionRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.servustech.eduson.features.permissions.PermissionsService;
import com.servustech.eduson.features.products.webinars.MyJwtDecoder;
import com.servustech.eduson.security.userdetails.CustomUserDetailsService;
import org.springframework.session.SessionRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.socket.config.annotation.WebMvcStompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.session.web.socket.handler.WebSocketConnectHandlerDecoratorFactory;
import org.springframework.session.web.socket.handler.WebSocketRegistryListener;
import org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;
import org.springframework.web.util.UrlPathHelper;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@EnableScheduling
public class WebSocketChatConfig
        implements WebSocketMessageBrokerConfigurer {

    private MyJwtDecoder jwtDecoder;
    private PermissionsService permissionsService;
    private CustomUserDetailsService customUserDetailsService;

    public WebSocketChatConfig(
            MyJwtDecoder jwtDecoder,
            PermissionsService permissionsService,
            CustomUserDetailsService customUserDetailsService) {
        this.jwtDecoder = jwtDecoder;
        this.permissionsService = permissionsService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Autowired
    @SuppressWarnings("rawtypes")
    private SessionRepository sessionRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(sessionRepositoryInterceptor());
    }

    @Override
    public final void registerStompEndpoints(StompEndpointRegistry registry) {
        if (registry instanceof WebMvcStompEndpointRegistry) {
            WebMvcStompEndpointRegistry mvcRegistry = (WebMvcStompEndpointRegistry) registry;
            configureStompEndpoints(new SessionStompEndpointRegistry(mvcRegistry, sessionRepositoryInterceptor()));
        }
    }

    /**
     * Register STOMP endpoints mapping each to a specific URL and (optionally)
     * enabling
     * and configuring SockJS fallback options with a
     * {@link ModifiedSessionRepositoryMessageInterceptor} automatically added as an
     * interceptor.
     * 
     * @param registry the {@link StompEndpointRegistry} which automatically has a
     *                 {@link ModifiedSessionRepositoryMessageInterceptor} added to
     *                 it.
     */

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.addDecoratorFactory(wsConnectHandlerDecoratorFactory());
    }

    @Bean
    public WebSocketRegistryListener webSocketRegistryListener() {
        return new WebSocketRegistryListener();
    }

    @Bean
    public WebSocketConnectHandlerDecoratorFactory wsConnectHandlerDecoratorFactory() {
        return new WebSocketConnectHandlerDecoratorFactory(this.eventPublisher);
    }

    @Bean
    @SuppressWarnings("unchecked")
    public ModifiedSessionRepositoryMessageInterceptor sessionRepositoryInterceptor() {
        return new ModifiedSessionRepositoryMessageInterceptor(this.sessionRepository,
                this.jwtDecoder, permissionsService, this.customUserDetailsService);
    }

    /**
     * A {@link StompEndpointRegistry} that applies {@link HandshakeInterceptor}.
     */
    static class SessionStompEndpointRegistry implements StompEndpointRegistry {

        private final WebMvcStompEndpointRegistry registry;

        private final HandshakeInterceptor interceptor;

        SessionStompEndpointRegistry(WebMvcStompEndpointRegistry registry, HandshakeInterceptor interceptor) {
            this.registry = registry;
            this.interceptor = interceptor;
        }

        @Override
        public StompWebSocketEndpointRegistration addEndpoint(String... paths) {
            StompWebSocketEndpointRegistration endpoints = this.registry.addEndpoint(paths);
            endpoints.addInterceptors(this.interceptor);
            return endpoints;
        }

        @Override
        public void setOrder(int order) {
            this.registry.setOrder(order);
        }

        @Override
        public void setUrlPathHelper(UrlPathHelper urlPathHelper) {
            this.registry.setUrlPathHelper(urlPathHelper);
        }

        @Override
        public WebMvcStompEndpointRegistry setErrorHandler(StompSubProtocolErrorHandler errorHandler) {
            return this.registry.setErrorHandler(errorHandler);
        }

    }

    // private URI allowOrigin;
    // @Inject // constructor injection not working in this class, use setter
    // injection instead
    // public void setAllowOrigin(@Value("${spring.frontend.domain2}") final URI
    // allowOrigin) {
    // this.allowOrigin = Objects.requireNonNull(allowOrigin);
    // }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/start");
        config.setApplicationDestinationPrefixes("/current");
    }

    public void configureStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/wchat")
                .setAllowedOrigins("http://localhost:4200", "http://18.197.26.150", "http://eduson.ro",
                        "https://eduson.ro") // TODO
                .withSockJS();
    }

    @Bean
    public MapSessionRepository sessionRepository() {
        return new MapSessionRepository(new ConcurrentHashMap<>());
    }

    // @Override
    // public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
    //     DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
    //     resolver.setDefaultMimeType(MimeTypeUtils.APPLICATION_JSON);
    //     MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
    //     converter.setObjectMapper(new ObjectMapper());
    //     converter.setContentTypeResolver(resolver);
    //     messageConverters.add(converter);
    //     return false;
    // }
}