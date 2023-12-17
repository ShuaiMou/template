package com.chengdu.template.web.controller;

import com.chengdu.template.common.JsonResult;
import com.chengdu.template.web.auth.CheckTimestamp;
import com.chengdu.template.web.dto.UserDto;
import com.chengdu.template.web.dto.request.LoginReqDto;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@CheckTimestamp
public class LoginController {

    @PostMapping("/login")
    @ApiOperation(value = "账号密码登陆验证", notes = "登陆", httpMethod = "POST", tags = {"一期接口", "用户"})
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "body", dataType = "LoginReqDto",
                    name = "loginReqDto", value = "请求参数body", required = true)
    })
    public JsonResult<UserDto> login(@RequestBody LoginReqDto loginReqDto) {
        UserDto user = new UserDto();
        BeanUtils.copyProperties(loginReqDto, user);
        return JsonResult.success(user);
    }

}
