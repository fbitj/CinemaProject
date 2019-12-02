package TTT;

import com.stylefeng.guns.rest.common.persistence.model.MoocOrderT;
import com.stylefeng.guns.rest.common.persistence.dao.MoocOrderTMapper;
import TTT.IMoocOrderTService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单信息表 服务实现类
 * </p>
 *
 * @author nathan
 * @since 2019-12-02
 */
@Service
public class MoocOrderTServiceImpl extends ServiceImpl<MoocOrderTMapper, MoocOrderT> implements IMoocOrderTService {

}
