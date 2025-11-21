# WebSocket实时消息功能实现

## 概述
XYST平台已实现完整的WebSocket实时消息功能，支持私聊、群聊、在线状态管理等核心功能。

## 技术架构

### 核心组件
1. **WebSocketConfig** - STOMP协议配置和消息代理设置
2. **WebSocketHandshakeInterceptor** - JWT认证和连接管理
3. **WebSocketMessageController** - 实时消息处理
4. **OnlineUserService** - Redis在线用户管理

### 技术栈
- **协议**: STOMP over WebSocket
- **消息代理**: Spring SimpleBroker (可升级到RabbitMQ/ActiveMQ)
- **存储**: Redis (在线状态) + MySQL (消息持久化)
- **认证**: JWT Token

## 功能特性

### 1. 实时消息
- ✅ 私聊消息实时推送
- ✅ 群聊消息广播
- ✅ 消息持久化存储
- ✅ 消息已读状态同步

### 2. 在线状态管理
- ✅ 用户上线/下线状态
- ✅ 多会话支持
- ✅ 自动过期清理
- ✅ 最后活跃时间跟踪

### 3. 输入状态
- ✅ 正在输入状态广播
- ✅ 群聊输入状态显示

### 4. 安全认证
- ✅ JWT Token验证
- ✅ 订阅权限控制
- ✅ 用户身份验证

## API端点

### WebSocket连接
```
ws://localhost:8080/api/ws
ws://localhost:8080/api/ws (with SockJS)
```

### 消息发送
```
# 私聊消息
/app/chat/private/{receiverId}

# 群聊消息  
/app/chat/group/{chatId}

# 消息已读
/app/chat/read/{messageId}

# 输入状态
/app/chat/typing/{chatId}
```

### 消息订阅
```
# 私聊消息队列
/user/{userId}/queue/private

# 群聊消息主题
/topic/chat/{chatId}

# 输入状态主题
/topic/chat/{chatId}/typing

# 错误消息队列
/user/{userId}/queue/error
```

## 客户端集成示例

### JavaScript客户端
```javascript
// 建立WebSocket连接
const socket = new SockJS('/api/ws');
const stompClient = Stomp.over(socket);

// 连接认证
stompClient.connect({
    'Authorization': 'Bearer ' + token
}, function(frame) {
    console.log('连接成功: ' + frame);
    
    // 订阅私聊消息
    stompClient.subscribe('/user/' + userId + '/queue/private', function(message) {
        console.log('收到私聊消息: ' + message.body);
    });
    
    // 订阅群聊消息
    stompClient.subscribe('/topic/chat/' + chatId, function(message) {
        console.log('收到群聊消息: ' + message.body);
    });
});

// 发送私聊消息
function sendPrivateMessage(receiverId, content) {
    stompClient.send('/app/chat/private/' + receiverId, {}, JSON.stringify({
        content: content,
        contentType: 'text'
    }));
}

// 发送群聊消息
function sendGroupMessage(chatId, content) {
    stompClient.send('/app/chat/group/' + chatId, {}, JSON.stringify({
        content: content,
        contentType: 'text'
    }));
}
```

## 消息格式

### 发送消息格式
```json
{
    "content": "消息内容",
    "contentType": "text|image|file",
    "chatId": 123
}
```

### 接收消息格式
```json
{
    "id": 456,
    "content": "消息内容",
    "contentType": "text",
    "senderId": 789,
    "chatId": 123,
    "createdAt": "2024-01-01T12:00:00",
    "sender": {
        "id": 789,
        "username": "用户名",
        "avatar": "头像URL"
    }
}
```

### 输入状态格式
```json
{
    "userId": 789,
    "chatId": 123,
    "typing": true,
    "username": "用户名"
}
```

## 配置说明

### application.yml配置
```yaml
spring:
  redis:
    host: localhost
    port: 6379
    database: 0
    timeout: 5000ms
```

### WebSocket配置
```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    // 配置消息代理和端点
}
```

## 性能优化

### 当前配置
- SimpleBroker适用于中等规模
- Redis存储在线状态
- 消息持久化到MySQL

### 扩展建议
- 大规模群聊: 升级到RabbitMQ/ActiveMQ
- 高并发: 增加Redis集群
- 消息压缩: 启用WebSocket压缩

## 监控指标

### 在线用户统计
```java
@Autowired
private OnlineUserService onlineUserService;

// 获取在线用户数量
long onlineCount = onlineUserService.getOnlineUserCount();

// 获取在线用户列表
Set<Long> onlineUsers = onlineUserService.getOnlineUsers();
```

### 消息统计
- 消息发送成功率
- 连接建立/断开频率
- 订阅/取消订阅次数

## 故障排除

### 常见问题
1. **连接失败**: 检查JWT token有效性
2. **消息不推送**: 验证订阅权限和目标路径
3. **离线状态**: 检查Redis连接和配置

### 日志监控
```properties
# 启用WebSocket调试日志
logging.level.org.springframework.web.socket=DEBUG
logging.level.com.xystapp.config.WebSocketHandshakeInterceptor=DEBUG
```

## 安全考虑

### 认证机制
- JWT Token验证
- 会话超时控制
- 订阅权限检查

### 数据保护
- 消息内容加密传输
- 敏感信息过滤
- 访问日志记录

## 后续优化

### 计划功能
- [ ] 离线消息推送
- [ ] 消息撤回功能
- [ ] 文件传输支持
- [ ] 消息搜索功能
- [ ] 群聊管理增强

### 性能提升
- [ ] 消息压缩
- [ ] 连接池优化
- [ ] 缓存策略改进
- [ ] 负载均衡配置

## 版本信息
- 实现版本: 1.0.0
- Spring Boot版本: 2.7.18
- Java版本: 8
- 最后更新: 2024-01-01
