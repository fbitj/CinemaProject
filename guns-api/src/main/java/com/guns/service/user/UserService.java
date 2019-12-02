package com.guns.service.user;

import com.guns.bo.UserInfoBO;
import com.guns.dto.RegisterRequest;
import com.guns.vo.BaseRespVO;


public interface UserService {

    boolean isUsernameExist(String username);

    int userRegist(UserInfoBO userInfoBO);

    BaseRespVO register(RegisterRequest registerRequest);

    BaseRespVO login(String username, String password);

}
