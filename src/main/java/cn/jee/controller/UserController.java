package cn.jee.controller;


import cn.jee.dao.UserDao;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserDao userDao;

    @RequestMapping("/login")
    public String login(String username, HttpServletRequest request) {
        userDao.login(username);
        request.getSession().setAttribute("username", username);
        return "redirect:/movie/load-all";
    }
}
