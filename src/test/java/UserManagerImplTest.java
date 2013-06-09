import edu.umflix.authenticationhandler.AuthenticationHandler;
import edu.umflix.authenticationhandler.exceptions.InvalidTokenException;
import edu.umflix.authenticationhandler.exceptions.InvalidUserException;
import edu.umflix.exceptions.RoleNotFoundException;
import edu.umflix.exceptions.UserNotFoundException;
import edu.umflix.model.Role;
import edu.umflix.model.User;
import edu.umflix.persistence.RoleDao;
import edu.umflix.persistence.UserDao;
import edu.umflix.usermanager.exceptions.*;
import edu.umflix.usermanager.impl.UserManagerImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import stub.UserManagerToTest;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

/**
 * Tests for UserManagerImpl
 */
public class UserManagerImplTest {

    UserManagerImpl userManager;
    UserDao userDao;
    RoleDao roleDao;
    AuthenticationHandler authenticationHandler;
    UserNotFoundException userNotFoundException;
    Role userRole;
    Role adminRole;

    @Before
    public void prepare() throws UserNotFoundException, RoleNotFoundException {

        //mockException
        userNotFoundException = mock(UserNotFoundException.class);

        //mockUserDao
        userDao = mock(UserDao.class);

        //mockRoleDao
        roleDao = mock(RoleDao.class);
        userRole = mockRole(Long.valueOf(3));
        adminRole = mockRole(Long.valueOf(1));
        when(roleDao.getRoleById(Long.valueOf(3))).thenReturn(userRole);
        when(roleDao.getRoleById(Long.valueOf(1))).thenReturn(adminRole);

        //mockAuthenticationHandler
        authenticationHandler = mock(AuthenticationHandler.class);

        //mockUserManager
        UserManagerToTest userManagerToTest = new UserManagerToTest();
        userManagerToTest.setUserDao(userDao);
        userManagerToTest.setRoleDao(roleDao);
        userManagerToTest.setAuthenticationHandler(authenticationHandler);
        userManager = userManagerToTest;


    }


    private Role mockRole(Long id) {
        Role role = mock(Role.class);
        when(role.getId()).thenReturn(Long.valueOf(id));
        return role;
    }


    private User mockUser(String email, String password, String name, Role role) {
        User user = mock(User.class);
        when(user.getEmail()).thenReturn(email);
        when(user.getName()).thenReturn(name);
        when(user.getPassword()).thenReturn(password);
        when(user.getRole()).thenReturn(role);
        return user;
    }

    @Test
    public void testRegisterValidUser() {
        try {
            User unregisteredValidUser = mockUser("unregistered@gmail.com", "unregistered@password@123", "unregisteredName", userRole);
            when(userDao.getUser("unregistered@gmail.com")).thenThrow(userNotFoundException);
            userManager.register(unregisteredValidUser);
            verify(userDao, times(1)).createUser(unregisteredValidUser);
        } catch (InvalidEmailException e) {
            fail();
        } catch (InvalidPasswordException e) {
            fail();
        } catch (InvalidRoleException e) {
            fail();
        } catch (EmailAlreadyTakenException e) {
            fail();
        } catch (UserNotFoundException e) {
            fail();
        }
    }

    @Test
    public void testRegisterNullUser() {
        try {
            userManager.register(null);
        } catch (IllegalArgumentException e) {
            verify(userDao, times(0)).createUser(any(User.class));
        } catch (InvalidEmailException e) {
            fail();
        } catch (InvalidPasswordException e) {
            fail();
        } catch (InvalidRoleException e) {
            fail();
        } catch (EmailAlreadyTakenException e) {
            fail();
        }
    }

    @Test
    public void testRegisterInvalidMail() {
        User user = mockUser(null, "passwd", "name", userRole);
        try {
            userManager.register(user);
        } catch (InvalidEmailException e) {
            verify(userDao, times(0)).createUser(any(User.class));
        } catch (InvalidPasswordException e) {
            fail();
        } catch (InvalidRoleException e) {
            fail();
        } catch (EmailAlreadyTakenException e) {
            fail();
        }
    }

