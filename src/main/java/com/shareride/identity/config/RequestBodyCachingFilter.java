package com.shareride.identity.config;

import com.shareride.identity.config.requestwrapper.CachedBodyHttpServletRequestWrapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.shareride.identity.utils.Constants.PATCH;
import static com.shareride.identity.utils.Constants.POST;
import static com.shareride.identity.utils.Constants.PUT;

@Component
public class RequestBodyCachingFilter extends OncePerRequestFilter {

    private static final List<String> METHODS_TO_CACHE = Arrays.asList(POST, PUT, PATCH);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (METHODS_TO_CACHE.contains(request.getMethod().toUpperCase())) {
            CachedBodyHttpServletRequestWrapper wrappedRequest = new CachedBodyHttpServletRequestWrapper(request);
            filterChain.doFilter(wrappedRequest, response);
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
