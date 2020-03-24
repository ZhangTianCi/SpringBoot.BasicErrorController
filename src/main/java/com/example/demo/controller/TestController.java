package com.example.demo.controller;

import com.example.demo.global.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Controller
@RequestMapping("/test")
public class TestController {

    @ResponseBody
    @RequestMapping(value = "/value")
    public void empty() {
        int temp = 1 / 0;
    }

    @RequestMapping(value = "/file/{path}")
    public ResponseEntity value(@PathVariable("path") String path, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String savePath = "~/";
            // 获取文件名称，中文可能被URL编码
            path = URLDecoder.decode(path, StandardCharsets.UTF_8);
            // 获取本地文件系统中的文件资源
            FileSystemResource resource = new FileSystemResource(savePath + path);
            // 解析文件的 mime 类型
            String mediaTypeStr = URLConnection.getFileNameMap().getContentTypeFor(path);
            // 无法判断MIME类型时，作为流类型
            mediaTypeStr = (mediaTypeStr == null) ? MediaType.APPLICATION_OCTET_STREAM_VALUE : mediaTypeStr;
            // 实例化MIME
            MediaType mediaType = MediaType.parseMediaType(mediaTypeStr);
            HttpHeaders headers = new HttpHeaders();
            // 下载之后需要在请求头中放置文件名，该文件名按照ISO_8859_1编码。
            String filenames = new String(resource.getFilename().getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
            headers.setContentDispositionFormData("attachment", filenames);
            headers.setContentType(mediaType);

            return ResponseEntity.ok().headers(headers).contentLength(resource.getInputStream().available()).body(resource);
        } catch (IOException e) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Location", "http://www.baidu.com");
            return ResponseEntity.status(302).headers(headers).body(null);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/calc/{value}", method = RequestMethod.POST)
    public ApiResult calc(@PathVariable("value") int value) {
        return ApiResult.success(1 / value);
    }

    @ResponseBody
    @RequestMapping(value = "/calc/string", method = RequestMethod.POST)
    public ApiResult string(@RequestBody(required = false) String input) {
        return ApiResult.success(input);
    }

    @ResponseBody
    @RequestMapping(value = "/calc/error", method = RequestMethod.POST)
    public ApiResult throwError() throws Exception {
        throw new Exception("自定义错误");
    }

    @RequestMapping(value = "/html")
    public ModelAndView toHtml() throws Exception {
        throw new Exception("自定义错误");
    }

}