    @Test
    public void testRegisterInvalidPassword() {
        User user = mockUser("invalid@hotmail.com", null, "name", userRole);
        try {
            when(userDao.getUser("invalid@hotmail.com")).thenReturn(user);
            userManager.register(user);
        } catch (UserNotFoundException e) {
            fail();
        } catch (InvalidEmailException e) {
            fail();
        } catch (InvalidPasswordException e) {
            verify(userDao, times(0)).createUser(any(User.class));
        } catch (InvalidRoleException e) {
            fail();
        } catch (EmailAlreadyTakenException e) {
            fail();
        }
    }

    @Test
    public void testRegisterNullRole() {
        User user = mockUser("invalid@hotmail.com", "passwd", "name", null);
        try {
            when(userDao.getUser("invalid@hotmail.com")).thenReturn(user);
            userManager.register(user);
        } catch (UserNotFoundException e) {
            fail();
        } catch (InvalidEmailException e) {
            fail();
        } catch (InvalidPasswordException e) {
            fail();
        } catch (InvalidRoleException e) {
            verify(userDao, times(0)).createUser(any(User.class));
        } catch (EmailAlreadyTakenException e) {
            fail();
        }
    }

    @Test
    public void testRegisterRoleNotFound() {
        try {
            Role invalidRole = mockRole(Long.valueOf(10));
            User user = mockUser("invalid@hotmail.com", "passwd", "name", invalidRole);
            when(roleDao.getRoleById(Long.valueOf(10))).thenThrow(mock(RoleNotFoundException.class));
            when(userDao.getUser("invalid@hotmail.com")).thenReturn(user);
            userManager.register(user);
        } catch (UserNotFoundException e) {
            fail();
        } catch (InvalidEmailException e) {
            fail();
        } catch (InvalidPasswordException e) {
            fail();
        } catch (InvalidRoleException e) {
            verify(userDao, times(0)).createUser(any(User.class));
        } catch (EmailAlreadyTakenException e) {
            fail();
        } catch (RoleNotFoundException e) {
            fail();
        }
    }

    @Test
    public void testRegisterRoleNotTheSameWithStored() {
        try {
            Role invalidRole = mockRole(Long.valueOf(10));
            Role storedRole = mockRole(Long.valueOf(10));
            User user = mockUser("invalid@hotmail.com", "passwd", "name", invalidRole);
            when(roleDao.getRoleById(Long.valueOf(10))).thenReturn(storedRole);
            when(userDao.getUser("invalid@hotmail.com")).thenReturn(user);
            userManager.register(user);
        } catch (UserNotFoundException e) {
            fail();
        } catch (InvalidEmailException e) {
            fail();
        } catch (InvalidPasswordException e) {
            fail();
        } catch (InvalidRoleException e) {
            verify(userDao, times(0)).createUser(any(User.class));
        } catch (EmailAlreadyTakenException e) {
            fail();
        } catch (RoleNotFoundException e) {
            fail();
        }
    }

    @Test
    public void testRegisterEmailAlreadyTaken() {
        try {
            User user = mockUser("email@hotmail.com", "passwd", "name", userRole);
            when(userDao.getUser("email@hotmail.com")).thenReturn(user);
            userManager.register(user);
        } catch (UserNotFoundException e) {
            fail();
        } catch (InvalidEmailException e) {
            fail();
        } catch (InvalidPasswordException e) {
            fail();
        } catch (InvalidRoleException e) {
            fail();
        } catch (EmailAlreadyTakenException e) {
            verify(userDao, times(0)).createUser(any(User.class));
        }
    }

    @Test
    public void testLoginValidUser() {
        try {
            User user = mockUser("email@hotmail.com", "passwd", null, null);
            when(authenticationHandler.authenticate(user)).thenReturn("tokenUser");
            assertTrue(("tokenUser").equals(userManager.login(user)));
        } catch (InvalidUserException e) {
            fail();
        }
    }

    @Test
    public void testLoginInvalidUser() {
        try {
            User user = mockUser("email@hotmail.com", "passwd", null, null);
            InvalidUserException invalidUserException = mock(InvalidUserException.class);
            when(authenticationHandler.authenticate(user)).thenThrow(invalidUserException);
            userManager.login(user);
            fail();
        } catch (InvalidUserException e) {
            try {
                verify(authenticationHandler, times(1)).authenticate(any(User.class));
            } catch (InvalidUserException e1) {
                fail();
            }
        }
    }

