<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${cfg.DAO_PACKAGE}.${entity}Dao">
    <!-- 通用查询映射结果 -->
    <resultMap id="BaseResultMap" type="${package.Entity}.${entity}">
        <#list table.fields as field>
            <#if field.keyFlag><#--生成主键排在第一位-->
                <id column="${field.name}" property="${field.propertyName}" />
            </#if>
        </#list>
        <#list table.commonFields as field><#--生成公共字段 -->
            <result column="${field.name}" property="${field.propertyName}" />
        </#list>
        <#list table.fields as field>
            <#if !field.keyFlag><#--生成普通字段 -->
                <result column="${field.name}" property="${field.propertyName}" />
            </#if>
        </#list>
    </resultMap>
	<!-- 通用查询DTO映射结果 -->
    <resultMap id="BaseDtoResultMap" type="${cfg.PARENT_PACKAGE}.${cfg.package_node_dto}.${entity}Dto">
        <#list table.fields as field>
            <#if field.keyFlag><#--生成主键排在第一位-->
                <id column="${field.name}" property="${field.propertyName}" />
            </#if>
        </#list>
        <#list table.commonFields as field><#--生成公共字段 -->
            <result column="${field.name}" property="${field.propertyName}" />
        </#list>
        <#list table.fields as field>
            <#if !field.keyFlag><#--生成普通字段 -->
                <result column="${field.name}" property="${field.propertyName}" />
            </#if>
        </#list>
    </resultMap>    
    <!-- 通用查询结果列 -->
    <sql id="Base_Column_List">
        <#list table.commonFields as field>
            ${field.name},
        </#list>
        ${table.fieldNames}
    </sql>

    <select id="get" parameterType="java.lang.Integer" resultMap="BaseDtoResultMap">
        select
        <include refid="Base_Column_List"/>
        from ${table.name}
        where 1=1
        <if test="id != null">
            and id = <#noparse>#{</#noparse>id<#noparse>}</#noparse>
        </if>
        <if test="code != null and code !=''">
            and code = <#noparse>#{</#noparse>code<#noparse>}</#noparse>
        </if>
    </select>
    <delete id="delete" parameterType="java.lang.Long">
        delete from ${table.name}
        where id = <#noparse>#{id,jdbcType=INTEGER</#noparse><#noparse>}</#noparse>
    </delete>
    <insert id="create" parameterType="${package.Entity}.${entity}"  useGeneratedKeys="true" keyProperty="id" keyColumn="id">
        insert into ${table.name}
        <trim prefix="(" suffix=")" suffixOverrides=",">
        <#list table.fields as field>
        <#if field.columnType?index_of("DATE")!=-1>
            <if test="${field.propertyName}!=null">
                ${field.name},
            </if>
        <#elseif field.columnType?index_of("INTEGER")!=-1 || field.columnType?index_of("LONG")!=-1>
            <if test="${field.propertyName}!=null">
                ${field.name},
            </if>
        <#else>
            <if test="${field.propertyName}!=null and ${field.propertyName}!=''">
                ${field.name},
            </if>
        </#if>
        </#list>
        </trim>
        <trim prefix="values (" suffix=")" suffixOverrides=",">
        <#list table.fields as field>
        <#if field.columnType?index_of("DATE")!=-1>
            <if test="${field.propertyName}!=null">
                ${'#'}{${field.propertyName}},
            </if>
        <#elseif field.columnType?index_of("INTEGER")!=-1 || field.columnType?index_of("LONG")!=-1>
            <if test="${field.propertyName}!=null">
                ${'#'}{${field.propertyName}},
            </if>
        <#else>
            <if test="${field.propertyName}!=null and ${field.propertyName}!=''">
                ${'#'}{${field.propertyName}},
            </if>
        </#if>
        </#list>
        </trim>
    </insert>
    <update id="update" parameterType="${package.Entity}.${entity}">
        update ${table.name}
        <set>
        <#list table.fields as field>
        <#if field.columnType?index_of("DATE")!=-1>
            <if test="${field.propertyName}!=null">
                ${field.name} = ${'#'}{${field.propertyName}},
            </if>
        <#elseif field.columnType?index_of("INTEGER")!=-1 || field.columnType?index_of("LONG")!=-1>
            <if test="${field.propertyName}!=null">
                ${field.name} = ${'#'}{${field.propertyName}},
            </if>
        <#else>
            <if test="${field.propertyName}!=null and ${field.propertyName}!=''">
                ${field.name} = ${'#'}{${field.propertyName}},
            </if>
        </#if>
        </#list>
        </set>
        where id = <#noparse>#{id,jdbcType=INTEGER</#noparse><#noparse>}</#noparse>
    </update>

    <select id="query" resultMap="BaseDtoResultMap"
            parameterType="${cfg.PARENT_PACKAGE}.${cfg.package_node_dto}.${entity}QueryDto">
        select
        <include refid="Base_Column_List"/>
        from
        ${table.name}
        <where>
            <#list table.fields as field>
            <#if field.columnType?index_of("DATE")!=-1>
            <if test="${field.propertyName}!=null">
                and ${field.name} = DATE_FORMAT(${'#'}{${field.propertyName}},'%Y-%m-%d %H:%i:%s')
            </if>
            <#elseif field.columnType?index_of("INTEGER")!=-1 || field.columnType?index_of("LONG")!=-1>
            <if test="${field.propertyName}!=null">
                and ${field.name} = ${'#'}{${field.propertyName}}
            </if>
            <#else>
            <if test="${field.propertyName}!=null and ${field.propertyName}!=''">
                and ${field.name} like concat('%',${'#'}{${field.propertyName}},'%')
            </if>
            </#if>
            </#list>
        </where>
    </select>

    <select id="findByCode" resultType="java.lang.Integer">
        select count(1) from ${table.name} where code =<#noparse>#{</#noparse>code<#noparse>}</#noparse>
    </select>

    <update id="updateBatch">
        update ${table.name} set status = <#noparse>#{</#noparse>status<#noparse>}</#noparse>
        where id in
        <foreach item="item" index="index" collection="ids" open="("
                 separator="," close=")">
            <#noparse>#{</#noparse>item<#noparse>}</#noparse>
        </foreach>
    </update>
</mapper>