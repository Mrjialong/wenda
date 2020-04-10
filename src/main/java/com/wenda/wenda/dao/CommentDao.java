package com.wenda.wenda.dao;

import com.wenda.wenda.model.Comment;
import com.wenda.wenda.model.Question;
import com.wenda.wenda.model.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface CommentDao {
    String TABLE_NAME = " comment ";
    String INSERT_FIELDS = " user_id,content,created_date,entity_id,entity_type,status ";
    String SELECT_FIELDS = " id, "+INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{userId},#{content},#{createdDate},#{entityId},#{entityType},#{status})"})
    int addComment(Comment comment);

    @Select({"select ",SELECT_FIELDS," from ",TABLE_NAME,
    " where entity_id = #{entityId} and entity_type = #{entityType} order by created_date desc"})
    List<Comment> selectCommentByEntity(@Param("entityId") int entityId, @Param("entityType") int entityType);


    @Select({"select count(id) from ",TABLE_NAME," where entity_id = #{entityId} and entity_type = #{entityType}"})
    int getCommentCount(@Param("entityId") int entityId, @Param("entityType") int entityType);

    @Update("update comment set status = #{status} where id = #{id}")
    int updateStatus(@Param("id") int id,@Param("status") int status);

    @Select({"select ",SELECT_FIELDS," from ",TABLE_NAME," where id = #{id}"})
    Comment getCommentbyId(int id);

    @Select({"select count(id) from ", TABLE_NAME, " where user_id=#{userId}"})
    int getUserCommentCount(int userId);
}
