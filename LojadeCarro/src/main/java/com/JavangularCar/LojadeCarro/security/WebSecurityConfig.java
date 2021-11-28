package com.JavangularCar.LojadeCarro.security;

import com.JavangularCar.LojadeCarro.service.UsuarioService;
import io.swagger.models.HttpMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public static BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(String.valueOf(HttpMethod.POST), "/**/import").hasRole("ADMINISTRATOR")
                .antMatchers("/api-docs/**", "/swagger-resources/**", "/v2/api-docs", "/**/favicon.ico", "/webjars/**", "/api/admin/health").permitAll()
                .anyRequest().permitAll()
                //replace .permitAll() with .authenticated() for authentiaction
                //replace .authenticated() with .permitAll() for disabling security
                .and()
                .csrf().disable()
                .headers().disable()
                .httpBasic();
    }
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception{
//        auth.inMemoryAuthentication().withUser("user")
//                .password(passwordEncoder().encode("password")).authorities("USER");
//    }

}
