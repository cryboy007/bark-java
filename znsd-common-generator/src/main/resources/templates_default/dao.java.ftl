package ${cfg.DAO_PACKAGE};
import ${package.Entity}.${entity};

import ${cfg.PARENT_PACKAGE}.${cfg.package_node_dto}.${entity}Dto;
import ${cfg.PARENT_PACKAGE}.${cfg.package_node_dto}.${entity}QueryDto;
import com.tao.common.message.Result;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
import com.github.pagehelper.Page;
import java.util.List;

/**
* @author ${author}
* @Date ${date}
* @Description
* @Modified By
*/
public interface ${entity}Dao{

    int create(${entity} entity);

    int update(${entity} entity);

    int delete(@Param("id") Long id);

    ${entity}Dto get(@Param("id") Long id,@Param("code") String code);

    List<${entity}Dto> query(${entity}QueryDto queryDto);

    int findByCode(@Param("code")String code);

    int updateBatch(@Param("ids") List<Long> ids, @Param("status") String status);
}