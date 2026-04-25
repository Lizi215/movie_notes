package cn.jee.dao;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;

@Repository
public class UserDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void login(String username) {
        String sql = "select count(1) from user_data where name = ?";
        Long num = jdbcTemplate.queryForObject(sql, Long.class, username);
        if (num == null || num == 0) {
            sql = "insert into user_data(name) values (?)";
            jdbcTemplate.update(sql, username);
        }
    }
}
