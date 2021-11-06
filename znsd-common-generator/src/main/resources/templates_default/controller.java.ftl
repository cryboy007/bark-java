package ${package.Controller};

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.annotations.*;
import com.tao.common.message.Result;
import com.github.pagehelper.PageInfo;
import ${package.Entity}.${entity};
import ${package.Service}.${table.serviceName};
import ${cfg.PARENT_PACKAGE}.${cfg.package_node_dto}.${entity}Dto;
import ${cfg.PARENT_PACKAGE}.${cfg.package_node_dto}.${entity}QueryDto;

import java.util.Arrays;
import java.util.List;

import javax.validation.Valid;
import org.springframework.http.MediaType;
import com.baison.e3plus.biz.system.api.common.Status;
// import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;


/**
* @author ${author}
* @Date ${date}
* @Description
* @Modified By
*/
@RestController
<#--@RequestMapping("<#if package.ModuleName??>/${package.ModuleName}</#if>/<#if controllerMappingHyphenStyle??>${controllerMappingHyphen}<#else>${table.entityPath}</#if>")-->
@RequestMapping("/portal/${entity?uncap_first}")
@Api(tags = "portal-${table.comment!}")
public class ${table.controllerName} {

	@Autowired
    private ${table.serviceName} service;

    @ApiOperation(value = "保存接口入参", notes = "保存接口入参", httpMethod = "POST")
//    @ApiOperationSupport(ignoreParameters = { "id", "status", "createBy", "createTime", "modifyBy", "modifyTime" })
    @PostMapping(value = "/create", produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
    public Result<?>  create(@Valid @RequestBody ${entity} entity) {
       service.create(entity);
       return Result.success(String.valueOf(entity.getId()), "创建成功！");
    }

    @ApiOperation(value = "修改接口入参", notes = "修改接口入参", httpMethod = "POST")
//    @ApiOperationSupport(ignoreParameters = { "status", "createBy", "createTime", "modifyBy", "modifyTime" })
    @PostMapping(value = "/update")
    public Result<?> update(@RequestBody ${entity} entity) {
        return service.update(entity);
    }

    @ApiImplicitParam(name = "id", value = "主键ID", dataType = "String")
    @ApiOperation(value = "删除接口入参", notes = "删除接口入参", httpMethod = "DELETE")
    @DeleteMapping(value = "/delete/{id}")
    public Result<?> delete(@PathVariable("id") Long id) {
        return service.delete(id);
    }

    @ApiImplicitParam(name = "id", value = "主键ID", dataType = "int")
    @ApiOperation(value = "根据主键ID查询接口入参", notes = "根据主键ID查询接口入参", httpMethod = "GET")
    @GetMapping(value = "/get")
    public Result<${entity}Dto> get(@RequestParam(value = "id",required = false) Long id,@RequestParam(value = "code",required = false) String code) {
        return Result.success(service.get(id,code));
    }

    @ApiImplicitParams({ @ApiImplicitParam(name = "pageNum", value = "当前页码", required = true, dataType = "int"),
    @ApiImplicitParam(name = "pageSize", value = "每页显示的条数", required = true, dataType = "int"),
    @ApiImplicitParam(name = "queryDto", value = "分页查询对象", dataType = "${entity}QueryDto") })
    @ApiOperation(value = "分页查询接口入参", notes = "分页查询接口入参", httpMethod = "POST")
    @PostMapping(value = "/query")
    public Result<PageInfo<#noparse><</#noparse>${entity}Dto>> query(@RequestBody ${entity}QueryDto queryDto, @RequestParam("pageNum") int pageNum,
    @RequestParam("pageSize") int pageSize) {
         return Result.success(service.query(queryDto,pageNum, pageSize));
    }

    @ApiImplicitParam(name = "id", value = "主键")
    @ApiOperation(value = "启用接口", notes = "启用接口", httpMethod = "POST")
    @PostMapping(value = "/enable")
    public Result<?> enable(@RequestParam("id") Long id) {
        service.updateBatch(Arrays.asList(id), Status.ENABLE);
        return Result.success();
    }

    @ApiImplicitParam(name = "id", value = "主键")
    @ApiOperation(value = "禁用接口", notes = "禁用接口", httpMethod = "POST")
    @PostMapping(value = "/disable")
    public Result<?> disable(@RequestParam("id") Long id) {
        service.updateBatch(Arrays.asList(id), Status.DISABLE);
        return Result.success();
    }
}
