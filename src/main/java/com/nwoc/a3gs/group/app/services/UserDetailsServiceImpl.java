package com.nwoc.a3gs.group.app.services;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Customer;
import com.braintreegateway.CustomerRequest;
import com.braintreegateway.Result;
import com.nwoc.a3gs.group.app.dto.PasswordResetTokenDTO;
import com.nwoc.a3gs.group.app.dto.ResetPasswordDTO;
import com.nwoc.a3gs.group.app.dto.UserDTO;
import com.nwoc.a3gs.group.app.exceptions.UserNameUsedException;
import com.nwoc.a3gs.group.app.model.PasswordResetToken;
import com.nwoc.a3gs.group.app.model.Role;
import com.nwoc.a3gs.group.app.model.RoleName;
import com.nwoc.a3gs.group.app.model.User;
import com.nwoc.a3gs.group.app.repository.PasswordResetTokenRepository;
import com.nwoc.a3gs.group.app.repository.RoleRepository;
import com.nwoc.a3gs.group.app.repository.UserRepository;

import javassist.NotFoundException;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	
	@Autowired
	private Environment env;
	
	@Autowired
	MailServiceImpl mailServiceImpl;

	@Autowired
    UserRepository userRepository;
	
	@Autowired
	PasswordResetTokenRepository passwordResetTokenRepository;
	
	@Autowired
	PasswordEncoder encoder;
	
	@Autowired
	BraintreeGateway gateWay;
	private static final Logger LOGGER = LogManager.getLogger(UserDetailsServiceImpl.class);
	
	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		User user = userRepository.findByUsername(username).orElseThrow(
				() -> new UsernameNotFoundException("User Not Found with -> username or email : " + username));

		return UserPrinciple.build(user);
	}
	
	
	@Autowired
	RoleRepository roleRepository;

	@Transactional
	public boolean save(UserDTO userDTO) throws NotFoundException, UserNameUsedException {
		
		String enabled = env.getProperty("user.mail.enabled"); 
		int active = Integer.parseInt(enabled);
		boolean isSave = false;
		userDTO.setPassword(encoder.encode(userDTO.getPassword()));
        User usr =new User();
        BeanUtils.copyProperties(userDTO, usr);
        if(userDTO.getRoles()!=null){
        	try{
        		Set<Role> roles= userDTO.getRoles().stream().map(x->roleRepository.findByName(RoleName.ROLE_USER).get()).collect(Collectors.toSet());
        		usr.setRoles(roles);
        	}catch (NoSuchElementException e) {
				throw new NotFoundException("Role not found");
			}
			
		}
        if(createCustomerInBrainTree(usr)){
        	LOGGER.info("brain tree Customer has been create for user {}",usr.getUsername());
        }else{
        	LOGGER.warn("Brain tree customer creation failed for user {}",usr.getUsername());
        	
        }
        
        Optional<User> userOpt =userRepository.findByUsername(userDTO.getUsername());
        if(userOpt.isPresent()){
        	throw new UserNameUsedException("username already in use");
        }
        
		 if( userRepository.save(usr) != null) {
			 isSave = true;
			 if(active == 1) {
			 mailServiceImpl.sendMail(userDTO);
			 LOGGER.info("Mail Sended to User");
			 }
			 else
			 {
				 LOGGER.info("Mail Functionality disabled");
			 }
			 return isSave;
		 }
		 else {
			 LOGGER.error("Customer creation in brain tree failed");
			 return isSave;
		 }
			 
		 }
	
	public User update(UserDTO userDTO, Long id) throws NotFoundException {
		Optional<User> usrOpt =findOne(id);
		if(usrOpt.isPresent()){
			User usr = usrOpt.get();
			if((userDTO.getPassword() == "") || (userDTO.getPassword() == null )) {
				usr.setPassword(usr.getPassword());
			}
			else
			{
				usr.setPassword(encoder.encode(userDTO.getPassword()));
			}
			usr.setName(userDTO.getName());
			usr.setPhone(userDTO.getPhone());
			usr.setAge(userDTO.getAge());
			usr.setLocation(userDTO.getLocation());
			//usr.setUsername(userDTO.getUsername());
			if(userDTO.getRoles()!=null){
				try{
					Set<Role> roles= userDTO.getRoles().stream().map(x->roleRepository.findByName(x.getName()).get()).collect(Collectors.toSet());
					usr.setRoles(roles);
	        	}catch (NoSuchElementException e) {
					throw new NotFoundException("Role not found");
				}	
			}
			if((userDTO.getEmail()).equals(usr.getEmail()) )
			{
				usr.setEmail(userDTO.getEmail());	
			}
			else
			{
				throw new NotFoundException("Email Cannot modify....");
			}
			if((userDTO.getUsername()).equals(usr.getUsername()) )
			{
				usr.setUsername(userDTO.getUsername());	
			}
			else
			{
				throw new NotFoundException("UserName Cannot modify....");
			}
			
			return userRepository.saveAndFlush(usr);
		}else{
			throw new NotFoundException("User not found exception");
		}
		
	}
	
    public User reset(ResetPasswordDTO resetPasswordDTO) throws NotFoundException {
		
		Optional<User> userOpt= userRepository.findByUsername(resetPasswordDTO.getUserName());
		if(!userOpt.isPresent()){
			throw new NotFoundException("User not found");
		}
		User usr = userOpt.get();
		String pass = resetPasswordDTO.getOldpassWord();
			if(encoder.matches(pass, usr.getPassword())) {
				usr.setPassword(encoder.encode(resetPasswordDTO.getNewPassword()));
				usr= userRepository.saveAndFlush(usr);
				return usr;
			}else{
				throw new NotFoundException("Old PassWord is not correct");
			}
		}

	public List<User> findAll() {
		return userRepository.findAll();
	}

	public Optional<User> findOne(Long id) {
		return userRepository.findById(id);
	}

	public void delete(User user) {
		userRepository.delete(user);
	}
	
	public User findUserByEmail(String email) {
		return userRepository.findUserByEmail(email);
	}
	
	public PasswordResetToken findUserByToken(String token) {
		return passwordResetTokenRepository.findByToken(token);
	}
	
	public PasswordResetToken createPasswordResetTokenForUser(User user, String token) {
	    PasswordResetToken myToken = new PasswordResetToken(token, user);
	    myToken.setExpiryDate(30);
	    return passwordResetTokenRepository.save(myToken);
	}

	public Page<User> findUserByPages(int pageNumber, int size) {
		Pageable pageable = new PageRequest(pageNumber, size);

		return userRepository.findAll(pageable);
	}
	
	public Boolean createCustomerInBrainTree(User user){
		try {
			CustomerRequest request = new CustomerRequest()
					  .firstName(user.getName())
					  .email(user.getEmail())
					  .phone(user.getPhone());
					Result<Customer> result = gateWay.customer().create(request);

					if(result.isSuccess()){
						user.setBtCusId(result.getTarget().getId());
						return true;
					}
		} catch (Exception e) {
			LOGGER.error("Customer creation in brain tree failed",e);
		}
		
				return false;
	}
	
	public boolean sendForgotMail(String token, User user)
	{
		boolean isSend = false;
		 if(mailServiceImpl.sendForgotPasswordMail(token, user)) {
			 isSend = true; 
		 }
		 
		return isSend;
		
	}
	  public User resetPass(String user, ResetPasswordDTO resetPasswordDTO) throws NotFoundException {
			
			Optional<User> userOpt= userRepository.findByUsername(user);
			if(!userOpt.isPresent()){
				throw new NotFoundException("User not found");
			}
			User usr = userOpt.get();
			String pass = resetPasswordDTO.getNewPassword();
			String confirm = resetPasswordDTO.getConfirnpass();
				if(pass.equalsIgnoreCase(confirm)) {
					usr.setPassword(encoder.encode(pass));
					usr= userRepository.saveAndFlush(usr);
					return usr;
				}else{
					throw new NotFoundException("Confirm Password is not match with new password");
				}
			}
	  public void deleteById(Long id) {
		  passwordResetTokenRepository.deleteById(id);
		}

	public Optional<User> findbyUsername(String username){
		Optional<User> userOpt= userRepository.findByUsername(username);
		return userOpt;
	}
}