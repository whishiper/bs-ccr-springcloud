package cn.bosenkeji.controller;

import cn.bosenkeji.service.TradePlatformApiBindProductComboService;
import cn.bosenkeji.util.Result;
import cn.bosenkeji.vo.tradeplatform.TradePlatformApiBindProductCombo;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * @author xivin
 * @ClassName cn.bosenkeji.controller
 * @Version V1.0
 * @create 2019-07-26 15:48
 */
@RestController
@RequestMapping("/trade_platform_api_bind_product_combo")
@Validated
@Api(tags = "tradePlatformApiBindProductCombo 交易平台api绑定用户套餐接口",value = "提供交易平台api绑定用户套餐相关功能 Rest接口")
public class TradePlatformApiBindProductComboController {

    @Autowired
    private TradePlatformApiBindProductComboService tradePlatformApiBindProductComboService;

    @GetMapping("/list_by_user_id")
    @ApiOperation(value = "根据用户ID 获取交易平台api绑定用户套餐列表 api接口"
            ,httpMethod = "GET",nickname = "getTradePlatformApiBindProductComboListByUserIdWithPage")
    public PageInfo getListByUserId(@RequestParam( value="pageNum",defaultValue="1") int pageNum,
                                    @RequestParam(value = "pageSize",defaultValue = "10") int pageSize,
                                    @RequestParam("userId") @Min(1) @ApiParam(value = "用户ID",required = true,type = "integer",example = "1") int userId) {

        return tradePlatformApiBindProductComboService.findByUserIdWithPage(userId,pageNum,pageSize);
    }

    public TradePlatformApiBindProductCombo getByUserIdAndComboId(@RequestParam("userId") int userId,@RequestParam("userProductComboId") int userProductComboId) {
        return this.tradePlatformApiBindProductComboService.getByUserIdAndComboId(userId,userProductComboId);
    }

    @PostMapping("/")
    @ApiOperation(value = "添加 交易平台api绑定用户套餐 api接口"
            ,httpMethod = "POST",nickname = "addTradePlatformApiBindProductComboList")
    public Result add(@RequestBody @NotNull @ApiParam(value = "交易谱平台api绑定用户套餐实体",required = true,type = "string") TradePlatformApiBindProductCombo tradePlatformApiBindProductCombo) {

        //判断该交易平台api是否未用户未绑定的
       /* tradePlatformApiBindProductComboService.checkExistByUserIdAndTradePlatformApiId(
                tradePlatformApiBindProductCombo.getUserId()
                , tradePlatformApiBindProductCombo.getTradePlatformApiId())
                .filter((value)->value==1)
                .orElseThrow(()->new AddException(TradePlatformApiBindProductComboEnum.NAME));*/

        //判断该套餐是否为用户为绑定的
        /*tradePlatformApiBindProductComboService.checkExistByUserIdAndUserProductComboId(
                tradePlatformApiBindProductCombo.getUserId()
                ,tradePlatformApiBindProductCombo.getUserProductComboId())
                .filter((value)->value==1)
                .orElseThrow(()->new AddException(TradePlatformApiBindProductComboEnum.NAME));*/

        tradePlatformApiBindProductCombo.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        tradePlatformApiBindProductCombo.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        return new Result(tradePlatformApiBindProductComboService.add(tradePlatformApiBindProductCombo));
    }

