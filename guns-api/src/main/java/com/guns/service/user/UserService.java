package com.guns.service.user;

import com.guns.bo.User;
import com.guns.bo.UserInfoBO;

public interface UserService {

    boolean isUsernameExist(String username);

    int userRegist(UserInfoBO userInfoBO);

    User checkUserIsExit(String userName, String password);
}
