package com.harrywu.springweb.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.activation.MimetypesFileTypeMap;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

import com.harrywu.springweb.common.StreamingViewRenderer;

@ControllerAdvice
public abstract class AbstractController {
    protected static final Log log = LogFactory.getLog(AbstractController.class);
    
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }
    
    @PostConstruct
    private void prepare() {
//        log.debug("SessionUtils: " + sessionUtils);
    }
    
    protected static String getMimeType(File file) {
        return getMimeType(file.getName());
    }

    protected static String getMimeType(String fileName) {
        return new MimetypesFileTypeMap().getContentType(fileName);
    }
    
    protected Map<String, Object> trimMapParameters(Map<String, Object> parameters) {
        for (String key: parameters.keySet()) {
            Object value = parameters.get(key);
            if (value != null && value instanceof String && value.toString().length() == 0)
                parameters.put(key, null);
        }
        return parameters;
    }
    
    protected void output(String filePath, String fileOutputName, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String fileNameExtension = FilenameUtils.getExtension(fileOutputName);
        String filePathExtension = FilenameUtils.getExtension(filePath);
        if(!fileNameExtension.equals(filePathExtension)) {
            fileOutputName = fileOutputName + "." + filePathExtension;
        }
        response.setCharacterEncoding("UTF-8");
        // 一律串流回應
        Map<String, Object> objectMap = new LinkedHashMap<String, Object>();
        File outputFile = new File(filePath);
        String contentType = getMimeType(outputFile);
        objectMap.put(StreamingViewRenderer.INPUT_STREAM, new FileInputStream(outputFile));
        objectMap.put(StreamingViewRenderer.CONTENT_TYPE, contentType);
        objectMap.put(StreamingViewRenderer.CONTENT_LENGTH, outputFile.length());
        objectMap.put(StreamingViewRenderer.FILENAME, URLEncoder.encode(fileOutputName, "UTF-8"));
        objectMap.put(StreamingViewRenderer.LAST_MODIFIED, new Date(outputFile.lastModified()));
        StreamingViewRenderer svr = new StreamingViewRenderer();
        svr.renderMergedOutputModel(objectMap, request, response);
    }

    protected void output(InputStream in, String fileOutputName, HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setCharacterEncoding("UTF-8");
        // 一律串流回應
        Map<String, Object> objectMap = new LinkedHashMap<String, Object>();
        String contentType = getMimeType(fileOutputName);
        objectMap.put(StreamingViewRenderer.INPUT_STREAM, in);
        objectMap.put(StreamingViewRenderer.CONTENT_TYPE, contentType);
        objectMap.put(StreamingViewRenderer.CONTENT_LENGTH, Long.valueOf(in.available()));
        objectMap.put(StreamingViewRenderer.FILENAME, URLEncoder.encode(fileOutputName, "UTF-8"));
        objectMap.put(StreamingViewRenderer.LAST_MODIFIED, new Date());
        StreamingViewRenderer svr = new StreamingViewRenderer();
        svr.renderMergedOutputModel(objectMap, request, response);
    }
    
    protected String lastAccessUrl(HttpServletRequest request) {
        String url = homeUrl(request);
        return verifyLastAccessUrl(url, request);
    }
    
    protected String verifyLastAccessUrl(String url, HttpServletRequest request) {
        String loginPage = loginUrl(request);
        String logoutPage = logoutUrl(request);
        String homePage = homeUrl(request);

        String localName = request.getLocalName();
        String referer = request.getHeader("referer");
        if (!StringUtils.isEmpty(referer) && !StringUtils.startsWith(referer, localName))
            url = referer;
        if (url.contains(loginPage) || url.contains(logoutPage))
            url = homePage;
        log.debug(String.format("referer: %s", referer));
        log.debug(String.format("lastAccessUrl: %s", url));
        return url;
    }
    
    protected String homeUrl(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        if (!contextPath.endsWith("/"))
            contextPath += "/";
        return contextPath;
    }
    
    protected String loginUrl(HttpServletRequest request) {
        return homeUrl(request) + "login";
    }
    
    protected String logoutUrl(HttpServletRequest request) {
        return homeUrl(request) + "logout";
    }
}

