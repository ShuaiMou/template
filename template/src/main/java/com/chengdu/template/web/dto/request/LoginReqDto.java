package com.chengdu.template.web.dto.request;

import com.chengdu.template.web.dto.DtoBase;
import io.swagger.annotations.ApiModelProperty;
import jakarta.annotation.Nonnull;
import lombok.Data;

@Data
public class LoginReqDto extends DtoBase {
    @ApiModelProperty(value = "登录账户名")
    private String username;

    @ApiModelProperty(value = "登录密码")
    private String password;
}
