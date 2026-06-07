package cn.jee.controller;


import cn.jee.dto.MovieDto;
import cn.jee.entity.Movie;
import cn.jee.service.MovieService;
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
    private MovieService movieService;

    @RequestMapping("/load-all")
    public String loadAllMovies(HttpSession session, Model model) {
        String username = (String)session.getAttribute("username");
        List<Movie> movies = movieService.loadAllMovies(username);
        model.addAttribute("movies", movies);
        return "movies";
    }

    @RequestMapping("/add-image")
    public String addImage(String id, MultipartFile[] images) throws IOException {
        movieService.addImage(id, images);
        return "redirect:/movie/look-images?id=" + id;
    }

    @RequestMapping("/upload-images")
    public String uploadImages(String id, HttpServletRequest request) {
        request.setAttribute("id", id);
        return "upload-images";
    }

    @RequestMapping("/look-images")
    public String lookImages(String id, Model model) {
        MovieDto dto = movieService.lookImages(id);
        model.addAttribute("movie", dto);
        return "look-images";
    }

    @RequestMapping("/delete-movie")
    public String deleteMovie(String id) {
        movieService.deleteMovie(id);
        return "redirect:/movie/load-all";
    }

    @RequestMapping("/delete-image")
    public String deleteImage(String id, String imageUrl) {
        movieService.deleteImage(id, imageUrl);
        return "redirect:/movie/look-images?id=" + id;
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
        movieService.saveMovie(movie, username);
        return "redirect:/movie/load-all";
    }

    @ResponseBody
    @ExceptionHandler(SQLException.class)
    public String handleSQLException(HttpServletResponse response) {
        return "观影时间冲突，请重试，尝试更换观影时间";
    }
}
