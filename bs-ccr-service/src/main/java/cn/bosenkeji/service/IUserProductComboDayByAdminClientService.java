package cn.bosenkeji.service;

import cn.bosenkeji.config.FeignClientConfig;
import cn.bosenkeji.service.fallback.IUserProductComboDayByAdminClientServiceFallbackFactory;
import cn.bosenkeji.util.Result;
import cn.bosenkeji.vo.combo.UserProductComboDay;
import cn.bosenkeji.vo.combo.UserProductComboDayByAdmin;
import com.github.pagehelper.PageInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author xivin
 * @ClassName cn.bosenkeji.service
 * @Version V1.0
 * @create 2019-07-18 11:29
 */
@FeignClient(name = "bs-ccr-provider-product-combo",configuration = FeignClientConfig.class,fallbackFactory = IUserProductComboDayByAdminClientServiceFallbackFactory.class)
public interface IUserProductComboDayByAdminClientService {

    @PostMapping("/user_product_combo_day_by_admin/")
    Result add(@RequestBody UserProductComboDayByAdmin userProductComboDayByAdmin);

    @GetMapping("/user_product_combo_day_by_admin/list_by_user_product_combo_id")
    PageInfo listByUserProductComboId(@RequestParam(value="pageNum",defaultValue="1") int pageNum,
                                      @RequestParam(value="pageSize",defaultValue="15") int pageSize,
                                      @RequestParam("userProductComboId") int userProductComboId);

    @GetMapping("/user_product_combo_day_by_admin/")
    PageInfo list(@RequestParam("pageNum") int pageNum,@RequestParam("pageSize") int pageSize);

    @GetMapping("/user_product_combo_day_by_admin/list_by_user_tel/")
    PageInfo listByUserTel(@RequestParam("pageNum") int pageNum,@RequestParam("pageSize") int pageSize,@RequestParam("userTel") String userTel);


}
