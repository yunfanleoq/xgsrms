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

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

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

    private boolean allowTokenInQuery = true;

    /** 非空时仅允许列表中的 Origin；为空则反射请求 Origin（旧行为） */
    private Set<String> corsAllowedOrigins = Collections.emptySet();

    public JwtFilter() {}

    public JwtFilter(boolean allowOrigin) {
        this.allowOrigin = allowOrigin;
    }

    public JwtFilter(boolean allowOrigin, boolean allowTokenInQuery, String corsAllowedOriginsCsv) {
        this.allowOrigin = allowOrigin;
        this.allowTokenInQuery = allowTokenInQuery;
        this.corsAllowedOrigins = parseCorsAllowedOrigins(corsAllowedOriginsCsv);
    }

    private static Set<String> parseCorsAllowedOrigins(String csv) {
        if (StringUtils.isBlank(csv)) {
            return Collections.emptySet();
        }
        Set<String> set = new LinkedHashSet<>();
        for (String part : csv.split(",")) {
            String t = part != null ? part.trim() : "";
            if (!t.isEmpty()) {
                set.add(t);
            }
        }
        return set.isEmpty() ? Collections.emptySet() : Collections.unmodifiableSet(set);
    }

    /**
     * 执行登录认证
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        try {
            if (InMemoryIgnoreAuth.contains(((HttpServletRequest) request).getServletPath())) {
                return true;
            }

            executeLogin(request, response);
            return true;
        } catch (Exception e) {
            JwtUtil.responseError(response,401,CommonConstant.TOKEN_IS_INVALID_MSG);
            return false;
        }
    }

    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String token = httpServletRequest.getHeader(CommonConstant.X_ACCESS_TOKEN);
        if (oConvertUtils.isEmpty(token) && allowTokenInQuery) {
            token = httpServletRequest.getParameter("token");
        }

        JwtToken jwtToken = new JwtToken(token);
        getSubject(request, response).login(jwtToken);
        return true;
    }

    /**
     * 对跨域提供支持
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        if (allowOrigin) {
            String origin = httpServletRequest.getHeader(HttpHeaders.ORIGIN);
            if (corsAllowedOrigins.isEmpty()) {
                httpServletResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
            } else if (origin != null && corsAllowedOrigins.contains(origin)) {
                httpServletResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
            } else if (origin != null) {
                log.debug("CORS: rejected Origin not in allowlist: {}", origin);
            }
            httpServletResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET,POST,OPTIONS,PUT,DELETE");
            String requestHeaders = httpServletRequest.getHeader(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS);
            if (StringUtils.isNotEmpty(requestHeaders)) {
                httpServletResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, requestHeaders);
            }
            if (StringUtils.isNotEmpty(httpServletResponse.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN))) {
                httpServletResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
            }
        }
        if (RequestMethod.OPTIONS.name().equalsIgnoreCase(httpServletRequest.getMethod())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
        String tenantId = httpServletRequest.getHeader(CommonConstant.TENANT_ID);
        TenantContext.setTenant(tenantId);

        return super.preHandle(request, response);
    }

    @Override
    public void afterCompletion(ServletRequest request, ServletResponse response, Exception exception) throws Exception {
        TenantContext.clear();
    }
}
