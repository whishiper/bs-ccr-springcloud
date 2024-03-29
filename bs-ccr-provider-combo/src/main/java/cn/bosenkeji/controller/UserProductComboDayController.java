package cn.bosenkeji.controller;

/**
 * @author xivin
 * @ClassName cn.bosenkeji.controller
 * @Version V1.0
 * @create 2019-07-15 11:15
 */


import cn.bosenkeji.service.IUserProductComboDayService;
import cn.bosenkeji.utils.ComboDOTransformToVOUtil;
import cn.bosenkeji.vo.combo.UserProductComboDay;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("/user_product_combo_day")
@Validated
@Api(tags = "UserProductCobmoDay 用户套餐时长相关接口",value="提供用户套餐时长相关的 Rest API")
public class UserProductComboDayController {

    @Resource
    private IUserProductComboDayService iUserProductComboDayService;

    @Resource
    private DiscoveryClient discoveryClient;

    /*@ApiOperation(value="获取用户套餐时长列表api接口",notes="获取用户套餐时长列表api接口",httpMethod = "GET")
    @RequestMapping(value="/",method = RequestMethod.GET)
    public PageInfo<UserProductComboDay> list(@RequestParam(value="pageNum",defaultValue="1") int pageNum, @RequestParam(value="pageSize",defaultValue="15") int pageSize) {
        return this.iUserProductComboDayService.list(pageNum,pageSize);
    }*/

    @ApiOperation(value="获取用户套餐时长详情api接口",notes="获取用户套餐时长详情api接口",httpMethod = "GET")
    @RequestMapping(value="/{id}",method = RequestMethod.GET)
    public UserProductComboDay get(@PathVariable("id") @Min(1) int id) { return this.iUserProductComboDayService.get(id);}


    //@Cacheable(value = RedisInterface.COMBO_DAY_LIST_TEL_KEY,key = "#tel+'-'+#pageNum+'-'+#pageSize") 缓存同步 代价太大
    @GetMapping("/list_by_tel")
    @ApiOperation(value = "通过用户电话 获取用户套餐时长列表 接口",httpMethod = "GET")
    public PageInfo listByTel(@RequestParam("tel") @ApiParam(value = "用户电话",required = true,type = "string",example = "13556559840") String tel,
                              @RequestParam(value="pageNum",defaultValue="1") int pageNum,
                              @RequestParam(value="pageSize",defaultValue="15") int pageSize) {

        PageInfo userProductComboDayPageInfo = this.iUserProductComboDayService.selectByUserTel(tel, pageNum, pageSize);
        if (null != userProductComboDayPageInfo.getList() && userProductComboDayPageInfo.getList().size() >0) {
            userProductComboDayPageInfo.setList(ComboDOTransformToVOUtil.transformUserProductComboDayDOToVO(userProductComboDayPageInfo.getList()));
        }
        return userProductComboDayPageInfo;

    }

    @GetMapping("/")
    @ApiOperation(value = "获取用户套餐时长列表 接口",httpMethod = "GET",nickname = "getUserProductComboDayListWithPage")
    public PageInfo list(
            @RequestParam(value="pageNum",defaultValue="1") int pageNum, @RequestParam(value="pageSize",defaultValue="10") int pageSize) {

        PageInfo userProductComboDayPageInfo = this.iUserProductComboDayService.list(pageNum, pageSize);
        if (null != userProductComboDayPageInfo.getList() && userProductComboDayPageInfo.getList().size() >0) {
            userProductComboDayPageInfo.setList(ComboDOTransformToVOUtil.transformUserProductComboDayDOToVO(userProductComboDayPageInfo.getList()));
        }
        return userProductComboDayPageInfo;

    }

    @GetMapping("/list_by_user_product_combo_id")
    @ApiOperation(value = "通过用户套餐ID 获取用户套餐时长列表 接口",httpMethod = "GET")
    public PageInfo listByUserProductComboId(@RequestParam("userProductComboId") @ApiParam(value = "用户套餐ID",required = true,type = "integer",example = "1") int userProductComboId,
                                             @RequestParam(value="pageNum",defaultValue="1") int pageNum,
                                             @RequestParam(value="pageSize",defaultValue="15") int pageSize) {

        PageInfo userProductComboDayPageInfo = this.iUserProductComboDayService.selectByUserProductComboId(userProductComboId, pageNum, pageSize);
        if (null != userProductComboDayPageInfo.getList() && userProductComboDayPageInfo.getList().size() >0) {
            userProductComboDayPageInfo.setList(ComboDOTransformToVOUtil.transformUserProductComboDayDOToVO(userProductComboDayPageInfo.getList()));
        }
        return userProductComboDayPageInfo;

    }


    @ApiOperation(value="获取当前服务api接口",notes="获取当前服务api接口",httpMethod = "GET")
    @RequestMapping(value="/discover")
    @ApiIgnore
    public Object discover() { return this.discoveryClient;}
}
