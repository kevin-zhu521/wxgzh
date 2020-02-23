package com.xiaoi.wxgzh.wx.controller;

import com.xiaoi.wxgzh.wx.service.WxService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

/**
 * @author kevin.zhu
 * @date 2020/2/15 20:39
 */
@RestController
@RequestMapping("wx")
@Api(value = "微信公众号")
public class WxController {

    @Autowired
    private WxService wxService;

    @GetMapping("check")
    @ApiOperation(value = "微信测试校验")
    public String WxCheck(@ApiParam(value = "签名", required = true) @RequestParam String signature,
                          @ApiParam(value = "时间戳", required = true) @RequestParam String timestamp,
                          @ApiParam(value = "随机数", required = true) @RequestParam String nonce,
                          @ApiParam(value = "回声", required = true) @RequestParam String echostr) {

        return wxService.WxCheck(signature, timestamp, nonce, echostr);
    }
    @PostMapping("check")
    @ApiOperation(value = "微信消息处理")
    public String WxMessageProcessing(@ApiParam(value = "消息请求", required = true)HttpServletRequest request) {
        return wxService.WxMessageProcessing(request);
    }
}
