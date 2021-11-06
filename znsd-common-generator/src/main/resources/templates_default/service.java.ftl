package ${package.Service};

import ${package.Entity}.${entity};
import ${cfg.PARENT_PACKAGE}.${cfg.package_node_dto}.${entity}Dto;
import ${cfg.PARENT_PACKAGE}.${cfg.package_node_dto}.${entity}QueryDto;
import com.github.pagehelper.PageInfo;
import com.tao.common.message.Result;
import java.util.List;

/**
 * <p>
 * ${table.comment!} 服务类
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
public interface ${table.serviceName} {

    void create(${entity} entity);

    Result<?> update(${entity} entity);

    Result<?> delete(Long id);

    ${entity}Dto get(Long id,String code);

    PageInfo<#noparse><</#noparse>${entity}Dto<#noparse>></#noparse> query(${entity}QueryDto queryDto, int pageNum, int pageSize);

    void updateBatch(List<Long> ids, String enable);

}