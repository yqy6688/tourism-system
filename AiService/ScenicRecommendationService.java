package org.example.springboot.AiService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.service.AiChatSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * AI景点推荐服务
 * 
 * 功能：提供智能景点推荐的AI服务
 * 
 * 使用场景：
 * - 用户在首页输入旅游需求
 * - AI理解需求并推荐合适的景点
 * - 支持多轮对话，保持上下文
 * 
 * @author AI Assistant
 */
@Slf4j
@Service
public class ScenicRecommendationService {

    @Autowired
    private DeepSeekService deepSeekService;

    @Resource
    private AiChatSessionService aiChatSessionService;

    @Resource
    private Tools tools;

    @Resource
    private org.example.springboot.service.ScenicSpotService scenicSpotService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        registerTools();
    }

    private void registerTools() {
        try {
            Method searchMethod = Tools.class.getMethod("searchScenicSpots", Integer.class, String.class, BigDecimal.class, BigDecimal.class);
            deepSeekService.registerToolFromMethod(tools, searchMethod, 
                "分页搜索景点数据库，每次返回最多20条景点数据。支持按地区、价格区间筛选。可以多次调用此工具，通过改变page参数来查询不同页的数据。",
                new String[]{"page"});

            Method getDetailMethod = Tools.class.getMethod("getScenicSpotDetail", Long.class);
            deepSeekService.registerToolFromMethod(tools, getDetailMethod,
                "根据景点ID获取景点的完整详细信息。当用户询问某个具体景点的详情、想了解更多信息时使用此工具。",
                new String[]{"scenicSpotId"});

            Method getCategoriesMethod = Tools.class.getMethod("getAllCategories");
            deepSeekService.registerToolFromMethod(tools, getCategoriesMethod,
                "获取系统中所有可用的景点分类列表。当用户询问'有哪些类型的景点'、'景点分类'、或需要了解可选分类时使用此工具。",
                new String[]{});

            log.info("DeepSeek工具注册完成");
        } catch (Exception e) {
            log.error("注册工具失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 获取结构化的景点推荐列表
     * 
     * 特点：
     * - 返回结构化数据，包含景点ID和推荐理由
     * - 不返回对话式文本
     * - 适合前端直接展示
     * 
     * @param userMessage 用户需求描述
     * @return 结构化的推荐列表
     */
    public StructOutPut.RecommendationList getRecommendations(String userMessage) {
        log.info("=== 开始AI景点推荐 ===");
        log.info("用户需求: {}", userMessage);
        
        try {
            String processedMessage = preprocessUserMessage(userMessage);
            log.info("预处理后消息: {}", processedMessage);
            
            long totalScenicSpots = scenicSpotService.count();
            log.info("数据库景点总数: {}", totalScenicSpots);
            
            String systemPrompt = PromptManage.getScenicRecommendationPrompt(totalScenicSpots);
            
            List<Map<String, Object>> toolDefs = deepSeekService.getToolDefinitions();
            log.info("已注册工具数量: {}", toolDefs.size());
            
            String jsonResponse = deepSeekService.sendMessageWithTools(systemPrompt, processedMessage, toolDefs);
            log.info("DeepSeek返回原始响应: {}", jsonResponse);
            
            StructOutPut.RecommendationList result = parseRecommendationList(jsonResponse);
            
            log.info("AI返回推荐数量: {}", 
                result.recommendations() != null ? result.recommendations().size() : 0);
            
            if (result.recommendations() != null) {
                result.recommendations().forEach(r -> 
                    log.info("推荐景点ID: {}, 理由: {}", r.scenicSpotId(), r.reason())
                );
            }
            
            log.info("=== AI景点推荐完成 ===");
            return result;
            
        } catch (Exception e) {
            log.error("=== AI景点推荐失败 ===");
            log.error("错误信息: {}", e.getMessage(), e);
            throw new RuntimeException("AI推荐服务调用失败: " + e.getMessage(), e);
        }
    }

    private StructOutPut.RecommendationList parseRecommendationList(String jsonResponse) {
        try {
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode recommendationsNode = rootNode.path("recommendations");
            
            if (recommendationsNode.isMissingNode() || !recommendationsNode.isArray()) {
                log.warn("响应中未找到recommendations数组，尝试提取JSON代码块");
                return extractJsonFromMarkdown(jsonResponse);
            }
            
            List<StructOutPut.SimpleRecommendation> recommendations = new ArrayList<>();
            for (JsonNode item : recommendationsNode) {
                Long scenicSpotId = item.path("scenicSpotId").asLong();
                String reason = item.path("reason").asText();
                recommendations.add(new StructOutPut.SimpleRecommendation(scenicSpotId, reason));
            }
            
            return new StructOutPut.RecommendationList(recommendations);
            
        } catch (Exception e) {
            log.error("解析推荐列表失败: {}", e.getMessage(), e);
            return extractJsonFromMarkdown(jsonResponse);
        }
    }

    private StructOutPut.RecommendationList extractJsonFromMarkdown(String text) {
        try {
            String cleanedText = text;
            
            if (text.contains("```json")) {
                int codeBlockStart = text.indexOf("```json") + 7;
                int codeBlockEnd = text.indexOf("```", codeBlockStart);
                if (codeBlockEnd > codeBlockStart) {
                    cleanedText = text.substring(codeBlockStart, codeBlockEnd).trim();
                }
            } else if (text.contains("```")) {
                int codeBlockStart = text.indexOf("```") + 3;
                int codeBlockEnd = text.indexOf("```", codeBlockStart);
                if (codeBlockEnd > codeBlockStart) {
                    cleanedText = text.substring(codeBlockStart, codeBlockEnd).trim();
                }
            }
            
            int jsonStart = cleanedText.indexOf("{");
            int jsonEnd = cleanedText.lastIndexOf("}");
            
            if (jsonStart >= 0 && jsonEnd > jsonStart) {
                String jsonStr = cleanedText.substring(jsonStart, jsonEnd + 1);
                JsonNode rootNode = objectMapper.readTree(jsonStr);
                JsonNode recommendationsNode = rootNode.path("recommendations");
                
                if (recommendationsNode.isArray()) {
                    List<StructOutPut.SimpleRecommendation> recommendations = new ArrayList<>();
                    for (JsonNode item : recommendationsNode) {
                        Long scenicSpotId = item.path("scenicSpotId").asLong();
                        String reason = item.path("reason").asText();
                        recommendations.add(new StructOutPut.SimpleRecommendation(scenicSpotId, reason));
                    }
                    return new StructOutPut.RecommendationList(recommendations);
                }
            }
            
            log.warn("无法从响应中提取有效的JSON数据");
            return new StructOutPut.RecommendationList(new ArrayList<>());
            
        } catch (Exception e) {
            log.error("提取JSON失败: {}", e.getMessage(), e);
            return new StructOutPut.RecommendationList(new ArrayList<>());
        }
    }

    /**
     * 预处理用户消息
     * 
     * 用途：
     * - 转换相对时间表达（如"今天"、"周末"转为具体日期）
     * - 清理特殊字符
     * - 补充上下文信息
     * 
     * @param userMessage 原始用户消息
     * @return 处理后的消息
     */
    private String preprocessUserMessage(String userMessage) {
        // 这里可以添加日期转换、关键词规范化等预处理逻辑
        // 目前暂时直接返回原消息
        return userMessage;
    }

    public void clearSessionMemory(String sessionId) {
        log.info("清除AI景点推荐会话记忆，会话ID: {}", sessionId);
        try {
            aiChatSessionService.deleteSession(sessionId);
            log.info("会话记忆清除成功，会话ID: {}", sessionId);
        } catch (Exception e) {
            log.error("清除会话记忆失败: {}", e.getMessage(), e);
        }
    }

    public java.util.List<StructOutPut.ChatMessage> getSessionHistory(String sessionId) {
        log.info("获取AI景点推荐会话历史，会话ID: {}", sessionId);
        try {
            return aiChatSessionService.getSessionMessages(sessionId);
        } catch (Exception e) {
            log.error("获取会话历史失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取会话历史失败", e);
        }
    }
}
