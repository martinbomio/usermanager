package edu.umflix.usermanager;


import edu.umflix.authenticationhandler.exceptions.InvalidTokenException;
import edu.umflix.model.User;
import edu.umflix.usermanager.exceptions.*;


public interface UserManager {

    /**
     * Registers a new user into the system
     * @param user the user to register
     * @throws InvalidEmailException thrown if the email is not valid
     * @throws InvalidPasswordException thrown if the password is not valid
     * @throws InvalidRoleException thrown if the role is not valid
     */
    public void register(User user) throws InvalidEmailException, InvalidPasswordException, InvalidRoleException;

    /**
     * Logs a user into the system
     * @param user the user to log
     * @return the token of the session
     * @throws InvalidUserException thrown if the user is not registered into the system
     */
    public String login(User user) throws InvalidUserException;

    /**
     * Deletes a user from the system
     * @param token the token of the session
     * @param user the user that needs to be deleted
     * @throws InvalidUserException thrown if the user is not registered into the system
     * @throws PermissionDeniedException thrown if the user that corresponds to the token is not allowed to remove the user
     * @throws InvalidTokenException thrown if the token is not a valid one
     */
    public void delete(String token,User user) throws InvalidUserException, PermissionDeniedException, InvalidTokenException;

    /**
     * Updates the password of a user
     * @param token the token of the session
     * @param user the user that needs it's password updated
     * @param newPassword the new password
     * @throws InvalidUserException thrown if the user is not registered into the system
     * @throws PermissionDeniedException thrown if the user that corresponds to the token is not allowed to do this action
     * @throws InvalidPasswordException thrown if the newPassword is not a valid one
     * @throws InvalidTokenException thrown if the token is not a valid one
     */
    public void update(String token, User user, String newPassword) throws InvalidUserException, PermissionDeniedException, InvalidPasswordException, InvalidTokenException;


}