    @Test
    public void testLoginNullUser() {
        try {
            User user = null;
            InvalidUserException invalidUserException = mock(InvalidUserException.class);
            when(authenticationHandler.authenticate(user)).thenThrow(invalidUserException);
            userManager.login(user);
            fail();
        } catch (InvalidUserException e) {
            try {
                verify(authenticationHandler, times(0)).authenticate(any(User.class));
            } catch (InvalidUserException e1) {
                fail();
            }
        }
    }

    @Test
    public void testValidUserDelete() {
        try {
            User user = mockUser("a@e.c", "123", null, null);
            User storedUser = mockUser("a@e.c", "123", "name", userRole);
            when(authenticationHandler.validateToken("userToken")).thenReturn(true);
            when(authenticationHandler.getUserOfToken("userToken")).thenReturn(storedUser);
            userManager.delete("userToken", user);
            verify(userDao, times(1)).deleteUser(user.getEmail());
        } catch (InvalidTokenException e) {
            fail();
        } catch (InvalidUserException e) {
            fail();
        } catch (PermissionDeniedException e) {
            fail();
        } catch (UserNotFoundException e) {
            fail();
        }
    }

    @Test
    public void testValidAdminDelete() {
        try {
            User user = mockUser("a@e.c", "123", null, null);
            User storedUser = mockUser("a@e.c", "123", "name", userRole);
            User admin = mockUser("admin@gmail.com","adminPassword","adminName",adminRole);
            when(authenticationHandler.validateToken("adminToken")).thenReturn(true);
            when(authenticationHandler.getUserOfToken("adminToken")).thenReturn(admin);
            userManager.delete("adminToken", user);
            verify(userDao, times(1)).deleteUser(user.getEmail());
        } catch (InvalidTokenException e) {
            fail();
        } catch (InvalidUserException e) {
            fail();
        } catch (PermissionDeniedException e) {
            fail();
        } catch (UserNotFoundException e) {
            fail();
        }
    }

    @Test
    public void testAdminRoleNotFoundDelete() {
        try {
            User user = mockUser("a@e.c", "123", null, null);
            User storedUser = mockUser("a@e.c", "123", "name", userRole);
            User admin = mockUser("admin@gmail.com","adminPassword","adminName",adminRole);
            when(authenticationHandler.validateToken("adminToken")).thenReturn(true);
            when(authenticationHandler.getUserOfToken("adminToken")).thenReturn(admin);
            when(roleDao.getRoleById(Long.valueOf(1))).thenThrow(mock(RoleNotFoundException.class));
            userManager.delete("adminToken", user);
        } catch (InvalidTokenException e) {
            fail();
        } catch (InvalidUserException e) {
            fail();
        } catch (PermissionDeniedException e) {
            fail();
        } catch (RoleNotFoundException e) {
            fail();
        }  catch (IllegalArgumentException e){
            try {
                verify(userDao, times(0)).deleteUser("a@e.c");
            } catch (UserNotFoundException e1) {
                fail();
            }
        }
    }

    @Test
    public void testInvalidUserDelete() {
        try {
            User user = mockUser("a@e.c", "123", null, null);
            User admin = mockUser("admin@gmail.com", "adminPassword", "adminName", adminRole);
            when(authenticationHandler.validateToken("adminToken")).thenReturn(true);
            when(authenticationHandler.getUserOfToken("adminToken")).thenReturn(admin);
            doThrow(mock(UserNotFoundException.class)).when(userDao).deleteUser("a@e.c");
            userManager.delete("adminToken", user);
        } catch (InvalidTokenException e) {
            fail();
        } catch (InvalidUserException e) {
            try {
                verify(userDao, times(1)).deleteUser("a@e.c");
            } catch (UserNotFoundException e1) {
                fail();
            }
        } catch (PermissionDeniedException e) {
            fail();
        } catch (UserNotFoundException e) {
            fail();
        }
    }

