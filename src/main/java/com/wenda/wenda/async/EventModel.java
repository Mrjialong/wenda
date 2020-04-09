package com.wenda.wenda.async;


import java.util.HashMap;
import java.util.Map;
//事件发生的现场

public class EventModel {
    private EventType type;//事件类型（点赞）
    private int actorId;//触发者（谁点赞）
    private int EntityType;//触发事件的类型（给什么东西点赞）
    private int entityId;//触发事件的ID（点赞东西的ID）
    private int entityOwnerId;//响应者

    public EventModel(EventType type){
        this.type = type;
    }
    public EventModel(){

    }
    private Map<String,String> exts = new HashMap<String, String>();

    public EventModel setExt(String key,String value){
        exts.put(key,value);
        return this;
    }

    public String getExt(String key){
        return exts.get(key);
    }

    public EventType getType() {
        return type;
    }

    public EventModel setType(EventType type) {
        this.type = type;
        return this;
    }

    public int getActorId() {
        return actorId;
    }

    public EventModel setActorId(int actorId) {
        this.actorId = actorId;
        return this;
    }

    public int getEntityType() {
        return EntityType;
    }

    public EventModel setEntityType(int evtityType) {
        EntityType = evtityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public EventModel setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityOwnerId() {
        return entityOwnerId;
    }

    public EventModel setEntityOwnerId(int entityOwnerId) {
        this.entityOwnerId = entityOwnerId;
        return this;
    }

    public Map<String, String> getExts() {
        return exts;
    }

    public EventModel setExts(Map<String, String> exts) {
        this.exts = exts;
        return this;
    }
}
