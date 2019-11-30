package com.stylefeng.guns.rest.service.Impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.guns.bo.UserInfoBO;
import com.guns.service.user.UserService;
import com.guns.utils.Md5Utils;
import com.guns.vo.UserCacheVO;
import com.guns.vo.UserInfoVo;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeUserTMapper;
import com.stylefeng.guns.rest.common.persistence.model.MtimeUserT;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author zhu rui
 * @version 1.0
 * @date 2019/11/28 22:18
 */
@Component
@Service(interfaceClass = UserService.class)
public class UserServiceImpl implements UserService {

    @Autowired
    MtimeUserTMapper mtimeUserTMapper;

    //private MtimeUserTMapper mtimeUserTMapper;

    @Override
    public boolean isUsernameExist(String username) {
        EntityWrapper<MtimeUserT> wrapper = new EntityWrapper<>();
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

    @Override
    public UserInfoVo selectUserInfoVo(String username) {
        UserInfoVo userInfoVo = new UserInfoVo();
        EntityWrapper<MtimeUserT> wrapper = new EntityWrapper<>();
        List<MtimeUserT> mtimeUserTList = mtimeUserTMapper.selectList(wrapper.eq("user_name", username));
        if (!CollectionUtils.isEmpty(mtimeUserTList)){
            for (MtimeUserT mtimeUserT : mtimeUserTList) {
                userInfoVo.setUuid(mtimeUserT.getUuid());
                userInfoVo.setUsername(mtimeUserT.getUserName());
                userInfoVo.setAddress(mtimeUserT.getAddress());
                userInfoVo.setEmail(mtimeUserT.getEmail());
                userInfoVo.setPhone(mtimeUserT.getUserPhone());
                userInfoVo.setBiography(mtimeUserT.getBiography());
                userInfoVo.setHeadAddress(mtimeUserT.getHeadUrl());
                userInfoVo.setBirthday(mtimeUserT.getBirthday());
                userInfoVo.setBeginTime(mtimeUserT.getBeginTime().getTime());
                userInfoVo.setNickname(mtimeUserT.getNickName());
                userInfoVo.setLifeState(mtimeUserT.getLifeState());
                userInfoVo.setSex(mtimeUserT.getUserSex());
                userInfoVo.setUpdateTime(mtimeUserT.getUpdateTime().getTime());
            }
        }
        return userInfoVo;
    }

    @Override
    public int updateUserInfo(UserInfoVo userInfoVo) {

        MtimeUserT mtimeUserT = new MtimeUserT();
        mtimeUserT.setUuid(userInfoVo.getUuid());
        mtimeUserT.setUserName(userInfoVo.getUsername());
        mtimeUserT.setAddress(userInfoVo.getAddress());
        mtimeUserT.setEmail(userInfoVo.getEmail());
        mtimeUserT.setUserPhone(userInfoVo.getPhone());
        mtimeUserT.setBiography(userInfoVo.getBiography());
        mtimeUserT.setHeadUrl(userInfoVo.getHeadAddress());
        mtimeUserT.setBirthday(userInfoVo.getBirthday());
        mtimeUserT.setNickName(userInfoVo.getNickname());
        mtimeUserT.setLifeState(userInfoVo.getLifeState());
        mtimeUserT.setUserSex(userInfoVo.getSex());
        Integer integer = mtimeUserTMapper.updateAllColumnById(mtimeUserT);
        return integer;
    }

    @Override
    public UserCacheVO login(String userName, String password) {
        EntityWrapper<MtimeUserT> wrapper = new EntityWrapper<>();
        wrapper.eq("user_name", userName);
        wrapper.eq("user_pwd", password);
        List<MtimeUserT> mtimeUserTS = mtimeUserTMapper.selectList(wrapper);
        // 没有查询出用户数据返回null
        if (CollectionUtils.isEmpty(mtimeUserTS)) {
            return null;
        }
        // 返回UserInfoVO用户数据
        UserCacheVO userCacheVO = new UserCacheVO();
        BeanUtils.copyProperties(mtimeUserTS.get(0), userCacheVO);
        return userCacheVO;
    }
}
