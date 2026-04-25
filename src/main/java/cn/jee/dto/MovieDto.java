package cn.jee.dto;

import lombok.Data;


@Data
public class MovieDto {
    private String watchDate;
    private String name;
    private String[] images;
}
