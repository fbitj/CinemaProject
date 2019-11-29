package com.guns.service.user;


import com.guns.bo.UserInfoBO;
import com.guns.vo.UserInfoVo;

/**
 * @author zhu rui
 * @version 1.0
 * @date 2019/11/28 21:28
 */

public interface UserService {

    boolean isUsernameExist(String username);

    int userRegist(UserInfoBO userInfoBO);

    UserInfoVo selectUserInfoVo(String username);

    int updateUserInfo(UserInfoVo userInfoVo);

}
