package com.guns.service.user;

import com.guns.bo.UserInfoBO;

public interface UserService {

    boolean isUsernameExist(String username);

    int userRegist(UserInfoBO userInfoBO);
}
