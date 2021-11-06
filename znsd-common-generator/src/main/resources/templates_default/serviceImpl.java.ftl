package ${package.ServiceImpl};

import ${package.Entity}.${entity};
import ${package.Mapper}.${table.mapperName};
import ${package.Service}.${table.serviceName};
import ${cfg.PARENT_PACKAGE}.${cfg.package_node_dto}.${entity}Dto;
import ${cfg.PARENT_PACKAGE}.${cfg.package_node_dto}.${entity}QueryDto;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Date;
import com.tao.common.message.Result;
import java.util.List;
import static com.github.pagehelper.page.PageMethod.startPage;
import com.tao.common.core.utils.DateUtil;
import com.tao.common.core.utils.KeyUtils;


/**
 * <p>
 * ${table.comment!} 服务实现类
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
@Service
public class ${table.serviceImplName} implements ${table.serviceName} {

    @Autowired
    private ${table.mapperName} baseDao;



    @Transactional(rollbackFor = Exception.class)
    @Override
    public void create(${entity} entity) {
        Date now = DateUtil.getNow();
        // TODO 代码存在校验
        // Assert.isTrue(verificationCode(entity.getCode()), "code已存在！");
        // TODO 主键、状态人员赋值
        /*
        entity.setId(KeyUtils.getSnakeflakeKey());
        entity.setStatus(Status.INITIAL);
        entity.setCreateTime(now);
        entity.setModifyTime(now);
        entity.setCreateBy(getOperator());
        */
        baseDao.create(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result<?>  update(${entity} entity) {
        // TODO 修改人 时间赋值
    	/*
        entity.setModifyTime(DateUtil.getNow());
        entity.setModifyBy(getOperator());
        */
        baseDao.update(entity);
        return Result.success();
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result<?> delete(Long id) {
        baseDao.delete(id);
        return Result.success();
    }

    private boolean verificationCode(String code) {
        return baseDao.findByCode(code) > 0 ? false : true;
    }

    @Override
    public ${entity}Dto get(Long id,String code) {
        return baseDao.get(id,code);
    }

    @Override
    public PageInfo<#noparse><</#noparse>${entity}Dto<#noparse>></#noparse> query(${entity}QueryDto queryDto, int pageNum, int pageSize) {
        startPage(pageNum, pageSize);
        return new PageInfo<>(baseDao.query(queryDto));
    }


    @Override
    public void updateBatch(List<Long> ids, String status) {
        baseDao.updateBatch(ids,status);
    }

}