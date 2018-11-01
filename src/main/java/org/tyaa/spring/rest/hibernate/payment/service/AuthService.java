package org.tyaa.spring.rest.hibernate.payment.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tyaa.spring.rest.hibernate.payment.dao.RoleHibernateDAO;
import org.tyaa.spring.rest.hibernate.payment.dao.UserHibernateDAO;
import org.tyaa.spring.rest.hibernate.payment.entity.Role;
import org.tyaa.spring.rest.hibernate.payment.entity.User;
import org.tyaa.spring.rest.hibernate.payment.model.AbstractResponse;
import org.tyaa.spring.rest.hibernate.payment.model.AccountInfo;
import org.tyaa.spring.rest.hibernate.payment.model.AccountInfoResponse;
import org.tyaa.spring.rest.hibernate.payment.model.UserRequest;

@Service
@Transactional
public class AuthService {

    @Autowired
    private RoleHibernateDAO roleDao;

    @Autowired
    private UserHibernateDAO userDao;

    public AbstractResponse<Object> createRole(Role _role) {

        AbstractResponse<Object> response
                = new AbstractResponse<Object>() {
        };
        try {
            roleDao.create(_role);
            response.setStatus("success");
            response.setMessage(
                    String.format("Role %s created", _role.getName())
            );
        } catch (Exception ex) {
            response.setStatus("error");
            response.setMessage(
                    String.format(
                            "Role %s wasn't created: %s",
                             _role.getName(),
                             ex.getMessage()
                    )
            );
        }

        return response;
    }

    public AbstractResponse<Object> createUser(UserRequest _userRequest) {

        AbstractResponse<Object> response
                = new AbstractResponse<Object>() {
        };
        try {
            Role role = roleDao.get(_userRequest.getRole_id());
            User newUser
                    = userDao.create(
                            new User(
                                    _userRequest.getName(),
                                     _userRequest.getPassword(),
                                     role
                            )
                    );

            response.setStatus("success");
            response.setMessage(
                    String.format("User %s created", newUser.getName())
            );
        } catch (Exception ex) {
            response.setStatus("error");
            response.setMessage(
                    String.format(
                            "User %s wasn't created: %s",
                             _userRequest.getName(),
                             ex.getMessage()
                    )
            );
        }

        return response;
    }

    public AccountInfoResponse getAccountInfoResponse(UserRequest _userRequest) {

        AccountInfoResponse response = new AccountInfoResponse();
        try {
            User user
                    = userDao.getUserByName(_userRequest.getName());
            if (user == null || !user.getPassword().equals(_userRequest.getPassword())) {

                response.setStatus("error");
                response.setMessage(
                        String.format("User %s not found or password is incorrect", _userRequest.getName())
                );
            } else {

                response.setStatus("success");
                response.setMessage(
                        String.format("User %s found", _userRequest.getName())
                );
                response.setData(
                        new AccountInfo(user.getName(), user.getRole().getId())
                );
            }
        } catch (Exception ex) {
            response.setStatus("error");
            response.setMessage(
                    String.format(
                            "Error: %s",
                             ex.getMessage()
                    )
            );
        }

        return response;
    }

    public AbstractResponse<Object> signOut() {

        AbstractResponse<Object> response
                = new AbstractResponse<Object>() {
        };
        response.setStatus("success");
        response.setMessage("signout");
        return response;
    }
    
    public AccountInfoResponse check(AccountInfo accountInfo) {

        AccountInfoResponse response = new AccountInfoResponse();
        if (accountInfo != null) {
            response.setStatus("success");
            response.setMessage(
                String.format("User %s signed in", accountInfo.userName)
            );
            response.setData(
                new AccountInfo(accountInfo.userName, accountInfo.roleId)
            );
        } else {
            response.setStatus("success");
            response.setMessage("User is a guest");
        }
        return response;
    }
}
