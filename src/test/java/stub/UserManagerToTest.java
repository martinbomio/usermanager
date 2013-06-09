package stub;

import edu.umflix.authenticationhandler.AuthenticationHandler;
import edu.umflix.persistence.RoleDao;
import edu.umflix.persistence.UserDao;
import edu.umflix.usermanager.impl.UserManagerImpl;

import javax.ejb.EJB;

/**
 *
 */
public class UserManagerToTest extends UserManagerImpl {

    public void setUserDao(UserDao userDao) {
        super.userDao = userDao;
    }

    public void setRoleDao(RoleDao roleDao) {
        super.roleDao = roleDao;
    }

    public void setAuthenticationHandler(AuthenticationHandler authenticationHandler) {
        super.authenticationHandler = authenticationHandler;
    }
}
