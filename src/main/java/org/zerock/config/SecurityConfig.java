package org.zerock.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.zerock.security.CustomLoginSuccessHadler;
import org.zerock.security.CustomUserDetailsService;

import lombok.Setter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Setter(onMethod = @__(@Autowired))
	private DataSource dataSource;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http.authorizeRequests()
			.antMatchers("/sample/all").permitAll()
			.antMatchers("/sample/admin").access("hasRole('ROLE_ADMIN')")
			.antMatchers("/sample/member").access("hasRole('ROLE_MEMBER')");
		
		http.formLogin()
			.loginPage("/customLogin")
			.loginProcessingUrl("/login")
			.successHandler(loginSuccessHandler());
		
		http.logout()
			.logoutUrl("/customLogout")
			.invalidateHttpSession(true)
			.deleteCookies("remember-me", "JSESSION_ID");
		
		http.rememberMe()
			.key("zerock")
			.tokenRepository(persistentTokenRepository())
			.tokenValiditySeconds(604800);
	}
	
	
	@Bean
	public AuthenticationSuccessHandler loginSuccessHandler() {
		return new CustomLoginSuccessHadler();
	}
	

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public UserDetailsService customUserService() {
		return new CustomUserDetailsService();
	}
	
	@Bean
	public PersistentTokenRepository persistentTokenRepository() {
		JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
		repo.setDataSource(dataSource);
		return repo;
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		auth.userDetailsService(customUserService()).
		passwordEncoder(passwordEncoder());
	}

	
}

/*
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		log.info("configure.....................");
		auth.inMemoryAuthentication().withUser("admin").password("{noop}admin").roles("ADMIN");
		auth.inMemoryAuthentication().withUser("member").password("$2a$10$EEXlppfY4aYsQd2vx4g9GOsTgzuEh6w/s0sM37VW6D2eD.xPNE1z.").roles("MEMBER");
		
		
	}
 */

/*
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		log.info("configure JDBC.....................");
		
		String queryUser = "select userid, userpw, enabled from tbl_member where userid=?";
		String queryDetails = "select userid, auth from tbl_member_auth where userid=?";
		
		auth.jdbcAuthentication()
			.dataSource(dataSource)
			.passwordEncoder(passwordEncoder())
			.usersByUsernameQuery(queryUser)
			.authoritiesByUsernameQuery(queryDetails);
	}
 */