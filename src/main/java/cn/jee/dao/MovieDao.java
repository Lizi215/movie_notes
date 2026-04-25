package cn.jee.dao;

import cn.jee.dto.MovieDto;
import cn.jee.entity.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.List;
import java.util.Objects;

@Repository
public class MovieDao {
    @Value("${default.images.path}")
    private String defaultImagesPath;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    public void addImage(String id, MultipartFile[] images) throws IOException {
        String sql = "select images from user_movie where watch_date = ?";
        StringBuilder imagesStr = new StringBuilder(Objects.requireNonNull(jdbcTemplate.queryForObject(sql, String.class, id)));
        for (MultipartFile image : images) {
            Path finalPath = Paths.get(defaultImagesPath, image.getOriginalFilename());
            imagesStr.append(" ").append(finalPath);
            image.transferTo(finalPath);
        }
        sql = "update user_movie set images = ? where watch_date = ?";
        jdbcTemplate.update(sql, imagesStr.toString(), id);
    }

    public MovieDto lookImages(String id) {
        String sql = "select movie_name, images from user_movie where watch_date = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                    MovieDto dto = new MovieDto();
                    dto.setWatchDate(id);
                    dto.setName(rs.getString("movie_name"));
                    dto.setImages(rs.getString("images").split(" "));
                    return dto;
                }, id);
    }

    public List<Movie> loadAllMovies(String username) {
        String sql = "select * from user_movie where user_name = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> Movie.builder()
            .watchDate(rs.getString("watch_date"))
            .name(rs.getString("movie_name"))
            .ticketPrice(rs.getInt("ticket_price"))
            .images(rs.getString("images").split(" "))
            .comment(rs.getString("comment"))
            .build(), username);
    }

    public void saveMovie(Movie movie, String username) {
        String sql = "insert into user_movie values(?,?,?,?,?,?)";
        jdbcTemplate.update(sql,movie.getWatchDate(), username,
            movie.getName(), movie.getTicketPrice(), "", movie.getComment());
    }
}
