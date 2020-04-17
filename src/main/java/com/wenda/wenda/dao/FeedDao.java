package com.wenda.wenda.dao;

import com.wenda.wenda.model.Comment;
import com.wenda.wenda.model.Feed;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface FeedDao {
    String TABLE_NAME = " feed ";
    String INSERT_FIELDS = " user_id,data,created_date,type ";
    String SELECT_FIELDS = " id, "+INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{userId},#{data},#{createdDate},#{type})"})
    int addFeed(Feed feed);

    /**
     * 推模式
     * @param id
     * @return
     */
    @Select({"select ",SELECT_FIELDS," from ",TABLE_NAME," where id = #{id}"})
    Feed getFeedbyId(int id);

    /**
     * 拉模式 查找新插入的feed
     * @param macId 上一次刷新到了的macid
     * @param userIds 关注的用户群
     * @param count 分页
     * @return
     */
    List<Feed> selectUserFeeds(@Param("maxId") int macId,
                               @Param("userIds") List<Integer>userIds,
                               @Param("count") int count);
}
