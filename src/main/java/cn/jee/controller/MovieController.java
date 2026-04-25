package cn.jee.controller;


import cn.jee.dao.MovieDao;
import cn.jee.dto.MovieDto;
import cn.jee.entity.Movie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/movie")
public class MovieController {
    @Autowired
    private MovieDao movieDao;

    @RequestMapping("/load-all")
    public String loadAllMovies(HttpSession session, Model model) {
        String username = (String)session.getAttribute("username");
        List<Movie> movies = movieDao.loadAllMovies(username);
        model.addAttribute("movies", movies);
        return "movies";
    }

    @RequestMapping("/add-image")
    public String addImage(String id, MultipartFile[] images) throws IOException {
        movieDao.addImage(id, images);
        return "redirect:/movie/load-all";
    }

    @RequestMapping("/upload-images")
    public String uploadImages(String id, HttpServletRequest request) {
        request.setAttribute("id", id);
        return "upload-images";
    }

    @ResponseBody
    @RequestMapping("/look-images")
    public MovieDto lookImages(String id) {
        return movieDao.lookImages(id);
    }

    @RequestMapping("/add-movie")
    public String addMovie() {
        return "add-movie";
    }

    @RequestMapping("/save-movie")
    public String saveMovie(@Validated Movie movie, BindingResult bindingResult, HttpServletRequest request) {
        List<String> errorMessages = new ArrayList<String>();
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(fieldError -> {
                errorMessages.add(fieldError.getDefaultMessage());
            });
            request.setAttribute("errorMessages", errorMessages);
            return "add-movie";
        }
        String username = (String) request.getSession().getAttribute("username");
        movieDao.saveMovie(movie, username);
        return "redirect:/movie/load-all";
    }

    @ResponseBody
    @ExceptionHandler(SQLException.class)
    public String handleSQLException(HttpServletResponse response) {
        return "观影时间冲突，请重试，尝试更换观影时间";
    }
}