    @Test
    public void testPermissionDeniedDelete() {
        try {
            User user = mockUser("a@e.c", "123", null, null);
            User admin = mockUser("user@gmail.com", "userPassword", "userName", userRole);
            when(authenticationHandler.validateToken("userToken")).thenReturn(true);
            when(authenticationHandler.getUserOfToken("userToken")).thenReturn(admin);
            userManager.delete("userToken", user);
            verify(userDao, times(1)).deleteUser(user.getEmail());
        } catch (InvalidTokenException e) {
            fail();
        } catch (InvalidUserException e) {
            fail();
        } catch (PermissionDeniedException e) {
            try {
                verify(userDao, times(0)).deleteUser("a@e.c");
            } catch (UserNotFoundException e1) {
                fail();
            }
        } catch (UserNotFoundException e) {
            fail();
        }
    }

    @Test
    public void testNullUserDelete() {
        try {
            User user = null;
            User admin = mockUser("admin@gmail.com", "adminPassword", "adminName", adminRole);
            when(authenticationHandler.validateToken("adminToken")).thenReturn(true);
            when(authenticationHandler.getUserOfToken("adminToken")).thenReturn(admin);
            userManager.delete("adminToken", user);
        } catch (InvalidTokenException e) {
            fail();
        } catch (InvalidUserException e) {
            try {
                verify(userDao, times(0)).deleteUser(any(String.class));
            } catch (UserNotFoundException e1) {
                fail();
            }
        } catch (PermissionDeniedException e) {
            fail();
        }
    }

    @Test
    public void testNullTokenDelete() {
        try {
            User user= mockUser("admin@gmail.com", "adminPassword", null, null);
            userManager.delete(null, user);
            verify(userDao, times(1)).deleteUser(user.getEmail());
        } catch (InvalidTokenException e) {
            try {
                verify(userDao, times(0)).deleteUser(any(String.class));
            } catch (UserNotFoundException e1) {
                fail();
            }
        } catch (InvalidUserException e) {
            fail();
        } catch (PermissionDeniedException e) {
            fail();
        } catch (UserNotFoundException e) {
            fail();
        }
    }

    @Test
    public void testValidUserUpdate(){
        try{
        User user = mockUser("user@gmail.com","password",null,null);
        User storedUser = mockUser("user@gmail.com", "password", "name", userRole);
        when(authenticationHandler.validateToken("userToken")).thenReturn(true);
        when(authenticationHandler.getUserOfToken("userToken")).thenReturn(storedUser);
        when(userDao.getUser("user@gmail.com")).thenReturn(storedUser);
        userManager.update("userToken",user,"newPassword");
        verify(userDao, times(1)).updateUser(storedUser);
        }catch (InvalidTokenException e) {
            fail();
        } catch (InvalidUserException e) {
            fail();
        } catch (PermissionDeniedException e) {
            fail();
        } catch (InvalidPasswordException e) {
            fail();
        } catch (UserNotFoundException e) {
            fail();
        }
    }

    @Test
    public void testAdminRoleNotFoundUpdate(){
        try{
            User user = mockUser("user@gmail.com","password",null,null);
            User storedUser = mockUser("user@gmail.com", "password", "name", userRole);
            when(authenticationHandler.validateToken("userToken")).thenReturn(true);
            when(authenticationHandler.getUserOfToken("userToken")).thenReturn(storedUser);
            when(userDao.getUser("user@gmail.com")).thenReturn(storedUser);
            when(roleDao.getRoleById(Long.valueOf(1))).thenThrow(mock(RoleNotFoundException.class));
            userManager.update("userToken",user,"newPassword");
            verify(userDao, times(1)).updateUser(storedUser);
        }catch (InvalidTokenException e) {
            fail();
        } catch (InvalidUserException e) {
            fail();
        } catch (PermissionDeniedException e) {
            fail();
        } catch (InvalidPasswordException e) {
            fail();
        } catch (UserNotFoundException e) {
            fail();
        } catch (RoleNotFoundException e) {
            fail();
        }   catch (IllegalArgumentException e){
            try {
                verify(userDao, times(0)).updateUser(any(User.class));
            } catch (UserNotFoundException e1) {
                fail();
            }
        }
    }

