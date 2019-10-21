package com.johnduq.microappservice.service.api.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.johnduq.microappservice.util.JsonKeyValue;
import com.johnduq.microappservice.util.TypeAuthValues;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

	public JWTAuthorizationFilter(AuthenticationManager authenticationManager) {
		super(authenticationManager);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		String header = request.getHeader("Authorization");
		if (!requiresAuthentication(header)) {
			chain.doFilter(request, response);
			return;
		}

		boolean tokenValid;
		Claims claims = null;
		try {
			String jwt = header.replace(TypeAuthValues.BEARER_.getCode(), "");
			claims = Jwts.parser().setSigningKey(JsonKeyValue.THE_SECRET_VALUE_FOR_MICRO_APP_SERVICE_PROJECT.getBytes())
					.parseClaimsJws(jwt)// get Token from header
					.getBody();
			tokenValid = true;
		} catch (JwtException | IllegalArgumentException e) {
			tokenValid = false;
		}

		UsernamePasswordAuthenticationToken authenticationToken = null;
		if (tokenValid) {
			String username = claims.getSubject();
			Object roles = claims.get(TypeAuthValues.AUTHORITIES.getCode());
			Collection<? extends GrantedAuthority> authorities = Arrays.asList(
					new ObjectMapper()
						.addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthritiesMixin.class)
						.readValue(roles.toString().getBytes(), SimpleGrantedAuthority[].class));
			authenticationToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
		}
		SecurityContextHolder.getContext().setAuthentication(authenticationToken);
		chain.doFilter(request, response);
	}

	protected boolean requiresAuthentication(String header) {
		if (header == null || !header.toLowerCase().startsWith(TypeAuthValues.BEARER + " ")) {
			return true;
		}
		return false;
	}

}
