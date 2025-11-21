package com.xystapp.config;

import com.xystapp.security.JwtAuthenticationFilter;
import com.xystapp.service.MessageService;
import com.xystapp.service.OnlineUserService;
import com.xystapp.utils.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;

import java.security.Principal;

/**
 * WebSocket配置类
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final Logger log = LoggerFactory.getLogger(WebSocketConfig.class);

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private MessageService messageService;

    @Autowired
    private OnlineUserService onlineUserService;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 启用简单消息代理，并设置消息代理的前缀
        config.enableSimpleBroker("/topic", "/queue");
        
        // 设置应用程序的目标前缀
        config.setApplicationDestinationPrefixes("/app");
        
        // 设置用户目标前缀
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 注册STOMP端点
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
        
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");
    }

    @Override
    public void configureClientInboundChannel(org.springframework.messaging.simp.config.ChannelRegistration registration) {
        registration.interceptors(new WebSocketHandshakeInterceptor(jwtUtils, messageService, onlineUserService));
    }

    @Override
    public void configureClientOutboundChannel(org.springframework.messaging.simp.config.ChannelRegistration registration) {
        registration.interceptors(new WebSocketHandshakeInterceptor(jwtUtils, messageService, onlineUserService));
    }

    /**
     * WebSocket连接和断开事件处理
     * TODO: 暂时注释掉，可能需要根据Spring版本调整
     */
    /*
    @Override
    public void configureWebSocketTransport(org.springframework.web.socket.config.annotation.WebSocketTransportRegistration registration) {
        registration.addDecoratorFactory(new WebSocketHandlerDecoratorFactory() {
            @Override
            public WebSocketHandler decorate(WebSocketHandler handler) {
                return new WebSocketHandlerDecorator(handler) {
                    @Override
                    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                        log.info("WebSocket连接建立: sessionId={}, remoteAddress={}", 
                                session.getId(), session.getRemoteAddress());
                        
                        // 获取用户信息并标记为在线
                        Principal user = session.getPrincipal();
                        if (user != null) {
                            log.info("用户上线: userId={}", user.getName());
                            // TODO: 将用户标记为在线状态存储到Redis
                        }
                        
                        super.afterConnectionEstablished(session);
                    }

                    @Override
                    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
                        log.info("WebSocket连接关闭: sessionId={}, status={}", 
                                session.getId(), closeStatus);
                        
                        // 获取用户信息并标记为离线
                        Principal user = session.getPrincipal();
                        if (user != null) {
                            log.info("用户下线: userId={}", user.getName());
                            // TODO: 将用户标记为离线状态从Redis移除
                        }
                        
                        super.afterConnectionClosed(session, closeStatus);
                    }
                };
            }
        });
        super.configureWebSocketTransport(registration);
    }
    */
}
