package com.sg.data.common.web.filter;

import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class SkipPathRequestMatcher implements RequestMatcher {

	private OrRequestMatcher orRequestMatcher;

	public SkipPathRequestMatcher(List<String> skipPathList) {
		// TODO Auto-generated constructor stub
		if (!skipPathList.isEmpty()) {
			List<RequestMatcher> requestMatcherList = skipPathList.stream().map(AntPathRequestMatcher::new)
					.collect(Collectors.toList());
			this.orRequestMatcher = new OrRequestMatcher(requestMatcherList);
		}
	}

	@Override
	public boolean matches(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return !orRequestMatcher.matches(request);
	}
}
