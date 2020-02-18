package com.wenda.wenda.dao;

import com.wenda.wenda.model.LoginTicket;
import com.wenda.wenda.model.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface LoginTicketDao {
    String TABLE_NAME = " login_ticket ";
    String INSERT_FIELDS = " user_id,expired,status,ticket ";
    String SELECT_FIELDS = " id, "+INSERT_FIELDS;

    @Insert({"insert into ",TABLE_NAME," (", INSERT_FIELDS ,") values(#{userId},#{expired},#{status},#{ticket})"})
    int addTicket(LoginTicket loginTicket);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where ticket=#{ticket}"})
    User selectByTicket(int id);

    @Update({"update ", TABLE_NAME, " set status=#{status} where ticket=#{ticket}"})
    void updatePassword(@Param("ticket") String ticket,@Param("status") int status);

}
