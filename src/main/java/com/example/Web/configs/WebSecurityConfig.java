package com.example.Web.configs;

import com.example.Web.models.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private DataSource dataSource;

    protected void configure(HttpSecurity http) throws Exception {
        String[] staticResources = {
                "/css/**",
                "/images/**",
                "/fonts/**",
                "/scripts/**",};
        http.csrf().disable();
        http
                .authorizeRequests()
                    .antMatchers("/", "/registration").permitAll()
                    .antMatchers("/expert/*", "/expert").hasAuthority("EXPERT")
                    .antMatchers(staticResources).permitAll()
                    .antMatchers("/occupation/add", "/test_result").hasAuthority("USER") // Добавляем путь для страницы, которую нужно защитить
                    .anyRequest().authenticated()
                .and()
                .formLogin().defaultSuccessUrl("/", true)
                .loginPage("/login")
                .permitAll()
                .and()
                .logout().logoutSuccessUrl("/login").logoutSuccessUrl("/")
                .permitAll();

    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .passwordEncoder(NoOpPasswordEncoder.getInstance())
                .usersByUsernameQuery("select email, password, 'true' from user where email=?")
                .authoritiesByUsernameQuery("select email, role from user inner join user_role on user.id=user_role.user_id where email=?");
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
                "/css/**", "/img/**");
    }

}

//,"/header_style"
//, "header_style.css"