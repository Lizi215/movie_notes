package cn.jee.service;

import cn.jee.dao.MovieDao;
import cn.jee.dto.MovieDto;
import cn.jee.entity.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieService {

    @Autowired
    private MovieDao movieDao;

    @Value("${default.images.path}")
    private String defaultImagesPath;

    /**
     * 加载指定用户的所有电影记录
     */
    public List<Movie> loadAllMovies(String username) {
        return movieDao.loadAllMovies(username);
    }

    /**
     * 为指定观影记录添加图片
     */
    public void addImage(String id, MultipartFile[] images) throws IOException {
        movieDao.addImage(id, images);
    }

    /**
     * 查询指定观影记录的图片，将文件系统路径转换为 Web 可访问的 URL，
     * 并自动清理数据库中存在但磁盘文件已不存在的记录
     */
    public MovieDto lookImages(String id) {
        MovieDto dto = movieDao.lookImages(id);
        if (dto.getImages() != null) {
            // 检查每个路径对应的文件是否真实存在
            List<String> validPaths = Arrays.stream(dto.getImages())
                    .filter(path -> !path.isBlank())
                    .filter(path -> Files.exists(Paths.get(path)))
                    .collect(Collectors.toList());

            // 如果有不存在的文件，更新数据库，清理掉失效记录
            long originalCount = Arrays.stream(dto.getImages())
                    .filter(path -> !path.isBlank())
                    .count();
            if (validPaths.size() < originalCount) {
                movieDao.updateImages(id, String.join(" ", validPaths));
            }

            // 转换为 Web URL
            dto.setImages(validPaths.stream()
                    .map(path -> "/images/" + Paths.get(path).getFileName().toString())
                    .toArray(String[]::new));
        }
        return dto;
    }

    /**
     * 保存电影记录
     */
    public void saveMovie(Movie movie, String username) {
        movieDao.saveMovie(movie, username);
    }

    /**
     * 删除电影记录及其关联的图片文件
     */
    public void deleteMovie(String watchDate) {
        // 先删除关联的图片文件
        MovieDto dto = movieDao.lookImages(watchDate);
        if (dto.getImages() != null) {
            for (String path : dto.getImages()) {
                if (!path.isBlank()) {
                    try {
                        Files.deleteIfExists(Paths.get(path));
                    } catch (IOException e) {
                        // 文件删除失败不影响数据库删除
                    }
                }
            }
        }
        movieDao.deleteMovie(watchDate);
    }

    /**
     * 删除单张图片（从数据库和磁盘）
     *
     * @param watchDate 观影日期
     * @param imageUrl  图片 Web URL，如 /images/uuid.jpg
     */
    public void deleteImage(String watchDate, String imageUrl) {
        // 从 URL 中提取文件名：/images/uuid.jpg → uuid.jpg
        String filename = imageUrl.substring("/images/".length());

        // 获取当前所有图片路径，移除要删除的那条
        MovieDto dto = movieDao.lookImages(watchDate);
        if (dto.getImages() == null) return;

        List<String> remaining = Arrays.stream(dto.getImages())
                .filter(path -> !path.isBlank())
                .filter(path -> !Paths.get(path).getFileName().toString().equals(filename))
                .collect(Collectors.toList());

        // 更新数据库
        movieDao.updateImages(watchDate, String.join(" ", remaining));

        // 删除磁盘文件
        try {
            Files.deleteIfExists(Paths.get(defaultImagesPath, filename));
        } catch (IOException e) {
            // 文件删除失败可忽略
        }
    }
}
