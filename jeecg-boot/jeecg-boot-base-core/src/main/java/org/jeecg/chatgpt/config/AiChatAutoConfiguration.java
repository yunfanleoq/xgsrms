package org.jeecg.chatgpt.config;

import com.zhipu.oapi.ClientV4;
import org.jeecg.chatgpt.prop.AiChatProperties;
import org.jeecg.chatgpt.service.AiChatService;
import org.jeecg.chatgpt.service.impl.ChatZhipuService;
import org.jeecg.chatgpt.service.impl.DefaultAiChatService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * AI聊天工具自动装配
 *
 * @author chenrui
 * @date 2024/1/12 15:45
 */
@Configuration
@EnableConfigurationProperties(AiChatProperties.class)
public class AiChatAutoConfiguration {

    //update-begin---author:chenrui ---date:20240126  for：新增streamClientBean------------

    /**
     * openAI客户端
     *
     * @param aiChatProperties
     * @return
     * @author chenrui
     * @date 2024/1/12 17:09
     */
    @Bean
    @ConditionalOnProperty(prefix = AiChatProperties.PREFIX, name = "enabled", havingValue = "true")
    public ClientV4 openAiClient(AiChatProperties aiChatProperties) {
        ClientV4.Builder builder = new ClientV4.Builder(aiChatProperties.getApiKey());
        builder.enableTokenCache();
        builder.networkConfig(300, 100, 100, 100, TimeUnit.SECONDS);
        builder.connectionPool(new okhttp3.ConnectionPool(10, 1, TimeUnit.SECONDS));
        ClientV4 client = builder.build();
        return client;
    }

    //update-end---author:chenrui ---date:20240126  for：新增streamClientBean------------

    /**
     * ChatGpt聊天Service
     *
     * @param openAiClient
     * @return
     * @author chenrui
     * @date 2024/1/12 17:09
     */
    @Bean
    @ConditionalOnBean(ClientV4.class)
    public AiChatService chatGptAiChatService(ClientV4 openAiClient) {
        return new ChatZhipuService(openAiClient);
    }

    /**
     * 默认的AI聊天Service
     *
     * @return
     * @author chenrui
     * @date 2024/1/12 17:08
     */
    @Bean
    @ConditionalOnMissingBean(AiChatService.class)
    public AiChatService defaultAiChatService() {
        return new DefaultAiChatService();
    }

}
