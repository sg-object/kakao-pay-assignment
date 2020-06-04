package com.sg.assignment.config;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import com.sg.assignment.common.web.filter.AjaxAuthenticationFilter;
import com.sg.assignment.common.web.filter.JwtAuthenticationFilter;
import com.sg.assignment.common.web.filter.SkipPathRequestMatcher;
import com.sg.assignment.common.web.handler.JwtFailHandler;
import com.sg.assignment.common.web.handler.LoginFailureHandler;
import com.sg.assignment.common.web.handler.LoginSuccessHandler;
import com.sg.assignment.common.web.provider.AjaxAuthenticationProvider;
import com.sg.assignment.common.web.provider.JwtAuthenticationProvider;

@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private JwtAuthenticationProvider jwtProvider;

	@Autowired
	private AjaxAuthenticationProvider ajaxProvider;

	@Autowired
	private LoginSuccessHandler loginSuccessHandler;

	@Autowired
	private LoginFailureHandler loginFailureHandler;

	@Autowired
	private JwtFailHandler jwtFailHandler;

	private static final String ROOT_ENTRY_POINT = "/**";

	private static final String LOGIN_ENTRY_POINT = "/login";

	private static final String JOIN_ENTRY_POINT = "/join";

	@Override
	public void configure(WebSecurity web) throws Exception {
		// TODO Auto-generated method stub
		web.ignoring().antMatchers("/resources/**", "/v2/api-docs", "/configuration/ui", "/swagger-resources/**",
				"/configuration/**", "/swagger-ui.html", "/webjars/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// TODO Auto-generated method stub
		http.authorizeRequests().antMatchers(LOGIN_ENTRY_POINT, JOIN_ENTRY_POINT).permitAll()
				.antMatchers(ROOT_ENTRY_POINT).authenticated();
		http.addFilterBefore(ajaxAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(jwtAuthenticationFilter(), FilterSecurityInterceptor.class).csrf().disable()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// TODO Auto-generated method stub
		auth.authenticationProvider(ajaxProvider).authenticationProvider(jwtProvider);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	public AntPathRequestMatcher antPathRequestMatcher() {
		return new AntPathRequestMatcher(LOGIN_ENTRY_POINT, HttpMethod.POST.name());
	}

	public SkipPathRequestMatcher skipPathRequestMatcher() {
		return new SkipPathRequestMatcher(Arrays.asList(LOGIN_ENTRY_POINT, JOIN_ENTRY_POINT));
	}

	public AjaxAuthenticationFilter ajaxAuthenticationFilter() throws Exception {
		AjaxAuthenticationFilter filter = new AjaxAuthenticationFilter(antPathRequestMatcher());
		filter.setAuthenticationManager(authenticationManager());
		filter.setAuthenticationSuccessHandler(loginSuccessHandler);
		filter.setAuthenticationFailureHandler(loginFailureHandler);
		return filter;
	}

	public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
		JwtAuthenticationFilter filter = new JwtAuthenticationFilter(skipPathRequestMatcher());
		filter.setAuthenticationManager(authenticationManager());
		filter.setAuthenticationFailureHandler(jwtFailHandler);
		return filter;
	}
}
