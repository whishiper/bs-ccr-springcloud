package cn.bosenkeji.controller;

/**
 * @author xivin
 * @ClassName cn.bosenkeji.controller
 * @Version V1.0
 * @create 2019-07-15 11:15
 */

import cn.bosenkeji.job.MySchedule;
import cn.bosenkeji.service.IUserProductComboDayByAdminService;
import cn.bosenkeji.util.Result;
import cn.bosenkeji.vo.Admin;
import cn.bosenkeji.vo.combo.UserProductComboDay;
import cn.bosenkeji.vo.combo.UserProductComboDayByAdmin;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.SchedulerException;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/user_product_combo_day_by_admin")
@Validated
@Api(tags = "UserProductComboDayByAdmin 用户套餐时长操作相关接口",value="提供用户套餐时长操作相关的 Rest API")
public class UserProductComboDayByAdminController {

    @Resource
    private IUserProductComboDayByAdminService iUserProductComboDayByAdminService;

    @Resource
    private DiscoveryClient discoveryClient;

    @Resource
    private MySchedule mySchedule;


    @ApiOperation(value="获取用户套餐时长操作详情api接口",httpMethod = "GET")
    @RequestMapping(value="/{id}",method = RequestMethod.GET)
    public UserProductComboDayByAdmin get(@PathVariable("id") @Min(1) @ApiParam(value = "用户套餐时长操作ID",required = true,type = "integer",example = "1") int id) {
        return this.iUserProductComboDayByAdminService.get(id);
    }

    //@NotNull @ApiParam(value = "用户套餐时长实体",required = true,type = "string")
    //@NotNull @ApiParam(value = "管理员实体",required = true,type = "string")
    @ApiOperation(value="添加用户套餐时长操作信息api接口",httpMethod = "POST",nickname = "addUserProductComboDayByAdmin")
    @RequestMapping(value="/",method = RequestMethod.POST)
    public Result add(
            @RequestBody @NotNull @ApiParam(value = "管理员实体",required = true,type = "string") UserProductComboDayByAdmin userProductComboDayByAdmin) {
        UserProductComboDay userProductComboDay = userProductComboDayByAdmin.getUserProductComboDay();
        userProductComboDay.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        userProductComboDay.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        userProductComboDayByAdmin.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        userProductComboDayByAdmin.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        userProductComboDayByAdmin.setStatus(1);
        userProductComboDay.setStatus(1);
        return new Result(this.iUserProductComboDayByAdminService.add(userProductComboDay,userProductComboDayByAdmin));
    }

    @DeleteMapping("/job")
    public Result deleteJob(int id) {
        return new Result(mySchedule.deleteJob(id));
    }

    @PostMapping("/job")
    public Result addJob(int comboId){
        try {
            this.mySchedule.scheduleJob(String.valueOf(comboId), String.valueOf(comboId));
            return new Result(1,"添加任务成功");
        }catch (ObjectAlreadyExistsException o) {
            o.printStackTrace();
            return new Result(0,"任务已存在！！！");
        }
        catch (Exception e) {
            e.printStackTrace();
            return new Result(0,"添加任务发生异常");
        }
    }




    @ApiOperation(value="获取当前服务api接口",notes="获取当前服务api接口",httpMethod = "GET")
    @RequestMapping(value="/discover")
    @ApiIgnore
    public Object discover() { return this.discoveryClient;}
}
