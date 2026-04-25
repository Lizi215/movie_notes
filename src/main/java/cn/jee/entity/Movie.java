package cn.jee.entity;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.Arrays;
import java.util.Objects;

@Data
@Builder
public class Movie {
    @Pattern(regexp = "^(19|20)\\d{2}(0[1-9]|1[0-2])" +
        "(0[1-9]|[12][0-9]|3[01])([01][0-9]|2[0-3])$",
      message = "{movie.datetime.not-suit}")
    private String watchDate;

    private String name;

    @Min(value = 1, message = "{movie.ticket-price.not-suit}")
    private Integer ticketPrice;

    private String[] images;

    @Size(min = 20, message = "{movie.comment.not-suit}")
    private String comment;

    @Override
    public String toString() {
        return "Movie{" +
                "watchDate='" + watchDate + '\'' +
                ", name='" + name + '\'' +
                ", ticketPrice=" + ticketPrice +
                ", images=" + Arrays.toString(images) +
                ", comment='" + comment + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return Objects.equals(watchDate, movie.watchDate)
                && Objects.equals(name, movie.name)
                && Objects.equals(ticketPrice, movie.ticketPrice)
                && Arrays.equals(images, movie.images)
                && Objects.equals(comment, movie.comment);
    }
    @Override
    public int hashCode() {
        int result = Objects.hash(watchDate, name, ticketPrice, comment);
        result = 31 * result + Arrays.hashCode(images);
        return result;
    }
}
