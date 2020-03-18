package org.study.test.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description: 用户信息
 * @Author: TeGongX
 * @Email: 1135782208@qq.com
 * @Date: 2020年03月16日
 * @Version: V1.0.0
 * @Copyright: Copyright (c) 2019
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    private static final long serialVersionUID = -7364161081477336286L;

    private Long id;
    private String name;
    private String password;
    private LocalDate birthday;
    private Boolean gender;
    private String email;
    private List<String> hobby;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtUpdate;
}
