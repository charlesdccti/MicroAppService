package com.johnduq.microappservice.service.api.security;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.johnduq.microappservice.control.IRoleControl;
import com.johnduq.microappservice.control.IUserControl;
import com.johnduq.microappservice.model.entity.Role;
import com.johnduq.microappservice.util.TypeState;

@Service
public class UserSecurity implements UserDetailsService {

	private static final String ROLE_ = "ROLE_";

	private static final String USUARIO_NO_TIENE_PERMISOS = "Usuario no tiene permisos";

	private static final String USUARIO_NO_EXISTE = "Usuario no existe:";

	private static final Logger logger = LoggerFactory.getLogger(UserSecurity.class);

	@Autowired
	private IUserControl iUserControl;
	@Autowired
	private IRoleControl iRoleControl;

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		com.johnduq.microappservice.model.entity.User user = iUserControl.findByUser(username);
		if (user == null) {
			logger.error(USUARIO_NO_EXISTE + username);
			throw new UsernameNotFoundException(USUARIO_NO_EXISTE + username);
		}
		List<Role> lRole = iRoleControl.findByUser(username);

		if (lRole.isEmpty()) {
			logger.error(USUARIO_NO_TIENE_PERMISOS);
			throw new UsernameNotFoundException(USUARIO_NO_TIENE_PERMISOS);
		}

		List<GrantedAuthority> lAuthorities = new ArrayList<GrantedAuthority>();

		lRole.forEach(role -> lAuthorities.add(new SimpleGrantedAuthority(ROLE_ + role.getName())));

		User userDetails = new User(user.getUsername(), user.getPassword(),
				TypeState.ENABLE.getCode().equals(user.getStatus()), true, true, true, lAuthorities);

		return userDetails;
	}

}
