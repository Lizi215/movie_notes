package cn.jee.service;

import cn.jee.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    /**
     * 用户登录/注册：如果用户名不存在则自动注册
     */
    public void login(String username) {
        userDao.login(username);
    }
}