    @Test
    public void testInvalidUserUpdate(){
        try{
            User user = mockUser("user@gmail.com","password",null,null);
            User storedUser = mockUser("user@gmail.com","password","name",userRole);
            when(authenticationHandler.validateToken("userToken")).thenReturn(true);
            when(authenticationHandler.getUserOfToken("userToken")).thenReturn(storedUser);
            doThrow(mock(UserNotFoundException.class)).when(userDao).getUser("user@gmail.com");
            doThrow(mock(UserNotFoundException.class)).when(userDao).updateUser(user);
            userManager.update("userToken",user,"newPassword");
            verify(userDao, times(1)).updateUser(user);
        }catch (InvalidTokenException e) {
            fail();
        } catch (InvalidUserException e) {
            try {
                verify(userDao, times(1)).getUser("user@gmail.com");
            } catch (UserNotFoundException e1) {
                fail();
            }
        } catch (PermissionDeniedException e) {
            fail();
        } catch (InvalidPasswordException e) {
            fail();
        } catch (UserNotFoundException e) {
            fail();
        }
    }

    @Test
    public void testPermissionDeniedUpdate(){
        try{
            User notAdminUser = mockUser("notAdmin@gmail.com","password",null,userRole);
            User user = mockUser("user@gmail.com","password",null,null);
            User storedUser = mockUser("user@gmail.com","password","name",userRole);
            when(authenticationHandler.validateToken("notAdminToken")).thenReturn(true);
            when(authenticationHandler.getUserOfToken("notAdminToken")).thenReturn(notAdminUser);
            when(userDao.getUser("user@gmail.com")).thenReturn(storedUser);
            userManager.update("notAdminToken", user, "newPassword");
        }catch (InvalidTokenException e) {
            fail();
        } catch (InvalidUserException e) {
            fail();
        } catch (PermissionDeniedException e) {
            try {
                verify(userDao, times(0)).updateUser(any(User.class));
            } catch (UserNotFoundException e1) {
                fail();
            }
        } catch (InvalidPasswordException e) {
            fail();
        } catch (UserNotFoundException e) {
            fail();
        }
    }

    @Test
    public void testNullPasswordUpdate(){
        try{
            User user = mockUser("user@gmail.com","password",null,null);
            User storedUser = mockUser("user@gmail.com","password","name",userRole);
            when(authenticationHandler.validateToken("userToken")).thenReturn(true);
            when(authenticationHandler.getUserOfToken("userToken")).thenReturn(storedUser);
            when(userDao.getUser("user@gmail.com")).thenReturn(storedUser);
            userManager.update("userToken", user, null);
        }catch (InvalidTokenException e) {
            fail();
        } catch (InvalidUserException e) {
            fail();
        } catch (PermissionDeniedException e) {
            fail();
        } catch (InvalidPasswordException e) {
            try {
                verify(userDao, times(0)).updateUser(any(User.class));
            } catch (UserNotFoundException e1) {
                fail();
            }
        } catch (UserNotFoundException e) {
            fail();
        }
    }

    @Test
    public void testNullUserUpdate(){
        try{
            User storedUser = mockUser("user@gmail.com","password","name",userRole);
            when(authenticationHandler.validateToken("userToken")).thenReturn(true);
            when(authenticationHandler.getUserOfToken("userToken")).thenReturn(storedUser);
            when(userDao.getUser("user@gmail.com")).thenReturn(storedUser);
            userManager.update("userToken", null, "newPassword");
        }catch (InvalidTokenException e) {
            fail();
        } catch (InvalidUserException e) {
            try {
                verify(userDao, times(0)).updateUser(any(User.class));
            } catch (UserNotFoundException e1) {
                fail();
            }
        } catch (PermissionDeniedException e) {
            fail();
        } catch (InvalidPasswordException e) {
            fail();
        } catch (UserNotFoundException e) {
            fail();
        }
    }

    @Test
    public void testNullTokenUpdate(){
        try{
            User user = mockUser("user@gmail.com","password",null,null);
            User storedUser = mockUser("user@gmail.com", "password", "name", userRole);
            when(userDao.getUser("user@gmail.com")).thenReturn(storedUser);
            userManager.update(null,user,"newPassword");
        }catch (InvalidTokenException e) {
            try {
                verify(userDao, times(0)).updateUser(any(User.class));
            } catch (UserNotFoundException e1) {
                fail();
            }
        } catch (InvalidUserException e) {
            fail();
        } catch (PermissionDeniedException e) {
            fail();
        } catch (InvalidPasswordException e) {
            fail();
        } catch (UserNotFoundException e) {
            fail();
        }
    }
}
