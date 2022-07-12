package com.servustech.eduson.config;

import com.servustech.eduson.security.jwt.JwtAccessDeniedHandler;
import com.servustech.eduson.security.jwt.JwtAuthenticationEntryPoint;
import com.servustech.eduson.security.jwt.JwtAuthenticationFilter;
import com.servustech.eduson.security.userdetails.CustomUserDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@AllArgsConstructor
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationEntryPoint unauthorizedHandler;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private static final String[] swaggerConfig = { "/swagger**", "/**/swagger**", "/**/springfox**", "/api-doc/**",
            "/**/api-docs" };

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder// .parentAuthenticationManager(authenticationManagerBean())
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .cors()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/auth/register", 
                        "/auth/register/admin", "/auth/register/lector",
                        "/auth/login", "/auth/confirmation", "/auth/resend",
                        "/auth/lost-password", "/auth/lost-password-email",
                        "/files/download", "/api/files/download", "/api/files/url", "/files/url", "/api/test/mail",
                        "/webinar/new", "/webinar/{id}", "/webinar/courses-ext/{id}", "/webinar/newchunk/**", // TODO
                        "/webinar/livehdsfbjdsfsbfsahfsavf/**", // TODO
                        "/course/new", "/course/{id}",
                        "/course/newchunk/**", // TODO
                        "/live-event/new", "/live-event/{id}",
                        "/module/page",
                        "/tag/{id}", "/tag/courses-assigned/{tagId}", "/tag/webinars-assigned/{tagId}",
                        "/tag-category/page", "/tag/category/{tagCategoryId}",
                        "/lector/public", "/lector/profile/{id}", "/lector/viewpublic",
                        "/course/lector/{lectorId}",
                        "/permission/subs/retail", "/files/download/url/{id}",
                        "/webhook", "/wchat/**/*",
                        "/wchat2/previous/**/*", // TODO
                        "/search/**",
                        "/assets/i18n",
                        "/general/**/*", "/general-file/**/*", "/general/page")
                .permitAll()
                // TODO /files/download/url/{id} and course files!!
                .antMatchers("/app/**/*.{js,html}").permitAll()
                .antMatchers(swaggerConfig).permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling()
                .accessDeniedHandler(jwtAccessDeniedHandler)
                .authenticationEntryPoint(unauthorizedHandler);

        // Add our custom JWT security filter
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

    }
}
