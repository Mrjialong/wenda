package com.wenda.wenda.async;

import java.util.List;

public interface EventHandler {
    //处理事件
    void doHandle(EventModel model);
    //注册事件，绑定了什么handler
    List<EventType> getSupportEventType();
}
