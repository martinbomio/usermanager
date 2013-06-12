package edu.umflix.usermanager.impl;

import edu.umflix.authenticationhandler.AuthenticationHandler;
import edu.umflix.authenticationhandler.exceptions.InvalidTokenException;
import edu.umflix.authenticationhandler.exceptions.InvalidUserException;
import edu.umflix.exceptions.RoleNotFoundException;
import edu.umflix.exceptions.UserNotFoundException;
import edu.umflix.model.Role;
import edu.umflix.model.User;
import edu.umflix.persistence.RoleDao;
import edu.umflix.persistence.UserDao;
import edu.umflix.usermanager.UserManager;
import edu.umflix.usermanager.exceptions.*;
import org.apache.log4j.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jws.WebService;

@Stateless(name = "UserManager")
@WebService(portName = "UserManagerPort",
        serviceName = "UserManagerWebService",
        targetNamespace = "http://um.org/wsdl")
public class UserManagerImpl implements UserManager{

    private static Logger logger = Logger.getLogger(UserManagerImpl.class);

    @EJB(beanName = "UserDao")
    protected UserDao userDao;

    @EJB(beanName = "RoleDao")
    protected RoleDao roleDao;

    @EJB(beanName = "AuthenticationHandler")
    protected AuthenticationHandler authenticationHandler;

    @Override
    public void register(User user) throws InvalidEmailException, InvalidPasswordException, InvalidRoleException, EmailAlreadyTakenException {
        if(user==null){
          logger.warn("registered ran with user null");
            throw new IllegalArgumentException("user null in register");
        }
        if(user.getEmail()==null){
            logger.warn("registered ran with email null");
            throw new InvalidEmailException();
        }
        if(user.getPassword()==null){
            logger.warn("registered ran with password null");
            throw new InvalidPasswordException();
        }
        if(user.getRole()==null){
            logger.warn("registered ran with role null");
            throw new InvalidRoleException();
        }
        try {
            //Check that the role is existent and the same with the persisted
            Role storedRole = roleDao.getRoleById(user.getRole().getId());
            if(!user.getRole().equals(storedRole)){
                logger.warn("registered ran with user that has a different role from stored");
                throw new InvalidRoleException();
            }
        } catch (RoleNotFoundException e) {
            logger.warn("registered ran with not existent role");
            throw new InvalidRoleException();
        }
        //Check the email is not taken
        try {
            userDao.getUser(user.getEmail());
            throw new EmailAlreadyTakenException();
        } catch (UserNotFoundException e) {
            userDao.createUser(user);
            logger.info("registered user "+user.getEmail());
        }
    }

    @Override
    public String login(User user) throws InvalidUserException {
        if(user==null){
            logger.warn("login ran with user null");
            throw new InvalidUserException();
        }
        String token = authenticationHandler.authenticate(user);
        logger.info("user "+user.getEmail()+" logged in");
        return token;
    }

    @Override
    public void delete(String token, User user) throws InvalidUserException, PermissionDeniedException, InvalidTokenException {
        if(token==null){
            logger.warn("delete ran with token null");
            throw new InvalidTokenException();
        }
        if(user==null){
            logger.warn("delete ran with user null");
            throw new InvalidUserException();
        }
        User userOfToken = authenticationHandler.getUserOfToken(token);
        try {
            if(userOfToken==null || userOfToken.getRole()==null){
                logger.debug("userOfToken or his role is null in delete");
                throw new IllegalArgumentException("userOfToken or his role is null in delete");
            }
            boolean tokenIsAdmin = userOfToken.getRole().equals(roleDao.getRoleById(Long.valueOf(1)));
            boolean tokenBelongsToDeletedUser = userOfToken.getPassword().equals(user.getPassword()) && userOfToken.getEmail().equals(user.getEmail());
            if(tokenIsAdmin || tokenBelongsToDeletedUser){
                 userDao.deleteUser(user.getEmail());
            }else{
                throw new PermissionDeniedException();
            }
        } catch (RoleNotFoundException e) {
            throw new IllegalArgumentException("role Admin not found in delete");
        } catch (UserNotFoundException e) {
            throw new InvalidUserException();
        }
    }

    @Override
    public void update(String token, User user, String newPassword) throws InvalidUserException, PermissionDeniedException, InvalidPasswordException, InvalidTokenException {
        if(token==null){
            logger.trace("update ran with token null");
            throw new InvalidTokenException();
        }
        if(user==null){
            logger.trace("update ran with user null");
            throw new InvalidUserException();
        }
        if(newPassword==null){
            logger.trace("update ran with newPassword null");
            throw new InvalidPasswordException();
        }
        User userOfToken = authenticationHandler.getUserOfToken(token);
        try {
            if(userOfToken==null || userOfToken.getRole()==null){
                logger.debug("userOfToken or his role is null in update");
                throw new IllegalArgumentException("userOfToken or his role is null in update");
            }
            boolean tokenIsAdmin = userOfToken.getRole().equals(roleDao.getRoleById(Long.valueOf(1)));
            boolean tokenBelongsToUpdatedUser = userOfToken.getPassword().equals(user.getPassword()) && userOfToken.getEmail().equals(user.getEmail());
            if(tokenIsAdmin || tokenBelongsToUpdatedUser){
                User updatedUser = userDao.getUser(user.getEmail());
                updatedUser.setPassword(newPassword);
                userDao.updateUser(updatedUser);
            }else{
                throw new PermissionDeniedException();
            }
        } catch (RoleNotFoundException e) {
            throw new IllegalArgumentException("role Admin not found in delete");
        } catch (UserNotFoundException e) {
            throw new InvalidUserException();
        }
    }


}
