
package org.example.springboot.AiService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DeepSeek AI服务类（支持真正的流式输出和Function Calling）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeepSeekService {

    private final org.example.springboot.ai.DeepSeekConfig deepSeekConfig;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final WebClient webClient = WebClient.builder().build();
    
    private final Map<String, ToolExecutor> toolExecutors = new ConcurrentHashMap<>();
    private final Map<String, ObjectNode> toolDefinitions = new ConcurrentHashMap();

    public interface ToolExecutor {
        Object execute(Map<String, Object> arguments);
    }

    public void registerTool(String name, String description, ObjectNode parameters, ToolExecutor executor) {
        ObjectNode toolDef = objectMapper.createObjectNode();
        toolDef.put("type", "function");
        ObjectNode function = objectMapper.createObjectNode();
        function.put("name", name);
        function.put("description", description);
        function.set("parameters", parameters);
        toolDef.set("function", function);
        
        toolDefinitions.put(name, toolDef);
        toolExecutors.put(name, executor);
        log.info("注册工具: {}", name);
    }

    public void registerToolFromMethod(Object bean, Method method, String description, String[] requiredParams) {
        String toolName = method.getName();
        
        ObjectNode parameters = objectMapper.createObjectNode();
        parameters.put("type", "object");
        ObjectNode properties = objectMapper.createObjectNode();
        ArrayNode required = objectMapper.createArrayNode();
        Set<String> requiredSet = new HashSet<>(Arrays.asList(requiredParams));
        
        for (Parameter param : method.getParameters()) {
            String paramName = param.getName();
            Class<?> paramType = param.getType();
            
            ObjectNode paramDef = objectMapper.createObjectNode();
            paramDef.put("type", getTypeString(paramType));
            paramDef.put("description", paramName);
            
            properties.set(paramName, paramDef);
            if (requiredSet.contains(paramName)) {
                required.add(paramName);
            }
        }
        
        parameters.set("properties", properties);
        parameters.set("required", required);
        
        registerTool(toolName, description, parameters, args -> {
            try {
                Object[] paramValues = new Object[method.getParameterCount()];
                Parameter[] params = method.getParameters();
                for (int i = 0; i < params.length; i++) {
                    String paramName = params[i].getName();
                    Object value = args.get(paramName);
                    paramValues[i] = convertValue(value, params[i].getType());
                }
                return method.invoke(bean, paramValues);
            } catch (Exception e) {
                log.error("执行工具失败: {}", e.getMessage(), e);
                return "工具执行失败: " + e.getMessage();
            }
        });
    }

    private String getTypeString(Class<?> type) {
        if (type == String.class) return "string";
        if (type == Integer.class || type == int.class) return "integer";
        if (type == Long.class || type == long.class) return "integer";
        if (type == Double.class || type == double.class) return "number";
        if (type == Float.class || type == float.class) return "number";
        if (type == Boolean.class || type == boolean.class) return "boolean";
        return "string";
    }

    private Object convertValue(Object value, Class<?> targetType) {
        if (value == null) return null;
        
        String valueStr = value.toString();
        if (valueStr.isEmpty()) return null;
        
        if (targetType == String.class) return valueStr;
        if (targetType == Integer.class || targetType == int.class) {
            if (value instanceof Number) return ((Number) value).intValue();
            return Integer.parseInt(valueStr);
        }
        if (targetType == Long.class || targetType == long.class) {
            if (value instanceof Number) return ((Number) value).longValue();
            return Long.parseLong(valueStr);
        }
        if (targetType == Double.class || targetType == double.class) {
            if (value instanceof Number) return ((Number) value).doubleValue();
            return Double.parseDouble(valueStr);
        }
        if (targetType == Float.class || targetType == float.class) {
            if (value instanceof Number) return ((Number) value).floatValue();
            return Float.parseFloat(valueStr);
        }
        if (targetType == Boolean.class || targetType == boolean.class) {
            if (value instanceof Boolean) return value;
            return Boolean.parseBoolean(valueStr);
        }
        if (targetType == BigDecimal.class) {
            if (value instanceof Number) return new BigDecimal(value.toString());
            return new BigDecimal(valueStr);
        }
        return value;
    }

    /**
     * 普通非流式调用（保留原有功能）
     */
    public String sendMessage(String number, String systemPrompt, String userMessage) {
        try {
            log.info("发送消息到DeepSeek API（非流式）");

            // 构建请求体
            Map<String, Object> requestBody = buildRequestBody(systemPrompt, userMessage, false);

            // 发送请求
            String response = webClient.post()
                    .uri(deepSeekConfig.getBaseUrl() + "/v1/chat/completions")
                    .headers(this::setRequestHeaders)
                    .body(Mono.just(requestBody), Map.class)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(); // 阻塞获取结果（非流式场景）

            // 解析响应
            JsonNode rootNode = objectMapper.readTree(response);
            String aiResponse = rootNode.path("choices").get(0).path("message").path("content").asText();

            log.info("DeepSeek API返回成功，响应长度: {}", aiResponse.length());
            return aiResponse;

        } catch (Exception e) {
            log.error("调用DeepSeek API失败: {}", e.getMessage(), e);
            throw new RuntimeException("DeepSeek AI服务调用失败: " + e.getMessage(), e);
        }
    }

    /**
     * 真正的流式调用（核心改造方法）
     */
    public Flux<String> sendMessageStream(String userId, String systemPrompt, String userMessage) {
        log.info("发送消息到DeepSeek API（流式模式）");

        // 构建流式请求体（stream=true）
        Map<String, Object> requestBody = buildRequestBody(systemPrompt, userMessage, true);

        // 发送流式请求并处理响应
        return webClient.post()
                .uri(deepSeekConfig.getBaseUrl() + "/v1/chat/completions")
                .headers(this::setRequestHeaders)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(requestBody), Map.class)
                .retrieve()
                // 将响应体按行分割（SSE分块是每行一个data:xxx）
                .bodyToFlux(String.class)
                // 过滤空行和结束标记
                .filter(line -> line != null && !line.isEmpty() && !line.equals("data: [DONE]"))
                // 解析每行的SSE数据
                .map(this::parseSseDataLine)
                // 过滤空内容（避免返回空字符串）
                .filter(content -> content != null && !content.isEmpty())
                // 异常处理
                .doOnError(e -> log.error("流式调用DeepSeek API失败: {}", e.getMessage(), e))
                .onErrorResume(e -> Flux.just("流式响应出错: " + e.getMessage()));

    }

    /**
     * 构建请求体（复用非流式/流式逻辑）
     */
    private Map<String, Object> buildRequestBody(String systemPrompt, String userMessage, boolean stream) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", deepSeekConfig.getModel());
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 2000);
        requestBody.put("stream", stream);

        List<Map<String, String>> messages = new ArrayList<>();
        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            Map<String, String> systemMsg = new HashMap<>();
            systemMsg.put("role", "system");
            systemMsg.put("content", systemPrompt);
            messages.add(systemMsg);
        }
        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        messages.add(userMsg);

        requestBody.put("messages", messages);
        return requestBody;
    }

    /**
     * 构建请求体（支持工具调用）
     */
    private Map<String, Object> buildRequestBodyWithTools(String systemPrompt, String userMessage, boolean stream, List<Map<String, Object>> tools) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", deepSeekConfig.getModel());
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 2000);
        requestBody.put("stream", stream);
        
        if (tools != null && !tools.isEmpty()) {
            requestBody.put("tools", tools);
        }

        List<Map<String, String>> messages = new ArrayList<>();
        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            Map<String, String> systemMsg = new HashMap<>();
            systemMsg.put("role", "system");
            systemMsg.put("content", systemPrompt);
            messages.add(systemMsg);
        }
        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        messages.add(userMsg);

        requestBody.put("messages", messages);
        return requestBody;
    }

    /**
     * 设置请求头
     */
    private void setRequestHeaders(HttpHeaders headers) {
        headers.set("Authorization", "Bearer " + deepSeekConfig.getApiKey());
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    /**
     * 解析SSE单行数据（格式：data: {"id":"xxx","choices":[{"delta":{"content":"xxx"}}]}
     */
    private String parseSseDataLine(String line) {
        try {
            String jsonStr = line.startsWith("data: ") ? line.substring(6) : line;
            if (jsonStr.isEmpty() || jsonStr.equals("[DONE]")) {
                return "";
            }
            JsonNode rootNode = objectMapper.readTree(jsonStr);
            JsonNode contentNode = rootNode.path("choices")
                    .get(0)
                    .path("delta")
                    .path("content");
            return contentNode.isMissingNode() ? "" : contentNode.asText();
        } catch (Exception e) {
            log.warn("解析SSE数据失败: {}, 原始数据: {}", e.getMessage(), line);
            return "";
        }
    }

    /**
     * 发送消息并支持工具调用（非流式）
     */
    public String sendMessageWithTools(String systemPrompt, String userMessage, List<Map<String, Object>> tools) {
        try {
            log.info("发送消息到DeepSeek API（支持工具调用）");

            Map<String, Object> requestBody = buildRequestBodyWithTools(systemPrompt, userMessage, false, tools);

            String response = webClient.post()
                    .uri(deepSeekConfig.getBaseUrl() + "/v1/chat/completions")
                    .headers(this::setRequestHeaders)
                    .body(Mono.just(requestBody), Map.class)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode firstChoice = rootNode.path("choices").get(0);
            JsonNode message = firstChoice.path("message");
            
            JsonNode toolCalls = message.path("tool_calls");
            if (toolCalls.isArray() && toolCalls.size() > 0) {
                return handleToolCalls(rootNode, systemPrompt, userMessage, tools);
            }
            
            return message.path("content").asText();

        } catch (Exception e) {
            log.error("调用DeepSeek API失败: {}", e.getMessage(), e);
            throw new RuntimeException("DeepSeek AI服务调用失败: " + e.getMessage(), e);
        }
    }

    /**
     * 处理工具调用
     */
    private String handleToolCalls(JsonNode rootNode, String systemPrompt, String userMessage, List<Map<String, Object>> tools) {
        try {
            JsonNode firstChoice = rootNode.path("choices").get(0);
            JsonNode message = firstChoice.path("message");
            JsonNode toolCalls = message.path("tool_calls");
            
            List<Map<String, Object>> messages = new ArrayList<>();
            if (systemPrompt != null && !systemPrompt.isEmpty()) {
                Map<String, Object> systemMsg = new HashMap<>();
                systemMsg.put("role", "system");
                systemMsg.put("content", systemPrompt);
                messages.add(systemMsg);
            }
            
            Map<String, Object> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage);
            messages.add(userMsg);
            
            Map<String, Object> assistantMsg = new HashMap<>();
            assistantMsg.put("role", "assistant");
            assistantMsg.put("content", "");
            assistantMsg.put("tool_calls", objectMapper.treeToValue(toolCalls, Object.class));
            messages.add(assistantMsg);
            
            for (JsonNode toolCall : toolCalls) {
                String toolName = toolCall.path("function").path("name").asText();
                String toolArgsStr = toolCall.path("function").path("arguments").asText();
                
                log.info("执行工具调用: {} with args: {}", toolName, toolArgsStr);
                
                Map<String, Object> toolArgs = objectMapper.readValue(toolArgsStr, Map.class);
                ToolExecutor executor = toolExecutors.get(toolName);
                
                if (executor != null) {
                    Object result = executor.execute(toolArgs);
                    
                    Map<String, Object> toolResponse = new HashMap<>();
                    toolResponse.put("role", "tool");
                    toolResponse.put("tool_call_id", toolCall.path("id").asText());
                    toolResponse.put("content", result != null ? result.toString() : "");
                    messages.add(toolResponse);
                } else {
                    log.warn("未找到工具执行器: {}", toolName);
                }
            }
            
            Map<String, Object> finalRequestBody = new HashMap<>();
            finalRequestBody.put("model", deepSeekConfig.getModel());
            finalRequestBody.put("temperature", 0.7);
            finalRequestBody.put("max_tokens", 2000);
            finalRequestBody.put("stream", false);
            finalRequestBody.put("messages", messages);
            
            String finalResponse = webClient.post()
                    .uri(deepSeekConfig.getBaseUrl() + "/v1/chat/completions")
                    .headers(this::setRequestHeaders)
                    .body(Mono.just(finalRequestBody), Map.class)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            JsonNode finalRoot = objectMapper.readTree(finalResponse);
            return finalRoot.path("choices").get(0).path("message").path("content").asText();
            
        } catch (Exception e) {
            log.error("处理工具调用失败: {}", e.getMessage(), e);
            throw new RuntimeException("工具调用处理失败: " + e.getMessage(), e);
        }
    }

    public List<Map<String, Object>> getToolDefinitions() {
        List<Map<String, Object>> defs = new ArrayList<>();
        for (ObjectNode def : toolDefinitions.values()) {
            try {
                defs.add(objectMapper.treeToValue(def, Map.class));
            } catch (Exception e) {
                    log.error("转换工具定义失败: {}", e.getMessage());
            }
        }
        return defs;
    }
}
