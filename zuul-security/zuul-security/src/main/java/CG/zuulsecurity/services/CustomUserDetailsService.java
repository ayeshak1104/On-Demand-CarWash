package CG.zuulsecurity.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import CG.zuulsecurity.models.User;
import CG.zuulsecurity.repositories.RoleRepository;
import CG.zuulsecurity.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import CG.zuulsecurity.models.Role;

@Service
public class CustomUserDetailsService implements UserDetailsService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private AuthService as;
	@Autowired
	private PasswordEncoder bCryptPasswordEncoder;

	//To find a user with thier email
	public User findUserByEmail(String email) {
	    return userRepository.findByEmail(email);
	}
	//To update the token of the user everytime the user logins
	public User updateTokenByID(User ExistingUser, String token){
		ExistingUser.setToken(token);
		return userRepository.save(ExistingUser);
	}
	//save user using his role
	public User saveUser(User user) {
		//Bcrypt is a one-way strong Hashing Function
	    user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
	    user.setEnabled(true);
	    System.out.println(user.getRoles());
		String roleToken=user.getToken();
		if(user.getToken().isEmpty()){
			Role userRole = roleRepository.findByRole("USER");
			user.setRoles((new HashSet<>(Arrays.asList(userRole))));
		}
		else {
			Role userRole = roleRepository.findByRole(roleToken);
			user.setRoles((new HashSet<>(Arrays.asList(userRole))));
		}
		//Logging the saved use in console
		System.out.println(user);
		//Returning and logging the user
	    return userRepository.save(user);
	}
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

	    User user = userRepository.findByEmail(email);
	    if(user != null) {
	        List<GrantedAuthority> authorities = getUserAuthority(user.getRoles());
	        return buildUserForAuthentication(user, authorities);
	    } else {
	        throw new UsernameNotFoundException("Username not found in database");
	    }
	}
	
	private List<GrantedAuthority> getUserAuthority(Set<Role> userRoles) {
	    Set<GrantedAuthority> roles = new HashSet<>();
	    userRoles.forEach((role) -> {
	        roles.add(new SimpleGrantedAuthority(role.getRole()));
	    });
	    List<GrantedAuthority> grantedAuthorities = new ArrayList<>(roles);
	    return grantedAuthorities;
	}
	
	private UserDetails buildUserForAuthentication(User user, List<GrantedAuthority> authorities) {
	    return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
	}
}


/**
 * The logic being used while getting the set roles
 * 		Iterator i=user.getRoles().iterator();
 *      //If the user doesn't mention any role it would be set as User by default
 * 		if(user.getRoles().size()==0)
 *                {
 * 			Role userRole = roleRepository.findByRole("USER");
 * 			user.setRoles((new HashSet<>(Arrays.asList(userRole))));
 *        }
 * 	    while (i.hasNext()) {
 *             Role r=(Role) i.next();
 * 			if(r.getRole()==null)
 *            {
 * 				Role userRole = roleRepository.findByRole("USER");
 * 				user.setRoles((new HashSet<>(Arrays.asList(userRole))));
 *            }
 * 			else
 *            {
 * 				if(r.getRole().equals("ADMIN"))
 *                {
 * 					Role userRole = roleRepository.findByRole("ADMIN");
 * 					user.setRoles(new HashSet<>(Arrays.asList(userRole)));
 *                }
 * 				else if(r.getRole().equals("WASHER"))
 *                {
 * 					Role userRole = roleRepository.findByRole("WASHER");
 * 					user.setRoles(new HashSet<>(Arrays.asList(userRole)));
 *                }
 * 				else
 *                {
 * 					Role userRole = roleRepository.findByRole("USER");
 * 					user.setRoles(new HashSet<>(Arrays.asList(userRole)));
 *                }
 *            }
 *         }
 * */