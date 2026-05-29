package org.jeecg.config.shiro.filters;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.jeecg.common.config.TenantContext;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.config.shiro.JwtToken;
import org.jeecg.config.shiro.ignore.InMemoryIgnoreAuth;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 鉴权登录拦截器
 * @Author: Scott
 * @Date: 2018/10/7
 **/
@Slf4j
public class JwtFilter extends BasicHttpAuthenticationFilter {

    /**
     * 默认开启跨域设置（使用单体）
     * 微服务情况下，此属性设置为false
     */
    private boolean allowOrigin = true;

    private static volatile boolean allowTokenInQuery = true;
    private static volatile List<String> corsAllowedOrigins = Collections.emptyList();

    public static void configure(boolean allowTokenInQueryParam, String corsAllowedOriginsCsv) {
        allowTokenInQuery = allowTokenInQueryParam;
        if (oConvertUtils.isEmpty(corsAllowedOriginsCsv)) {
            corsAllowedOrigins = Collections.emptyList();
        } else {
            corsAllowedOrigins = Arrays.stream(corsAllowedOriginsCsv.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }
    }

    public JwtFilter(){}
    public JwtFilter(boolean allowOrigin){
        this.allowOrigin = allowOrigin;
    }

    /**
     * 执行登录认证
     *
     * @param request
     * @param response
     * @param mappedValue
     * @return
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        try {
            // 判断当前路径是不是注解了@IngoreAuth路径，如果是，则放开验证
            if (InMemoryIgnoreAuth.contains(((HttpServletRequest) request).getServletPath())) {
                return true;
            }
            
            executeLogin(request, response);
            return true;
        } catch (Exception e) {
            // 使用异常中的具体错误信息，保留"不允许同一账号多地同时登录"等具体提示
            String errorMsg = e.getMessage();
            if (oConvertUtils.isEmpty(errorMsg)) {
                errorMsg = CommonConstant.TOKEN_IS_INVALID_MSG;
            }
            JwtUtil.responseError((HttpServletResponse)response, 401, errorMsg);
            return false;
        }
    }

    /**
     *
     */
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String token = httpServletRequest.getHeader(CommonConstant.X_ACCESS_TOKEN);
        // 代码逻辑说明: JT-355 OA聊天添加 token 参数；生产建议 jeecg.security.allow-token-in-query=false
        if (oConvertUtils.isEmpty(token) && allowTokenInQuery) {
            token = httpServletRequest.getParameter("token");
        }

        JwtToken jwtToken = new JwtToken(token);
        // 提交给realm进行登入，如果错误他会抛出异常并被捕获
        getSubject(request, response).login(jwtToken);
        // 如果没有抛出异常则代表登入成功，返回true
        return true;
    }

    /**
     * 对跨域提供支持
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        if(allowOrigin){
            String requestOrigin = httpServletRequest.getHeader(HttpHeaders.ORIGIN);
            if (!corsAllowedOrigins.isEmpty()) {
                if (requestOrigin != null && corsAllowedOrigins.contains(requestOrigin)) {
                    httpServletResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, requestOrigin);
                }
            } else if (requestOrigin != null) {
                httpServletResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, requestOrigin);
            }
            // 允许客户端请求方法
            httpServletResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET,POST,OPTIONS,PUT,DELETE");
            // 允许客户端提交的Header
            String requestHeaders = httpServletRequest.getHeader(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS);
            if (StringUtils.isNotEmpty(requestHeaders)) {
                httpServletResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, requestHeaders);
            }
            // 允许客户端携带凭证信息(是否允许发送Cookie)
            httpServletResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        }
        // 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
        if (RequestMethod.OPTIONS.name().equalsIgnoreCase(httpServletRequest.getMethod())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
        // 代码逻辑说明: 多租户用到
        String tenantId = httpServletRequest.getHeader(CommonConstant.TENANT_ID);
        TenantContext.setTenant(tenantId);

        return super.preHandle(request, response);
    }

    /**
     * JwtFilter中ThreadLocal需要及时清除 #3634
     *
     * @param request
     * @param response
     * @param exception
     * @throws Exception
     */
    @Override
    public void afterCompletion(ServletRequest request, ServletResponse response, Exception exception) throws Exception {
        //log.info("------清空线程中多租户的ID={}------",TenantContext.getTenant());
        TenantContext.clear();
    }
}