    @PutMapping("/{id}")
    @ApiOperation(value="更新 交易品台绑定用户套餐",httpMethod = "PUT",nickname = "updateTradePlatformApiBindProductCombo")
    public Result update(@PathVariable("id") @ApiParam(value = "交易平台绑定用户套餐ID",required = true,type = "integer",example = "1") int id,
                         @RequestParam("tradePlatformApiId") @ApiParam(value = "交易平台api ID",required = true,type = "integer",example = "1") int tradePlatformApiId,
                         @RequestParam("userId") @ApiParam(value = "用户ID",required = true,type = "integer",example = "1") int userId) {
        //判断该 api绑定是否为 该用户下的 否则不能操作
        if(tradePlatformApiBindProductComboService.checkExistByUserIdAndId(userId,id)==0) {
            return new Result(0, "权限不足，不能进行更新操作");
        }
        //判断 是否为该用户 未绑定的 api
        if(tradePlatformApiBindProductComboService.checkExistByUserIdAndTradePlatformApiId(userId,tradePlatformApiId)>=1)
            return new Result(0,"交易平台api已被绑定或不存在");


        /* }filter((value)->value==1)
                .orElseThrow(()->new UpdateException(TradePlatformApiBindProductComboEnum.NAME));
        tradePlatformApiBindProductComboService.checkExistByUserIdAndTradePlatformApiId(userId,tradePlatformApiId)
                .filter((value)->value==0)
                .orElseThrow(()->new UpdateException(TradePlatformApiBindProductComboEnum.NAME));*/

        //判断该交易平台api是否未用户未绑定的
        /*tradePlatformApiBindProductComboService.checkExistByUserIdAndTradePlatformApiId(
                userId
                , tradePlatformApiId)
                .filter((value)->value==1)
                .orElseThrow(()->new UpdateException(TradePlatformApiBindProductComboEnum.NAME));*/

        TradePlatformApiBindProductCombo tradePlatformApiBindProductCombo=new TradePlatformApiBindProductCombo();
        tradePlatformApiBindProductCombo.setId(id);
        tradePlatformApiBindProductCombo.setTradePlatformApiId(tradePlatformApiId);
        tradePlatformApiBindProductCombo.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        return new Result<>(this.tradePlatformApiBindProductComboService.updateBindApi(tradePlatformApiBindProductCombo));
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value="解除 交易品台绑定用户套餐",httpMethod = "DELETE",nickname = "deleteTradePlatformApiBindProductCombo")
    public Result delete(@PathVariable("id") @ApiParam(value = "交易平台绑定用户套餐ID",required = true,type = "integer",example = "1") int id,
                         @RequestParam("userId") @ApiParam(value = "用户ID",required = true,type = "integer",example = "1") int userId) {

        //判断该 api绑定是否为 该用户下的 否则不能操作

        if(tradePlatformApiBindProductComboService.checkExistByUserIdAndId(userId,id)==0)
            return new Result(0,"权限不足，不能执行删除操作");

              /*  .filter((value)->value==1)
                .orElseThrow(()->new DeleteException(TradePlatformApiBindProductComboEnum.NAME));*/

        return new Result<>(this.tradePlatformApiBindProductComboService.removeBinding(id));
    }

    @GetMapping("/get_no_bind_trade_platform_api_list_by_user_id")
    @ApiOperation(value = "根据用户ID 获取用户未绑定的交易平台api列表 api接口"
            ,httpMethod = "GET",nickname = "getNoBindTradePlatformApiListByUserIdWithPage")
    public PageInfo getNoBindTradePlatformApiListByUserId(@RequestParam( value="pageNum",defaultValue="1") int pageNum,
                                    @RequestParam(value = "pageSize",defaultValue = "10") int pageSize,
                                    @RequestParam("userId") @Min(1) @ApiParam(value = "用户ID",required = true,type = "integer",example = "1") int userId) {

        return tradePlatformApiBindProductComboService.findNoBindTradePlatformApiListByUserId(userId,pageNum,pageSize);
    }

    @GetMapping("/get_no_bind_user_product_combo_list_by_user_id")
    @ApiOperation(value = "根据用户ID 获取用户未绑定的用户套餐列表 api接口"
            ,httpMethod = "GET",nickname = "getNoBindUserProductComboListByUserIdWithPage")
    public PageInfo getNoBindUserProductComboListByUserId(@RequestParam( value="pageNum",defaultValue="1") int pageNum,
                                    @RequestParam(value = "pageSize",defaultValue = "10") int pageSize,
                                    @RequestParam("userId") @Min(1) @ApiParam(value = "用户ID",required = true,type = "integer",example = "1") int userId) {

        return tradePlatformApiBindProductComboService.findNoBindUserProductComboListByUserId(userId,pageNum,pageSize);
    }

    @ApiOperation(value = "删除 绑定记录 接口",httpMethod = "DELETE",nickname = "realDeleteBind")
    @DeleteMapping("/real/{id}")
    public Result realDelete(@PathVariable("id") @ApiParam(value = "绑定ID",required = true,type = "integer",example = "1") int id) {
        return new Result(this.tradePlatformApiBindProductComboService.delete(id));
    }

    @ApiOperation(value = "通过套餐ID 删除 绑定记录 接口",httpMethod = "DELETE",nickname = "deleteByComboId")
    @DeleteMapping("/by_combo/{userProductComboId}")
    public Result deleteByComboId(@PathVariable("userProductComboId")
                                      @ApiParam(value = "绑定ID",required = true,type = "integer",example = "1")
                                              int userProductComboId) {
        return new Result(this.tradePlatformApiBindProductComboService.deleteByComboId(userProductComboId));
    }

}
