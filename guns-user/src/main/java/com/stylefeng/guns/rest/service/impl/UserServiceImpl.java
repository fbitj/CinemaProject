package com.stylefeng.guns.rest.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.guns.bo.UserInfoBO;
import com.guns.service.user.UserService;
import com.guns.utils.Md5Utils;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeUserTMapper;
import com.stylefeng.guns.rest.common.persistence.model.MtimeUserT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Service(interfaceClass = UserService.class)
public class UserServiceImpl implements UserService {

    @Autowired
    private MtimeUserTMapper mtimeUserTMapper;

    @Override
    public boolean isUsernameExist(String username) {
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("user_name", username);
        Integer count = mtimeUserTMapper.selectCount(wrapper);
        if (count==0){
            return false;
        }
        return true;
    }

    @Override
    public int userRegist(UserInfoBO userInfoBO) {
        MtimeUserT mtimeUserT = new MtimeUserT();
        mtimeUserT.setUserName(userInfoBO.getUsername());
        // 对密码进行MD5加密
        String encryptPwd = Md5Utils.getDefaultMd5Encrypt(userInfoBO.getPassword());
        mtimeUserT.setUserPwd(encryptPwd);
        mtimeUserT.setEmail(userInfoBO.getEmail());
        mtimeUserT.setUserPhone(userInfoBO.getMobile());
        mtimeUserT.setAddress(userInfoBO.getAddress());
        Integer insert = mtimeUserTMapper.insert(mtimeUserT);
        return insert;
    }
}
