package org.study.test.upsert;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 作用描述：TODO
 *
 * @author doveylovey
 * @version v1.0.0
 * @email 1135782208@qq.com
 * @date 2020年09月09日
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAccountRelation implements Serializable {
    private static final long serialVersionUID = 5251284481807535632L;

    /**
     * 自增主键
     */
    private Integer id;

    /**
     * 用户编号
     */
    private Integer userId;

    /**
     * 账户编号
     */
    private Integer accountId;

    /**
     * 状态：0-禁用、1-启用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime gmtCreate;

    /**
     * 更新时间
     */
    private LocalDateTime gmtUpdate;
}
