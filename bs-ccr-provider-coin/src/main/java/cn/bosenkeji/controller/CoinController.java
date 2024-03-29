package cn.bosenkeji.controller;

import cn.bosenkeji.service.CoinService;
import cn.bosenkeji.util.Result;
import cn.bosenkeji.vo.coin.Coin;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.LocalDateTime;


/**
 * @ClassName CoinController
 * @Description 货币
 * @Author Yu XueWen
 * @Email 8586826@qq.com
 * @Versio V1.0
**/
@RestController
@RequestMapping(value = "/coin")
@Validated
@Api(tags = "Coin 货币相关接口", value = "提供货币相关接口的 Rest API")
public class CoinController {

    @Resource
    private CoinService coinService;

    @ApiOperation(value = "获取货币列表接口", notes = "获取货币列表接口", httpMethod = "GET", nickname = "getCoinListWithPage")
    @RequestMapping(value="/")
    public PageInfo list(@RequestParam( value="pageNum",defaultValue="1") int pageNum,
                         @RequestParam(value = "pageSizeCommon",defaultValue = "10") int pageSizeCommon) {
        return this.coinService.listByPage(pageNum,pageSizeCommon) ;
    }

    @ApiOperation(value = "获取单个货币信息列表接口", httpMethod = "GET",nickname = "getOneCoin")
    @RequestMapping(value="/{id}")
    public Coin get( @PathVariable("id")  @Min(value = 1) @ApiParam(value = "币种ID", required = true, type = "integer",example = "1") int id) {
        return this.coinService.get(id);
    }

    @ApiOperation(value = "添加单个货币接口", httpMethod = "POST",nickname = "addCoin")
    @RequestMapping(value="/", method = RequestMethod.POST)
    public Result add(@RequestBody @Valid @NotNull @ApiParam(value = "币种实体", required = true, type = "string") Coin coin) {

        if (this.coinService.checkExistByName(coin.getName()).get() >= 1){
            return new Result<>(this.coinService.getByName(coin.getName()).getId(),"币种已存在");
        }

        coin.setName(coin.getName().toLowerCase());
        coin.setStatus(1);
        coin.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        coin.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

        return new Result<>(this.coinService.add(coin));
    }

    @ApiOperation(value = "更新单个货币接口", httpMethod = "PUT" ,nickname = "updateCoin")
    @RequestMapping(value="/", method = RequestMethod.PUT)
    public Result put(@RequestBody @Valid @ApiParam(value = "币种实体", required = true, type = "string") Coin coin) {

        coin.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        return new Result<>(this.coinService.update(coin));
    }

    @ApiOperation(value = "删除单个货币接口", httpMethod = "DELETE",nickname = "deleteOneCoin")
    @RequestMapping(value="/{id}", method = RequestMethod.DELETE)
    public Result delete(@PathVariable("id") @ApiParam(value = "币种ID", required = true, type = "integer",example = "1") int id) {
        return new Result<>(this.coinService.delete(id));
    }


}
